package cn.edu.nynu.codelab.upload.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.upload.entity.UploadFile;
import cn.edu.nynu.codelab.upload.mapper.UploadFileMapper;
import cn.edu.nynu.codelab.upload.service.UploadFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传服务实现
 *
 * <p>安全策略（纵深防御）：
 * <ol>
 *   <li>检查文件非空</li>
 *   <li>检查原始文件名，拒绝路径穿越 payload</li>
 *   <li>检查扩展名白名单</li>
 *   <li>检查 Content-Type（客户端声明）</li>
 *   <li>检查文件头 Magic Bytes（不可伪造）</li>
 *   <li>检查文件大小上限</li>
 *   <li>使用 UUID 重命名 + 日期子目录落盘，不信任任何用户输入</li>
 * </ol>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadFileMapper uploadFileMapper;

    @Value("${app.upload.base-path:uploads}")
    private String uploadBasePath;

    /** 允许的 MIME 类型（客户端声明） */
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    /** 允许的扩展名（小写） */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp"
    );

    /** 最大文件大小：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // ---- Magic Bytes ----

    /** JPEG: FF D8 FF */
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    /** PNG: 89 50 4E 47 */
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    /** WebP RIFF 头: 52 49 46 46 */
    private static final byte[] RIFF_MAGIC = {0x52, 0x49, 0x46, 0x46};
    /** WebP 子类型: 57 45 42 50 (WEBP) */
    private static final byte[] WEBP_MAGIC = {0x57, 0x45, 0x42, 0x50};

    /** 需要读取的最大 magic byte 长度（RIFF 头 + 4 字节大小 + WEBP 标识 = 12 字节） */
    private static final int MAX_MAGIC_BYTES = 12;

    private static final DateTimeFormatter DATE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public UploadFile upload(MultipartFile file, String usageType) {
        // 1. 校验文件非空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 2. 校验原始文件名安全性（防路径穿越）
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new RuntimeException("文件名不能为空");
        }
        String sanitizedName = sanitizeFilename(originalName);
        if (sanitizedName.isEmpty()) {
            throw new RuntimeException("文件名不合法");
        }

        // 3. 安全提取扩展名 + 白名单校验
        String extension = extractExtensionSafe(sanitizedName);
        if (extension.isEmpty()) {
            throw new RuntimeException("无法识别的文件类型，仅允许上传 jpg、jpeg、png、webp 格式的图片");
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("不支持的文件类型 ." + extension + "，仅允许上传 jpg、jpeg、png、webp 格式的图片");
        }

        // 4. 校验 MIME 类型（客户端声明，作为第一道过滤）
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new RuntimeException("不支持的文件类型，仅允许上传 jpg、png、webp 格式的图片");
        }

        // 5. 校验文件头 Magic Bytes（不可伪造的最终防线）
        try {
            validateMagicBytes(file, extension);
        } catch (IOException e) {
            log.error("读取文件头失败", e);
            throw new RuntimeException("文件读取失败，请重试");
        }

        // 6. 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过 10MB");
        }

        // 7. 生成安全存储文件名：UUID + 安全扩展名
        String storedName = UUID.randomUUID().toString() + extension;

        // 8. 按日期创建子目录（便于运维清理）
        String dateSubDir = LocalDate.now().format(DATE_DIR_FORMATTER);
        Path uploadDir = Paths.get(uploadBasePath, dateSubDir);
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            log.error("创建上传目录失败: {}", uploadDir, e);
            throw new RuntimeException("创建上传目录失败");
        }

        // 9. 保存文件（含路径边界校验）
        Path destPath = uploadDir.resolve(storedName);
        assertWithinBasePath(destPath);
        try {
            file.transferTo(destPath);
        } catch (IOException e) {
            log.error("文件保存失败: {}", destPath, e);
            throw new RuntimeException("文件保存失败");
        }

        // 10. 构建记录（filePath 使用 Path API，避免字符串拼接引入不规范路径）
        String relativePath = dateSubDir + "/" + storedName;
        Path basePath = Paths.get(uploadBasePath);
        UploadFile record = new UploadFile();
        record.setOriginalName(originalName);
        record.setStoredName(storedName);
        record.setFileUrl("/uploads/" + relativePath);
        record.setFilePath(basePath.resolve(relativePath).toString());
        record.setMimeType(mimeType);
        record.setFileSize(file.getSize());
        record.setUsageType(usageType);
        record.setUploaderId(StpUtil.getLoginIdAsLong());

        // 11. 插入数据库（失败时清理已落盘的文件，避免孤儿文件）
        try {
            uploadFileMapper.insert(record);
        } catch (Exception e) {
            log.error("数据库插入失败，清理孤儿文件: {}", destPath, e);
            try {
                Files.deleteIfExists(destPath);
            } catch (IOException ignored) {
                log.warn("清理孤儿文件失败: {}", destPath);
            }
            throw new RuntimeException("文件记录保存失败，请重试");
        }

        log.info("文件上传成功: {} -> {} ({} bytes, type={})", originalName, relativePath, file.getSize(), mimeType);
        return record;
    }

    @Override
    public List<UploadFile> listRecent() {
        LambdaQueryWrapper<UploadFile> wrapper = new LambdaQueryWrapper<UploadFile>()
                .orderByDesc(UploadFile::getCreateTime);
        // 使用 MyBatis-Plus Page 限制返回条数，避免 .last() 潜在的 SQL 注入风险
        return uploadFileMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<UploadFile>(1, 50, false),
                wrapper
        ).getRecords();
    }

    @Override
    public void deleteFileRecord(Long id) {
        UploadFile record = uploadFileMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("文件记录不存在");
        }

        // 尝试删除物理文件（失败不影响数据库删除，文件可能已被外部清理）
        // 安全：校验文件路径在上传根目录内，防止路径穿越删除任意文件
        try {
            Path filePath = Paths.get(record.getFilePath()).toAbsolutePath().normalize();
            Path basePath = Paths.get(uploadBasePath).toAbsolutePath().normalize();
            if (!filePath.startsWith(basePath)) {
                log.warn("拒绝删除越界文件: path={}, base={}", filePath, basePath);
            } else if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("物理文件已删除: {}", record.getFilePath());
            }
        } catch (IOException e) {
            log.warn("删除物理文件失败（可能已被清理）: {}", record.getFilePath(), e);
        }

        uploadFileMapper.deleteById(id);
        log.info("文件记录已删除: id={}, storedName={}", id, record.getStoredName());
    }

    // ==================== 私有方法 ====================

    /**
     * 清理文件名：去除路径分隔符，仅保留最后的文件名部分。
     *
     * <p>防御以下攻击模式：
     * <ul>
     *   <li>{@code ../../../etc/passwd}</li>
     *   <li>{@code ..\..\..\windows\system32}</li>
     *   <li>{@code shell.jsp\x00.jpg}（空字节截断）</li>
     * </ul>
     */
    private String sanitizeFilename(String originalName) {
        // 去除空字节
        String name = originalName.replace("\0", "");
        // 统一路径分隔符
        name = name.replace('\\', '/');
        // 拒绝路径穿越
        if (name.contains("../") || name.contains("/..") || name.startsWith("..")) {
            log.warn("检测到路径穿越尝试: {}", originalName);
            return "";
        }
        // 取最后一个路径段的文件名部分
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        return name.trim();
    }

    /**
     * 安全提取扩展名。
     *
     * <p>仅提取最后一个点之后的部分，且只允许字母数字字符。
     * <ul>
     *   <li>{@code photo.jpg} → {@code .jpg}</li>
     *   <li>{@code file.tar.gz} → {@code .gz}（拒绝双扩展名绕过）</li>
     *   <li>{@code .htaccess} → 空字符串（无扩展名的隐藏文件）</li>
     *   <li>{@code shell.jsp%00.jpg} → 空字符串（已在 sanitizeFilename 中去除空字节）</li>
     * </ul>
     */
    private String extractExtensionSafe(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == 0) {
            // 无扩展名 或 隐藏文件（如 .gitignore）
            return "";
        }
        // 最后一个点后至少有一个字符
        if (lastDot >= filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(lastDot).toLowerCase();
        // 扩展名仅允许字母数字，拒绝特殊字符
        if (!ext.matches("\\.[a-z0-9]+")) {
            return "";
        }
        return ext;
    }

    /**
     * 校验文件头 Magic Bytes。
     *
     * <p>读取文件前若干字节，与已知合法图片格式的文件头比对。
     * 这是 Content-Type 不可伪造的最终防线。
     */
    private void validateMagicBytes(MultipartFile file, String extension) throws IOException {
        byte[] header = new byte[MAX_MAGIC_BYTES];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(header);
            if (read < 8) {
                throw new RuntimeException("文件内容过短，无法识别文件类型");
            }
        }

        switch (extension) {
            case ".jpg":
            case ".jpeg":
                if (!startsWith(header, JPEG_MAGIC)) {
                    throw new RuntimeException("文件头与扩展名不匹配：非 JPEG 格式");
                }
                break;
            case ".png":
                if (!startsWith(header, PNG_MAGIC)) {
                    throw new RuntimeException("文件头与扩展名不匹配：非 PNG 格式");
                }
                break;
            case ".webp":
                if (!startsWith(header, RIFF_MAGIC) || !bytesMatch(header, 8, WEBP_MAGIC)) {
                    throw new RuntimeException("文件头与扩展名不匹配：非 WebP 格式");
                }
                break;
            default:
                throw new RuntimeException("不支持的文件类型");
        }
    }

    private boolean startsWith(byte[] source, byte[] prefix) {
        if (source.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (source[i] != prefix[i]) return false;
        }
        return true;
    }

    /**
     * 校验目标路径在上传根目录内（纵深防御：防止路径穿越落盘到其他目录）。
     */
    private void assertWithinBasePath(Path target) {
        Path basePath = Paths.get(uploadBasePath).toAbsolutePath().normalize();
        Path resolved = target.toAbsolutePath().normalize();
        if (!resolved.startsWith(basePath)) {
            log.error("文件路径越界: base={}, target={}", basePath, resolved);
            throw new RuntimeException("文件路径不合法");
        }
    }

    private boolean bytesMatch(byte[] source, int offset, byte[] expected) {
        if (source.length < offset + expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (source[offset + i] != expected[i]) return false;
        }
        return true;
    }
}

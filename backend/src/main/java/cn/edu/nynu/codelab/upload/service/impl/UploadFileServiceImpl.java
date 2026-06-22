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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadFileMapper uploadFileMapper;

    @Value("${app.upload.base-path:uploads}")
    private String uploadBasePath;

    /** 允许的 MIME 类型 */
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    /** 最大文件大小：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public UploadFile upload(MultipartFile file, String usageType) {
        // 1. 校验文件非空
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 2. 校验 MIME 类型
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new RuntimeException("不支持的文件类型，仅允许上传 jpg、png、webp 格式的图片");
        }

        // 3. 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过 10MB");
        }

        // 4. 生成存储文件名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        String storedName = UUID.randomUUID().toString() + extension;

        // 5. 创建上传目录
        try {
            Path uploadDir = Paths.get(uploadBasePath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            throw new RuntimeException("创建上传目录失败");
        }

        // 6. 保存文件
        Path destPath = Paths.get(uploadBasePath, storedName);
        try {
            file.transferTo(destPath);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new RuntimeException("文件保存失败");
        }

        // 7. 构建记录
        UploadFile record = new UploadFile();
        record.setOriginalName(originalName);
        record.setStoredName(storedName);
        record.setFileUrl("/uploads/" + storedName);
        record.setFilePath(uploadBasePath + "/" + storedName);
        record.setMimeType(mimeType);
        record.setFileSize(file.getSize());
        record.setUsageType(usageType);

        // 8. 获取上传用户ID
        record.setUploaderId(StpUtil.getLoginIdAsLong());

        // 9. 插入数据库
        uploadFileMapper.insert(record);

        log.info("文件上传成功: {} -> {}", originalName, storedName);

        // 10. 返回记录
        return record;
    }

    @Override
    public List<UploadFile> listRecent() {
        LambdaQueryWrapper<UploadFile> wrapper = new LambdaQueryWrapper<UploadFile>()
                .orderByDesc(UploadFile::getCreateTime)
                .last("LIMIT 50");
        return uploadFileMapper.selectList(wrapper);
    }

    @Override
    public void deleteFileRecord(Long id) {
        UploadFile record = uploadFileMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("文件记录不存在");
        }
        uploadFileMapper.deleteById(id);
    }
}

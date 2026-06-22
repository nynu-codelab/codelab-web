package cn.edu.nynu.codelab.upload.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件上传记录实体
 *
 * @author NYNU Code Lab
 */
@Data
@TableName("lab_upload_file")
public class UploadFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 存储文件名（UUID） */
    private String storedName;

    /** 文件访问URL */
    private String fileUrl;

    /**
     * 文件存储路径（不变式文档）。
     *
     * <p>该字段由 {@code UploadFileServiceImpl} 通过 {@link java.nio.file.Path} API 构造，
     * 满足以下不变式：
     * <ul>
     *   <li><b>绝对路径</b>：基于配置的 {@code app.upload.base-path} 解析，非相对路径</li>
     *   <li><b>路径范围</b>：始终在 {@code app.upload.base-path} 目录树内（经 {@code assertWithinBasePath} 校验）</li>
     *   <li><b>格式</b>：{@code {basePath}/yyyy/MM/dd/{uuid}.{ext}}，日期子目录 + UUID 重命名</li>
     *   <li><b>跨平台</b>：使用 {@code Path.resolve()} 拼接，自动适配操作系统分隔符</li>
     *   <li><b>不用于对外 URL</b>：对外访问 URL 使用 {@code fileUrl} 字段（{@code /uploads/...}）</li>
     *   <li><b>读写分离</b>：仅由 {@code UploadFileServiceImpl} 写入，外部只读</li>
     * </ul>
     *
     * <p>示例值（Linux 容器）：{@code /app/uploads/2026/06/22/a1b2c3d4.jpg}
     */
    private String filePath;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 用途类型 */
    private String usageType;

    /** 上传用户ID */
    private Long uploaderId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

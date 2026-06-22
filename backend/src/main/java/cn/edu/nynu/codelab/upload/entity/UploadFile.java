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

    /** 文件存储路径 */
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

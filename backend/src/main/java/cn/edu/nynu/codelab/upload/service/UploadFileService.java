package cn.edu.nynu.codelab.upload.service;

import cn.edu.nynu.codelab.upload.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 *
 * @author NYNU Code Lab
 */
public interface UploadFileService {

    /**
     * 上传文件
     *
     * @param file      上传的文件
     * @param usageType 用途类型
     * @return 上传记录
     */
    UploadFile upload(MultipartFile file, String usageType);

    /**
     * 获取最近上传的文件列表（最多50条）
     *
     * @return 文件列表
     */
    List<UploadFile> listRecent();

    /**
     * 删除文件记录
     *
     * @param id 文件记录ID
     */
    void deleteFileRecord(Long id);
}

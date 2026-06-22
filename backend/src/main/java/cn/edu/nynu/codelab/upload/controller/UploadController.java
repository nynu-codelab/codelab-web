package cn.edu.nynu.codelab.upload.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.upload.entity.UploadFile;
import cn.edu.nynu.codelab.upload.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/upload")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class UploadController {

    private final UploadFileService uploadFileService;

    /**
     * 上传文件
     *
     * @param file      上传的文件
     * @param usageType 用途类型，默认为 other
     * @return 上传记录
     */
    @PostMapping
    public Result<UploadFile> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "other") String usageType) {
        UploadFile uploadFile = uploadFileService.upload(file, usageType);
        return Result.success(uploadFile);
    }

    /**
     * 获取最近上传的文件列表
     *
     * @return 文件列表
     */
    @GetMapping
    public Result<List<UploadFile>> listRecent() {
        List<UploadFile> files = uploadFileService.listRecent();
        return Result.success(files);
    }

    /**
     * 删除文件记录
     *
     * @param id 文件记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFileRecord(@PathVariable Long id) {
        uploadFileService.deleteFileRecord(id);
        return Result.success("删除成功");
    }
}

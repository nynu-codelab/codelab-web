package cn.edu.nynu.codelab.recruit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.recruit.dto.ReviewRequest;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;
import cn.edu.nynu.codelab.recruit.service.ApplyRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminApplyController {

    private final ApplyRecordService applyRecordService;

    /**
     * 查看报名列表（可选状态筛选）
     */
    @GetMapping
    public Result<List<ApplyRecord>> list(@RequestParam(required = false) String status) {
        return Result.success(applyRecordService.listApplications(status));
    }

    /**
     * 查看报名详情
     */
    @GetMapping("/{id}")
    public Result<ApplyRecord> detail(@PathVariable Long id) {
        return Result.success(applyRecordService.getApplicationDetail(id));
    }

    /**
     * 审核报名
     */
    @PutMapping("/{id}/review")
    public Result<?> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest req) {
        applyRecordService.reviewApplication(id, req);
        return Result.success("审核完成");
    }
}

package cn.edu.nynu.codelab.recruit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.recruit.dto.ReviewRequest;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;
import cn.edu.nynu.codelab.recruit.service.ApplyRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminApplyController {

    private final ApplyRecordService applyRecordService;

    /**
     * 查看报名列表（可分页，可选状态、方向、关键词筛选）
     */
    @GetMapping
    public Result<PageResult<ApplyRecord>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String keyword) {
        return Result.success(applyRecordService.adminListPaged(page, pageSize, status, direction, keyword));
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

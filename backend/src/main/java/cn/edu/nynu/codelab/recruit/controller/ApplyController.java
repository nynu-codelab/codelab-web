package cn.edu.nynu.codelab.recruit.controller;

import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.recruit.dto.ApplyRequest;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;
import cn.edu.nynu.codelab.recruit.service.ApplyRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyRecordService applyRecordService;

    /**
     * 提交报名
     */
    @PostMapping
    public Result<ApplyRecord> submit(@Valid @RequestBody ApplyRequest req) {
        return Result.success("报名提交成功", applyRecordService.submitApply(req));
    }

    /**
     * 查看我的报名
     */
    @GetMapping("/my")
    public Result<ApplyRecord> my() {
        return Result.success(applyRecordService.getMyApply());
    }

    /**
     * 修改我的报名（仅 PENDING 状态可修改）
     */
    @PutMapping("/my")
    public Result<ApplyRecord> updateMy(@Valid @RequestBody ApplyRequest req) {
        return Result.success("报名信息修改成功", applyRecordService.updateMyApply(req));
    }
}

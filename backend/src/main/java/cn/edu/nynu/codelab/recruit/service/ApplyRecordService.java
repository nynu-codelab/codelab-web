package cn.edu.nynu.codelab.recruit.service;

import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.recruit.dto.ApplyRequest;
import cn.edu.nynu.codelab.recruit.dto.ReviewRequest;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;

import java.util.List;

public interface ApplyRecordService {

    /**
     * 提交报名（一个用户只能有一条当前有效的报名记录）
     */
    ApplyRecord submitApply(ApplyRequest req);

    /**
     * 获取当前用户的报名记录
     */
    ApplyRecord getMyApply();

    /**
     * 修改报名（仅 PENDING 状态可修改）
     */
    ApplyRecord updateMyApply(ApplyRequest req);

    /**
     * 管理员：查看报名列表（可选状态筛选）
     */
    List<ApplyRecord> listApplications(String status);

    /**
     * 管理员：分页查看报名列表（可按状态、方向、关键词筛选）
     */
    PageResult<ApplyRecord> adminListPaged(int page, int pageSize, String status, String direction, String keyword);

    /**
     * 管理员：查看报名详情
     */
    ApplyRecord getApplicationDetail(Long id);

    /**
     * 管理员：审核报名
     */
    void reviewApplication(Long id, ReviewRequest req);
}

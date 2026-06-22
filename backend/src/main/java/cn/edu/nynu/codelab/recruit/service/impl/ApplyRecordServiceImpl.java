package cn.edu.nynu.codelab.recruit.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.common.PageQuery;
import cn.edu.nynu.codelab.recruit.dto.ApplyRequest;
import cn.edu.nynu.codelab.recruit.dto.ReviewRequest;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;
import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.common.exception.BusinessException;
import cn.edu.nynu.codelab.recruit.mapper.ApplyRecordMapper;
import cn.edu.nynu.codelab.recruit.service.ApplyRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ApplyRecordServiceImpl implements ApplyRecordService {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            ApplyRecord.STATUS_PENDING,
            ApplyRecord.STATUS_PRELIMINARY_PASSED,
            ApplyRecord.STATUS_INTERVIEWING,
            ApplyRecord.STATUS_VIEWED,
            ApplyRecord.STATUS_CONTACTED,
            ApplyRecord.STATUS_PASSED,
            ApplyRecord.STATUS_REJECTED,
            ApplyRecord.STATUS_WITHDRAWN
    ));

    private static final Set<String> ACTIVE_STATUSES = new HashSet<>(Arrays.asList(
            ApplyRecord.STATUS_PENDING,
            ApplyRecord.STATUS_PRELIMINARY_PASSED,
            ApplyRecord.STATUS_INTERVIEWING,
            ApplyRecord.STATUS_VIEWED,
            ApplyRecord.STATUS_CONTACTED,
            ApplyRecord.STATUS_PASSED,
            ApplyRecord.STATUS_REJECTED
    ));

    private final ApplyRecordMapper applyRecordMapper;

    @Override
    public ApplyRecord submitApply(ApplyRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查是否已有有效报名（已撤回的不算）
        Long existingCount = applyRecordMapper.selectCount(
                new LambdaQueryWrapper<ApplyRecord>()
                        .eq(ApplyRecord::getUserId, userId)
                        .in(ApplyRecord::getStatus, ACTIVE_STATUSES)
        );
        if (existingCount > 0) {
            throw new BusinessException("您已有正在处理中的报名记录，无法重复提交");
        }

        ApplyRecord record = buildRecord(req, userId);
        record.setStatus(ApplyRecord.STATUS_PENDING);
        applyRecordMapper.insert(record);
        log.info("用户 {} 提交了报名，记录ID: {}", userId, record.getId());
        return record;
    }

    @Override
    public ApplyRecord getMyApply() {
        Long userId = StpUtil.getLoginIdAsLong();
        ApplyRecord record = applyRecordMapper.selectOne(
                new LambdaQueryWrapper<ApplyRecord>()
                        .eq(ApplyRecord::getUserId, userId)
                        .in(ApplyRecord::getStatus, ACTIVE_STATUSES)
                        .orderByDesc(ApplyRecord::getCreateTime)
                        .last("LIMIT 1")
        );
        if (record == null) {
            throw new BusinessException("暂无报名记录");
        }
        return record;
    }

    @Override
    public ApplyRecord updateMyApply(ApplyRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();
        ApplyRecord record = applyRecordMapper.selectOne(
                new LambdaQueryWrapper<ApplyRecord>()
                        .eq(ApplyRecord::getUserId, userId)
                        .eq(ApplyRecord::getStatus, ApplyRecord.STATUS_PENDING)
                        .orderByDesc(ApplyRecord::getCreateTime)
                        .last("LIMIT 1")
        );
        if (record == null) {
            throw new BusinessException("暂无待审核的报名记录可修改");
        }

        // 更新报名字段
        record.setRealName(req.getRealName());
        record.setGrade(req.getGrade());
        record.setMajor(req.getMajor());
        record.setClassName(req.getClassName());
        record.setPhone(req.getPhone());
        record.setQq(req.getQq());
        record.setDirection(req.getDirection());
        record.setHasProgrammingBasis(req.getHasProgrammingBasis() != null ? req.getHasProgrammingBasis() : 0);
        record.setSkills(req.getSkills() != null ? req.getSkills() : "");
        record.setIntroduction(req.getIntroduction() != null ? req.getIntroduction() : "");
        record.setReason(req.getReason() != null ? req.getReason() : "");
        record.setWeeklyAvailableTime(req.getWeeklyAvailableTime() != null ? req.getWeeklyAvailableTime() : "");
        record.setPortfolioUrl(req.getPortfolioUrl() != null ? req.getPortfolioUrl() : "");

        applyRecordMapper.updateById(record);
        log.info("用户 {} 修改了报名记录 {}", userId, record.getId());
        return record;
    }

    @Override
    public List<ApplyRecord> listApplications(String status) {
        LambdaQueryWrapper<ApplyRecord> wrapper = new LambdaQueryWrapper<ApplyRecord>()
                .orderByDesc(ApplyRecord::getCreateTime);
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new BusinessException("无效的审核状态: " + status);
            }
            wrapper.eq(ApplyRecord::getStatus, status);
        }
        return applyRecordMapper.selectList(wrapper);
    }

    @Override
    public PageResult<ApplyRecord> adminListPaged(int page, int pageSize, String status, String direction, String keyword) {
        page = PageQuery.normalizePage(page);
        pageSize = PageQuery.normalizePageSize(pageSize);
        Page<ApplyRecord> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ApplyRecord> wrapper = new LambdaQueryWrapper<ApplyRecord>()
                .orderByDesc(ApplyRecord::getCreateTime);
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new BusinessException("无效的审核状态: " + status);
            }
            wrapper.eq(ApplyRecord::getStatus, status);
        }
        if (direction != null && !direction.isBlank()) {
            wrapper.eq(ApplyRecord::getDirection, direction);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(ApplyRecord::getRealName, keyword);
        }
        Page<ApplyRecord> result = applyRecordMapper.selectPage(mpPage, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public ApplyRecord getApplicationDetail(Long id) {
        ApplyRecord record = applyRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("报名记录不存在");
        }
        return record;
    }

    @Override
    public void reviewApplication(Long id, ReviewRequest req) {
        Long reviewerId = StpUtil.getLoginIdAsLong();

        if (!VALID_STATUSES.contains(req.getStatus())) {
            throw new BusinessException("无效的审核状态: " + req.getStatus()
                    + "，有效值为: " + String.join(", ", VALID_STATUSES));
        }

        ApplyRecord record = applyRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("报名记录不存在");
        }

        record.setStatus(req.getStatus());
        record.setReviewRemark(req.getReviewRemark() != null ? req.getReviewRemark() : "");
        record.setReviewerId(reviewerId);
        record.setReviewedAt(LocalDateTime.now());

        applyRecordMapper.updateById(record);
        log.info("管理员 {} 审核了报名记录 {}，状态更新为 {}", reviewerId, id, req.getStatus());
    }

    private ApplyRecord buildRecord(ApplyRequest req, Long userId) {
        ApplyRecord record = new ApplyRecord();
        record.setUserId(userId);
        record.setRealName(req.getRealName());
        record.setGrade(req.getGrade());
        record.setMajor(req.getMajor());
        record.setClassName(req.getClassName());
        record.setPhone(req.getPhone());
        record.setQq(req.getQq());
        record.setDirection(req.getDirection());
        record.setHasProgrammingBasis(req.getHasProgrammingBasis() != null ? req.getHasProgrammingBasis() : 0);
        record.setSkills(req.getSkills() != null ? req.getSkills() : "");
        record.setIntroduction(req.getIntroduction() != null ? req.getIntroduction() : "");
        record.setReason(req.getReason() != null ? req.getReason() : "");
        record.setWeeklyAvailableTime(req.getWeeklyAvailableTime() != null ? req.getWeeklyAvailableTime() : "");
        record.setPortfolioUrl(req.getPortfolioUrl() != null ? req.getPortfolioUrl() : "");
        return record;
    }
}

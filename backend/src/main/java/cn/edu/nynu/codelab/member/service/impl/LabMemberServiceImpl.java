package cn.edu.nynu.codelab.member.service.impl;

import cn.edu.nynu.codelab.member.dto.MemberCreateRequest;
import cn.edu.nynu.codelab.member.entity.LabMember;
import cn.edu.nynu.codelab.member.mapper.LabMemberMapper;
import cn.edu.nynu.codelab.member.service.LabMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实验室成员服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LabMemberServiceImpl implements LabMemberService {

    private final LabMemberMapper labMemberMapper;

    @Override
    public List<LabMember> listEnabled() {
        LambdaQueryWrapper<LabMember> wrapper = new LambdaQueryWrapper<LabMember>()
                .eq(LabMember::getStatus, LabMember.STATUS_ENABLED)
                .orderByDesc(LabMember::getSortOrder);
        return labMemberMapper.selectList(wrapper);
    }

    @Override
    public LabMember getById(Long id) {
        LabMember member = labMemberMapper.selectOne(
                new LambdaQueryWrapper<LabMember>()
                        .eq(LabMember::getId, id)
                        .eq(LabMember::getStatus, LabMember.STATUS_ENABLED)
        );
        if (member == null) {
            throw new RuntimeException("成员不存在或已停用");
        }
        return member;
    }

    @Override
    public List<LabMember> listAll(Integer status) {
        LambdaQueryWrapper<LabMember> wrapper = new LambdaQueryWrapper<LabMember>()
                .orderByDesc(LabMember::getSortOrder)
                .orderByDesc(LabMember::getCreateTime);
        if (status != null) {
            wrapper.eq(LabMember::getStatus, status);
        }
        return labMemberMapper.selectList(wrapper);
    }

    @Override
    public LabMember adminGetById(Long id) {
        LabMember member = labMemberMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        return member;
    }

    @Override
    public LabMember create(MemberCreateRequest req) {
        LabMember member = new LabMember();
        member.setName(req.getName());
        member.setRoleTitle(req.getRoleTitle());
        member.setAvatarUrl(req.getAvatarUrl());
        member.setDirectionId(req.getDirectionId());
        member.setGrade(req.getGrade());
        member.setBio(req.getBio());
        member.setSkills(req.getSkills());
        member.setGithubUrl(req.getGithubUrl());
        member.setBlogUrl(req.getBlogUrl());
        member.setEmail(req.getEmail());
        member.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        member.setStatus(req.getStatus() != null ? req.getStatus() : LabMember.STATUS_ENABLED);
        labMemberMapper.insert(member);
        log.info("创建了实验室成员，ID: {}", member.getId());
        return member;
    }

    @Override
    public LabMember update(Long id, MemberCreateRequest req) {
        LabMember member = labMemberMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        member.setName(req.getName());
        member.setRoleTitle(req.getRoleTitle());
        member.setAvatarUrl(req.getAvatarUrl());
        member.setDirectionId(req.getDirectionId());
        member.setGrade(req.getGrade());
        member.setBio(req.getBio());
        member.setSkills(req.getSkills());
        member.setGithubUrl(req.getGithubUrl());
        member.setBlogUrl(req.getBlogUrl());
        member.setEmail(req.getEmail());
        if (req.getSortOrder() != null) {
            member.setSortOrder(req.getSortOrder());
        }
        if (req.getStatus() != null) {
            member.setStatus(req.getStatus());
        }
        labMemberMapper.updateById(member);
        log.info("更新了实验室成员 ID: {}", id);
        return member;
    }

    @Override
    public void delete(Long id) {
        LabMember member = labMemberMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        labMemberMapper.deleteById(id);
        log.info("删除了实验室成员 ID: {}", id);
    }
}

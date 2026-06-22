package cn.edu.nynu.codelab.member.service;

import cn.edu.nynu.codelab.member.dto.MemberCreateRequest;
import cn.edu.nynu.codelab.member.entity.LabMember;

import java.util.List;

/**
 * 实验室成员服务接口
 *
 * @author NYNU Code Lab
 */
public interface LabMemberService {

    /**
     * 前台：获取所有已启用的成员列表，按 sortOrder 降序排列
     */
    List<LabMember> listEnabled();

    /**
     * 前台：根据 ID 获取启用状态的成员详情
     */
    LabMember getById(Long id);

    /**
     * 后台：获取所有成员列表（可按状态筛选）
     */
    List<LabMember> listAll(Integer status);

    /**
     * 后台：根据 ID 获取成员详情（不限制状态）
     */
    LabMember adminGetById(Long id);

    /**
     * 后台：创建成员
     */
    LabMember create(MemberCreateRequest req);

    /**
     * 后台：更新成员
     */
    LabMember update(Long id, MemberCreateRequest req);

    /**
     * 后台：删除成员（软删除）
     */
    void delete(Long id);
}

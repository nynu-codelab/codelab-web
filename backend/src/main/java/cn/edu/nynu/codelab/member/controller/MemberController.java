package cn.edu.nynu.codelab.member.controller;

import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.member.entity.LabMember;
import cn.edu.nynu.codelab.member.service.LabMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台实验室成员接口（公开访问）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final LabMemberService labMemberService;

    @GetMapping
    public Result<List<LabMember>> list() {
        List<LabMember> members = labMemberService.listEnabled();
        return Result.success(members);
    }

    @GetMapping("/{id}")
    public Result<LabMember> detail(@PathVariable Long id) {
        LabMember member = labMemberService.getById(id);
        return Result.success(member);
    }
}

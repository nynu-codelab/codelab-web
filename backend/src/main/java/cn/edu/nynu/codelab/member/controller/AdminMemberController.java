package cn.edu.nynu.codelab.member.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.member.dto.MemberCreateRequest;
import cn.edu.nynu.codelab.member.entity.LabMember;
import cn.edu.nynu.codelab.member.service.LabMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台实验室成员管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminMemberController {

    private final LabMemberService labMemberService;

    @GetMapping
    public Result<List<LabMember>> list(
            @RequestParam(required = false) Integer status) {
        List<LabMember> members = labMemberService.listAll(status);
        return Result.success(members);
    }

    @GetMapping("/{id}")
    public Result<LabMember> detail(@PathVariable Long id) {
        LabMember member = labMemberService.adminGetById(id);
        return Result.success(member);
    }

    @PostMapping
    public Result<LabMember> create(@Valid @RequestBody MemberCreateRequest req) {
        LabMember member = labMemberService.create(req);
        return Result.success(member);
    }

    @PutMapping("/{id}")
    public Result<LabMember> update(@PathVariable Long id, @Valid @RequestBody MemberCreateRequest req) {
        LabMember member = labMemberService.update(id, req);
        return Result.success(member);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        labMemberService.delete(id);
        return Result.success("删除成功");
    }
}

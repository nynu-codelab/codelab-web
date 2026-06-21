package cn.edu.nynu.codelab.project.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.project.dto.ProjectCreateRequest;
import cn.edu.nynu.codelab.project.entity.Project;
import cn.edu.nynu.codelab.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台项目成果管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminProjectController {

    private final ProjectService projectService;

    @GetMapping
    public Result<List<Project>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer featured) {
        List<Project> projects = projectService.adminList(status, featured);
        return Result.success(projects);
    }

    @GetMapping("/{id}")
    public Result<Project> detail(@PathVariable Long id) {
        Project project = projectService.adminGetById(id);
        return Result.success(project);
    }

    @PostMapping
    public Result<Project> create(@Valid @RequestBody ProjectCreateRequest req) {
        Project project = projectService.create(req);
        return Result.success(project);
    }

    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectCreateRequest req) {
        Project project = projectService.update(id, req);
        return Result.success(project);
    }

    @PutMapping("/{id}/publish")
    public Result<Project> publish(@PathVariable Long id) {
        Project project = projectService.publish(id);
        return Result.success(project);
    }

    @PutMapping("/{id}/offline")
    public Result<Project> offline(@PathVariable Long id) {
        Project project = projectService.offline(id);
        return Result.success(project);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.success("删除成功");
    }
}

package cn.edu.nynu.codelab.project.controller;

import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.project.entity.Project;
import cn.edu.nynu.codelab.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台项目成果接口（公开访问）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public Result<PageResult<Project>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Project> projects = projectService.listPublishedPaged(page, pageSize);
        return Result.success(projects);
    }

    @GetMapping("/featured")
    public Result<List<Project>> featured() {
        List<Project> projects = projectService.listFeatured();
        return Result.success(projects);
    }

    @GetMapping("/{id}")
    public Result<Project> detail(@PathVariable Long id) {
        Project project = projectService.getPublishedById(id);
        return Result.success(project);
    }
}

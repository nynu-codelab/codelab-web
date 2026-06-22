package cn.edu.nynu.codelab.direction.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.direction.dto.DirectionCreateRequest;
import cn.edu.nynu.codelab.direction.entity.LabDirection;
import cn.edu.nynu.codelab.direction.service.LabDirectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台实验室方向管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/directions")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminDirectionController {

    private final LabDirectionService labDirectionService;

    /**
     * 获取所有方向列表（可按状态筛选）
     */
    @GetMapping
    public Result<List<LabDirection>> list(@RequestParam(required = false) Integer status) {
        List<LabDirection> directions = labDirectionService.listAll(status);
        return Result.success(directions);
    }

    /**
     * 获取方向详情
     */
    @GetMapping("/{id}")
    public Result<LabDirection> detail(@PathVariable Long id) {
        LabDirection direction = labDirectionService.adminGetById(id);
        return Result.success(direction);
    }

    /**
     * 创建方向
     */
    @PostMapping
    public Result<LabDirection> create(@Valid @RequestBody DirectionCreateRequest req) {
        LabDirection direction = labDirectionService.create(req);
        return Result.success(direction);
    }

    /**
     * 更新方向
     */
    @PutMapping("/{id}")
    public Result<LabDirection> update(@PathVariable Long id, @Valid @RequestBody DirectionCreateRequest req) {
        LabDirection direction = labDirectionService.update(id, req);
        return Result.success(direction);
    }

    /**
     * 启用方向
     */
    @PutMapping("/{id}/enable")
    public Result<?> enable(@PathVariable Long id) {
        labDirectionService.enable(id);
        return Result.success("启用成功");
    }

    /**
     * 禁用方向
     */
    @PutMapping("/{id}/disable")
    public Result<?> disable(@PathVariable Long id) {
        labDirectionService.disable(id);
        return Result.success("禁用成功");
    }

    /**
     * 删除方向
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        labDirectionService.delete(id);
        return Result.success("删除成功");
    }
}

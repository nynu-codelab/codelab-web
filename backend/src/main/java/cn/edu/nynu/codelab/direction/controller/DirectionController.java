package cn.edu.nynu.codelab.direction.controller;

import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.direction.entity.LabDirection;
import cn.edu.nynu.codelab.direction.service.LabDirectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台实验室方向接口（公开访问）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/directions")
@RequiredArgsConstructor
public class DirectionController {

    private final LabDirectionService labDirectionService;

    /**
     * 获取所有已启用的方向列表
     */
    @GetMapping
    public Result<List<LabDirection>> list() {
        List<LabDirection> directions = labDirectionService.listEnabled();
        return Result.success(directions);
    }

    /**
     * 根据编码获取方向详情
     */
    @GetMapping("/{code}")
    public Result<LabDirection> detail(@PathVariable String code) {
        LabDirection direction = labDirectionService.getByCode(code);
        return Result.success(direction);
    }
}

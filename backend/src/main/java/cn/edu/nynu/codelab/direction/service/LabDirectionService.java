package cn.edu.nynu.codelab.direction.service;

import cn.edu.nynu.codelab.direction.dto.DirectionCreateRequest;
import cn.edu.nynu.codelab.direction.entity.LabDirection;

import java.util.List;

/**
 * 实验室方向服务接口
 *
 * @author NYNU Code Lab
 */
public interface LabDirectionService {

    /**
     * 前台：获取所有已启用的方向列表
     */
    List<LabDirection> listEnabled();

    /**
     * 前台：根据编码获取已启用的方向详情
     */
    LabDirection getByCode(String code);

    /**
     * 前台：根据 ID 获取已启用的方向详情
     */
    LabDirection getById(Long id);

    /**
     * 后台：获取所有方向列表（可按状态筛选）
     */
    List<LabDirection> listAll(Integer status);

    /**
     * 后台：获取方向详情
     */
    LabDirection adminGetById(Long id);

    /**
     * 后台：创建方向
     */
    LabDirection create(DirectionCreateRequest req);

    /**
     * 后台：更新方向
     */
    LabDirection update(Long id, DirectionCreateRequest req);

    /**
     * 后台：启用方向
     */
    void enable(Long id);

    /**
     * 后台：禁用方向
     */
    void disable(Long id);

    /**
     * 后台：删除方向（软删除）
     */
    void delete(Long id);
}

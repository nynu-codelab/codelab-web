package cn.edu.nynu.codelab.project.service;

import cn.edu.nynu.codelab.project.dto.ProjectCreateRequest;
import cn.edu.nynu.codelab.project.entity.Project;

import java.util.List;

/**
 * 项目成果服务接口
 *
 * @author NYNU Code Lab
 */
public interface ProjectService {

    /**
     * 前台：获取已发布项目列表
     */
    List<Project> listPublished();

    /**
     * 前台：获取已发布且精选的项目列表（首页展示）
     */
    List<Project> listFeatured();

    /**
     * 前台：获取已发布项目详情
     */
    Project getPublishedById(Long id);

    /**
     * 后台：获取所有项目（可按状态筛选、可按精选筛选）
     */
    List<Project> adminList(String status, Integer featured);

    /**
     * 后台：获取项目详情
     */
    Project adminGetById(Long id);

    /**
     * 后台：创建项目成果
     */
    Project create(ProjectCreateRequest req);

    /**
     * 后台：更新项目成果
     */
    Project update(Long id, ProjectCreateRequest req);

    /**
     * 后台：发布项目成果
     */
    Project publish(Long id);

    /**
     * 后台：下架项目成果
     */
    Project offline(Long id);

    /**
     * 后台：删除项目成果（软删除）
     */
    void delete(Long id);
}

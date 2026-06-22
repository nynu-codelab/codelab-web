package cn.edu.nynu.codelab.project.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.project.dto.ProjectCreateRequest;
import cn.edu.nynu.codelab.project.entity.Project;
import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.project.mapper.ProjectMapper;
import cn.edu.nynu.codelab.project.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目成果服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            Project.STATUS_DRAFT,
            Project.STATUS_PUBLISHED,
            Project.STATUS_OFFLINE
    ));

    private final ProjectMapper projectMapper;

    @Override
    public List<Project> listPublished() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, Project.STATUS_PUBLISHED)
                .orderByDesc(Project::getSortOrder)
                .orderByDesc(Project::getPublishedAt);
        return projectMapper.selectList(wrapper);
    }

    @Override
    public PageResult<Project> listPublishedPaged(int page, int pageSize) {
        Page<Project> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, Project.STATUS_PUBLISHED)
                .orderByDesc(Project::getSortOrder)
                .orderByDesc(Project::getPublishedAt);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public List<Project> listFeatured() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, Project.STATUS_PUBLISHED)
                .eq(Project::getFeatured, 1)
                .orderByDesc(Project::getSortOrder)
                .orderByDesc(Project::getPublishedAt);
        return projectMapper.selectList(wrapper);
    }

    @Override
    public Project getPublishedById(Long id) {
        Project project = projectMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getId, id)
                        .eq(Project::getStatus, Project.STATUS_PUBLISHED)
        );
        if (project == null) {
            throw new RuntimeException("项目不存在或未发布");
        }
        // 增加浏览次数
        project.setViewCount(project.getViewCount() + 1);
        projectMapper.updateById(project);
        return project;
    }

    @Override
    public List<Project> adminList(String status, Integer featured) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .orderByDesc(Project::getCreateTime);
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new RuntimeException("无效的项目状态: " + status);
            }
            wrapper.eq(Project::getStatus, status);
        }
        if (featured != null) {
            wrapper.eq(Project::getFeatured, featured);
        }
        return projectMapper.selectList(wrapper);
    }

    @Override
    public PageResult<Project> adminListPaged(int page, int pageSize, String status, Integer featured) {
        Page<Project> mpPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .orderByDesc(Project::getCreateTime);
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new RuntimeException("无效的项目状态: " + status);
            }
            wrapper.eq(Project::getStatus, status);
        }
        if (featured != null) {
            wrapper.eq(Project::getFeatured, featured);
        }
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), page, pageSize);
    }

    @Override
    public Project adminGetById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        return project;
    }

    @Override
    public Project create(ProjectCreateRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();
        Project project = buildProject(req, userId);
        project.setStatus(Project.STATUS_DRAFT);
        projectMapper.insert(project);
        log.info("管理员 {} 创建了项目成果，ID: {}", userId, project.getId());
        return project;
    }

    @Override
    public Project update(Long id, ProjectCreateRequest req) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        project.setTitle(req.getTitle());
        project.setSummary(req.getSummary() != null ? req.getSummary() : "");
        project.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        project.setDescriptionMarkdown(req.getDescriptionMarkdown());
        project.setProjectType(req.getProjectType() != null ? req.getProjectType() : "");
        project.setTechStack(req.getTechStack() != null ? req.getTechStack() : "");
        project.setLeaderName(req.getLeaderName() != null ? req.getLeaderName() : "");
        project.setMembersText(req.getMembersText() != null ? req.getMembersText() : "");
        project.setRepoUrl(req.getRepoUrl() != null ? req.getRepoUrl() : "");
        project.setDemoUrl(req.getDemoUrl() != null ? req.getDemoUrl() : "");
        project.setDocumentUrl(req.getDocumentUrl() != null ? req.getDocumentUrl() : "");
        if (req.getFeatured() != null) {
            project.setFeatured(req.getFeatured());
        }
        if (req.getSortOrder() != null) {
            project.setSortOrder(req.getSortOrder());
        }
        projectMapper.updateById(project);
        log.info("管理员更新了项目成果 ID: {}", id);
        return project;
    }

    @Override
    public Project publish(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!Project.STATUS_DRAFT.equals(project.getStatus())) {
            throw new RuntimeException("只有草稿状态的项目才能发布");
        }
        project.setStatus(Project.STATUS_PUBLISHED);
        project.setPublishedAt(LocalDateTime.now());
        projectMapper.updateById(project);
        log.info("项目成果 {} 已发布", id);
        return project;
    }

    @Override
    public Project offline(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (!Project.STATUS_PUBLISHED.equals(project.getStatus())) {
            throw new RuntimeException("只有已发布状态的项目才能下架");
        }
        project.setStatus(Project.STATUS_OFFLINE);
        projectMapper.updateById(project);
        log.info("项目成果 {} 已下架", id);
        return project;
    }

    @Override
    public void delete(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        projectMapper.deleteById(id);
        log.info("项目成果 {} 已删除", id);
    }

    private Project buildProject(ProjectCreateRequest req, Long authorId) {
        Project project = new Project();
        project.setTitle(req.getTitle());
        project.setSummary(req.getSummary() != null ? req.getSummary() : "");
        project.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        project.setDescriptionMarkdown(req.getDescriptionMarkdown());
        project.setProjectType(req.getProjectType() != null ? req.getProjectType() : "");
        project.setTechStack(req.getTechStack() != null ? req.getTechStack() : "");
        project.setLeaderName(req.getLeaderName() != null ? req.getLeaderName() : "");
        project.setMembersText(req.getMembersText() != null ? req.getMembersText() : "");
        project.setRepoUrl(req.getRepoUrl() != null ? req.getRepoUrl() : "");
        project.setDemoUrl(req.getDemoUrl() != null ? req.getDemoUrl() : "");
        project.setDocumentUrl(req.getDocumentUrl() != null ? req.getDocumentUrl() : "");
        project.setFeatured(req.getFeatured() != null ? req.getFeatured() : 0);
        project.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        project.setViewCount(0);
        project.setAuthorId(authorId);
        return project;
    }
}

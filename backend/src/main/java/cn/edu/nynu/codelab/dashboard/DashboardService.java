package cn.edu.nynu.codelab.dashboard;

import cn.edu.nynu.codelab.article.entity.Article;
import cn.edu.nynu.codelab.article.mapper.ArticleMapper;
import cn.edu.nynu.codelab.member.mapper.LabMemberMapper;
import cn.edu.nynu.codelab.project.entity.Project;
import cn.edu.nynu.codelab.project.mapper.ProjectMapper;
import cn.edu.nynu.codelab.recruit.entity.ApplyRecord;
import cn.edu.nynu.codelab.recruit.mapper.ApplyRecordMapper;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 仪表盘统计服务
 *
 * @author NYNU Code Lab
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final ProjectMapper projectMapper;
    private final ApplyRecordMapper applyRecordMapper;
    private final LabMemberMapper labMemberMapper;

    /**
     * 获取仪表盘统计数据
     *
     * @return 各类统计数据的键值对
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 用户总数（@TableLogic 自动过滤已删除）
        stats.put("userCount", userMapper.selectCount(null));

        // 文章总数（@TableLogic 自动过滤已删除）
        stats.put("articleCount", articleMapper.selectCount(null));

        // 已发布文章数
        LambdaQueryWrapper<Article> publishedArticleWrapper = new LambdaQueryWrapper<>();
        publishedArticleWrapper.eq(Article::getStatus, Article.STATUS_PUBLISHED);
        stats.put("publishedArticleCount", articleMapper.selectCount(publishedArticleWrapper));

        // 项目总数（@TableLogic 自动过滤已删除）
        stats.put("projectCount", projectMapper.selectCount(null));

        // 已发布项目数
        LambdaQueryWrapper<Project> publishedProjectWrapper = new LambdaQueryWrapper<>();
        publishedProjectWrapper.eq(Project::getStatus, Project.STATUS_PUBLISHED);
        stats.put("publishedProjectCount", projectMapper.selectCount(publishedProjectWrapper));

        // 招新报名 — 待处理
        LambdaQueryWrapper<ApplyRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(ApplyRecord::getStatus, ApplyRecord.STATUS_PENDING);
        stats.put("recruitPending", applyRecordMapper.selectCount(pendingWrapper));

        // 招新报名 — 总数（@TableLogic 自动过滤已删除）
        stats.put("recruitTotal", applyRecordMapper.selectCount(null));

        // 招新报名 — 已通过
        LambdaQueryWrapper<ApplyRecord> passedWrapper = new LambdaQueryWrapper<>();
        passedWrapper.eq(ApplyRecord::getStatus, ApplyRecord.STATUS_PASSED);
        stats.put("recruitPassed", applyRecordMapper.selectCount(passedWrapper));

        // 招新报名 — 已拒绝
        LambdaQueryWrapper<ApplyRecord> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(ApplyRecord::getStatus, ApplyRecord.STATUS_REJECTED);
        stats.put("recruitRejected", applyRecordMapper.selectCount(rejectedWrapper));

        // 实验室成员总数（@TableLogic 自动过滤已删除）
        stats.put("memberCount", labMemberMapper.selectCount(null));

        return stats;
    }
}

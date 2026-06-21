package cn.edu.nynu.codelab.article.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.article.dto.ArticleCreateRequest;
import cn.edu.nynu.codelab.article.entity.Article;
import cn.edu.nynu.codelab.article.mapper.ArticleMapper;
import cn.edu.nynu.codelab.article.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文章服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            Article.STATUS_DRAFT,
            Article.STATUS_PUBLISHED,
            Article.STATUS_OFFLINE
    ));

    private final ArticleMapper articleMapper;

    @Override
    public List<Article> listPublished(String category) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, Article.STATUS_PUBLISHED)
                .orderByDesc(Article::getPublishedAt)
                .orderByDesc(Article::getSortOrder);
        if (category != null && !category.isBlank()) {
            wrapper.eq(Article::getCategory, category);
        }
        return articleMapper.selectList(wrapper);
    }

    @Override
    public Article getPublishedById(Long id) {
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getId, id)
                        .eq(Article::getStatus, Article.STATUS_PUBLISHED)
        );
        if (article == null) {
            throw new RuntimeException("文章不存在或未发布");
        }
        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return article;
    }

    @Override
    public List<Article> adminList(String status) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);
        if (status != null && !status.isBlank()) {
            if (!VALID_STATUSES.contains(status)) {
                throw new RuntimeException("无效的文章状态: " + status);
            }
            wrapper.eq(Article::getStatus, status);
        }
        return articleMapper.selectList(wrapper);
    }

    @Override
    public Article adminGetById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return article;
    }

    @Override
    public Article create(ArticleCreateRequest req) {
        Long userId = StpUtil.getLoginIdAsLong();
        Article article = buildArticle(req, userId);
        article.setStatus(Article.STATUS_DRAFT);
        articleMapper.insert(article);
        log.info("管理员 {} 创建了文章，ID: {}", userId, article.getId());
        return article;
    }

    @Override
    public Article update(Long id, ArticleCreateRequest req) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        article.setTitle(req.getTitle());
        article.setSummary(req.getSummary() != null ? req.getSummary() : "");
        article.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        article.setContentMarkdown(req.getContentMarkdown());
        article.setCategory(req.getCategory() != null ? req.getCategory() : "");
        article.setTags(req.getTags() != null ? req.getTags() : "");
        if (req.getSortOrder() != null) {
            article.setSortOrder(req.getSortOrder());
        }
        articleMapper.updateById(article);
        log.info("管理员更新了文章 ID: {}", id);
        return article;
    }

    @Override
    public Article publish(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (!Article.STATUS_DRAFT.equals(article.getStatus())) {
            throw new RuntimeException("只有草稿状态的文章才能发布");
        }
        article.setStatus(Article.STATUS_PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        articleMapper.updateById(article);
        log.info("文章 {} 已发布", id);
        return article;
    }

    @Override
    public Article offline(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (!Article.STATUS_PUBLISHED.equals(article.getStatus())) {
            throw new RuntimeException("只有已发布状态的文章才能下架");
        }
        article.setStatus(Article.STATUS_OFFLINE);
        articleMapper.updateById(article);
        log.info("文章 {} 已下架", id);
        return article;
    }

    @Override
    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        articleMapper.deleteById(id);
        log.info("文章 {} 已删除", id);
    }

    private Article buildArticle(ArticleCreateRequest req, Long authorId) {
        Article article = new Article();
        article.setTitle(req.getTitle());
        article.setSummary(req.getSummary() != null ? req.getSummary() : "");
        article.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        article.setContentMarkdown(req.getContentMarkdown());
        article.setCategory(req.getCategory() != null ? req.getCategory() : "");
        article.setTags(req.getTags() != null ? req.getTags() : "");
        article.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        article.setViewCount(0);
        article.setAuthorId(authorId);
        return article;
    }
}

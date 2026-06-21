package cn.edu.nynu.codelab.article.service;

import cn.edu.nynu.codelab.article.dto.ArticleCreateRequest;
import cn.edu.nynu.codelab.article.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 *
 * @author NYNU Code Lab
 */
public interface ArticleService {

    /**
     * 前台：获取已发布文章列表
     */
    List<Article> listPublished(String category);

    /**
     * 前台：获取已发布文章详情
     */
    Article getPublishedById(Long id);

    /**
     * 后台：获取所有文章（可按状态筛选）
     */
    List<Article> adminList(String status);

    /**
     * 后台：获取文章详情
     */
    Article adminGetById(Long id);

    /**
     * 后台：创建文章
     */
    Article create(ArticleCreateRequest req);

    /**
     * 后台：更新文章
     */
    Article update(Long id, ArticleCreateRequest req);

    /**
     * 后台：发布文章
     */
    Article publish(Long id);

    /**
     * 后台：下架文章
     */
    Article offline(Long id);

    /**
     * 后台：删除文章（软删除）
     */
    void delete(Long id);
}

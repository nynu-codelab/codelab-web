package cn.edu.nynu.codelab.article.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.article.dto.ArticleCreateRequest;
import cn.edu.nynu.codelab.article.entity.Article;
import cn.edu.nynu.codelab.article.service.ArticleService;
import cn.edu.nynu.codelab.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台文章管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Result<List<Article>> list(@RequestParam(required = false) String status) {
        List<Article> articles = articleService.adminList(status);
        return Result.success(articles);
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.adminGetById(id);
        return Result.success(article);
    }

    @PostMapping
    public Result<Article> create(@Valid @RequestBody ArticleCreateRequest req) {
        Article article = articleService.create(req);
        return Result.success(article);
    }

    @PutMapping("/{id}")
    public Result<Article> update(@PathVariable Long id, @Valid @RequestBody ArticleCreateRequest req) {
        Article article = articleService.update(id, req);
        return Result.success(article);
    }

    @PutMapping("/{id}/publish")
    public Result<Article> publish(@PathVariable Long id) {
        Article article = articleService.publish(id);
        return Result.success(article);
    }

    @PutMapping("/{id}/offline")
    public Result<Article> offline(@PathVariable Long id) {
        Article article = articleService.offline(id);
        return Result.success(article);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success("删除成功");
    }
}

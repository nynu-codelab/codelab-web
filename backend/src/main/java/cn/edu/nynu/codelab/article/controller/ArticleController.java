package cn.edu.nynu.codelab.article.controller;

import cn.edu.nynu.codelab.article.entity.Article;
import cn.edu.nynu.codelab.article.service.ArticleService;
import cn.edu.nynu.codelab.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台文章接口（公开访问）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Result<List<Article>> list(@RequestParam(required = false) String category) {
        List<Article> articles = articleService.listPublished(category);
        return Result.success(articles);
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getPublishedById(id);
        return Result.success(article);
    }
}

package cn.edu.nynu.codelab.article.mapper;

import cn.edu.nynu.codelab.article.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

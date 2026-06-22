package cn.edu.nynu.codelab.article.mapper;

import cn.edu.nynu.codelab.article.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 文章 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /** 原子增加浏览量，避免读-改-写竞态条件 */
    @Update("UPDATE lab_article SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
}

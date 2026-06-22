package cn.edu.nynu.codelab.project.mapper;

import cn.edu.nynu.codelab.project.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 项目成果 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    /** 原子增加浏览量，避免读-改-写竞态条件 */
    @Update("UPDATE lab_project SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
}

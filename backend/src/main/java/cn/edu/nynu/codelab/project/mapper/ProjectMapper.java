package cn.edu.nynu.codelab.project.mapper;

import cn.edu.nynu.codelab.project.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目成果 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}

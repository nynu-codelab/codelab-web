package cn.edu.nynu.selab.user.mapper;

import cn.edu.nynu.selab.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author NYNU SE Lab
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

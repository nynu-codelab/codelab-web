package cn.edu.nynu.codelab.user.mapper;

import cn.edu.nynu.codelab.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

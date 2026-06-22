package cn.edu.nynu.codelab.member.mapper;

import cn.edu.nynu.codelab.member.entity.LabMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实验室成员 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface LabMemberMapper extends BaseMapper<LabMember> {
}

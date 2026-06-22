package cn.edu.nynu.codelab.upload.mapper;

import cn.edu.nynu.codelab.upload.entity.UploadFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传记录 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface UploadFileMapper extends BaseMapper<UploadFile> {
}

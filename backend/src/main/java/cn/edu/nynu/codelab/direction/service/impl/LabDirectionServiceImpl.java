package cn.edu.nynu.codelab.direction.service.impl;

import cn.edu.nynu.codelab.direction.dto.DirectionCreateRequest;
import cn.edu.nynu.codelab.direction.entity.LabDirection;
import cn.edu.nynu.codelab.direction.mapper.LabDirectionMapper;
import cn.edu.nynu.codelab.direction.service.LabDirectionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实验室方向服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LabDirectionServiceImpl implements LabDirectionService {

    private final LabDirectionMapper labDirectionMapper;

    @Override
    public List<LabDirection> listEnabled() {
        LambdaQueryWrapper<LabDirection> wrapper = new LambdaQueryWrapper<LabDirection>()
                .eq(LabDirection::getStatus, LabDirection.STATUS_ENABLED)
                .orderByAsc(LabDirection::getSortOrder);
        return labDirectionMapper.selectList(wrapper);
    }

    @Override
    public LabDirection getByCode(String code) {
        LabDirection direction = labDirectionMapper.selectOne(
                new LambdaQueryWrapper<LabDirection>()
                        .eq(LabDirection::getCode, code)
                        .eq(LabDirection::getStatus, LabDirection.STATUS_ENABLED)
        );
        if (direction == null) {
            throw new RuntimeException("方向不存在或已禁用");
        }
        return direction;
    }

    @Override
    public LabDirection getById(Long id) {
        LabDirection direction = labDirectionMapper.selectOne(
                new LambdaQueryWrapper<LabDirection>()
                        .eq(LabDirection::getId, id)
                        .eq(LabDirection::getStatus, LabDirection.STATUS_ENABLED)
        );
        if (direction == null) {
            throw new RuntimeException("方向不存在或已禁用");
        }
        return direction;
    }

    @Override
    public List<LabDirection> listAll(Integer status) {
        LambdaQueryWrapper<LabDirection> wrapper = new LambdaQueryWrapper<LabDirection>()
                .orderByAsc(LabDirection::getSortOrder);
        if (status != null) {
            wrapper.eq(LabDirection::getStatus, status);
        }
        return labDirectionMapper.selectList(wrapper);
    }

    @Override
    public LabDirection adminGetById(Long id) {
        LabDirection direction = labDirectionMapper.selectById(id);
        if (direction == null) {
            throw new RuntimeException("方向不存在");
        }
        return direction;
    }

    @Override
    public LabDirection create(DirectionCreateRequest req) {
        // 校验 code 唯一性
        Long count = labDirectionMapper.selectCount(
                new LambdaQueryWrapper<LabDirection>()
                        .eq(LabDirection::getCode, req.getCode())
        );
        if (count > 0) {
            throw new RuntimeException("方向编码已存在: " + req.getCode());
        }

        LabDirection direction = new LabDirection();
        direction.setName(req.getName());
        direction.setCode(req.getCode());
        direction.setSummary(req.getSummary());
        direction.setDescription(req.getDescription() != null ? req.getDescription() : "");
        direction.setTags(req.getTags() != null ? req.getTags() : "[]");
        direction.setIcon(req.getIcon() != null ? req.getIcon() : "");
        direction.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        direction.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0);
        direction.setStatus(LabDirection.STATUS_ENABLED);

        labDirectionMapper.insert(direction);
        log.info("创建了实验室方向，ID: {}, 名称: {}", direction.getId(), direction.getName());
        return direction;
    }

    @Override
    public LabDirection update(Long id, DirectionCreateRequest req) {
        LabDirection direction = labDirectionMapper.selectById(id);
        if (direction == null) {
            throw new RuntimeException("方向不存在");
        }

        // 校验 code 唯一性（排除自身）
        Long count = labDirectionMapper.selectCount(
                new LambdaQueryWrapper<LabDirection>()
                        .eq(LabDirection::getCode, req.getCode())
                        .ne(LabDirection::getId, id)
        );
        if (count > 0) {
            throw new RuntimeException("方向编码已存在: " + req.getCode());
        }

        direction.setName(req.getName());
        direction.setCode(req.getCode());
        direction.setSummary(req.getSummary());
        direction.setDescription(req.getDescription() != null ? req.getDescription() : "");
        direction.setTags(req.getTags() != null ? req.getTags() : "[]");
        direction.setIcon(req.getIcon() != null ? req.getIcon() : "");
        direction.setCoverUrl(req.getCoverUrl() != null ? req.getCoverUrl() : "");
        if (req.getSortOrder() != null) {
            direction.setSortOrder(req.getSortOrder());
        }

        labDirectionMapper.updateById(direction);
        log.info("更新了实验室方向 ID: {}", id);
        return direction;
    }

    @Override
    public void enable(Long id) {
        LabDirection direction = labDirectionMapper.selectById(id);
        if (direction == null) {
            throw new RuntimeException("方向不存在");
        }
        direction.setStatus(LabDirection.STATUS_ENABLED);
        labDirectionMapper.updateById(direction);
        log.info("启用了实验室方向 ID: {}", id);
    }

    @Override
    public void disable(Long id) {
        LabDirection direction = labDirectionMapper.selectById(id);
        if (direction == null) {
            throw new RuntimeException("方向不存在");
        }
        direction.setStatus(LabDirection.STATUS_DISABLED);
        labDirectionMapper.updateById(direction);
        log.info("禁用了实验室方向 ID: {}", id);
    }

    @Override
    public void delete(Long id) {
        LabDirection direction = labDirectionMapper.selectById(id);
        if (direction == null) {
            throw new RuntimeException("方向不存在");
        }
        labDirectionMapper.deleteById(id);
        log.info("删除了实验室方向 ID: {}", id);
    }
}

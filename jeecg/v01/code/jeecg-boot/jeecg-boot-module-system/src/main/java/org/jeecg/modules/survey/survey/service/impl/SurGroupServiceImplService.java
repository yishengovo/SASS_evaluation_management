package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.entity.SurGroup;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;
import org.jeecg.modules.survey.survey.mapper.SurGroupContactMapper;
import org.jeecg.modules.survey.survey.mapper.SurGroupMapper;
import org.jeecg.modules.survey.survey.service.ISurGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-02  16:53
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurGroupServiceImplService extends ServiceImpl<SurGroupMapper, SurGroup> implements ISurGroupService {
    @Autowired
    SurGroupMapper surGroupMapper;
    @Autowired
    SurGroupContactMapper surGroupContactMapper;

    @Override
    public Boolean addGroup(String name) {
        SurGroup surGroup = new SurGroup();
        surGroup.setName(name);
        return surGroupMapper.insert(surGroup)>0;
    }

    @Override
    public Boolean updateGroup(String id, String name) {
        SurGroup surGroup = surGroupMapper.selectById(id);
        surGroup.setName(name);
        return surGroupMapper.updateById(surGroup)>0;
    }

    @Override
    @Transactional
    public Boolean deleteGroup(String id) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", id);
        surGroupContactMapper.delete(queryWrapper);
        return surGroupMapper.deleteById(id)>0;
    }

    @Override
    public List<SurGroup> getGroups() {
        QueryWrapper<SurGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return surGroupMapper.selectList(queryWrapper);
    }

    @Override
    public String getGroupNameById(String id) {
        return surGroupMapper.selectById(id).getName();
    }

}

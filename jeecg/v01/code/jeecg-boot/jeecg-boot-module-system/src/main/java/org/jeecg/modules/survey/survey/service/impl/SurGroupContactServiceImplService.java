package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;
import org.jeecg.modules.survey.survey.mapper.SurGroupContactMapper;
import org.jeecg.modules.survey.survey.service.ISurGroupContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-02  16:53
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurGroupContactServiceImplService extends ServiceImpl<SurGroupContactMapper, SurGroupContact> implements ISurGroupContactService {
    @Autowired
    SurGroupContactMapper surGroupContactMapper;
    @Override
    public Integer getCountByGroupId(String groupId) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        Integer count = Math.toIntExact(surGroupContactMapper.selectCount(queryWrapper));
        return count;
    }

    @Override
    public Boolean deleteByContactId(String contactId) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        return surGroupContactMapper.delete(queryWrapper)>0;
    }

    @Override
    public List<String> getIdsByContactId(String contactId) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        List<String> ids = surGroupContactMapper.selectList(queryWrapper).stream().map(i->{
            return i.getId();
        }).collect(Collectors.toList());
        return ids;
    }

    @Override
    public List<String> getContactIdsByGroupId(String groupId) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId).orderByDesc("create_time");
        List<String> ids = surGroupContactMapper.selectList(queryWrapper).stream().map(i->{
            return i.getContactId();
        }).collect(Collectors.toList());
        return ids;
    }

    @Override
    public Integer getTotal() {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();

        return Math.toIntExact(surGroupContactMapper.selectCount(queryWrapper));
    }

    @Override
    public List<SurGroupContact> getGroupContactByContactId(String contactId) {
        QueryWrapper<SurGroupContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        return surGroupContactMapper.selectList(queryWrapper);
    }
}

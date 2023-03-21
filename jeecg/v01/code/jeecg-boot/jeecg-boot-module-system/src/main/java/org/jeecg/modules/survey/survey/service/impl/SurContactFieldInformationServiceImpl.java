package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.entity.SurContactFieldInformation;
import org.jeecg.modules.survey.survey.mapper.SurContactFieldInformationMapper;
import org.jeecg.modules.survey.survey.service.ISurContactFieldInformationService;
import org.jeecg.modules.survey.survey.service.ISurContactFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service
 * @Author: GGB
 * @CreateTime: 2022-11-02  20:46
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurContactFieldInformationServiceImpl extends ServiceImpl<SurContactFieldInformationMapper, SurContactFieldInformation> implements ISurContactFieldInformationService {
    @Autowired
    SurContactFieldInformationMapper surContactFieldInformationMapper;
    @Autowired
    ISurContactFieldService surContactFieldService;
    @Override
    public Boolean deleteByContactId(String contactId) {
        QueryWrapper<SurContactFieldInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        return surContactFieldInformationMapper.delete(queryWrapper)>0;
    }

    @Override
    public List<String> getIdsByContactId(String contactId) {
        QueryWrapper<SurContactFieldInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        List<String> idList = surContactFieldInformationMapper.selectList(queryWrapper).stream().map(i->{
            return i.getId();
        }).collect(Collectors.toList());
        return idList;
    }

    @Override
    public List<SurContactFieldInformation> getFieldInfoByContactId(String contactId) {
        QueryWrapper<SurContactFieldInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_id",contactId);
        List<SurContactFieldInformation> surContactFieldInformationList = surContactFieldInformationMapper.selectList(queryWrapper);
        surContactFieldInformationList.stream().forEach(i->{
            i.setFieldName(surContactFieldService.getById(i.getFieldId()).getName());
        });
        return surContactFieldInformationList;
    }
}

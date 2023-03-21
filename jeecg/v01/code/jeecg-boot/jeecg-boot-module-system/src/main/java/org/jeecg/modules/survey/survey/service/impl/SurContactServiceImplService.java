package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.survey.entity.SurContact;
import org.jeecg.modules.survey.survey.entity.SurContactFieldInformation;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;
import org.jeecg.modules.survey.survey.mapper.SurContactMapper;
import org.jeecg.modules.survey.survey.model.SurContactModel;
import org.jeecg.modules.survey.survey.service.ISurContactFieldInformationService;
import org.jeecg.modules.survey.survey.service.ISurContactService;
import org.jeecg.modules.survey.survey.service.ISurGroupContactService;
import org.jeecg.modules.survey.survey.service.ISurGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-02  16:52
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurContactServiceImplService extends ServiceImpl<SurContactMapper, SurContact> implements ISurContactService {
    @Autowired
    SurContactMapper surContactMapper;
    @Autowired
    ISurGroupContactService surGroupContactService;
    @Autowired
    ISurContactFieldInformationService surContactFieldInformationService;
    @Autowired
    ISurGroupService surGroupService;

    @Override
    public String addSurContact(SurContact surContact) {
        surContactMapper.insert(surContact);
        return surContact.getId();
    }

    @Override
    public String updateContact(SurContact surContact) {

        return null;
    }

    @Override
    public List<SurContactModel> getContactList(String groupId, String seachName) {
        QueryWrapper<SurContact> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        if(oConvertUtils.isNotEmpty(seachName)){
            queryWrapper.like("name",seachName).or().like("phone",seachName);
        }
        List<SurContact> surContactList = new ArrayList<>();
        if(oConvertUtils.isEmpty(groupId)){
            surContactList =  surContactMapper.selectList(queryWrapper);
        }else {
            List<String> ids = surGroupContactService.getContactIdsByGroupId(groupId);
            List<SurContact> surContacts = surContactMapper.selectList(queryWrapper);
            surContactList = surContacts.stream().filter(i->ids.contains(i.getId())).collect(Collectors.toList());
        }

        List<SurContactModel> surContactModelList = surContactList.stream().map(i->{
            SurContactModel surContactModel = new SurContactModel();
            List<SurContactFieldInformation> surContactFieldInformationList = surContactFieldInformationService.getFieldInfoByContactId(i.getId());
            List<SurGroupContact> surGroupContactList = surGroupContactService.getGroupContactByContactId(i.getId());
            List<String> groupNameList = surGroupContactList.stream().map(j->{
                return surGroupService.getGroupNameById(j.getGroupId());
            }).collect(Collectors.toList());

            i.setGroupNameList(groupNameList);
            surContactModel.setSurContact(i);
            surContactModel.setSurGroupContactList(surGroupContactList);
            surContactModel.setSurContactFieldInformationList(surContactFieldInformationList);
            return surContactModel;
        }).collect(Collectors.toList());

        return surContactModelList;
    }

    @Override
    public Integer getTotal() {
        QueryWrapper<SurContact> queryWrapper = new QueryWrapper<>();
        return Math.toIntExact(surContactMapper.selectCount(queryWrapper));
    }
}

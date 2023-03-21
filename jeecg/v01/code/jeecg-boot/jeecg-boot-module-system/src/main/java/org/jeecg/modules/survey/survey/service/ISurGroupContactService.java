package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;

import java.util.List;

public interface ISurGroupContactService extends IService<SurGroupContact> {
    //获取该群组有多少联系人
    Integer getCountByGroupId(String groupId);
    //根据联系人id批量删除
    Boolean deleteByContactId(String contactId);
    //通过联系人Id获取信息
    List<String> getIdsByContactId(String contactId);
    //获取联系人ids通过群组id
    List<String> getContactIdsByGroupId(String groupId);
    //获取联系人总数
    Integer getTotal();
    //通过联系人id获取群组联系人信息
    List<SurGroupContact> getGroupContactByContactId(String contactId);
}

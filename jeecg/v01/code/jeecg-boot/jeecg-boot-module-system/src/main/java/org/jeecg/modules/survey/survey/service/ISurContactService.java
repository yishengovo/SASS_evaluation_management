package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurContact;
import org.jeecg.modules.survey.survey.model.SurContactModel;

import java.util.List;

public interface ISurContactService extends IService<SurContact> {
    //添加联系人，返回id
    String addSurContact(SurContact surContact);

    //修改联系人，返回id
    String updateContact(SurContact surContact);
    //获取联系人
    List<SurContactModel> getContactList(String groupId, String serchName);
    //获取联系人总数
    Integer getTotal();
}

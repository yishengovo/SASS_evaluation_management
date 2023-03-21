package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurContactFieldInformation;

import java.util.List;

public interface ISurContactFieldInformationService extends IService<SurContactFieldInformation> {
    //通过联系人id，批量删除
    Boolean deleteByContactId(String contactId);
    //通过联系人id，获取信息id
    List<String> getIdsByContactId(String contactId);
    //通过联系人id，获取信息
    List<SurContactFieldInformation> getFieldInfoByContactId(String contactId);

}

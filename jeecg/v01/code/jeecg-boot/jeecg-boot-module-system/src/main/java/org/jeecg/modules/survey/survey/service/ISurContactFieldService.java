package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurContactField;

import java.util.List;

public interface ISurContactFieldService extends IService<SurContactField> {
    /*新建自定义字段*/
    Boolean addContactField(String name);
    /*修改自定义字段*/
    Boolean updateContactField(String id, String name);
    /*删除自定义字段*/
    Boolean deleteContactField(String id);
    /*查询自定义字段*/
    List<SurContactField> getContactFields();
}

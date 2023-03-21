package org.jeecg.modules.survey.survey.model;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurContact;
import org.jeecg.modules.survey.survey.entity.SurContactFieldInformation;
import org.jeecg.modules.survey.survey.entity.SurGroupContact;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.model
 * @Author: GGB
 * @CreateTime: 2022-11-02  22:15
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class SurContactModel {
    //联系人
    private SurContact surContact;
    //用户自定义联系人字段
    private List<SurContactFieldInformation> surContactFieldInformationList;
    //添加到群组
    private List<SurGroupContact> surGroupContactList;
}

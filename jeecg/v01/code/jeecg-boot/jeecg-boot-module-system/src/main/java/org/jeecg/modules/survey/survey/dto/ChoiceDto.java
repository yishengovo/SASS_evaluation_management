package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

//选项返回类
@Data
public class ChoiceDto {
    //选项的id
    private String id;
    //选项唯一编号
    private String pid;
    //问卷id
    private String surveyUid;
    //问题id
    private String questionId;
    //选项内容
    private String choice;
    //基础分数
    private Object basicScore;
}

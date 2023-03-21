package org.jeecg.modules.survey.client.dto;

import lombok.Data;

@Data
//判断身份返回的360度考核项目被评价人dto
public class AssessmentUserDto {
    //被评价人id
    private String userId;
    //被评价人姓名
    private String name;
    //被评价人手机号
    private String phone;
    //问卷id
    private String surveyId;
    //问卷名称
    private String surName;
    //问卷描述
    private String surContent;
    //问卷json
    private String jsonPreview;
    //答卷状态 0未达题 1答题中  2已完成
    private Integer status;
}

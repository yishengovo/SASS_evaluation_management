package org.jeecg.modules.survey.client.dto;

import lombok.Data;

import java.util.List;

@Data
//测评项目用户答卷结果dto
public class EvaluationUserResultDto {
    //用户id
    private String id;
    //姓名
    private String name;
    /**性别*/
    private String gender;
    /**年龄*/
    private Integer age;
    /**手机号*/
    private String phone;
    //问卷列表
    private List<SurveyDto> surveyList;
}

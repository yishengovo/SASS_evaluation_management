package org.jeecg.modules.survey.client.dto;

import lombok.Data;

import java.util.List;

//项目用户dto
@Data
public class AssessmentUser {
    //被评价者id
    private String id;
    //被评价者姓名
    private String name;
    /**被评价者性别*/
    private String gender;
    /**被评价者年龄*/
    private Integer age;
    /**被评价者手机号*/
    private String phone;
    //问卷模板
    private String jsonPreview;
    //评价者数组
    private List<AssessmentEvaluatorDto> evaluator;
    //分数
    private Integer score;
}

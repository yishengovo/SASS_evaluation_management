package org.jeecg.modules.survey.client.dto;

import lombok.Data;

@Data
//360度项目评价者dto
public class AssessmentEvaluatorDto {
    //评价人id
    private String id;
    //评价人姓名
    private String name;
    //评价人手机号
    private String phone;
    //评价者级别  评价者级别（ 0 自评1上级 2同事 3下级 4其他等）
    private Integer level;
    //答案json
    private String result;
    //答卷状态 0未达题 1答题中  2已完成
    private Integer status;
}

package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

@Data
//被评级人分数Dto
public class EvaluatedScoreDto {
    //被评价人姓名
    private String name;
    //被评价人个人得分
    private String score;
    //总体平均分
    private String averageScore;
    //项目名字
    private String projectName;
}

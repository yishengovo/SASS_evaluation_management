package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

//题目得分结果dto
@Data
public class QuestionScoreDto {
    //题目名称
    private String name;
    //个人得分
    private String selfScore;
    //上级得分
    private String superiorScore;
    //同事得分
    private String colleagueScore;
    //下级得分
    private String subordinateScore;
    //其他得分
    private String otherScore;
    //总体平均分
    private String averageScore;
}

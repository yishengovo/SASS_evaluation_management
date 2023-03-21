package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

//题目统计情况dto
@Data
public class StatisticsDto {
    //题目id
    private String questionId;
    //题目类型
    private String questionType;
    //题目类型名称
    private String questionTypeName;
    //题目内容
    private String questionContent;
    //题目答案
    private String questionAnswer;
}

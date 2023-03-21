package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

import java.util.List;

@Data
public class SurveyDto {
    //问卷id
    private String id;
    //问题id
    private String questionId;
    //问题名称
    private String content;
    //答案数组
    private List<ChoiceDto> choice;
}

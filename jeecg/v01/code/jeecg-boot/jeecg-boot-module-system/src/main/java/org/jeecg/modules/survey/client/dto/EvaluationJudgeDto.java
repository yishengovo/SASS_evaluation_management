package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.survey.survey.entity.Survey;

import java.util.List;

@Data
//测评项目判断身份返回dto
public class EvaluationJudgeDto {
    //填写问卷的评价人
    private SurUser user;
    //该用户要填的问卷
    private List<Survey> surveyList;
}


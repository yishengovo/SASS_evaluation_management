package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurUser;

import java.util.List;

@Data
//判断身份返回dto
public class JudgeDto {
    //填写问卷的评价人
    private SurUser user;
    //360考核dto
    private List<AssessmentUserDto> evaluator;
}

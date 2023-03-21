package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.Survey;

import java.util.List;

@Data
@ApiModel(value = "SurveyAddReq", description = "新建问卷参数类")
public class SurveyAddReq {
    private Survey survey;
    private List<SurveyQuestionReq> question;
}

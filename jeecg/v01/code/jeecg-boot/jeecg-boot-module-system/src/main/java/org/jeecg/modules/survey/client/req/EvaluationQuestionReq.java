package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "EvaluationQuestionReq", description = "测评项目查询问题请求参数" )
public class EvaluationQuestionReq extends PageReq {
    private String surveyId;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "DimensionQuestionReq", description = "查询绑定的问题的请求参数" )
public class DimensionQuestionReq {
    //维度id
    private String id;
    //问卷id
    private String surveyId;
}

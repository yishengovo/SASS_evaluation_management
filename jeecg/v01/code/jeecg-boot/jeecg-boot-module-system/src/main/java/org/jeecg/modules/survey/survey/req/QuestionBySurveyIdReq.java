package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "QuestionBySurveyIdReq", description = "根据问卷ID查询问题列表的请求参数" )
public class QuestionBySurveyIdReq extends PageReq {
    //问卷id
    private String id;
}

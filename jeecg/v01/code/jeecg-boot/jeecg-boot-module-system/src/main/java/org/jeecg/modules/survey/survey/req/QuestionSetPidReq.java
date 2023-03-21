package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "QuestionSetPidReq", description = "问卷问题设置唯一编号的请求参数" )
public class QuestionSetPidReq {
    //问卷id
    private String surveyId;
    //问题id
    private String questionId;
    //唯一编号
    private String pid;
}

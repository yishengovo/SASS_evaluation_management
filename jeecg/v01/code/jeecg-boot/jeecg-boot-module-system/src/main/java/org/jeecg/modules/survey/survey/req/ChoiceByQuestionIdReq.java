package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "ChoiceByQuestionIdReq", description = "根据问题ID查询选项的请求参数" )
public class ChoiceByQuestionIdReq {
    //问卷id
    private String questionId;
    //项目id
    private String projectId;
}

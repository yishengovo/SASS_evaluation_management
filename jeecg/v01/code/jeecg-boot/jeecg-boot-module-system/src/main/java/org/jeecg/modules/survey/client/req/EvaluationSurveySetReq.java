package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

//测评项目设置测评问卷参数类
@Data
@ApiModel( value = "EvaluationSurveySetReq", description = "测评项目设置测评问卷参数类" )
public class EvaluationSurveySetReq {
    //项目id
    private String id;
    //问卷id数组
    private List<String> survey;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SetProjectSurveyReq", description = "设置项目的问卷参数类")
public class SetProjectSurveyReq {
    //项目id
    private String id;
    //问卷id
    private String surveyId;
    //问卷索引
    private List<String> rowKeys;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "SurveyByTypeReq", description = "新建问卷参数类")
public class SurveyByTypeReq extends PageReq {
    //问卷类型
    private String type;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "SurveyReq", description = "查询问卷模板参数")
public class SurveyReq{
    //问卷id
   private String id;
}

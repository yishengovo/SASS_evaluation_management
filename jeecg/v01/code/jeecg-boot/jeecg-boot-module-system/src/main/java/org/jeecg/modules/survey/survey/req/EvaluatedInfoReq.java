package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "EvaluatedInfoReq", description = "查询被评价人的得分信息请求类" )
public class EvaluatedInfoReq  {
    //项目id
    private String projectId;
    //被评价人id
    private String evaluatedId;
}

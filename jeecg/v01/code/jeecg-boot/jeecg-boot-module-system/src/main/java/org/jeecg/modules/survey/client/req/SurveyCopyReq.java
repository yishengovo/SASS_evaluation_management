package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "SurveyCopyReq", description = "复制问卷的参数类" )
public class SurveyCopyReq {
    //源问卷id
    private String id;
    //租户id
    private String tenantId;
    //项目名称
    private String projectName;
    //问卷名称
    private String surveyName;
}

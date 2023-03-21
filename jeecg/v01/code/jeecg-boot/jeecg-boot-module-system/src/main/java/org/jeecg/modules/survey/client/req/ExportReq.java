package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "ExportReq", description = "导出请求参数" )
public class ExportReq {
    //项目id
    private String projectId;
    //问卷id
    private String surveyId;
    //答卷人id
    private String userId;
    //被评价人id
    private String evaluatedId;
}

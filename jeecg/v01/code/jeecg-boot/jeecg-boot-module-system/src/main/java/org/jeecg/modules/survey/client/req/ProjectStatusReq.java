package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "ProjectStatusReq", description = "修改项目状态参数" )
public class ProjectStatusReq {
    private String id;
    private Boolean isPublish;
}

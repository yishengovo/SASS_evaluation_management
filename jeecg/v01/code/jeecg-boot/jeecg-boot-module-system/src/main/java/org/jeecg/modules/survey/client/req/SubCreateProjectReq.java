package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;



@Data
@ApiModel(value = "SubCreateProjectReq", description = "创建项目的请求类")
public class SubCreateProjectReq extends CreateProjectReq{
    private String surProjectId;
}

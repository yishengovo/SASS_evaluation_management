package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "JudgePhoneReq", description = "判断问卷后4位的请求参数" )
public class JudgePhoneReq {
    //项目id
    private String projectId;
    //手机号后4位
    private String phoneSuffix;
}

package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "JudgeReq", description = "判断身份参数类" )
public class JudgeReq {
    //项目id
    private String projectId;
    //手机号
    private String phone;
    //验证码
    private String code;
    //密码
    private String password;
}

package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel( value = "RecoveryReq", description = "回收站请求类" )
public class RecoveryReq {
    //项目id
    private List<String> ids;
}

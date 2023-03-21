package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "WeightSetReq", description = "评价关系权重设置参数类" )
public class WeightSetReq {
    //项目id
    private String id;
    //类型  0为默认平均分配  1为自定义
    private Integer type;
    //自评权重
    private String self;
    //上级权重
    private String superior;
    //同事权重
    private String colleague;
    //下级权重
    private String subordinate;
}

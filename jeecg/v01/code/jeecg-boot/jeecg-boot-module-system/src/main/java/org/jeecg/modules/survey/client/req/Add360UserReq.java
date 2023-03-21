package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel( value = "Add360UserReq", description = "批量添加360度项目的评价人员请求参数" )
public class Add360UserReq {
    //项目id
    private String id;
    //评价人员姓名
    private List<String> names;
}

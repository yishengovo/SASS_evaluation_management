package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "PageReq", description = "分页请求参数" )
public class PageReq {
    private String id;
    private int pageNum;
    private int pageSize;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "DimensionReq", description = "维度绑定题目的请求参数类")
public class DimensionReq {
    private List<Dimension> dimensionList;
}

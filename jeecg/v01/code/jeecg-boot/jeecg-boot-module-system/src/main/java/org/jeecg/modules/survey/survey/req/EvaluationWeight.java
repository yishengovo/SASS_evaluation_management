package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "EvaluationWeight", description = "评价体系的请求参数类")
public class EvaluationWeight {
    //评价关系的主键
    private String id;
    //权重
    private String weight;
}

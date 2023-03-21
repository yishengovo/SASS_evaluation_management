package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Dimension", description = "维度绑定题目的请求参数类")
public class Dimension {
    //维度id
    private String dimensionId;
    //绑定的问题id
    private List<String> questionId;
}

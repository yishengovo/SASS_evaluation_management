package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

//被评者参数类
@Data
@ApiModel(value = "EvaluationSetReq", description = "设置评价关系的请求参数类")
public class EvaluationSetReq {
    List<Evaluation> evaluationList;
}

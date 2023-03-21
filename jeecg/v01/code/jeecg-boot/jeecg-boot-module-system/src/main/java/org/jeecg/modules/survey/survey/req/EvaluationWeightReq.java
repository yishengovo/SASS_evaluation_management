package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EvaluationWeight", description = "评价体系的请求参数类")
public class EvaluationWeightReq {
  private   List<EvaluationWeight> list;
}

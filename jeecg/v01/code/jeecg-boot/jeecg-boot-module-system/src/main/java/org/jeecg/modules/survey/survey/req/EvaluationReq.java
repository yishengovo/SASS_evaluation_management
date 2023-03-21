package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EvaluationReq", description = "评价体系的请求参数类")
public class EvaluationReq {
   private String id;
   private String userId;
   private String userName;
   //0为无上下级的评级体系 1为有上下级的评价体系
   private Integer type;
   //是否为自评
   private Boolean isSelf;
   private String surveyId;
   private String projectId;
   private List<Evaluation> list;
}

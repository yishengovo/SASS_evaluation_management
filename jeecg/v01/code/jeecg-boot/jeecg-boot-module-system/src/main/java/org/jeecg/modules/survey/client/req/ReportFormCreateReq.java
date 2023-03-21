package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ReportFormCreateReq", description = "生成报表参数")
public class ReportFormCreateReq {
  // 项目id
  private String projectId;
  // 答卷人id
  private String userId;
}

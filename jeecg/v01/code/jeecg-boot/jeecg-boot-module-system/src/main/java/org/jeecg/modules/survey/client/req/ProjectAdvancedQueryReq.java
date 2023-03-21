package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "PageReq", description = "项目高级查询参数类")
public class ProjectAdvancedQueryReq extends PageReq {
  // 项目类型
  private String type;
  // 降序还是升序 true为降序
  private Boolean order;
  // 项目状态
  private Boolean isPublish;
  // 项目名称
  private String name;
  // 租户编号
  private String tenantId;
}

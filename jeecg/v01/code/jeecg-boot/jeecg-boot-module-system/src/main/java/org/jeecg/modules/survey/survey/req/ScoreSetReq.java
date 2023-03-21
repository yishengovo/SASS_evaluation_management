package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ScoreSetReq", description = "给选项赋分参数")
public class ScoreSetReq {
    //选项id
  private String id;
  //唯一编号
  private String pid;
  //分数
  private List<Object> score;
  //项目id
  private String projectId;
}

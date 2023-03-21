package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

@Data
// 充值积分排行榜 dto
public class RankDto {
  // 租户名字
  private String name;
  // 充值的积分
  private int total;
}

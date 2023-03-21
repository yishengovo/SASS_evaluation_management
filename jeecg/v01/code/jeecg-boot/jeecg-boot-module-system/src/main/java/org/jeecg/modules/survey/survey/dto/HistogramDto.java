package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

@Data
// 柱状图 dto
public class HistogramDto {
  // x轴为月份
  private String x;
  // y轴为数量
  private int y;
}

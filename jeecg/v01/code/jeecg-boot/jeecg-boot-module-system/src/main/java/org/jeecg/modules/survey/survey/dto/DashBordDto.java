package org.jeecg.modules.survey.survey.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
// 后台首页 dto
public class DashBordDto {
  // 总销售额卡片
  private JSONObject totalSales;
  // 总充值积分量卡片
  private JSONObject orders;
  // 总支付笔数卡片
  private JSONObject pays;
  // 总租户数卡片
  private JSONObject talents;
  // 租户充值排行榜
  private List<RankDto> rankList;
  // 项目数柱状图
  private List<HistogramDto> projectList;
  // 问卷数柱状图
  private List<HistogramDto> surveyList;
}

package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurSurveyProject;
import org.jeecg.modules.survey.survey.entity.SurUser;

@Data
// 调查项目判断身份返回dto
public class InvestigationJudgeDto {
  // 填写问卷的评价人
  private SurUser user;
  // 要填写的问卷
  private SurSurveyProject survey;
}

package org.jeecg.modules.survey.survey.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurEvaluationUser;

import java.util.List;

// 项目用户dto
@Data
public class UserDto {
  // 答卷人id
  private String id;
  // 姓名
  private String name;
  /** 性别 */
  private String gender;
  /** 年龄 */
  private Integer age;
  /** 手机号 */
  private String phone;
  //    问卷名称
  private String surName;
  // 问卷模板
  private String jsonPreview;
  // 用户答案对象
  private String result;
  // 评价者数组
  private List<SurEvaluationUser> evaluator;
  // 分数
  private Integer score;
  // 答题状态
  private Integer status;
}

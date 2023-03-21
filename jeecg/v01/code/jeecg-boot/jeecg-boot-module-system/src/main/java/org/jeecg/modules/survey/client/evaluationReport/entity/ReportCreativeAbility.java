package org.jeecg.modules.survey.client.evaluationReport.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName report_creative_ability
 */
@TableName(value = "report_creative_ability")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCreativeAbility implements Serializable {
  /** */
  @TableId private String id;

  /** 创建人 */
  private String createBy;

  /** 创建日期 */
  private Date createTime;

  /** 更新人 */
  private String updateBy;

  /** 更新日期 */
  private Date updateTime;

  /** 所属部门 */
  private String sysOrgCode;

  /** 总得分 */
  private Integer total;

  /** 等级 */
  private String level;

  /** 冒险性得分 */
  private Integer s1;

  /** 好奇心得分 */
  private Integer s2;

  /** 想象力得分 */
  private Integer s3;

  /** 挑战性得分 */
  private Integer s4;

  /** 创造力评价说明 */
  private String evaluationDescription;

  /** 创造力主要属于 */
  private String mainly;

  /** 创造力最缺乏为 */
  private String lack;

  /** 用户id */
  private String userId;

  /** 问卷id */
  private String surveyId;

  /** 项目id */
  private String projectId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

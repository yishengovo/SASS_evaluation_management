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
 * @TableName report_mode_of_thinking
 */
@TableName(value = "report_mode_of_thinking")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportModeOfThinking implements Serializable {
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

  /** 框1值 */
  private Integer k1;

  /** 框2值 */
  private Integer k2;

  /** 框3值 */
  private Integer k3;

  /** 框4值 */
  private Integer k4;

  /** 框5值 */
  private Integer k5;

  /** 框6值 */
  private Integer k6;

  /** 横向总计1 */
  private Integer hx1;

  /** 横向总计2 */
  private Integer hx2;

  /** 纵向总计1 */
  private Integer zx1;

  /** 纵向总计2 */
  private Integer zx2;

  /** 纵向总计3 */
  private Integer zx3;

  /** 总分 */
  private Integer total;

  /** 思维模式主色 */
  private String dominantColor;

  /** 思维模式主色调说明 */
  private String dominantColorExplain;

  /** 用户id */
  private String userId;

  /** 项目id */
  private String projectId;

  /** 问卷id */
  private String surveyId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

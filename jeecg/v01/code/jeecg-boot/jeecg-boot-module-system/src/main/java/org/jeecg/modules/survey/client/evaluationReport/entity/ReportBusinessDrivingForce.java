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
 * @TableName report_business_driving_force
 */
@TableName(value = "report_business_driving_force")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportBusinessDrivingForce implements Serializable {
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

  /** A：物质报酬 */
  private Integer a1;

  /** B：权力：影响力 */
  private Integer b2;

  /** C：追求成就感 */
  private Integer c3;

  /** D：专精 */
  private Integer d4;

  /** E：创意 */
  private Integer e5;

  /** F：亲和 */
  private Integer f6;

  /** G：自主 */
  private Integer g7;

  /** H：安全 */
  private Integer h8;

  /** I：地位 */
  private Integer i9;

  /** 最高分类型 */
  private String dominant;

  /** 最高分说明 */
  private String dominantExplain;

  /** 次高分类型 */
  private String secondary;

  /** 次高分说明 */
  private String secondaryExplain;

  /** 最低分类型 */
  private String lowest;

  /** 最低分说明 */
  private String lowestExplain;

  /** 答卷人id */
  private String userId;

  /** 项目id */
  private String projectId;

  /** 问卷id */
  private String surveyId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

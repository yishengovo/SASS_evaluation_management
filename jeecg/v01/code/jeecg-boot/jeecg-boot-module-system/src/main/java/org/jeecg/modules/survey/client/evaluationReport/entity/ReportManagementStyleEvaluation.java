package org.jeecg.modules.survey.client.evaluationReport.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName report_management_style_evaluation
 */
@TableName(value = "report_management_style_evaluation")
@Data
public class ReportManagementStyleEvaluation implements Serializable {
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

  /** s1 */
  private Integer s1;

  /** s2 */
  private Integer s2;

  /** s3 */
  private Integer s3;

  /** s4 */
  private Integer s4;

  /** 适应性分数 */
  private Integer result;

  /** 主导型领导力风格 */
  private String dominant;

  /** 主导型领导路风格说明 */
  private String dominantExplain;

  /** 次要领导力风格 */
  private String secondary;

  /** 次要领导力风格说明 */
  private String secondaryExplain;

  /** 适应性类型 */
  private String adaptabilityType;

  /** 适应性说明 */
  private String adaptabilityExplain;

  /** 适应力水平 */
  private String level;

  /** 主导型领导力适合管理员工的类型 */
  private String dominantAdaptType;

  /** 次要领导力适合管理员工的类型 */
  private String secondaryAdaptType;

  /** 答卷人id */
  private String userId;

  /** 问卷id */
  private String surveyId;

  /** 项目id */
  private String projectId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

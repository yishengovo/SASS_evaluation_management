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
 * @TableName report_team_role
 */
@TableName(value = "report_team_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTeamRole implements Serializable {
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

  /** IMP：实施者 */
  private Integer imp;

  /** CO：协调者 */
  private Integer co;

  /** SH：塑造者 */
  private Integer sh;

  /** PL：深思者 */
  private Integer pl;

  /** RI：资源调查者 */
  private Integer ri;

  /** ME：监督者一评估者 */
  private Integer me;

  /** TW：团队协作者 */
  private Integer tw;

  /** CF：完成者 */
  private Integer cf;

  /** 最高类型 */
  private String first;

  /** 最高特征 */
  private String firstFeatures;

  /** 最高团队角色 */
  private String firstRole;

  /** 最高可允许的缺点 */
  private String firstShortcoming;

  /** 第二 */
  private String second;

  /** 第二特征 */
  private String secondFeatures;

  /** 第二团队角色 */
  private String secondRole;

  /** 第二可允许缺点 */
  private String secondShortcoming;

  /** 第三 */
  private String third;

  /** 第三特征 */
  private String thirdFeatures;

  /** 第三团队角色 */
  private String thirdRole;

  /** 第三可允许缺点 */
  private String thirdShortcoming;

  /** 第四 */
  private String fourth;

  /** 第四特征 */
  private String fourthFeatures;

  /** 第四团队角色 */
  private String fourthRole;

  /** 第四可允许缺点 */
  private String fourthShortcoming;

  /** 第八 */
  private String eighth;

  /** 第八特征 */
  private String eighthFeatures;

  /** 第八团队角色 */
  private String eighthRole;

  /** 第八可允许缺点 */
  private String eighthShortcoming;

  /** 用户id */
  private String userId;

  /** 项目id */
  private String projectId;

  /** 问卷id */
  private String surveyId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

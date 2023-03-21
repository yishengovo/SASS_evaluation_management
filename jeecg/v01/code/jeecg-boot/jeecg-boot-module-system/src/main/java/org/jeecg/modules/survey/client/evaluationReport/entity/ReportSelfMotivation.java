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
 * @TableName report_self_motivation
 */
@TableName(value = "report_self_motivation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSelfMotivation implements Serializable {
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

  /** A：财务动机 */
  private Integer a1;

  /** B：表彰与称赞/工作认可 */
  private Integer b1;

  /** C：工作责任 */
  private Integer c1;

  /** D：和你的经理的关系 */
  private Integer d1;

  /** E：晋升机会 */
  private Integer e1;

  /** F：工作上的成就感 */
  private Integer f1;

  /** H：工作内容/工作本身兴趣感 */
  private Integer h1;

  /** I：和他人合作/内部人际关系 */
  private Integer i1;

  /** A1+B1+C1+D1的和 */
  private Integer sum1;

  /** E1+F1+H1+I1的和 */
  private Integer sum2;

  /** 最高得分类型 */
  private String highest;

  /** 最低得分类型 */
  private String second;

  /** 为主 */
  private String mainly;

  /** 说明 */
  private String instructions;

  /** */
  private String projectId;

  /** */
  private String surveyId;

  /** */
  private String userId;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}

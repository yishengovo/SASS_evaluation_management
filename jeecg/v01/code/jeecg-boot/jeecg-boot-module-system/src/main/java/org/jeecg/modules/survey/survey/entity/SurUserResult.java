package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 用户所提交的问卷答案 @Author: jeecg-boot @Date: 2022-07-12 @Version: V1.0
 */
@Data
@TableName("sur_user_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_user_result对象", description = "用户所提交的问卷答案")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurUserResult implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId(type = IdType.ASSIGN_ID)
  @ApiModelProperty(value = "主键")
  private java.lang.String id;
  /** 创建人 */
  @ApiModelProperty(value = "创建人")
  private java.lang.String createBy;
  /** 创建日期 */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "创建日期")
  @TableField(fill = FieldFill.INSERT)
  private java.util.Date createTime;
  /** 更新人 */
  @ApiModelProperty(value = "更新人")
  private java.lang.String updateBy;
  /** 更新日期 */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "更新日期")
  @TableField(fill = FieldFill.UPDATE)
  private java.util.Date updateTime;
  /** 所属部门 */
  @ApiModelProperty(value = "所属部门")
  private java.lang.String sysOrgCode;
  /** 所填问卷的用户的手机号 */
  @Excel(name = "所填问卷的用户的手机号", width = 15)
  @ApiModelProperty(value = "所填问卷的用户的手机号")
  private java.lang.String phone;
  /** 所填问卷的id */
  @Excel(name = "所填问卷的id", width = 15)
  @ApiModelProperty(value = "所填问卷的id")
  private java.lang.String surveyId;
  /** 项目id */
  @Excel(name = "项目id", width = 15)
  @ApiModelProperty(value = "项目id")
  private java.lang.String projectId;
  /** 所填问卷的用户的id */
  @Excel(name = "所填问卷的用户的id", width = 15)
  @ApiModelProperty(value = "所填问卷的用户的id")
  private java.lang.String userId;
  /** 所填问卷的用户的id */
  @Excel(name = "评价人的级别", width = 15)
  @ApiModelProperty(value = "评价人的级别")
  private java.lang.Integer userLevel;
  /** 所填问卷的用户的id */
  @Excel(name = "被评价人的id", width = 15)
  @ApiModelProperty(value = "被评价人的id")
  private java.lang.String evaluatedId;
  /** 父问题Id */
  @Excel(name = "父问题Id", width = 15)
  @ApiModelProperty(value = "父问题Id")
  private java.lang.String parentQuestionId;
  /** 父问题内容 */
  @Excel(name = "父问题内容", width = 15)
  @ApiModelProperty(value = "父问题内容")
  private java.lang.String parentQuestionContent;
  /** 问卷的问题 */
  @Excel(name = "问题Id", width = 15)
  @ApiModelProperty(value = "问题Id")
  private java.lang.String questionId;
  /** 问题类型 */
  @Excel(name = "问题类型", width = 15)
  @ApiModelProperty(value = "问题类型")
  private java.lang.String questionType;
  /** 问卷的问题 */
  @Excel(name = "问卷的问题", width = 15)
  @ApiModelProperty(value = "问卷的问题")
  private java.lang.String questionKey;
  /** 问卷的问题 */
  @Excel(name = "问卷的问题题目文本", width = 15)
  @ApiModelProperty(value = "问卷的问题题目文本")
  private java.lang.String questionTitle;
  /** 问题的答案 */
  @Excel(name = "问题的答案", width = 15)
  @ApiModelProperty(value = "问题的答案")
  private java.lang.String questionValue;
  /** 问题的答案 */
  @Excel(name = "问题的答案", width = 15)
  @ApiModelProperty(value = "问题的答案")
  private Object basicScore;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;
}

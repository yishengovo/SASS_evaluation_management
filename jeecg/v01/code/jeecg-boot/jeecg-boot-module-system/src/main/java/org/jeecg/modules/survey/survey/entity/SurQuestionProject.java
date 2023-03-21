package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 存放项目复制出来的问卷的问题 @Author: jeecg-boot @Date: 2022-08-17 @Version: V1.0
 */
@Data
@TableName("sur_question_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_question_project对象", description = "存放项目复制出来的问卷的问题")
public class SurQuestionProject implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId(type = IdType.ASSIGN_ID)
  @ApiModelProperty(value = "主键")
  @TableField(updateStrategy = FieldStrategy.IGNORED)
  private String id;
  /** 创建人 */
  @ApiModelProperty(value = "创建人")
  private String createBy;
  /** 创建日期 */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "创建日期")
  @TableField(fill = FieldFill.INSERT)
  private Date createTime;
  /** 更新人 */
  @ApiModelProperty(value = "更新人")
  private String updateBy;
  /** 更新日期 */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "更新日期")
  @TableField(fill = FieldFill.UPDATE)
  private Date updateTime;
  /** 所属部门 */
  @ApiModelProperty(value = "所属部门")
  private String sysOrgCode;
  /** 所属问卷的id */
  @Excel(name = "所属问卷的id", width = 15)
  @ApiModelProperty(value = "所属问卷的id")
  private String surveyUid;
  /** 标题 */
  @Excel(name = "标题", width = 15)
  @ApiModelProperty(value = "标题")
  private String content;
  /** 是否为父问题 */
  @Excel(name = "是否为父问题", width = 15)
  @ApiModelProperty(value = "是否为父问题")
  private Boolean isParent;
  /** 父id */
  @Excel(name = "父id", width = 15)
  @ApiModelProperty(value = "父id")
  private String parentId;
  /** 父内容 */
  @Excel(name = "父内容", width = 15)
  @ApiModelProperty(value = "父内容")
  private String parentContent;
  /** 题目文本 */
  @Excel(name = "题目文本", width = 15)
  @ApiModelProperty(value = "题目文本")
  private String title;
  /** 问题类型 */
  @Excel(name = "问题类型", width = 15)
  @ApiModelProperty(value = "问题类型")
  private String typeId;
  /** 所属维度的id */
  @Excel(name = "所属维度的id", width = 15)
  @ApiModelProperty(value = "所属维度的id")
  private String dimensionId;
  /** 是否必填 */
  @Excel(name = "是否必填", width = 15)
  @ApiModelProperty(value = "是否必填")
  private Integer required;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;
}

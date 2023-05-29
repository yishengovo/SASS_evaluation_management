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
 * @Description: 存放项目复制出来的问卷表 @Author: jeecg-boot @Date: 2022-08-17 @Version: V1.0
 */
@Data
@TableName("sur_survey_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_survey_project对象", description = "存放项目复制出来的问卷表")
public class SurSurveyProject implements Serializable {
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
  /** 问卷类型（调查、测评、360度） */
  @Excel(name = "问卷类型（调查、测评、360度）", width = 15)
  @ApiModelProperty(value = "问卷类型（调查、测评、360度）")
  private String type;
  /** 问卷名称 */
  @Excel(name = "问卷名称", width = 15)
  @ApiModelProperty(value = "问卷名称")
  private String surName;
  /** 描述 */
  @Excel(name = "描述", width = 15)
  @ApiModelProperty(value = "描述")
  private String surContent;
  /** 预览json */
  @Excel(name = "预览json", width = 15)
  @ApiModelProperty(value = "预览json")
  private String jsonPreview;
  /** 源问卷id */
  @Excel(name = "源问卷id", width = 15)
  @ApiModelProperty(value = "源问卷id")
  private String srcId;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 用户答题状态 */
  @Excel(name = "用户答题状态", width = 15)
  @ApiModelProperty(value = "用户答题状态")
  @TableField(exist = false)
  private Integer status;
  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;



}

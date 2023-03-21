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
 * @Description: sur_question @Author: jeecg-boot @Date: 2022-06-14 @Version: V1.0
 */
@Data
@TableName("sur_question")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_question对象", description = "sur_question")
public class SurQuestion implements Serializable {
  private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId(type = IdType.ASSIGN_ID)
  @ApiModelProperty(value = "主键")
  private java.lang.String id;
  /** 唯一标识 */
  @Excel(name = "唯一标识", width = 15)
  @ApiModelProperty(value = "唯一标识")
  private java.lang.String fid;
  /** 试题唯一编号 */
  @Excel(name = "试题唯一编号", width = 15)
  @ApiModelProperty(value = "试题唯一编号")
  private java.lang.String pid;
  /** 关联调查 */
  @Excel(name = "关联调查", width = 15)
  @ApiModelProperty(value = "关联调查")
  private java.lang.String surveyUid;
  /** 标题 */
  @Excel(name = "标题", width = 15)
  @ApiModelProperty(value = "标题")
  private java.lang.String content;
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
  private java.lang.String typeId;
  /** 排序 */
  @Excel(name = "排序", width = 15)
  @ApiModelProperty(value = "排序")
  private java.lang.Integer sortIndex;
  /** 其他 */
  @Excel(name = "其他", width = 15)
  @ApiModelProperty(value = "其他")
  private java.lang.Integer hasOther;
  /** 必填 */
  @Excel(name = "必填", width = 15)
  @ApiModelProperty(value = "必填")
  private java.lang.Integer required;
  /** 页码 */
  @Excel(name = "页码", width = 15)
  @ApiModelProperty(value = "页码")
  private java.lang.Integer pageNum;
  /** 索引 */
  @Excel(name = "索引", width = 15)
  @ApiModelProperty(value = "索引")
  private java.lang.Integer qindex;
  /** 绝对编号 */
  @Excel(name = "绝对编号", width = 15)
  @ApiModelProperty(value = "绝对编号")
  private java.lang.Integer absoluteId;
  /** 末尾绝对编号 */
  @Excel(name = "末尾绝对编号", width = 15)
  @ApiModelProperty(value = "末尾绝对编号")
  private java.lang.Integer lastAbsoluteId;
  /** 逻辑隐藏 */
  @Excel(name = "逻辑隐藏", width = 15)
  @ApiModelProperty(value = "逻辑隐藏")
  private java.lang.Integer logicHide;
  /** 互斥选项 */
  @Excel(name = "互斥选项", width = 15)
  @ApiModelProperty(value = "互斥选项")
  private java.lang.String exclusiveOptions;
  /** 标题引用 */
  @Excel(name = "标题引用", width = 15)
  @ApiModelProperty(value = "标题引用")
  private java.lang.Integer titleQuote;
  /** 最大值 */
  @Excel(name = "最大值", width = 15)
  @ApiModelProperty(value = "最大值")
  private java.lang.String maxValue;
  /** 最小值 */
  @Excel(name = "最小值", width = 15)
  @ApiModelProperty(value = "最小值")
  private java.lang.String minValue;
  /** json内容 */
  @Excel(name = "json内容", width = 15)
  @ApiModelProperty(value = "json内容")
  private java.lang.String jsonContent;
  /** 跳转关系 */
  @Excel(name = "跳转关系", width = 15)
  @ApiModelProperty(value = "跳转关系")
  private java.lang.String redirectRelation;
  /** 选择项引用 */
  @Excel(name = "选择项引用", width = 15)
  @ApiModelProperty(value = "选择项引用")
  private java.lang.String choiceQuote;
  /** 组织 */
  @Excel(name = "组织", width = 15)
  @ApiModelProperty(value = "组织")
  private java.lang.String orgUid;
  /** 所属租户 */
  @Excel(name = "所属租户", width = 15)
  @ApiModelProperty(value = "所属租户")
  private java.lang.String tenantUid;
  /** 有效开始时间 */
  @Excel(name = "有效开始时间", width = 15)
  @ApiModelProperty(value = "有效开始时间")
  private java.lang.String enableDate;
  /** 有效截止时间 */
  @Excel(name = "有效截止时间", width = 15)
  @ApiModelProperty(value = "有效截止时间")
  private java.lang.String disableDate;
  /** 删除标记 */
  @Excel(name = "删除标记", width = 15)
  @ApiModelProperty(value = "删除标记")
  private java.lang.Integer dr;
  /** 时间戳 */
  @Excel(name = "时间戳", width = 15)
  @ApiModelProperty(value = "时间戳")
  private java.lang.Integer ts;
  /** 创建人 */
  @ApiModelProperty(value = "创建人")
  private java.lang.String createBy;
  /** 创建人名称 */
  @Excel(name = "创建人名称", width = 15)
  @ApiModelProperty(value = "创建人名称")
  private java.lang.String createName;
  /** 创建时间 */
  @Excel(name = "创建时间", width = 15)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "创建日期")
  @TableField(fill = FieldFill.INSERT)
  private Date createDate;
  /** 更新人 */
  @ApiModelProperty(value = "更新人")
  private java.lang.String updateBy;
  /** 更新人名称 */
  @Excel(name = "更新人名称", width = 15)
  @ApiModelProperty(value = "更新人名称")
  private java.lang.String updateName;
  /** 更新时间 */
  @Excel(name = "更新时间", width = 15)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "更新日期")
  @TableField(fill = FieldFill.UPDATE)
  private Date updateDate;
  /** 所属权重的id */
  @Excel(name = "所属维度的id", width = 15)
  @ApiModelProperty(value = "所属维度的id")
  // mybatis-plus封装的updateById方法来更新数据时，发现更新后数据没有为null还是原来的值，这是因为mybatis-plus在更新的时候做了null判断，默认不更新为null的传参。
  @TableField(updateStrategy = FieldStrategy.IGNORED)
  private java.lang.String dimensionId;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;
}

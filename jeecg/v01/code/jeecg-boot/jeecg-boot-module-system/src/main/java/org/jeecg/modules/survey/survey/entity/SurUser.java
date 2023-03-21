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

/**
 * @Description: 保存填写问卷的用户表 @Author: jeecg-boot @Date: 2022-07-12 @Version: V1.0
 */
@Data
@TableName("sur_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_user对象", description = "保存填写问卷的用户表")
public class SurUser implements Serializable {
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

  @Excel(name = "姓名", width = 15)
  @ApiModelProperty(value = "姓名")
  private java.lang.String name;
  /** 性别 */
  @Excel(name = "性别", width = 15)
  @ApiModelProperty(value = "性别")
  private java.lang.String gender;
  /** 年龄 */
  @Excel(name = "年龄", width = 15)
  @ApiModelProperty(value = "年龄")
  private java.lang.Integer age;
  /** 手机号 */
  @Excel(name = "手机号", width = 15)
  @ApiModelProperty(value = "手机号")
  private java.lang.String phone;
  /** 项目id */
  @Excel(name = "项目id", width = 15)
  @ApiModelProperty(value = "项目id")
  private java.lang.String projectId;
  /** 用户类型 1为导入2为部门 */
  @Excel(name = "1为手动导入的用户 2为部门里的用户 3为调查问卷中自动插入的用户", width = 15)
  @ApiModelProperty(value = "1为手动导入的用户 2为部门里的用户 3为调查问卷中自动插入的用户")
  private Integer type;
  /** 所属租户 */
  @Excel(name = "所属租户", width = 15)
  @ApiModelProperty(value = "所属租户")
  private java.lang.String tenantUid;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;
  /** 是否已经生成报表 */
  @Excel(name = "是否已经生成报表", width = 15)
  @ApiModelProperty(value = "是否已经生成报表")
  private Boolean isGenerate;
  /** 是否已经生成报表 */
  @Excel(name = "是否已经完成项目", width = 15)
  @ApiModelProperty(value = "是否已经完成项目")
  private Boolean isFinished;
}

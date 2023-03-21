package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 问卷项目表 @Author: jeecg-boot @Date: 2022-07-01 @Version: V1.0
 */
@Data
@TableName("sur_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sur_project对象", description = "问卷项目表")
public class SurProject implements Serializable {
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
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private java.util.Date updateTime;
  /** 所属部门 */
  @ApiModelProperty(value = "所属部门")
  private java.lang.String sysOrgCode;
  /** 唯一标识 */
  @Excel(name = "唯一标识", width = 15)
  @ApiModelProperty(value = "唯一标识")
  private java.lang.String fid;
  /** 项目名称 */
  @Excel(name = "项目名称", width = 15)
  @ApiModelProperty(value = "项目名称")
  private java.lang.String projectName;
  /** 项目描述 */
  @Excel(name = "项目描述", width = 15)
  @ApiModelProperty(value = "项目描述")
  private java.lang.String content;
  /** 项目负责人 */
  @Excel(name = "项目负责人", width = 15)
  @ApiModelProperty(value = "项目负责人")
  private java.lang.String leader;

  @Excel(name = "问卷是否发布", width = 15)
  @ApiModelProperty(value = "问卷是否发布")
  private Boolean isPublish;

  @Excel(name = "项目是否需要添加人员", width = 15)
  @ApiModelProperty(value = "项目是否需要添加人员")
  private Boolean isAddUser;
  /** 项目选择答题的用户数 */
  @Excel(name = "项目选择答题的用户数", width = 15)
  @ApiModelProperty(value = "项目选择答题的用户数")
  private java.lang.Integer selectNumber;
  /** 0为无上下级的评价关系模式 1为有上下级的模式 */
  @Excel(name = "0为无上下级的评价关系模式 1为有上下级的模式", width = 15)
  @ApiModelProperty(value = "0为无上下级的评价关系模式 1为有上下级的模式")
  private java.lang.Integer evaluationType;
  /** 项目已经提交的人数 */
  @Excel(name = "项目已经提交的人数", width = 15)
  @ApiModelProperty(value = "项目已经提交的人数")
  private java.lang.Integer collectNumber;
  /** 项目类型 */
  @Excel(name = "项目类型(调查、测评、360度)", width = 15)
  @ApiModelProperty(value = "项目类型")
  private String type;

  @Excel(name = "复制模板后的问卷id", width = 15)
  @ApiModelProperty(value = "复制模板后的问卷id")
  private String surveyCurrentId;
  /** 源问卷模板的id */
  @Excel(name = "源问卷模板的id", width = 15)
  @ApiModelProperty(value = "源问卷模板的id")
  private String surveySrcId;

  @Excel(name = "存放问卷模板数组的索引", width = 15)
  @ApiModelProperty(value = "存放问卷模板数组的索引")
  private String rowKeys;

  @Excel(name = "存放部门用户的rowkeys", width = 15)
  @ApiModelProperty(value = "存放部门用户的rowkeys")
  private String userRowKeys;
  /** 组织 */
  @Excel(name = "组织", width = 15)
  @ApiModelProperty(value = "组织")
  private java.lang.String orgUid;
  /** 所属租户 */
  @Excel(name = "所属租户", width = 15)
  @ApiModelProperty(value = "所属租户")
  private java.lang.String tenantUid;
  // 问卷id
  @TableField(exist = false)
  private String surveyId;

  //    /**租户id*/
  //    @Excel(name = "租户id", width = 15)
  //    @ApiModelProperty(value = "租户id")
  //    private java.lang.Integer tenantId;

  /** 所属租户id */
  @Excel(name = "所属租户id", width = 15)
  @ApiModelProperty(value = "所属租户id")
  private String tenantId;

  @Excel(name = "删除标记", width = 15)
  @ApiModelProperty(value = "删除标记")
  private Boolean isDel;

  @Excel(name = "", width = 15)
  @ApiModelProperty(value = "租户")
  @TableField(exist = false)
  private SysTenant tenant;
}

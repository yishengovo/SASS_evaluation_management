package org.jeecg.modules.survey.client.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 用户端项目表
 * @Author: jeecg-boot
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@TableName("user_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_project对象", description="用户端项目表")
public class UserProject implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private String name;
    /**项目描述*/
    @Excel(name = "项目描述", width = 15)
    @ApiModelProperty(value = "项目描述")
    private String content;
	/**项目负责人*/
	@Excel(name = "项目负责人", width = 15)
    @ApiModelProperty(value = "项目负责人")
    private String leader;
	/**项目类型*/
	@Excel(name = "项目类型", width = 15)
    @ApiModelProperty(value = "项目类型")
    private String type;
	/**0为未发布 1未发布*/
	@Excel(name = "0为未发布 1未发布", width = 15)
    @ApiModelProperty(value = "0为未发布 1未发布")
    private Boolean isPublish;
	/**0为无上下级的评价关系模式 1为有上下级的模式*/
	@Excel(name = "0为无上下级的评价关系模式 1为有上下级的模式", width = 15)
    @ApiModelProperty(value = "0为无上下级的评价关系模式 1为有上下级的模式")
    private Integer evaluationType;
	/**复制模板后的问卷id*/
	@Excel(name = "复制模板后的问卷id", width = 15)
    @ApiModelProperty(value = "复制模板后的问卷id")
    private String surveyCurrentId;
	/**源问卷模板的id*/
	@Excel(name = "源问卷模板的id", width = 15)
    @ApiModelProperty(value = "源问卷模板的id")
    private String surveySrcId;
	/**项目选择答题的用户数*/
	@Excel(name = "项目选择答题的用户数", width = 15)
    @ApiModelProperty(value = "项目选择答题的用户数")
    private Integer selectNumber;
	/**已经提交过的问卷数*/
	@Excel(name = "已经提交过的问卷数", width = 15)
    @ApiModelProperty(value = "已经提交过的问卷数")
    private Integer collectNumber;
	/**存放问卷模板数组的索引*/
	@Excel(name = "存放问卷模板数组的索引", width = 15)
    @ApiModelProperty(value = "存放问卷模板数组的索引")
    private String rowKeys;
	/**存放部门用户的rowkeys*/
	@Excel(name = "存放部门用户的rowkeys", width = 15)
    @ApiModelProperty(value = "存放部门用户的rowkeys")
    private String userRowKeys;
	/**所属租户id*/
	@Excel(name = "所属租户id", width = 15)
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;
}

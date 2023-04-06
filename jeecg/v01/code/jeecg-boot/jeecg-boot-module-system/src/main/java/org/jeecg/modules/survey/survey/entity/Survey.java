package org.jeecg.modules.survey.survey.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.modules.survey.survey.utils.json.CommonStringTypeHandler;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: survey
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName(value="survey", autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="survey对象", description="survey")
public class Survey implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String id;
	/**唯一标识*/
	@Excel(name = "唯一标识", width = 15)
    @ApiModelProperty(value = "唯一标识")
    private java.lang.String fid;
	/**问卷名称*/
	@Excel(name = "问卷名称", width = 15)
    @ApiModelProperty(value = "问卷名称")
    private java.lang.String surName;
    /**问卷类型*/
    @Excel(name = "问卷类型", width = 15)
    @ApiModelProperty(value = "问卷类型")
    private java.lang.String type;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private java.lang.String surContent;
    /**测评问卷报表链接*/
    @Excel(name = "测评问卷报表链接", width = 15)
    @ApiModelProperty(value = "测评问卷报表链接")
    private String reportLink;
	/**共享*/
	@Excel(name = "共享", width = 15)
    @ApiModelProperty(value = "共享")
    private java.lang.Integer isShare;
	/**发布方式*/
	@Excel(name = "发布方式", width = 15)
    @ApiModelProperty(value = "发布方式")
    private java.lang.String filterModel;
	/**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
	/**完成情况*/
	@Excel(name = "完成情况", width = 15)
    @ApiModelProperty(value = "完成情况")
    private java.lang.String completed;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.Integer surStatus;
	/**收集量*/
	@Excel(name = "收集量", width = 15)
    @ApiModelProperty(value = "收集量")
    private java.lang.Integer collectionAmount;
	/**已收集数量*/
	@Excel(name = "已收集数量", width = 15)
    @ApiModelProperty(value = "已收集数量")
    private java.lang.Integer amounted;
	/**目标用户*/
	@Excel(name = "目标用户", width = 15)
    @ApiModelProperty(value = "目标用户")
    private java.lang.String targetUser;
	/**质量控制*/
	@Excel(name = "质量控制", width = 15)
    @ApiModelProperty(value = "质量控制")
    private java.lang.Integer qualityControl;
	/**创建员工*/
	@Excel(name = "创建员工", width = 15)
    @ApiModelProperty(value = "创建员工")
    private java.lang.String empUid;
	/**创建用户*/
	@Excel(name = "创建用户", width = 15)
    @ApiModelProperty(value = "创建用户")
    private java.lang.String userUid;
	/**调研结果*/
	@Excel(name = "调研结果", width = 15)
    @ApiModelProperty(value = "调研结果")
    private java.lang.String surResult;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private java.lang.String jsonContent;
	/**预览json*/
	@Excel(name = "预览json", width = 15)
    @ApiModelProperty(value = "预览json")
    private java.lang.String jsonPreview;
	/**发布json*/
	@Excel(name = "发布json", width = 15)
    @ApiModelProperty(value = "发布json")
    private java.lang.String jsonPublish;
	/**发布时间*/
	@Excel(name = "发布时间", width = 15)
    @ApiModelProperty(value = "发布时间")
    private java.lang.String publishTime;
	/**收集开始时间*/
	@Excel(name = "收集开始时间", width = 15)
    @ApiModelProperty(value = "收集开始时间")
    private java.lang.String surStartDate;
	/**收集结束时间*/
	@Excel(name = "收集结束时间", width = 15)
    @ApiModelProperty(value = "收集结束时间")
    private java.lang.String surEndDate;
	/**未锁定*/
	@Excel(name = "未锁定", width = 15)
    @ApiModelProperty(value = "未锁定")
    private java.lang.Integer isUnlocked;
	/**在线*/
	@Excel(name = "在线", width = 15)
    @ApiModelProperty(value = "在线")
    private java.lang.Integer isOnline;
	/**投票类型*/
	@Excel(name = "投票类型", width = 15)
    @ApiModelProperty(value = "投票类型")
    private java.lang.Integer voteType;
	/**组织*/
	@Excel(name = "组织", width = 15)
    @ApiModelProperty(value = "组织")
    private java.lang.String orgUid;
	/**所属租户*/
	@Excel(name = "所属租户", width = 15)
    @ApiModelProperty(value = "所属租户")
    private java.lang.String tenantUid;
	/**有效开始时间*/
	@Excel(name = "有效开始时间", width = 15)
    @ApiModelProperty(value = "有效开始时间")
    private java.lang.String enableDate;
	/**有效截止时间*/
	@Excel(name = "有效截止时间", width = 15)
    @ApiModelProperty(value = "有效截止时间")
    private java.lang.String disableDate;
	/**删除标记*/
	@Excel(name = "删除标记", width = 15)
    @ApiModelProperty(value = "删除标记")
    private java.lang.Integer dr;
	/**时间戳*/
	@Excel(name = "时间戳", width = 15)
    @ApiModelProperty(value = "时间戳")
    private java.lang.Integer ts;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建人名称*/
	@Excel(name = "创建人名称", width = 15)
    @ApiModelProperty(value = "创建人名称")
    private java.lang.String createName;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新人名称*/
	@Excel(name = "更新人名称", width = 15)
    @ApiModelProperty(value = "更新人名称")
    private java.lang.String updateName;
	/**更新时间*/
	@Excel(name = "更新时间", width = 15)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

//    /**租户id*/
//    @Excel(name = "租户id", width = 15)
//    @ApiModelProperty(value = "租户id")
//    private java.lang.Integer tenantId;

    /**是否为公共的问卷模板  1为公共 0为指定租户*/
    @Excel(name = "是否为公共的问卷模板  1为公共 0为指定租户", width = 15)
    @ApiModelProperty(value = "是否为公共的问卷模板  1为公共 0为指定租户")
    private Boolean isPublic;

    /**是否启用 1启用 0停用*/
    @Excel(name = "是否启用 1启用 0停用", width = 15)
    @ApiModelProperty(value = "是否启用 1启用 0停用")
    private Boolean isUse;

    /**所属租户id*/
    @Excel(name = "所属租户id", width = 15)
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

    @TableField(exist = false)
    //问卷状态  答题状态 0未达题 1答题中  2已完成
    private Integer status;
    @TableField(exist = false)
    //问卷状态  答题状态 0未达题 1答题中  2已完成
    private List<Integer> tenantIdList;
    @TableField(typeHandler = CommonStringTypeHandler.class)
    private List<String> tagRowkeys;

}

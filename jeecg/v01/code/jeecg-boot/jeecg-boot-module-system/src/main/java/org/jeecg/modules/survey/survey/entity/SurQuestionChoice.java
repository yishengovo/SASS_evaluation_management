package org.jeecg.modules.survey.survey.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: sur_question_choice
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("sur_question_choice")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_question_choice对象", description="sur_question_choice")
public class SurQuestionChoice implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**唯一标识*/
	@Excel(name = "唯一标识", width = 15)
    @ApiModelProperty(value = "唯一标识")
    private java.lang.String fid;
    /**选项唯一标识*/
    @Excel(name = "选项唯一标识", width = 15)
    @ApiModelProperty(value = "选项唯一标识")
    private String pid;
	/**关联问题*/
	@Excel(name = "关联问题", width = 15)
    @ApiModelProperty(value = "关联问题")
    private java.lang.String questionUid;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sortIndex;
	/**选项绝对编号*/
	@Excel(name = "选项绝对编号", width = 15)
    @ApiModelProperty(value = "选项绝对编号")
    private java.lang.Integer choiceAbsoluteId;
	/**标题*/
	@Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String content;
	/**是否含文本框*/
	@Excel(name = "是否含文本框", width = 15)
    @ApiModelProperty(value = "是否含文本框")
    private java.lang.Integer isOther;
	/**必填*/
	@Excel(name = "必填", width = 15)
    @ApiModelProperty(value = "必填")
    private java.lang.Integer required;
	/**问题ID*/
	@Excel(name = "问题ID", width = 15)
    @ApiModelProperty(value = "问题ID")
    private java.lang.String questionId;
    @Excel(name = "基础分数", width = 15)
    @ApiModelProperty(value = "基础分数")
    private java.lang.String basicScore;
	/**问卷*/
	@Excel(name = "问卷", width = 15)
    @ApiModelProperty(value = "问卷")
    private java.lang.String surveyUid;
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
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 15)
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

//    /**租户id*/
//    @Excel(name = "租户id", width = 15)
//    @ApiModelProperty(value = "租户id")
//    private java.lang.Integer tenantId;

    /**所属租户id*/
    @Excel(name = "所属租户id", width = 15)
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

}

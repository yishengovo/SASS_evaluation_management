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
import java.util.List;

/**
 * @Description: 问卷问题维度表
 * @Author: jeecg-boot
 * @Date:   2022-08-05
 * @Version: V1.0
 */
@Data
@TableName("sur_question_dimension")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_question_dimension对象", description="问卷问题维度表")
public class SurQuestionDimension implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
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
	/**问卷id*/
	@Excel(name = "问卷id", width = 15)
    @ApiModelProperty(value = "问卷id")
    private String surveyId;
	/**维度名称*/
	@Excel(name = "维度名称", width = 15)
    @ApiModelProperty(value = "维度名称")
    private String name;
	/**该维度的题目索引*/
	@Excel(name = "该维度的题目索引", width = 15)
    @ApiModelProperty(value = "该维度的题目索引")
    private String weight;
    /**维度权重*/
    @Excel(name = "维度权重", width = 15)
    @ApiModelProperty(value = "维度权重")
    private String rowKeys;
    @TableField(exist = false)
    //题目数
    private Integer count;
    @TableField(exist = false)
    //题目
    List<SurQuestion> questionList;

//    /**租户id*/
//    @Excel(name = "租户id", width = 15)
//    @ApiModelProperty(value = "租户id")
//    private java.lang.Integer tenantId;

    /**所属租户id*/
    @Excel(name = "所属租户id", width = 15)
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

}

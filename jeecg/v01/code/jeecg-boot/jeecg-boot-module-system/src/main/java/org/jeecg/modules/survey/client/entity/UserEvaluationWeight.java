package org.jeecg.modules.survey.client.entity;

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
 * @Description: 360度项目评级关系权重表
 * @Author: jeecg-boot
 * @Date:   2022-10-09
 * @Version: V1.0
 */
@Data
@TableName("user_evaluation_weight")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_evaluation_weight对象", description="360度项目评级关系权重表")
public class UserEvaluationWeight implements Serializable {
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
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;
	/**自评的权重*/
	@Excel(name = "自评的权重", width = 15)
    @ApiModelProperty(value = "自评的权重")
    private String selfWeight;
	/**上级的权重*/
	@Excel(name = "上级的权重", width = 15)
    @ApiModelProperty(value = "上级的权重")
    private String superiorWeight;
	/**同事的权重*/
	@Excel(name = "同事的权重", width = 15)
    @ApiModelProperty(value = "同事的权重")
    private String colleagueWeight;
	/**下级的权重*/
	@Excel(name = "下级的权重", width = 15)
    @ApiModelProperty(value = "下级的权重")
    private String subordinateWeight;
	/**其他的权重*/
	@Excel(name = "其他的权重", width = 15)
    @ApiModelProperty(value = "其他的权重")
    private String otherWeight;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private String tenantId;
}

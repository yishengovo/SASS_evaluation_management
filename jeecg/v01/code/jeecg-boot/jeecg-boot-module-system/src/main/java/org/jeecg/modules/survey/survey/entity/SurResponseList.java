package org.jeecg.modules.survey.survey.entity;

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
 * @Description: sur_response_list
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Data
@TableName("sur_response_list")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_response_list对象", description="sur_response_list")
public class SurResponseList implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**唯一标识*/
	@Excel(name = "唯一标识", width = 15)
    @ApiModelProperty(value = "唯一标识")
    private java.lang.String fid;
	/**问卷*/
	@Excel(name = "问卷", width = 15)
    @ApiModelProperty(value = "问卷")
    private java.lang.String surveyUid;
	/**用户*/
	@Excel(name = "用户", width = 15)
    @ApiModelProperty(value = "用户")
    private java.lang.String userUid;
	/**员工*/
	@Excel(name = "员工", width = 15)
    @ApiModelProperty(value = "员工")
    private java.lang.String empUid;
	/**答题时长*/
	@Excel(name = "答题时长", width = 15)
    @ApiModelProperty(value = "答题时长")
    private java.math.BigDecimal timeLength;
	/**答题时长*/
	@Excel(name = "答题时长", width = 15)
    @ApiModelProperty(value = "答题时长")
    private java.lang.String timeLenDesc;
	/**提交时间*/
	@Excel(name = "提交时间", width = 15)
    @ApiModelProperty(value = "提交时间")
    private java.lang.String submitTime;
	/**开始答卷时间*/
	@Excel(name = "开始答卷时间", width = 15)
    @ApiModelProperty(value = "开始答卷时间")
    private java.lang.String startTime;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private java.lang.Integer sortIndex;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.String responseStatus;
	/**IP地址*/
	@Excel(name = "IP地址", width = 15)
    @ApiModelProperty(value = "IP地址")
    private java.lang.String ipAddress;
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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新人名称*/
	@Excel(name = "更新人名称", width = 15)
    @ApiModelProperty(value = "更新人名称")
    private java.lang.String updateName;
	/**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
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

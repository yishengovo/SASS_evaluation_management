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
 * @Description: 用户端填写人每题的答案表
 * @Author: jeecg-boot
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@TableName("user_person_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="user_person_result对象", description="用户端填写人每题的答案表")
public class UserPersonResult implements Serializable {
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
	/**所填问卷的填写人的手机号*/
	@Excel(name = "所填问卷的填写人的手机号", width = 15)
    @ApiModelProperty(value = "所填问卷的填写人的手机号")
    private String phone;
	/**所填问卷id*/
	@Excel(name = "所填问卷id", width = 15)
    @ApiModelProperty(value = "所填问卷id")
    private String surveyId;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;
	/**填写人id*/
	@Excel(name = "填写人id", width = 15)
    @ApiModelProperty(value = "填写人id")
    private String userId;
	/**评价人级别*/
	@Excel(name = "评价人级别", width = 15)
    @ApiModelProperty(value = "评价人级别")
    private Integer userLevel;
	/**被评价人id*/
	@Excel(name = "被评价人id", width = 15)
    @ApiModelProperty(value = "被评价人id")
    private String evaluatedId;
	/**问卷问题*/
	@Excel(name = "问卷问题", width = 15)
    @ApiModelProperty(value = "问卷问题")
    private String questionKey;
	/**问题答案*/
	@Excel(name = "问题答案", width = 15)
    @ApiModelProperty(value = "问题答案")
    private String questionValue;
	/**基础分数*/
	@Excel(name = "基础分数", width = 15)
    @ApiModelProperty(value = "基础分数")
    private Object basicScore;
	/**所属租户*/
	@Excel(name = "所属租户", width = 15)
    @ApiModelProperty(value = "所属租户")
    private String tenantId;
}

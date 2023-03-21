package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecg.modules.survey.survey.utils.json.CommonStringTypeHandler;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 评价关系表
 * @Author: jeecg-boot
 * @Date:   2022-08-05
 * @Version: V1.0
 */
@Data
@TableName(value = "sur_evaluation_relationship",autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_evaluation_relationship对象", description="评价关系表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurEvaluationRelationship implements Serializable {
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
	/**被评价者用户id*/
	@Excel(name = "被评价者用户id", width = 15)
    @ApiModelProperty(value = "被评价者用户id")
    private String userId;
	/**被评价者姓名*/
	@Excel(name = "被评价者姓名", width = 15)
    @ApiModelProperty(value = "被评价者姓名")
    private String userName;
	/**评价类型(0为无上下级、1为有上下级)*/
	@Excel(name = "评价类型(0为无上下级、1为有上下级)", width = 15)
    @ApiModelProperty(value = "评价类型(0为无上下级、1为有上下级)")
    private Integer type;
	/**问卷id*/
	@Excel(name = "问卷id", width = 15)
    @ApiModelProperty(value = "问卷id")
    private String surveyId;
	/**项目id*/
	@Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;
    /**是否为自评*/
    @Excel(name = "是否为自评", width = 15)
    @ApiModelProperty(value = "是否为自评")
    private Boolean isSelf;
    //上级用户id数组
    @TableField(typeHandler = CommonStringTypeHandler.class)
    private List<String> superiorId;
    //同事用户id数组
    @TableField(typeHandler = CommonStringTypeHandler.class)
    private List<String> colleagueId;
    //下级用户id数组
    @TableField(typeHandler = CommonStringTypeHandler.class)
    private List<String> subordinateId;
    //其他用户id数组
    @TableField(typeHandler = CommonStringTypeHandler.class)
    private List<String> otherId;

//    /**租户id*/
//    @Excel(name = "租户id", width = 15)
//    @ApiModelProperty(value = "租户id")
//    private java.lang.Integer tenantId;

    /**所属租户id*/
    @Excel(name = "所属租户id", width = 15)
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

}

package org.jeecg.modules.survey.survey.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.entity
 * @Author: GGB
 * @CreateTime: 2022-11-09  16:01
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("sur_project_enable")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_project_enable对象", description="问卷项目控制表")
public class SurProjectEnable implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**项目id*/
    @ApiModelProperty(value = "项目Id")
    private java.lang.String projectId;
    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
    /**结束时间*/
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
    /**时间控制是否启用*/
    @ApiModelProperty(value = "时间控制是否启用")
    private java.lang.Boolean timeEnable;
    /**创建时间*/
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**租户id*/
    @ApiModelProperty(value = "租户Id")
    private java.lang.String tenantId;
    /*每日开始时间控制*/
    @ApiModelProperty(value = "每日开始时间控制")
    private java.util.Date dayStartTime;
    /*每日结束时间控制*/
    @ApiModelProperty(value = "每日结束时间控制")
    private java.util.Date dayEndTime;
    /*密码*/
    @ApiModelProperty(value = "密码")
    private java.lang.String password;
    /*密码控制，True则启用，False则不启用*/
    @ApiModelProperty(value = "密码控制，True则启用，False则不启用")
    private java.lang.Boolean passwordEnable;
    /*手机号验证码控制，True则启用，False则不启用*/
    @ApiModelProperty(value = "手机号验证码控制，True则启用，False则不启用")
    private java.lang.Boolean phoneCaptchaEnable;
}

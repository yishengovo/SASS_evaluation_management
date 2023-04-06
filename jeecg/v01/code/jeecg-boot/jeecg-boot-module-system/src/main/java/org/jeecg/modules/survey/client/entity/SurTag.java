package org.jeecg.modules.survey.client.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 问卷标签表
 * @Author: jeecg-boot
 * @Date:   2023-04-05
 * @Version: V1.0
 */
@Data
@TableName("sur_tag")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sur_tag对象", description="问卷标签表")
public class SurTag implements Serializable {
    private static final long serialVersionUID = 1L;

    /**标签ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "标签ID")
    private java.lang.String id;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**所属部门编码*/
    @ApiModelProperty(value = "所属部门编码")
    private java.lang.String sysOrgCode;
    /**标签名称*/
    @Excel(name = "标签名称", width = 15)
    @ApiModelProperty(value = "标签名称")
    private java.lang.String tagName;
    /**标签描述*/
    @Excel(name = "标签描述", width = 15)
    @ApiModelProperty(value = "标签描述")
    private java.lang.String description;
    /**标签状态：0=禁用，1=启用*/
    @Excel(name = "标签状态：0=禁用，1=启用", width = 15)
    @ApiModelProperty(value = "标签状态：0=禁用，1=启用")
    private java.lang.Integer status;
    /**标签创建来源*/
    @Excel(name = "标签创建来源", width = 15)
    @ApiModelProperty(value = "标签创建来源")
    private java.lang.String createSource;
}

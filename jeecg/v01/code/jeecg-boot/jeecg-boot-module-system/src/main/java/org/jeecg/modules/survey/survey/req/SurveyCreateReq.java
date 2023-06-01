package org.jeecg.modules.survey.survey.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

//创建模板参数
@Data
public class SurveyCreateReq {
    /**主键*/
    private java.lang.String id;
    /**问卷名称*/
    private java.lang.String surName;
    /**问卷类型*/
    private java.lang.String type;
    /**描述*/
    private java.lang.String surContent;
    /**测评问卷报表链接*/
    private String reportLink;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**预览json*/
    private java.lang.String jsonPreview;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    /**是否为公共的问卷模板  1为公共 0为指定租户*/
    private Boolean isPublic;
    /**是否启用 1启用 0停用*/
    private Boolean isUse;
    // 积分
    private int credit;
    /**所属租户id*/
    private List<Integer> tenantIdList;
}

package org.jeecg.modules.survey.client.dto;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

//问卷模板dto
@Data
public class SurveyDto {
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
    /**试卷json*/
    private java.lang.String jsonPreview;
    /**创建人*/
    private java.lang.String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    /**所属租户id*/
    private String tenantId;
    //问卷状态  答题状态 0未达题 1答题中  2已完成
    private Integer status;
    //题目结果  {"问题一":"不认真","问题2":"积极"}"
    private String result;
    //分数
    private Integer score;
    //问卷结果json
    private List<JSONObject> surveyResults;
}

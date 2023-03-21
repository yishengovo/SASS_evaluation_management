package org.jeecg.modules.survey.client.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurEvaluationUser;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class EvaluationRelationDto {
    private String id;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    //被评价者id
    private String userId;
    //被评价者姓名
    private String userName;
    private Integer type;
    private String surveyId;
    private String projectId;
    //评价者自身
    private SurEvaluationUser self;
    //是否自评
    private Boolean isSelf;
    //评价人数组
    private List<SurEvaluationUser> evaluatedList;
    //上级数组
    private List<SurEvaluationUser> superiorList;
    //同事数组
    private List<SurEvaluationUser> colleagueList;
    //下级数组
    private List<SurEvaluationUser> subordinateList;
}

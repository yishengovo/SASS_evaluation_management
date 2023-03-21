package org.jeecg.modules.survey.survey.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurEvaluationUser;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
//评价关系Dto
public class EvaluationRelationshipDto {
    private String id;
    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    private String sysOrgCode;
    //被评价者id
    private String userId;
    //被评价者姓名
    private String userName;
    private Integer type;
    //是否为自评
    private Boolean isSelf;
    private String surveyId;
    private String projectId;
    //被评价人id
    private List<String> evaluatedName;
    //评价人id
    private List<String> evaluatorName;
    //参与者数组
    private List<SurEvaluationUser> userList;
//    //上级数组
//    private List<SurEvaluationUser> superiorList;
//    //同事数组
//    private List<SurEvaluationUser> colleagueList;
//    //下级数组
//    private List<SurEvaluationUser> subordinateList;
//    //其他数组
//    private List<SurEvaluationUser> otherList;
}

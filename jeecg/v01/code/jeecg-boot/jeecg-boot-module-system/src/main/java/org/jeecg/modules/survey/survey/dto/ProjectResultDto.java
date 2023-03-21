package org.jeecg.modules.survey.survey.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class ProjectResultDto {
    //项目id
    private String id;
    //项目名称
    private String name;
    //问卷题目json
    private String surveyJson;
    //最后修改时间
    private Date lastEditTime;
    //收集人数
    private Integer collectNum;
    //问卷结果json
    private List<JSONObject> surveyResults;
}

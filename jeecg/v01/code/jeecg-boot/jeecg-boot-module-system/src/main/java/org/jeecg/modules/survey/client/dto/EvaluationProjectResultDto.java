package org.jeecg.modules.survey.client.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

//测评项目 数据大屏返回的dto
@Data
public class EvaluationProjectResultDto {
    //项目id
    private String id;
    //项目名称
    private String name;
    //项目类型
    private String type;
    //问卷
    private List<SurveyDto> surveyList;
    //最后修改时间
    private Date lastEditTime;
    //收集人数
    private Integer collectNum;

}

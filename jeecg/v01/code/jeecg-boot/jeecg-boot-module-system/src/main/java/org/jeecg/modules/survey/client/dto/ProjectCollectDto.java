package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.Survey;

import java.util.List;

//项目统计分析dto
@Data
public class ProjectCollectDto {
    private String projectId;
    private String projectName;
    private String type;
    //项目收集数量
    private Integer collectNumber;
    //问卷列表
    private List<Survey> surveyList;
    private List<Object> data;
}

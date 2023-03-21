package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.*;

import java.util.List;

@Data
//测评类项目dto
public class EvaluationProjectDto {
    //项目
    private SurProject project;
    //excelUrl
    private String url;
    //测评项目中的问卷列表
    private List<Survey> surveyList;
}

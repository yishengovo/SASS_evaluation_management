package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurProject;
import org.jeecg.modules.survey.survey.entity.SurProjectEnable;
import org.jeecg.modules.survey.survey.entity.SurSurveyProject;

//项目SurveyProjectDto
@Data
public class SurveyProjectDto {
    private SurSurveyProject survey;
    private String url;
    //项目控制信息
    private SurProjectEnable control;
}

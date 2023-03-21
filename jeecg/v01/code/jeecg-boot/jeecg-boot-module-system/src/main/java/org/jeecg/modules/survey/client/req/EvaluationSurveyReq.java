package org.jeecg.modules.survey.client.req;

import lombok.Data;

@Data
public class EvaluationSurveyReq {
    //项目id
    private String projectId;
    //问卷id
    private String surveyId;
}

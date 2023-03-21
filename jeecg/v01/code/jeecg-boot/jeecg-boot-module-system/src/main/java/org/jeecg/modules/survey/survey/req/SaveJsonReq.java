package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SaveJsonReq", description = "保存问卷json的请求类")
public class SaveJsonReq {
    //问卷的id
    private String surveyId;
    //预览json
    private String jsonPreview;
    //问题和选项
    private List<SurveyQuestionReq> question;
}

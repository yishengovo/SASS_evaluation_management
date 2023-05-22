package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SurveyMarketSaveReq", description = "问卷保存参数")
public class SurveyMarketSaveReq {
    // 问卷名称
    private String name;
    // 类型
    private String type;
    //问卷id
    private String surveyId;
    //预览json
    private String jsonPreview;
    //问题和选项
    private List<SurveyQuestionReq> question;
    // 问卷描述
    private String content;
}

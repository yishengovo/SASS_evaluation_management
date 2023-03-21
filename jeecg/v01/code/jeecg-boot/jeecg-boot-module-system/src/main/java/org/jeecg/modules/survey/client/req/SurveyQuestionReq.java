package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SurveyQuestionReq", description = "问卷问题参数")
public class SurveyQuestionReq {
    //问题名称
    private String name;
    //题目文本
    private String title;
    //矩阵问题名称
    private List<String> names;
    //问题类型
    private String type;
    //问题对应的选项
    private List<String> choices;
}

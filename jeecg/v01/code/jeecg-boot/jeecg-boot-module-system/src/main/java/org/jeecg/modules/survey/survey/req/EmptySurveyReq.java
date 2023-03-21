package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "EmptySurveyReq", description = "新建空白问卷参数类")
public class EmptySurveyReq {
    //项目id
    private String id;
    //    "问卷类型（调查、测评、360度）"
    private String type;
    /**
     * 问卷名称
     */
    private String surName;
    /**
     * 描述
     */

    private String surContent;
    /**
     * 预览json
     */

    private String jsonPreview;
    /**
     * 源问卷id
     */

    private String srcId;
}

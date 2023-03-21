package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "CreateProjectReq", description = "创建项目的请求类")
public class CreateEvaluationProjectReq {

    //项目id
    private String id;
    /**
     * 项目名称
     */

    private String name;
    /**
     * 项目描述
     */
    private String content;
    /**
     * 项目类型
     */
    private String type;
    //问卷id
    private List<String> survey;
}

package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "SaveLibJsonReq", description = "保存问卷库中问卷的json的请求类")
public class SaveLibJsonReq {
    //问卷库中问卷的id
    private String id;
    //预览json
    private String json;
}

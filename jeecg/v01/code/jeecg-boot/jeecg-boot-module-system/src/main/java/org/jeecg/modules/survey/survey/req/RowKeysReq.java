package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel( value = "RowKeysReq", description = "根据项目id保存问卷rowKeys请求参数" )
public class RowKeysReq {
    //项目id
    private String id;
    //问卷id
    private String surveyId;
    //问卷索引
    private List<String> rowKeys;
}

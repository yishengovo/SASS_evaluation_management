package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel( value = "ChoiceByQuestionIdReq", description = "根据项目ID查询收集详情" )
public class CollectReq extends PageReq {
    //项目id
    private String id;
}

package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel( value = "EvaluationRelSetReq", description = "设置有上下级的评价关系的参数类" )
public class EvaluationRelSetReq {
    //主键
    private String id;
    //项目id
    private String projectId;
    //被评价人id
    private String evaluator;
    //是否开启自评
    private Boolean isSelf;
    //评价人id数组
    private List<String> evaluated;
    //上级用户id数组
    private List<String> superior;
    //同事用户id数组
    private List<String> colleague;
    //下级用户id数组
    private List<String> subordinate;
}

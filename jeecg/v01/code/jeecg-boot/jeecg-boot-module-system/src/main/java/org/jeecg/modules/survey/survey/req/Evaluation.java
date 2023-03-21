package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "Evaluation", description = "评价体系的请求参数类")
public class Evaluation {
    //主键
    private String id;
    //项目id
    private String projectId;
    //被评价人id
    private String evaluatedName;
    //评价人id
    private String evaluatorName;
    //是否开启自评
    private Boolean isSelf;
    //上级用户id数组
    private String superiorName;
    //同事用户id数组
    private String colleagueName;
    //下级用户id数组
    private String subordinateName;
    //其他用户id数组
    private String otherName;
}

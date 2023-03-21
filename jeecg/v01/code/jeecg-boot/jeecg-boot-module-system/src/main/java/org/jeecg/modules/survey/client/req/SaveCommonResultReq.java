package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurUser;

import java.util.Map;

/**
 * @author 一笙
 * @date 2022/6/24 11:18
 * @Version 1.0
 */
@Data
@ApiModel(value = "SaveCommonResultReq", description = "测评和调查问卷项目保存用户问卷答案的json的请求类")
public class SaveCommonResultReq {
    //项目id
    private String projectId;
    //问卷id
    private String surveyId;
    //问卷类型
    private String type;
    //问卷状态
    private Integer status;
    //答案的json
    private String answer;
    //填写人
    private SurUser user;
    //被评价者id
    private String evaluator;
    //问卷的答案和问题的map集合
    private Map<String, Object> result;
}

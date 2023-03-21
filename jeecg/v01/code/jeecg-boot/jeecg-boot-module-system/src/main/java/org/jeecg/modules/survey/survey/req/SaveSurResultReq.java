package org.jeecg.modules.survey.survey.req;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Map;

/**
 * @author 一笙
 * @date 2022/6/24 11:18
 * @Version 1.0
 */
@Data
@ApiModel(value = "SaveSurResultReq", description = "保存用户问卷答案的json的请求类")
public class SaveSurResultReq {
    //问卷id
    private String surveyId;
    //被评价人id
    private String evaluatedId;
    //项目id
    private String projectId;
    //答案的json
    private String answer;
    //填写问卷的用户
    private JSONObject user;
    //问卷的答案和问题的map集合
    private Map<String, Object> result;
}

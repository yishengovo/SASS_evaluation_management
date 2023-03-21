package org.jeecg.modules.survey.survey.req;

import lombok.Data;

import java.util.List;

//替换用户问卷json的请求参数
@Data
public class ReplaceJsonReq {
    //项目id
    private String projectId;
    //问卷id
    private String surveyId;
    //需要替换的用户姓名list
    private List<String> userList;
}

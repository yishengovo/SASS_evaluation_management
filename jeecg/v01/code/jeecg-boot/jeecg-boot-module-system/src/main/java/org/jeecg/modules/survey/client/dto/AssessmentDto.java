package org.jeecg.modules.survey.client.dto;

import lombok.Data;

@Data
//360度收集详情dto
public class AssessmentDto {
    //项目id
    private String id;
    //问卷id
    private String surveyId;
    //总人数
    private Integer selectNumber;
    //已收集数量
    private Integer collectNumber;
    //完成率
    private String rate;
    //用户列表
    private AssessmentUserPage users;
}

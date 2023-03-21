package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

@Data
//项目分析 收集详情
public class CollectDto {
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
    private UserPage users;
}

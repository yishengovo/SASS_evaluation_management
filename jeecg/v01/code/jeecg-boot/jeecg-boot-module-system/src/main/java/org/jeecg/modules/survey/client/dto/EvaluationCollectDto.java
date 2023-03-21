package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.dto.UserPage;

@Data
//测评类项目dto
public class EvaluationCollectDto {
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

package org.jeecg.modules.survey.client.dto;

import lombok.Data;
import org.jeecg.modules.survey.survey.entity.Survey;

import java.util.List;

@Data
//测评类项目dto
public class EvaluationCollectProjectDto {
    //项目id
    private String id;
    //项目名称
    private String name;
    //项目类型
    private String type;
    //总人数
    private Integer selectNumber;
    //已收集数量
    private Integer collectNumber;
    //完成率
    private String rate;
    //问卷列表
    private List<Survey> surveyList;
    //用户列表
    private EvaluationUserPageDto userPage;
}

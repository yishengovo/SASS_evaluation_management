package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurQuestionDimension;

import java.util.List;

@Data
@ApiModel(value = "DimensionSetReq", description = "设置维度的请求参数类")
public class DimensionSetReq {
    private List<SurQuestionDimension> list;
}

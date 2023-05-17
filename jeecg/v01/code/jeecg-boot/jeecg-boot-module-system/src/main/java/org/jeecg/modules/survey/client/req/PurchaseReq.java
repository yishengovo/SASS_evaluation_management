package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "PurchaseReq", description = "购买问卷的信息和租户的信息")
public class PurchaseReq {
    // 问卷模板ID
    private String surveyId;
}

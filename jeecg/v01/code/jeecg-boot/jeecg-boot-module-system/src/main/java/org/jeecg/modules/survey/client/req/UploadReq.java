package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "UploadReq", description = "上传问卷模板")
public class UploadReq {
    // 用户问卷模板ID
    private String surveyProjectId;
    //用户问卷模板积分（要卖多少积分）
    private int credit;
    //上传问卷扣积分
    private int upCredit;

}

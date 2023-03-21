package org.jeecg.modules.survey.client.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

//360userPage
@Data
public class AssessmentUserPage {
    private Long total;
    private Long size;
    //被评价者数组
    private List<JSONObject> user;
}

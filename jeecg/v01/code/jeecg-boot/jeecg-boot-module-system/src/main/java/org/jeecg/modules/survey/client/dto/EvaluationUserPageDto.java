package org.jeecg.modules.survey.client.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

//测评项目用户page dto
@Data
public class EvaluationUserPageDto {
    private Long total;
    private Long size;
    private List<JSONObject> user;
}

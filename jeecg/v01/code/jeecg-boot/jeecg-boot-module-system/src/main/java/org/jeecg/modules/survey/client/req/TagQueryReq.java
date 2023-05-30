package org.jeecg.modules.survey.client.req;

import lombok.Data;

/**
 * @author QiaoQi
 * @version 1.0
 */

@Data
public class TagQueryReq extends PageReq{
    private String tagName;
    private String description;
    private String status;
}
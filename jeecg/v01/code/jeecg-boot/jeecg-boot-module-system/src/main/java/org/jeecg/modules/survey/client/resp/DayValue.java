package org.jeecg.modules.survey.client.resp;

import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.resp
 * @Author: GGB
 * @CreateTime: 2022-12-09  11:50
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class DayValue {
    Date date;
    Integer value;
}

package org.jeecg.modules.survey.client.resp;

import lombok.Data;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.resp
 * @Author: GGB
 * @CreateTime: 2022-12-09  12:05
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MonthValue {
    /*月份*/
    String month;
    /*该月数值*/
    Integer value;
}

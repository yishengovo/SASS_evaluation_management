package org.jeecg.modules.survey.client.resp;

import lombok.Data;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.resp
 * @Author: GGB
 * @CreateTime: 2022-12-09  11:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class Leaderboard {
    /*租户公司名*/
    String tenantName;
    /*积分数*/
    Integer integral;
}

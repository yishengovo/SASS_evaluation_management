package org.jeecg.modules.survey.client.resp;

import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.resp
 * @Author: GGB
 * @CreateTime: 2023-01-06  15:53
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class IntegralModel {
    /*用户id*/
    private String userId;
    /*用户名*/
    private String userName;
    /*租户名*/
    private String tenantName;
    /*已充值积分*/
    private Integer topUpIntegraled;
    /*积分余额*/
    private Integer integral;
    /*上次充值时间*/
    private Date lastTopUp;
}

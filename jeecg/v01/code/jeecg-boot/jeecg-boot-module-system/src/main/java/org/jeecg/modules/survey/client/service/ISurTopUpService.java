package org.jeecg.modules.survey.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.client.entity.SurTopUp;

public interface ISurTopUpService extends IService<SurTopUp> {
    Boolean addTopUp(Integer integral);

    Integer getTopUp();

    SurTopUp getSurTopUp();

}

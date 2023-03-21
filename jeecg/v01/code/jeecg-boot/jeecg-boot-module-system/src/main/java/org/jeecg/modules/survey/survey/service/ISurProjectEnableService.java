package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurProjectEnable;

public interface ISurProjectEnableService extends IService<SurProjectEnable> {
    SurProjectEnable getSurProjectEnable(String projectId);

    void deleteProjectEnable(String projectId);

    Boolean saveProjectEnable(SurProjectEnable surProjectEnable);
}

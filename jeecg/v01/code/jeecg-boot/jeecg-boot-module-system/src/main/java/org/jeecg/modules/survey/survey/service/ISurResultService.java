package org.jeecg.modules.survey.survey.service;

import org.jeecg.modules.survey.survey.entity.SurResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.req.SaveSurResultReq;

/**
 * @Description: sur_result
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface ISurResultService extends IService<SurResult> {
    //保存用户填写的问卷答案
    Boolean saveSurResult(SaveSurResultReq req);
}

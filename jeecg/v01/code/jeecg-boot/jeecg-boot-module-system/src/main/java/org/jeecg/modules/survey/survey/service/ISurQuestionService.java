package org.jeecg.modules.survey.survey.service;

import org.jeecg.modules.survey.survey.entity.SurQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.req.QuestionSetPidReq;

/**
 * @Description: sur_question
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface ISurQuestionService extends IService<SurQuestion> {
    //给问卷问题设置唯一编号
    Boolean setQuestionPid(QuestionSetPidReq req);
}

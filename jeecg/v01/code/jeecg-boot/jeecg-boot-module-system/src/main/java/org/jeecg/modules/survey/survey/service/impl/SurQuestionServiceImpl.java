package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.survey.survey.entity.SurQuestion;
import org.jeecg.modules.survey.survey.mapper.SurQuestionMapper;
import org.jeecg.modules.survey.survey.req.QuestionSetPidReq;
import org.jeecg.modules.survey.survey.service.ISurQuestionService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: sur_question
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class SurQuestionServiceImpl extends ServiceImpl<SurQuestionMapper, SurQuestion> implements ISurQuestionService {

    @Override
    public Boolean setQuestionPid(QuestionSetPidReq req) {
        //查询question对象
        SurQuestion question = this.getOne(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()).eq(SurQuestion::getId, req.getQuestionId()));
        if (question == null) {
            return false;
        }
        question.setPid(req.getPid());
        return this.updateById(question);
    }
}

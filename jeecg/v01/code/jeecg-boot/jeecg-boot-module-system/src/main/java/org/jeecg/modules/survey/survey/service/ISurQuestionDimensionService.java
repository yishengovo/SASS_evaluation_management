package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurQuestionDimension;

import java.util.List;

/**
 * @Description: 问卷问题维度表
 * @Author: jeecg-boot
 * @Date:   2022-08-05
 * @Version: V1.0
 */
public interface ISurQuestionDimensionService extends IService<SurQuestionDimension> {

    //根据维度id返回维度绑定的问题
    List<Object> queryBindQuestion(String surveyId);

}

package org.jeecg.modules.survey.survey.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.dto.BindQuestionDto;
import org.jeecg.modules.survey.survey.entity.SurQuestion;
import org.jeecg.modules.survey.survey.entity.SurQuestionDimension;
import org.jeecg.modules.survey.survey.mapper.SurQuestionDimensionMapper;
import org.jeecg.modules.survey.survey.service.ISurQuestionDimensionService;
import org.jeecg.modules.survey.survey.service.ISurQuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 问卷问题维度表
 * @Author: jeecg-boot
 * @Date:   2022-08-05
 * @Version: V1.0
 */
@Service
public class SurQuestionDimensionServiceImpl extends ServiceImpl<SurQuestionDimensionMapper, SurQuestionDimension> implements ISurQuestionDimensionService {

    @Autowired
    private ISurQuestionService questionService;
    @Override
    public List<Object> queryBindQuestion(String surveyId) {
        //查询该问卷的维度
        List<SurQuestionDimension> dimensionList = list(new LambdaQueryWrapper<SurQuestionDimension>().eq(SurQuestionDimension::getSurveyId, surveyId));
        //查询该问卷所有的问题
        List<SurQuestion> questionList = questionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, surveyId));
        //获取未绑定的问题
        List<SurQuestion> noBindQuestionList = getNoBindQuestionList(questionList, dimensionList);
        List<Object> res = new ArrayList<>();
        //判断有没有没有绑定题目的维度
        if (!judgeDimension(dimensionList)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",null);
            jsonObject.put("questionList",noBindQuestionList);
            res.add(jsonObject);
        }
        //查询每个维度的问题数组
        dimensionList.forEach(surQuestionDimension -> {
            BindQuestionDto result = new BindQuestionDto();
            List<SurQuestion> bindList = questionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, surveyId).eq(SurQuestion::getDimensionId, surQuestionDimension.getId()));
            BeanUtils.copyProperties(surQuestionDimension,result);
            //如果没有题目
            if (bindList.isEmpty()){
                result.setName(null);
                //没有绑定的题目数组
                result.setQuestionList(noBindQuestionList);
            }
            else {
                result.setQuestionList(bindList);
            }
            if (surQuestionDimension.getRowKeys() == null){
                result.setRowKeys(new ArrayList<>().toString());
            }
            res.add(result);
        });
        return res;
    }

    //判断维度列表中是否有没有绑定过问题的维度
    private boolean judgeDimension(List<SurQuestionDimension> dimensionList){
        List<SurQuestionDimension> noBindQuestionList = dimensionList.stream().filter(surQuestionDimension -> surQuestionDimension.getRowKeys() == null || surQuestionDimension.getRowKeys().equals("[]")).collect(Collectors.toList());
        return noBindQuestionList.size() > 0;
    }
    //获取没有绑定维度的题目
    private List<SurQuestion> getNoBindQuestionList(List<SurQuestion> questionList, List<SurQuestionDimension> dimensionList){
        List<SurQuestion> bindQuestionList = new ArrayList<>();
        //遍历维度列表 查询出绑定的题目
        dimensionList.forEach(surQuestionDimension -> {
            List<SurQuestion> surQuestionList = questionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getDimensionId, surQuestionDimension.getId()));
            bindQuestionList.addAll(surQuestionList);
        });
        //截取没有绑定的题目数组
        return questionList.stream().filter(item -> !bindQuestionList.contains(item)).collect(Collectors.toList());
    }
}

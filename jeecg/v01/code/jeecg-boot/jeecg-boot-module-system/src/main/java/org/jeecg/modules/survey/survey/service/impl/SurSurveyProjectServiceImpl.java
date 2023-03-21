package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.SurSurveyProjectMapper;
import org.jeecg.modules.survey.survey.req.EmptySurveyReq;
import org.jeecg.modules.survey.survey.req.SaveJsonReq;
import org.jeecg.modules.survey.survey.req.SurveyQuestionReq;
import org.jeecg.modules.survey.survey.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 存放项目复制出来的问卷表
 * @Author: jeecg-boot
 * @Date:   2022-08-17
 * @Version: V1.0
 */
@Service
public class SurSurveyProjectServiceImpl extends ServiceImpl<SurSurveyProjectMapper, SurSurveyProject> implements ISurSurveyProjectService {

    @Autowired
    private ISurProjectService surProjectService;
    @Autowired
    private SurProjectSurveyService surProjectSurveyService;
    @Autowired
    private ISurQuestionProjectService questionService;
    @Autowired
    private ISurQuestionChoiceProjectService questionChoiceService;
    @Override
    public Boolean createSurvey(EmptySurveyReq req) {
        SurSurveyProject surSurveyProject = new SurSurveyProject();
        BeanUtils.copyProperties(req, surSurveyProject);
        surSurveyProject.setId(null);
        //保存主表信息
        this.save(surSurveyProject);
        //保存子表信息
        surProjectService.update(new LambdaUpdateWrapper<SurProject>().eq(SurProject::getId, req.getId()).set(SurProject::getSurveyCurrentId,surSurveyProject.getId()));
        //保存关系表
        //判断关系是否存在
        SurProjectSurvey one = surProjectSurveyService.getOne(new LambdaQueryWrapper<SurProjectSurvey>().eq(SurProjectSurvey::getProjectId, req.getId()));
        if (one != null) {
            one.setSurveyId(surSurveyProject.getId());
            surProjectSurveyService.updateById(one);
        } else {
            SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
            surProjectSurvey.setSurveyId(surSurveyProject.getId());
            surProjectSurvey.setProjectId(req.getId());
            surProjectSurveyService.save(surProjectSurvey);
        }
        return true;
    }

    @Override
    public SurSurveyProject getSurveyByProjectId(String projectId) {
        //根据项目id查询项目
        SurProject one = surProjectService.getOne(new LambdaQueryWrapper<SurProject>().eq(SurProject::getId, projectId));
        return this.getOne(new LambdaQueryWrapper<SurSurveyProject>().eq(SurSurveyProject::getId, one.getSurveyCurrentId()));
    }

    @Override
    public Boolean saveJsonPreview(SaveJsonReq req) {
        //        surveyMapper.update(null,new LambdaUpdateWrapper<Survey>().eq(Survey::getId,req.getId()).set(Survey::getJsonPreview,req.getJsonPreview()));
        //先根据问卷id查出问卷
        SurSurveyProject survey = this.getById(req.getSurveyId());
        if (req.getJsonPreview() != null) {
            survey.setJsonPreview(req.getJsonPreview());
        }
        this.updateById(survey);
        //如果有问题的话 先删除原来的问题 再去保存新的问题
        List<SurQuestionProject> questions = questionService.list(new LambdaQueryWrapper<SurQuestionProject>().eq(SurQuestionProject::getSurveyUid, req.getSurveyId()));
        if (questions != null) {
            questionService.removeBatchByIds(questions);
        }
        List<SurQuestionChoiceProject> questionChoices = questionChoiceService.list(new LambdaQueryWrapper<SurQuestionChoiceProject>().eq(SurQuestionChoiceProject::getSurveyUid, req.getSurveyId()));
        if (questionChoices != null) {
            questionChoiceService.removeBatchByIds(questionChoices);
        }
        //将参数中的问题保存到 question和choices表中
        List<SurveyQuestionReq> questionList = req.getQuestion();
        SurQuestionProject surQuestion = null;
        for (SurveyQuestionReq question : questionList) {
            //判断是单个问题还是矩阵里的问题
            //如果是单个问题
            if (question.getName() != null && question.getNames().size() == 0) {
                List<Integer> score = new ArrayList<>();
                surQuestion = new SurQuestionProject();
                List<String> choices = question.getChoices();
                //将问题插入到question表中
                surQuestion.setSurveyUid(survey.getId().toString());
                surQuestion.setContent(question.getName());
                surQuestion.setTypeId(question.getType());
                questionService.save(surQuestion);
                //遍历choices 将每个答案存到数据库中
                SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
                surQuestionChoice.setQuestionId(surQuestion.getId());
                surQuestionChoice.setSurveyUid(survey.getId().toString());
                surQuestionChoice.setContent(choices.toString());
                for (int i = 0; i < choices.size(); i++) {
                    score.add(0);
                }
                surQuestionChoice.setBasicScore(score.toString());
                questionChoiceService.save(surQuestionChoice);
            }
            //如果是矩阵问题
            if (question.getNames() != null) {
                List<String> names = question.getNames();
                for (String name : names) {
                    List<Integer> score = new ArrayList<>();
                    surQuestion = new SurQuestionProject();
                    List<String> choices = question.getChoices();
                    //将问题插入到question表中
                    surQuestion.setSurveyUid(survey.getId().toString());
                    surQuestion.setContent(name);
                    surQuestion.setTypeId(question.getType());
                    questionService.save(surQuestion);
                    //遍历choices 将每个答案存到数据库中
                    SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
                    surQuestionChoice.setQuestionId(surQuestion.getId());
                    surQuestionChoice.setSurveyUid(survey.getId().toString());
                    surQuestionChoice.setContent(choices.toString());
                    for (int i = 0; i < choices.size(); i++) {
                        score.add(0);
                    }
                    surQuestionChoice.setBasicScore(score.toString());
                    questionChoiceService.save(surQuestionChoice);
                }
            }

        }
        return true;
    }

    @Override
    public List<SurveyDto> getQuestionAndChoice(String id) {
        //根据问卷id查询问卷数组
        List<SurQuestionProject> questionList = questionService.list(new LambdaQueryWrapper<SurQuestionProject>().eq(SurQuestionProject::getSurveyUid, id));
        List<SurveyDto> result = new ArrayList<>();
        for (SurQuestionProject surQuestion : questionList) {
            List<ChoiceDto> choiceDtoList = new ArrayList<>();
            SurveyDto surveyDto = new SurveyDto();
            BeanUtils.copyProperties(surQuestion, surveyDto);
            surveyDto.setQuestionId(surQuestion.getId());
            //根据问题id查询出选项
            SurQuestionChoiceProject surChoice = questionChoiceService.getOne(new LambdaQueryWrapper<SurQuestionChoiceProject>().eq(SurQuestionChoiceProject::getQuestionId, surQuestion.getId()).eq(SurQuestionChoiceProject::getSurveyUid, id));
            //获取选项数组
            String content = surChoice.getContent();
            List<String> choiceList = Arrays.asList(content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
            //获取答案数组
            String basicScore = surChoice.getBasicScore();
            //如果选项为空
            if (choiceList.size() == 1) {
                //空数组
                surveyDto.setChoice(new ArrayList<>());
                result.add(surveyDto);
            }
            //如果选项不为空
            else {
                if (basicScore == null) {
                    //如果答案为空  默认赋分为0
                    for (String s : choiceList) {
                        ChoiceDto choiceDto = new ChoiceDto();
                        BeanUtils.copyProperties(surChoice, choiceDto);
                        choiceDto.setChoice(s);
                        choiceDto.setBasicScore(0);
                        choiceDtoList.add(choiceDto);
                    }
                    surveyDto.setChoice(choiceDtoList);
                    result.add(surveyDto);
                } else {
                    List<String> scoreList = Arrays.asList(basicScore.substring(1, basicScore.length() - 1).replaceAll("\\s", "").split(","));
                    //将选项数组拆分成单个的
                    if (choiceList.size() != 0) {
                        for (int i = 0; i < choiceList.size(); i++) {
                            if (scoreList.get(i) != null) {
                                ChoiceDto choiceDto = new ChoiceDto();
                                BeanUtils.copyProperties(surChoice, choiceDto);
                                choiceDto.setChoice(choiceList.get(i));
                                choiceDto.setBasicScore(scoreList.get(i));
                                choiceDtoList.add(choiceDto);
                            }
                        }
                        surveyDto.setChoice(choiceDtoList);
                        result.add(surveyDto);
                    }
                }
            }
        }
        return result;
    }

}

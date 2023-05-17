package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.SurQuestion;
import org.jeecg.modules.survey.survey.entity.SurQuestionChoice;
import org.jeecg.modules.survey.survey.entity.SurSurveyTenant;
import org.jeecg.modules.survey.survey.entity.Survey;
import org.jeecg.modules.survey.survey.mapper.SurveyMapper;
import org.jeecg.modules.survey.survey.req.SaveJsonReq;
import org.jeecg.modules.survey.survey.req.SurveyCreateReq;
import org.jeecg.modules.survey.survey.req.SurveyQuestionReq;
import org.jeecg.modules.survey.survey.service.ISurQuestionChoiceService;
import org.jeecg.modules.survey.survey.service.ISurQuestionService;
import org.jeecg.modules.survey.survey.service.ISurSurveyTenantService;
import org.jeecg.modules.survey.survey.service.ISurveyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: survey @Author: jeecg-boot @Date: 2022-06-14 @Version: V1.0
 */
@Service
public class SurveyServiceImpl extends ServiceImpl<SurveyMapper, Survey> implements ISurveyService {

  @Autowired private SurveyMapper surveyMapper;
  @Autowired private ISurQuestionService questionService;
  @Autowired private ISurQuestionChoiceService questionChoiceService;
  @Autowired private ISurSurveyTenantService surveyTenantService;

  @Override
  public Boolean createSurvey(SurveyCreateReq req) {
    Survey survey = new Survey();
    BeanUtils.copyProperties(req, survey);
    survey.setId(null);
    survey.setTenantId(null);
    survey.setIsPublic(true);
    survey.setIsUse(req.getIsUse());
    save(survey);
    // 如果不是公共的问卷 创建问卷租户关系
    if (req.getTenantIdList() != null && !req.getTenantIdList().isEmpty()) {
      survey.setIsPublic(false);
      updateById(survey);
      req.getTenantIdList()
          .forEach(
              tenantId -> {
                SurSurveyTenant surveyTenant = new SurSurveyTenant();
                surveyTenant.setSurveyId(survey.getId());
                surveyTenant.setTenantId(tenantId);
                surveyTenantService.save(surveyTenant);
              });
    }
    return true;
  }

  @Override
  @Transactional
  public Boolean saveJsonPreview(SaveJsonReq req) {
    //        surveyMapper.update(null,new
    // LambdaUpdateWrapper<Survey>().eq(Survey::getId,req.getId()).set(Survey::getJsonPreview,req.getJsonPreview()));
    // 先根据问卷id查出问卷
    Survey survey = getById(req.getSurveyId());
    if (req.getJsonPreview() != null) {
      survey.setJsonPreview(req.getJsonPreview());
    }
    updateById(survey);
    // 如果有问题的话 先删除原来的问题 再去保存新的问题
    List<SurQuestion> questions =
        questionService.list(
            new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()));
    if (questions != null) {
      questionService.removeBatchByIds(questions);
    }
    List<SurQuestionChoice> questionChoices =
        questionChoiceService.list(
            new LambdaQueryWrapper<SurQuestionChoice>()
                .eq(SurQuestionChoice::getSurveyUid, req.getSurveyId()));
    if (questionChoices != null) {
      questionChoiceService.removeBatchByIds(questionChoices);
    }
    // 将参数中的问题保存到 question和choices表中
    List<SurveyQuestionReq> questionList = req.getQuestion();
    SurQuestion surQuestion = null;
    for (SurveyQuestionReq question : questionList) {
      // 判断是单个问题还是矩阵里的问题
      // 如果是单个问题
      if (question.getName() != null && question.getNames().isEmpty()) {
        List<Integer> score = new ArrayList<>();
        surQuestion = new SurQuestion();
        List<String> choices = question.getChoices();
        // 将问题插入到question表中
        surQuestion.setSurveyUid(survey.getId().toString());
        surQuestion.setContent(question.getName());
        surQuestion.setIsParent(false);
        surQuestion.setTypeId(question.getType());
        surQuestion.setTitle(question.getTitle());
        questionService.save(surQuestion);
        // 遍历choices 将每个答案存到数据库中
        SurQuestionChoice surQuestionChoice = new SurQuestionChoice();
        surQuestionChoice.setQuestionId(surQuestion.getId());
        surQuestionChoice.setSurveyUid(survey.getId().toString());
        surQuestionChoice.setContent(choices.toString());
        for (int i = 0; i < choices.size(); i++) {
          score.add(0);
        }
        surQuestionChoice.setBasicScore(score.toString());
        questionChoiceService.save(surQuestionChoice);
      }
      // 如果是矩阵问题
      if (!question.getNames().isEmpty()) {
        List<String> names = question.getNames();
        // 父问题
        SurQuestion parentQuestion = new SurQuestion();
        parentQuestion.setSurveyUid(survey.getId());
        parentQuestion.setContent(question.getName());
        parentQuestion.setTitle(question.getTitle());
        parentQuestion.setTypeId(question.getType());
        parentQuestion.setIsParent(true);
        questionService.save(parentQuestion);
        for (String name : names) {
          List<Integer> score = new ArrayList<>();
          surQuestion = new SurQuestion();
          List<String> choices = question.getChoices();
          // 将问题插入到question表中
          surQuestion.setSurveyUid(survey.getId().toString());
          surQuestion.setContent(name);
          surQuestion.setTypeId(question.getType());
          surQuestion.setTitle(question.getTitle());
          surQuestion.setIsParent(false);
          surQuestion.setParentId(parentQuestion.getId());
          surQuestion.setParentContent(parentQuestion.getContent());
          questionService.save(surQuestion);
          // 遍历choices 将每个答案存到数据库中
          SurQuestionChoice surQuestionChoice = new SurQuestionChoice();
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
  @Transactional
  public List<SurveyDto> getQuestionAndChoice(String id) {
    // 根据问卷id查询问卷数组
    List<SurQuestion> questionList =
        questionService.list(
            new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, id));
    List<SurveyDto> result = new ArrayList<>();
    for (SurQuestion surQuestion : questionList) {
      List<ChoiceDto> choiceDtoList = new ArrayList<>();
      SurveyDto surveyDto = new SurveyDto();
      BeanUtils.copyProperties(surQuestion, surveyDto);
      surveyDto.setQuestionId(surQuestion.getId());
      // 根据问题id查询出选项
      SurQuestionChoice surChoice =
          questionChoiceService.getOne(
              new LambdaQueryWrapper<SurQuestionChoice>()
                  .eq(SurQuestionChoice::getQuestionId, surQuestion.getId())
                  .eq(SurQuestionChoice::getSurveyUid, id));
      // 获取选项数组
      String content = surChoice.getContent();
      List<String> choiceList =
          Arrays.asList(
              content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
      // 获取答案数组
      String basicScore = surChoice.getBasicScore();
      // 如果选项为空
      if (choiceList.size() == 1) {
        // 空数组
        surveyDto.setChoice(new ArrayList<>());
        result.add(surveyDto);
      }
      // 如果选项不为空
      else {
        if (basicScore == null) {
          // 如果答案为空  默认赋分为0
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
          List<String> scoreList =
              Arrays.asList(
                  basicScore
                      .substring(1, basicScore.length() - 1)
                      .replaceAll("\\s", "")
                      .split(","));
          // 将选项数组拆分成单个的
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

  @Override
  public String getSurveyCredit(String id) {
    Survey survey = surveyMapper.selectById(id);
    String credit = survey.getCredit().toString();
    return credit;
  }


}

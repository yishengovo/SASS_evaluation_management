package org.jeecg.modules.survey.survey.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.*;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.*;
import org.jeecg.modules.survey.survey.req.SaveSurResultReq;
import org.jeecg.modules.survey.survey.service.ISurResultService;
import org.jeecg.modules.survey.survey.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: sur_result @Author: jeecg-boot @Date: 2022-06-14 @Version: V1.0
 */
@Service
public class SurResultServiceImpl extends ServiceImpl<SurResultMapper, SurResult>
    implements ISurResultService {

  @Autowired private SurUserResultMapper userResultMapper;
  @Autowired private SurResultMapper resultMapper;
  @Autowired private SurProjectSurveyMapper projectSurveyMapper;
  @Autowired private SurProjectMapper projectMapper;
  @Autowired private SurveyMapper surveyMapper;
  @Autowired private SurUserMapper userMapper;
  @Autowired private SurQuestionMapper questionMapper;
  @Autowired private SurQuestionChoiceMapper choiceMapper;
  @Autowired private SurSurveyProjectMapper surveyProjectMapper;
  @Autowired private SurQuestionProjectMapper questionProjectMapper;
  @Autowired private SurQuestionChoiceProjectMapper choiceProjectMapper;
  @Autowired private SurEvaluationUserMapper evaluationUserMapper;

  @Override
  @Transactional
  public Boolean saveSurResult(SaveSurResultReq req) {
    // 先判断该用户是否已经提交过
    JSONObject userObj = req.getUser();
    SurUser user = userObj.getObject("user", SurUser.class);
    // 查询出对应的问卷和项目对象
    SurProject surProject = projectMapper.selectById(req.getProjectId());
    // 如果项目是测评项目 则只有一个问卷答案
    if (surProject.getType().equals("测评")) {
      SurResult one =
          resultMapper.selectOne(
              new LambdaQueryWrapper<SurResult>()
                  .eq(SurResult::getProjectUid, req.getProjectId())
                  .eq(SurResult::getUserUid, user.getId()));
      if (one != null) {
        return false;
      }
    }
    // 如果是360度项目 则可能有多个答案
    else {
      SurUserResult userResult =
          userResultMapper.selectOne(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getProjectId, req.getProjectId())
                  .eq(SurUserResult::getUserId, user.getId())
                  .eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()));
      if (userResult != null) {
        return false;
      }
    }
    String surveyId = surProject.getSurveyCurrentId();
    // 根据评价人id查询评价关系对象
    SurEvaluationUser evaluationUser =
        evaluationUserMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationUser>()
                .eq(SurEvaluationUser::getEvaluatorId, user.getId())
                .eq(SurEvaluationUser::getUserId, req.getEvaluatedId())
                .eq(SurEvaluationUser::getProjectId, req.getProjectId()));
    // 先将整个问卷的答案存入到result表中
    SurResult surResult = new SurResult();
    surResult.setFid(IdUtil.simpleUUID());
    surResult.setAnswer(req.getAnswer());
    surResult.setSurveyUid(surveyId);
    surResult.setProjectUid(req.getProjectId());
    surResult.setUserUid(user.getId());
    surResult.setProject(surProject.getProjectName());
    // 判断项目的类型
    // 如果是测评项目
    if (surProject.getType().equals("测评")) {
      if (surProject.getSurveyCurrentId() != null) {
        // 查询出该问卷
        SurSurveyProject surSurveyProject =
            surveyProjectMapper.selectById(surProject.getSurveyCurrentId());
        surResult.setSurvey(surSurveyProject.getSurName());
      } else {
        Survey survey = surveyMapper.selectById(req.getSurveyId());
        surResult.setSurvey(survey.getSurName());
      }
    }
    // 如果是360度项目
    else {
      SurSurveyProject surSurveyProject =
          surveyProjectMapper.selectById(surProject.getSurveyCurrentId());
      surResult.setSurvey(surSurveyProject.getSurName());
    }
    save(surResult);
    Map<String, Object> result = req.getResult();
    Map<String, Object> map1;
    Map<String, Object> map2;
    Map<String, Object> map3;
    Iterator<Map.Entry<String, Object>> entries = result.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, Object> next = entries.next();
      String key = next.getKey();
      Object value = next.getValue();
      // 如果是String类型
      if (value instanceof String) {
        SurUserResult userResult =
            SurUserResult.builder()
                .phone(user.getPhone())
                .surveyId(surveyId)
                .userId(user.getId())
                .projectId(req.getProjectId())
                .build();
        userResult.setEvaluatedId(req.getEvaluatedId());
        if (evaluationUser != null) {
          userResult.setUserLevel(evaluationUser.getEvaluatorLevel());
        }
        userResult.setQuestionKey(key);
        userResult.setQuestionValue(value.toString());
        map1 = getChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map1 == null) {
          userResult.setBasicScore(0);
        } else {
          Object o = map1.get(value.toString());
          if (o == null) {
            userResult.setBasicScore(0);
          } else {
            userResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(userResult);
      }
      // 如果是布尔值
      // 如果是map值
      if (value instanceof Boolean) {
        Boolean booleanValue = (Boolean) value;
        SurUserResult userResult =
            SurUserResult.builder()
                .phone(user.getPhone())
                .surveyId(surveyId)
                .userId(user.getId())
                .projectId(req.getProjectId())
                .build();
        userResult.setEvaluatedId(req.getEvaluatedId());
        if (evaluationUser != null) {
          userResult.setUserLevel(evaluationUser.getEvaluatorLevel());
        }
        userResult.setQuestionKey(key);
        userResult.setQuestionValue(booleanValue.toString());
        map2 = getChoiceMap(surveyId, key);
        if (map2 == null) {
          userResult.setBasicScore(0);
        } else {
          Object o = map2.get(value.toString());
          if (o == null) {
            userResult.setBasicScore(0);
          } else {
            userResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(userResult);
      }
      if (value instanceof Map) {
        Map<String, Object> objectMap = (Map<String, Object>) value;
        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<String, Object> entry = iterator.next();
          SurUserResult userResult =
              SurUserResult.builder()
                  .phone(user.getPhone())
                  .surveyId(surveyId)
                  .userId(user.getId())
                  .projectId(req.getProjectId())
                  .build();
          userResult.setEvaluatedId(req.getEvaluatedId());
          if (evaluationUser != null) {
            userResult.setUserLevel(evaluationUser.getEvaluatorLevel());
          }
          userResult.setQuestionKey(entry.getKey());
          userResult.setQuestionValue(entry.getValue().toString());
          map3 = getChoiceMap(surveyId, entry.getKey());
          if (map3 == null) {
            userResult.setBasicScore(0);
          } else {
            // 遍历选项和分数的map
            //                    Iterator<Map.Entry<String, Object>> entries3 =
            // map3.entrySet().iterator();
            //                    while (entries3.hasNext()){
            //                        Map.Entry<String, Object> next3 = entries3.next();
            Object o = map3.get(entry.getValue().toString());
            if (o == null) {
              userResult.setBasicScore(0);
            } else {
              userResult.setBasicScore(o);
            }
          }

          userResultMapper.insert(userResult);
        }
      }
    }
    // 每提交一个用户就将已收集数量加1
    surProject.setCollectNumber(surProject.getCollectNumber() + 1);
    projectMapper.updateById(surProject);
    return true;
  }

  // 根据问卷id和key获取map
  private Map<String, Object> getChoiceMap(String surveyId, String key) {
    // 匹配数据库中的分数
    // 根据问卷id和key查询对应的问题来获取问题id
    SurQuestionProject surQuestion =
        questionProjectMapper.selectOne(
            new LambdaQueryWrapper<SurQuestionProject>()
                .eq(SurQuestionProject::getSurveyUid, surveyId)
                .eq(SurQuestionProject::getContent, key));
    Map<String, Object> map = new LinkedHashMap<>();
    // 根据问题id查询数据库中的选项对象
    if (surQuestion != null) {
      SurQuestionChoiceProject surQuestionChoice =
          choiceProjectMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionChoiceProject>()
                  .eq(SurQuestionChoiceProject::getQuestionId, surQuestion.getId()));
      // 获取选项数组
      String content = surQuestionChoice.getContent();
      List<String> choiceList = ListUtils.toList(content);
      // 如果选项数组为空
      if (choiceList == null || choiceList.size() == 1 || choiceList.size() == 0) {
        return null;
      }
      // 获取对应的分数数组
      if (surQuestionChoice.getBasicScore() != null) {
        String basicScore = surQuestionChoice.getBasicScore();
        //                List<String> scoreList = JSON.parseArray(basicScore, String.class);
        List<String> scoreList = ListUtils.toList(basicScore);
        // 如果分数为空数组
        if (scoreList.size() == 1 || scoreList.size() == 0) {
          for (String s : choiceList) {
            map.put(s, 0);
          }
        } else {
          for (int i = 0; i < choiceList.size(); i++) {
            map.put(choiceList.get(i), scoreList.get(i));
          }
        }

      } else {
        for (String s : choiceList) {
          map.put(s, 0);
        }
      }
    }
    return map;
  }
}

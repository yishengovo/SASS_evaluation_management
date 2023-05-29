package org.jeecg.modules.survey.client.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.mail.iap.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.survey.client.dto.SurveyDto;
import org.jeecg.modules.survey.client.dto.*;
import org.jeecg.modules.survey.client.entity.SurUserSurvey;
import org.jeecg.modules.survey.client.entity.UserProject;
import org.jeecg.modules.survey.client.evaluationReport.entity.*;
import org.jeecg.modules.survey.client.evaluationReport.mapper.*;
import org.jeecg.modules.survey.client.mapper.SurUserSurveyMapper;
import org.jeecg.modules.survey.client.mapper.UserProjectMapper;
import org.jeecg.modules.survey.client.req.*;
import org.jeecg.modules.survey.client.resp.PageResp;
import org.jeecg.modules.survey.client.service.ISurUserSurveyService;
import org.jeecg.modules.survey.client.service.IUserProjectService;
import org.jeecg.modules.survey.client.utils.ExcelImportListener;
import org.jeecg.modules.survey.client.utils.HttpClientUtil;
import org.jeecg.modules.survey.survey.dto.*;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.*;
import org.jeecg.modules.survey.survey.req.ChoiceByQuestionIdReq;
import org.jeecg.modules.survey.survey.req.CollectReq;
import org.jeecg.modules.survey.survey.req.ScoreSetReq;
import org.jeecg.modules.survey.survey.service.ISurProjectService;
import org.jeecg.modules.survey.survey.service.ISurSurveyProjectService;
import org.jeecg.modules.survey.survey.utils.ListUtils;
import org.jeecg.modules.survey.survey.utils.NumUtils;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysTenantMapper;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 用户端项目表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Service
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProject>
    implements IUserProjectService {
  @Autowired private SurSurveyProjectMapper userSurveyMapper;
  @Autowired private SurQuestionProjectMapper userSurveyQuestionMapper;
  @Autowired private SurQuestionChoiceProjectMapper userSurveyChoiceMapper;
  @Autowired private SysUserMapper sysUserMapper;
  @Autowired private SurUserMapper userMapper;
  @Autowired private SurUserResultMapper userResultMapper;
  @Autowired private SurResultMapper resultMapper;
  @Autowired private SurEvaluationRelationshipMapper relationshipMapper;
  @Autowired private SurEvaluationUserMapper evaluationUserMapper;
  @Autowired private SurEvaluationWeightMapper evaluationWeightMapper;
  @Autowired private ISurProjectService projectService;
  @Autowired private ISysUserService sysUserService;
  @Autowired private SurveyMapper surveyMapper;
  @Autowired private SysTenantMapper sysTenantMapper;
  @Autowired private ISurSurveyProjectService surSurveyProjectService;

  @Autowired private SurProjectSurveyMapper projectSurveyMapper;
  @Autowired private SurQuestionMapper surQuestionMapper;
  @Autowired private SurQuestionChoiceMapper surQuestionChoiceMapper;
  @Autowired private SurUserSurveyMapper surUserSurveyMapper;
  @Autowired private SurSurveyTenantMapper surSurveyTenantMapper;
  @Autowired private ReportManagementStyleEvaluationMapper reportManagementStyleEvaluationMapper;
  @Autowired private ReportBusinessDrivingForceMapper reportBusinessDrivingForceMapper;
  @Autowired private ReportCreativeAbilityMapper reportCreativeAbilityMapper;
  @Autowired private ReportModeOfThinkingMapper reportModeOfThinkingMapper;
  @Autowired private ReportTeamRoleMapper reportTeamRoleMapper;

  @Autowired private ReportSelfMotivationMapper reportSelfMotivationMapper;
  @Autowired private SurProjectEnableMapper surProjectEnableMapper;
  @Autowired private ISurUserSurveyService userSurveyService;
  @Autowired private RedisUtil redisUtil;

  @Override
  public Object queryProjectInfo(String projectId) {
    SurProject userProject = projectService.getById(projectId);
    // 获取url
    String url = (String) redisUtil.get("oss:excel");
    if (userProject == null) {
      return null;
    }
    if ("测评".equals(userProject.getType())) {
      EvaluationProjectDto evaluationProjectDto = new EvaluationProjectDto();
      // 查询问卷项目关系
      List<SurProjectSurvey> relationList =
          projectSurveyMapper.selectList(
              new LambdaQueryWrapper<SurProjectSurvey>()
                  .eq(SurProjectSurvey::getProjectId, projectId));
      List<Survey> surveyList = new ArrayList<>();
      relationList.forEach(
          relation -> {
            Survey survey = surveyMapper.selectById(relation.getSurveyId());
            surveyList.add(survey);
          });
      evaluationProjectDto.setProject(userProject);
      evaluationProjectDto.setSurveyList(surveyList);
      evaluationProjectDto.setUrl(url);
      return evaluationProjectDto;
    } else {
      ProjectDto projectDto = new ProjectDto();
      projectDto.setProject(userProject);
      SurSurveyProject survey =
          userSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurSurveyProject>()
                  .eq(SurSurveyProject::getId, userProject.getSurveyCurrentId()));
      projectDto.setSurvey(survey);
      projectDto.setUrl(url);
      return projectDto;
    }
  }

  @Override
  public Object querySurveyProject(String surveyId) {
    SurSurveyProject userProject = surSurveyProjectService.getById(surveyId);
    if (userProject == null) {
      return null;
    }
    // 获取url
    String url = (String) redisUtil.get("oss:excel");
    SurveyProjectDto surveyProjectDto = new SurveyProjectDto();
    SurSurveyProject survey =
            userSurveyMapper.selectOne(
                    new LambdaQueryWrapper<SurSurveyProject>()
                            .eq(SurSurveyProject::getId,surveyId));
    surveyProjectDto.setSurvey(survey);
    surveyProjectDto.setUrl(url);
      return surveyProjectDto;
  }


  @Override
  public SurProject createProject(CreateProjectReq req) {
    // 如果有项目id则更新
    if (req.getId() != null) {
      // 根据项目id查询项目
      SurProject project = projectService.getById(req.getId());
      // 根据项目id查询问卷
      // 此处userSurveyMapper是SurSurveyProject表的mapper
      SurSurveyProject survey =
          userSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurSurveyProject>()
                      // 条件：租户问卷对应的id==复制模板后的问卷id
                  .eq(SurSurveyProject::getId, project.getSurveyCurrentId()));
      // 如果有问卷
      if (survey != null) {
        survey.setSurName(req.getName());
        survey.setType(req.getType());
        survey.setSurContent(req.getContent());
        survey.setJsonPreview(req.getJsonPreview());
        userSurveyMapper.updateById(survey);
        // 删除答卷
        resultMapper.delete(
            new LambdaQueryWrapper<SurResult>().eq(SurResult::getSurveyUid, survey.getId()));
        userResultMapper.delete(
            new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getSurveyId, survey.getId()));
        // 将项目已经提交的人数清零
        project.setCollectNumber(0);
        // 将项目是否需要添加人员置为false
        project.setIsAddUser(false);
        // 删除用户
        userMapper.delete(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, project.getId()));
        // 修改问卷与用户关系
        userSurveyService.remove(
            new LambdaQueryWrapper<SurUserSurvey>().eq(SurUserSurvey::getSurveyId, survey.getId()));
        projectService.updateById(project);
      } else {
        // 如果没有问卷则新增
        survey = new SurSurveyProject();
        survey.setSurName(req.getName());
        survey.setSurContent(req.getContent());
        survey.setType(req.getType());
        survey.setJsonPreview(req.getJsonPreview());
        userSurveyMapper.insert(survey);
        // 插入的过程自动生成主键，并且会set给对象属性
        project.setSurveyCurrentId(survey.getId());
      }
      projectService.updateById(project);
      // 如果有问题的话 先删除原来的问题 再去保存新的问题
      // 属于该问卷的所有问题
      List<SurQuestionProject> questions =
          userSurveyQuestionMapper.selectList(
              new LambdaQueryWrapper<SurQuestionProject>()
                  .eq(SurQuestionProject::getSurveyUid, project.getSurveyCurrentId()));
      // 获取所有问题的id，去除重复
      List<String> questionIList =
          questions.stream().map(SurQuestionProject::getId).distinct().collect(Collectors.toList());
      // 根据所有问题的id，批量删除
      if (!questionIList.isEmpty()) {
        userSurveyQuestionMapper.deleteBatchIds(questionIList);
      }
      // 属于该问卷的所有问题选项
      List<SurQuestionChoiceProject> questionChoices =
          userSurveyChoiceMapper.selectList(
              new LambdaQueryWrapper<SurQuestionChoiceProject>()
                  .eq(SurQuestionChoiceProject::getSurveyUid, project.getSurveyCurrentId()));
      // 拿到所有选项的id
      List<String> questionChoiceList =
          questionChoices.stream()
              .map(SurQuestionChoiceProject::getId)
              .distinct()
              .collect(Collectors.toList());
      // 根据问题选项id，批量删除
      if (!questionChoiceList.isEmpty()) {
        userSurveyChoiceMapper.deleteBatchIds(questionChoiceList);
      }

      // 将参数中的问题保存到 question和choices表中
      List<SurveyQuestionReq> questionList = req.getQuestion();
      SurQuestionProject surQuestion;
      for (SurveyQuestionReq question : questionList) {
        // 判断是单个问题还是矩阵里的问题
        // 如果是单个问题
        if (StringUtils.isNotBlank(question.getName()) && question.getNames().isEmpty()) {
          List<Integer> score = new ArrayList<>();
          surQuestion = new SurQuestionProject();
          List<String> choices = question.getChoices();
          // 将问题插入到question表中
          surQuestion.setSurveyUid(survey.getId());
          surQuestion.setContent(question.getName());
          surQuestion.setIsParent(false);
          surQuestion.setTitle(question.getTitle());
          surQuestion.setTypeId(question.getType());
          userSurveyQuestionMapper.insert(surQuestion);
          // 遍历choices 将每个答案存到数据库中
          SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
          surQuestionChoice.setQuestionId(surQuestion.getId());
          surQuestionChoice.setSurveyUid(survey.getId());
          // 将choiceList转化成json字符串
          String choiceListJson = JSONObject.toJSONString(choices);
          surQuestionChoice.setContent(choiceListJson);
          for (int i = 0; i < choices.size(); i++) {
            score.add(0);
          }
          surQuestionChoice.setBasicScore(score.toString());
          userSurveyChoiceMapper.insert(surQuestionChoice);
        }
        // 如果是矩阵问题
        if (!question.getNames().isEmpty()) {
          List<String> names = question.getNames();
          // 父问题
          SurQuestionProject parentQuestion = new SurQuestionProject();
          parentQuestion.setSurveyUid(survey.getId());
          parentQuestion.setIsParent(true);
          parentQuestion.setContent(question.getName());
          parentQuestion.setTitle(question.getTitle());
          parentQuestion.setTypeId(question.getType());
          userSurveyQuestionMapper.insert(parentQuestion);
          for (String name : names) {
            List<Integer> score = new ArrayList<>();
            surQuestion = new SurQuestionProject();
            List<String> choices = question.getChoices();
            // 将问题插入到question表中
            surQuestion.setSurveyUid(survey.getId());
            surQuestion.setContent(name);
            surQuestion.setTitle(question.getTitle());
            surQuestion.setTypeId(question.getType());
            surQuestion.setIsParent(false);
            surQuestion.setParentId(parentQuestion.getId());
            surQuestion.setParentContent(parentQuestion.getContent());
            userSurveyQuestionMapper.insert(surQuestion);
            // 遍历choices 将每个选项存到数据库中
            SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
            surQuestionChoice.setQuestionId(surQuestion.getId());
            surQuestionChoice.setSurveyUid(survey.getId());
            String choiceListJson = JSONObject.toJSONString(choices);
            surQuestionChoice.setContent(choiceListJson);
            for (int i = 0; i < choices.size(); i++) {
              score.add(0);
            }
            surQuestionChoice.setBasicScore(score.toString());
            userSurveyChoiceMapper.insert(surQuestionChoice);
          }
        }
      }
      return project;
    }
    // 如果没有id则新建项目
    else {
      // 创建新项目
      SurProject project = new SurProject();
      BeanUtils.copyProperties(req, project);
      // 部分字段对应不上，需要手动赋值
      project.setProjectName(req.getName());
      project.setSelectNumber(0);
      project.setCollectNumber(0);
      project.setIsPublish(false);
      project.setIsDel(false);
      // 创建新问卷，调查和360目前只能手动创建问卷，所以项目本体和问卷是一对一
      SurSurveyProject survey = new SurSurveyProject();
      survey.setSurName(req.getName());
      survey.setSurContent(req.getContent());
      survey.setJsonPreview(req.getJsonPreview());
      survey.setType(req.getType());
      userSurveyMapper.insert(survey);
      // 插入的时候，雪花算法可以生成主键值，并通过set方法给对象赋值，所以可以直接拿到
      project.setSurveyCurrentId(survey.getId());
      projectService.save(project);
      // 创建问卷和项目关系
      SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
      surProjectSurvey.setProjectId(project.getId());
      surProjectSurvey.setSurveyId(survey.getId());
      projectSurveyMapper.insert(surProjectSurvey);
      // 将参数中的问题保存到 question和choices表中
      List<SurveyQuestionReq> questionList = req.getQuestion();
      SurQuestionProject surQuestion;
      for (SurveyQuestionReq question : questionList) {
        // 判断是单个问题还是矩阵里的问题
        // 如果是单个问题，那么矩阵问题名称是空的
        if (StringUtils.isNotBlank(question.getName()) && question.getNames().isEmpty()) {
          // 每个选项的分值
          List<Integer> score = new ArrayList<>();
          // 放到question_project表中
          surQuestion = new SurQuestionProject();
          List<String> choices = question.getChoices();
          // 将问题插入到question表中
          surQuestion.setSurveyUid(survey.getId());
          surQuestion.setIsParent(false);
          surQuestion.setContent(question.getName());
          surQuestion.setTitle(question.getTitle());
          surQuestion.setTypeId(question.getType());
          userSurveyQuestionMapper.insert(surQuestion);
          // 遍历choices 将每个答案存到数据库中
          SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
          surQuestionChoice.setQuestionId(surQuestion.getId());
          surQuestionChoice.setSurveyUid(survey.getId());
          // JSONObject.toJSONString()默认忽略值为null的属性
          String choiceListJson = JSONObject.toJSONString(choices);
          surQuestionChoice.setContent(choiceListJson);
          for (int i = 0; i < choices.size(); i++) {
            score.add(0);
          }
          // 直接转成字符串
          surQuestionChoice.setBasicScore(score.toString());
          userSurveyChoiceMapper.insert(surQuestionChoice);
        }
        // 如果是矩阵问题
        if (!question.getNames().isEmpty()) {
          List<String> names = question.getNames();
          // 父问题
          SurQuestionProject parentQuestion = new SurQuestionProject();
          parentQuestion.setSurveyUid(survey.getId());
          parentQuestion.setContent(question.getName());
          parentQuestion.setIsParent(true);
          parentQuestion.setTitle(question.getTitle());
          parentQuestion.setTypeId(question.getType());
          userSurveyQuestionMapper.insert(parentQuestion);
          for (String name : names) {
            List<Integer> score = new ArrayList<>();
            surQuestion = new SurQuestionProject();
            List<String> choices = question.getChoices();
            // 将问题插入到question表中
            surQuestion.setSurveyUid(survey.getId());
            surQuestion.setContent(name);
            surQuestion.setTitle(question.getTitle());
            surQuestion.setTypeId(question.getType());
            surQuestion.setIsParent(false);
            surQuestion.setParentId(parentQuestion.getId());
            surQuestion.setParentContent(parentQuestion.getContent());
            userSurveyQuestionMapper.insert(surQuestion);
            // 遍历choices 将每个答案存到数据库中
            SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
            surQuestionChoice.setQuestionId(surQuestion.getId());
            surQuestionChoice.setSurveyUid(survey.getId());
            String choiceListJson = JSONObject.toJSONString(choices);
            surQuestionChoice.setContent(choiceListJson);
            for (int i = 0; i < choices.size(); i++) {
              score.add(0);
            }
            surQuestionChoice.setBasicScore(score.toString());
            userSurveyChoiceMapper.insert(surQuestionChoice);
          }
        }
      }
      return project;
    }
  }

  @Override
  public SurSurveyProject surveyMarketSave(SurveyMarketSaveReq req) {
    // 先通过问题id查到问卷
    SurSurveyProject survey =
            userSurveyMapper.selectOne(
                    new LambdaQueryWrapper<SurSurveyProject>()
                            .eq(SurSurveyProject::getId, req.getSurveyId()));

    // 判断是否拿到问卷
    if (survey==null) return null;

    // 不为null,则执行更新
    survey.setSurName(req.getName())
            .setType(req.getType())
            .setJsonPreview(req.getJsonPreview())
            .setSurContent(req.getContent());
    userSurveyMapper.updateById(survey);

    // 如果有问题的话 先删除原来的问题 再去保存新的问题
    // 属于该问卷的所有问题
    List<SurQuestionProject> questions =
            userSurveyQuestionMapper.selectList(
                    new LambdaQueryWrapper<SurQuestionProject>()
                            .eq(SurQuestionProject::getSurveyUid, survey.getId()));
    // 获取所有问题的id，去除重复
    List<String> questionIList =
            questions.stream().map(SurQuestionProject::getId).distinct().collect(Collectors.toList());
    // 根据所有问题的id，批量删除
    if (!questionIList.isEmpty()) {
      userSurveyQuestionMapper.deleteBatchIds(questionIList);
    }
    // 属于该问卷的所有问题选项
    List<SurQuestionChoiceProject> questionChoices =
            userSurveyChoiceMapper.selectList(
                    new LambdaQueryWrapper<SurQuestionChoiceProject>()
                            .eq(SurQuestionChoiceProject::getSurveyUid, survey.getId()));
    // 拿到所有选项的id
    List<String> questionChoiceList =
            questionChoices.stream()
                    .map(SurQuestionChoiceProject::getId)
                    .distinct()
                    .collect(Collectors.toList());
    // 根据问题选项id，批量删除
    if (!questionChoiceList.isEmpty()) {
      userSurveyChoiceMapper.deleteBatchIds(questionChoiceList);
    }

    // 将参数中的问题保存到 question和choices表中
    List<SurveyQuestionReq> questionList = req.getQuestion();
    SurQuestionProject surQuestion;
    for (SurveyQuestionReq question : questionList) {
      // 判断是单个问题还是矩阵里的问题
      // 如果是单个问题
      if (StringUtils.isNotBlank(question.getName()) && question.getNames().isEmpty()) {
        List<Integer> score = new ArrayList<>();
        surQuestion = new SurQuestionProject();
        List<String> choices = question.getChoices();
        // 将问题插入到question表中
        surQuestion.setSurveyUid(survey.getId());
        surQuestion.setContent(question.getName());
        surQuestion.setIsParent(false);
        surQuestion.setTitle(question.getTitle());
        surQuestion.setTypeId(question.getType());
        userSurveyQuestionMapper.insert(surQuestion);
        // 遍历choices 将每个答案存到数据库中
        SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
        surQuestionChoice.setQuestionId(surQuestion.getId());
        surQuestionChoice.setSurveyUid(survey.getId());
        // 将choiceList转化成json字符串
        String choiceListJson = JSONObject.toJSONString(choices);
        surQuestionChoice.setContent(choiceListJson);
        for (int i = 0; i < choices.size(); i++) {
          score.add(0);
        }
        surQuestionChoice.setBasicScore(score.toString());
        userSurveyChoiceMapper.insert(surQuestionChoice);
      }
      // 如果是矩阵问题
      if (!question.getNames().isEmpty()) {
        List<String> names = question.getNames();
        // 父问题
        SurQuestionProject parentQuestion = new SurQuestionProject();
        parentQuestion.setSurveyUid(survey.getId());
        parentQuestion.setIsParent(true);
        parentQuestion.setContent(question.getName());
        parentQuestion.setTitle(question.getTitle());
        parentQuestion.setTypeId(question.getType());
        userSurveyQuestionMapper.insert(parentQuestion);
        for (String name : names) {
          List<Integer> score = new ArrayList<>();
          surQuestion = new SurQuestionProject();
          List<String> choices = question.getChoices();
          // 将问题插入到question表中
          surQuestion.setSurveyUid(survey.getId());
          surQuestion.setContent(name);
          surQuestion.setTitle(question.getTitle());
          surQuestion.setTypeId(question.getType());
          surQuestion.setIsParent(false);
          surQuestion.setParentId(parentQuestion.getId());
          surQuestion.setParentContent(parentQuestion.getContent());
          userSurveyQuestionMapper.insert(surQuestion);
          // 遍历choices 将每个选项存到数据库中
          SurQuestionChoiceProject surQuestionChoice = new SurQuestionChoiceProject();
          surQuestionChoice.setQuestionId(surQuestion.getId());
          surQuestionChoice.setSurveyUid(survey.getId());
          String choiceListJson = JSONObject.toJSONString(choices);
          surQuestionChoice.setContent(choiceListJson);
          for (int i = 0; i < choices.size(); i++) {
            score.add(0);
          }
          surQuestionChoice.setBasicScore(score.toString());
          userSurveyChoiceMapper.insert(surQuestionChoice);
        }
      }
    }
    return survey;
  }


  @Override
  public Page<SurProject> getProjectList(ProjectAdvancedQueryReq req) {
    // 条件高级查询
    LambdaQueryWrapper<SurProject> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SurProject::getTenantId, req.getId()).eq(SurProject::getIsDel, false);
    if (req.getIsPublish() != null) {
      queryWrapper.eq(SurProject::getIsPublish, req.getIsPublish());
    }
    if (req.getType() != null && !req.getType().isEmpty()) {
      queryWrapper.eq(SurProject::getType, req.getType());
    }
    if (req.getName() != null && !req.getName().isEmpty()) {
      queryWrapper.like(SurProject::getProjectName, req.getName());
    }
    if (req.getOrder() != null) {
      // 如果为true 为降序
      if (req.getOrder()) {
        queryWrapper.orderByDesc(SurProject::getUpdateTime);
      } else {
        queryWrapper.orderByAsc(SurProject::getUpdateTime);
      }
    }
    return projectService.page(new Page<>(req.getPageNum(), req.getPageSize()), queryWrapper);
  }

  @Override
  public Page getQuestionList(EvaluationQuestionReq req) {
    // 根据项目id查询项目实体
    SurProject project = projectService.getById(req.getId());
    Page<SurQuestionProject> userSurveyQuestionPage =
        new Page<>(req.getPageNum(), req.getPageSize());
    if (project != null) {
      if ("测评".equals(project.getType())) {
        return surQuestionMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()));
      } else {
        userSurveyQuestionPage =
            userSurveyQuestionMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()),
                new LambdaQueryWrapper<SurQuestionProject>()
                    .eq(SurQuestionProject::getSurveyUid, project.getSurveyCurrentId()));
      }
    }
    return userSurveyQuestionPage;
  }

  @Override
  public Boolean updateStatus(ProjectStatusReq req) {
    SurProject project = projectService.getById(req.getId());
    project.setIsPublish(req.getIsPublish());
    projectService.updateById(project);
    return true;
  }

  @Override
  public List<ChoiceDto> getQuestionListByQuestionId(ChoiceByQuestionIdReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return null;
    }
    if ("测评".equals(project.getType())) {
      // 如果是测评项目
      SurQuestionChoice choice =
          surQuestionChoiceMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionChoice>()
                  .eq(SurQuestionChoice::getQuestionId, req.getQuestionId()));
      // 获取选项数组
      String content = choice.getContent();
      List<String> choiceList =
          Arrays.asList(
              content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
      // 获取答案数组
      String basicScore = choice.getBasicScore();
      List<ChoiceDto> result = new ArrayList<>();
      if (basicScore == null) {
        // 如果选项为空
        if (choiceList.size() == 1) {
          return new ArrayList<>();
        }
        // 如果选项不为空
        for (String s : choiceList) {
          ChoiceDto choiceDto = new ChoiceDto();
          BeanUtils.copyProperties(choice, choiceDto);
          choiceDto.setChoice(s);
          choiceDto.setBasicScore(0);
          result.add(choiceDto);
        }
        return result;
      }
      List<String> scoreList =
          Arrays.asList(
              basicScore.substring(1, basicScore.length() - 1).replaceAll("\\s", "").split(","));
      // 将选项数组拆分成单个的
      if (choiceList.size() != 0) {
        for (int i = 0; i < choiceList.size(); i++) {
          if (scoreList.get(i) != null) {
            ChoiceDto choiceDto = new ChoiceDto();
            BeanUtils.copyProperties(choice, choiceDto);
            choiceDto.setChoice(choiceList.get(i));
            choiceDto.setBasicScore(scoreList.get(i));
            result.add(choiceDto);
          }
        }
      } else {
        return new ArrayList<>();
      }
    } else {
      SurQuestionChoiceProject choice =
          userSurveyChoiceMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionChoiceProject>()
                  .eq(SurQuestionChoiceProject::getQuestionId, req.getQuestionId()));
      // 获取选项数组
      String content = choice.getContent();
      List<String> choiceList =
          Arrays.asList(
              content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
      // 获取答案数组
      String basicScore = choice.getBasicScore();
      List<ChoiceDto> result = new ArrayList<>();
      if (basicScore == null) {
        // 如果选项为空
        if (choiceList.size() == 1) {
          return new ArrayList<>();
        }
        // 如果选项不为空
        for (String s : choiceList) {
          ChoiceDto choiceDto = new ChoiceDto();
          BeanUtils.copyProperties(choice, choiceDto);
          choiceDto.setChoice(s);
          choiceDto.setBasicScore(0);
          result.add(choiceDto);
        }
        return result;
      }
      List<String> scoreList =
          Arrays.asList(
              basicScore.substring(1, basicScore.length() - 1).replaceAll("\\s", "").split(","));
      // 将选项数组拆分成单个的
      if (choiceList.size() != 0) {
        for (int i = 0; i < choiceList.size(); i++) {
          if (scoreList.get(i) != null) {
            ChoiceDto choiceDto = new ChoiceDto();
            BeanUtils.copyProperties(choice, choiceDto);
            choiceDto.setChoice(choiceList.get(i));
            choiceDto.setBasicScore(scoreList.get(i));
            result.add(choiceDto);
          }
        }
      } else {
        return new ArrayList<>();
      }
      return result;
    }
    return null;
  }

  @Override
  public Boolean setScore(ScoreSetReq req) {
    // 根据项目id查询项目实体
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return false;
    }
    if ("测评".equals(project.getType())) {
      // 如果是测评项目
      // 先根据选项id查询选项实体
      SurQuestionChoice choice = surQuestionChoiceMapper.selectById(req.getId());
      choice.setBasicScore(req.getScore().toString());
      // 更新数据库
      surQuestionChoiceMapper.updateById(choice);
    } else {
      // 先根据选项id查询选项实体
      SurQuestionChoiceProject choice = userSurveyChoiceMapper.selectById(req.getId());
      //		 String content = choice.getContent();
      //		 List<String> choiceList = Arrays.asList(content.substring(1, content.length() -
      // 1).replaceAll("\\s", "").split(","));
      choice.setBasicScore(req.getScore().toString());
      // 更新数据库
      userSurveyChoiceMapper.updateById(choice);
    }

    return true;
  }

  @Override
  public Boolean deleteProjectById(String id) {
    // 根据项目id查询项目实体
    SurProject project = projectService.getById(id);
    if (project == null) {
      return false;
    }
    // 删除项目主表
    projectService.removeById(id);
    // 删除问卷
    SurSurveyProject survey =
        userSurveyMapper.selectOne(
            new LambdaQueryWrapper<SurSurveyProject>()
                .eq(SurSurveyProject::getId, project.getSurveyCurrentId()));
    if (survey != null) {
      userSurveyMapper.deleteById(survey.getId());
      // 删除问卷的问题和选项
      userSurveyQuestionMapper.delete(
          new LambdaQueryWrapper<SurQuestionProject>()
              .eq(SurQuestionProject::getSurveyUid, survey.getId()));
      userSurveyChoiceMapper.delete(
          new LambdaQueryWrapper<SurQuestionChoiceProject>()
              .eq(SurQuestionChoiceProject::getSurveyUid, survey.getId()));
      userSurveyMapper.deleteById(survey);
    }
    // 删除项目控制
    surProjectEnableMapper.delete(
        new LambdaQueryWrapper<SurProjectEnable>().eq(SurProjectEnable::getProjectId, id));
    // 删除人
    userMapper.delete(new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, project.getId()));
    // 删除result和user_result
    resultMapper.delete(
        new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, project.getId()));
    userResultMapper.delete(
        new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, project.getId()));
    // 删除项目和问卷关系
    projectSurveyMapper.delete(
        new LambdaQueryWrapper<SurProjectSurvey>()
            .eq(SurProjectSurvey::getProjectId, project.getId()));
    // 删除问卷和人关系
    surUserSurveyMapper.delete(
        new LambdaQueryWrapper<SurUserSurvey>().eq(SurUserSurvey::getProjectId, project.getId()));
    // 如果是360项目 则还要删除评价关系
    if ("360度评估".equals(project.getType())) {
      relationshipMapper.delete(
          new LambdaQueryWrapper<SurEvaluationRelationship>()
              .eq(SurEvaluationRelationship::getProjectId, project.getId()));
      evaluationUserMapper.delete(
          new LambdaQueryWrapper<SurEvaluationUser>()
              .eq(SurEvaluationUser::getProjectId, project.getId()));
      evaluationWeightMapper.delete(
          new LambdaQueryWrapper<SurEvaluationWeight>()
              .eq(SurEvaluationWeight::getProjectId, project.getId()));
    }
    // 删除报表
    reportBusinessDrivingForceMapper.delete(
        new LambdaQueryWrapper<ReportBusinessDrivingForce>()
            .eq(ReportBusinessDrivingForce::getProjectId, project.getId()));
    reportCreativeAbilityMapper.delete(
        new LambdaQueryWrapper<ReportCreativeAbility>()
            .eq(ReportCreativeAbility::getProjectId, project.getId()));
    reportManagementStyleEvaluationMapper.delete(
        new LambdaQueryWrapper<ReportManagementStyleEvaluation>()
            .eq(ReportManagementStyleEvaluation::getProjectId, project.getId()));
    reportModeOfThinkingMapper.delete(
        new LambdaQueryWrapper<ReportModeOfThinking>()
            .eq(ReportModeOfThinking::getProjectId, project.getId()));
    reportSelfMotivationMapper.delete(
        new LambdaQueryWrapper<ReportSelfMotivation>()
            .eq(ReportSelfMotivation::getProjectId, project.getId()));
    reportTeamRoleMapper.delete(
        new LambdaQueryWrapper<ReportTeamRole>().eq(ReportTeamRole::getProjectId, project.getId()));
    return true;
  }

  @Override
  public AssessmentDto get360CollectData(CollectReq req) {
    // 首先根据项目id查询项目对象
    SurProject project = projectService.getById(req.getId());
    AssessmentDto collectDto = new AssessmentDto();
    BeanUtils.copyProperties(project, collectDto);
    collectDto.setSurveyId(project.getSurveyCurrentId());
    collectDto.setRate("100%");
    // 查询问卷
    SurSurveyProject survey =
        userSurveyMapper.selectOne(
            new LambdaQueryWrapper<SurSurveyProject>()
                .eq(SurSurveyProject::getId, project.getSurveyCurrentId()));
    // 查询被评价人
    Page<SurEvaluationRelationship> evaluatedPage =
        relationshipMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurEvaluationRelationship>()
                .eq(SurEvaluationRelationship::getProjectId, req.getId()));
    List<SurEvaluationRelationship> evaluatedList = evaluatedPage.getRecords();
    // 查询全部的被评价者
    List<SurEvaluationRelationship> allEvaluatedList =
        relationshipMapper.selectList(
            new LambdaQueryWrapper<SurEvaluationRelationship>()
                .eq(SurEvaluationRelationship::getProjectId, req.getId()));
    // 项目选择人数
    int selectNum = allEvaluatedList.size();
    collectDto.setSelectNumber(selectNum);
    int finishNum = 0;
    for (SurEvaluationRelationship evaluated : allEvaluatedList) {
      // 查询用户问卷关系
      List<SurUserSurvey> userSurveys =
          surUserSurveyMapper.selectList(
              new LambdaQueryWrapper<SurUserSurvey>()
                  .eq(SurUserSurvey::getEvaluatedId, evaluated.getUserId()));
      // 只保留status为0的
      userSurveys =
          userSurveys.stream()
              .filter(userSurvey -> userSurvey.getStatus() == 0)
              .collect(Collectors.toList());
      if (userSurveys.isEmpty()) {
        // 如果为空 则说明完成了问卷
        finishNum++;
      }
    }
    collectDto.setCollectNumber(finishNum);
    if (selectNum == 0) {
      collectDto.setRate("0%");
    } else {
      collectDto.setRate((finishNum * 100 / selectNum) + "%");
    }
    // 被评价人dto
    List<JSONObject> userDtoList = new ArrayList<>();
    // 遍历被评价人
    evaluatedList.forEach(
        evaluated -> {
          // 查询用户对象
          SurUser user = userMapper.selectById(evaluated.getUserId());
          // 被评价者dto
          JSONObject evaluatedDto = new JSONObject();
          evaluatedDto.put("id", evaluated.getUserId());
          evaluatedDto.put("evaluatedName", evaluated.getUserName());
          if (user != null) {
            evaluatedDto.put("gender", user.getGender());
            evaluatedDto.put("phone", user.getPhone());
            evaluatedDto.put("age", user.getAge());
            if (survey != null) {
              evaluatedDto.put("jsonPreview", survey.getJsonPreview());
              evaluatedDto.put("surName", survey.getSurName());
            } else {
              evaluatedDto.put("jsonPreview", "");
              evaluatedDto.put("surName", "");
            }
          }
          // 查询评价者
          List<SurEvaluationUser> evaluators =
              evaluationUserMapper.selectList(
                  new LambdaQueryWrapper<SurEvaluationUser>()
                      .eq(SurEvaluationUser::getProjectId, req.getId())
                      .eq(SurEvaluationUser::getUserId, evaluated.getUserId()));
          int index = 1;
          // 遍历评价者
          for (SurEvaluationUser evaluator : evaluators) {
            // 查询评价者对象
            SurUser evaluatorUser = userMapper.selectById(evaluator.getEvaluatorId());
            // 评价人dto
            AssessmentEvaluatorDto evaluatorDto = new AssessmentEvaluatorDto();
            evaluatorDto.setId(evaluator.getEvaluatorId());
            evaluatorDto.setName(evaluator.getEvaluatorName());
            if (evaluatorUser != null) {
              evaluatorDto.setPhone(evaluatorUser.getPhone());
            }
            evaluatorDto.setLevel(evaluator.getEvaluatorLevel());
            // 查询答卷json
            SurResult surResult =
                resultMapper.selectOne(
                    new LambdaQueryWrapper<SurResult>()
                        .eq(SurResult::getUserUid, evaluator.getEvaluatorId())
                        .eq(SurResult::getEvaluatedId, evaluated.getUserId()));
            if (surResult != null) {
              evaluatorDto.setResult(surResult.getAnswer());
            }
            // 查询用户问卷关系
            SurUserSurvey userSurvey =
                surUserSurveyMapper.selectOne(
                    new LambdaQueryWrapper<SurUserSurvey>()
                        .eq(SurUserSurvey::getEvaluatedId, evaluated.getUserId())
                        .eq(SurUserSurvey::getUserId, evaluator.getEvaluatorId()));
            if (userSurvey != null) {
              evaluatorDto.setStatus(userSurvey.getStatus());
            }
            evaluatedDto.put("evaluator" + index, evaluatorDto);
            index++;
          }
          // 计算分数
          double score = getEvaluatedScore(evaluated.getUserId(), req.getId());
          evaluatedDto.put("score", score);
          userDtoList.add(evaluatedDto);
        });
    AssessmentUserPage userPage = new AssessmentUserPage();
    userPage.setUser(userDtoList);
    userPage.setSize(evaluatedPage.getSize());
    userPage.setTotal(evaluatedPage.getTotal());
    collectDto.setUsers(userPage);
    return collectDto;
  }

  @Override
  public CollectDto getInvestigationCollectData(CollectReq req) {
    // 首先根据项目id查询项目对象
    SurProject project = projectService.getById(req.getId());
    CollectDto collectDto = new CollectDto();
    BeanUtils.copyProperties(project, collectDto);
    SurSurveyProject surSurveyProject = userSurveyMapper.selectById(project.getSurveyCurrentId());
    collectDto.setSurveyId(project.getSurveyCurrentId());
    // 查询项目用户列表
    List<SurUser> userList =
        userMapper.selectList(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    Page<SurUser> page =
        userMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    // 判断该项目是否导入人员
    // 如果导入
    List<SurUser> completed =
        userList.stream().filter(SurUser::getIsFinished).collect(Collectors.toList());
    collectDto.setCollectNumber(completed.size());
    collectDto.setSelectNumber(userList.size());
    if (userList.isEmpty()) {
      collectDto.setRate("0%");
    } else {
      // 计算比例
      String rate = new DecimalFormat("0.00%").format((double) completed.size() / userList.size());
      collectDto.setRate(rate);
    }
    List<SurUser> records = page.getRecords();
    List<UserDto> userDtoList = new ArrayList<>();
    records.forEach(
        surUser -> {
          UserDto userDto = new UserDto();
          BeanUtils.copyProperties(surUser, userDto);
          SurUserResult name =
              userResultMapper.selectOne(
                  new LambdaQueryWrapper<SurUserResult>()
                      .eq(SurUserResult::getProjectId, req.getId())
                      .eq(SurUserResult::getUserId, surUser.getId())
                      .eq(SurUserResult::getQuestionKey, "姓名"));
          SurUserResult phone =
              userResultMapper.selectOne(
                  new LambdaQueryWrapper<SurUserResult>()
                      .eq(SurUserResult::getProjectId, req.getId())
                      .eq(SurUserResult::getUserId, surUser.getId())
                      .eq(SurUserResult::getQuestionKey, "电话"));
          // 查询用户答题状态
          SurUserSurvey surUserSurvey =
              userSurveyService.getOne(
                  new LambdaQueryWrapper<SurUserSurvey>()
                      .eq(SurUserSurvey::getProjectId, req.getId())
                      .eq(SurUserSurvey::getUserId, surUser.getId()));
          if (surUserSurvey != null && surUserSurvey.getStatus() != null) {
            userDto.setStatus(surUserSurvey.getStatus());
          } else {
            userDto.setStatus(0);
          }
          if (name != null) {
            userDto.setName(name.getQuestionValue());
          } else if (StringUtils.isNotBlank(surUser.getName())) {
            userDto.setName(surUser.getName());
          } else {
            userDto.setName("匿名");
          }
          if (phone != null) {
            userDto.setPhone(phone.getQuestionValue());
          } else if (StringUtils.isNotBlank(surUser.getPhone())) {
            userDto.setPhone(surUser.getPhone());
          } else {
            userDto.setPhone("未知");
          }

          if (surSurveyProject != null) {
            userDto.setJsonPreview(surSurveyProject.getJsonPreview());
            userDto.setSurName(surSurveyProject.getSurName());
          } else {
            userDto.setJsonPreview("");
            userDto.setSurName("");
          }
          // 查出用户完整的答案json
          SurResult surResult =
              resultMapper.selectOne(
                  new LambdaQueryWrapper<SurResult>()
                      .eq(SurResult::getProjectUid, req.getId())
                      .eq(SurResult::getUserUid, surUser.getId()));
          if (surResult != null) {
            userDto.setResult(surResult.getAnswer());
          }
          userDtoList.add(userDto);
        });
    page.setRecords(records);
    UserPage userPage = new UserPage();
    userPage.setUser(userDtoList);
    userPage.setSize(page.getSize());
    userPage.setTotal(page.getTotal());
    collectDto.setUsers(userPage);
    return collectDto;
  }

  @Override
  public ProjectResultDto getSurveyResult(String projectId) {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return null;
    }
    // 根据问卷id查询问卷
    SurSurveyProject survey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    ProjectResultDto projectResultDto = new ProjectResultDto();
    projectResultDto.setId(project.getId());
    projectResultDto.setName(project.getProjectName());
    if (survey != null) {
      projectResultDto.setSurveyJson(survey.getJsonPreview());
    }
    projectResultDto.setCollectNum(project.getCollectNumber());
    // 查询用户结果
    List<SurResult> resultList =
        resultMapper.selectList(
            new LambdaQueryWrapper<SurResult>()
                .eq(SurResult::getProjectUid, projectId)
                .orderByDesc(SurResult::getCreateDate));
    if (resultList.size() > 0) {
      SurResult lastResult = resultList.get(0);
      if (lastResult.getCreateDate() != null) {
        projectResultDto.setLastEditTime(lastResult.getCreateDate());
      } else {
        projectResultDto.setLastEditTime(project.getUpdateTime());
      }
    } else {
      projectResultDto.setLastEditTime(project.getUpdateTime());
    }
    List<JSONObject> results = new ArrayList<>();
    resultList.forEach(
        surResult -> {
          JSONObject resultObj = new JSONObject();
          resultObj.put("answer", surResult.getAnswer());
          results.add(resultObj);
        });
    projectResultDto.setSurveyResults(results);
    return projectResultDto;
  }

  @Override
  public ProjectCollectDto getStatistics(String projectId) {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return null;
    }
    // 根据问卷id查询问卷
    SurSurveyProject survey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    ProjectCollectDto projectCollectDto = new ProjectCollectDto();
    projectCollectDto.setProjectId(project.getId());
    projectCollectDto.setProjectName(project.getProjectName());
    projectCollectDto.setType(project.getType());
    projectCollectDto.setCollectNumber(project.getCollectNumber());
    List<Object> resultList = new ArrayList<>();
    if (survey != null) {
      // 查询该问卷的所有题目
      List<SurQuestionProject> questionProjectList =
          userSurveyQuestionMapper.selectList(
              new LambdaQueryWrapper<SurQuestionProject>()
                  .eq(SurQuestionProject::getSurveyUid, survey.getId()));
      questionProjectList.forEach(
          question -> {
            // 构造题目json对象
            Map<String, Object> questionMap = new LinkedHashMap<>();
            questionMap.put("id", question.getId());
            questionMap.put("content", question.getContent());
            questionMap.put("type", question.getTypeId());
            List<JSONObject> choiceList = new ArrayList<>();
            // 查询该题目的有效填写人数
            List<SurUserResult> surUserResults =
                userResultMapper.selectList(
                    new LambdaQueryWrapper<SurUserResult>()
                        .eq(SurUserResult::getProjectId, projectId)
                        .eq(SurUserResult::getQuestionKey, question.getContent()));
            questionMap.put("userNumber", surUserResults.size());
            // 计算该题目的平均分
            //                surUserResults.stream().map(SurUserResult::getBasicScore).reduce((a,
            // b) -> a + b).ifPresent(score -> {
            //                    questionObj.put("averageScore", score / surUserResults.size());
            //                });
            surUserResults.stream()
                .map(SurUserResult::getBasicScore)
                .reduce((a, b) -> Integer.parseInt(a.toString()) + Integer.parseInt(b.toString()))
                .ifPresent(
                    score -> {
                      int total = Integer.parseInt(score.toString());
                      if (total != 0) {
                        questionMap.put("average", NumUtils.device(total, surUserResults.size()));
                      } else {
                        questionMap.put("average", 0);
                      }
                    });
            // 查询该题目的选项
            SurQuestionChoiceProject choiceProject =
                userSurveyChoiceMapper.selectOne(
                    new LambdaQueryWrapper<SurQuestionChoiceProject>()
                        .eq(SurQuestionChoiceProject::getQuestionId, question.getId()));
            String choiceListJson = choiceProject.getContent();
            List<String> choiceLists = JSONObject.parseArray(choiceListJson, String.class);
            //            List<String> choiceLists =
            //                Arrays.asList(
            //                    content.substring(1, content.length() - 1).replaceAll("\\s",
            // "").split(","));
            if (choiceLists.size() > 1) {
              // 遍历选项数组
              Long totalNum = 0L;
              // 计算 多选题中每个选项的总人数
              for (String choice : choiceLists) {
                if ("checkbox".equals(question.getTypeId())) {
                  Long filledNumber = 0L;
                  // 查询出该问题的所有userResult
                  // 转换为question_value数组
                  List<String> questionValueList =
                      surUserResults.stream()
                          .map(SurUserResult::getQuestionValue)
                          .collect(Collectors.toList());
                  for (String value : questionValueList) {
                    if (value.contains(choice)) {
                      filledNumber++;
                    }
                  }
                  totalNum += filledNumber;
                }
              }
              for (String list : choiceLists) {
                Long filledNumber = 0L;
                // 查询该选项的有效填写人数
                // 构造选项json对象
                JSONObject choiceObj = new JSONObject();
                choiceObj.put("option", list);
                // 如果是多选题
                if ("checkbox".equals(question.getTypeId())) {
                  // 转换为question_value数组
                  List<String> questionValueList =
                      surUserResults.stream()
                          .map(SurUserResult::getQuestionValue)
                          .collect(Collectors.toList());
                  for (String value : questionValueList) {
                    if (value.contains(list)) {
                      filledNumber++;
                    }
                  }
                  // 如果有效填写人数为0
                  if (surUserResults.size() == 0) {
                    choiceObj.put("proportion", 0);
                  } else {
                    // 计算该选项的百分比
                    choiceObj.put(
                        "proportion",
                        NumUtils.getPercent(
                            Integer.parseInt(filledNumber.toString()),
                            Integer.parseInt(totalNum.toString())));
                  }
                } else {
                  // 如果是单选题
                  filledNumber =
                      userResultMapper.selectCount(
                          new LambdaQueryWrapper<SurUserResult>()
                              .eq(SurUserResult::getQuestionKey, question.getContent())
                              .eq(SurUserResult::getProjectId, projectId)
                              .eq(SurUserResult::getQuestionValue, list));
                  // 如果有效填写人数为0
                  if (surUserResults.size() == 0) {
                    choiceObj.put("proportion", 0);
                  } else {
                    // 计算该选项的百分比
                    choiceObj.put(
                        "proportion",
                        NumUtils.getPercent(
                            Integer.parseInt(filledNumber.toString()), surUserResults.size()));
                  }
                }
                choiceObj.put("subtotal", filledNumber);
                choiceList.add(choiceObj);
              }
            }
            questionMap.put("choiceList", choiceList);
            resultList.add(questionMap);
          });
    }
    projectCollectDto.setData(resultList);
    return projectCollectDto;
  }

  @Override
  public Boolean saveInvestigationResult(SaveCommonResultReq req) {
    // 查询出对应的问卷和项目对象
    SurProject project = projectService.getById(req.getProjectId());
    // 查询系统租户
    SysUser sysUser = sysUserService.getUserByName(project.getCreateBy());
    // 查询出对应的问卷对象
    SurSurveyProject survey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    SurUser user;
    // 判断是否有user
    if (Objects.isNull(req.getUser())) {
      // 随机生成一个用户
      user = new SurUser();
      user.setProjectId(req.getProjectId());
      if (sysUser != null) {
        user.setTenantId(sysUser.getRelTenantIds());
        user.setCreateBy(sysUser.getRealname());
      }
      user.setType(3);
      // 插入
      userMapper.insert(user);
      // 插入问卷与用户关系
      SurUserSurvey userSurvey = new SurUserSurvey();
      userSurvey.setUserId(user.getId());
      userSurvey.setPhone(user.getPhone());
      userSurvey.setSurveyId(project.getSurveyCurrentId());
      userSurvey.setProjectId(req.getProjectId());
      userSurvey.setStatus(req.getStatus());
      surUserSurveyMapper.insert(userSurvey);
    } else {
      user = req.getUser();
      // 查询问卷用户关系tId
      // 先删除原来的结果
      // 判断该用户有没有答题过
      SurResult surResult =
          resultMapper.selectOne(
              new LambdaQueryWrapper<SurResult>()
                  .eq(SurResult::getUserUid, user.getId())
                  .eq(SurResult::getProjectUid, req.getProjectId()));
      if (!Objects.isNull(surResult)) {
        return false;
      }
      //      resultMapper.delete(
      //          new LambdaQueryWrapper<SurResult>()
      //              .eq(SurResult::getProjectUid, req.getProjectId())
      //              .eq(SurResult::getUserUid, user.getId()));
      //      userResultMapper.delete(
      //          new LambdaQueryWrapper<SurUserResult>()
      //              .eq(SurUserResult::getProjectId, req.getProjectId())
      //              .eq(SurUserResult::getUserId, user.getId()));
      SurUserSurvey surUserSurvey =
          surUserSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurUserSurvey>()
                  .eq(SurUserSurvey::getSurveyId, project.getSurveyCurrentId())
                  .eq(SurUserSurvey::getUserId, user.getId())
                  .eq(SurUserSurvey::getProjectId, req.getProjectId()));
      if (surUserSurvey != null) {
        surUserSurvey.setStatus(req.getStatus());
        surUserSurveyMapper.updateById(surUserSurvey);
      }
    }
    // 先将整个问卷的答案存入到result表中
    SurResult result = new SurResult();
    result.setSurveyUid(project.getSurveyCurrentId());
    result.setProjectUid(req.getProjectId());
    result.setSurvey(survey.getSurName());
    result.setProject(project.getProjectName());
    result.setUserUid(user.getId());
    result.setAnswer(req.getAnswer());
    if (sysUser != null) {
      result.setTenantId(sysUser.getRelTenantIds());
    }
    resultMapper.insert(result);
    saveInvestigationUserResult(
        project, project.getSurveyCurrentId(), user, sysUser, req.getResult());
    user.setIsFinished(true);
    userMapper.updateById(user);
    return true;
  }

  @Override
  public Boolean saveEvaluationResult(SaveCommonResultReq req) {
    SurResult surResult =
        resultMapper.selectOne(
            new LambdaQueryWrapper<SurResult>()
                .eq(SurResult::getUserUid, req.getUser().getId())
                .eq(SurResult::getSurveyUid, req.getSurveyId())
                .eq(SurResult::getProjectUid, req.getProjectId()));
    if (!Objects.isNull(surResult)) {
      return false;
    }
    // 查询出对应的问卷和项目对象
    SurProject project = projectService.getById(req.getProjectId());
    // 查询问卷用户关系tId
    SurUserSurvey surUserSurvey =
        surUserSurveyMapper.selectOne(
            new LambdaQueryWrapper<SurUserSurvey>()
                .eq(SurUserSurvey::getSurveyId, req.getSurveyId())
                .eq(SurUserSurvey::getUserId, req.getUser().getId())
                .eq(SurUserSurvey::getProjectId, req.getProjectId()));
    if (surUserSurvey != null) {
      surUserSurvey.setStatus(req.getStatus());
      surUserSurveyMapper.updateById(surUserSurvey);
    }
    // 查询系统租户
    SysUser sysUser = sysUserService.getUserByName(project.getCreateBy());
    // 获取答题人对象
    SurUser user = req.getUser();
    // 查询出对应的问卷对象
    Survey survey = surveyMapper.selectById(req.getSurveyId());
    // 先将整个问卷的答案存入到result表中
    SurResult result = new SurResult();
    result.setSurveyUid(req.getSurveyId());
    result.setProjectUid(req.getProjectId());
    result.setSurvey(survey.getSurName());
    result.setProject(project.getProjectName());
    result.setUserUid(req.getUser().getId());
    result.setAnswer(req.getAnswer());
    if (sysUser != null) {
      result.setTenantId(sysUser.getRelTenantIds());
    }
    resultMapper.insert(result);
    saveEvaluationUserResult(project, req.getSurveyId(), user, sysUser, req.getResult());
    // 查询用户的所有问卷是否都已经完成
    List<SurUserSurvey> userSurveyList =
        surUserSurveyMapper.selectList(
            new LambdaQueryWrapper<SurUserSurvey>()
                .eq(SurUserSurvey::getUserId, req.getUser().getId())
                .eq(SurUserSurvey::getProjectId, req.getProjectId()));
    // 转换为状态数组
    List<Integer> statusList =
        userSurveyList.stream().map(SurUserSurvey::getStatus).collect(Collectors.toList());
    // 如果都为2则 更改项目完成状态
    if (!statusList.contains(0) && !statusList.contains(1)) {
      user.setIsFinished(true);
      userMapper.updateById(user);
    }
    return true;
  }

  @Override
  public Boolean save360Result(SaveCommonResultReq req) throws JeecgBootException {
    // 填写人
    SurUser user = req.getUser();
    // 判断该用户是否已经提交过
    List<SurUserResult> surUserResults =
        userResultMapper.selectList(
            new LambdaQueryWrapper<SurUserResult>()
                .eq(SurUserResult::getProjectId, req.getProjectId())
                .eq(SurUserResult::getUserId, user.getId())
                .eq(SurUserResult::getEvaluatedId, req.getEvaluator()));
    // 如果不为空 则说明已经提交过对该用户的评价
    if (!surUserResults.isEmpty()) {
      return false;
    }
    // 查询出对应的问卷和项目对象
    SurProject project = projectService.getById(req.getProjectId());
    // 查询问卷用户关系tId
    SurUserSurvey surUserSurvey =
        surUserSurveyMapper.selectOne(
            new LambdaQueryWrapper<SurUserSurvey>()
                .eq(SurUserSurvey::getSurveyId, project.getSurveyCurrentId())
                .eq(SurUserSurvey::getEvaluatedId, req.getEvaluator())
                .eq(SurUserSurvey::getUserId, user.getId())
                .eq(SurUserSurvey::getProjectId, req.getProjectId()));
    surUserSurvey.setStatus(req.getStatus());
    surUserSurveyMapper.updateById(surUserSurvey);
    // 查询系统租户
    SysUser sysUser = sysUserService.getUserByName(project.getCreateBy());
    // 查询出对应的问卷对象
    SurSurveyProject survey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    // 查询出evalutar对象
    SurEvaluationUser evaluationUser =
        evaluationUserMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationUser>()
                .eq(SurEvaluationUser::getUserId, req.getEvaluator())
                .eq(SurEvaluationUser::getEvaluatorId, user.getId())
                .eq(SurEvaluationUser::getProjectId, req.getProjectId()));
    if (evaluationUser == null) {
      throw new JeecgBootException("评价人与被评价人关系错误！");
    }
    // 先将整个问卷的答案存入到result表中
    SurResult result = new SurResult();
    result.setSurveyUid(project.getSurveyCurrentId());
    result.setProjectUid(req.getProjectId());
    result.setSurvey(survey.getSurName());
    result.setProject(project.getProjectName());
    result.setUserUid(user.getId());
    result.setEvaluatedId(req.getEvaluator());
    result.setAnswer(req.getAnswer());
    if (sysUser != null) {
      result.setTenantId(sysUser.getRelTenantIds());
    }
    resultMapper.insert(result);
    save360UserResult(
        project, project.getSurveyCurrentId(), user, evaluationUser, sysUser, req.getResult());
    user.setIsFinished(true);
    userMapper.updateById(user);
    return true;
  }

  @Override
  public Boolean add360User(Add360UserReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    } else {
      // 过滤掉名字中的重复值和空字符串
      List<String> list =
          req.getNames().stream()
              .distinct()
              .filter(s -> !StringUtils.isEmpty(s))
              .collect(Collectors.toList());
      // 遍历名字数组
      list.forEach(
          name -> {
            // 按中文逗号拆分字符串
            String[] split = name.split("，");
            String userName = split[0];
            String phone = split[1];
            SurUser user = new SurUser();
            user.setProjectId(req.getId());
            user.setType(1);
            user.setName(userName);
            user.setPhone(phone);
            // 查找有没有同名的
            List<SurUser> surUserList =
                userMapper.selectList(
                    new LambdaQueryWrapper<SurUser>()
                        .eq(SurUser::getName, userName)
                        .eq(SurUser::getProjectId, req.getId()));
            if (!surUserList.isEmpty()) {
              return;
            }
            userMapper.insert(user);
          });
    }
    return true;
  }

  @Override
  public Boolean remove360User(Add360UserReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    } else {
      // 参数中的names数组为要保留的数组 不在数组内的删掉
      // 查询项目之前的名字数组
      List<SurUser> old =
          userMapper.selectList(
              new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
      // 过滤掉名字中的重复值和空字符串
      List<String> list =
          req.getNames().stream()
              .distinct()
              .filter(s -> !StringUtils.isEmpty(s))
              .collect(Collectors.toList());
      // 要删除的人的数组
      List<SurUser> remaining =
          old.stream()
              .filter(userPerson -> !list.contains(userPerson.getName()))
              .collect(Collectors.toList());
      //            project.setSelectNumber(project.getSelectNumber() - remaining.size());
      remaining.forEach(
          userPerson -> {
            // 被评价人id
            String userId = userPerson.getId();
            // 查询被评价人
            SurEvaluationRelationship evaluationRelationship =
                relationshipMapper.selectOne(
                    new LambdaQueryWrapper<SurEvaluationRelationship>()
                        .eq(SurEvaluationRelationship::getUserId, userId));
            // 被删掉的是被评价人
            if (evaluationRelationship != null) {
              // 查询出该有关被评价者的result
              List<SurResult> resultList =
                  resultMapper.selectList(
                      new LambdaQueryWrapper<SurResult>()
                          .eq(SurResult::getEvaluatedId, userId)
                          .eq(SurResult::getProjectUid, req.getId()));
              project.setCollectNumber(project.getCollectNumber() - resultList.size());
              projectService.updateById(project);
              // 删除用户问卷关系
              surUserSurveyMapper.delete(
                  new LambdaQueryWrapper<SurUserSurvey>()
                      .eq(SurUserSurvey::getEvaluatedId, userId));
              // 删除被评价人
              relationshipMapper.deleteById(evaluationRelationship.getId());
              // 删除评价人
              evaluationUserMapper.delete(
                  new LambdaQueryWrapper<SurEvaluationUser>()
                      .eq(SurEvaluationUser::getUserId, userId));
              // 删除被评价人
              evaluationUserMapper.delete(
                  new LambdaQueryWrapper<SurEvaluationUser>()
                      .eq(SurEvaluationUser::getEvaluatorId, userId));
              // 删除result
              resultMapper.delete(
                  new LambdaQueryWrapper<SurResult>().eq(SurResult::getEvaluatedId, userId));
              // 删除userResult
              userResultMapper.delete(
                  new LambdaQueryWrapper<SurUserResult>()
                      .eq(SurUserResult::getEvaluatedId, userId));
            } else {
              // 删除用户问卷关系
              surUserSurveyMapper.delete(
                  new LambdaQueryWrapper<SurUserSurvey>().eq(SurUserSurvey::getUserId, userId));
              // 如果删掉的是评价人
              // 查询出该有关评价者的result
              List<SurResult> resultList =
                  resultMapper.selectList(
                      new LambdaQueryWrapper<SurResult>()
                          .eq(SurResult::getUserUid, userId)
                          .eq(SurResult::getProjectUid, req.getId()));
              project.setCollectNumber(project.getCollectNumber() - resultList.size());
              projectService.updateById(project);
              // 删除result
              resultMapper.delete(
                  new LambdaQueryWrapper<SurResult>().eq(SurResult::getUserUid, userId));
              // 删除userResult
              userResultMapper.delete(
                  new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getUserId, userId));
              // 查询他评价的人
              List<SurEvaluationUser> evaluationUsers =
                  evaluationUserMapper.selectList(
                      new LambdaQueryWrapper<SurEvaluationUser>()
                          .eq(SurEvaluationUser::getEvaluatorId, userId));
              evaluationUsers.forEach(
                  evaluationUser -> {
                    // 他评价的人
                    SurEvaluationRelationship relationship =
                        relationshipMapper.selectOne(
                            new LambdaQueryWrapper<SurEvaluationRelationship>()
                                .eq(
                                    SurEvaluationRelationship::getUserId,
                                    evaluationUser.getUserId()));
                    List<String> superiorId = relationship.getSuperiorId();
                    List<String> colleagueId = relationship.getColleagueId();
                    List<String> subordinateId = relationship.getSubordinateId();
                    switch (evaluationUser.getEvaluatorLevel()) {
                      case 1:
                        superiorId.remove(userId);
                        relationship.setSuperiorId(superiorId);
                        break;
                      case 2:
                        colleagueId.remove(userId);
                        relationship.setColleagueId(colleagueId);
                        break;
                      case 3:
                        subordinateId.remove(userId);
                        relationship.setSubordinateId(subordinateId);
                        break;
                    }
                    relationshipMapper.updateById(relationship);
                    // 删除这条数据
                    evaluationUserMapper.deleteById(evaluationUser);
                  });
            }
            userMapper.deleteById(userId);
          });
    }
    return true;
  }

  @Override
  public JudgeDto judge360User(JudgeReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return null;
    }
    // 根据项目id查询用户
    List<SurUser> userList =
        userMapper.selectList(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getProjectId()));
    // 将用户列表转换为手机号
    List<String> phoneList = userList.stream().map(SurUser::getPhone).collect(Collectors.toList());
    // 查询问卷
    SurSurveyProject survey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    JudgeDto judgeDto = new JudgeDto();
    List<AssessmentUserDto> assessmentUserDtoList = new ArrayList<>();
    if (phoneList.contains(req.getPhone())) {
      // 查询出该用户
      //            SurUser one = userService.getOne(new
      // LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId,
      // req.getProjectId()).likeLeft(SurUser::getPhone, req.getPhoneSuffix()));
      SurUser one =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, req.getProjectId())
                  .eq(SurUser::getPhone, req.getPhone()));
      List<SurEvaluationUser> surEvaluationUserList =
          evaluationUserMapper.selectList(
              new LambdaQueryWrapper<SurEvaluationUser>()
                  .eq(SurEvaluationUser::getProjectId, req.getProjectId())
                  .eq(SurEvaluationUser::getEvaluatorId, one.getId()));
      judgeDto.setUser(one);
      // 查询被评价人
      surEvaluationUserList.stream()
          .map(surEvaluationUser -> userMapper.selectById(surEvaluationUser.getUserId()))
          .forEach(
              user -> {
                AssessmentUserDto assessmentUserDto = new AssessmentUserDto();
                assessmentUserDto.setUserId(user.getId());
                assessmentUserDto.setName(user.getName());
                assessmentUserDto.setPhone(user.getPhone());
                assessmentUserDto.setSurveyId(survey.getId());
                assessmentUserDto.setSurName(survey.getSurName());
                assessmentUserDto.setSurContent(survey.getSurContent());
                assessmentUserDto.setJsonPreview(survey.getJsonPreview());
                // 查询用户和问卷关系
                SurUserSurvey userSurvey =
                    surUserSurveyMapper.selectOne(
                        new LambdaQueryWrapper<SurUserSurvey>()
                            .eq(SurUserSurvey::getUserId, one.getId())
                            .eq(SurUserSurvey::getEvaluatedId, user.getId())
                            .eq(SurUserSurvey::getSurveyId, survey.getId()));
                if (userSurvey != null) {
                  assessmentUserDto.setStatus(userSurvey.getStatus());
                }
                assessmentUserDtoList.add(assessmentUserDto);
              });
      judgeDto.setEvaluator(assessmentUserDtoList);
      return judgeDto;
    }
    return null;
  }

  @Override
  public List<SurUser> get360UserList(String projectId) {
    return userMapper.selectList(
        new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, projectId));
  }

  @Override
  public Boolean setLevelEvaluationRelationship(EvaluationRelSetReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return false;
    } else {
      project.setEvaluationType(1);
      projectService.updateById(project);
      // 创建被评价人对象
      // 查询用户对象
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, req.getEvaluator()));
      SurEvaluationRelationship evaluationRelationship =
          SurEvaluationRelationship.builder()
              .userId(req.getEvaluator())
              .userName(user.getName())
              .isSelf(req.getIsSelf())
              .type(1)
              .surveyId(project.getSurveyCurrentId())
              .projectId(req.getProjectId())
              .build();
      // 上级id数组
      evaluationRelationship.setSuperiorId(req.getSuperior());
      // 同事id数组
      evaluationRelationship.setColleagueId(req.getColleague());
      // 下级id数组
      evaluationRelationship.setSubordinateId(req.getSubordinate());
      // 保存关系
      relationshipMapper.insert(evaluationRelationship);
      // 如果是自评 则手动创建自评
      if (req.getIsSelf()) {
        SurEvaluationUser self = new SurEvaluationUser();
        self.setProjectId(req.getProjectId());
        self.setUserId(req.getEvaluator());
        self.setUserName(user.getName());
        self.setEvaluatorId(req.getEvaluator());
        self.setEvaluatorName(user.getName());
        self.setEvaluatorLevel(0);
        evaluationUserMapper.insert(self);
        // 创建问卷和用户关系
        SurUserSurvey surUserSurvey = new SurUserSurvey();
        // 被评价人id
        surUserSurvey.setEvaluatedId(req.getEvaluator());
        surUserSurvey.setSurveyId(project.getSurveyCurrentId());
        surUserSurvey.setProjectId(req.getProjectId());
        surUserSurvey.setUserId(user.getId());
        surUserSurvey.setPhone(user.getPhone());
        surUserSurveyMapper.insert(surUserSurvey);
      }
      // 遍历上级id数组
      if (req.getSuperior() != null) {
        req.getSuperior()
            .forEach(
                superiorId -> {
                  // 查询评价人对象
                  SurUser evaluator =
                      userMapper.selectOne(
                          new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, superiorId));
                  // 设置问卷用户关系
                  // 创建问卷和用户关系
                  SurUserSurvey userSurvey = new SurUserSurvey();
                  // 被评价人id
                  userSurvey.setEvaluatedId(req.getEvaluator());
                  userSurvey.setSurveyId(project.getSurveyCurrentId());
                  userSurvey.setProjectId(req.getProjectId());
                  userSurvey.setUserId(superiorId);
                  userSurvey.setPhone(evaluator.getPhone());
                  surUserSurveyMapper.insert(userSurvey);
                  // 上级评价人对象
                  SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                  userEvaluationUser.setProjectId(req.getProjectId());
                  // 评价人id
                  userEvaluationUser.setUserId(req.getEvaluator());
                  userEvaluationUser.setUserName(user.getName());
                  // 评价人id
                  userEvaluationUser.setEvaluatorId(superiorId);
                  userEvaluationUser.setEvaluatorName(evaluator.getName());
                  userEvaluationUser.setEvaluatorLevel(1);
                  // 插入数据库
                  evaluationUserMapper.insert(userEvaluationUser);
                });
      }
      // 遍历同事id数组
      if (req.getColleague() != null) {
        req.getColleague()
            .forEach(
                colleagueId -> {
                  // 查询评价人对象
                  SurUser evaluator =
                      userMapper.selectOne(
                          new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, colleagueId));
                  // 设置问卷用户关系
                  // 创建问卷和用户关系
                  SurUserSurvey userSurvey = new SurUserSurvey();
                  // 被评价人id
                  userSurvey.setEvaluatedId(req.getEvaluator());
                  userSurvey.setSurveyId(project.getSurveyCurrentId());
                  userSurvey.setProjectId(req.getProjectId());
                  userSurvey.setUserId(colleagueId);
                  userSurvey.setPhone(evaluator.getPhone());
                  surUserSurveyMapper.insert(userSurvey);
                  // 上级评价人对象
                  SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                  userEvaluationUser.setProjectId(req.getProjectId());
                  // 评价人id
                  userEvaluationUser.setUserId(req.getEvaluator());
                  userEvaluationUser.setUserName(user.getName());
                  // 评价人id
                  userEvaluationUser.setEvaluatorId(colleagueId);
                  userEvaluationUser.setEvaluatorName(evaluator.getName());
                  userEvaluationUser.setEvaluatorLevel(2);
                  // 插入数据库
                  evaluationUserMapper.insert(userEvaluationUser);
                });
      }
      // 遍历下级id数组
      if (req.getSubordinate() != null) {
        req.getSubordinate()
            .forEach(
                subordinateId -> {
                  // 查询评价人对象
                  SurUser evaluator =
                      userMapper.selectOne(
                          new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, subordinateId));
                  // 设置问卷用户关系
                  // 创建问卷和用户关系
                  SurUserSurvey userSurvey = new SurUserSurvey();
                  // 被评价人id
                  userSurvey.setEvaluatedId(req.getEvaluator());
                  userSurvey.setSurveyId(project.getSurveyCurrentId());
                  userSurvey.setProjectId(req.getProjectId());
                  userSurvey.setUserId(subordinateId);
                  userSurvey.setPhone(evaluator.getPhone());
                  surUserSurveyMapper.insert(userSurvey);
                  // 上级评价人对象
                  SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                  userEvaluationUser.setProjectId(req.getProjectId());
                  // 评价人id
                  userEvaluationUser.setUserId(req.getEvaluator());
                  userEvaluationUser.setUserName(user.getName());
                  // 评价人id
                  userEvaluationUser.setEvaluatorId(subordinateId);
                  userEvaluationUser.setEvaluatorName(evaluator.getName());
                  userEvaluationUser.setEvaluatorLevel(3);
                  // 插入数据库
                  evaluationUserMapper.insert(userEvaluationUser);
                });
      }
      return true;
    }
  }

  @Override
  public Boolean setCommonEvaluationRelationship(EvaluationRelSetReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return false;
    } else {
      project.setEvaluationType(0);
      projectService.updateById(project);
      // 创建被评价人对象
      // 查询用户对象
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, req.getEvaluator()));
      SurEvaluationRelationship evaluationRelationship =
          SurEvaluationRelationship.builder()
              .userId(req.getEvaluator())
              .userName(user.getName())
              .type(0)
              .surveyId(project.getSurveyCurrentId())
              .projectId(req.getProjectId())
              .build();
      // 保存关系
      relationshipMapper.insert(evaluationRelationship);
      // 遍历评价人id数组
      req.getEvaluated()
          .forEach(
              evaluatedId -> {
                // 查询评价人对象
                SurUser evaluator =
                    userMapper.selectOne(
                        new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, evaluatedId));
                // 上级评价人对象
                SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                userEvaluationUser.setProjectId(req.getProjectId());
                // 评价人id
                userEvaluationUser.setUserId(req.getEvaluator());
                userEvaluationUser.setUserName(user.getName());
                // 评价人id
                userEvaluationUser.setEvaluatorId(evaluatedId);
                userEvaluationUser.setEvaluatorName(evaluator.getName());
                // 插入数据库
                evaluationUserMapper.insert(userEvaluationUser);
              });
      return true;
    }
  }

  @Override
  public Boolean removeEvaluationRelationship(String id) {
    // 查询该被评级人
    SurEvaluationRelationship relationship =
        relationshipMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationRelationship>()
                .eq(SurEvaluationRelationship::getId, id));
    if (relationship == null) {
      return false;
    } else {
      // 查询项目对象
      SurProject project = projectService.getById(relationship.getProjectId());
      List<SurResult> surResults =
          resultMapper.selectList(
              new LambdaQueryWrapper<SurResult>()
                  .eq(SurResult::getEvaluatedId, relationship.getUserId()));
      project.setCollectNumber(project.getCollectNumber() - surResults.size());
      projectService.updateById(project);
      // 删除答卷
      resultMapper.delete(
          new LambdaQueryWrapper<SurResult>()
              .eq(SurResult::getEvaluatedId, relationship.getUserId()));
      userResultMapper.delete(
          new LambdaQueryWrapper<SurUserResult>()
              .eq(SurUserResult::getEvaluatedId, relationship.getUserId()));
      // 查询评价人
      List<SurEvaluationUser> evaluationUsers =
          evaluationUserMapper.selectList(
              new LambdaQueryWrapper<SurEvaluationUser>()
                  .eq(SurEvaluationUser::getProjectId, relationship.getProjectId())
                  .eq(SurEvaluationUser::getUserId, relationship.getUserId()));
      evaluationUsers.forEach(
          evaluationUser -> {
            // 删除用户问卷关系
            surUserSurveyMapper.delete(
                new LambdaQueryWrapper<SurUserSurvey>()
                    .eq(SurUserSurvey::getProjectId, relationship.getProjectId())
                    .eq(SurUserSurvey::getUserId, evaluationUser.getEvaluatorId())
                    .eq(SurUserSurvey::getEvaluatedId, relationship.getUserId()));
          });
      // 删除关系
      relationshipMapper.deleteById(id);
      // 删除评价人
      evaluationUserMapper.delete(
          new LambdaQueryWrapper<SurEvaluationUser>()
              .eq(SurEvaluationUser::getUserId, relationship.getUserId()));
      return true;
    }
  }

  @Override
  public Boolean updateEvaluation(EvaluationRelSetReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getProjectId());
    if (project == null) {
      return false;
    }
    // 查询被评价人对象
    SurEvaluationRelationship relationship =
        relationshipMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationRelationship>()
                .eq(SurEvaluationRelationship::getId, req.getId()));
    SurUser user = userMapper.selectById(req.getEvaluator());
    if (relationship == null) {
      return false;
    } else {
      // 判断被评价人是否修改
      if (!(req.getEvaluator() == relationship.getUserId())) {
        // 如果被评价人被修改
        String oldId = relationship.getUserId();
        relationship.setIsSelf(req.getIsSelf());
        relationship.setUserId(req.getEvaluator());
        relationship.setUserName(user.getName());
        relationship.setSuperiorId(req.getSuperior());
        relationship.setColleagueId(req.getColleague());
        relationship.setSubordinateId(req.getSubordinate());
        // 保存主表
        relationshipMapper.updateById(relationship);
        // 删除评价人
        evaluationUserMapper.delete(
            new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, oldId));
        // 删除用户问卷关系
        surUserSurveyMapper.delete(
            new LambdaQueryWrapper<SurUserSurvey>()
                .eq(SurUserSurvey::getProjectId, relationship.getProjectId())
                .eq(SurUserSurvey::getEvaluatedId, oldId));
        // 如果是自评 则手动创建自评
        if (req.getIsSelf()) {
          SurEvaluationUser self = new SurEvaluationUser();
          self.setProjectId(req.getProjectId());
          self.setUserId(req.getEvaluator());
          self.setUserName(user.getName());
          self.setEvaluatorId(req.getEvaluator());
          self.setEvaluatorName(user.getName());
          self.setEvaluatorLevel(0);
          evaluationUserMapper.insert(self);
          // 创建问卷和用户关系
          SurUserSurvey surUserSurvey = new SurUserSurvey();
          // 被评价人id
          surUserSurvey.setEvaluatedId(req.getEvaluator());
          surUserSurvey.setSurveyId(project.getSurveyCurrentId());
          surUserSurvey.setProjectId(req.getProjectId());
          surUserSurvey.setUserId(req.getEvaluator());
          surUserSurvey.setPhone(user.getPhone());
          surUserSurveyMapper.insert(surUserSurvey);
        }
        // 遍历评价人id数组
        if (req.getEvaluated() != null) {
          req.getEvaluated()
              .forEach(
                  evaluatedId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, evaluatedId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(evaluatedId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(evaluatedId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getSuperior() != null) {
          // 遍历上级id数组
          req.getSuperior()
              .forEach(
                  superiorId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, superiorId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(superiorId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(1);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(superiorId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getColleague() != null) {
          // 遍历同事id数组
          req.getColleague()
              .forEach(
                  colleagueId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, colleagueId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(colleagueId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(2);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(colleagueId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getSubordinate() != null) {
          // 遍历下级id数组
          req.getSubordinate()
              .forEach(
                  subordinateId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, subordinateId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(subordinateId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(3);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(subordinateId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
      } else {
        relationship.setIsSelf(req.getIsSelf());
        relationship.setSuperiorId(req.getSuperior());
        relationship.setColleagueId(req.getColleague());
        relationship.setSubordinateId(req.getSubordinate());
        // 保存主表
        relationshipMapper.updateById(relationship);
        // 删除评价人
        evaluationUserMapper.delete(
            new LambdaQueryWrapper<SurEvaluationUser>()
                .eq(SurEvaluationUser::getUserId, relationship.getUserId()));
        // 删除问卷和用户关系
        surUserSurveyMapper.delete(
            new LambdaQueryWrapper<SurUserSurvey>()
                .eq(SurUserSurvey::getEvaluatedId, relationship.getUserId()));
        // 如果是自评 则手动创建自评
        if (req.getIsSelf()) {
          SurEvaluationUser self = new SurEvaluationUser();
          self.setProjectId(req.getProjectId());
          self.setUserId(req.getEvaluator());
          self.setUserName(user.getName());
          self.setEvaluatorId(req.getEvaluator());
          self.setEvaluatorName(user.getName());
          self.setEvaluatorLevel(0);
          evaluationUserMapper.insert(self);
          // 创建问卷和用户关系
          SurUserSurvey surUserSurvey = new SurUserSurvey();
          // 被评价人id
          surUserSurvey.setEvaluatedId(req.getEvaluator());
          surUserSurvey.setSurveyId(project.getSurveyCurrentId());
          surUserSurvey.setProjectId(req.getProjectId());
          surUserSurvey.setUserId(req.getEvaluator());
          surUserSurvey.setPhone(user.getPhone());
          surUserSurveyMapper.insert(surUserSurvey);
        }
        // 遍历评价人id数组
        if (req.getEvaluated() != null) {
          req.getEvaluated()
              .forEach(
                  evaluatedId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, evaluatedId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(evaluatedId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(evaluatedId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getSuperior() != null) {
          // 遍历上级id数组
          req.getSuperior()
              .forEach(
                  superiorId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, superiorId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(superiorId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(1);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(superiorId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getColleague() != null) {
          // 遍历同事id数组
          req.getColleague()
              .forEach(
                  colleagueId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, colleagueId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(colleagueId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(2);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(colleagueId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
        if (req.getSubordinate() != null) {
          // 遍历下级id数组
          req.getSubordinate()
              .forEach(
                  subordinateId -> {
                    // 查询评价人对象
                    SurUser evaluator =
                        userMapper.selectOne(
                            new LambdaQueryWrapper<SurUser>().eq(SurUser::getId, subordinateId));
                    // 上级评价人对象
                    SurEvaluationUser userEvaluationUser = new SurEvaluationUser();
                    userEvaluationUser.setProjectId(req.getProjectId());
                    // 评价人id
                    userEvaluationUser.setUserId(req.getEvaluator());
                    userEvaluationUser.setUserName(relationship.getUserName());
                    // 评价人id
                    userEvaluationUser.setEvaluatorId(subordinateId);
                    userEvaluationUser.setEvaluatorName(evaluator.getName());
                    userEvaluationUser.setEvaluatorLevel(3);
                    // 插入数据库
                    evaluationUserMapper.insert(userEvaluationUser);
                    // 创建问卷和用户关系
                    SurUserSurvey userSurvey = new SurUserSurvey();
                    // 被评价人id
                    userSurvey.setEvaluatedId(req.getEvaluator());
                    userSurvey.setSurveyId(project.getSurveyCurrentId());
                    userSurvey.setProjectId(req.getProjectId());
                    userSurvey.setUserId(subordinateId);
                    userSurvey.setPhone(evaluator.getPhone());
                    surUserSurveyMapper.insert(userSurvey);
                  });
        }
      }
      return true;
    }
  }

  @Override
  public List<EvaluationRelationDto> getEvaluationRelationship(String projectId) {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return new ArrayList<>();
    }
    if (project.getEvaluationType() == null) {
      return new ArrayList<>();
    }
    List<EvaluationRelationDto> resultList = new ArrayList<>();
    // 查询被评价人对象
    List<SurEvaluationRelationship> relationshipList =
        relationshipMapper.selectList(
            new LambdaQueryWrapper<SurEvaluationRelationship>()
                .eq(SurEvaluationRelationship::getProjectId, projectId));

    // 如果是没有上下级关系的项目
    if (project.getEvaluationType() == 0) {
      // 遍历被评价人对象
      relationshipList.forEach(
          relationship -> {
            EvaluationRelationDto evaluationRelationDto = new EvaluationRelationDto();
            evaluationRelationDto.setIsSelf(relationship.getIsSelf());
            BeanUtils.copyProperties(relationship, evaluationRelationDto);
            // 判断是否是自评
            if (relationship.getIsSelf()) {
              // 如果是自评  查询该对象
              SurEvaluationUser self =
                  evaluationUserMapper.selectOne(
                      new LambdaQueryWrapper<SurEvaluationUser>()
                          .eq(SurEvaluationUser::getEvaluatorId, relationship.getUserId())
                          .eq(SurEvaluationUser::getUserId, relationship.getUserId())
                          .eq(SurEvaluationUser::getProjectId, projectId));
              evaluationRelationDto.setSelf(self);
            }
            // 查询评价人对象
            List<SurEvaluationUser> evaluationUserList =
                evaluationUserMapper.selectList(
                    new LambdaQueryWrapper<SurEvaluationUser>()
                        .eq(SurEvaluationUser::getUserId, relationship.getUserId()));
            evaluationRelationDto.setEvaluatedList(evaluationUserList);
            resultList.add(evaluationRelationDto);
          });
    }
    // 如果是有上下级关系的项目
    else {
      // 遍历被评价人对象
      relationshipList.forEach(
          relationship -> {
            EvaluationRelationDto evaluationRelationDto = new EvaluationRelationDto();
            BeanUtils.copyProperties(relationship, evaluationRelationDto);
            // 判断是否是自评
            if (relationship.getIsSelf()) {
              // 如果是自评  查询该对象
              SurEvaluationUser self =
                  evaluationUserMapper.selectOne(
                      new LambdaQueryWrapper<SurEvaluationUser>()
                          .eq(SurEvaluationUser::getEvaluatorId, relationship.getUserId())
                          .eq(SurEvaluationUser::getUserId, relationship.getUserId())
                          .eq(SurEvaluationUser::getProjectId, projectId));
              evaluationRelationDto.setSelf(self);
            }
            // 查询上级对象
            List<SurEvaluationUser> superiorList =
                evaluationUserMapper.selectList(
                    new LambdaQueryWrapper<SurEvaluationUser>()
                        .eq(SurEvaluationUser::getUserId, relationship.getUserId())
                        .eq(SurEvaluationUser::getEvaluatorLevel, 1));
            evaluationRelationDto.setSuperiorList(superiorList);
            // 查询同事对象
            List<SurEvaluationUser> colleagueList =
                evaluationUserMapper.selectList(
                    new LambdaQueryWrapper<SurEvaluationUser>()
                        .eq(SurEvaluationUser::getUserId, relationship.getUserId())
                        .eq(SurEvaluationUser::getEvaluatorLevel, 2));
            evaluationRelationDto.setColleagueList(colleagueList);
            // 查询下级对象
            List<SurEvaluationUser> subordinateList =
                evaluationUserMapper.selectList(
                    new LambdaQueryWrapper<SurEvaluationUser>()
                        .eq(SurEvaluationUser::getUserId, relationship.getUserId())
                        .eq(SurEvaluationUser::getEvaluatorLevel, 3));
            evaluationRelationDto.setSubordinateList(subordinateList);
            resultList.add(evaluationRelationDto);
          });
    }
    return resultList;
  }

  @Override
  public SurSurveyProject copySurvey(SurveyCopyReq req) {
    // 查询问卷对象
    SurSurveyProject userSurvey = userSurveyMapper.selectById(req.getId());
    if (userSurvey == null) {
      return null;
    }
    SurSurveyProject copy = copy(userSurvey, req.getSurveyName());
    return copy;
  }

  @Override
  public Boolean deleteSurvey(String id) {
    // 查询问卷对象
    SurSurveyProject userSurvey = userSurveyMapper.selectById(id);
    if (userSurvey == null) {
      return false;
    }
    // 删除题目
    userSurveyQuestionMapper.delete(
        new LambdaQueryWrapper<SurQuestionProject>().eq(SurQuestionProject::getSurveyUid, id));
    // 删除选项
    userSurveyChoiceMapper.delete(
        new LambdaQueryWrapper<SurQuestionChoiceProject>()
            .eq(SurQuestionChoiceProject::getSurveyUid, id));
    // 删除问卷
    userSurveyMapper.deleteById(id);
    return true;
  }

  @Override
  public SurProject copyProject(SurveyCopyReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return null;
    }
    // 复制项目对象
    SurProject copyProject = new SurProject();
    BeanUtils.copyProperties(project, copyProject);
    copyProject.setId(null);
    copyProject.setCreateTime(null);
    copyProject.setProjectName(req.getProjectName());
    copyProject.setUpdateTime(null);
    copyProject.setIsPublish(false);
    copyProject.setCollectNumber(0);
    copyProject.setTenantId(req.getTenantId());
    // 复制问卷
    SurSurveyProject resourceSurvey = userSurveyMapper.selectById(project.getSurveyCurrentId());
    if (resourceSurvey != null) {
      SurSurveyProject copy = copy(resourceSurvey, req.getProjectName());
      copyProject.setSurveyCurrentId(copy.getId());
    }
    projectService.save(copyProject);
    return copyProject;
  }

  @Override
  public SurEvaluationWeight getProjectWeight(String projectId) {
    return evaluationWeightMapper.selectOne(
        new LambdaQueryWrapper<SurEvaluationWeight>()
            .eq(SurEvaluationWeight::getProjectId, projectId));
  }

  @Override
  public Boolean setWeight(WeightSetReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    }
    // 先判断之前是否有权重
    SurEvaluationWeight oldWeight =
        evaluationWeightMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationWeight>()
                .eq(SurEvaluationWeight::getProjectId, req.getId()));
    if (oldWeight != null) {
      // 更新权重
      oldWeight.setType(req.getType());
      oldWeight.setSelfWeight(req.getSelf());
      oldWeight.setSuperiorWeight(req.getSuperior());
      oldWeight.setColleagueWeight(req.getColleague());
      oldWeight.setSubordinateWeight(req.getSubordinate());
      evaluationWeightMapper.updateById(oldWeight);
    } else {
      SurEvaluationWeight weight = new SurEvaluationWeight();
      weight.setType(req.getType());
      weight.setProjectId(req.getId());
      weight.setSelfWeight(req.getSelf());
      weight.setSuperiorWeight(req.getSuperior());
      weight.setSubordinateWeight(req.getSubordinate());
      weight.setColleagueWeight(req.getColleague());
      evaluationWeightMapper.insert(weight);
    }
    return true;
  }

  @Override
  public Boolean logicDeleteProject(String id) {
    // 查询项目对象
    SurProject project = projectService.getById(id);
    if (project == null) {
      return false;
    } else {
      project.setIsDel(true);
      projectService.updateById(project);
      return true;
    }
  }

  @Override
  public Page<SurProject> getBinProjects(PageReq req) {
    return projectService.page(
        new Page<>(req.getPageNum(), req.getPageSize()),
        new LambdaQueryWrapper<SurProject>().eq(SurProject::getIsDel, true));
  }

  @Override
  public Boolean cleanBinProject() {
    // 查询回收站项目
    List<SurProject> projectList =
        projectService.list(new LambdaQueryWrapper<SurProject>().eq(SurProject::getIsDel, true));
    // 遍历
    projectList.forEach(
        project -> {
          // 删除项目主表
          projectService.removeById(project);
          // 删除问卷
          SurSurveyProject survey =
              userSurveyMapper.selectOne(
                  new LambdaQueryWrapper<SurSurveyProject>()
                      .eq(SurSurveyProject::getId, project.getSurveyCurrentId()));
          if (survey != null) {
            userSurveyMapper.deleteById(survey.getId());
            // 删除问卷的问题和选项
            userSurveyQuestionMapper.delete(
                new LambdaQueryWrapper<SurQuestionProject>()
                    .eq(SurQuestionProject::getSurveyUid, survey.getId()));
            userSurveyChoiceMapper.delete(
                new LambdaQueryWrapper<SurQuestionChoiceProject>()
                    .eq(SurQuestionChoiceProject::getSurveyUid, survey.getId()));
            userSurveyMapper.deleteById(survey);
          }
          // 删除人
          userMapper.delete(
              new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, project.getId()));
          // 删除result和user_result
          resultMapper.delete(
              new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, project.getId()));
          userResultMapper.delete(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getProjectId, project.getId()));
          // 删除项目和问卷关系
          projectSurveyMapper.delete(
              new LambdaQueryWrapper<SurProjectSurvey>()
                  .eq(SurProjectSurvey::getProjectId, project.getId()));
          // 删除问卷和人关系
          surUserSurveyMapper.delete(
              new LambdaQueryWrapper<SurUserSurvey>()
                  .eq(SurUserSurvey::getProjectId, project.getId()));
          // 如果是360项目 则还要删除评价关系
          if ("360度评估".equals(project.getType())) {
            relationshipMapper.delete(
                new LambdaQueryWrapper<SurEvaluationRelationship>()
                    .eq(SurEvaluationRelationship::getProjectId, project.getId()));
            evaluationUserMapper.delete(
                new LambdaQueryWrapper<SurEvaluationUser>()
                    .eq(SurEvaluationUser::getProjectId, project.getId()));
            evaluationWeightMapper.delete(
                new LambdaQueryWrapper<SurEvaluationWeight>()
                    .eq(SurEvaluationWeight::getProjectId, project.getId()));
          }
        });
    return true;
  }

  @Override
  public Page<SurUser> getProjectUserList(PageReq req) {
    Page<SurUser> userPage =
        userMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    return userPage;
  }

  @Override
  public EvaluationProjectDto createEvaluationProject(CreateEvaluationProjectReq req) {
    SurProject project;
    EvaluationProjectDto evaluationProjectDto = new EvaluationProjectDto();
    List<Survey> surveyList = new ArrayList<>();
    // 查询项目原来的问卷id数组
    List<String> oldList =
        projectSurveyMapper
            .selectList(
                new LambdaQueryWrapper<SurProjectSurvey>()
                    .eq(SurProjectSurvey::getProjectId, req.getId()))
            .stream()
            .map(SurProjectSurvey::getSurveyId)
            .collect(Collectors.toList());
    List<String> newList = req.getSurvey();
    // 找出要被删掉的id数组
    List<String> deleteList =
        oldList.stream().filter(item -> !newList.contains(item)).collect(Collectors.toList());
    if (req.getId() != null) {
      // 更新项目
      project = projectService.getById(req.getId());
      //            project.setProjectName(req.getName());
      //            project.setType(req.getType());
      //            project.setContent(req.getContent());
      //            projectService.updateById(project);
      // 先删除之前的问卷和项目的关系
      projectSurveyMapper.delete(
          new LambdaQueryWrapper<SurProjectSurvey>()
              .eq(SurProjectSurvey::getProjectId, req.getId()));
      resultMapper.delete(
          new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, req.getId()));
      userResultMapper.delete(
          new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getId()));
      surUserSurveyMapper.delete(
          new LambdaQueryWrapper<SurUserSurvey>().eq(SurUserSurvey::getProjectId, req.getId()));
      userMapper.delete(new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
      if (!deleteList.isEmpty()) {
        project.setCollectNumber(0);
        projectService.updateById(project);
      }
      // 删除问卷和用户关系
      if (req.getSurvey() != null && !req.getSurvey().isEmpty()) {
        // 遍历问卷id
        List<String> surveyIdList = req.getSurvey();
        surveyIdList.forEach(
            id -> {
              SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
              surProjectSurvey.setProjectId(req.getId());
              surProjectSurvey.setSurveyId(id);
              projectSurveyMapper.insert(surProjectSurvey);
              // 查询问卷
              Survey survey = surveyMapper.selectById(id);
              surveyList.add(survey);
            });
      }
      evaluationProjectDto.setSurveyList(surveyList);
    } else {
      project = new SurProject();
      project.setProjectName(req.getName());
      project.setType(req.getType());
      project.setContent(req.getContent());
      project.setSelectNumber(0);
      project.setCollectNumber(0);
      project.setIsPublish(false);
      project.setIsDel(false);
      projectService.save(project);
      if (req.getSurvey() != null && !req.getSurvey().isEmpty()) {
        // 遍历问卷id
        List<String> surveyIdList = req.getSurvey();
        surveyIdList.forEach(
            id -> {
              SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
              surProjectSurvey.setProjectId(project.getId());
              surProjectSurvey.setSurveyId(id);
              projectSurveyMapper.insert(surProjectSurvey);
              // 查询问卷
              Survey survey = surveyMapper.selectById(id);
              surveyList.add(survey);
            });
      }
      evaluationProjectDto.setSurveyList(surveyList);
    }
    evaluationProjectDto.setProject(project);
    return evaluationProjectDto;
  }

  @Override
  public List<Survey> getEvaluationSurveyList(String projectId) {
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return null;
    }
    List<Survey> resultList = new ArrayList<>();
    List<SurProjectSurvey> surProjectSurveys =
        projectSurveyMapper.selectList(
            new LambdaQueryWrapper<SurProjectSurvey>()
                .eq(SurProjectSurvey::getProjectId, projectId));
    surProjectSurveys.forEach(
        surProjectSurvey -> {
          Survey survey = surveyMapper.selectById(surProjectSurvey.getSurveyId());
          resultList.add(survey);
        });
    return resultList;
  }

  @Override
  public PageResp<Survey> getSurveyTemplateList(ProjectAdvancedQueryReq req, String tenantId) {
    LambdaQueryWrapper<Survey> queryAllWrapper =
        new LambdaQueryWrapper<Survey>()
            .eq(Survey::getIsPublic, true)
            .eq(Survey::getIsUse, true)
            .eq(Survey::getType, req.getType());
    if (req.getName() != null && !StringUtils.isEmpty(req.getName())) {
      queryAllWrapper.like(Survey::getSurName, req.getName());
    }
    List<Survey> surveyList = surveyMapper.selectList(queryAllWrapper);
    // 对surveyList进行分页
    List<Survey> collect =
        surveyList.stream()
            .skip((req.getPageNum() - 1) * req.getPageSize())
            .limit(req.getPageSize())
            .collect(Collectors.toList());
    PageResp<Survey> surveyPageResp = new PageResp<>();
    surveyPageResp.setRecords(collect);
    surveyPageResp.setTotal(surveyList.size());
    return surveyPageResp;
  }
  @Override
  public PageResp<Survey> getExclusiveSurveyTemplateList(ProjectAdvancedQueryReq req, String tenantId) {
    // 专属该租户的问卷
    List<Survey> exclusive = new ArrayList<>();
    // 查询问卷租户关系
    List<SurSurveyTenant> surSurveyTenantList =
            surSurveyTenantMapper.selectList(
                    new LambdaQueryWrapper<SurSurveyTenant>().eq(SurSurveyTenant::getTenantId, tenantId));
    //取出所有的问卷id
    List<String> surveyIds =
            surSurveyTenantList.stream().map(SurSurveyTenant::getSurveyId).distinct().collect(Collectors.toList());
    // 查询用户自己的问卷表
    List<SurSurveyProject> surSurveyProjects =
            userSurveyMapper.selectList(
                    new LambdaQueryWrapper<SurSurveyProject>().eq(SurSurveyProject::getTenantId, tenantId));
    // 遍历用户自己的问卷,同时取出所有购买的问卷
    if (req.getType().equals("我的") && !StringUtils.isEmpty(req.getType())) {
      for (SurSurveyProject surSurveyProject : surSurveyProjects){
        if (surveyIds.contains(surSurveyProject.getSrcId())){
          Survey survey = new Survey();
          BeanUtils.copyProperties(surSurveyProject,survey);
          exclusive.add(survey);
        }
      }
    }
    // 过滤掉exclusive里面的null
    List<Survey> exclusiveList =
            exclusive.stream().filter(Objects::nonNull).collect(Collectors.toList());
    // 对exclusiveList进行分页
    List<Survey> collect =
            exclusiveList.stream()
                    .skip((req.getPageNum() - 1) * req.getPageSize())
                    .limit(req.getPageSize())
                    .collect(Collectors.toList());
    PageResp<Survey> surveyPageResp = new PageResp<>();
    surveyPageResp.setRecords(collect);
    surveyPageResp.setTotal(exclusiveList.size());
    return surveyPageResp;
  }

  @Override
  public Boolean getTenantAndSurveyRelation(PurchaseReq req, String tenantId) {
    SurSurveyTenant surSurveyTenant = surSurveyTenantMapper.selectOne(
            new LambdaQueryWrapper<SurSurveyTenant>()
                    .eq(SurSurveyTenant::getTenantId, tenantId)
                    .eq(SurSurveyTenant::getSurveyId, req.getSurveyId()));
    return surSurveyTenant==null;
  }

  @Override
  public Boolean purchaseByPoint(PurchaseReq req, String tenantId) {
    // 取得用户对象
    SysTenant tenant = sysTenantMapper.selectById(tenantId);
    // 取得问卷模板对象
    Survey survey = surveyMapper.selectById(req.getSurveyId());
    // 通过模板对象的租户id拿到问卷模板制作者
    SysTenant maker = sysTenantMapper.selectById(survey.getTenantId());
    // 取用户对象
    String userName = sysUserService.getUserNameByTenantId(tenantId);
    SysUser user = sysUserService.getUserByName(userName);
    // 取到maker用户对象
    String makerName = sysUserService.getUserNameByTenantId(survey.getTenantId());
    SysUser userMaker = sysUserService.getUserByName(makerName);
    // 判断租户积分是否足够
    if(tenant.getIntegral()<survey.getCredit()){
      return false;
    }
    // 判断用户积分是否足够
    if (user.getIntegral()<survey.getCredit()){
      return false;
    }
    // 取得问卷问题
    List<SurQuestion> surQuestions = surQuestionMapper.selectList(new LambdaQueryWrapper<SurQuestion>()
            .eq(SurQuestion::getSurveyUid, req.getSurveyId()));

    // 取得问题选项
    List<SurQuestionChoice> surQuestionChoices = surQuestionChoiceMapper.selectList(new LambdaQueryWrapper<SurQuestionChoice>()
            .eq(SurQuestionChoice::getSurveyUid, req.getSurveyId()));

    // 问卷模板复制到用户问卷模板
    SurSurveyProject surSurveyProject = new SurSurveyProject();
    surSurveyProject.setType(survey.getType())
            .setSurName(survey.getSurName())
            .setSurContent(survey.getSurContent())
            .setJsonPreview(survey.getJsonPreview())
            .setSysOrgCode(survey.getOrgUid())
            .setTenantId(tenantId)
            .setSrcId(survey.getId());
    userSurveyMapper.insert(surSurveyProject);

    // 问卷问题复制到用户问卷问题
    //获取问卷id（通过迭代器获取）
    List<SurSurveyProject> recentSurSurveyProjects = userSurveyMapper.selectList(new LambdaQueryWrapper<SurSurveyProject>()
            .eq(SurSurveyProject::getTenantId,tenantId));
    Iterator<SurSurveyProject> iter = recentSurSurveyProjects.iterator();
    SurSurveyProject recentSurSurveyProject =new SurSurveyProject();
    while(iter.hasNext()){
      recentSurSurveyProject = iter.next();
    }

    for (SurQuestion surQuestion : surQuestions) {
      SurQuestionProject surQuestionProject = new SurQuestionProject();
      surQuestionProject.setSurveyUid(recentSurSurveyProject.getId())
              .setContent(surQuestion.getContent())
              .setIsParent(surQuestion.getIsParent())
              .setParentId(surQuestion.getParentId())
              .setParentContent(surQuestion.getParentContent())
              .setSysOrgCode(surQuestion.getOrgUid())
              .setTitle(surQuestion.getTitle())
              .setTypeId(surQuestion.getTypeId())
              .setDimensionId(surQuestion.getDimensionId())
              .setRequired(surQuestion.getRequired())
              .setTenantId(recentSurSurveyProject.getTenantId());
      userSurveyQuestionMapper.insert(surQuestionProject);
    }

    // 问题选项复制到用户问题选项
    List<SurQuestionProject> recentSurQuestionProjects = userSurveyQuestionMapper.selectList(new LambdaQueryWrapper<SurQuestionProject>()
            .eq(SurQuestionProject::getSurveyUid,recentSurSurveyProject.getId()));
    String [] questionIds = new String[1000];
    int index = 0;
    for (SurQuestionProject recentSurQuestionProject: recentSurQuestionProjects) {
      questionIds[index]=recentSurQuestionProject.getId();
      index = index+1;
    }

    int i =0;
    for (SurQuestionChoice surQuestionChoice:surQuestionChoices) {
      SurQuestionChoiceProject surQuestionChoiceProject = new SurQuestionChoiceProject();
      surQuestionChoiceProject.setSurveyUid(recentSurSurveyProject.getId())
              .setContent(surQuestionChoice.getContent())
              .setBasicScore(surQuestionChoice.getBasicScore())
              .setRequired(surQuestionChoice.getRequired())
              .setSysOrgCode(surQuestionChoice.getOrgUid())
              .setTenantId(recentSurSurveyProject.getTenantId())
              .setQuestionId(questionIds[i]);
      userSurveyChoiceMapper.insert(surQuestionChoiceProject);
      i=i+1;
    }

    // 添加租户和问卷关系
    SurSurveyTenant surSurveyTenant = new SurSurveyTenant()
            .setTenantId(Integer.valueOf(tenantId))
            .setSurveyId(req.getSurveyId());
    surSurveyTenantMapper.insert(surSurveyTenant);

    // 租户扣除积分
    tenant.setIntegral(tenant.getIntegral()-survey.getCredit());
    sysTenantMapper.update(tenant,new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getId,tenantId));
    // 用户扣除积分
    user.setIntegral(user.getIntegral()-survey.getCredit());
    sysUserService.deductIntegral(user);
    if (!"0".equals(survey.getTenantId())){
      // 制作者用户加积分
      sysUserService.updateIntegral(userMaker.getId(), (int) (survey.getCredit()*0.5));
      // 制作者租户加积分
      maker.setIntegral((int) ((maker.getIntegral()+survey.getCredit())*0.5));
      sysTenantMapper.update(maker,new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getId,maker.getId()));
    }
    return true;
  }


  @Override
  public Boolean uploadTemplate(UploadReq req,String tenantId){
  //获得需要上传问卷模板
  SurSurveyProject surSurveyProject = userSurveyMapper.selectById(req.getSurveyProjectId());

  // 取得用户对象
  SysTenant tenant = sysTenantMapper.selectById(tenantId);

  //用户问卷模板的上传所需的积分(也可以不写死，可以让前端传值过来)
  if((tenant.getIntegral()-1)<0){
    return false;
  }

  tenant.setIntegral((tenant.getIntegral()-1));
  sysTenantMapper.update(tenant,new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getId,tenantId));

  //判断是否编辑过
//  if(!surSurveyProject.getIsEdit()){
//    return false;
//  }

  //将用户surveyProject自己的模板上传到市场
    Survey survey = new Survey();
    survey.setType(surSurveyProject.getType())
            .setSurName(surSurveyProject.getSurName())
            .setSurContent(surSurveyProject.getSurContent())
            .setJsonPreview(surSurveyProject.getJsonPreview())
            .setOrgUid(surSurveyProject.getSysOrgCode())
            .setCredit(req.getCredit())
            .setTenantId(tenantId);
    surveyMapper.insert(survey);

    // 取得用户问卷问题
    List<SurQuestionProject> surQuestionProjects = userSurveyQuestionMapper.selectList(new LambdaQueryWrapper<SurQuestionProject>()
            .eq(SurQuestionProject::getSurveyUid, req.getSurveyProjectId()));

    // 取得用户问题选项
    List<SurQuestionChoiceProject> surQuestionChoiceProjects = userSurveyChoiceMapper.selectList(new LambdaQueryWrapper<SurQuestionChoiceProject>()
            .eq(SurQuestionChoiceProject::getSurveyUid, req.getSurveyProjectId()));

    //mybatisPlus自带的雪花算法，直接获取刚插入的id值
    //获取市场模板上的一条数据
    Survey recentSurSurvey = surveyMapper.selectById(survey.getId());

    for (SurQuestionProject surQuestionProject : surQuestionProjects) {
      SurQuestion surQuestion = new SurQuestion();

      surQuestion.setSurveyUid(recentSurSurvey.getId())
              .setContent(surQuestionProject.getContent())
              .setIsParent(surQuestionProject.getIsParent())
              .setParentId(surQuestionProject.getParentId())
              .setParentContent(surQuestionProject.getParentContent())
              .setOrgUid(surQuestionProject.getSysOrgCode())
              .setTitle(surQuestionProject.getTitle())
              .setTypeId(surQuestionProject.getTypeId())
              .setDimensionId(surQuestionProject.getDimensionId())
              .setRequired(surQuestionProject.getRequired())
              .setTenantId(recentSurSurvey.getTenantId());
      surQuestionMapper.insert(surQuestion);
    }

    // 用户问题选项复制到问题选项
    List<SurQuestion> recentSurQuestion = surQuestionMapper.selectList(new LambdaQueryWrapper<SurQuestion>()
            .eq(SurQuestion::getSurveyUid,recentSurSurvey.getId()));
    String [] questionIds = new String[1000];
    int index = 0;
    for (SurQuestion recentSurQuestions: recentSurQuestion) {
      questionIds[index]=recentSurQuestions.getId();
      index = index+1;
    }

    int i =0;
    for (SurQuestionChoiceProject surQuestionChoiceProject:surQuestionChoiceProjects) {
      SurQuestionChoice surQuestionChoice = new SurQuestionChoice();
      surQuestionChoice.setSurveyUid(recentSurSurvey.getId())
              .setContent(surQuestionChoiceProject.getContent())
              .setBasicScore(surQuestionChoiceProject.getBasicScore())
              .setRequired(surQuestionChoiceProject.getRequired())
              .setOrgUid(surQuestionChoiceProject.getSysOrgCode())
              .setTenantId(recentSurSurvey.getTenantId())
              .setQuestionId(questionIds[i]);
      surQuestionChoiceMapper.insert(surQuestionChoice);
      i=i+1;
    }


  return true;
  }

  @Override
  public Survey getSurveyTemplateById(String id) {
    return surveyMapper.selectById(id);
  }

  @Override
  public Boolean setEvaluationSurveyList(EvaluationSurveySetReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    } else {
      // 先删除之前的问卷和项目的关系
      projectSurveyMapper.delete(
          new LambdaQueryWrapper<SurProjectSurvey>()
              .eq(SurProjectSurvey::getProjectId, req.getId()));
      // 遍历问卷id
      List<String> surveyIdList = req.getSurvey();
      surveyIdList.forEach(
          id -> {
            SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
            surProjectSurvey.setProjectId(req.getId());
            surProjectSurvey.setSurveyId(id);
            projectSurveyMapper.insert(surProjectSurvey);
          });
    }
    return true;
  }

  @Override
  public Boolean addEvaluationUser(Add360UserReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    } else {
      // 过滤掉名字中的重复值和空字符串、转义字符
      List<String> list =
          req.getNames().stream()
              .filter(name -> !StringUtils.isEmpty(name))
              .distinct()
              .collect(Collectors.toList());
      // 去除转义字符
      list.forEach(StringEscapeUtils::unescapeJava);
      // 遍历名字数组
      list.forEach(
          name -> {
            // 按中文逗号拆分字符串
            String[] split = name.split("，");
            String userName = split[0];
            String phone = split[1];
            SurUser user = new SurUser();
            user.setProjectId(req.getId());
            user.setType(1);
            user.setName(userName);
            user.setPhone(phone);
            // 查找有没有同名的
            List<SurUser> surUserList =
                userMapper.selectList(
                    new LambdaQueryWrapper<SurUser>()
                        .eq(SurUser::getName, userName)
                        .eq(SurUser::getProjectId, req.getId()));
            if (!surUserList.isEmpty()) {
              return;
            }
            userMapper.insert(user);
            // 根据项目id查询项目问卷关系
            List<SurProjectSurvey> surProjectSurveys =
                projectSurveyMapper.selectList(
                    new LambdaQueryWrapper<SurProjectSurvey>()
                        .eq(SurProjectSurvey::getProjectId, req.getId()));
            surProjectSurveys.forEach(
                relation -> {
                  SurUserSurvey surUserSurvey = new SurUserSurvey();
                  surUserSurvey.setUserId(user.getId());
                  surUserSurvey.setPhone(phone);
                  surUserSurvey.setSurveyId(relation.getSurveyId());
                  surUserSurvey.setProjectId(req.getId());
                  surUserSurveyMapper.insert(surUserSurvey);
                });
          });
    }
    return true;
  }

  @Override
  public Boolean importSurveyUser(MultipartFile file, String projectId) throws IOException {
    ExcelImportListener importListener = new ExcelImportListener();
    List<Map<Integer, String>> data =
        EasyExcel.read(file.getInputStream(), importListener).sheet(0).doReadSync();
    // 解析之后的数据
    List<Map<Integer, String>> result = importListener.getList();
    if (result.isEmpty()) {
      return false;
    }
    // 获取表头
    Map<Integer, String> head = result.get(0);
    Integer nameIndex = null;
    Integer phoneIndex = null;
    Integer genderIndex = null;
    // 遍历表头map
    for (Map.Entry<Integer, String> entry : head.entrySet()) {
      if (entry.getValue().equals("姓名") || entry.getValue().equals("name")) {
        nameIndex = entry.getKey();
      }
      if (entry.getValue().equals("手机号")
          || entry.getValue().equals("电话号码")
          || entry.getValue().equals("phone")
          || entry.getValue().equals("手机号码")
          || entry.getValue().equals("电话")
          || entry.getValue().equals("手机")) {
        phoneIndex = entry.getKey();
      }
    }
    List<Map<String, String>> filter = new ArrayList<>();
    // 遍历data
    for (Map<Integer, String> map : data) {
      String name = map.get(nameIndex);
      String phone = map.get(phoneIndex);
      Map<String, String> stringStringHashMap = new HashMap<>();
      stringStringHashMap.put(name, phone);
      filter.add(stringStringHashMap);
    }
    // 过滤掉相同姓名的
    //        List<Map<String, String>> collect =
    // filter.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new
    // TreeSet<>(Comparator.comparing(o -> o.keySet().iterator().next()))), ArrayList::new));
    // 遍历名字数组
    filter.forEach(
        map -> {
          String userName = map.keySet().iterator().next();
          String phone = map.get(userName);
          SurUser user = new SurUser();
          user.setProjectId(projectId);
          user.setType(1);
          user.setName(userName);
          user.setPhone(phone);
          // 查找有没有同名的
          List<SurUser> surUserList =
              userMapper.selectList(
                  new LambdaQueryWrapper<SurUser>()
                      .eq(SurUser::getName, userName)
                      .eq(SurUser::getPhone, phone)
                      .eq(SurUser::getProjectId, projectId));
          if (!surUserList.isEmpty()) {
            return;
          }
          userMapper.insert(user);
          // 根据项目id查询项目问卷关系
          List<SurProjectSurvey> surProjectSurveys =
              projectSurveyMapper.selectList(
                  new LambdaQueryWrapper<SurProjectSurvey>()
                      .eq(SurProjectSurvey::getProjectId, projectId));
          surProjectSurveys.forEach(
              relation -> {
                SurUserSurvey surUserSurvey = new SurUserSurvey();
                surUserSurvey.setUserId(user.getId());
                surUserSurvey.setPhone(phone);
                surUserSurvey.setSurveyId(relation.getSurveyId());
                surUserSurvey.setProjectId(projectId);
                surUserSurveyMapper.insert(surUserSurvey);
              });
        });
    return true;
  }

  @Override
  public Boolean removeEvaluationUser(Add360UserReq req) {
    // 查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return false;
    } else {
      // 参数中的names数组为要保留的数组 不在数组内的删掉
      // 查询项目之前的名字数组
      List<SurUser> old =
          userMapper.selectList(
              new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
      // 过滤掉名字中的重复值和空字符串
      List<String> list =
          req.getNames().stream()
              .distinct()
              .filter(s -> !StringUtils.isEmpty(s))
              .collect(Collectors.toList());
      // 要删除的人的数组
      List<SurUser> remaining =
          old.stream()
              .filter(userPerson -> !list.contains(userPerson.getName()))
              .collect(Collectors.toList());
      // 如果全部删除 将isAddUSer置为false
      if (remaining.size() == old.size()) {
        project.setIsAddUser(false);
        projectService.updateById(project);
      }
      remaining.forEach(
          userPerson -> {
            // 被评价人id
            String userId = userPerson.getId();
            userMapper.deleteById(userId);
            // 删除被评价人问卷关系
            surUserSurveyMapper.delete(
                new LambdaQueryWrapper<SurUserSurvey>()
                    .eq(SurUserSurvey::getProjectId, req.getId())
                    .eq(SurUserSurvey::getUserId, userId));
            // 查询result
            List<SurResult> surResults =
                resultMapper.selectList(
                    new LambdaQueryWrapper<SurResult>()
                        .eq(SurResult::getProjectUid, req.getId())
                        .eq(SurResult::getUserUid, userId));
            if (!surResults.isEmpty()) {
              // 如果有答卷
              project.setCollectNumber(project.getCollectNumber() - surResults.size());
              projectService.updateById(project);
              // 删除答卷
              resultMapper.delete(
                  new LambdaQueryWrapper<SurResult>()
                      .eq(SurResult::getProjectUid, req.getId())
                      .eq(SurResult::getUserUid, userId));
              userResultMapper.delete(
                  new LambdaQueryWrapper<SurUserResult>()
                      .eq(SurUserResult::getProjectId, req.getId())
                      .eq(SurUserResult::getUserId, userId));
            }
          });
    }
    return true;
  }

  @Override
  public List<SurUser> getEvaluationUserList(String projectId) {
    return userMapper.selectList(
        new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, projectId));
  }

  @Override
  public EvaluationJudgeDto judgeEvaluation(JudgeReq req) {
    // 根据项目id查询用户
    List<SurUser> userList =
        userMapper.selectList(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getProjectId()));
    // 将用户列表转换为手机号
    List<String> phoneList = userList.stream().map(SurUser::getPhone).collect(Collectors.toList());
    EvaluationJudgeDto judgeDto = new EvaluationJudgeDto();
    if (phoneList.contains(req.getPhone())) {
      // 查询出该用户
      //            SurUser one = userService.getOne(new
      // LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId,
      // req.getProjectId()).likeLeft(SurUser::getPhone, req.getPhoneSuffix()));
      SurUser one =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, req.getProjectId())
                  .eq(SurUser::getPhone, req.getPhone()));
      judgeDto.setUser(one);
      // 查询出该用户的问卷
      List<SurProjectSurvey> surProjectSurveys =
          projectSurveyMapper.selectList(
              new LambdaQueryWrapper<SurProjectSurvey>()
                  .eq(SurProjectSurvey::getProjectId, req.getProjectId()));
      List<Survey> surveyList = new ArrayList<>();
      surProjectSurveys.forEach(
          relation -> {
            // 查询出问卷
            Survey survey = surveyMapper.selectById(relation.getSurveyId());
            // 查询用户问卷关系
            SurUserSurvey surUserSurvey =
                surUserSurveyMapper.selectOne(
                    new LambdaQueryWrapper<SurUserSurvey>()
                        .eq(SurUserSurvey::getUserId, one.getId())
                        .eq(SurUserSurvey::getProjectId, req.getProjectId())
                        .eq(SurUserSurvey::getSurveyId, relation.getSurveyId()));
            if (surUserSurvey != null) {
              survey.setStatus(surUserSurvey.getStatus());
            } else {
              survey.setStatus(0);
            }
            surveyList.add(survey);
          });
      judgeDto.setSurveyList(surveyList);
      return judgeDto;
    }
    return null;
  }

  @Override
  public InvestigationJudgeDto judgeInvestigation(JudgeReq req) {
    // 根据项目id查询用户
    List<SurUser> userList =
        userMapper.selectList(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getProjectId()));
    // 将用户列表转换为手机号
    List<String> phoneList = userList.stream().map(SurUser::getPhone).collect(Collectors.toList());
    InvestigationJudgeDto judgeDto = new InvestigationJudgeDto();
    if (phoneList.contains(req.getPhone())) {
      // 查询出该用户
      //            SurUser one = userService.getOne(new
      // LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId,
      // req.getProjectId()).likeLeft(SurUser::getPhone, req.getPhoneSuffix()));
      SurUser one =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, req.getProjectId())
                  .eq(SurUser::getPhone, req.getPhone()));
      judgeDto.setUser(one);
      // 查询出该用户的问卷
      SurProjectSurvey surProjectSurvey =
          projectSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurProjectSurvey>()
                  .eq(SurProjectSurvey::getProjectId, req.getProjectId()));
      // 查询出问卷
      SurSurveyProject survey = userSurveyMapper.selectById(surProjectSurvey.getSurveyId());
      // 查询用户问卷关系
      SurUserSurvey surUserSurvey =
          surUserSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurUserSurvey>()
                  .eq(SurUserSurvey::getUserId, one.getId())
                  .eq(SurUserSurvey::getProjectId, req.getProjectId())
                  .eq(SurUserSurvey::getSurveyId, surProjectSurvey.getSurveyId()));
      if (surUserSurvey != null) {
        survey.setStatus(surUserSurvey.getStatus());
      } else {
        survey.setStatus(0);
      }
      judgeDto.setSurvey(survey);
      return judgeDto;
    }
    return null;
  }

  @Override
  public EvaluationJudgeDto getUserSurveyResult(JudgeReq req) {
    EvaluationJudgeDto judgeDto = new EvaluationJudgeDto();
    SurUser one =
        userMapper.selectOne(
            new LambdaQueryWrapper<SurUser>()
                .eq(SurUser::getProjectId, req.getProjectId())
                .eq(SurUser::getPhone, req.getPhone()));
    judgeDto.setUser(one);
    // 查询出该用户的问卷
    List<SurProjectSurvey> surProjectSurveys =
        projectSurveyMapper.selectList(
            new LambdaQueryWrapper<SurProjectSurvey>()
                .eq(SurProjectSurvey::getProjectId, req.getProjectId()));
    List<Survey> surveyList = new ArrayList<>();
    surProjectSurveys.forEach(
        relation -> {
          // 查询出问卷
          Survey survey = surveyMapper.selectById(relation.getSurveyId());
          // 查询用户问卷关系
          SurUserSurvey surUserSurvey =
              surUserSurveyMapper.selectOne(
                  new LambdaQueryWrapper<SurUserSurvey>()
                      .eq(SurUserSurvey::getUserId, one.getId())
                      .eq(SurUserSurvey::getProjectId, req.getProjectId())
                      .eq(SurUserSurvey::getSurveyId, relation.getSurveyId()));
          if (surUserSurvey != null) {
            survey.setStatus(surUserSurvey.getStatus());
          } else {
            survey.setStatus(0);
          }
          surveyList.add(survey);
        });
    judgeDto.setSurveyList(surveyList);
    return judgeDto;
  }

  @Override
  public JudgeDto get360UserSurveyResult(JudgeReq req) {
    return judge360User(req);
  }

  @Override
  public ProjectCollectDto getEvaluationStatistics(String projectId) {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return null;
    }
    ProjectCollectDto projectCollectDto = new ProjectCollectDto();
    projectCollectDto.setProjectId(project.getId());
    projectCollectDto.setProjectName(project.getProjectName());
    projectCollectDto.setType(project.getType());
    projectCollectDto.setCollectNumber(project.getCollectNumber());
    List<Survey> surveyList = getEvaluationSurveyList(projectId);
    projectCollectDto.setSurveyList(surveyList);
    return projectCollectDto;
  }

  @Override
  public List<Object> getEvaluationSurvey(EvaluationSurveyReq req) {
    // 根据问卷id查询问卷
    Survey survey = surveyMapper.selectById(req.getSurveyId());
    List<Object> resultList = new ArrayList<>();
    if (survey != null) {
      // 查询该问卷的所有题目
      List<SurQuestion> questionProjectList =
          surQuestionMapper.selectList(
              new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, survey.getId()));
      // 过滤掉isParent为true的  即获取子问题集合
      List<SurQuestion> questionList =
          questionProjectList.stream()
              .filter(surQuestion -> !surQuestion.getIsParent())
              .collect(Collectors.toList());
      questionList.forEach(
          question -> {
            // 构造题目json对象
            Map<String, Object> questionMap = new LinkedHashMap<>();
            questionMap.put("id", question.getId());
            questionMap.put("content", question.getContent());
            questionMap.put("type", question.getTypeId());
            List<JSONObject> choiceList = new ArrayList<>();
            // 如果是多行文本赋分题
            if ("multipletext".equals(question.getTypeId())) {
              // 计算该题目的有效填写人数
              List<SurUserResult> userResultList =
                  userResultMapper.selectList(
                      new LambdaQueryWrapper<SurUserResult>()
                          .eq(SurUserResult::getProjectId, req.getProjectId())
                          .eq(SurUserResult::getSurveyId, req.getSurveyId()));
              // 将list根据userId分组
              Map<String, List<SurUserResult>> resultMapList = new HashMap<>();
              resultMapList =
                  userResultList.stream().collect(Collectors.groupingBy(SurUserResult::getUserId));
              List<Map<String, List<SurUserResult>>> result = new ArrayList<>();
              // 遍历resultMapList
              Iterator<Map.Entry<String, List<SurUserResult>>> iterator =
                  resultMapList.entrySet().iterator();
              while (iterator.hasNext()) {
                Map.Entry<String, List<SurUserResult>> entry = iterator.next();
                Map tmp = new HashMap();
                tmp.put(entry.getKey(), entry.getValue());
                result.add(tmp);
              }

              // 有效填写人数
              questionMap.put("userNumber", result.size());
              // 计算该题目的平均分

              userResultList.stream()
                  .map(SurUserResult::getBasicScore)
                  .reduce((a, b) -> Integer.parseInt(a.toString()) + Integer.parseInt(b.toString()))
                  .ifPresent(
                      score -> {
                        int total = Integer.parseInt(score.toString());
                        if (total != 0) {
                          questionMap.put("average", NumUtils.device(total, userResultList.size()));
                        } else {
                          questionMap.put("average", 0);
                        }
                      });

              // 查询该题目的选项
              SurQuestionChoice choiceProject =
                  surQuestionChoiceMapper.selectOne(
                      new LambdaQueryWrapper<SurQuestionChoice>()
                          .eq(SurQuestionChoice::getQuestionId, question.getId()));
              String content = choiceProject.getContent();
              List<String> choiceLists =
                  Arrays.asList(
                      content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
              if (choiceLists.size() > 1) {
                // 查询该问题的总人数
                List<SurUserResult> questionUserResult =
                    userResultMapper.selectList(
                        new LambdaQueryWrapper<SurUserResult>()
                            .eq(SurUserResult::getProjectId, req.getProjectId())
                            .eq(SurUserResult::getSurveyId, req.getSurveyId())
                            .eq(SurUserResult::getQuestionId, question.getId()));
                int questionTotalNum = questionUserResult.size();
                // 计算 多选题中每个选项的总人数
                for (String choice : choiceLists) {
                  // 构造选项json对象
                  JSONObject choiceObj = new JSONObject();
                  choiceObj.put("option", choice);
                  if (questionTotalNum == 0) {
                    choiceObj.put("proportion", 0);
                    choiceObj.put("subtotal", 0);
                  }
                  // 查询每个选项的人数
                  List<SurUserResult> choiceUserResult =
                      userResultMapper.selectList(
                          new LambdaQueryWrapper<SurUserResult>()
                              .eq(SurUserResult::getProjectId, req.getProjectId())
                              .eq(SurUserResult::getSurveyId, req.getSurveyId())
                              .eq(SurUserResult::getQuestionValue, choice));
                  int choiceCount = choiceUserResult.size();
                  // 如果选该选项的人数为0
                  if (choiceUserResult.isEmpty()) {
                    choiceObj.put("proportion", 0);
                    choiceObj.put("subtotal", 0);
                  } else {
                    choiceObj.put("subtotal", choiceCount);
                    // 计算该选项的百分比
                    choiceObj.put("proportion", NumUtils.getPercent(choiceCount, questionTotalNum));
                  }
                  choiceList.add(choiceObj);
                }
              }
              questionMap.put("choiceList", choiceList);
              resultList.add(questionMap);
            }
            // 如果是其他题型
            else {
              // 查询该题目的有效填写人数
              List<SurUserResult> surUserResults =
                  userResultMapper.selectList(
                      new LambdaQueryWrapper<SurUserResult>()
                          .eq(SurUserResult::getProjectId, req.getProjectId())
                          .eq(SurUserResult::getSurveyId, req.getSurveyId())
                          .eq(SurUserResult::getQuestionKey, question.getContent()));
              questionMap.put("userNumber", surUserResults.size());
              // 计算该题目的平均分
              surUserResults.stream()
                  .map(SurUserResult::getBasicScore)
                  .reduce((a, b) -> Integer.parseInt(a.toString()) + Integer.parseInt(b.toString()))
                  .ifPresent(
                      score -> {
                        int total = Integer.parseInt(score.toString());
                        if (total != 0) {
                          questionMap.put("average", NumUtils.device(total, surUserResults.size()));
                        } else {
                          questionMap.put("average", 0);
                        }
                      });
              // 查询该题目的选项
              SurQuestionChoice choiceProject =
                  surQuestionChoiceMapper.selectOne(
                      new LambdaQueryWrapper<SurQuestionChoice>()
                          .eq(SurQuestionChoice::getQuestionId, question.getId()));
              String content = choiceProject.getContent();
              List<String> choiceLists =
                  Arrays.asList(
                      content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
              if (choiceLists.size() > 1) {
                // 遍历选项数组
                Long totalNum = 0L;
                // 计算 多选题中每个选项的总人数
                for (String choice : choiceLists) {
                  if ("checkbox".equals(question.getTypeId())) {
                    Long filledNumber = 0L;
                    // 查询出该问题的所有userResult
                    // 转换为question_value数组
                    List<String> questionValueList =
                        surUserResults.stream()
                            .map(SurUserResult::getQuestionValue)
                            .collect(Collectors.toList());
                    for (String value : questionValueList) {
                      if (value.contains(choice)) {
                        filledNumber++;
                      }
                    }
                    totalNum += filledNumber;
                  }
                }
                for (String list : choiceLists) {
                  Long filledNumber = 0L;
                  // 查询该选项的有效填写人数
                  // 构造选项json对象
                  JSONObject choiceObj = new JSONObject();
                  choiceObj.put("option", list);
                  // 如果是多选题
                  if ("checkbox".equals(question.getTypeId())) {
                    // 转换为question_value数组
                    List<String> questionValueList =
                        surUserResults.stream()
                            .map(SurUserResult::getQuestionValue)
                            .collect(Collectors.toList());
                    for (String value : questionValueList) {
                      if (value.contains(list)) {
                        filledNumber++;
                      }
                    }
                    // 如果有效填写人数为0
                    if (surUserResults.size() == 0) {
                      choiceObj.put("proportion", 0);
                    } else {
                      // 计算该选项的百分比
                      choiceObj.put(
                          "proportion",
                          NumUtils.getPercent(
                              Integer.parseInt(filledNumber.toString()),
                              Integer.parseInt(totalNum.toString())));
                    }
                  } else {
                    // 如果是单选题
                    filledNumber =
                        userResultMapper.selectCount(
                            new LambdaQueryWrapper<SurUserResult>()
                                .eq(SurUserResult::getQuestionKey, question.getContent())
                                .eq(SurUserResult::getProjectId, req.getProjectId())
                                .eq(SurUserResult::getQuestionValue, list));
                    // 如果有效填写人数为0
                    if (surUserResults.size() == 0) {
                      choiceObj.put("proportion", 0);
                    } else {
                      // 计算该选项的百分比
                      choiceObj.put(
                          "proportion",
                          NumUtils.getPercent(
                              Integer.parseInt(filledNumber.toString()), surUserResults.size()));
                    }
                  }
                  choiceObj.put("subtotal", filledNumber);
                  choiceList.add(choiceObj);
                }
              }
              questionMap.put("choiceList", choiceList);
              resultList.add(questionMap);
            }
          });
    }
    return resultList;
  }

  @Override
  public EvaluationCollectProjectDto getEvaluationCollectData(CollectReq req) {
    // 首先根据项目id查询项目对象
    SurProject project = projectService.getById(req.getId());
    if (project == null) {
      return null;
    }
    // 查询项目用户列表
    List<SurUser> userList =
        userMapper.selectList(
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    Page<SurUser> page =
        userMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    List<SurUser> records = page.getRecords();
    // 查询项目的问卷
    List<Survey> surveyList = getEvaluationSurveyList(req.getId());
    EvaluationCollectProjectDto collectDto = new EvaluationCollectProjectDto();
    collectDto.setId(project.getId());
    collectDto.setName(project.getProjectName());
    collectDto.setType(project.getType());
    collectDto.setSurveyList(surveyList);
    // 统计项目的总人数、已完成人数和完成率
    // 已经完成的人数组
    List<SurUser> completed =
        userList.stream().filter(SurUser::getIsFinished).collect(Collectors.toList());
    collectDto.setCollectNumber(completed.size());
    collectDto.setSelectNumber(userList.size());
    // 如果没有选择人员
    if (userList.isEmpty()) {
      collectDto.setRate("0%");
    } else {
      // 计算比例
      String rate = new DecimalFormat("0.00%").format((double) completed.size() / userList.size());
      collectDto.setRate(rate);
    }
    List<JSONObject> userResultDtoList = new ArrayList<>();
    for (SurUser surUser : records) {
      JSONObject userJsonObj = new JSONObject();
      userJsonObj.put("id", surUser.getId());
      userJsonObj.put("name", surUser.getName());
      userJsonObj.put("gender", surUser.getGender());
      userJsonObj.put("age", surUser.getAge());
      userJsonObj.put("phone", surUser.getPhone());
      int index = 1;
      for (Survey survey : surveyList) {
        SurveyDto surveyDto = new SurveyDto();
        BeanUtils.copyProperties(survey, surveyDto);
        // 查询用户问卷关系对象
        SurUserSurvey userSurvey =
            surUserSurveyMapper.selectOne(
                new LambdaQueryWrapper<SurUserSurvey>()
                    .eq(SurUserSurvey::getProjectId, req.getId())
                    .eq(SurUserSurvey::getUserId, surUser.getId())
                    .eq(SurUserSurvey::getSurveyId, survey.getId()));
        surveyDto.setStatus(userSurvey.getStatus());
        // 查询result对象
        SurResult surResult =
            resultMapper.selectOne(
                new LambdaQueryWrapper<SurResult>()
                    .eq(SurResult::getProjectUid, req.getId())
                    .eq(SurResult::getUserUid, surUser.getId())
                    .eq(SurResult::getSurveyUid, survey.getId()));
        if (surResult != null) {
          surveyDto.setResult(surResult.getAnswer());
        }
        // 查出用户分数
        //        List<SurUserResult> surUserResults =
        //            userResultMapper.selectList(
        //                new LambdaQueryWrapper<SurUserResult>()
        //                    .eq(SurUserResult::getProjectId, req.getId())
        //                    .eq(SurUserResult::getSurveyId, survey.getId())
        //                    .eq(SurUserResult::getUserId, surUser.getId()));
        //        List<Object> scoreCollect =
        //
        // surUserResults.stream().map(SurUserResult::getBasicScore).collect(Collectors.toList());
        //        Integer totalScore = 0;
        //        // 计算总分数
        //        for (Object value : scoreCollect) {
        //          if (value != null) {
        //            totalScore += Integer.parseInt(value.toString());
        //          }
        //        }
        //        surveyDto.setScore(totalScore);
        userJsonObj.put("survey" + index, surveyDto);
        index++;
      }
      userResultDtoList.add(userJsonObj);
    }
    page.setRecords(records);
    EvaluationUserPageDto userPage = new EvaluationUserPageDto();
    userPage.setUser(userResultDtoList);
    userPage.setSize(page.getSize());
    userPage.setTotal(page.getTotal());
    collectDto.setUserPage(userPage);
    return collectDto;
  }

  @Override
  public EvaluationProjectResultDto getEvaluationSurveyResult(String projectId) {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    if (project == null) {
      return null;
    }
    // 查询项目的问卷
    EvaluationProjectResultDto evaluationProjectResultDto = new EvaluationProjectResultDto();
    List<Survey> surveyList = getEvaluationSurveyList(projectId);
    List<SurveyDto> surveyDtoList = new ArrayList<>();
    evaluationProjectResultDto.setId(project.getId());
    evaluationProjectResultDto.setName(project.getProjectName());
    evaluationProjectResultDto.setType(project.getType());
    evaluationProjectResultDto.setCollectNum(project.getCollectNumber());
    // 查询用户结果
    List<SurResult> resultList =
        resultMapper.selectList(
            new LambdaQueryWrapper<SurResult>()
                .eq(SurResult::getProjectUid, projectId)
                .orderByDesc(SurResult::getCreateDate));
    if (resultList.size() > 0) {
      SurResult lastResult = resultList.get(0);
      if (lastResult.getCreateDate() != null) {
        evaluationProjectResultDto.setLastEditTime(lastResult.getCreateDate());
      } else {
        evaluationProjectResultDto.setLastEditTime(project.getUpdateTime());
      }
    } else {
      evaluationProjectResultDto.setLastEditTime(project.getUpdateTime());
    }
    surveyList.forEach(
        survey -> {
          SurveyDto surveyDto = new SurveyDto();
          BeanUtils.copyProperties(survey, surveyDto);
          // 查询用户结果
          List<SurResult> resultLists =
              resultMapper.selectList(
                  new LambdaQueryWrapper<SurResult>()
                      .eq(SurResult::getProjectUid, projectId)
                      .eq(SurResult::getSurveyUid, survey.getId())
                      .orderByDesc(SurResult::getCreateDate));
          List<JSONObject> results = new ArrayList<>();
          resultLists.forEach(
              surResult -> {
                JSONObject resultObj = new JSONObject();
                resultObj.put("answer", surResult.getAnswer());
                results.add(resultObj);
              });
          surveyDto.setSurveyResults(results);
          surveyDtoList.add(surveyDto);
        });
    evaluationProjectResultDto.setSurveyList(surveyDtoList);
    return evaluationProjectResultDto;
  }

  @Override
  public void getManagementStyleReport(String projectId, String userId) throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1587985620128116738")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 遍历问题列表
      for (SurUserResult result : results) {
        // 截取问题编号
        String option = result.getQuestionKey().substring(2);
        // 截取选项编号
        String value = result.getQuestionValue().substring(0, 1);
        param.put(option, value);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json =
            HttpClientUtil.doPost(
                "http://106.14.140.92:7777/managementStyleEvaluation", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        // 新建报表对象
        ReportManagementStyleEvaluation reportManagementStyleEvaluation =
            new ReportManagementStyleEvaluation();
        reportManagementStyleEvaluation.setUserId(userId);
        reportManagementStyleEvaluation.setProjectId(projectId);
        reportManagementStyleEvaluation.setSurveyId("1587985620128116738");
        if (resultJson != null) {
          // 获取分数
          Integer s1 = (Integer) resultJson.get("s1");
          Integer s2 = (Integer) resultJson.get("s2");
          Integer s3 = (Integer) resultJson.get("s3");
          Integer s4 = (Integer) resultJson.get("s4");
          Integer re_core = (Integer) resultJson.get("re_core");
          String dominant = (String) resultJson.get("dominant");
          JSONArray dominant_explain = (JSONArray) resultJson.get("dominant_explain");
          String secondary = (String) resultJson.get("secondary");
          JSONArray secondary_explain = (JSONArray) resultJson.get("secondary_explain");
          String adaptability_type = (String) resultJson.get("adaptability_type");
          String adaptability_explain = (String) resultJson.get("adaptability_explain");
          String level = (String) resultJson.get("level");
          String dominant_adapt_type = (String) resultJson.get("dominant_adapt_type");
          String secondary_adapt_type = (String) resultJson.get("secondary_adapt_type");
          List<String> dominant_explain_list =
              JSONObject.parseArray(dominant_explain.toJSONString(), String.class);
          List<String> secondary_explain_list =
              JSONObject.parseArray(secondary_explain.toJSONString(), String.class);
          StringBuilder dominant_explain_final = new StringBuilder();
          StringBuilder secondary_explain_final = new StringBuilder();
          // 换行符
          String lineSeparator = System.lineSeparator();
          reportManagementStyleEvaluation.setS1(s1);
          reportManagementStyleEvaluation.setS2(s2);
          reportManagementStyleEvaluation.setS3(s3);
          reportManagementStyleEvaluation.setS4(s4);
          reportManagementStyleEvaluation.setResult(re_core);
          reportManagementStyleEvaluation.setDominant(dominant);
          reportManagementStyleEvaluation.setLevel(level);
          reportManagementStyleEvaluation.setDominantAdaptType(dominant_adapt_type);
          reportManagementStyleEvaluation.setSecondaryAdaptType(secondary_adapt_type);
          // 遍历 dominant_explain_list 添加换行符
          for (String s : dominant_explain_list) {
            dominant_explain_final.append(s).append(lineSeparator);
          }
          for (String s : secondary_explain_list) {
            secondary_explain_final.append(s).append(lineSeparator);
          }
          reportManagementStyleEvaluation.setDominantExplain(dominant_explain_final.toString());
          reportManagementStyleEvaluation.setSecondary(secondary);
          reportManagementStyleEvaluation.setSecondaryExplain(secondary_explain_final.toString());
          reportManagementStyleEvaluation.setAdaptabilityType(adaptability_type);
          reportManagementStyleEvaluation.setAdaptabilityExplain(adaptability_explain);
        }
        reportManagementStyleEvaluationMapper.insert(reportManagementStyleEvaluation);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public void getTeamRoleReport(String projectId, String userId) throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1587994429726797826")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 获取该用户的问题数组
      List<String> questionList =
          results.stream()
              .map(SurUserResult::getQuestionKey)
              .distinct()
              .collect(Collectors.toList());
      // 遍历questionLIst 来构造参数
      for (String questionKey : questionList) {
        // 获取每到题目对应的userResult 数组
        List<SurUserResult> questionValueList =
            results.stream()
                .filter(result -> result.getQuestionKey().equals(questionKey))
                .collect(Collectors.toList());
        JSONObject choice = new JSONObject();
        String questionIndex = "";
        // 遍历questionValueList
        for (SurUserResult surUserResult : questionValueList) {
          // 截取问题编号
          questionIndex = surUserResult.getQuestionKey().substring(2);
          // 截取选项编号
          String choiceIndex = surUserResult.getQuestionValue().substring(0, 1);
          // 获取每个选项对应的分数
          int score = Integer.parseInt(surUserResult.getBasicScore().toString());
          choice.put(choiceIndex, score);
        }
        param.put(questionIndex, choice);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json = HttpClientUtil.doPost("http://106.14.140.92:7777/teamRoleTest", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        Integer IMP = (Integer) resultJson.get("IMP");
        Integer CO = (Integer) resultJson.get("CO");
        Integer SH = (Integer) resultJson.get("SH");
        Integer PL = (Integer) resultJson.get("PL");
        Integer RI = (Integer) resultJson.get("RI");
        Integer ME = (Integer) resultJson.get("ME");
        Integer TW = (Integer) resultJson.get("TW");
        Integer CF = (Integer) resultJson.get("CF");
        String first = (String) resultJson.get("first");
        String first_features = (String) resultJson.get("first_features");
        String first_role = (String) resultJson.get("first_role");
        String first_shortcoming = (String) resultJson.get("first_shortcoming");
        String second = (String) resultJson.get("second");
        String second_features = (String) resultJson.get("second_features");
        String second_role = (String) resultJson.get("second_role");
        String second_shortcoming = (String) resultJson.get("second_shortcoming");
        String third = (String) resultJson.get("third");
        String third_features = (String) resultJson.get("third_features");
        String third_role = (String) resultJson.get("third_role");
        String third_shortcoming = (String) resultJson.get("third_shortcoming");
        String fourth = (String) resultJson.get("fourth");
        String fourth_features = (String) resultJson.get("fourth_features");
        String fourth_role = (String) resultJson.get("fourth_role");
        String fourth_shortcoming = (String) resultJson.get("fourth_shortcoming");
        String eighth = (String) resultJson.get("eighth");
        String eighth_features = (String) resultJson.get("eighth_features");
        String eighth_role = (String) resultJson.get("eighth_role");
        String eighth_shortcoming = (String) resultJson.get("eighth_shortcoming");
        ReportTeamRole reportTeamRole =
            ReportTeamRole.builder()
                .imp(IMP)
                .co(CO)
                .sh(SH)
                .pl(PL)
                .me(ME)
                .ri(RI)
                .tw(TW)
                .cf(CF)
                .first(first)
                .firstFeatures(first_features)
                .firstRole(first_role)
                .firstShortcoming(first_shortcoming)
                .second(second)
                .secondFeatures(second_features)
                .secondRole(second_role)
                .secondShortcoming(second_shortcoming)
                .third(third)
                .thirdFeatures(third_features)
                .thirdRole(third_role)
                .thirdShortcoming(third_shortcoming)
                .fourth(fourth)
                .fourthFeatures(fourth_features)
                .fourthRole(fourth_role)
                .fourthShortcoming(fourth_shortcoming)
                .eighth(eighth)
                .eighthFeatures(eighth_features)
                .eighthRole(eighth_role)
                .eighthShortcoming(eighth_shortcoming)
                .userId(userId)
                .surveyId("1587994429726797826")
                .projectId(projectId)
                .build();
        reportTeamRoleMapper.insert(reportTeamRole);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public void getPersonalCareerDrivingForceReport(String projectId, String userId)
      throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1587998080067317762")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 获取该用户的问题数组
      List<String> questionList =
          results.stream()
              .map(SurUserResult::getQuestionKey)
              .distinct()
              .collect(Collectors.toList());
      // 遍历questionLIst 来构造参数
      for (String questionKey : questionList) {
        // 获取每到题目对应的userResult 数组
        List<SurUserResult> questionValueList =
            results.stream()
                .filter(result -> result.getQuestionKey().equals(questionKey))
                .collect(Collectors.toList());
        JSONObject choice = new JSONObject();
        String questionIndex = "";
        // 遍历questionValueList
        for (SurUserResult surUserResult : questionValueList) {
          // 截取问题编号
          questionIndex = surUserResult.getQuestionKey().substring(2);
          // 截取选项编号
          String choiceIndex = surUserResult.getQuestionValue().substring(0, 1);
          // 获取每个选项对应的分数
          int score = Integer.parseInt(surUserResult.getBasicScore().toString());
          choice.put(choiceIndex, score);
        }
        param.put(questionIndex, choice);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json =
            HttpClientUtil.doPost(
                "http://106.14.140.92:7777/personalCareerDrivingForce", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        Integer a1 = (Integer) resultJson.get("a1");
        Integer b2 = (Integer) resultJson.get("b2");
        Integer c3 = (Integer) resultJson.get("c3");
        Integer d4 = (Integer) resultJson.get("d4");
        Integer e5 = (Integer) resultJson.get("e5");
        Integer f6 = (Integer) resultJson.get("f6");
        Integer g7 = (Integer) resultJson.get("g7");
        Integer h8 = (Integer) resultJson.get("h8");
        Integer i9 = (Integer) resultJson.get("i9");
        String dominant = (String) resultJson.get("dominant");
        JSONArray dominant_explain_array = (JSONArray) resultJson.get("dominant_explain");
        String secondary = (String) resultJson.get("secondary");
        JSONArray secondary_explain_array = (JSONArray) resultJson.get("secondary_explain");
        String lowest = (String) resultJson.get("lowest");
        JSONArray lowest_explain_array = (JSONArray) resultJson.get("lowest_explain");
        List<String> dominant_explain_list =
            JSONObject.parseArray(dominant_explain_array.toJSONString(), String.class);
        List<String> secondary_explain_list =
            JSONObject.parseArray(secondary_explain_array.toJSONString(), String.class);
        List<String> lowest_explain_list =
            JSONObject.parseArray(lowest_explain_array.toJSONString(), String.class);
        StringBuilder dominant_explain_final = new StringBuilder();
        StringBuilder secondary_explain_final = new StringBuilder();
        StringBuilder lowest_explain_final = new StringBuilder();
        // 换行符
        String lineSeparator = System.lineSeparator();
        for (String s : dominant_explain_list) {
          dominant_explain_final.append(s).append(lineSeparator);
        }
        for (String s : secondary_explain_list) {
          secondary_explain_final.append(s).append(lineSeparator);
        }
        for (String s : lowest_explain_list) {
          lowest_explain_final.append(s).append(lineSeparator);
        }
        ReportBusinessDrivingForce reportBusinessDrivingForce =
            ReportBusinessDrivingForce.builder()
                .a1(a1)
                .b2(b2)
                .c3(c3)
                .d4(d4)
                .e5(e5)
                .f6(f6)
                .g7(g7)
                .h8(h8)
                .i9(i9)
                .dominant(dominant)
                .dominantExplain(dominant_explain_final.toString())
                .secondary(secondary)
                .secondaryExplain(secondary_explain_final.toString())
                .lowest(lowest)
                .lowestExplain(lowest_explain_final.toString())
                .userId(userId)
                .surveyId("1587998080067317762")
                .projectId(projectId)
                .build();
        reportBusinessDrivingForceMapper.insert(reportBusinessDrivingForce);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public void getManagersCommunicateSelfPerceptionReport(String projectId, String userId) {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1600696623468507137")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 遍历问题列表
      for (SurUserResult result : results) {
        // 截取问题编号
        String option = result.getQuestionKey().substring(2);
        // 截取选项编号
        String value = result.getQuestionValue().substring(0, 1);
        param.put(option, value);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      //      String json =
      //          HttpClientUtil.doPost("http://106.14.140.92:7777/managementStyleEvaluation",
      // finalParam);
      //      JSONObject resultObj = (JSONObject) JSON.parse(json);
      //      resultJson = (JSONObject) resultObj.get("result");
    }
  }

  @Override
  public void getHumanizationReport(String projectId, String userId) {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1600734216985870338")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 遍历问题列表
      for (SurUserResult result : results) {
        // 截取问题编号
        String option = StrUtil.cleanBlank(result.getQuestionKey().substring(0, 2));
        // 截取选项编号
        String value = result.getQuestionValue();
        param.put(option, value);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      //      String json =
      //          HttpClientUtil.doPost("http://106.14.140.92:7777/managementStyleEvaluation",
      // finalParam);
      //      JSONObject resultObj = (JSONObject) JSON.parse(json);
      //      resultJson = (JSONObject) resultObj.get("result");
    }
  }

  @Override
  public void getTalentCreativityReport(String projectId, String userId)
      throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1587995890414137346")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 遍历问题列表
      for (SurUserResult result : results) {
        // 截取问题编号
        String option =
            org.jeecg.modules.survey.client.utils.StrUtil.getNumeric(
                result.getQuestionKey().substring(0, 2));
        // 截取选项编号
        String value = result.getQuestionValue();
        switch (value) {
          case "完全符合":
            {
              value = "A";
              break;
            }
          case "部分符合":
            {
              value = "B";
              break;
            }
          case "完全不符合":
            {
              value = "C";
              break;
            }
        }
        param.put(option, value);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json =
            HttpClientUtil.doPost("http://106.14.140.92:7777/talentCreativity", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        Integer s1 = (Integer) resultJson.get("s1");
        Integer s2 = (Integer) resultJson.get("s2");
        Integer s3 = (Integer) resultJson.get("s3");
        Integer s4 = (Integer) resultJson.get("s4");
        Integer total = (Integer) resultJson.get("total");
        String level = (String) resultJson.get("level");
        String evaluation_description = (String) resultJson.get("evaluation_description");
        String mainly = (String) resultJson.get("mainly");
        String lack = (String) resultJson.get("lack");
        ReportCreativeAbility reportCreativeAbility =
            ReportCreativeAbility.builder()
                .s1(s1)
                .s2(s2)
                .s3(s3)
                .s4(s4)
                .total(total)
                .level(level)
                .evaluationDescription(evaluation_description)
                .mainly(mainly)
                .lack(lack)
                .userId(userId)
                .surveyId("1587995890414137346")
                .projectId(projectId)
                .build();
        reportCreativeAbilityMapper.insert(reportCreativeAbility);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public void getAssessmentOfTalentThinkingModeReport(String projectId, String userId)
      throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1588115916102221826")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 获取该用户的问题数组
      List<String> questionList =
          results.stream()
              .map(SurUserResult::getQuestionKey)
              .distinct()
              .collect(Collectors.toList());
      // 遍历questionLIst 来构造参数
      for (String questionKey : questionList) {
        List<Integer> choice = new ArrayList<>();
        // 获取每到题目对应的userResult 数组
        List<SurUserResult> questionValueList =
            results.stream()
                .filter(result -> result.getQuestionKey().equals(questionKey))
                .collect(Collectors.toList());
        String questionIndex = "";
        // 遍历questionValueList
        for (SurUserResult surUserResult : questionValueList) {
          // 截取问题编号
          questionIndex = surUserResult.getQuestionKey().substring(2);
          int score = Integer.parseInt(surUserResult.getBasicScore().toString());
          choice.add(score);
        }
        param.put(questionIndex, choice);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json = HttpClientUtil.doPost("http://106.14.140.92:7777/talentThinking", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        Integer k1 = (Integer) resultJson.get("k1");
        Integer k2 = (Integer) resultJson.get("k2");
        Integer k3 = (Integer) resultJson.get("k3");
        Integer k4 = (Integer) resultJson.get("k4");
        Integer k5 = (Integer) resultJson.get("k5");
        Integer k6 = (Integer) resultJson.get("k6");
        Integer hx1 = (Integer) resultJson.get("hx1");
        Integer hx2 = (Integer) resultJson.get("hx2");
        Integer zx1 = (Integer) resultJson.get("zx1");
        Integer zx2 = (Integer) resultJson.get("zx2");
        Integer zx3 = (Integer) resultJson.get("zx3");
        Integer total = (Integer) resultJson.get("total");
        String dominant_color = (String) resultJson.get("dominant_color");
        JSONArray dominant_color_explain_array =
            (JSONArray) resultJson.get("dominant_color_explain");
        List<String> dominant_color_explain_list =
            JSONObject.parseArray(dominant_color_explain_array.toJSONString(), String.class);
        // 换行符
        String lineSeparator = System.lineSeparator();
        StringBuilder dominant_color_explain_final = new StringBuilder();
        for (String s : dominant_color_explain_list) {
          dominant_color_explain_final.append(s).append(lineSeparator);
        }
        ReportModeOfThinking reportModeOfThinking =
            ReportModeOfThinking.builder()
                .k1(k1)
                .k2(k2)
                .k3(k3)
                .k4(k4)
                .k5(k5)
                .k6(k6)
                .hx1(hx1)
                .hx2(hx2)
                .zx1(zx1)
                .zx2(zx2)
                .zx3(zx3)
                .total(total)
                .dominantColor(dominant_color)
                .dominantColorExplain(dominant_color_explain_final.toString())
                .userId(userId)
                .surveyId("1588115916102221826")
                .projectId(projectId)
                .build();
        reportModeOfThinkingMapper.insert(reportModeOfThinking);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public void getTeamEnergyReport(String projectId, String userId) {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1587998270916538369")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      // 遍历问题列表
      for (SurUserResult result : results) {
        // 截取问题编号
        String option = StrUtil.cleanBlank(result.getQuestionKey().substring(0, 2));
        // 截取选项编号
        String value = result.getQuestionValue();
        param.put(option, value);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      //      String json =
      //          HttpClientUtil.doPost("http://106.14.140.92:7777/managementStyleEvaluation",
      // finalParam);
      //      JSONObject resultObj = (JSONObject) JSON.parse(json);
      //      resultJson = (JSONObject) resultObj.get("result");
    }
  }

  @Override
  public void getSelfDriveReport(String projectId, String userId) throws ConnectionException {
    // 查询该答卷人
    SurUser user = userMapper.selectById(userId);
    if (user != null && !user.getIsGenerate()) {
      // 查询用户详细答卷
      List<SurUserResult> results =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getSurveyId, "1588119405956087810")
                  .eq(SurUserResult::getProjectId, projectId)
                  .eq(SurUserResult::getUserId, userId));
      // 创建请求参数
      JSONObject param = new JSONObject();
      JSONObject resultJson;
      String questionIndex = "";
      // 遍历questionValueList
      for (SurUserResult surUserResult : results) {
        // 截取问题编号
        questionIndex =
            org.jeecg.modules.survey.client.utils.StrUtil.getNumeric(
                surUserResult.getQuestionValue().substring(0, 2));
        // 获取每个选项对应的分数
        int score = Integer.parseInt(surUserResult.getBasicScore().toString());
        param.put(questionIndex, score);
      }
      JSONObject params = new JSONObject();
      params.put("params", param);
      // 构造请求参数
      JSONObject finalParam = new JSONObject();
      finalParam.put("param", params);
      // 发送请求
      try {
        String json =
            HttpClientUtil.doPost("http://106.14.140.92:7777/individualSelfMotivation", finalParam);
        JSONObject resultObj = (JSONObject) JSON.parse(json);
        resultJson = (JSONObject) resultObj.get("result");
        Integer a1 = (Integer) resultJson.get("a1");
        Integer b1 = (Integer) resultJson.get("b1");
        Integer c1 = (Integer) resultJson.get("c1");
        Integer d1 = (Integer) resultJson.get("d1");
        Integer e1 = (Integer) resultJson.get("e1");
        Integer f1 = (Integer) resultJson.get("f1");
        Integer h1 = (Integer) resultJson.get("h1");
        Integer i1 = (Integer) resultJson.get("i1");
        Integer sum1 = (Integer) resultJson.get("sum1");
        Integer sum2 = (Integer) resultJson.get("sum2");
        String highest = (String) resultJson.get("highest");
        String second = (String) resultJson.get("second");
        String mainly = (String) resultJson.get("mainly");
        String instructions = (String) resultJson.get("instructions");
        ReportSelfMotivation reportSelfMotivation =
            ReportSelfMotivation.builder()
                .a1(a1)
                .b1(b1)
                .c1(c1)
                .d1(d1)
                .e1(e1)
                .f1(f1)
                .h1(h1)
                .i1(i1)
                .sum1(sum1)
                .sum2(sum2)
                .highest(highest)
                .second(second)
                .mainly(mainly)
                .instructions(instructions)
                .userId(userId)
                .projectId(projectId)
                .surveyId("1588119405956087810")
                .build();
        reportSelfMotivationMapper.insert(reportSelfMotivation);
      } catch (Exception e) {
        throw new ConnectionException();
      }
    }
  }

  @Override
  public Boolean generateReportForUser(ReportFormCreateReq req) throws ConnectionException {
    // 查询用户的问卷id
    List<String> surveyIdList =
        userSurveyService
            .list(
                new LambdaQueryWrapper<SurUserSurvey>()
                    .eq(SurUserSurvey::getUserId, req.getUserId())
                    .eq(SurUserSurvey::getProjectId, req.getProjectId()))
            .stream()
            .map(SurUserSurvey::getSurveyId)
            .distinct()
            .collect(Collectors.toList());
    for (String surveyId : surveyIdList) {
      switch (surveyId) {
          // 管理风格测评
        case "1587985620128116738":
          {
            getManagementStyleReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 团队角色测评
        case "1587994429726797826":
          {
            getTeamRoleReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 人才创造力测评
        case "1587995890414137346":
          {
            getTalentCreativityReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 个人事业驱动力测评
        case "1587998080067317762":
          {
            getPersonalCareerDrivingForceReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 团队能量测评
        case "1587998270916538369":
          {
            getTeamEnergyReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 人才思维方式测评
        case "1588115916102221826":
          {
            getAssessmentOfTalentThinkingModeReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 自我驱动力测评
        case "1588119405956087810":
          {
            getSelfDriveReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 管理者沟通自我认知测评
        case "1600696623468507137":
          {
            getManagersCommunicateSelfPerceptionReport(req.getProjectId(), req.getUserId());
            break;
          }
          // 人本化12字精髓运用调研
        case "1600734216985870338":
          {
            getHumanizationReport(req.getProjectId(), req.getUserId());
            break;
          }
      }
    }
    // 查询用户  修改报表生成状态
    SurUser surUser = userMapper.selectById(req.getUserId());
    surUser.setIsGenerate(true);
    userMapper.updateById(surUser);
    return true;
  }

  @Override
  public void transferManagementStyleEvaluation(
      MultipartFile file, String projectId, String surveyId) throws IOException {
    // 查询项目对象
    SurProject project = projectService.getById(projectId);
    // 查询问卷对象
    Survey survey = surveyMapper.selectById(surveyId);
    ExcelImportListener importListener = new ExcelImportListener();
    List<Map<Integer, String>> data =
        EasyExcel.read(file.getInputStream(), importListener).sheet(0).doReadSync();
    // 解析之后的数据
    List<Map<Integer, String>> result = importListener.getList();
    // 获取表头
    Map<Integer, String> head = result.get(0);
    // 姓名索引
    Integer nameIndex = null;
    // 电话索引
    Integer phoneIndex = null;
    // 题号索引
    Integer questionPrefIndex = null;
    // 题目索引
    Integer questionIndex = null;
    // 选项索引
    Integer choicePrefIndex = null;
    // 选项内容索引
    Integer choiceIndex = null;
    // 遍历表头map
    for (Map.Entry<Integer, String> entry : head.entrySet()) {
      if (entry.getValue().equals("姓名")) {
        nameIndex = entry.getKey();
      }
      if (entry.getValue().equals("手机号")
          || entry.getValue().equals("电话号码")
          || entry.getValue().equals("手机号码")
          || entry.getValue().equals("电话")
          || entry.getValue().equals("手机")) {
        phoneIndex = entry.getKey();
      }
      if (entry.getValue().equals("题号")) {
        questionPrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("题目")) {
        questionIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项")) {
        choicePrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项内容")) {
        choiceIndex = entry.getKey();
      }
    }
    // 遍历data
    for (Map<Integer, String> map : data) {
      // 答卷人名字
      String name = map.get(nameIndex);
      // 手机号
      String phone = map.get(phoneIndex);
      // 题号
      String questionPref = map.get(questionPrefIndex);
      // 题目
      String question = map.get(questionIndex);
      // 选项
      String choicePref = map.get(choicePrefIndex);
      // 选项内容
      String choice = map.get(choiceIndex);
      // 真实题目名称
      String realQuestion = questionPref + " " + question;
      // 真实选项名称
      String realChoice = choicePref + "." + choice;
      // 查询答题人对象
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, projectId)
                  .eq(SurUser::getName, name)
                  .eq(SurUser::getPhone, phone));
      if (!Objects.isNull(user)) {
        // 判断是否已经存在result对象
        SurResult exitResult =
            resultMapper.selectOne(
                new LambdaQueryWrapper<SurResult>()
                    .eq(SurResult::getProjectUid, projectId)
                    .eq(SurResult::getSurveyUid, surveyId)
                    .eq(SurResult::getUserUid, user.getId()));
        if (Objects.isNull(exitResult)) {
          // 构造result对象
          SurResult surResult = new SurResult();
          surResult.setSurveyUid(surveyId);
          surResult.setProjectUid(projectId);
          surResult.setSurvey(survey.getSurName());
          surResult.setProject(project.getProjectName());
          surResult.setUserUid(user.getId());
          resultMapper.insert(surResult);
        }
        // 构造userResult对象
        SurUserResult userResult = new SurUserResult();
        userResult.setPhone(phone);
        userResult.setSurveyId(surveyId);
        userResult.setProjectId(projectId);
        userResult.setUserId(user.getId());
        // 查询question对象
        SurQuestion surQuestion =
            surQuestionMapper.selectOne(
                new LambdaQueryWrapper<SurQuestion>()
                    .eq(SurQuestion::getSurveyUid, surveyId)
                    .eq(SurQuestion::getTitle, realQuestion));
        userResult.setQuestionKey(surQuestion.getContent());
        userResult.setQuestionTitle(realQuestion);
        userResult.setQuestionValue(realChoice);
        userResultMapper.insert(userResult);
      }
    }
  }

  @Override
  public void insertSurResult(MultipartFile file) throws IOException {
    ExcelImportListener importListener = new ExcelImportListener();
    List<Map<Integer, String>> data =
        EasyExcel.read(file.getInputStream(), importListener).sheet(0).doReadSync();
    // 解析之后的数据
    List<Map<Integer, String>> result = importListener.getList();
    // 获取表头
    Map<Integer, String> head = result.get(0);
    // 姓名索引
    Integer projectIdIndex = null;
    // 电话索引
    Integer surveyIdIndex = null;
    // 题号索引
    Integer userIdIndex = null;
    // 题目索引
    Integer nameIndex = null;
    // 选项索引
    Integer phoneIndex = null;
    // 选项内容索引
    Integer surveyIndex = null;
    Integer projectIndex = null;
    // 遍历表头map
    for (Map.Entry<Integer, String> entry : head.entrySet()) {
      if (entry.getValue().equals("projectId")) {
        projectIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("surveyId")) {
        surveyIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("userId")) {
        userIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("name")) {
        nameIndex = entry.getKey();
      }
      if (entry.getValue().equals("phone")) {
        phoneIndex = entry.getKey();
      }
      if (entry.getValue().equals("survey")) {
        surveyIndex = entry.getKey();
      }
      if (entry.getValue().equals("project")) {
        projectIndex = entry.getKey();
      }
    }
    // 遍历data
    for (Map<Integer, String> map : data) {
      // 答卷人名字
      String projectId = map.get(projectIdIndex);
      // 手机号
      String surveyId = map.get(surveyIdIndex);
      // 题号
      // 题目
      String name = map.get(nameIndex);
      // 选项
      String phone = map.get(phoneIndex);
      // 选项内容
      String survey = map.get(surveyIndex);
      String project = map.get(projectIndex);
      // 构造 sur_result对象
      SurResult surResult = new SurResult();
      surResult.setSurveyUid(surveyId);
      surResult.setProjectUid(projectId);
      surResult.setSurvey(survey);
      surResult.setProject(project);
      // 查询user对象
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, projectId)
                  .eq(SurUser::getName, name)
                  .eq(SurUser::getPhone, phone));
      if (user != null) {
        surResult.setUserUid(user.getId());
      }
      resultMapper.insert(surResult);
    }
  }

  @Override
  public void insertSurUserResult(MultipartFile file) throws IOException {
    Map<String, String> questionMap = getTeamRoleQuestionMap();
    ExcelImportListener importListener = new ExcelImportListener();
    List<Map<Integer, String>> data =
        EasyExcel.read(file.getInputStream(), importListener).sheet(0).doReadSync();
    // 解析之后的数据
    List<Map<Integer, String>> result = importListener.getList();
    // 获取表头
    Map<Integer, String> head = result.get(0);
    // 姓名索引
    Integer projectIdIndex = null;
    // 电话索引
    Integer surveyIdIndex = null;
    // 题目索引
    Integer nameIndex = null;
    // 选项索引
    Integer phoneIndex = null;
    Integer contentIndex = null;
    // 题号索引
    Integer questionPrefIndex = null;
    // 题目索引
    Integer questionIndex = null;
    // 选项索引
    Integer choicePrefIndex = null;
    // 选项内容索引
    Integer choiceIndex = null;
    Integer scoreIndex = null;
    // 遍历表头map
    for (Map.Entry<Integer, String> entry : head.entrySet()) {
      if (entry.getValue().equals("projectId")) {
        projectIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("surveyId")) {
        surveyIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("name")) {
        nameIndex = entry.getKey();
      }
      if (entry.getValue().equals("phone")) {
        phoneIndex = entry.getKey();
      }
      if (entry.getValue().equals("content")) {
        contentIndex = entry.getKey();
      }
      if (entry.getValue().equals("题号")) {
        questionPrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("题目")) {
        questionIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项")) {
        choicePrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项内容")) {
        choiceIndex = entry.getKey();
      }
      if (entry.getValue().equals("score")) {
        scoreIndex = entry.getKey();
      }
    }
    // 遍历data
    for (Map<Integer, String> map : data) {
      String projectId = map.get(projectIdIndex);
      String surveyId = map.get(surveyIdIndex);
      String questionPref = map.get(questionPrefIndex);
      String question = map.get(questionIndex);
      // 选项
      String choicePref = map.get(choicePrefIndex);
      // 选项内容
      String choice = map.get(choiceIndex);
      // 题目
      String name = map.get(nameIndex);
      // 选项
      String phone = map.get(phoneIndex);
      // 真实题目名称  1 我认为我能
      String realQuestion = questionPref + " " + question;
      // 真实选项名称   A.我生来就爱出注意
      String realChoice = choicePref + "." + choice;
      String content = map.get(contentIndex);
      String score = map.get(scoreIndex);
      //      String questionId = questionMap.get(content);
      String questionId = questionMap.get(content);
      // 查询答题人对象
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, projectId)
                  .eq(SurUser::getName, name)
                  .eq(SurUser::getPhone, phone));
      // 构造 sur_user_result对象
      // 构造userResult对象
      SurUserResult userResult = new SurUserResult();
      userResult.setPhone(phone);
      userResult.setSurveyId(surveyId);
      userResult.setProjectId(projectId);
      userResult.setUserId(user.getId());
      userResult.setQuestionId(questionId);
      //      userResult.setQuestionKey(realChoice);
      //      userResult.setQuestionTitle(realQuestion);
      //      userResult.setQuestionValue(score);
      //      userResult.setQuestionKey(content);
      //      userResult.setQuestionTitle(realQuestion);
      //      userResult.setQuestionValue(realChoice);
      //      SurQuestion surQuestion = surQuestionMapper.selectById(questionId);
      userResult.setQuestionKey(content);
      userResult.setQuestionTitle(realQuestion);
      userResult.setQuestionValue(realChoice);
      userResult.setBasicScore(score);
      userResultMapper.insert(userResult);
    }
  }

  @Override
  public void transferSatisfactionSurvey(MultipartFile file) throws IOException {
    Map<String, String> questionMap = getTeamRoleQuestionMap();
    ExcelImportListener importListener = new ExcelImportListener();
    List<Map<Integer, String>> data =
        EasyExcel.read(file.getInputStream(), importListener).sheet(0).doReadSync();
    // 解析之后的数据
    List<Map<Integer, String>> result = importListener.getList();
    // 获取表头
    Map<Integer, String> head = result.get(0);
    Integer projectIdIndex = null;
    // 电话索引
    Integer surveyIdIndex = null;
    // 题目索引
    Integer nameIndex = null;
    // 选项索引
    Integer phoneIndex = null;
    Integer contentIndex = null;
    // 题号索引
    Integer questionPrefIndex = null;
    // 题目索引
    Integer questionIndex = null;
    // 选项索引
    Integer choicePrefIndex = null;
    // 选项内容索引
    Integer choiceIndex = null;
    Integer scoreIndex = null;
    Integer userIdIndex = null;

    // 遍历表头map
    for (Map.Entry<Integer, String> entry : head.entrySet()) {
      if (entry.getValue().equals("projectId")) {
        projectIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("surveyId")) {
        surveyIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("name")) {
        nameIndex = entry.getKey();
      }
      if (entry.getValue().equals("phone")) {
        phoneIndex = entry.getKey();
      }
      if (entry.getValue().equals("content")) {
        contentIndex = entry.getKey();
      }
      if (entry.getValue().equals("题号")) {
        questionPrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("题目")) {
        questionIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项")) {
        choicePrefIndex = entry.getKey();
      }
      if (entry.getValue().equals("选项内容")) {
        choiceIndex = entry.getKey();
      }
      if (entry.getValue().equals("userId")) {
        userIdIndex = entry.getKey();
      }
      if (entry.getValue().equals("score")) {
        scoreIndex = entry.getKey();
      }
    }
    // 遍历data
    for (Map<Integer, String> map : data) {
      String projectId = map.get(projectIdIndex);
      String surveyId = map.get(surveyIdIndex);
      String userId = map.get(userIdIndex);
      String questionPref = map.get(questionPrefIndex);
      String question = map.get(questionIndex);
      // 选项
      String choicePref = map.get(choicePrefIndex);
      // 选项内容
      String choice = map.get(choiceIndex);
      // 题目
      String name = map.get(nameIndex);
      // 选项
      String phone = map.get(phoneIndex);
      // 真实题目名称  1 我认为我能
      String realQuestion = questionPref + " " + question;
      // 真实选项名称   A.我生来就爱出注意
      String realChoice = choicePref + "." + choice;
      String content = map.get(contentIndex);
      String score = map.get(scoreIndex);
      //      String questionId = questionMap.get(content);
      String questionId = questionMap.get(question);
      //      SurUser user = new SurUser();
      //      user.setProjectId(projectId);
      //      user.setType(1);
      //      user.setName(name);
      //      user.setPhone(phone);
      // 查找有没有同名的
      //      List<SurUser> surUserList =
      //          userMapper.selectList(
      //              new LambdaQueryWrapper<SurUser>()
      //                  .eq(SurUser::getName, name)
      //                  .eq(SurUser::getPhone, phone)
      //                  .eq(SurUser::getProjectId, projectId));
      //      if (surUserList.isEmpty()) {
      //        userMapper.insert(user);
      //      }
      // 构造用户问卷关系
      //      SurUserSurvey surUserSurvey = new SurUserSurvey();
      //      surUserSurvey.setUserId(userId);
      //      surUserSurvey.setPhone(phone);
      //      surUserSurvey.setSurveyId(surveyId);
      //      surUserSurvey.setProjectId(projectId);
      //      surUserSurvey.setStatus(2);
      //      surUserSurveyMapper.insert(surUserSurvey);

      // 判断是否已经存在result对象
      //      SurResult exitResult =
      //          resultMapper.selectOne(
      //              new LambdaQueryWrapper<SurResult>()
      //                  .eq(SurResult::getProjectUid, projectId)
      //                  .eq(SurResult::getSurveyUid, surveyId)
      //                  .eq(SurResult::getUserUid, user.getId()));
      // 构造result对象
      //      SurResult surResult = new SurResult();
      //      surResult.setSurveyUid(surveyId);
      //      surResult.setProjectUid(projectId);
      //      surResult.setSurvey("满意度调查");
      //      surResult.setProject("释放活力 共创梦想 创新执行（满意度调查）");
      //      surResult.setUserUid(userId);
      //      resultMapper.insert(surResult);

      // 构造userResult对象
      SurUserResult userResult = new SurUserResult();
      userResult.setPhone(phone);
      userResult.setSurveyId(surveyId);
      userResult.setProjectId(projectId);
      // 查询用户
      SurUser user =
          userMapper.selectOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getName, name)
                  .eq(SurUser::getPhone, phone)
                  .eq(SurUser::getProjectId, projectId));
      userResult.setUserId(user.getId());
      userResult.setQuestionId(questionId);
      //      userResult.setQuestionKey(realChoice);
      //      userResult.setQuestionTitle(realQuestion);
      //      userResult.setQuestionValue(score);
      //      userResult.setQuestionKey(content);
      //      userResult.setQuestionTitle(realQuestion);
      //      userResult.setQuestionValue(realChoice);

      userResult.setQuestionKey(question);
      userResult.setQuestionTitle(content);
      userResult.setQuestionValue(choice);
      userResult.setBasicScore(score);
      userResultMapper.insert(userResult);
    }
  }

  private Map<String, Object> getChoiceMap(String surveyId, String key) {
    // 匹配数据库中的分数
    // 根据问卷id和key查询对应的问题来获取问题id
    SurQuestionProject surQuestion =
        userSurveyQuestionMapper.selectOne(
            new LambdaQueryWrapper<SurQuestionProject>()
                .eq(SurQuestionProject::getSurveyUid, surveyId)
                .eq(SurQuestionProject::getContent, key));
    Map<String, Object> map = new LinkedHashMap<>();
    // 根据问题id查询数据库中的选项对象
    if (surQuestion != null) {
      SurQuestionChoiceProject surQuestionChoice =
          userSurveyChoiceMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionChoiceProject>()
                  .eq(SurQuestionChoiceProject::getQuestionId, surQuestion.getId()));
      // 获取选项数组
      String content = surQuestionChoice.getContent();
      List<String> choiceList = JSONObject.parseArray(content, String.class);
      //      List<String> choiceList = ListUtils.toList(content);
      //      // 如果选项数组为空
      //      if (choiceList == null || choiceList.size() == 1 || choiceList.size() == 0) {
      //        return null;
      //      }
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
          // 如果是单选题
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

  private Map<String, Object> getSurveyChoiceMap(String surveyId, String key) {
    // 匹配数据库中的分数
    // 根据问卷id和key查询对应的问题来获取问题id
    SurQuestion surQuestion =
        surQuestionMapper.selectOne(
            new LambdaQueryWrapper<SurQuestion>()
                .eq(SurQuestion::getSurveyUid, surveyId)
                .eq(SurQuestion::getContent, key));
    Map<String, Object> map = new LinkedHashMap<>();
    // 根据问题id查询数据库中的选项对象
    if (surQuestion != null) {
      SurQuestionChoice surQuestionChoice =
          surQuestionChoiceMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionChoice>()
                  .eq(SurQuestionChoice::getQuestionId, surQuestion.getId()));
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

  /**
   * @Author 一笙 @Description //TODO 保存调查项目拆分答卷人每道题的答案 @Date 13:01 2022/11/29
   *
   * @param project 项目对象
   * @param surveyId 问卷id
   * @param user 答卷人对象
   * @param sysUser 系统租户对象
   * @param resultMap 问卷结果集
   * @return void
   */
  private void saveInvestigationUserResult(
      SurProject project,
      String surveyId,
      SurUser user,
      SysUser sysUser,
      Map<String, Object> resultMap) {
    // string 类型的问卷结果集
    Map<String, Object> map1;
    // bool 类型的问卷结果集
    Map<String, Object> map2;
    // map 类型的问卷结果集
    Map<String, Object> map3;
    // collection 类型的问卷结果姐
    Map<String, Object> map4;
    Iterator<Map.Entry<String, Object>> entries = resultMap.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, Object> next = entries.next();
      String key = next.getKey();
      Object value = next.getValue();
      // 根据key和surveyid 查询question对象
      SurQuestionProject question =
          userSurveyQuestionMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionProject>()
                  .eq(SurQuestionProject::getSurveyUid, surveyId)
                  .eq(SurQuestionProject::getContent, key));
      // 如果是String类型
      if (value instanceof String) {
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        personResult.setQuestionValue(value.toString());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map1 = getChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map1 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map1.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      // 如果是布尔值
      if (value instanceof Boolean) {
        Boolean booleanValue = (Boolean) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setQuestionValue(booleanValue.toString());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map2 = getChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map2 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map2.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      // 如果是map值
      if (value instanceof Map) {
        Map<String, Object> objectMap = (Map<String, Object>) value;
        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();
        // 查询父问题对象
        SurQuestionProject parentQuestion =
            userSurveyQuestionMapper.selectOne(
                new LambdaQueryWrapper<SurQuestionProject>()
                    .eq(SurQuestionProject::getSurveyUid, surveyId)
                    .eq(SurQuestionProject::getContent, key));
        // 如果该问题是 多行文本题目
        if ("multipletext".equals(parentQuestion.getTypeId())) {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            SurUserResult personResult = new SurUserResult();
            personResult.setQuestionId(parentQuestion.getId());
            personResult.setQuestionType(parentQuestion.getTypeId());
            personResult.setQuestionTitle(parentQuestion.getTitle());
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(key);
            String questionKey = entry.getKey();
            // 去掉空格
            String finalQuestionKey = StrUtil.cleanBlank(questionKey);
            personResult.setQuestionValue(finalQuestionKey);
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            personResult.setBasicScore(entry.getValue());
            userResultMapper.insert(personResult);
          }
        }
        // 如果为 矩阵题目
        else {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String questionKey = entry.getKey();
            SurUserResult personResult = new SurUserResult();
            // 根据key查询问题对象
            SurQuestionProject surQuestion =
                userSurveyQuestionMapper.selectOne(
                    new LambdaQueryWrapper<SurQuestionProject>()
                        .eq(SurQuestionProject::getSurveyUid, surveyId)
                        .eq(SurQuestionProject::getContent, questionKey));
            if (!Objects.isNull(surQuestion)) {
              personResult.setQuestionId(surQuestion.getId());
              personResult.setQuestionTitle(surQuestion.getTitle());
              personResult.setQuestionType(surQuestion.getTypeId());
            }
            if (!Objects.isNull(parentQuestion)) {
              personResult.setParentQuestionId(parentQuestion.getId());
              personResult.setParentQuestionContent(parentQuestion.getContent());
              personResult.setQuestionType(surQuestion.getTypeId());
            }
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(questionKey);
            personResult.setQuestionValue(entry.getValue().toString());
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            map3 = getChoiceMap(surveyId, questionKey);
            // 遍历选项和分数的map
            if (map3 == null) {
              personResult.setBasicScore(0);
            } else {
              Object o = map3.get(entry.getValue().toString());
              if (o == null) {
                personResult.setBasicScore(0);
              } else {
                personResult.setBasicScore(o);
              }
            }
            userResultMapper.insert(personResult);
          }
        }
      }
      // 如果是集合类型
      if (value instanceof Collection) {
        List<String> resultList = (List) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        // 将list转化为json字符串
        String json = JSON.toJSONString(resultList);
        personResult.setQuestionValue(json);
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map4 = getChoiceMap(surveyId, key);
        int sum = 0;
        // 遍历选项和分数的map
        if (map4 == null) {
          personResult.setBasicScore(0);
        } else {
          // 遍历选项数组
          for (String choice : resultList) {
            Object o = map4.get(choice);
            if (o != null) {
              // 每个选项的分数是
              int v = Integer.parseInt(((String) o));
              sum += v;
            }
          }
          personResult.setBasicScore(sum);
        }
        userResultMapper.insert(personResult);
      }
    }
    // 每提交一个用户就将已收集数量加1
    project.setCollectNumber(project.getCollectNumber() + 1);
    projectService.updateById(project);
  }

  /**
   * @Author 一笙 @Description //TODO 保存测评项目拆分答卷人每道题的答案 @Date 13:01 2022/11/29
   *
   * @param project 项目对象
   * @param surveyId 问卷id
   * @param user 答卷人对象
   * @param sysUser 系统租户对象
   * @param resultMap 问卷结果集
   * @return void
   */
  private void saveEvaluationUserResult(
      SurProject project,
      String surveyId,
      SurUser user,
      SysUser sysUser,
      Map<String, Object> resultMap) {
    Map<String, Object> map1;
    Map<String, Object> map2;
    Map<String, Object> map3;
    Map<String, Object> map4;
    Iterator<Map.Entry<String, Object>> entries = resultMap.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, Object> next = entries.next();
      // 问题一这种类型的key
      String key = next.getKey();
      Object value = next.getValue();
      // 查询对应的问题
      SurQuestion question =
          surQuestionMapper.selectOne(
              new LambdaQueryWrapper<SurQuestion>()
                  .eq(SurQuestion::getContent, key)
                  .eq(SurQuestion::getSurveyUid, surveyId));
      // 如果是String类型
      if (value instanceof String) {
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setQuestionValue(value.toString());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map1 = getSurveyChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map1 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map1.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      // 如果是布尔值
      if (value instanceof Boolean) {
        Boolean booleanValue = (Boolean) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setQuestionValue(booleanValue.toString());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map2 = getSurveyChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map2 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map2.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      if (value instanceof Map) {
        Map<String, Object> objectMap = (Map<String, Object>) value;
        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();
        // 查询父问题对象
        SurQuestion parentQuestion =
            surQuestionMapper.selectOne(
                new LambdaQueryWrapper<SurQuestion>()
                    .eq(SurQuestion::getSurveyUid, surveyId)
                    .eq(SurQuestion::getContent, key));
        // 如果该问题是 多行文本题目
        if ("multipletext".equals(parentQuestion.getTypeId())) {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            SurUserResult personResult = new SurUserResult();
            personResult.setQuestionId(parentQuestion.getId());
            personResult.setQuestionType(parentQuestion.getTypeId());
            personResult.setQuestionTitle(parentQuestion.getTitle());
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(key);
            String questionKey = entry.getKey();
            // 去掉空格
            String finalQuestionKey = StrUtil.cleanBlank(questionKey);
            personResult.setQuestionValue(finalQuestionKey);
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            personResult.setBasicScore(entry.getValue());
            userResultMapper.insert(personResult);
          }
        }
        // 如果为 矩阵题目
        else {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String questionKey = entry.getKey();
            SurUserResult personResult = new SurUserResult();
            // 根据key查询问题对象
            SurQuestion surQuestion =
                surQuestionMapper.selectOne(
                    new LambdaQueryWrapper<SurQuestion>()
                        .eq(SurQuestion::getSurveyUid, surveyId)
                        .eq(SurQuestion::getContent, questionKey));
            if (!Objects.isNull(surQuestion)) {
              personResult.setQuestionId(surQuestion.getId());
              personResult.setQuestionTitle(surQuestion.getTitle());
              personResult.setQuestionType(surQuestion.getTypeId());
            }
            if (!Objects.isNull(parentQuestion)) {
              personResult.setParentQuestionId(parentQuestion.getId());
              personResult.setParentQuestionContent(parentQuestion.getContent());
              personResult.setQuestionType(parentQuestion.getTypeId());
            }
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(questionKey);
            personResult.setQuestionValue(entry.getValue().toString());
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            map3 = getSurveyChoiceMap(surveyId, questionKey);
            // 遍历选项和分数的map
            if (map3 == null) {
              personResult.setBasicScore(0);
            } else {
              Object o = map3.get(entry.getValue().toString());
              if (o == null) {
                personResult.setBasicScore(0);
              } else {
                personResult.setBasicScore(o);
              }
            }
            userResultMapper.insert(personResult);
          }
        }
      }
      // 如果是集合类型
      if (value instanceof Collection) {
        List<String> resultList = (List) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        // 将list转化为json字符串
        String json = JSON.toJSONString(resultList);
        personResult.setQuestionValue(json);
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map4 = getSurveyChoiceMap(surveyId, key);
        int sum = 0;
        // 遍历选项和分数的map
        if (map4 == null) {
          personResult.setBasicScore(0);
        } else {
          // 遍历选项数组
          for (String choice : resultList) {
            Object o = map4.get(choice);
            if (o != null) {
              // 每个选项的分数是
              int v = Integer.parseInt(((String) o));
              sum += v;
            }
          }
          personResult.setBasicScore(sum);
        }
        userResultMapper.insert(personResult);
      }
    }
    // 每提交一个用户就将已收集数量加1
    project.setCollectNumber(project.getCollectNumber() + 1);
    projectService.updateById(project);
  }

  /**
   * @Author 一笙 @Description //TODO 保存360项目拆分答卷人每道题的答案 @Date 13:01 2022/11/29
   *
   * @param project 项目对象
   * @param surveyId 问卷id
   * @param user 答卷人对象
   * @param evaluationUser 评价人对象
   * @param sysUser 系统租户对象
   * @param resultMap 问卷结果集
   * @return void
   */
  private void save360UserResult(
      SurProject project,
      String surveyId,
      SurUser user,
      SurEvaluationUser evaluationUser,
      SysUser sysUser,
      Map<String, Object> resultMap) {
    Map<String, Object> map1;
    Map<String, Object> map2;
    Map<String, Object> map3;
    Map<String, Object> map4;
    Iterator<Map.Entry<String, Object>> entries = resultMap.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, Object> next = entries.next();
      String key = next.getKey();
      Object value = next.getValue();
      // 查询对应的问题
      SurQuestionProject question =
          userSurveyQuestionMapper.selectOne(
              new LambdaQueryWrapper<SurQuestionProject>()
                  .eq(SurQuestionProject::getContent, key)
                  .eq(SurQuestionProject::getSurveyUid, surveyId));
      // 如果是String类型
      if (value instanceof String) {
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setQuestionValue(value.toString());
        personResult.setUserLevel(evaluationUser.getEvaluatorLevel());
        personResult.setEvaluatedId(evaluationUser.getUserId());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map1 = getChoiceMap(surveyId, key);
        // 遍历选项和分数的map
        if (map1 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map1.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      // 如果是布尔值
      if (value instanceof Boolean) {
        Boolean booleanValue = (Boolean) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        personResult.setQuestionValue(booleanValue.toString());
        personResult.setUserLevel(evaluationUser.getEvaluatorLevel());
        personResult.setEvaluatedId(evaluationUser.getUserId());
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map2 = getChoiceMap(surveyId, key);
        if (map2 == null) {
          personResult.setBasicScore(0);
        } else {
          Object o = map2.get(value.toString());
          if (o == null) {
            personResult.setBasicScore(0);
          } else {
            personResult.setBasicScore(o);
          }
        }
        userResultMapper.insert(personResult);
      }
      if (value instanceof Map) {
        Map<String, Object> objectMap = (Map<String, Object>) value;
        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();
        // 查询父问题对象
        SurQuestionProject parentQuestion =
            userSurveyQuestionMapper.selectOne(
                new LambdaQueryWrapper<SurQuestionProject>()
                    .eq(SurQuestionProject::getSurveyUid, surveyId)
                    .eq(SurQuestionProject::getContent, key));
        // 如果该问题是 多行文本题目
        if ("multipletext".equals(parentQuestion.getTypeId())) {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String questionKey = entry.getKey();
            String fianlQuestionKey = StrUtil.cleanBlank(questionKey);
            SurUserResult personResult = new SurUserResult();
            personResult.setQuestionId(parentQuestion.getId());
            personResult.setQuestionType(parentQuestion.getTypeId());
            personResult.setQuestionTitle(parentQuestion.getTitle());
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(key);
            personResult.setQuestionValue(fianlQuestionKey);
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            personResult.setBasicScore(entry.getValue());
            personResult.setUserLevel(evaluationUser.getEvaluatorLevel());
            personResult.setEvaluatedId(evaluationUser.getUserId());
            userResultMapper.insert(personResult);
          }
        }
        // 如果为 矩阵题目
        else {
          while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String questionKey = entry.getKey();
            SurUserResult personResult = new SurUserResult();
            // 根据key查询问题对象
            SurQuestionProject surQuestion =
                userSurveyQuestionMapper.selectOne(
                    new LambdaQueryWrapper<SurQuestionProject>()
                        .eq(SurQuestionProject::getSurveyUid, surveyId)
                        .eq(SurQuestionProject::getContent, questionKey));
            if (!Objects.isNull(surQuestion)) {
              personResult.setQuestionId(surQuestion.getId());
              personResult.setQuestionTitle(surQuestion.getTitle());
              personResult.setQuestionType(surQuestion.getTypeId());
            }
            if (!Objects.isNull(parentQuestion)) {
              personResult.setParentQuestionId(parentQuestion.getId());
              personResult.setParentQuestionContent(parentQuestion.getContent());
              personResult.setQuestionType(surQuestion.getTypeId());
            }
            personResult.setProjectId(project.getId());
            personResult.setSurveyId(surveyId);
            personResult.setUserId(user.getId());
            personResult.setQuestionKey(questionKey);
            personResult.setQuestionValue(entry.getValue().toString());
            personResult.setUserLevel(evaluationUser.getEvaluatorLevel());
            personResult.setEvaluatedId(evaluationUser.getUserId());
            if (sysUser != null) {
              personResult.setTenantId(sysUser.getRelTenantIds());
            }
            map3 = getChoiceMap(surveyId, questionKey);
            // 遍历选项和分数的map
            if (map3 == null) {
              personResult.setBasicScore(0);
            } else {
              Object o = map3.get(entry.getValue().toString());
              if (o == null) {
                personResult.setBasicScore(0);
              } else {
                personResult.setBasicScore(o);
              }
            }
            userResultMapper.insert(personResult);
          }
        }
      }
      // 如果是集合类型
      if (value instanceof Collection) {
        List<String> resultList = (List) value;
        SurUserResult personResult = new SurUserResult();
        personResult.setProjectId(project.getId());
        personResult.setSurveyId(surveyId);
        personResult.setUserId(user.getId());
        personResult.setQuestionKey(key);
        personResult.setUserLevel(evaluationUser.getEvaluatorLevel());
        personResult.setEvaluatedId(evaluationUser.getUserId());
        if (!Objects.isNull(question)) {
          personResult.setQuestionId(question.getId());
          personResult.setQuestionType(question.getTypeId());
          personResult.setQuestionTitle(question.getTitle());
        }
        // 将list转化为json字符串
        String json = JSON.toJSONString(resultList);
        personResult.setQuestionValue(json);
        if (sysUser != null) {
          personResult.setTenantId(sysUser.getRelTenantIds());
        }
        map4 = getChoiceMap(surveyId, key);
        int sum = 0;
        // 遍历选项和分数的map
        if (map4 == null) {
          personResult.setBasicScore(0);
        } else {
          // 遍历选项数组
          for (String choice : resultList) {
            Object o = map4.get(choice);
            if (o != null) {
              // 每个选项的分数是
              int v = Integer.parseInt(((String) o));
              sum += v;
            }
          }
          personResult.setBasicScore(sum);
        }
        userResultMapper.insert(personResult);
      }
    }
    // 每提交一个用户就将已收集数量加1
    project.setCollectNumber(project.getCollectNumber() + 1);
    projectService.updateById(project);
  }

  // 复制问卷
  private SurSurveyProject copy(SurSurveyProject resource, String name) {
    // 复制问卷对象
    SurSurveyProject copySurvey = new SurSurveyProject();
    copySurvey.setType(resource.getType());
    copySurvey.setSurName(name);
    copySurvey.setSurContent(resource.getSurContent());
    copySurvey.setJsonPreview(resource.getJsonPreview());
    copySurvey.setSrcId(resource.getId());
    copySurvey.setTenantId(resource.getTenantId());
    userSurveyMapper.insert(copySurvey);
    // 查询问卷题目对象
    List<SurQuestionProject> questionList =
        userSurveyQuestionMapper.selectList(
            new LambdaQueryWrapper<SurQuestionProject>()
                .eq(SurQuestionProject::getSurveyUid, resource.getId()));
    // 遍历问卷题目对象
    questionList.forEach(
        question -> {
          // 复制问卷题目对象
          SurQuestionProject copyQuestion = new SurQuestionProject();
          BeanUtils.copyProperties(question, copyQuestion);
          copyQuestion.setSurveyUid(copySurvey.getId());
          copyQuestion.setId(null);
          copyQuestion.setCreateTime(null);
          copyQuestion.setUpdateTime(null);
          userSurveyQuestionMapper.insert(copyQuestion);
          // 根据题目id查询选项对象
          List<SurQuestionChoiceProject> surveyChoicesByQuestion =
              userSurveyChoiceMapper.selectList(
                  new LambdaQueryWrapper<SurQuestionChoiceProject>()
                      .eq(SurQuestionChoiceProject::getQuestionId, question.getId())
                      .eq(SurQuestionChoiceProject::getSurveyUid, resource.getId()));
          // 遍历选项对象
          surveyChoicesByQuestion.forEach(
              option -> {
                // 复制选项对象
                SurQuestionChoiceProject copyOption = new SurQuestionChoiceProject();
                BeanUtils.copyProperties(option, copyOption);
                copyOption.setSurveyUid(copySurvey.getId());
                copyOption.setQuestionId(copyQuestion.getId());
                copyOption.setId(null);
                copyOption.setCreateTime(null);
                copyOption.setUpdateTime(null);
                userSurveyChoiceMapper.insert(copyOption);
              });
        });
    return copySurvey;
  }

  //        计算360度项目被评价人的分数   被评价人id
  private double getEvaluatedScore(String id, String projectId) {
    // 自评分数
    double selfSum = 0;
    // 上级分数
    double superiorSum = 0;
    // 同事分数
    double colleagueSum = 0;
    // 下级分数
    double subordinateSum = 0;
    // 其他分数
    double otherSum = 0;
    // 根据被评价人id查询评价人
    List<SurEvaluationUser> evaluatorList =
        evaluationUserMapper.selectList(
            new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, id));
    for (SurEvaluationUser evaluator : evaluatorList) {
      // 查询评价人的答卷
      List<SurUserResult> evaluatorResults =
          userResultMapper.selectList(
              new LambdaQueryWrapper<SurUserResult>()
                  .eq(SurUserResult::getUserId, evaluator.getEvaluatorId())
                  .eq(SurUserResult::getEvaluatedId, id));
      // 获取其中自评的答卷数组
      List<SurUserResult> selfResults =
          evaluatorResults.stream()
              .filter(evaluatorResult -> evaluatorResult.getUserLevel() == 0)
              .collect(Collectors.toList());
      // 获取其中上级的答卷数组
      List<SurUserResult> superiorResults =
          evaluatorResults.stream()
              .filter(evaluatorResult -> evaluatorResult.getUserLevel() == 1)
              .collect(Collectors.toList());
      // 获取其中同事的答卷数组
      List<SurUserResult> colleagueResults =
          evaluatorResults.stream()
              .filter(evaluatorResult -> evaluatorResult.getUserLevel() == 2)
              .collect(Collectors.toList());
      // 获取其中下级的答卷数组
      List<SurUserResult> subordinateResults =
          evaluatorResults.stream()
              .filter(evaluatorResult -> evaluatorResult.getUserLevel() == 3)
              .collect(Collectors.toList());
      // 获取其中其他的答卷数组
      List<SurUserResult> otherResults =
          evaluatorResults.stream()
              .filter(evaluatorResult -> evaluatorResult.getUserLevel() == 4)
              .collect(Collectors.toList());
      // 累加分数  自评
      for (SurUserResult evaluatorResult : selfResults) {
        // 获取每个答卷的分数
        double score = Double.parseDouble((String) evaluatorResult.getBasicScore());
        selfSum += score;
      }
      // 累加分数  上级
      for (SurUserResult evaluatorResult : superiorResults) {
        // 获取每个答卷的分数
        double score = Double.parseDouble((String) evaluatorResult.getBasicScore());
        superiorSum += score;
      }
      // 累加分数  同事
      for (SurUserResult evaluatorResult : colleagueResults) {
        // 获取每个答卷的分数
        double score = Double.parseDouble((String) evaluatorResult.getBasicScore());
        colleagueSum += score;
      }
      // 累加分数  下级
      for (SurUserResult evaluatorResult : subordinateResults) {
        // 获取每个答卷的分数
        double score = Double.parseDouble((String) evaluatorResult.getBasicScore());
        subordinateSum += score;
      }
      // 累加分数  其他
      for (SurUserResult evaluatorResult : otherResults) {
        // 获取每个答卷的分数
        double score = Double.parseDouble((String) evaluatorResult.getBasicScore());
        otherSum += score;
      }
    }
    // 查询项目权重
    SurEvaluationWeight weight =
        evaluationWeightMapper.selectOne(
            new LambdaQueryWrapper<SurEvaluationWeight>()
                .eq(SurEvaluationWeight::getProjectId, projectId));
    // 如果没有权重  默认计算
    if (weight == null) {
      // 自评分数
      double selfScore = selfSum * 0.25;
      // 上级分数
      double superiorScore = superiorSum * 0.25;
      // 同事分数
      double colleagueScore = colleagueSum * 0.25;
      // 下级分数
      double subordinateScore = subordinateSum * 0.25;
      // 其他分数
      //      double otherScore = otherSum * 0.20;
      // 总分
      double totalScore = selfScore + superiorScore + colleagueScore + subordinateScore;
      return totalScore;
    } else {
      // 自评分数
      double selfScore = selfSum * (Double.parseDouble(weight.getSelfWeight()) / 100);
      // 上级分数
      double superiorScore = superiorSum * (Double.parseDouble(weight.getSuperiorWeight()) / 100);
      // 同事分数
      double colleagueScore =
          colleagueSum * (Double.parseDouble(weight.getColleagueWeight()) / 100);
      // 下级分数
      double subordinateScore =
          subordinateSum * (Double.parseDouble(weight.getSubordinateWeight()) / 100);
      // 其他分数
      double otherScore = otherSum * (Double.parseDouble(weight.getOtherWeight()) / 100);
      // 总分
      double totalScore =
          selfScore + superiorScore + colleagueScore + subordinateScore + otherScore;
      return totalScore;
    }
  }

  private Map<String, String> getTeamRoleQuestionMap() {
    Map<String, String> result = new HashMap<>();
    // 查询
    List<SurQuestion> questions =
        surQuestionMapper.selectList(
            new LambdaQueryWrapper<SurQuestion>()
                .eq(SurQuestion::getSurveyUid, "1587998080067317762"));

    //    List<SurQuestionProject> questions =
    //        userSurveyQuestionMapper.selectList(
    //            new LambdaQueryWrapper<SurQuestionProject>()
    //                .eq(SurQuestionProject::getSurveyUid, "1601834534257188865"));
    for (SurQuestion question : questions) {
      result.put(question.getContent(), question.getId());
    }
    return result;
  }
}

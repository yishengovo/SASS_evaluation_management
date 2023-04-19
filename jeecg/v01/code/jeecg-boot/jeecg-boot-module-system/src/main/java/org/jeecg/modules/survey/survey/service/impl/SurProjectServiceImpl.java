package org.jeecg.modules.survey.survey.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.*;
import java.util.stream.Collectors;
import org.jeecg.modules.survey.client.entity.SurTag;
import org.jeecg.modules.survey.client.req.ProjectAdvancedQueryReq;
import org.jeecg.modules.survey.client.service.ISurTagService;
import org.jeecg.modules.survey.survey.dto.*;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.*;
import org.jeecg.modules.survey.survey.req.CollectReq;
import org.jeecg.modules.survey.survey.req.ProjectEditReq;
import org.jeecg.modules.survey.survey.req.SetProjectSurveyReq;
import org.jeecg.modules.survey.survey.service.ISurProjectService;
import org.jeecg.modules.survey.survey.service.ISurveyService;
import org.jeecg.modules.survey.survey.utils.NumUtils;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysTenantService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 问卷项目表 @Author: jeecg-boot @Date: 2022-07-01 @Version: V1.0
 */
@Service
public class SurProjectServiceImpl extends ServiceImpl<SurProjectMapper, SurProject>
    implements ISurProjectService {
  @Autowired private SurProjectUserMapper projectUserMapper;
  @Autowired private SurProjectSurveyMapper projectSurveyMapper;
  @Autowired private SurUserMapper userMapper;
  @Autowired private SurUserResultMapper userResultMapper;
  @Autowired private SurResultMapper resultMapper;
  @Autowired private SurveyMapper surveyMapper;
  @Autowired private SurSurveyProjectMapper surveyProjectMapper;
  @Autowired private SurQuestionProjectMapper questionProjectMapper;
  @Autowired private SurQuestionChoiceProjectMapper questionChoiceProjectMapper;
  @Autowired private SurQuestionMapper questionMapper;
  @Autowired private SurQuestionChoiceMapper questionChoiceMapper;
  @Autowired private ISysTenantService sysTenantService;
  @Autowired private ISurTagService tagService;
  @Autowired private ISurveyService surveyService;

  @Override
  public Boolean setSurvey(SetProjectSurveyReq req) {
    // 根据项目id查询项目
    SurProject surProject = this.getById(req.getId());
    surProject.setSurveySrcId(req.getSurveyId());
    if (req.getRowKeys() != null) {
      surProject.setRowKeys(JSON.toJSONString(req.getRowKeys()));
    }
    // 查询出该问卷模板
    SurSurveyProject exitSurvey =
        surveyProjectMapper.selectOne(
            new LambdaQueryWrapper<SurSurveyProject>()
                .eq(SurSurveyProject::getSrcId, req.getSurveyId()));
    this.updateById(surProject);
    // 如果数据库已经存在该问卷的模板，则不需要再次插入
    if (exitSurvey != null) {
      surProject.setSurveyCurrentId(exitSurvey.getId());
      this.updateById(surProject);
      return true;
    }
    Survey survey = surveyMapper.selectById(req.getSurveyId());
    SurSurveyProject surSurveyProject = new SurSurveyProject();
    // 复制问卷
    BeanUtils.copyProperties(survey, surSurveyProject);
    surSurveyProject.setId(null);
    surSurveyProject.setSrcId(req.getSurveyId());
    surveyProjectMapper.insert(surSurveyProject);
    surProject.setSurveyCurrentId(surSurveyProject.getId());
    this.updateById(surProject);
    // 查询问卷的问题和选项
    List<SurQuestion> surQuestions =
        questionMapper.selectList(
            new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()));
    surQuestions.forEach(
        surQuestion -> {
          // 查询对应的选项数组
          SurQuestionChoice questionChoice =
              questionChoiceMapper.selectOne(
                  new LambdaQueryWrapper<SurQuestionChoice>()
                      .eq(SurQuestionChoice::getSurveyUid, req.getSurveyId())
                      .eq(SurQuestionChoice::getQuestionId, surQuestion.getId()));
          SurQuestionProject questionProject = new SurQuestionProject();
          BeanUtils.copyProperties(surQuestion, questionProject);
          // 替换问卷id
          questionProject.setId(null);
          questionProject.setSurveyUid(surSurveyProject.getId());
          questionProjectMapper.insert(questionProject);
          // 复制选项
          SurQuestionChoiceProject surQuestionChoiceProject = new SurQuestionChoiceProject();
          BeanUtils.copyProperties(questionChoice, surQuestionChoiceProject);
          surQuestionChoiceProject.setId(null);
          surQuestionChoiceProject.setQuestionId(questionProject.getId());
          surQuestionChoiceProject.setSurveyUid(surSurveyProject.getId());
          questionChoiceProjectMapper.insert(surQuestionChoiceProject);
        });
    // 项目问卷关系表
    SurProjectSurvey one =
        projectSurveyMapper.selectOne(
            new LambdaQueryWrapper<SurProjectSurvey>()
                .eq(SurProjectSurvey::getProjectId, req.getId()));
    if (one != null) {
      one.setSurveyId(surSurveyProject.getId());
      projectSurveyMapper.updateById(one);
    } else {
      SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
      surProjectSurvey.setProjectId(surProject.getId());
      surProjectSurvey.setSurveyId(req.getSurveyId());
      projectSurveyMapper.insert(surProjectSurvey);
    }

    return true;
  }

  @Override
  public Boolean editSurvey(ProjectEditReq req) {
    // 根据项目id查询项目
    SurProject project = this.getById(req.getId());
    if (req.getSurveyId() != null) {
      project.setSurveySrcId(req.getSurveyId());
      SurProjectSurvey projectSurvey =
          projectSurveyMapper.selectOne(
              new LambdaQueryWrapper<SurProjectSurvey>()
                  .eq(SurProjectSurvey::getProjectId, project.getId()));
      projectSurvey.setSurveyId(req.getSurveyId());
      projectSurveyMapper.updateById(projectSurvey);
    }
    if (req.getRowKeys() != null) {
      project.setRowKeys(req.getRowKeys().toString());
    }
    // 修改项目主表
    this.updateById(project);
    return true;
  }

  @Override
  public Boolean setUser(ProjectEditReq req) {
    // 修改关系表
    // 根据项目id查询项目
    SurProject project = this.getById(req.getId());
    if (req.getUserRowKeys() != null) {
      project.setUserRowKeys(JSON.toJSONString(req.getUserRowKeys()));
    }
    //       //先删掉所有的用户和项目用户关系
    //       List<SurProjectUser> projectUsers = projectUserService.list(new
    // LambdaQueryWrapper<SurProjectUser>().eq(SurProjectUser::getProjectId, project.getId()));
    //       //获取到与本项目有关的用户id
    //       List<String> userIds =
    // projectUsers.stream().map(SurProjectUser::getUserId).distinct().collect(Collectors.toList());
    //       //删除用户
    //       userService.removeByIds(userIds);
    //       projectUserService.removeByIds(projectUsers);
    // 再创建新的关系
    // 如果用户数组不为空
    List<Object> allUsers = new ArrayList<>();
    if (req.getUserList() != null) {
      List<SurUser> userList = req.getUserList();
      allUsers.addAll(userList);
      for (SurUser user : userList) {
        // 判断用户列表中有没有id
        // 有id则更新用户信息
        if (user.getId() != null) {
          user.setProjectId(project.getId());
          userMapper.updateById(user);
        } else {
          // 如果没有id 说明是新增加的用户
          // 项目用户关系表
          user.setProjectId(project.getId());
          user.setType(1);
          userMapper.insert(user);
          SurProjectUser surProjectUser = new SurProjectUser();
          surProjectUser.setProjectId(project.getId());
          surProjectUser.setUserId(user.getId());
          projectUserMapper.insert(surProjectUser);
        }
      }
    }
    // 如果部门用户数组不为空
    if (req.getDepUserList() != null) {
      List<SysUser> depUserList = req.getDepUserList();
      allUsers.addAll(depUserList);
      // 获取id
      //           List<String> userIds =
      // depUserList.stream().map(SysUser::getId).distinct().collect(Collectors.toList());
      // 删除之前的用户和项目的关系
      //           projectUserService.remove(new
      // LambdaQueryWrapper<SurProjectUser>().in(SurProjectUser::getUserId,userIds));
      depUserList.forEach(
          user -> {
            // 处理要删除的用户
            // 先查询出之前该项目的用户
            // 判断用户表中有没有改部门用户
            SurUser one =
                userMapper.selectOne(
                    new LambdaQueryWrapper<SurUser>()
                        .eq(SurUser::getId, user.getId())
                        .eq(SurUser::getProjectId, req.getId()));
            // 如果表中没有该用户 则插入 如果
            if (one == null) {
              // 构造surUser对象
              SurUser surUser = new SurUser();
              BeanUtils.copyProperties(user, surUser);
              surUser.setId(null);
              surUser.setProjectId(project.getId());
              if (user.getSex() != null) {
                surUser.setGender(user.getSex().toString());
              }
              surUser.setName(user.getRealname());
              if (user.getOrgCode() != null) {
                surUser.setSysOrgCode(user.getOrgCode());
              }
              surUser.setType(2);
              userMapper.insert(surUser);
              // 项目用户关系表
              SurProjectUser surProjectUser = new SurProjectUser();
              surProjectUser.setProjectId(project.getId());
              surProjectUser.setUserId(user.getId());
              projectUserMapper.insert(surProjectUser);
            }
          });
    }
    project.setSelectNumber(allUsers.size());
    // 修改项目主表
    this.updateById(project);
    return true;
  }

  @Override
  public CollectDto getCollectData(CollectReq req) {
    // 首先根据项目id查询项目对象
    SurProject project = getById(req.getId());
    CollectDto collectDto = new CollectDto();
    BeanUtils.copyProperties(project, collectDto);
    // 根据项目id查询问卷id
    //        SurProjectSurvey projectSurvey = projectSurveyMapper.selectOne(new
    // LambdaQueryWrapper<SurProjectSurvey>().eq(SurProjectSurvey::getProjectId, req.getId()));
    //        if (projectSurvey != null) {
    //            collectDto.setSurveyId(projectSurvey.getSurveyId());
    //        }
    collectDto.setSurveyId(project.getSurveyCurrentId());
    // 如果无收集数量则 完成率为0
    if (collectDto.getSelectNumber().equals(0)) {
      collectDto.setRate("0%");
    }
    // 如果有收集 进行计算
    else {
      Double rate = Double.valueOf(collectDto.getCollectNumber() / collectDto.getSelectNumber());
      // 保留两位小数
      collectDto.setRate(NumUtils.getPercent(rate, 2));
    }
    // 查询项目用户列表
    Page<SurUser> page =
        userMapper.selectPage(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, req.getId()));
    List<SurUser> records = page.getRecords();
    List<UserDto> userDtoList = new ArrayList<>();
    records.forEach(
        surUser -> {
          UserDto userDto = new UserDto();
          BeanUtils.copyProperties(surUser, userDto);
          // 查出用户答案
          List<SurUserResult> surUserResults =
              userResultMapper.selectList(
                  new LambdaQueryWrapper<SurUserResult>()
                      .eq(SurUserResult::getProjectId, req.getId())
                      .eq(SurUserResult::getUserId, surUser.getId()));
          List<Object> scoreCollect =
              surUserResults.stream()
                  .map(SurUserResult::getBasicScore)
                  .collect(Collectors.toList());
          Integer totalScore = 0;
          // 计算总分数
          for (Object value : scoreCollect) {
            if (value != null) {
              totalScore += Integer.parseInt(value.toString());
            }
          }
          userDto.setScore(totalScore);
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
  public Boolean replaceUserJson() {
    return null;
  }

  @Override
  public Boolean new360Survey(SurProject req) {
    req.setFid(IdUtil.simpleUUID());
    return save(req);
  }

  @Override
  public ProjectResultDto getSurveyResult(String projectId) {
    // 查询项目对象
    SurProject project = this.getById(projectId);
    // 根据问卷id查询问卷
    SurSurveyProject survey = surveyProjectMapper.selectById(project.getSurveyCurrentId());
    ProjectResultDto projectResultDto = new ProjectResultDto();
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
  public List<Object> getStatistics(String projectId) {
    // 查询项目对象
    SurProject project = this.getById(projectId);
    // 根据问卷id查询问卷
    SurSurveyProject survey = surveyProjectMapper.selectById(project.getSurveyCurrentId());
    List<Object> resultList = new ArrayList<>();
    if (survey != null) {
      // 查询该问卷的所有题目
      List<SurQuestionProject> questionProjectList =
          questionProjectMapper.selectList(
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
                questionChoiceProjectMapper.selectOne(
                    new LambdaQueryWrapper<SurQuestionChoiceProject>()
                        .eq(SurQuestionChoiceProject::getQuestionId, question.getId()));
            String content = choiceProject.getContent();
            List<String> choiceLists =
                Arrays.asList(
                    content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
            if (choiceLists.size() > 1) {
              // 遍历选项数组
              for (String list : choiceLists) {
                // 查询出该选项的人数
                Long filledNumber =
                    userResultMapper.selectCount(
                        new LambdaQueryWrapper<SurUserResult>()
                            .eq(SurUserResult::getQuestionKey, question.getContent())
                            .eq(SurUserResult::getProjectId, projectId)
                            .eq(SurUserResult::getQuestionValue, list));
                // 构造选项json对象
                JSONObject choiceObj = new JSONObject();
                choiceObj.put("option", list);
                choiceObj.put("subtotal", filledNumber);
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
                choiceList.add(choiceObj);
              }
            }
            questionMap.put("choiceList", choiceList);
            resultList.add(questionMap);
          });
    }
    return resultList;
  }

  @Override
  public DashBordDto getDashBordData() {
    DashBordDto dashBordDto = new DashBordDto();
    // 总销售额卡片
    JSONObject totalSales = createTotalSalesData();
    // 总充值积分量卡片
    JSONObject orders = createOrdersData();
    // 总支付笔数卡片
    JSONObject pays = createPaysData();
    // 总租户数卡片
    JSONObject talents = createTalentsData();
    // 租户充值排行榜
    List<RankDto> rankList = getRankList();
    // 项目柱状图
    List<HistogramDto> projectHistogram = getProjectHistogram();
    // 问卷柱状图
    List<HistogramDto> surveyHistogram = getSurveyHistogram();
    dashBordDto.setTotalSales(totalSales);
    dashBordDto.setOrders(orders);
    dashBordDto.setPays(pays);
    dashBordDto.setTalents(talents);
    dashBordDto.setRankList(rankList);
    dashBordDto.setProjectList(projectHistogram);
    dashBordDto.setSurveyList(surveyHistogram);
    return dashBordDto;
  }

  @Override
  public Page<SurProject> getProjectList(ProjectAdvancedQueryReq req) {
    // 条件高级查询
    LambdaQueryWrapper<SurProject> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SurProject::getIsDel, false);
    if (req.getIsPublish() != null) {
      queryWrapper.eq(SurProject::getIsPublish, req.getIsPublish());
    }
    if (req.getType() != null && !req.getType().isEmpty()) {
      queryWrapper.eq(SurProject::getType, req.getType());
    }
    if (req.getName() != null && !req.getName().isEmpty()) {
      queryWrapper.like(SurProject::getProjectName, req.getName());
    }
    if (req.getTenantId() != null && !req.getTenantId().isEmpty()) {
      // 根据租户编号查询租户对象
      queryWrapper.eq(SurProject::getTenantId, req.getTenantId());
    }
    if (req.getOrder() != null) {
      // 如果为true 为降序
      if (req.getOrder()) {
        queryWrapper.orderByDesc(SurProject::getUpdateTime);
      } else {
        queryWrapper.orderByAsc(SurProject::getUpdateTime);
      }
    }
    Page<SurProject> page = page(new Page<>(req.getPageNum(), req.getPageSize()), queryWrapper);
    List<SurProject> records = page.getRecords();
    records.forEach(
        record -> {
          // 查询租户
          SysTenant tenant = sysTenantService.getById(record.getTenantId());
          record.setTenant(tenant);
        });
    page.setRecords(records);
    return page;
  }

  @Override
  public long getDataScreenSurveyCount() {
    return surveyService.count();
  }

  @Override
  public long getDataScreenTagCount() {
    return tagService.count();
  }

  @Override
  public long getDataScreenProjectCount() {
    return count();
  }

  @Override
  public long getDataScreenCount() {
    long surveyCount = getDataScreenSurveyCount();
    long tagCount = getDataScreenTagCount();
    long projectCount = getDataScreenProjectCount();
    return surveyCount + tagCount + projectCount;
  }

  @Override
  public JSONObject getProjectDistribution() {
    JSONObject result = new JSONObject();
    // 查询360度评估
    long count360 = count(new LambdaQueryWrapper<SurProject>().eq(SurProject::getType, "360度评估"));
    DataScreenProjectDisDto dto1 = new DataScreenProjectDisDto();
    dto1.setName("360度评估");
    dto1.setValue(count360);
    // 查询调查
    long countInvestigate =
        count(new LambdaQueryWrapper<SurProject>().eq(SurProject::getType, "调查"));
    DataScreenProjectDisDto dto2 = new DataScreenProjectDisDto();
    dto2.setName("调查");
    dto2.setValue(countInvestigate);
    // 查询测评
    long countEvaluation =
        count(new LambdaQueryWrapper<SurProject>().eq(SurProject::getType, "测评"));
    DataScreenProjectDisDto dto3 = new DataScreenProjectDisDto();
    dto3.setName("测评");
    dto3.setValue(countEvaluation);
    List<DataScreenProjectDisDto> dtoList = new ArrayList<>();
    Collections.addAll(dtoList, dto1, dto2, dto3);
    result.put("data", dtoList);
    return result;
  }

  @Override
  public JSONObject getTagDistribution() {
    JSONObject result = new JSONObject();
    // 查询问卷列表
    List<Survey> surveyList = surveyService.list();
    // 查询标签列表
    List<SurTag> tagList = tagService.list();
    // 转换为名字
    List<String> tagNames = tagList.stream().map(SurTag::getTagName).collect(Collectors.toList());
    result.put("categories", tagNames);
    List<Integer> seriesList = new ArrayList<>();
    JSONObject seriesObj = new JSONObject();
    seriesObj.put("name", "使用量");
    // 遍历标签列表
    for (SurTag tag : tagList) {
      // 查询该标签的使用量
      int i = 0;
      for (Survey survey : surveyList) {
        if (survey.getTagRowkeys().contains(tag.getId())) {
          i++;
        }
      }
      seriesList.add(i);
    }
    seriesObj.put("data", seriesList);
    List<JSONObject> resList = new ArrayList<>();
    resList.add(seriesObj);
    result.put("series", resList);
    JSONObject data = new JSONObject();
    data.put("data", result);
    return data;
  }

  @Override
  public JSONObject getProjectRelease() {
    JSONObject result = new JSONObject();
    // 查询已发布
    long count360 = count(new LambdaQueryWrapper<SurProject>().eq(SurProject::getIsPublish, true));
    DataScreenProjectDisDto dto1 = new DataScreenProjectDisDto();
    dto1.setName("已发布");
    dto1.setValue(count360);
    // 查询未发布
    long countInvestigate =
        count(new LambdaQueryWrapper<SurProject>().eq(SurProject::getIsPublish, false));
    DataScreenProjectDisDto dto2 = new DataScreenProjectDisDto();
    dto2.setName("未发布");
    dto2.setValue(countInvestigate);
    List<DataScreenProjectDisDto> dtoList = new ArrayList<>();
    Collections.addAll(dtoList, dto1, dto2);
    result.put("data", dtoList);
    return result;
  }

  // 总销售额卡片数据
  private JSONObject createTotalSalesData() {
    JSONObject totalSales = new JSONObject();
    // 总销售额
    totalSales.put("total", 15679);
    // 周同比
    totalSales.put("weekRate", "12%");
    // 日同比
    totalSales.put("dayRate", "11%");
    // 日均销售额
    totalSales.put("daySale", 136);
    return totalSales;
  }

  // 总充值积分卡片数据
  private JSONObject createOrdersData() {
    JSONObject orders = new JSONObject();
    // 总订单量
    orders.put("total", 1048);
    // 日订单
    orders.put("day", 56);
    return orders;
  }

  // 总充值笔数卡片数据
  private JSONObject createPaysData() {
    JSONObject pays = new JSONObject();
    // 支付笔数
    pays.put("total", 629);
    // 转化率
    pays.put("rate", "61%");
    return pays;
  }

  // 总租户卡片数据
  private JSONObject createTalentsData() {
    // 总租户数卡片
    JSONObject talents = new JSONObject();
    // 查询所有的租户
    List<SysTenant> tenantList =
        sysTenantService.list(new LambdaQueryWrapper<SysTenant>().ne(SysTenant::getId, 1));
    // 租户数量
    talents.put("size", tenantList.size());
    talents.put("weekRate", "5%");
    talents.put("dayRate", "11%");
    return talents;
  }

  // 充值积分排行榜
  private List<RankDto> getRankList() {
    List<RankDto> rankList = new ArrayList<>();
    // 查询所有的租户
    //    List<SysTenant> tenantList =
    //        sysTenantService.list(new LambdaQueryWrapper<SysTenant>().ne(SysTenant::getId, 1));
    RankDto rankDto1 = new RankDto();
    rankDto1.setName("海翼租赁");
    rankDto1.setTotal(8790);
    RankDto rankDto2 = new RankDto();
    rankDto2.setName("融资租赁");
    rankDto2.setTotal(3669);
    RankDto rankDto3 = new RankDto();
    rankDto3.setName("广汉移动");
    rankDto3.setTotal(1467);
    RankDto rankDto4 = new RankDto();
    rankDto4.setName("阆中移动");
    rankDto4.setTotal(800);
    RankDto rankDto5 = new RankDto();
    rankDto5.setName("南充移动");
    rankDto5.setTotal(551);
    RankDto rankDto6 = new RankDto();
    rankDto6.setName("四川贡井移动公司");
    rankDto6.setTotal(400);
    Collections.addAll(rankList, rankDto1, rankDto2, rankDto3, rankDto4, rankDto5, rankDto6);
    return rankList;
  }

  // 项目数柱状图
  private List<HistogramDto> getProjectHistogram() {
    List<HistogramDto> dataList = new ArrayList<>();
    // 查询项目数
    for (int i = 1; i < 13; i++) {
      HistogramDto histogramDto = new HistogramDto();
      histogramDto.setX(i + "月");
      Calendar calendar1 = Calendar.getInstance();
      calendar1.set(2022, i, 1);
      Calendar calendar2 = Calendar.getInstance();
      calendar2.set(2022, i + 1, 1);
      Date start = calendar1.getTime();
      Date end = calendar2.getTime();
      List<SurProject> list =
          list(new LambdaQueryWrapper<SurProject>().between(SurProject::getCreateTime, start, end));
      //      Random rand = new Random();
      //      int randomNumber = rand.nextInt(90) + 10;
      histogramDto.setY(list.size());
      dataList.add(histogramDto);
    }
    return dataList;
  }

  // 问卷数柱状图
  private List<HistogramDto> getSurveyHistogram() {
    List<HistogramDto> dataList = new ArrayList<>();
    // 查询项目数
    for (int i = 1; i < 13; i++) {
      HistogramDto histogramDto = new HistogramDto();
      histogramDto.setX(i + "月");
      //      Random rand = new Random();
      //      int randomNumber = rand.nextInt(90) + 10;
      Calendar calendar1 = Calendar.getInstance();
      calendar1.set(2022, i, 1);
      Calendar calendar2 = Calendar.getInstance();
      calendar2.set(2022, i + 1, 1);
      Date start = calendar1.getTime();
      Date end = calendar2.getTime();
      List<SurResult> list =
          resultMapper.selectList(
              new LambdaQueryWrapper<SurResult>().between(SurResult::getCreateDate, start, end));
      histogramDto.setY(list.size());
      dataList.add(histogramDto);
    }
    return dataList;
  }
}

package org.jeecg.modules.survey.survey.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.client.req.ProjectAdvancedQueryReq;
import org.jeecg.modules.survey.client.service.ISurTagService;
import org.jeecg.modules.survey.survey.dto.CollectDto;
import org.jeecg.modules.survey.survey.dto.DashBordDto;
import org.jeecg.modules.survey.survey.dto.DataScreenDto;
import org.jeecg.modules.survey.survey.dto.ProjectResultDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.SurProjectSurveyMapper;
import org.jeecg.modules.survey.survey.mapper.SurSurveyProjectMapper;
import org.jeecg.modules.survey.survey.mapper.SurveyMapper;
import org.jeecg.modules.survey.survey.req.*;
import org.jeecg.modules.survey.survey.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 问卷项目表 @Author: jeecg-boot @Date: 2022-07-01 @Version: V1.0
 */
@Api(tags = "问卷项目表")
@RestController
@RequestMapping("/survey/surProject")
@Slf4j
public class SurProjectController extends JeecgController<SurProject, ISurProjectService> {
  @Autowired private ISurProjectService surProjectService;
  @Autowired private ISurveyService surveyService;
  @Autowired private ISurTagService tagService;
  @Autowired private ISurUserService userService;
  @Autowired private SurProjectSurveyService projectSurveyService;
  @Autowired private SurProjectUserService projectUserService;
  @Autowired private ISurUserResultService userResultService;
  @Autowired private ISurResultService resultService;
  @Autowired private ISurEvaluationRelationshipService evaluationRelationshipService;
  @Autowired private ISurSurveyProjectService surveyProjectService;
  @Autowired private ISurEvaluationUserService evaluationUserService;
  @Autowired private SurProjectSurveyMapper surProjectSurveyMapper;
  @Autowired private SurSurveyProjectMapper surSurveyProjectMapper;

  @AutoLog(value = "返回问卷的每个题目的统计信息")
  @ApiOperation(value = "返回问卷的每个题目的统计信息", notes = "返回问卷的每个题目的统计信息")
  @GetMapping(value = "/statistics/{id}")
  public Result<?> getStatistics(@PathVariable String id) {
    List<Object> result = surProjectService.getStatistics(id);
    return Result.ok(result);
  }

  @AutoLog(value = "返回问卷的题目json和结果")
  @ApiOperation(value = "返回问卷的题目json和结果", notes = "返回问卷的题目json和结果")
  @GetMapping(value = "/result/{id}")
  public Result<?> getResult(@PathVariable String id) {
    ProjectResultDto surveyResult = surProjectService.getSurveyResult(id);
    return Result.ok(surveyResult);
  }

  @AutoLog(value = "问卷项目表-编辑360度项目")
  @ApiOperation(value = "问卷项目表-编辑360度项目", notes = "问卷项目表-编辑360度项目")
  @PostMapping(value = "/edit360")
  public Result<?> edit360Project(@RequestBody SurProject req) {
    if (surProjectService.updateById(req)) {
      return Result.OK("修改成功！");
    } else {
      return Result.error("修改失败！");
    }
  }

  @AutoLog(value = "问卷项目表-添加360度项目")
  @ApiOperation(value = "问卷项目表-添加360度项目", notes = "问卷项目表-添加360度项目")
  @PostMapping(value = "/add360")
  public Result<?> add360Project(@RequestBody SurProject req) {
    if (surProjectService.new360Survey(req)) {
      return Result.OK("新建成功！");
    } else {
      return Result.error("新建失败！");
    }
  }

  @AutoLog(value = "发布项目问卷")
  @ApiOperation(value = "发布项目问卷", notes = "发布项目问卷")
  @PostMapping(value = "/publish")
  public Result<?> surveyListByType(@RequestBody PageReq req) {
    SurProject project = surProjectService.getById(req.getId());
    project.setIsPublish(true);
    surProjectService.updateById(project);
    return Result.ok("发布成功!");
  }

  @AutoLog(value = "根据项目type查询问卷")
  @ApiOperation(value = "根据项目type查询问卷", notes = "根据项目type查询问卷")
  @PostMapping(value = "/surveyByType")
  public Result<?> surveyListByType(@RequestBody SurveyByTypeReq req) {
    Page<Survey> surveyPage =
        surveyService.page(
            new Page<>(req.getPageNum(), req.getPageSize()),
            new LambdaQueryWrapper<Survey>().eq(Survey::getType, req.getType()));
    return Result.ok(surveyPage);
  }

  @AutoLog(value = "根据项目询项目用户列表")
  @ApiOperation(value = "根据项目id询项目用户列表", notes = "根据项目id询项目用户列表")
  @GetMapping(value = "/userList/{id}")
  public Result<?> userList(@PathVariable("id") String id) {
    List<SurUser> userList =
        userService.list(new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, id));
    return Result.ok(userList);
  }

  @AutoLog(value = "查询收集项目详情")
  @ApiOperation(value = "查询收集项目详情", notes = "查询收集项目详情")
  @PostMapping(value = "/getAnalysis")
  public Result<CollectDto> analysis(@RequestBody CollectReq req) {
    CollectDto collectData = surProjectService.getCollectData(req);
    return Result.ok(collectData);
  }

  /**
   * 判断是否是项目参与人员的接口
   *
   * @param req
   * @return
   */
  @AutoLog(value = "判断手机号后4位")
  @ApiOperation(value = "判断手机号后4位", notes = "判断手机号后4位")
  @PostMapping(value = "/judge")
  public Result<?> judge(@RequestBody JudgePhoneReq req) {
    // 根据项目id查询用户
    List<SurProjectUser> projectUsers =
        projectUserService.list(
            new LambdaQueryWrapper<SurProjectUser>()
                .eq(SurProjectUser::getProjectId, req.getProjectId()));
    List<String> userIds =
        projectUsers.stream().map(SurProjectUser::getUserId).collect(Collectors.toList());
    List<SurUser> userList = userService.listByIds(userIds);
    // 将用户列表转换为手机号
    List<String> phoneList = userList.stream().map(SurUser::getPhone).collect(Collectors.toList());
    List<String> phoneSuffix = new ArrayList<>();
    //        for (String s : phoneList) {
    //            String suf = s.substring(s.length() - 4);
    //            phoneSuffix.add(suf);
    //        }

    if (phoneList.contains(req.getPhoneSuffix())) {
      // 查询出该用户
      //            SurUser one = userService.getOne(new
      // LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId,
      // req.getProjectId()).likeLeft(SurUser::getPhone, req.getPhoneSuffix()));
      SurUser one =
          userService.getOne(
              new LambdaQueryWrapper<SurUser>()
                  .eq(SurUser::getProjectId, req.getProjectId())
                  .eq(SurUser::getPhone, req.getPhoneSuffix()));
      //            //查询评价者数组
      //            List<String> evaluationList = evaluationUserService.list(new
      // LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getProjectId,
      // req.getProjectId()).eq(SurEvaluationUser::getUserId, one.getId()))
      //
      // .stream().map(SurEvaluationUser::getEvaluatorId).collect(Collectors.toList());
      //            List<SurUser> surUserList = new ArrayList<>();
      //            if (!evaluationList.isEmpty()) {
      //                surUserList = userService.listByIds(evaluationList);
      //            }
      List<SurEvaluationUser> surEvaluationUserList =
          evaluationUserService.list(
              new LambdaQueryWrapper<SurEvaluationUser>()
                  .eq(SurEvaluationUser::getProjectId, req.getProjectId())
                  .eq(SurEvaluationUser::getEvaluatorId, one.getId()));
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("user", one);
      jsonObject.put("evaluator", surEvaluationUserList);
      return Result.OK("该用户有权访问!", jsonObject);
    }
    return Result.error(500, "该用户无权访问！");
  }

  /**
   * 分页查询问卷模板列表
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷模板列表")
  @ApiOperation(value = "问卷模板列表", notes = "问卷模板列表")
  @PostMapping(value = "/surList")
  public Result<IPage<Survey>> surList(@RequestBody PageReq req) {
    Page<Survey> page = surveyService.page(new Page<>(req.getPageNum(), req.getPageSize()));
    return Result.OK(page);
  }

  /**
   * 替换源问卷中的json
   *
   * @param req
   * @return
   */
  @AutoLog(value = "替换源问卷中的json")
  @ApiOperation(value = "替换源问卷中的json", notes = "替换源问卷中的json")
  @PostMapping(value = "/replaceJson")
  public Result<?> replaceJson(@RequestBody PageReq req) {
    Page<Survey> page = surveyService.page(new Page<>(req.getPageNum(), req.getPageSize()));
    return Result.OK(page);
  }

  /**
   * 分页列表查询
   *
   * @param surProject
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "问卷项目表-分页列表查询")
  @ApiOperation(value = "问卷项目表-分页列表查询", notes = "问卷项目表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<SurProject>> queryPageList(
      SurProject surProject,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<SurProject> queryWrapper =
        QueryGenerator.initQueryWrapper(surProject, req.getParameterMap());
    Page<SurProject> page = new Page<SurProject>(pageNo, pageSize);
    IPage<SurProject> pageList = surProjectService.page(page, queryWrapper);
    // 将问卷id set到project对象中
    List<SurProject> records = pageList.getRecords();
    records.forEach(
        project -> {
          if ("测评".equals(project.getType())) {

          } else {
            // 先查询出关联的问卷
            SurProjectSurvey projectSurvey =
                projectSurveyService.getOne(
                    new LambdaQueryWrapper<SurProjectSurvey>()
                        .eq(SurProjectSurvey::getProjectId, project.getId()));
            if (projectSurvey == null) {
              project.setSurveyId(null);
            } else {
              project.setSurveyId(projectSurvey.getSurveyId());
            }
          }
        });
    pageList.setRecords(records);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷项目表-添加")
  @ApiOperation(value = "问卷项目表-添加", notes = "问卷项目表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody SurProject req) {
    req.setFid(IdUtil.simpleUUID());
    req.setSelectNumber(0);
    req.setCollectNumber(0);
    req.setIsPublish(false);
    if (surProjectService.save(req)) {
      return Result.OK("新建成功!");
    }
    return Result.error("新建失败");
  }

//  @AutoLog(value = "问卷项目表-设置问卷")
//  @ApiOperation(value = "问卷项目表-设置问卷", notes = "问卷项目表-设置问卷")
//  @PostMapping(value = "/setSurvey")
//  public Result<?> addSurvey(@RequestBody SetProjectSurveyReq req) {
//    if (surProjectService.setSurvey(req)) {
//      return Result.OK("设置成功!");
//    }
//    return Result.error("设置失败");
//  }
@AutoLog(value = "问卷项目表-设置问卷")
@ApiOperation(value = "问卷项目表-设置问卷", notes = "问卷项目表-设置问卷")
@PostMapping(value = "/setSurvey")
public Result<?> addSurvey(@RequestBody SetProjectSurveyReq req) {
  if (surProjectService.setSurvey(req)) {
    return Result.OK("设置成功!");
  }
  return Result.error("设置失败");
}


  /**
   * 编辑用户 0
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷项目表-设置用户信息")
  @ApiOperation(value = "问卷项目表-设置用户信息", notes = "问卷项目表-设置用户信息")
  @PostMapping(value = "/setUser")
  public Result<?> setUser(@RequestBody ProjectEditReq req) {
    if (surProjectService.setUser(req)) {
      return Result.OK("编辑成功!");
    }
    return Result.error("编辑失败！");
  }

  /**
   * 编辑用户
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷项目表-编辑问卷")
  @ApiOperation(value = "问卷项目表-编辑问卷", notes = "问卷项目表-编辑问卷")
  @PostMapping(value = "/editSurvey")
  public Result<?> editSurvey(@RequestBody ProjectEditReq req) {
    if (surProjectService.editSurvey(req)) {
      return Result.OK("编辑成功!");
    }
    return Result.error("编辑失败！");
  }

  /**
   * 编辑
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷项目表-编辑")
  @ApiOperation(value = "问卷项目表-编辑", notes = "问卷项目表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody SurProject req) {
    if (surProjectService.updateById(req)) {
      return Result.OK("编辑成功!");
    }
    return Result.error("修改失败!");
  }

  /**
   * 根据项目id保存问卷rowKeys
   *
   * @param req
   * @return
   */
  @AutoLog(value = "问卷项目表-根据项目id保存问卷rowKeys")
  @ApiOperation(value = "问卷项目表-根据项目id保存问卷rowKeys", notes = "问卷项目表-根据项目id保存问卷rowKeys")
  @PostMapping(value = "/saveRowKeys")
  @Transactional(rollbackFor = Exception.class)
  public Result<?> saveRowKeys(@RequestBody RowKeysReq req) {
    // 根据项目id查询项目
    SurProject project = surProjectService.getById(req.getId());
    project.setRowKeys(JSON.toJSONString(req.getRowKeys()));
    project.setSurveySrcId(req.getSurveyId());
    surProjectService.updateById(project);
    // 修改关系表
    SurProjectSurvey projectSurvey =
        projectSurveyService.getOne(
            new LambdaQueryWrapper<SurProjectSurvey>()
                .eq(SurProjectSurvey::getProjectId, req.getId()));
    if (projectSurvey == null) {
      SurProjectSurvey surProjectSurvey = new SurProjectSurvey();
      surProjectSurvey.setProjectId(req.getId());
      surProjectSurvey.setSurveyId(req.getSurveyId());
      projectSurveyService.save(surProjectSurvey);
    } else {
      projectSurvey.setSurveyId(req.getSurveyId());
      projectSurveyService.updateById(projectSurvey);
    }
    return Result.OK("保存成功!");
  }
  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "问卷项目表-通过id删除")
  @ApiOperation(value = "问卷项目表-通过id删除", notes = "问卷项目表-通过id删除")
  @DeleteMapping(value = "/delete")
  @Transactional
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    SurProject project = surProjectService.getById(id);
    // 查询出问卷项目关系
    SurProjectSurvey projectSurvey =
        projectSurveyService.getOne(
            new LambdaQueryWrapper<SurProjectSurvey>()
                .eq(SurProjectSurvey::getProjectId, project.getId()));
    // 删除项目问卷关系
    projectSurveyService.removeById(projectSurvey);
    // 查询项目用户关系
    List<SurProjectUser> projectUsers =
        projectUserService.list(
            new LambdaQueryWrapper<SurProjectUser>()
                .eq(SurProjectUser::getProjectId, project.getId()));
    // 将关系数组转为userid
    List<String> userIdList =
        projectUsers.stream().map(SurProjectUser::getUserId).collect(Collectors.toList());
    // 删除用户
    userService.removeByIds(userIdList);
    projectUserService.removeByIds(projectUsers);
    // 删除所有已提交的用户的问卷
    resultService.remove(new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, id));
    userResultService.remove(
        new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, id));
    // 删除项目的评价关系
    evaluationRelationshipService.remove(
        new LambdaQueryWrapper<SurEvaluationRelationship>()
            .eq(SurEvaluationRelationship::getProjectId, id));
    // 删除项目
    surProjectService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "问卷项目表-批量删除")
  @ApiOperation(value = "问卷项目表-批量删除", notes = "问卷项目表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    List<String> projectIds = Arrays.asList(ids.split(","));
    projectIds.forEach(
        id -> {
          SurProject project = surProjectService.getById(id);
          // 查询出问卷项目关系
          SurProjectSurvey projectSurvey =
              projectSurveyService.getOne(
                  new LambdaQueryWrapper<SurProjectSurvey>()
                      .eq(SurProjectSurvey::getProjectId, project.getId()));
          // 删除项目问卷关系
          projectSurveyService.removeById(projectSurvey);
          // 查询项目用户关系
          List<SurProjectUser> projectUsers =
              projectUserService.list(
                  new LambdaQueryWrapper<SurProjectUser>()
                      .eq(SurProjectUser::getProjectId, project.getId()));
          // 将关系数组转为userid
          List<String> userIdList =
              projectUsers.stream().map(SurProjectUser::getUserId).collect(Collectors.toList());
          // 删除用户
          userService.removeByIds(userIdList);
          projectUserService.removeByIds(projectUsers);
          // 删除所有已提交的用户的问卷
          resultService.remove(
              new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, id));
          userResultService.remove(
              new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, id));
          // 删除项目的评价关系
          evaluationRelationshipService.remove(
              new LambdaQueryWrapper<SurEvaluationRelationship>()
                  .eq(SurEvaluationRelationship::getProjectId, id));
          // 删除项目
          surProjectService.removeById(id);
        });
    return Result.OK("批量删除成功!");
  }


  /**
   * 删除选择的问卷
   *
   * @param surveyId
   * @return
   */
  @AutoLog(value = "问卷项目表-选择问卷删除")
  @ApiOperation(value = "问卷项目表-选择问卷删除", notes = "问卷项目表-选择问卷删除")
  @DeleteMapping(value = "/deleteSelectSurvey")
  public Result<String> deleteSelectSurvey(@RequestParam(name = "surveyId", required = true) String surveyId) {

    if (surProjectService.deleteSelectSurvey(surveyId)){
      return Result.OK("删除成功!");
    }
    return Result.error("删除失败!");



  }

//  /**
//   * 通过id查询
//   *
//   * @param id
//   * @return
//   */
//  // @AutoLog(value = "问卷项目表-通过id查询")
//  @ApiOperation(value = "问卷项目表-通过id查询", notes = "问卷项目表-通过id查询")
//  @GetMapping(value = "/queryById")
//  public Result<Map<String, Object>> queryById(
//      @RequestParam(name = "id", required = true) String id,
//      @RequestParam("pageNum") Integer pageNum,
//      @RequestParam("pageSize") Integer pageSize) {
//    SurProject surProject = surProjectService.getById(id);
//    // 将json转换为list
//    String rowKeysJson = surProject.getRowKeys();
//    List<String> rowKeys = JSON.parseArray(rowKeysJson, String.class);
//    // 获取userRowKeys
//    String userRowKeysJson = surProject.getUserRowKeys();
//    List<String> userRowKeys = new ArrayList<>();
//    if (userRowKeysJson != null) {
//      userRowKeys = JSON.parseArray(userRowKeysJson, String.class);
//    }
//    // 查询出对应的用户列表
//    //        List<String> userId = projectUserService.list(new
//    // LambdaQueryWrapper<SurProjectUser>().eq(SurProjectUser::getProjectId,
//    // surProject.getId())).stream().map(SurProjectUser::getUserId).collect(Collectors.toList());
//    List<String> userId =
//        userService
//            .list(new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, surProject.getId()))
//            .stream()
//            .map(SurUser::getId)
//            .collect(Collectors.toList());
//    Page<SurUser> surUserPage;
//    Page<SurUser> depUserPage;
//    if (!userId.isEmpty()) {
//      surUserPage =
//          userService.page(
//              new Page<>(pageNum, pageSize),
//              new LambdaQueryWrapper<SurUser>().in(SurUser::getId, userId).eq(SurUser::getType, 1));
//      // 查出对应的部门用户列表
//      depUserPage =
//          userService.page(
//              new Page<>(pageNum, pageSize),
//              new LambdaQueryWrapper<SurUser>().in(SurUser::getId, userId).eq(SurUser::getType, 2));
//    } else {
//      surUserPage = new Page<>();
//      depUserPage = new Page<>();
//    }
//    Map<String, Object> data = new HashMap<>();
//    // 查询出对应的问卷模板
//    //        SurProjectSurvey surProjectSurvey = projectSurveyService.getOne(new
//    // LambdaQueryWrapper<SurProjectSurvey>().eq(SurProjectSurvey::getProjectId,
//    // surProject.getId()));
//    Survey survey =
//        surveyService.getOne(
//            new LambdaQueryWrapper<Survey>().eq(Survey::getId, surProject.getSurveySrcId()));
//    data.put("survey", survey);
//    // 查询是否有复制好的问卷
//    SurSurveyProject copySurvey =
//        surveyProjectService.getOne(
//            new LambdaQueryWrapper<SurSurveyProject>()
//                .eq(SurSurveyProject::getId, surProject.getSurveyCurrentId()));
//    data.put("copy_survey", copySurvey);
//    if (surProject == null) {
//      return Result.error("未找到对应数据");
//    }
//
//    data.put("project", surProject);
//    data.put("rowKeys", rowKeys);
//    data.put("user", surUserPage);
//    data.put("depUser", depUserPage);
//    data.put("userRowKeys", userRowKeys);
//    return Result.OK(data);
//  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "问卷项目表-通过id查询")
  @ApiOperation(value = "问卷项目表-通过id查询", notes = "问卷项目表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<?> queryById(
          @RequestParam(name = "id", required = true) String id,
          @RequestParam("pageNum") Integer pageNum,
          @RequestParam("pageSize") Integer pageSize) {
    SurProject surProject = surProjectService.getById(id);
    List<SurProjectSurvey> surProjectSurveys = surProjectSurveyMapper.selectList(new LambdaQueryWrapper<SurProjectSurvey>()
            .eq(SurProjectSurvey::getProjectId,surProject.getId()));
    List<SurSurveyProject> surveyProjectList = new ArrayList<SurSurveyProject>();
//    System.out.println("======================"+surProjectSurveys); 存在数据
    for (SurProjectSurvey surProjectSurvey : surProjectSurveys) {
//      System.out.println("============="+surProjectSurvey);
      SurSurveyProject surSurveyProject = surSurveyProjectMapper.selectOne(new LambdaQueryWrapper<SurSurveyProject>()
              .eq(SurSurveyProject::getId,surProjectSurvey.getSurveyId()));
      surveyProjectList.add(surSurveyProject);

    }

    // 将json转换为list
    String rowKeysJson = surProject.getRowKeys();
    List<String> rowKeys = JSON.parseArray(rowKeysJson, String.class);
    // 获取userRowKeys
    String userRowKeysJson = surProject.getUserRowKeys();
    List<String> userRowKeys = new ArrayList<>();
    if (userRowKeysJson != null) {
      userRowKeys = JSON.parseArray(userRowKeysJson, String.class);
    }
    // 查询出对应的用户列表
    //        List<String> userId = projectUserService.list(new
    // LambdaQueryWrapper<SurProjectUser>().eq(SurProjectUser::getProjectId,
    // surProject.getId())).stream().map(SurProjectUser::getUserId).collect(Collectors.toList());
    List<String> userId =
            userService
                    .list(new LambdaQueryWrapper<SurUser>().eq(SurUser::getProjectId, surProject.getId()))
                    .stream()
                    .map(SurUser::getId)
                    .collect(Collectors.toList());
    Page<SurUser> surUserPage;
    Page<SurUser> depUserPage;
    if (!userId.isEmpty()) {
      surUserPage =
              userService.page(
                      new Page<>(pageNum, pageSize),
                      new LambdaQueryWrapper<SurUser>().in(SurUser::getId, userId).eq(SurUser::getType, 1));
      // 查出对应的部门用户列表
      depUserPage =
              userService.page(
                      new Page<>(pageNum, pageSize),
                      new LambdaQueryWrapper<SurUser>().in(SurUser::getId, userId).eq(SurUser::getType, 2));
    } else {
      surUserPage = new Page<>();
      depUserPage = new Page<>();
    }

    Map<String, Object> data = new HashMap<>();
    // 查询出对应的问卷模板
    //        SurProjectSurvey surProjectSurvey = projectSurveyService.getOne(new
    // LambdaQueryWrapper<SurProjectSurvey>().eq(SurProjectSurvey::getProjectId,
    // surProject.getId()));
//    Survey survey =
//            surveyService.getOne(
//                    new LambdaQueryWrapper<Survey>().eq(Survey::getId, surProject.getSurveySrcId()));
    data.put("surveyProjectList", surveyProjectList);
//    // 查询是否有复制好的问卷
//    SurSurveyProject copySurvey =
//            surveyProjectService.getOne(
//                    new LambdaQueryWrapper<SurSurveyProject>()
//                            .eq(SurSurveyProject::getId, surProject.getSurveyCurrentId()));
//    data.put("copy_survey", copySurvey);
    if (surProject == null) {
      return Result.error("未找到对应数据");
    }
//
    data.put("project", surProject);
    data.put("rowKeys", rowKeys);
    data.put("user", surUserPage);
    data.put("depUser", depUserPage);
    data.put("userRowKeys", userRowKeys);
    return Result.OK(data);
  }

  @AutoLog(value = "查询首页数据")
  @ApiOperation(value = "查询首页数据", notes = "查询首页数据")
  @GetMapping(value = "/dashBord")
  public Result<DashBordDto> getDashBord() {
    DashBordDto data = surProjectService.getDashBordData();
    return Result.ok(data);
  }

  /**
   * 条件查询项目列表
   *
   * @param req
   * @return
   */
  @AutoLog(value = "条件查询项目列表")
  @ApiOperation(value = "条件查询项目列表", notes = "条件查询项目列表")
  @PostMapping(value = "/search")
  public Result<?> getProjectList(@RequestBody ProjectAdvancedQueryReq req) {
    Page<SurProject> projectPage = surProjectService.getProjectList(req);
    return Result.OK(projectPage);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param surProject
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SurProject surProject) {
    return super.exportXls(request, surProject, SurProject.class, "问卷项目表");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
    return super.importExcel(request, response, SurProject.class);
  }

  @AutoLog(value = "数据大屏 问卷个数")
  @ApiOperation(value = "数据大屏 问卷个数", notes = "数据大屏 问卷个数")
  @GetMapping(value = "/dashBord/survey")
  public JSONObject dataScreenSurvey() {
    DataScreenDto result = new DataScreenDto();
    long count = surProjectService.getDataScreenSurveyCount();
    result.setValue(String.valueOf(count));
    JSONObject res = new JSONObject();
    res.put("data", result);
    return res;
  }

  @AutoLog(value = "数据大屏 标签个数")
  @ApiOperation(value = "数据大屏 标签个数", notes = "数据大屏 标签个数")
  @GetMapping(value = "/dashBord/tag")
  public JSONObject dataScreenTag() {
    DataScreenDto result = new DataScreenDto();
    long count = surProjectService.getDataScreenTagCount();
    result.setValue(String.valueOf(count));
    JSONObject res = new JSONObject();
    res.put("data", result);
    return res;
  }

  @AutoLog(value = "数据大屏 项目个数")
  @ApiOperation(value = "数据大屏 项目个数", notes = "数据大屏 项目个数")
  @GetMapping(value = "/dashBord/project")
  public JSONObject dataScreenProject() {
    DataScreenDto result = new DataScreenDto();
    long count = surProjectService.count();
    result.setValue(String.valueOf(count));
    JSONObject res = new JSONObject();
    res.put("data", result);
    return res;
  }

  @AutoLog(value = "数据大屏 总数")
  @ApiOperation(value = "数据大屏 总数", notes = "数据大屏 总数")
  @GetMapping(value = "/dashBord/count")
  public JSONObject dataScreenCount() {
    DataScreenDto result = new DataScreenDto();
    long count = surProjectService.getDataScreenCount();
    result.setValue(String.valueOf(count));
    JSONObject res = new JSONObject();
    res.put("data", result);
    return res;
  }

  @AutoLog(value = "数据大屏 项目分布占比")
  @ApiOperation(value = "数据大屏 项目分布占比", notes = "数据大屏 项目分布占比")
  @GetMapping(value = "/dashBord/projectDistribution")
  public JSONObject dataScreenProjectDistribution() {
    return surProjectService.getProjectDistribution();
  }

  @AutoLog(value = "数据大屏 标签占比")
  @ApiOperation(value = "数据大屏 标签占比", notes = "数据大屏 标签占比")
  @GetMapping(value = "/dashBord/tagDistribution")
  public JSONObject dataScreenTagDistribution() {
    return surProjectService.getTagDistribution();
  }

  @AutoLog(value = "数据大屏 项目发布")
  @ApiOperation(value = "数据大屏 项目发布", notes = "数据大屏 项目发布")
  @GetMapping(value = "/dashBord/projectRelease")
  public JSONObject dataScreenProjectRelease() {
    return surProjectService.getProjectRelease();
  }
}

package org.jeecg.modules.survey.client.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.mail.iap.ConnectionException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.survey.client.dto.*;
import org.jeecg.modules.survey.client.entity.UserProject;
import org.jeecg.modules.survey.client.evaluationReport.entity.*;
import org.jeecg.modules.survey.client.evaluationReport.mapper.*;
import org.jeecg.modules.survey.client.req.*;
import org.jeecg.modules.survey.client.resp.PageResp;
import org.jeecg.modules.survey.client.service.*;
import org.jeecg.modules.survey.client.utils.HttpClientUtil;
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.CollectDto;
import org.jeecg.modules.survey.survey.dto.ProjectResultDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.SurSurveyProjectMapper;
import org.jeecg.modules.survey.survey.mapper.SurSurveyTenantMapper;
import org.jeecg.modules.survey.survey.mapper.SurveyMapper;
import org.jeecg.modules.survey.survey.req.ChoiceByQuestionIdReq;
import org.jeecg.modules.survey.survey.req.CollectReq;
import org.jeecg.modules.survey.survey.req.ScoreSetReq;
import org.jeecg.modules.survey.survey.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Description: 用户端项目表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Api(tags = "用户端项目表")
@RestController
@RequestMapping("/client/userProject")
@Slf4j
public class UserProjectController extends JeecgController<UserProject, IUserProjectService> {
    @Autowired
    private IUserProjectService userProjectService;
    @Autowired
    private ISurProjectService surProjectService;
    @Autowired
    private IUserSurveyQuestionService questionService;
    @Autowired
    private ISurQuestionChoiceProjectService questionChoiceService;
    @Autowired
    private IUserSurveyService surveyService;
    @Autowired
    private ISurSurveyProjectService surveyProjectService;
    @Autowired
    private ISurUserSurveyService userSurveyService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISurProjectEnableService surProjectEnableService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ISurUserService urUserService;
    @Autowired
    private ReportBusinessDrivingForceMapper reportBusinessDrivingForceMapper;
    @Autowired
    private ReportCreativeAbilityMapper reportCreativeAbilityMapper;
    @Autowired
    private ReportManagementStyleEvaluationMapper reportManagementStyleEvaluationMapper;
    @Autowired
    private ReportModeOfThinkingMapper reportModeOfThinkingMapper;
    @Autowired
    private ReportTeamRoleMapper reportTeamRoleMapper;
    @Autowired
    private ReportSelfMotivationMapper reportSelfMotivationMapper;
    @Autowired
    private SurSurveyProjectMapper userSurveyMapper;

    @ApiOperation(value = "导出答卷")
    @PostMapping("/exportAnswerSheet")
    public void exportStudentExcel(HttpServletResponse response, @RequestBody ExportReq req)
            throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        //        response.setContentType("application/vnd.ms-excel");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(new Date().toString(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 查询项目对象
        SurProject project = surProjectService.getById(req.getProjectId());
        switch (project.getType()) {
            case "调查":
                excelService.exportInvestigation(response, req);
                break;
            case "测评":
                excelService.exportEvaluation(response, req);
                break;
            case "360度评估":
                excelService.export360(response, req);
                break;
        }
    }

    @ApiOperation(value = "查询项目答卷人", notes = "查询项目答卷人")
    @PostMapping(value = "/testHttp")
    public Result<?> testHttp(@RequestBody JSONObject req) {

        JSONObject jsonObject = new JSONObject();
        JSONObject paramJson = new JSONObject();
        jsonObject.put("pageNum", req.get("pageNum"));
        jsonObject.put("pageSize", req.get("pageSize"));
        paramJson.put("param", jsonObject);
        String s =
                HttpClientUtil.doPost("http://106.14.140.92:7777/managementStyleEvaluation", paramJson);
        return Result.ok(s);
    }

    @ApiOperation(value = "生成测评报告", notes = "生成测评报告")
    @PostMapping(value = "/generateReport")
    public Result<?> generateReport(@RequestBody ReportFormCreateReq req) throws ConnectionException {
        // 判断该用户是否已经生成报表
        SurUser user = urUserService.getById(req.getUserId());
        if (user.getIsGenerate()) {
            return Result.error(500, "该用户不存在或已经生成过！");
        }
        if (userProjectService.generateReportForUser(req)) {
            return Result.ok("测评报表生成成功！");
        }
        return Result.error(500, "该用户不存在或已经生成过！");
    }

    @ApiOperation(value = "个人事业驱动力测评柱状图所需api", notes = "个人事业驱动力测评所需api")
    @GetMapping(value = "/getBusinessDrivingForce")
    public JSONObject getBusinessDrivingForceHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportBusinessDrivingForce(projectId, userId);
    }

    @ApiOperation(value = "人才创造力测评柱状图所需api", notes = "人才创造力测评所需api")
    @GetMapping(value = "/getReportCreativeAbility")
    public JSONObject getReportCreativeAbilityHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportCreativeAbility(projectId, userId);
    }

    @ApiOperation(value = "管理风格测评柱状图所需api", notes = "管理风格测评所需api")
    @GetMapping(value = "/getReportManagementStyleEvaluation")
    public JSONObject getReportManagementStyleEvaluationHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportManagementStyleEvaluation(projectId, userId);
    }

    @ApiOperation(value = "人才思维方式测评柱状图所需api", notes = "人才思维方式柱状图测评所需api")
    @GetMapping(value = "/getReportModeOfThinking")
    public JSONObject getReportModeOfThinkingHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportModeOfThinking(projectId, userId);
    }

    @ApiOperation(value = "团队角色测评柱状图所需api", notes = "团队角色柱状图测评所需api")
    @GetMapping(value = "/getReportTeamRole")
    public JSONObject getReportTeamRoleHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportTeamRole(projectId, userId);
    }

    @ApiOperation(value = "自我驱动力测评柱状图所需api", notes = "自我驱动力测评所需api")
    @GetMapping(value = "/getReportSelfMotivation")
    public JSONObject getReportSelfMotivationHistogram(
            @RequestParam String projectId, @RequestParam String userId) {
        return getReportSelfMotivation(projectId, userId);
    }

    @ApiOperation(value = "查询项目答卷人", notes = "查询项目答卷人")
    @PostMapping(value = "/getUsers")
    public Result<?> selectUserList(@RequestBody PageReq req) {
        Page<SurUser> userList = userProjectService.getProjectUserList(req);
        return Result.ok(userList);
    }

    @ApiOperation(value = "恢复回收站", notes = "恢复回收站")
    @PostMapping(value = "/recovery")
    public Result<?> recovery(@RequestBody RecoveryReq req) {
        req.getIds()
                .forEach(
                        id -> {
                            SurProject project = surProjectService.getById(id);
                            project.setIsDel(false);
                            surProjectService.updateById(project);
                        });
        return Result.ok("恢复成功！");
    }

    @ApiOperation(value = "查询回收站项目", notes = "查询回收站项目")
    @PostMapping(value = "/getBin")
    public Result<?> getBinProjects(@RequestBody PageReq req) {
        Page<SurProject> page = userProjectService.getBinProjects(req);
        return Result.ok(page);
    }

    @ApiOperation(value = "清空回收站项目", notes = "清空回收站项目")
    @DeleteMapping(value = "/clean")
    public Result<?> getBinProjects() {
        if (userProjectService.cleanBinProject()) {
            return Result.ok("清空成功！");
        }
        return Result.error(500, "清空失败！");
    }

    @ApiOperation(value = "逻辑删除问卷", notes = "逻辑删除问卷")
    @DeleteMapping(value = "/logicDel/{id}")
    public Result<?> logicDelProject(@PathVariable("id") String id) {
        if (userProjectService.logicDeleteProject(id)) {
            return Result.ok("删除成功!");
        }
        return Result.error(500, "删除失败!");
    }

    @ApiOperation(value = "根据问卷模板id查询问卷模板", notes = "根据问卷模板id查询问卷模板")
    @GetMapping(value = "/getSurveyTemplateById/{id}")
    public Result<?> getSurveyTemplateById(@PathVariable("id") String id) {
        Survey template = userProjectService.getSurveyTemplateById(id);
        return Result.ok(template);
    }

    @ApiOperation(value = "查询问卷模板", notes = "查询问卷模板")
    @PostMapping(value = "/getSurveyTemplate")
    public Result<?> getSurveyTemplateList(@RequestBody ProjectAdvancedQueryReq req) {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");
        PageResp<Survey> page = userProjectService.getSurveyTemplateList(req, tenantId);
        return Result.ok(page);
    }

    @ApiOperation(value = "查询用户购买的问卷模板的原问卷的信息", notes = "查询用户购买的问卷模板的原问卷的信息")
    @PostMapping(value = "/getMySurveyTemplate")
    public Result<?> getMySurveyTemplateList(@RequestBody ProjectAdvancedQueryReq req) {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");
        PageResp<Survey> page = userProjectService.getMySurveyTemplateList(req, tenantId);
        return Result.ok(page);
    }

    @ApiOperation(value = "查询当前租户购买过的问卷集合", notes = "查询当前租户购买过的问卷集合")
    @PostMapping(value = "/getHavingSurveyTemplate")
    public Result<List<String>> getHavingSurveyTemplateList() {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");
        List<String> page = userProjectService.getHavingSurveyTemplateList(tenantId);
        return Result.ok(page);
    }

    @ApiOperation(value = "查询租户自己的问卷模板", notes = "查询租户自己的问卷模板")
    @PostMapping(value = "/getExclusiveSurveyTemplate")
    public Result<?> getExclusiveSurveyTemplateList(@RequestBody ProjectAdvancedQueryReq req) {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");
        PageResp<Survey> page = userProjectService.getExclusiveSurveyTemplateList(req, tenantId);
        return Result.ok(page);
    }

    @ApiOperation(value = "通过积分购买问卷模板", notes = "通过积分购买问卷模板")
    @PostMapping(value = "/purchaseByPoint")
    public Result<?> purchaseByPoint(@RequestBody PurchaseReq req) {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");
        // 先判断用户是否已经购买过问卷模板
        if (!userProjectService.getTenantAndSurveyRelation(req, tenantId)) {
            return Result.error("已经购买，不可重复购买！");
        }
        // 购买问卷模板
        if (userProjectService.purchaseByPoint(req, tenantId)) {
            return Result.ok("购买成功！");
        }
        return Result.error("购买失败！");
    }

    @ApiOperation(value = "上传问卷模板到市场", notes = "上传问卷模板到市场")
    @PostMapping(value = "/uploadTemplate")
    public Result<?> uploadTemplate(@RequestBody UploadReq req) {
        // 获取请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenant-id");

        if (userProjectService.uploadTemplate(req, tenantId)) {

            return Result.ok("上传成功！");

        }
        return Result.error("上传失败！");
    }

    @ApiOperation(value = "根据项目id查询问卷模板", notes = "根据项目id查询问卷模板")
    @GetMapping(value = "/getEvaluationSurvey/{id}")
    public Result<?> getSurveyTemplate(@PathVariable String id) {
        List<Survey> surveyList = userProjectService.getEvaluationSurveyList(id);
        return Result.ok(surveyList);
    }

    @AutoLog(value = "测评项目查询人自己的答卷及其状态")
    @ApiOperation(value = "测评项目查询人自己的答卷及其状态", notes = "测评项目查询人自己的答卷及其状态")
    @PostMapping(value = "/queryUserSurvey")
    public Result<?> queryUserSurvey(@RequestBody JudgeReq req) {
        // 查询项目对象
        SurProject project = surProjectService.getById(req.getProjectId());
        if (project == null) {
            return Result.error("项目不存在！");
        }
        if ("360度评估".equals(project.getType())) {
            JudgeDto result = userProjectService.get360UserSurveyResult(req);
            return Result.ok(result);
        }
        EvaluationJudgeDto dto = userProjectService.getUserSurveyResult(req);
        return Result.ok(dto);
    }

    @ApiOperation(value = "查询测评项目的评价人员", notes = "查询360度项目的评价人员")
    @GetMapping(value = "/queryEvaluationUser")
    public Result<?> queryEvaluationUser(@RequestParam("id") String id) {
        List<SurUser> userList = userProjectService.getEvaluationUserList(id);
        return Result.ok(userList);
    }

    @ApiOperation(value = "删除一些测评项目的评价人员", notes = "删除一些360度项目的评价人员")
    @PostMapping(value = "/removeEvaluationUser")
    public Result<String> removeEvaluationUser(@RequestBody Add360UserReq req) {
        if (userProjectService.removeEvaluationUser(req)) {
            return Result.ok("保存成功！");
        }
        return Result.error(500, "数据未找到！");
    }

    @ApiOperation(value = "批量添加测评项目的评价人员", notes = "批量添加测评项目的评价人员")
    @PostMapping(value = "/addEvaluationUser")
    public Result<String> addEvaluationPerson(@RequestBody Add360UserReq req) {
        if (userProjectService.addEvaluationUser(req)) {
            return Result.ok("添加成功");
        }
        return Result.error(500, "数据未找到！");
    }

    @ApiOperation(value = "通过Excel导入问卷填写人", notes = "通过Excel导入问卷填写人")
    @PostMapping(value = "/importByExcel")
    public Result<String> importByExcel(
            @RequestParam("file") MultipartFile file, @RequestParam("id") String id) {
        // 查询项目对象
        SurProject project = surProjectService.getById(id);
        if (project == null) {
            return Result.error("项目不存在！");
        }
        if (!project.getIsAddUser()) {
            project.setIsAddUser(true);
            surProjectService.updateById(project);
        }
        try {
            if (userProjectService.importSurveyUser(file, id)) {
                return Result.ok("添加成功");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.error(500, "excel为空，请重新上传！");
    }

    @ApiOperation(value = "测评项目设置测评问卷", notes = "测评项目设置测评问卷")
    @PostMapping(value = "/setEvaluationSurvey")
    public Result<?> setEvaluationSurvey(@RequestBody EvaluationSurveySetReq req) {
        if (userProjectService.setEvaluationSurveyList(req)) {
            return Result.ok("设置成功");
        } else {
            return Result.error(500, "设置失败");
        }
    }

    @ApiOperation(value = "创建测评项目", notes = "创建测评项目")
    @PostMapping(value = "/createEvaluationProject")
    public Result<?> createEvaluationProject(@RequestBody CreateEvaluationProjectReq req) {
        EvaluationProjectDto evaluationProject = userProjectService.createEvaluationProject(req);
        return Result.ok(evaluationProject);
    }

    @ApiOperation(value = "复制项目", notes = "复制项目")
    @PostMapping(value = "/copyProject")
    public Result<?> copyProject(@RequestBody SurveyCopyReq req) {
        SurProject copyProject = userProjectService.copyProject(req);
        if (copyProject != null) {
            return Result.ok(copyProject);
        } else {
            return Result.error(500, "源项目不存在！");
        }
    }

    @ApiOperation(value = "删除问卷", notes = "删除问卷")
    @DeleteMapping(value = "/removeSurvey/{id}")
    public Result<?> removeSurvey(@PathVariable("id") String id) {
        if (userProjectService.deleteSurvey(id)) {
            return Result.ok("删除成功!");
        } else {
            return Result.error(500, "数据未找到！");
        }
    }

    @ApiOperation(value = "复制问卷", notes = "复制问卷")
    @PostMapping(value = "/copySurvey")
    public Result<?> copySurvey(@RequestBody SurveyCopyReq req) {
        SurSurveyProject copySurvey = userProjectService.copySurvey(req);
        if (copySurvey != null) {
            return Result.ok(copySurvey);
        } else {
            return Result.error(500, "源问卷不存在！");
        }
    }

    @ApiOperation(value = "查询评价关系", notes = "查询评价关系")
    @GetMapping(value = "/queryEvaluation/{id}")
    public Result<?> queryEvaluation(@PathVariable("id") String id) {
        List<EvaluationRelationDto> evaluationRelationship =
                userProjectService.getEvaluationRelationship(id);
        return Result.ok(evaluationRelationship);
    }

    @ApiOperation(value = "查询项目权重分配", notes = "查询项目权重分配")
    @GetMapping(value = "/getWeight/{id}")
    public Result<?> getWeight(@PathVariable("id") String id) {
        SurEvaluationWeight projectWeight = userProjectService.getProjectWeight(id);
        return Result.ok(projectWeight);
    }

    @ApiOperation(value = "为评价关系分配权重", notes = "为评价关系分配权重")
    @PostMapping(value = "/setWeight")
    public Result<?> setWeight(@RequestBody WeightSetReq req) {
        if (userProjectService.setWeight(req)) {
            return Result.ok("设置成功!");
        } else {
            return Result.error(500, "数据未找到！");
        }
    }

    @AutoLog(value = "360项目判断身份")
    @ApiOperation(value = "360项目判断身份", notes = "360项目判断身份")
    @PostMapping(value = "/getPhoneCode")
    public Result<?> getPhoneCode(@RequestBody JudgeReq req) throws IOException {
        //        SurProjectEnable surProjectEnable =
        // surProjectEnableService.getSurProjectEnable(req.getProjectId());
        //        if(surProjectEnable.getTimeEnable() == 1){
        //            Date date = DateUtil.date();
        //            Date startTime = surProjectEnable.getStartTime();
        //            Date endTime = surProjectEnable.getEndTime();
        //            if(startTime!=null&&startTime.getTime()>date.getTime()){
        //                return Result.error("尚未到达开始时间!");
        //            }
        //            if(endTime!=null&&date.getTime()>endTime.getTime()){
        //                return Result.error("答卷时间已结束!");
        //            }
        //        }

        JudgeDto judgeDto = userProjectService.judge360User(req);
        if (judgeDto == null) {
            return Result.error(500, "该用户无权访问！");
        }
        String phone = req.getPhone();
        //        Object code = redisUtil.get("judgeDto"+phone);
        //        if(code != null){
        //            return Result.error("验证码5分钟内，仍然有效！");
        //        }
        // 随机数
        String captcha = RandomUtil.randomNumbers(6);
        JSONObject obj = new JSONObject();
        obj.put("code", captcha);
        boolean b = false;
        // 发送验证码
        b = sendPhoneCOde(captcha, phone);
        if (b == false) {
            return Result.error("短信验证码发送失败,请稍后重试");
        }
        redisUtil.set("judgeDto" + phone, captcha, 300);
        return Result.ok("验证码发送成功!");
    }

    @AutoLog(value = "360项目判断身份")
    @ApiOperation(value = "360项目判断身份", notes = "360项目判断身份")
    @PostMapping(value = "/judge")
    public Result<?> judge(@RequestBody JudgeReq req) {
        SurProject project = surProjectService.getById(req.getProjectId());
        if (Objects.isNull(project)) {
            return Result.error(500, "项目信息未找到!");
        }
        // 查询项目控制信息
        SurProjectEnable projectEnable =
                surProjectEnableService.getOne(
                        new LambdaQueryWrapper<SurProjectEnable>()
                                .eq(SurProjectEnable::getProjectId, req.getProjectId()));
        if (Objects.isNull(projectEnable)) {
            return Result.error(500, "项目未设置控制!");
        }
        // 如果项目不需要导入人员
        if ("调查".equals(project.getType()) && !project.getIsAddUser()) {
            // 如果开启了密码控制
            if (!projectEnable.getPhoneCaptchaEnable() && projectEnable.getPasswordEnable()) {
                if (req.getPassword().equals(projectEnable.getPassword())) {
                    return Result.OK("验证通过!");
                } else {
                    return Result.error(500, "密码错误!");
                }
            }
        }
        // 1.只有验证码控制
        if (projectEnable.getPhoneCaptchaEnable() && !projectEnable.getPasswordEnable()) {
            return judgeByPhoneCaptcha(project, projectEnable, req);
        }
        // 2.只有密码控制
        if (!projectEnable.getPhoneCaptchaEnable() && projectEnable.getPasswordEnable()) {
            return judgeByPassword(project, projectEnable, req);
        }
        // 3。先验证验证码 后验证密码
        if (projectEnable.getPhoneCaptchaEnable() && projectEnable.getPasswordEnable()) {
            return judgeByPhoneCaptchaAndPassword(project, projectEnable, req);
        }
        // 4.没有控制
        if (!projectEnable.getPhoneCaptchaEnable() && !projectEnable.getPasswordEnable()) {
            if ("测评".equals(project.getType())) {
                EvaluationJudgeDto judgeDto = userProjectService.judgeEvaluation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!!", judgeDto);
                }
                return Result.error(500, "该用户无权访问！");
            }
            // 如果是调查项目
            else if ("调查".equals(project.getType())) {
                InvestigationJudgeDto judgeDto = userProjectService.judgeInvestigation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!!", judgeDto);
                }
                return Result.error(500, "该用户无权访问！");
            } else {
                JudgeDto judgeDto = userProjectService.judge360User(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "该用户无权访问！");
            }
        }
        return null;
    }

    @ApiOperation(value = "更新评价关系", notes = "更新评价关系")
    @PostMapping(value = "/updateEvaluation")
    public Result<?> updateEvaluation(@RequestBody EvaluationRelSetReq req) {
        if (userProjectService.updateEvaluation(req)) {
            return Result.ok("更新成功！");
        } else {
            return Result.error(500, "未找到数据！");
        }
    }

    @ApiOperation(value = "删除评价关系", notes = "删除评价关系")
    @DeleteMapping(value = "/deleteEvaluationRelationship/{id}")
    public Result<?> removeEvaluationRelationship(@PathVariable("id") String id) {
        if (userProjectService.removeEvaluationRelationship(id)) {
            return Result.ok("删除成功！");
        } else {
            return Result.error(500, "未找到数据！");
        }
    }

    @ApiOperation(value = "设置360度项目的没有有上下级的评价关系", notes = "设置360度项目的没有有上下级的评价关系")
    @PostMapping(value = "/setCommon")
    public Result<?> setCommonEvaluation(@RequestBody EvaluationRelSetReq req) {
        if (userProjectService.setCommonEvaluationRelationship(req)) {
            return Result.ok("设置成功");
        } else {
            return Result.error(500, "设置失败");
        }
    }

    @ApiOperation(value = "设置360度项目的有上下级的评价关系", notes = "设置360度项目的有上下级的评价关系")
    @PostMapping(value = "/setLevel")
    public Result<?> setLevel(@RequestBody EvaluationRelSetReq req) {
        if (userProjectService.setLevelEvaluationRelationship(req)) {
            return Result.ok("设置成功");
        } else {
            return Result.error(500, "设置失败");
        }
    }

    @ApiOperation(value = "查询360度项目的评价人员", notes = "查询360度项目的评价人员")
    @GetMapping(value = "/queryPerson/{id}")
    public Result<?> select360Person(@PathVariable("id") String id) {
        List<SurUser> userList = userProjectService.get360UserList(id);
        return Result.ok(userList);
    }

    @ApiOperation(value = "删除一些360度项目的评价人员", notes = "删除一些360度项目的评价人员")
    @PostMapping(value = "/removePerson")
    public Result<String> removePerson(@RequestBody Add360UserReq req) {
        if (userProjectService.remove360User(req)) {
            return Result.ok("保存成功！");
        }
        return Result.error(500, "数据未找到！");
    }

    @ApiOperation(value = "批量添加360度项目的评价人员", notes = "批量添加360度项目的评价人员")
    @PostMapping(value = "/addPerson")
    public Result<String> addPerson(@RequestBody Add360UserReq req) {
        SurProject project = surProjectService.getById(req.getId());
        if (project == null) {
            return Result.error(500, "数据未找到！");
        }
        if (!project.getIsAddUser()) {
            project.setIsAddUser(true);
            surProjectService.updateById(project);
        }
        if ("测评".equals(project.getType()) || "调查".equals(project.getType())) {
            if (userProjectService.addEvaluationUser(req)) {
                return Result.ok("保存成功！");
            } else {
                return Result.error(500, "数据未找到！");
            }
        } else {
            if (userProjectService.add360User(req)) {
                return Result.ok("保存成功！");
            } else {
                return Result.error(500, "数据未找到！");
            }
        }
    }

    @ApiOperation(value = "sur_result-普通项目保存用户填写的问卷答案", notes = "sur_result-普通项目保存用户填写的问卷答案")
    @PostMapping(value = "/saveAnswer")
    public Result<String> saveResult(@RequestBody SaveCommonResultReq req) {
        // 查询项目对象
        SurProject project = surProjectService.getById(req.getProjectId());
        if (project == null) {
            return Result.error(500, "数据未找到！");
        }
        switch (project.getType()) {
            case "调查": {
                if (userProjectService.saveInvestigationResult(req)) {
                    return Result.ok("保存成功！");
                } else {
                    return Result.error(500, "该用户已提交过！");
                }
            }
            case "测评": {
                if (userProjectService.saveEvaluationResult(req)) {
                    return Result.ok("保存成功！");
                } else {
                    return Result.error(500, "该用户已提交过！");
                }
            }
            case "360度评估": {
                if (userProjectService.save360Result(req)) {
                    return Result.ok("保存成功！");
                } else {
                    return Result.error(500, "该用户已提交过！");
                }
            }
        }
        return Result.error(500, "该用户已提交过！");
    }

    @AutoLog(value = "统计&&分析  --返回问卷的每个题目的统计信息")
    @ApiOperation(value = "返回问卷的每个题目的统计信息", notes = "返回问卷的每个题目的统计信息")
    @GetMapping(value = "/statistics")
    public Result<?> getStatistics(@RequestParam("id") String id) {
        ProjectCollectDto result = userProjectService.getStatistics(id);
        return Result.OK(result);
    }

    @AutoLog(value = "测评项目统计&&分析  --查询项目问卷列表")
    @ApiOperation(value = "根据问卷id返回每个问题的统计情况", notes = "根据问卷id返回每个问题的统计情况")
    @GetMapping(value = "/getSurveyData/{id}")
    public Result<?> getSurveyData(@PathVariable("id") String id) {
        ProjectCollectDto projectCollectDto = userProjectService.getEvaluationStatistics(id);
        return Result.OK(projectCollectDto);
    }

    @AutoLog(value = "测评项目统计&&分析  --查询每个问卷的情况")
    @ApiOperation(value = "根据问卷id返回每个问题的统计情况", notes = "根据问卷id返回每个问题的统计情况")
    @PostMapping(value = "/getSurveyDataById")
    public Result<?> getSurveyDataById(@RequestBody EvaluationSurveyReq req) {
        List<Object> evaluationSurvey = userProjectService.getEvaluationSurvey(req);
        return Result.OK(evaluationSurvey);
    }

    @AutoLog(value = "收集情况 --查询收集项目详情")
    @ApiOperation(value = "查询收集项目详情", notes = "查询收集项目详情")
    @PostMapping(value = "/getAnalysis")
    public Result<?> analysis(@RequestBody CollectReq req) {
        SurProject project = surProjectService.getById(req.getId());
        if (project == null) {
            return Result.error(500, "数据未找到！");
        }
        if ("测评".equals(project.getType())) {
            EvaluationCollectProjectDto collectDto = userProjectService.getEvaluationCollectData(req);
            return Result.OK(collectDto);
        } else if (("调查").equals(project.getType())) {
            CollectDto collectData = userProjectService.getInvestigationCollectData(req);
            return Result.OK(collectData);
        } else {
            AssessmentDto collectDto = userProjectService.get360CollectData(req);
            return Result.OK(collectDto);
        }
    }

    @AutoLog(value = "数据大屏 --返回问卷的题目json和结果")
    @ApiOperation(value = "返回问卷的题目json和结果", notes = "返回问卷的题目json和结果")
    @GetMapping(value = "/result/{id}")
    public Result<?> getResult(@PathVariable String id) {
        SurProject project = surProjectService.getById(id);
        if (project == null) {
            return Result.error(500, "数据未找到！");
        }
        if ("测评".equals(project.getType())) {
            EvaluationProjectResultDto evaluationSurveyResult =
                    userProjectService.getEvaluationSurveyResult(id);
            return Result.OK(evaluationSurveyResult);
        } else {
            ProjectResultDto surveyResult = userProjectService.getSurveyResult(id);
            return Result.ok(surveyResult);
        }
    }

    @AutoLog(value = "发布项目问卷")
    @ApiOperation(value = "发布项目问卷", notes = "发布项目问卷")
    @PostMapping(value = "/changeStatus")
    public Result<?> publish(@RequestBody ProjectStatusReq req) {
        if (userProjectService.updateStatus(req)) {
            return Result.ok("状态修改成功!");
        }
        return Result.error("操作失败！");
    }

    @AutoLog(value = "survey-根据问题ID查询问题列表和分数")
    @ApiOperation(value = "survey-根据问题ID查询选项列表和分数", notes = "survey-根据问题ID查询问题列表和分数")
    @PostMapping(value = "/choice")
    public Result<List<ChoiceDto>> getQuestionList(@RequestBody ChoiceByQuestionIdReq req) {
        List<ChoiceDto> choiceDtoList = userProjectService.getQuestionListByQuestionId(req);
        return Result.ok(choiceDtoList);
    }

    // 根据选项ID赋分
    @AutoLog(value = "survey-根据选项ID赋分")
    @ApiOperation(value = "survey-根据选项ID赋分", notes = "survey-根据选项ID赋分")
    @PostMapping(value = "/setScore")
    public Result<SurQuestionChoice> getQuestionList(@RequestBody ScoreSetReq req) {
        if (userProjectService.setScore(req)) {
            return Result.ok("设置成功");
        } else {
            return Result.error(500, "设置失败");
        }
    }

    // 根据问题ID查询问题选项和分数
    @AutoLog(value = "survey-根据项目ID查询问题列表")
    @ApiOperation(value = "survey-根据项目ID查询问题列表", notes = "survey-根据项目ID查询问题列表")
    @PostMapping(value = "/question")
    public Result<?> getQuestionList(@RequestBody EvaluationQuestionReq req) {
        Page questionList = userProjectService.getQuestionList(req);
        return Result.ok(questionList);
    }

    /**
     * 根据租户返回项目列表
     *
     * @param req
     * @return
     */
    @AutoLog(value = "根据租户返回项目列表")
    @ApiOperation(value = "根据租户返回项目列表", notes = "根据租户返回项目列表")
    @PostMapping(value = "/getByTenant")
    public Result<?> getByTenant(@RequestBody ProjectAdvancedQueryReq req) {
        Page<SurProject> projectPage = userProjectService.getProjectList(req);
        return Result.OK(projectPage);
    }

    /**
     * 创建项目
     *
     * @param req
     * @return
     */
    @AutoLog(value = "用户端项目表-添加")
    @ApiOperation(value = "用户端项目表-添加", notes = "用户端项目表-添加")
    @PostMapping(value = "/save")
    public Result<?> create(@RequestBody CreateProjectReq req) {
        SurProject project = userProjectService.createProject(req);
        if (project != null) {
            return Result.OK(project);
        } else {
            return Result.error(500, "创建失败");
        }
    }

    @AutoLog(value = "问卷市场-保存")
    @ApiOperation(value = "问卷市场-保存", notes = "问卷市场-保存")
    @PostMapping(value = "/surveyMarketSave")
    public Result<?> surveyMarketSave(@RequestBody SurveyMarketSaveReq req) {
        SurSurveyProject surSurveyProject = userProjectService.surveyMarketSave(req);
        if (surSurveyProject != null) {
            return Result.OK(surSurveyProject);
        } else {
            return Result.error(500, "保存失败");
        }
    }


    /**
     * 分页列表查询
     *
     * @param userProject
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    // @AutoLog(value = "用户端项目表-分页列表查询")
    @ApiOperation(value = "用户端项目表-分页列表查询", notes = "用户端项目表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserProject>> queryPageList(
            UserProject userProject,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        QueryWrapper<UserProject> queryWrapper =
                QueryGenerator.initQueryWrapper(userProject, req.getParameterMap());
        Page<UserProject> page = new Page<UserProject>(pageNo, pageSize);
        IPage<UserProject> pageList = userProjectService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param userProject
     * @return
     */
    @AutoLog(value = "用户端项目表-添加")
    @ApiOperation(value = "用户端项目表-添加", notes = "用户端项目表-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody UserProject userProject) {
        userProjectService.save(userProject);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param userProject
     * @return
     */
    @AutoLog(value = "用户端项目表-编辑")
    @ApiOperation(value = "用户端项目表-编辑", notes = "用户端项目表-编辑")
    @RequestMapping(
            value = "/edit",
            method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody UserProject userProject) {
        SurProject project = surProjectService.getById(userProject.getId());
        if (project == null) {
            return Result.error(500, "未找到对应数据");
        }
        project.setProjectName(userProject.getName());
        project.setContent(userProject.getContent());
        surProjectService.updateById(project);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "用户端项目表-通过id删除")
    @ApiOperation(value = "用户端项目表-通过id删除", notes = "用户端项目表-通过id删除")
    @DeleteMapping(value = "/delete/{id}")
    public Result<String> delete(@PathVariable("id") String id) {
        if (userProjectService.deleteProjectById(id)) {
            return Result.OK("删除成功!");
        }
        return Result.error(500, "项目不存在!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "用户端项目表-批量删除")
    @ApiOperation(value = "用户端项目表-批量删除", notes = "用户端项目表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.userProjectService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    // @AutoLog(value = "用户端项目表-通过id查询")
    @ApiOperation(value = "用户端项目表-通过id查询", notes = "用户端项目表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Object projectInfo = userProjectService.queryProjectInfo(id);
        return Result.OK(projectInfo);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    // @AutoLog(value = "用户端项目表-通过id查询")
    @ApiOperation(value = "用户端项目表-通过id查询", notes = "用户端项目表-通过id查询")
    @GetMapping(value = "/userProjectQueryById")
    public Result<?> userProjectQueryById(@RequestParam(name = "id", required = true) String id) {
        Object userProjectInfo = userProjectService.querySurveyProject(id);
        System.out.println(userProjectInfo);
        return Result.OK(userProjectInfo);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param userProject
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UserProject userProject) {
        return super.exportXls(request, userProject, UserProject.class, "用户端项目表");
    }

    @RequestMapping(value = "/exportIncompleteUsers")
    public void exportIncompleteUsers(HttpServletResponse response, String projectId) {
        try {
            excelService.exportIncompleteUsers(response,projectId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        return super.importExcel(request, response, UserProject.class);
    }

    /**
     * @description: 发送手机验证码
     * @author: GGB
     * @date: 2022/11/2 11:23
     * @param: captcha
     * @param: mobile
     * @return: Boolean
     */
    private Boolean sendPhoneCOde(String captcha, String mobile) throws IOException {
        Boolean b = false;
        // 发送验证码
        String content = "【朗新天霁】验证码" + captcha + "(5分钟有效)。如非本人操作，请忽略本短信。";
        // 内容转成GBK
        StringBuilder sb = new StringBuilder();
        byte[] bytes = content.getBytes("GBK");
        for (byte by : bytes) {
            sb.append("%" + Integer.toHexString((by & 0xff)).toUpperCase());
        }
        String gbk = sb.toString();

        // 时间
        String now = DateUtil.now();
        String nowStr = now.replaceFirst(" ", "&nbsp");
        System.out.println(nowStr);
        String url =
                "http://api.sms1086.com/Api/verifycode.aspx?username=18059257022&password=r12345&mobiles="
                        + mobile
                        + "&content="
                        + gbk;
        System.out.println(url);
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        // 设置请求和传输超时时间
        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setSocketTimeout(60000)
                        .setConnectTimeout(60000)
                        .setConnectionRequestTimeout(60000)
                        .build();
        httpGet.setConfig(requestConfig);
        httpGet.setURI(URI.create(url));
        httpGet.setHeader("Accept", "application/x-www-form-urlencoded");
        httpGet.setHeader("Content-Type", "application/json;charset=GBK");
        httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        String result1 = null;
        if (entity != null) {
            result1 = EntityUtils.toString(entity, "UTF-8");
            System.out.println("这里是短信验证返回的内容");
            String str = result1.substring(0, result1.indexOf("&"));
            System.out.println(result1);
            System.out.println(str);
            if (str.equals("result=0")) {
                b = true;
            }
        }
        httpGet.abort();
        return b;
    }

    @ApiOperation(value = "迁移数据", notes = "迁移数据")
    @PostMapping(value = "/transfer")
    public Result<?> transfer(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") String projectId,
            @RequestParam("surveyId") String surveyId)
            throws IOException {
        // 管理风格测评数据迁移
        userProjectService.transferManagementStyleEvaluation(file, projectId, surveyId);
        System.out.println("迁移成功！");
        return Result.ok("迁移成功！");
    }

    @ApiOperation(value = "迁移数据--插入sur_result", notes = "迁移数据")
    @PostMapping(value = "/transferSurResult")
    public Result<?> transferSurResult(@RequestParam("file") MultipartFile file) throws IOException {
        // 管理风格测评数据迁移
        userProjectService.insertSurResult(file);
        System.out.println("迁移成功！");
        return Result.ok("迁移成功！");
    }

    @ApiOperation(value = "迁移数据--插入sur_user_result", notes = "迁移数据")
    @PostMapping(value = "/transferSurUserResult")
    public Result<?> transferSurUserResult(@RequestParam("file") MultipartFile file)
            throws IOException {
        userProjectService.insertSurUserResult(file);
        System.out.println("迁移成功！");
        return Result.ok("迁移成功！");
    }

    @ApiOperation(value = "迁移数据--插入sur_user_result", notes = "迁移数据")
    @PostMapping(value = "/transferSatisfactionSurvey")
    public Result<?> transferSatisfactionSurvey(@RequestParam("file") MultipartFile file)
            throws IOException {
        userProjectService.transferSatisfactionSurvey(file);
        System.out.println("迁移成功！");
        return Result.ok("迁移成功！");
    }

    // 单独验证码控制
    private Result<?> judgeByPhoneCaptcha(
            SurProject project, SurProjectEnable projectEnable, JudgeReq req) {
        String phone = req.getPhone();
        Object code = redisUtil.get("judgeDto" + phone);
        if (code == null) {
            return Result.error("验证码已失效，请重新获取!");
        }
        if (!code.equals(req.getCode())) {
            return Result.error("验证码错误!");
        }
        if ("测评".equals(project.getType())) {
            EvaluationJudgeDto judgeDto = userProjectService.judgeEvaluation(req);
            if (judgeDto != null) {
                redisUtil.del("judgeDto" + phone);
                return Result.OK("验证通过!!", judgeDto);
            }
            return Result.error(500, "该用户无权访问！");

        } else if ("调查".equals(project.getType())) {
            InvestigationJudgeDto judgeDto = userProjectService.judgeInvestigation(req);
            if (judgeDto != null) {
                redisUtil.del("judgeDto" + phone);
                return Result.OK("验证通过!!", judgeDto);
            }
            return Result.error(500, "该用户无权访问！");
        } else {
            JudgeDto judgeDto = userProjectService.judge360User(req);
            if (judgeDto != null) {
                redisUtil.del("judgeDto" + phone);
                return Result.OK("验证通过!", judgeDto);
            }
            return Result.error(500, "该用户无权访问！");
        }
    }

    // 单独密码控制
    private Result<?> judgeByPassword(
            SurProject project, SurProjectEnable projectEnable, JudgeReq req) {
        if ("测评".equals(project.getType())) {
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                EvaluationJudgeDto judgeDto = userProjectService.judgeEvaluation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "用户无权访问!");
            }
            return Result.error(500, "密码错误！");
        } else if ("调查".equals(project.getType())) {
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                InvestigationJudgeDto judgeDto = userProjectService.judgeInvestigation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "用户无权访问!");
            }
            return Result.error(500, "密码错误！");
        } else {
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                JudgeDto judgeDto = userProjectService.judge360User(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "该用户无权访问！");
            }
            return Result.error(500, "密码错误！");
        }
    }

    // 验证码和密码双重控制
    private Result<?> judgeByPhoneCaptchaAndPassword(
            SurProject project, SurProjectEnable projectEnable, JudgeReq req) {
        // 先判断验证码是否正确
        String phone = req.getPhone();
        Object code = redisUtil.get("judgeDto" + phone);
        if (code == null) {
            return Result.error("验证码已失效，请重新获取!");
        }
        if (!code.equals(req.getCode())) {
            return Result.error("验证码错误!");
        }
        // 判断密码
        // 如果是测评项目
        if ("测评".equals(project.getType())) {
            redisUtil.del("judgeDto" + phone);
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                EvaluationJudgeDto judgeDto = userProjectService.judgeEvaluation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "用户无权访问!");
            }
            return Result.error(500, "密码错误！");
        }
        // 如果是调查项目
        else if ("调查".equals(project.getType())) {
            redisUtil.del("judgeDto" + phone);
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                InvestigationJudgeDto judgeDto = userProjectService.judgeInvestigation(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "用户无权访问!");
            }
            return Result.error(500, "密码错误！");
        }
        // 如果是360度项目
        else {
            redisUtil.del("judgeDto" + phone);
            // 密码正确
            if (req.getPassword().equals(projectEnable.getPassword())) {
                JudgeDto judgeDto = userProjectService.judge360User(req);
                if (judgeDto != null) {
                    return Result.OK("验证通过!", judgeDto);
                }
                return Result.error(500, "该用户无权访问！");
            }
            return Result.error(500, "密码错误！");
        }
    }

    // 获取个人事业驱动力柱状图数据
    private JSONObject getReportBusinessDrivingForce(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportBusinessDrivingForce reportBusinessDrivingForce =
                reportBusinessDrivingForceMapper.selectOne(
                        new LambdaQueryWrapper<ReportBusinessDrivingForce>()
                                .eq(ReportBusinessDrivingForce::getUserId, userId)
                                .eq(ReportBusinessDrivingForce::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "物质报酬");
        obj1.put("value", reportBusinessDrivingForce.getA1());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "权力：影响力");
        obj2.put("value", reportBusinessDrivingForce.getB2());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "追求成就感");
        obj3.put("value", reportBusinessDrivingForce.getC3());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "专精");
        obj4.put("value", reportBusinessDrivingForce.getD4());
        JSONObject obj5 = new JSONObject();
        obj5.put("name", "创意");
        obj5.put("value", reportBusinessDrivingForce.getE5());
        JSONObject obj6 = new JSONObject();
        obj6.put("name", "亲和");
        obj6.put("value", reportBusinessDrivingForce.getF6());
        JSONObject obj7 = new JSONObject();
        obj7.put("name", "自主");
        obj7.put("value", reportBusinessDrivingForce.getG7());
        JSONObject obj8 = new JSONObject();
        obj8.put("name", "安全");
        obj8.put("value", reportBusinessDrivingForce.getH8());
        JSONObject obj9 = new JSONObject();
        obj9.put("name", "地位");
        obj9.put("value", reportBusinessDrivingForce.getI9());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        dataList.add(obj4);
        dataList.add(obj5);
        dataList.add(obj6);
        dataList.add(obj7);
        dataList.add(obj8);
        dataList.add(obj9);
        resultObj.put("data", dataList);
        return resultObj;
    }

    // 获取个人创造力柱状图数据
    private JSONObject getReportCreativeAbility(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportCreativeAbility reportCreativeAbility =
                reportCreativeAbilityMapper.selectOne(
                        new LambdaQueryWrapper<ReportCreativeAbility>()
                                .eq(ReportCreativeAbility::getUserId, userId)
                                .eq(ReportCreativeAbility::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "冒险性");
        obj1.put("value", reportCreativeAbility.getS1());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "好奇心");
        obj2.put("value", reportCreativeAbility.getS2());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "想象力");
        obj3.put("value", reportCreativeAbility.getS3());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "挑战性");
        obj4.put("value", reportCreativeAbility.getS4());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        dataList.add(obj4);
        resultObj.put("data", dataList);
        return resultObj;
    }

    // 获取管理风格测评柱状图数据
    private JSONObject getReportManagementStyleEvaluation(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportManagementStyleEvaluation reportManagementStyleEvaluation =
                reportManagementStyleEvaluationMapper.selectOne(
                        new LambdaQueryWrapper<ReportManagementStyleEvaluation>()
                                .eq(ReportManagementStyleEvaluation::getUserId, userId)
                                .eq(ReportManagementStyleEvaluation::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "S1：指导型");
        obj1.put("value", reportManagementStyleEvaluation.getS1());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "S2：教练型");
        obj2.put("value", reportManagementStyleEvaluation.getS2());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "S3：支持型");
        obj3.put("value", reportManagementStyleEvaluation.getS3());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "S4：授权型");
        obj4.put("value", reportManagementStyleEvaluation.getS4());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        dataList.add(obj4);
        resultObj.put("data", dataList);
        return resultObj;
    }

    // 获取人才思维方式测评柱状图数据
    private JSONObject getReportModeOfThinking(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportModeOfThinking reportModeOfThinking =
                reportModeOfThinkingMapper.selectOne(
                        new LambdaQueryWrapper<ReportModeOfThinking>()
                                .eq(ReportModeOfThinking::getUserId, userId)
                                .eq(ReportModeOfThinking::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "蓝色思维");
        obj1.put("value", reportModeOfThinking.getZx1());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "红色思维");
        obj2.put("value", reportModeOfThinking.getZx2());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "绿色思维");
        obj3.put("value", reportModeOfThinking.getZx3());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        resultObj.put("data", dataList);
        return resultObj;
    }

    // 获取团队角色测评柱状图数据
    private JSONObject getReportTeamRole(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportTeamRole reportTeamRole =
                reportTeamRoleMapper.selectOne(
                        new LambdaQueryWrapper<ReportTeamRole>()
                                .eq(ReportTeamRole::getUserId, userId)
                                .eq(ReportTeamRole::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "IMP");
        obj1.put("value", reportTeamRole.getImp());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "CO");
        obj2.put("value", reportTeamRole.getCo());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "SH");
        obj3.put("value", reportTeamRole.getSh());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "PL");
        obj4.put("value", reportTeamRole.getPl());
        JSONObject obj5 = new JSONObject();
        obj5.put("name", "RI");
        obj5.put("value", reportTeamRole.getRi());
        JSONObject obj6 = new JSONObject();
        obj6.put("name", "ME");
        obj6.put("value", reportTeamRole.getMe());
        JSONObject obj7 = new JSONObject();
        obj7.put("name", "TW");
        obj7.put("value", reportTeamRole.getTw());
        JSONObject obj8 = new JSONObject();
        obj8.put("name", "CF");
        obj8.put("value", reportTeamRole.getCf());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        dataList.add(obj4);
        dataList.add(obj5);
        dataList.add(obj6);
        dataList.add(obj7);
        dataList.add(obj8);
        resultObj.put("data", dataList);
        return resultObj;
    }

    // 获取自我驱动力测评柱状图数据
    private JSONObject getReportSelfMotivation(String projectId, String userId) {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> dataList = new ArrayList<>();
        // 查询  该用户的测评报告
        ReportSelfMotivation reportSelfMotivation =
                reportSelfMotivationMapper.selectOne(
                        new LambdaQueryWrapper<ReportSelfMotivation>()
                                .eq(ReportSelfMotivation::getUserId, userId)
                                .eq(ReportSelfMotivation::getProjectId, projectId));
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "A：财务动机");
        obj1.put("value", reportSelfMotivation.getA1());
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "B：表彰与称赞/工作认可");
        obj2.put("value", reportSelfMotivation.getB1());
        JSONObject obj3 = new JSONObject();
        obj3.put("name", "C：工作责任");
        obj3.put("value", reportSelfMotivation.getC1());
        JSONObject obj4 = new JSONObject();
        obj4.put("name", "D：和你的经理的关系");
        obj4.put("value", reportSelfMotivation.getD1());
        JSONObject obj5 = new JSONObject();
        obj5.put("name", "E：晋升机会");
        obj5.put("value", reportSelfMotivation.getE1());
        JSONObject obj6 = new JSONObject();
        obj6.put("name", "F：工作上的成就感");
        obj6.put("value", reportSelfMotivation.getF1());
        JSONObject obj7 = new JSONObject();
        obj7.put("name", "H：工作内容/工作本身兴趣感");
        obj7.put("value", reportSelfMotivation.getH1());
        JSONObject obj8 = new JSONObject();
        obj8.put("name", "I：和他人合作/内部人际关系");
        obj8.put("value", reportSelfMotivation.getI1());
        dataList.add(obj1);
        dataList.add(obj2);
        dataList.add(obj3);
        dataList.add(obj4);
        dataList.add(obj5);
        dataList.add(obj6);
        dataList.add(obj7);
        dataList.add(obj8);
        resultObj.put("data", dataList);
        return resultObj;
    }
}

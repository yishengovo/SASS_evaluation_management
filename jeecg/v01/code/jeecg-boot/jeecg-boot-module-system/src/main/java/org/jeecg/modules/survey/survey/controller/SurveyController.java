package org.jeecg.modules.survey.survey.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.req.*;
import org.jeecg.modules.survey.survey.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.survey.survey.service.impl.SurveyServiceImpl;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: survey
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="survey")
@RestController
@RequestMapping("/survey/survey")
@Slf4j
public class SurveyController extends JeecgController<Survey, ISurveyService> {
	@Autowired
	private ISurveyService surveyService;
	@Autowired
	private ISurQuestionService questionService;
	@Autowired
	private ISurQuestionChoiceService questionChoiceService;
	@Autowired
	private ISurProjectService projectService;
	@Autowired
	private SurProjectSurveyService projectSurveyService;
	 @Autowired
	 private ISurUserResultService userResultService;
	 @Autowired
	 private ISurResultService resultService;
	 @Autowired
	 private ISysTenantService tenantService;
	 @Autowired
	 private ISurSurveyTenantService surveyTenantService;


	 //查询租户列表
	 @AutoLog(value = "查询租户列表")
	 @ApiOperation(value="查询租户列表", notes="查询租户列表")
	 @GetMapping(value = "/tenant")
	 public Result<?> getTenantList() {
		 List<SysTenant> tenantList = tenantService.list();
		 return Result.ok(tenantList);
	 }


	 //根据问卷ID查询问题和选项
	 @AutoLog(value = "survey-根据问卷ID查询问题和选项")
	 @ApiOperation(value="survey-根据问卷ID查询问题和选项", notes="survey-根据问卷ID查询问题和选项")
	 @GetMapping(value = "/questionById")
	 public Result<List<SurveyDto>> getQuestionAndChoice(@RequestParam(name="id",required=true) String id) {
		 List<SurveyDto> result = surveyService.getQuestionAndChoice(id);
		 return Result.ok(result);
	 }

	 //根据选项ID赋分
	 @AutoLog(value = "survey-根据选项ID赋分")
	 @ApiOperation(value="survey-根据选项ID赋分", notes="survey-根据选项ID赋分")
	 @PostMapping(value = "/setScore")
	 @Transactional
	 public Result<SurQuestionChoice> getQuestionList(@RequestBody ScoreSetReq req) {
		 //先根据选项id查询选项实体
		 SurQuestionChoice choice = questionChoiceService.getById(req.getId());
//		 String content = choice.getContent();
//		 List<String> choiceList = Arrays.asList(content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
		 choice.setBasicScore(req.getScore().toString());
		 choice.setPid(req.getPid());
		 //更新数据库
		 questionChoiceService.updateById(choice);
		 return Result.ok(choice);
	 }


	 //根据问卷ID查询问题列表
	 @AutoLog(value = "survey-根据问题ID查询问题列表和分数")
	 @ApiOperation(value="survey-根据问题ID查询选项列表和分数", notes="survey-根据问题ID查询问题列表和分数")
	 @PostMapping(value = "/choice")
	 @Transactional
	 public Result<List<ChoiceDto>> getQuestionList(@RequestBody ChoiceByQuestionIdReq req) {
		 SurQuestionChoice choice = questionChoiceService.getOne(new LambdaQueryWrapper<SurQuestionChoice>().eq(SurQuestionChoice::getQuestionId, req.getQuestionId()));
		 //获取选项数组
		 String content = choice.getContent();
		 List<String> choiceList = Arrays.asList(content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
		 //获取答案数组
		 String basicScore = choice.getBasicScore();
		 List<ChoiceDto> result = new ArrayList<>();
		 if (basicScore == null){
			 //如果选项为空
			 if (choiceList.size() == 1){
				 return Result.ok(null);
			 }
			 //如果选项不为空
			 for (String s : choiceList) {
				 ChoiceDto choiceDto = new ChoiceDto();
				 BeanUtils.copyProperties(choice,choiceDto);
				 choiceDto.setChoice(s);
				 choiceDto.setBasicScore(0);
				 result.add(choiceDto);
			 }
			 return Result.ok(result);
		 }
		 List<String> scoreList = Arrays.asList(basicScore.substring(1, basicScore.length() - 1).replaceAll("\\s", "").split(","));
		 //将选项数组拆分成单个的
		 if (choiceList.size()!= 0){
			 for (int i = 0; i < choiceList.size(); i++) {
				 if (scoreList.get(i) != null){
					 ChoiceDto choiceDto = new ChoiceDto();
					 BeanUtils.copyProperties(choice,choiceDto);
					 choiceDto.setChoice(choiceList.get(i));
					 choiceDto.setBasicScore(scoreList.get(i));
					 result.add(choiceDto);
				 }
			 }
		 }
		 else {
			 return Result.ok(null);
		 }
		 return Result.ok(result);
	 }

	 //根据问题ID查询问题选项和分数
	 @AutoLog(value = "survey-根据问卷ID查询问题列表")
	 @ApiOperation(value="survey-根据问卷ID查询问题列表", notes="survey-根据问卷ID查询问题列表")
	 @PostMapping(value = "/question")
	 @Transactional
	 public Result<Page<SurQuestion>> getQuestionList(@RequestBody QuestionBySurveyIdReq req) {
		 Page<SurQuestion> page = questionService.page(new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getId()));
		 return Result.ok(page);
	 }


	 //为问题填写唯一编号
	 @AutoLog(value = "survey-为问题填写唯一编号")
	 @ApiOperation(value="survey-为问题填写唯一编号", notes="survey-为问题填写唯一编号")
	 @PostMapping(value = "/setQuestionPid")
	 public Result<?> setQuestionPid(@RequestBody QuestionSetPidReq req) {
		 if (questionService.setQuestionPid(req)){
			 return Result.ok("设置成功");
		 }
		 return Result.error(500,"设置失败");
	 }


	//保存json
	@AutoLog(value = "survey-保存问卷json")
	@ApiOperation(value="survey-保存问卷json", notes="survey-保存问卷json")
	@PostMapping(value = "/saveJson")
	@Transactional
	public Result<String> saveJson(@RequestBody SaveJsonReq req) {
		if (surveyService.saveJsonPreview(req)){
			return Result.OK("保存成功！");
		}
		return Result.error(500,"操作失败!");
	}



	/**
	 * 分页列表查询
	 *
	 * @param survey
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "survey-分页列表查询")
	@ApiOperation(value="survey-分页列表查询", notes="survey-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Survey>> queryPageList(Survey survey,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Survey> queryWrapper = QueryGenerator.initQueryWrapper(survey, req.getParameterMap());
		Page<Survey> page = new Page<Survey>(pageNo, pageSize);
		IPage<Survey> pageList = surveyService.page(page, queryWrapper);
		List<Survey> records = pageList.getRecords();
		records.forEach(record -> {
			List<Integer> tenantList = surveyTenantService.list(new LambdaQueryWrapper<SurSurveyTenant>().eq(SurSurveyTenant::getSurveyId, record.getId())).stream().map(SurSurveyTenant::getTenantId).collect(Collectors.toList());
			record.setTenantIdList(tenantList);
		});
		pageList.setRecords(records);
		//查询租户list
		return Result.OK(pageList);
	}

	/**
	 *   新建问卷模板
	 *
	 * @param req
	 * @return
	 */
	@AutoLog(value = "survey-添加")
	@ApiOperation(value="survey-添加", notes="survey-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurveyCreateReq req) {
		if (surveyService.createSurvey(req)){
			return Result.ok("新建成功!");
		}
		return Result.error("操作失败！");
	}

	/**
	 *  编辑
	 *
	 * @param survey
	 * @return
	 */
	@AutoLog(value = "survey-编辑")
	@ApiOperation(value="survey-编辑", notes="survey-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	@Transactional(rollbackFor = Exception.class)
	public Result<String> edit(@RequestBody Survey survey) {
		//删除租户和问卷关系
		surveyTenantService.remove(new LambdaQueryWrapper<SurSurveyTenant>().eq(SurSurveyTenant::getSurveyId, survey.getId()));
		//添加租户和问卷关系
		if(survey.getTenantIdList() != null && !survey.getTenantIdList().isEmpty()) {
			survey.setIsPublic(false);
			survey.getTenantIdList().forEach(tenantId -> {
				SurSurveyTenant surveyTenant = new SurSurveyTenant();
				surveyTenant.setSurveyId(survey.getId());
				surveyTenant.setTenantId(tenantId);
				surveyTenantService.save(surveyTenant);
			});
		}
		else {
			survey.setIsPublic(true);
		}
		surveyService.updateById(survey);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "survey-通过id删除")
	@ApiOperation(value="survey-通过id删除", notes="survey-通过id删除")
	@DeleteMapping(value = "/delete")
	@Transactional
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		//先判断有没有项目绑定了该问卷
		List<String> projectIdS = projectSurveyService.list(new LambdaQueryWrapper<SurProjectSurvey>().eq(SurProjectSurvey::getSurveyId, id)).stream()
				.map(SurProjectSurvey::getProjectId)
				.collect(Collectors.toList());
		if (!projectIdS.isEmpty()){
			return Result.error("此问卷已经被项目绑定过！");
		}
		//删除题目和选项
		questionService.remove(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid,id));
		questionChoiceService.remove(new LambdaQueryWrapper<SurQuestionChoice>().eq(SurQuestionChoice::getSurveyUid,id));
		//删除用户的答卷
		//删除所有已提交的用户的问卷
		resultService.remove(new LambdaQueryWrapper<SurResult>().eq(SurResult::getSurveyUid,id));
		userResultService.remove(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getSurveyId,id));
		surveyService.removeById(id);
		//删除租户问卷关系
		surveyTenantService.remove(new LambdaQueryWrapper<SurSurveyTenant>().eq(SurSurveyTenant::getSurveyId,id));
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "survey-批量删除")
	@ApiOperation(value="survey-批量删除", notes="survey-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surveyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "survey-通过id查询")
	@ApiOperation(value="survey-通过id查询", notes="survey-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Survey> queryById(@RequestParam(name="id",required=true) String id) {
		Survey survey = surveyService.getById(id);
		if(survey==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(survey);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param survey
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Survey survey) {
        return super.exportXls(request, survey, Survey.class, "survey");
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
        return super.importExcel(request, response, Survey.class);
    }





}

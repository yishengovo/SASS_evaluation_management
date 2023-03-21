package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurQuestion;
import org.jeecg.modules.survey.survey.service.ISurQuestionDimensionService;
import org.jeecg.modules.survey.survey.service.ISurQuestionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: sur_question
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_question")
@RestController
@RequestMapping("/survey/surQuestion")
@Slf4j
public class SurQuestionController extends JeecgController<SurQuestion, ISurQuestionService> {
	@Autowired
	private ISurQuestionService surQuestionService;
	@Autowired
	private ISurQuestionDimensionService questionDimensionService;

	/**
	 * 分页列表查询
	 *
	 * @param surQuestion
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_question-分页列表查询")
	@ApiOperation(value="sur_question-分页列表查询", notes="sur_question-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurQuestion>> queryPageList(SurQuestion surQuestion,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurQuestion> queryWrapper = QueryGenerator.initQueryWrapper(surQuestion, req.getParameterMap());
		Page<SurQuestion> page = new Page<SurQuestion>(pageNo, pageSize);
		IPage<SurQuestion> pageList = surQuestionService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surQuestion
	 * @return
	 */
	@AutoLog(value = "sur_question-添加")
	@ApiOperation(value="sur_question-添加", notes="sur_question-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurQuestion surQuestion) {
		surQuestionService.save(surQuestion);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surQuestion
	 * @return
	 */
	@AutoLog(value = "sur_question-编辑")
	@ApiOperation(value="sur_question-编辑", notes="sur_question-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurQuestion surQuestion) {
		surQuestionService.updateById(surQuestion);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_question-通过id删除")
	@ApiOperation(value="sur_question-通过id删除", notes="sur_question-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surQuestionService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_question-批量删除")
	@ApiOperation(value="sur_question-批量删除", notes="sur_question-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surQuestionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_question-通过id查询")
	@ApiOperation(value="sur_question-通过id查询", notes="sur_question-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurQuestion> queryById(@RequestParam(name="id",required=true) String id) {
		SurQuestion surQuestion = surQuestionService.getById(id);
		if(surQuestion==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surQuestion);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surQuestion
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurQuestion surQuestion) {
        return super.exportXls(request, surQuestion, SurQuestion.class, "sur_question");
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
        return super.importExcel(request, response, SurQuestion.class);
    }

//	 /**
//	  * 设置题目维度
//	  *
//	  * @param req
//	  * @return
//	  */
//	 @PostMapping(value = "/setDimension")
//	 public Result<?> setDimension(@RequestBody DimensionSetReq req) {
//		 List<SurQuestionDimension> surQuestionDimensionList = req.getList();
//		 //遍历权重数组
//		 surQuestionDimensionList.forEach(surQuestionDimension -> {
//			 questionDimensionService.save(surQuestionDimension);
//		 });
//		 return Result.ok("保存成功！");
//	 }

}

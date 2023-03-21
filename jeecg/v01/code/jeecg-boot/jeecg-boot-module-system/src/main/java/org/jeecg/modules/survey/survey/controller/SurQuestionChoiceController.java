package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurQuestionChoice;
import org.jeecg.modules.survey.survey.service.ISurQuestionChoiceService;

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
 * @Description: sur_question_choice
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_question_choice")
@RestController
@RequestMapping("/survey/surQuestionChoice")
@Slf4j
public class SurQuestionChoiceController extends JeecgController<SurQuestionChoice, ISurQuestionChoiceService> {
	@Autowired
	private ISurQuestionChoiceService surQuestionChoiceService;

	/**
	 * 分页列表查询
	 *
	 * @param surQuestionChoice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_question_choice-分页列表查询")
	@ApiOperation(value="sur_question_choice-分页列表查询", notes="sur_question_choice-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurQuestionChoice>> queryPageList(SurQuestionChoice surQuestionChoice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurQuestionChoice> queryWrapper = QueryGenerator.initQueryWrapper(surQuestionChoice, req.getParameterMap());
		Page<SurQuestionChoice> page = new Page<SurQuestionChoice>(pageNo, pageSize);
		IPage<SurQuestionChoice> pageList = surQuestionChoiceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surQuestionChoice
	 * @return
	 */
	@AutoLog(value = "sur_question_choice-添加")
	@ApiOperation(value="sur_question_choice-添加", notes="sur_question_choice-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurQuestionChoice surQuestionChoice) {
		surQuestionChoiceService.save(surQuestionChoice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surQuestionChoice
	 * @return
	 */
	@AutoLog(value = "sur_question_choice-编辑")
	@ApiOperation(value="sur_question_choice-编辑", notes="sur_question_choice-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurQuestionChoice surQuestionChoice) {
		surQuestionChoiceService.updateById(surQuestionChoice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_question_choice-通过id删除")
	@ApiOperation(value="sur_question_choice-通过id删除", notes="sur_question_choice-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surQuestionChoiceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_question_choice-批量删除")
	@ApiOperation(value="sur_question_choice-批量删除", notes="sur_question_choice-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surQuestionChoiceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_question_choice-通过id查询")
	@ApiOperation(value="sur_question_choice-通过id查询", notes="sur_question_choice-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurQuestionChoice> queryById(@RequestParam(name="id",required=true) String id) {
		SurQuestionChoice surQuestionChoice = surQuestionChoiceService.getById(id);
		if(surQuestionChoice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surQuestionChoice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surQuestionChoice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurQuestionChoice surQuestionChoice) {
        return super.exportXls(request, surQuestionChoice, SurQuestionChoice.class, "sur_question_choice");
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
        return super.importExcel(request, response, SurQuestionChoice.class);
    }

}

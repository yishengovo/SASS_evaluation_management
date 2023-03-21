package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurSurveyTenant;
import org.jeecg.modules.survey.survey.service.ISurSurveyTenantService;

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
 * @Description: 问卷租户关系表
 * @Author: jeecg-boot
 * @Date:   2022-11-08
 * @Version: V1.0
 */
@Api(tags="问卷租户关系表")
@RestController
@RequestMapping("/survey/surSurveyTenant")
@Slf4j
public class SurSurveyTenantController extends JeecgController<SurSurveyTenant, ISurSurveyTenantService> {
	@Autowired
	private ISurSurveyTenantService surSurveyTenantService;

	/**
	 * 分页列表查询
	 *
	 * @param surSurveyTenant
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "问卷租户关系表-分页列表查询")
	@ApiOperation(value="问卷租户关系表-分页列表查询", notes="问卷租户关系表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurSurveyTenant>> queryPageList(SurSurveyTenant surSurveyTenant,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurSurveyTenant> queryWrapper = QueryGenerator.initQueryWrapper(surSurveyTenant, req.getParameterMap());
		Page<SurSurveyTenant> page = new Page<SurSurveyTenant>(pageNo, pageSize);
		IPage<SurSurveyTenant> pageList = surSurveyTenantService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surSurveyTenant
	 * @return
	 */
	@AutoLog(value = "问卷租户关系表-添加")
	@ApiOperation(value="问卷租户关系表-添加", notes="问卷租户关系表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurSurveyTenant surSurveyTenant) {
		surSurveyTenantService.save(surSurveyTenant);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surSurveyTenant
	 * @return
	 */
	@AutoLog(value = "问卷租户关系表-编辑")
	@ApiOperation(value="问卷租户关系表-编辑", notes="问卷租户关系表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurSurveyTenant surSurveyTenant) {
		surSurveyTenantService.updateById(surSurveyTenant);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问卷租户关系表-通过id删除")
	@ApiOperation(value="问卷租户关系表-通过id删除", notes="问卷租户关系表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surSurveyTenantService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "问卷租户关系表-批量删除")
	@ApiOperation(value="问卷租户关系表-批量删除", notes="问卷租户关系表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surSurveyTenantService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "问卷租户关系表-通过id查询")
	@ApiOperation(value="问卷租户关系表-通过id查询", notes="问卷租户关系表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurSurveyTenant> queryById(@RequestParam(name="id",required=true) String id) {
		SurSurveyTenant surSurveyTenant = surSurveyTenantService.getById(id);
		if(surSurveyTenant==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surSurveyTenant);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surSurveyTenant
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurSurveyTenant surSurveyTenant) {
        return super.exportXls(request, surSurveyTenant, SurSurveyTenant.class, "问卷租户关系表");
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
        return super.importExcel(request, response, SurSurveyTenant.class);
    }

}

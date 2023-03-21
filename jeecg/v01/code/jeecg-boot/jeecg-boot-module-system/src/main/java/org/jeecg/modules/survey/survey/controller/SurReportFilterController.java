package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurReportFilter;
import org.jeecg.modules.survey.survey.service.ISurReportFilterService;

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
 * @Description: sur_report_filter
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_report_filter")
@RestController
@RequestMapping("/survey/surReportFilter")
@Slf4j
public class SurReportFilterController extends JeecgController<SurReportFilter, ISurReportFilterService> {
	@Autowired
	private ISurReportFilterService surReportFilterService;

	/**
	 * 分页列表查询
	 *
	 * @param surReportFilter
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_report_filter-分页列表查询")
	@ApiOperation(value="sur_report_filter-分页列表查询", notes="sur_report_filter-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurReportFilter>> queryPageList(SurReportFilter surReportFilter,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurReportFilter> queryWrapper = QueryGenerator.initQueryWrapper(surReportFilter, req.getParameterMap());
		Page<SurReportFilter> page = new Page<SurReportFilter>(pageNo, pageSize);
		IPage<SurReportFilter> pageList = surReportFilterService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surReportFilter
	 * @return
	 */
	@AutoLog(value = "sur_report_filter-添加")
	@ApiOperation(value="sur_report_filter-添加", notes="sur_report_filter-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurReportFilter surReportFilter) {
		surReportFilterService.save(surReportFilter);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surReportFilter
	 * @return
	 */
	@AutoLog(value = "sur_report_filter-编辑")
	@ApiOperation(value="sur_report_filter-编辑", notes="sur_report_filter-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurReportFilter surReportFilter) {
		surReportFilterService.updateById(surReportFilter);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_report_filter-通过id删除")
	@ApiOperation(value="sur_report_filter-通过id删除", notes="sur_report_filter-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surReportFilterService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_report_filter-批量删除")
	@ApiOperation(value="sur_report_filter-批量删除", notes="sur_report_filter-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surReportFilterService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_report_filter-通过id查询")
	@ApiOperation(value="sur_report_filter-通过id查询", notes="sur_report_filter-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurReportFilter> queryById(@RequestParam(name="id",required=true) String id) {
		SurReportFilter surReportFilter = surReportFilterService.getById(id);
		if(surReportFilter==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surReportFilter);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surReportFilter
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurReportFilter surReportFilter) {
        return super.exportXls(request, surReportFilter, SurReportFilter.class, "sur_report_filter");
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
        return super.importExcel(request, response, SurReportFilter.class);
    }

}

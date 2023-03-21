package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurFilter;
import org.jeecg.modules.survey.survey.service.ISurFilterService;

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
 * @Description: sur_filter
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_filter")
@RestController
@RequestMapping("/survey/surFilter")
@Slf4j
public class SurFilterController extends JeecgController<SurFilter, ISurFilterService> {
	@Autowired
	private ISurFilterService surFilterService;

	/**
	 * 分页列表查询
	 *
	 * @param surFilter
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_filter-分页列表查询")
	@ApiOperation(value="sur_filter-分页列表查询", notes="sur_filter-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurFilter>> queryPageList(SurFilter surFilter,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurFilter> queryWrapper = QueryGenerator.initQueryWrapper(surFilter, req.getParameterMap());
		Page<SurFilter> page = new Page<SurFilter>(pageNo, pageSize);
		IPage<SurFilter> pageList = surFilterService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surFilter
	 * @return
	 */
	@AutoLog(value = "sur_filter-添加")
	@ApiOperation(value="sur_filter-添加", notes="sur_filter-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurFilter surFilter) {
		surFilterService.save(surFilter);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surFilter
	 * @return
	 */
	@AutoLog(value = "sur_filter-编辑")
	@ApiOperation(value="sur_filter-编辑", notes="sur_filter-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurFilter surFilter) {
		surFilterService.updateById(surFilter);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_filter-通过id删除")
	@ApiOperation(value="sur_filter-通过id删除", notes="sur_filter-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surFilterService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_filter-批量删除")
	@ApiOperation(value="sur_filter-批量删除", notes="sur_filter-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surFilterService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_filter-通过id查询")
	@ApiOperation(value="sur_filter-通过id查询", notes="sur_filter-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurFilter> queryById(@RequestParam(name="id",required=true) String id) {
		SurFilter surFilter = surFilterService.getById(id);
		if(surFilter==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surFilter);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surFilter
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurFilter surFilter) {
        return super.exportXls(request, surFilter, SurFilter.class, "sur_filter");
    }

    /**
      * 通过excel导入数据;
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SurFilter.class);
    }

}

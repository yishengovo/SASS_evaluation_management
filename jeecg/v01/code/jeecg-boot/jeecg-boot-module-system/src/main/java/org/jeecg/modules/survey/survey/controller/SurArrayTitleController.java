package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurArrayTitle;
import org.jeecg.modules.survey.survey.service.ISurArrayTitleService;

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
 * @Description: sur_array_title
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_array_title")
@RestController
@RequestMapping("/survey/surArrayTitle")
@Slf4j
public class SurArrayTitleController extends JeecgController<SurArrayTitle, ISurArrayTitleService> {
	@Autowired
	private ISurArrayTitleService surArrayTitleService;

	/**
	 * 分页列表查询
	 *
	 * @param surArrayTitle
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_array_title-分页列表查询")
	@ApiOperation(value="sur_array_title-分页列表查询", notes="sur_array_title-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurArrayTitle>> queryPageList(SurArrayTitle surArrayTitle,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurArrayTitle> queryWrapper = QueryGenerator.initQueryWrapper(surArrayTitle, req.getParameterMap());
		Page<SurArrayTitle> page = new Page<SurArrayTitle>(pageNo, pageSize);
		IPage<SurArrayTitle> pageList = surArrayTitleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surArrayTitle
	 * @return
	 */
	@AutoLog(value = "sur_array_title-添加")
	@ApiOperation(value="sur_array_title-添加", notes="sur_array_title-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurArrayTitle surArrayTitle) {
		surArrayTitleService.save(surArrayTitle);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surArrayTitle
	 * @return
	 */
	@AutoLog(value = "sur_array_title-编辑")
	@ApiOperation(value="sur_array_title-编辑", notes="sur_array_title-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurArrayTitle surArrayTitle) {
		surArrayTitleService.updateById(surArrayTitle);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_array_title-通过id删除")
	@ApiOperation(value="sur_array_title-通过id删除", notes="sur_array_title-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surArrayTitleService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_array_title-批量删除")
	@ApiOperation(value="sur_array_title-批量删除", notes="sur_array_title-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surArrayTitleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_array_title-通过id查询")
	@ApiOperation(value="sur_array_title-通过id查询", notes="sur_array_title-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurArrayTitle> queryById(@RequestParam(name="id",required=true) String id) {
		SurArrayTitle surArrayTitle = surArrayTitleService.getById(id);
		if(surArrayTitle==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surArrayTitle);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surArrayTitle
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurArrayTitle surArrayTitle) {
        return super.exportXls(request, surArrayTitle, SurArrayTitle.class, "sur_array_title");
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
        return super.importExcel(request, response, SurArrayTitle.class);
    }

}

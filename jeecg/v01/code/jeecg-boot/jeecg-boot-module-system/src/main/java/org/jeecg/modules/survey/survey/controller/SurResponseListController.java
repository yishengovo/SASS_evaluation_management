package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurResponseList;
import org.jeecg.modules.survey.survey.service.ISurResponseListService;

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
 * @Description: sur_response_list
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_response_list")
@RestController
@RequestMapping("/survey/surResponseList")
@Slf4j
public class SurResponseListController extends JeecgController<SurResponseList, ISurResponseListService> {
	@Autowired
	private ISurResponseListService surResponseListService;

	/**
	 * 分页列表查询
	 *
	 * @param surResponseList
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_response_list-分页列表查询")
	@ApiOperation(value="sur_response_list-分页列表查询", notes="sur_response_list-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurResponseList>> queryPageList(SurResponseList surResponseList,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurResponseList> queryWrapper = QueryGenerator.initQueryWrapper(surResponseList, req.getParameterMap());
		Page<SurResponseList> page = new Page<SurResponseList>(pageNo, pageSize);
		IPage<SurResponseList> pageList = surResponseListService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surResponseList
	 * @return
	 */
	@AutoLog(value = "sur_response_list-添加")
	@ApiOperation(value="sur_response_list-添加", notes="sur_response_list-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurResponseList surResponseList) {
		surResponseListService.save(surResponseList);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surResponseList
	 * @return
	 */
	@AutoLog(value = "sur_response_list-编辑")
	@ApiOperation(value="sur_response_list-编辑", notes="sur_response_list-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurResponseList surResponseList) {
		surResponseListService.updateById(surResponseList);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_response_list-通过id删除")
	@ApiOperation(value="sur_response_list-通过id删除", notes="sur_response_list-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surResponseListService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_response_list-批量删除")
	@ApiOperation(value="sur_response_list-批量删除", notes="sur_response_list-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surResponseListService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_response_list-通过id查询")
	@ApiOperation(value="sur_response_list-通过id查询", notes="sur_response_list-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurResponseList> queryById(@RequestParam(name="id",required=true) String id) {
		SurResponseList surResponseList = surResponseListService.getById(id);
		if(surResponseList==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surResponseList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surResponseList
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurResponseList surResponseList) {
        return super.exportXls(request, surResponseList, SurResponseList.class, "sur_response_list");
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
        return super.importExcel(request, response, SurResponseList.class);
    }

}

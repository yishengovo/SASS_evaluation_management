package org.jeecg.modules.survey.client.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.survey.client.entity.SurTag;
import org.jeecg.modules.survey.client.req.TagQueryReq;
import org.jeecg.modules.survey.client.service.ISurTagService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 问卷标签表
 * @Author: jeecg-boot
 * @Date:   2023-03-23
 * @Version: V1.0
 */
@Api(tags="问卷标签表")
@RestController
@RequestMapping("/client/surTag")
@Slf4j
public class SurTagController extends JeecgController<SurTag, ISurTagService> {
	@Autowired
	private ISurTagService surTagService;

	/**
	 * 分页列表查询
	 * @param req
	 * @return
	 */
	@AutoLog(value = "问卷标签表-分页列表查询")
	@ApiOperation(value="问卷标签表-分页列表查询", notes="问卷标签表-分页列表查询")
	@PostMapping(value = "/query")
	public Result<?> queryList(@RequestBody TagQueryReq req) {
		Page<SurTag> queryPage = surTagService.queryPage(req);
		return Result.OK(queryPage);
	}

	/**
	 * 分页列表查询
	 *
	 * @param surTag
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "问卷标签表-分页列表查询")
	@ApiOperation(value="问卷标签表-分页列表查询", notes="问卷标签表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurTag>> queryPageList(SurTag surTag,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		QueryWrapper<SurTag> queryWrapper = QueryGenerator.initQueryWrapper(surTag, req.getParameterMap());
		Page<SurTag> page = new Page<SurTag>(pageNo, pageSize);
		IPage<SurTag> pageList = surTagService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surTag
	 * @return
	 */
	@AutoLog(value = "问卷标签表-添加")
	@ApiOperation(value="问卷标签表-添加", notes="问卷标签表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurTag surTag) {
		surTagService.save(surTag);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surTag
	 * @return
	 */
	@AutoLog(value = "问卷标签表-编辑")
	@ApiOperation(value="问卷标签表-编辑", notes="问卷标签表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurTag surTag) {
		surTagService.updateById(surTag);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问卷标签表-通过id删除")
	@ApiOperation(value="问卷标签表-通过id删除", notes="问卷标签表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surTagService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "问卷标签表-批量删除")
	@ApiOperation(value="问卷标签表-批量删除", notes="问卷标签表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surTagService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "问卷标签表-通过id查询")
	@ApiOperation(value="问卷标签表-通过id查询", notes="问卷标签表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurTag> queryById(@RequestParam(name="id",required=true) String id) {
		SurTag surTag = surTagService.getById(id);
		if(surTag==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surTag);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param surTag
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SurTag surTag) {
		return super.exportXls(request, surTag, SurTag.class, "问卷标签表");
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
		return super.importExcel(request, response, SurTag.class);
	}

}
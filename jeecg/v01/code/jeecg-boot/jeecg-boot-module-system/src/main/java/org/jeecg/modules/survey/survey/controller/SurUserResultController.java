package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurUserResult;
import org.jeecg.modules.survey.survey.service.ISurUserResultService;

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
 * @Description: 用户所提交的问卷答案
 * @Author: jeecg-boot
 * @Date:   2022-07-12
 * @Version: V1.0
 */
@Api(tags="用户所提交的问卷答案")
@RestController
@RequestMapping("/survey/surUserResult")
@Slf4j
public class SurUserResultController extends JeecgController<SurUserResult, ISurUserResultService> {
	@Autowired
	private ISurUserResultService surUserResultService;

	/**
	 * 分页列表查询
	 *
	 * @param surUserResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户所提交的问卷答案-分页列表查询")
	@ApiOperation(value="用户所提交的问卷答案-分页列表查询", notes="用户所提交的问卷答案-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurUserResult>> queryPageList(SurUserResult surUserResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurUserResult> queryWrapper = QueryGenerator.initQueryWrapper(surUserResult, req.getParameterMap());
		Page<SurUserResult> page = new Page<SurUserResult>(pageNo, pageSize);
		IPage<SurUserResult> pageList = surUserResultService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surUserResult
	 * @return
	 */
	@AutoLog(value = "用户所提交的问卷答案-添加")
	@ApiOperation(value="用户所提交的问卷答案-添加", notes="用户所提交的问卷答案-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurUserResult surUserResult) {
		surUserResultService.save(surUserResult);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surUserResult
	 * @return
	 */
	@AutoLog(value = "用户所提交的问卷答案-编辑")
	@ApiOperation(value="用户所提交的问卷答案-编辑", notes="用户所提交的问卷答案-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurUserResult surUserResult) {
		surUserResultService.updateById(surUserResult);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户所提交的问卷答案-通过id删除")
	@ApiOperation(value="用户所提交的问卷答案-通过id删除", notes="用户所提交的问卷答案-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surUserResultService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户所提交的问卷答案-批量删除")
	@ApiOperation(value="用户所提交的问卷答案-批量删除", notes="用户所提交的问卷答案-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surUserResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户所提交的问卷答案-通过id查询")
	@ApiOperation(value="用户所提交的问卷答案-通过id查询", notes="用户所提交的问卷答案-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurUserResult> queryById(@RequestParam(name="id",required=true) String id) {
		SurUserResult surUserResult = surUserResultService.getById(id);
		if(surUserResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surUserResult);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surUserResult
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurUserResult surUserResult) {
        return super.exportXls(request, surUserResult, SurUserResult.class, "用户所提交的问卷答案");
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
        return super.importExcel(request, response, SurUserResult.class);
    }

}

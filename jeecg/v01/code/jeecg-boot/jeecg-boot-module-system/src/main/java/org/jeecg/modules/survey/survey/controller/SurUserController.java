package org.jeecg.modules.survey.survey.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.survey.survey.req.PageReq;
import org.jeecg.modules.survey.survey.service.ISurUserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 保存填写问卷的用户表
 * @Author: jeecg-boot
 * @Date:   2022-07-12
 * @Version: V1.0
 */
@Api(tags="保存填写问卷的用户表")
@RestController
@RequestMapping("/survey/surUser")
@Slf4j
public class SurUserController extends JeecgController<SurUser, ISurUserService> {
	@Autowired
	private ISurUserService surUserService;


	//根据部门id查询用户
	 @ApiOperation(value="根据部门id查询用户", notes="根据部门id查询用户")
	 @PostMapping(value = "/queryByDep")
	 public Result<Page<SysUser>> queryUserByDep(@RequestBody PageReq req) {
		 Page<SysUser> userPage = surUserService.getUserListByDep(req);
		 return Result.OK(userPage);
	 }
	/**
	 * 分页列表查询
	 *
	 * @param surUser
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "保存填写问卷的用户表-分页列表查询")
	@ApiOperation(value="保存填写问卷的用户表-分页列表查询", notes="保存填写问卷的用户表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SurUser>> queryPageList(SurUser surUser,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurUser> queryWrapper = QueryGenerator.initQueryWrapper(surUser, req.getParameterMap());
		Page<SurUser> page = new Page<SurUser>(pageNo, pageSize);
		IPage<SurUser> pageList = surUserService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param surUser
	 * @return
	 */
	@AutoLog(value = "保存填写问卷的用户表-添加")
	@ApiOperation(value="保存填写问卷的用户表-添加", notes="保存填写问卷的用户表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurUser surUser) {
		surUserService.save(surUser);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surUser
	 * @return
	 */
	@AutoLog(value = "保存填写问卷的用户表-编辑")
	@ApiOperation(value="保存填写问卷的用户表-编辑", notes="保存填写问卷的用户表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurUser surUser) {
		surUserService.updateById(surUser);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "保存填写问卷的用户表-通过id删除")
	@ApiOperation(value="保存填写问卷的用户表-通过id删除", notes="保存填写问卷的用户表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surUserService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "保存填写问卷的用户表-批量删除")
	@ApiOperation(value="保存填写问卷的用户表-批量删除", notes="保存填写问卷的用户表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surUserService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "保存填写问卷的用户表-通过id查询")
	@ApiOperation(value="保存填写问卷的用户表-通过id查询", notes="保存填写问卷的用户表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurUser> queryById(@RequestParam(name="id",required=true) String id) {
		SurUser surUser = surUserService.getById(id);
		if(surUser==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surUser);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surUser
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurUser surUser) {
        return super.exportXls(request, surUser, SurUser.class, "保存填写问卷的用户表");
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
        return super.importExcel(request, response, SurUser.class);
    }

}

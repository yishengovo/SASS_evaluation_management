package org.jeecg.modules.survey.survey.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.survey.entity.SurResult;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.survey.survey.req.SaveSurResultReq;
import org.jeecg.modules.survey.survey.service.ISurResultService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.survey.survey.service.ISurUserService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: sur_result
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Api(tags="sur_result")
@RestController
@RequestMapping("/survey/surResult")
@Slf4j
public class SurResultController extends JeecgController<SurResult, ISurResultService> {
	@Autowired
	private ISurResultService surResultService;
	@Autowired
	private ISurUserService userService;

	 @ApiOperation(value="sur_result-保存用户填写的问卷答案", notes="sur_result-保存用户填写的问卷答案")
	 @PostMapping(value = "/saveAnswer")
	 @Transactional
	 public Result<String> saveResult(@RequestBody SaveSurResultReq req) {
		 if (surResultService.saveSurResult(req)){
			 return Result.OK("保存成功！");
		 }
		 return Result.error(500,"该用户已提交过！");

	 }
	/**
	 * 分页列表查询
	 *
	 * @param surResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sur_result-分页列表查询")
	@ApiOperation(value="sur_result-分页列表查询", notes="sur_result-分页列表查询")
	@GetMapping(value = "/list")
	public Result<JSONObject> queryPageList(SurResult surResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SurResult> queryWrapper = QueryGenerator.initQueryWrapper(surResult, req.getParameterMap());
		Page<SurResult> page = new Page<SurResult>(pageNo, pageSize);
		IPage<SurResult> pageList = surResultService.page(page, queryWrapper);
		//构造一个map用于返回
		List<SurResult> resultList = pageList.getRecords();
		List<Map<String, Object>> resulArray = new ArrayList<>();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("total",pageList.getTotal());
		jsonObject.put("pages",pageList.getPages());
		jsonObject.put("size",pageList.getSize());
		for (SurResult result : resultList) {
			//将result对象转换为map
			Map<String, Object> finalResult;
			finalResult = JSON.parseObject(JSON.toJSONString(result), new TypeReference<Map<String, Object>>() {
			});
			//查询出用户信息
			if (result.getUserUid()!=null){
				SurUser user = userService.getById(result.getUserUid());
				finalResult.put("name",user.getName());
				finalResult.put("gender",user.getGender());
				finalResult.put("age",user.getAge());
				finalResult.put("phone",user.getPhone());
				finalResult.put("tenantUid",user.getTenantUid());
			}
			//将map放入数组中
			resulArray.add(finalResult);
		}
		jsonObject.put("records",resulArray);
		return Result.OK(jsonObject);
	}

	/**
	 *   添加
	 *
	 * @param surResult
	 * @return
	 */
	@AutoLog(value = "sur_result-添加")
	@ApiOperation(value="sur_result-添加", notes="sur_result-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SurResult surResult) {
		surResultService.save(surResult);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param surResult
	 * @return
	 */
	@AutoLog(value = "sur_result-编辑")
	@ApiOperation(value="sur_result-编辑", notes="sur_result-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SurResult surResult) {
		surResultService.updateById(surResult);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sur_result-通过id删除")
	@ApiOperation(value="sur_result-通过id删除", notes="sur_result-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		surResultService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sur_result-批量删除")
	@ApiOperation(value="sur_result-批量删除", notes="sur_result-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.surResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sur_result-通过id查询")
	@ApiOperation(value="sur_result-通过id查询", notes="sur_result-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SurResult> queryById(@RequestParam(name="id",required=true) String id) {
		SurResult surResult = surResultService.getById(id);
		if(surResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(surResult);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param surResult
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SurResult surResult) {
        return super.exportXls(request, surResult, SurResult.class, "sur_result");
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
        return super.importExcel(request, response, SurResult.class);
    }

}

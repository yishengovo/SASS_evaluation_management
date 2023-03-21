package org.jeecg.modules.survey.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.survey.client.entity.UserPersonResult;
import org.jeecg.modules.survey.client.service.IUserPersonResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 用户端填写人每题的答案表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Api(tags = "用户端填写人每题的答案表")
@RestController
@RequestMapping("/client/userPersonResult")
@Slf4j
public class UserPersonResultController
    extends JeecgController<UserPersonResult, IUserPersonResultService> {
  @Autowired private IUserPersonResultService userPersonResultService;

  /**
   * 分页列表查询
   *
   * @param userPersonResult
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "用户端填写人每题的答案表-分页列表查询")
  @ApiOperation(value = "用户端填写人每题的答案表-分页列表查询", notes = "用户端填写人每题的答案表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<UserPersonResult>> queryPageList(
      UserPersonResult userPersonResult,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<UserPersonResult> queryWrapper =
        QueryGenerator.initQueryWrapper(userPersonResult, req.getParameterMap());
    Page<UserPersonResult> page = new Page<UserPersonResult>(pageNo, pageSize);
    IPage<UserPersonResult> pageList = userPersonResultService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param userPersonResult
   * @return
   */
  @AutoLog(value = "用户端填写人每题的答案表-添加")
  @ApiOperation(value = "用户端填写人每题的答案表-添加", notes = "用户端填写人每题的答案表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody UserPersonResult userPersonResult) {
    userPersonResultService.save(userPersonResult);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param userPersonResult
   * @return
   */
  @AutoLog(value = "用户端填写人每题的答案表-编辑")
  @ApiOperation(value = "用户端填写人每题的答案表-编辑", notes = "用户端填写人每题的答案表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody UserPersonResult userPersonResult) {
    userPersonResultService.updateById(userPersonResult);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "用户端填写人每题的答案表-通过id删除")
  @ApiOperation(value = "用户端填写人每题的答案表-通过id删除", notes = "用户端填写人每题的答案表-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    userPersonResultService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "用户端填写人每题的答案表-批量删除")
  @ApiOperation(value = "用户端填写人每题的答案表-批量删除", notes = "用户端填写人每题的答案表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.userPersonResultService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "用户端填写人每题的答案表-通过id查询")
  @ApiOperation(value = "用户端填写人每题的答案表-通过id查询", notes = "用户端填写人每题的答案表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<UserPersonResult> queryById(@RequestParam(name = "id", required = true) String id) {
    UserPersonResult userPersonResult = userPersonResultService.getById(id);
    if (userPersonResult == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(userPersonResult);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param userPersonResult
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, UserPersonResult userPersonResult) {
    return super.exportXls(request, userPersonResult, UserPersonResult.class, "用户端填写人每题的答案表");
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
    return super.importExcel(request, response, UserPersonResult.class);
  }
}

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
import org.jeecg.modules.survey.client.entity.UserSurvey;
import org.jeecg.modules.survey.client.service.IUserSurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 用户端项目问卷表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Api(tags = "用户端项目问卷表")
@RestController
@RequestMapping("/client/userSurvey")
@Slf4j
public class UserSurveyController extends JeecgController<UserSurvey, IUserSurveyService> {
  @Autowired private IUserSurveyService userSurveyService;

  /**
   * 分页列表查询
   *
   * @param userSurvey
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "用户端项目问卷表-分页列表查询")
  @ApiOperation(value = "用户端项目问卷表-分页列表查询", notes = "用户端项目问卷表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<UserSurvey>> queryPageList(
      UserSurvey userSurvey,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<UserSurvey> queryWrapper =
        QueryGenerator.initQueryWrapper(userSurvey, req.getParameterMap());
    Page<UserSurvey> page = new Page<UserSurvey>(pageNo, pageSize);
    IPage<UserSurvey> pageList = userSurveyService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param userSurvey
   * @return
   */
  @AutoLog(value = "用户端项目问卷表-添加")
  @ApiOperation(value = "用户端项目问卷表-添加", notes = "用户端项目问卷表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody UserSurvey userSurvey) {
    userSurveyService.save(userSurvey);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param userSurvey
   * @return
   */
  @AutoLog(value = "用户端项目问卷表-编辑")
  @ApiOperation(value = "用户端项目问卷表-编辑", notes = "用户端项目问卷表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody UserSurvey userSurvey) {
    userSurveyService.updateById(userSurvey);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "用户端项目问卷表-通过id删除")
  @ApiOperation(value = "用户端项目问卷表-通过id删除", notes = "用户端项目问卷表-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    userSurveyService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "用户端项目问卷表-批量删除")
  @ApiOperation(value = "用户端项目问卷表-批量删除", notes = "用户端项目问卷表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.userSurveyService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "用户端项目问卷表-通过id查询")
  @ApiOperation(value = "用户端项目问卷表-通过id查询", notes = "用户端项目问卷表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<UserSurvey> queryById(@RequestParam(name = "id", required = true) String id) {
    UserSurvey userSurvey = userSurveyService.getById(id);
    if (userSurvey == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(userSurvey);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param userSurvey
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, UserSurvey userSurvey) {
    return super.exportXls(request, userSurvey, UserSurvey.class, "用户端项目问卷表");
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
    return super.importExcel(request, response, UserSurvey.class);
  }
}

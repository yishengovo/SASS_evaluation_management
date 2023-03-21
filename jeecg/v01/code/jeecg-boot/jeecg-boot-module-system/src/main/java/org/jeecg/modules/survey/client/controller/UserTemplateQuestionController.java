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
import org.jeecg.modules.survey.client.entity.UserTemplateQuestion;
import org.jeecg.modules.survey.client.service.IUserTemplateQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 用户端问卷模板问题表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Api(tags = "用户端问卷模板问题表")
@RestController
@RequestMapping("/client/userTemplateQuestion")
@Slf4j
public class UserTemplateQuestionController
    extends JeecgController<UserTemplateQuestion, IUserTemplateQuestionService> {
  @Autowired private IUserTemplateQuestionService userTemplateQuestionService;

  /**
   * 分页列表查询
   *
   * @param userTemplateQuestion
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "用户端问卷模板问题表-分页列表查询")
  @ApiOperation(value = "用户端问卷模板问题表-分页列表查询", notes = "用户端问卷模板问题表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<UserTemplateQuestion>> queryPageList(
      UserTemplateQuestion userTemplateQuestion,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<UserTemplateQuestion> queryWrapper =
        QueryGenerator.initQueryWrapper(userTemplateQuestion, req.getParameterMap());
    Page<UserTemplateQuestion> page = new Page<UserTemplateQuestion>(pageNo, pageSize);
    IPage<UserTemplateQuestion> pageList = userTemplateQuestionService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param userTemplateQuestion
   * @return
   */
  @AutoLog(value = "用户端问卷模板问题表-添加")
  @ApiOperation(value = "用户端问卷模板问题表-添加", notes = "用户端问卷模板问题表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody UserTemplateQuestion userTemplateQuestion) {
    userTemplateQuestionService.save(userTemplateQuestion);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param userTemplateQuestion
   * @return
   */
  @AutoLog(value = "用户端问卷模板问题表-编辑")
  @ApiOperation(value = "用户端问卷模板问题表-编辑", notes = "用户端问卷模板问题表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody UserTemplateQuestion userTemplateQuestion) {
    userTemplateQuestionService.updateById(userTemplateQuestion);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "用户端问卷模板问题表-通过id删除")
  @ApiOperation(value = "用户端问卷模板问题表-通过id删除", notes = "用户端问卷模板问题表-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    userTemplateQuestionService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "用户端问卷模板问题表-批量删除")
  @ApiOperation(value = "用户端问卷模板问题表-批量删除", notes = "用户端问卷模板问题表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.userTemplateQuestionService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "用户端问卷模板问题表-通过id查询")
  @ApiOperation(value = "用户端问卷模板问题表-通过id查询", notes = "用户端问卷模板问题表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<UserTemplateQuestion> queryById(
      @RequestParam(name = "id", required = true) String id) {
    UserTemplateQuestion userTemplateQuestion = userTemplateQuestionService.getById(id);
    if (userTemplateQuestion == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(userTemplateQuestion);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param userTemplateQuestion
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(
      HttpServletRequest request, UserTemplateQuestion userTemplateQuestion) {
    return super.exportXls(request, userTemplateQuestion, UserTemplateQuestion.class, "用户端问卷模板问题表");
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
    return super.importExcel(request, response, UserTemplateQuestion.class);
  }
}

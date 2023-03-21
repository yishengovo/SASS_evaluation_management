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
import org.jeecg.modules.survey.client.entity.UserTemplate;
import org.jeecg.modules.survey.client.service.IUserTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 用户端问卷模板表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
@Api(tags = "用户端问卷模板表")
@RestController
@RequestMapping("/client/userTemplate")
@Slf4j
public class UserTemplateController extends JeecgController<UserTemplate, IUserTemplateService> {
  @Autowired private IUserTemplateService userTemplateService;

  /**
   * 分页列表查询
   *
   * @param userTemplate
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "用户端问卷模板表-分页列表查询")
  @ApiOperation(value = "用户端问卷模板表-分页列表查询", notes = "用户端问卷模板表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<UserTemplate>> queryPageList(
      UserTemplate userTemplate,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<UserTemplate> queryWrapper =
        QueryGenerator.initQueryWrapper(userTemplate, req.getParameterMap());
    Page<UserTemplate> page = new Page<UserTemplate>(pageNo, pageSize);
    IPage<UserTemplate> pageList = userTemplateService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param userTemplate
   * @return
   */
  @AutoLog(value = "用户端问卷模板表-添加")
  @ApiOperation(value = "用户端问卷模板表-添加", notes = "用户端问卷模板表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody UserTemplate userTemplate) {
    userTemplateService.save(userTemplate);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param userTemplate
   * @return
   */
  @AutoLog(value = "用户端问卷模板表-编辑")
  @ApiOperation(value = "用户端问卷模板表-编辑", notes = "用户端问卷模板表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody UserTemplate userTemplate) {
    userTemplateService.updateById(userTemplate);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "用户端问卷模板表-通过id删除")
  @ApiOperation(value = "用户端问卷模板表-通过id删除", notes = "用户端问卷模板表-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    userTemplateService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "用户端问卷模板表-批量删除")
  @ApiOperation(value = "用户端问卷模板表-批量删除", notes = "用户端问卷模板表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.userTemplateService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "用户端问卷模板表-通过id查询")
  @ApiOperation(value = "用户端问卷模板表-通过id查询", notes = "用户端问卷模板表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<UserTemplate> queryById(@RequestParam(name = "id", required = true) String id) {
    UserTemplate userTemplate = userTemplateService.getById(id);
    if (userTemplate == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(userTemplate);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param userTemplate
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, UserTemplate userTemplate) {
    return super.exportXls(request, userTemplate, UserTemplate.class, "用户端问卷模板表");
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
    return super.importExcel(request, response, UserTemplate.class);
  }
}

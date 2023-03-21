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
import org.jeecg.modules.survey.client.entity.UserEvaluationRelationship;
import org.jeecg.modules.survey.client.service.IUserEvaluationRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 360度项目中的被评价人表 @Author: jeecg-boot @Date: 2022-10-09 @Version: V1.0
 */
@Api(tags = "360度项目中的被评价人表")
@RestController
@RequestMapping("/client/userEvaluationRelationship")
@Slf4j
public class UserEvaluationRelationshipController
    extends JeecgController<UserEvaluationRelationship, IUserEvaluationRelationshipService> {
  @Autowired private IUserEvaluationRelationshipService userEvaluationRelationshipService;

  /**
   * 分页列表查询
   *
   * @param userEvaluationRelationship
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "360度项目中的被评价人表-分页列表查询")
  @ApiOperation(value = "360度项目中的被评价人表-分页列表查询", notes = "360度项目中的被评价人表-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<UserEvaluationRelationship>> queryPageList(
      UserEvaluationRelationship userEvaluationRelationship,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<UserEvaluationRelationship> queryWrapper =
        QueryGenerator.initQueryWrapper(userEvaluationRelationship, req.getParameterMap());
    Page<UserEvaluationRelationship> page = new Page<UserEvaluationRelationship>(pageNo, pageSize);
    IPage<UserEvaluationRelationship> pageList =
        userEvaluationRelationshipService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 添加
   *
   * @param userEvaluationRelationship
   * @return
   */
  @AutoLog(value = "360度项目中的被评价人表-添加")
  @ApiOperation(value = "360度项目中的被评价人表-添加", notes = "360度项目中的被评价人表-添加")
  @PostMapping(value = "/add")
  public Result<String> add(@RequestBody UserEvaluationRelationship userEvaluationRelationship) {
    userEvaluationRelationshipService.save(userEvaluationRelationship);
    return Result.OK("添加成功！");
  }

  /**
   * 编辑
   *
   * @param userEvaluationRelationship
   * @return
   */
  @AutoLog(value = "360度项目中的被评价人表-编辑")
  @ApiOperation(value = "360度项目中的被评价人表-编辑", notes = "360度项目中的被评价人表-编辑")
  @RequestMapping(
      value = "/edit",
      method = {RequestMethod.PUT, RequestMethod.POST})
  public Result<String> edit(@RequestBody UserEvaluationRelationship userEvaluationRelationship) {
    userEvaluationRelationshipService.updateById(userEvaluationRelationship);
    return Result.OK("编辑成功!");
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "360度项目中的被评价人表-通过id删除")
  @ApiOperation(value = "360度项目中的被评价人表-通过id删除", notes = "360度项目中的被评价人表-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    userEvaluationRelationshipService.removeById(id);
    return Result.OK("删除成功!");
  }

  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "360度项目中的被评价人表-批量删除")
  @ApiOperation(value = "360度项目中的被评价人表-批量删除", notes = "360度项目中的被评价人表-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.userEvaluationRelationshipService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "360度项目中的被评价人表-通过id查询")
  @ApiOperation(value = "360度项目中的被评价人表-通过id查询", notes = "360度项目中的被评价人表-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<UserEvaluationRelationship> queryById(
      @RequestParam(name = "id", required = true) String id) {
    UserEvaluationRelationship userEvaluationRelationship =
        userEvaluationRelationshipService.getById(id);
    if (userEvaluationRelationship == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(userEvaluationRelationship);
  }

  /**
   * 导出excel
   *
   * @param request
   * @param userEvaluationRelationship
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(
      HttpServletRequest request, UserEvaluationRelationship userEvaluationRelationship) {
    return super.exportXls(
        request, userEvaluationRelationship, UserEvaluationRelationship.class, "360度项目中的被评价人表");
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
    return super.importExcel(request, response, UserEvaluationRelationship.class);
  }
}

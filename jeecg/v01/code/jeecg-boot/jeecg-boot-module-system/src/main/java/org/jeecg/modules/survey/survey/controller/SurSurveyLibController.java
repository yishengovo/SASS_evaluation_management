package org.jeecg.modules.survey.survey.controller;

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
import org.jeecg.modules.survey.survey.entity.SurSurveyLib;
import org.jeecg.modules.survey.survey.req.SaveLibJsonReq;
import org.jeecg.modules.survey.survey.service.ISurSurveyLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
* @Description: 问卷库
* @Author: jeecg-boot
* @Date:   2022-08-06
* @Version: V1.0
*/
@Api(tags="问卷库")
@RestController
@RequestMapping("/survey/surSurveyLib")
@Slf4j
public class SurSurveyLibController extends JeecgController<SurSurveyLib, ISurSurveyLibService> {
   @Autowired
   private ISurSurveyLibService surSurveyLibService;


    /**
     *   保存模板问卷json
     *
     * @param req
     * @return
     */
    @AutoLog(value = "问卷库保存json")
    @ApiOperation(value="问卷库保存json", notes="问卷库保存json")
    @PostMapping(value = "/saveJson")
    public Result<String> saveJson(@RequestBody SaveLibJsonReq req) {
        //根据id查询问卷库
        SurSurveyLib surveyLib = surSurveyLibService.getById(req.getId());
        surveyLib.setJson(req.getJson());
        surSurveyLibService.updateById(surveyLib);
        return Result.OK("添加成功！");
    }


   /**
    * 分页列表查询
    *
    * @param surSurveyLib
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "问卷库-分页列表查询")
   @ApiOperation(value="问卷库-分页列表查询", notes="问卷库-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SurSurveyLib>> queryPageList(SurSurveyLib surSurveyLib,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<SurSurveyLib> queryWrapper = QueryGenerator.initQueryWrapper(surSurveyLib, req.getParameterMap());
       Page<SurSurveyLib> page = new Page<SurSurveyLib>(pageNo, pageSize);
       IPage<SurSurveyLib> pageList = surSurveyLibService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param surSurveyLib
    * @return
    */
   @AutoLog(value = "问卷库-添加")
   @ApiOperation(value="问卷库-添加", notes="问卷库-添加")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SurSurveyLib surSurveyLib) {
       surSurveyLibService.save(surSurveyLib);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param surSurveyLib
    * @return
    */
   @AutoLog(value = "问卷库-编辑")
   @ApiOperation(value="问卷库-编辑", notes="问卷库-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SurSurveyLib surSurveyLib) {
       surSurveyLibService.updateById(surSurveyLib);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "问卷库-通过id删除")
   @ApiOperation(value="问卷库-通过id删除", notes="问卷库-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       surSurveyLibService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "问卷库-批量删除")
   @ApiOperation(value="问卷库-批量删除", notes="问卷库-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.surSurveyLibService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "问卷库-通过id查询")
   @ApiOperation(value="问卷库-通过id查询", notes="问卷库-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SurSurveyLib> queryById(@RequestParam(name="id",required=true) String id) {
       SurSurveyLib surSurveyLib = surSurveyLibService.getById(id);
       if(surSurveyLib==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(surSurveyLib);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param surSurveyLib
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SurSurveyLib surSurveyLib) {
       return super.exportXls(request, surSurveyLib, SurSurveyLib.class, "问卷库");
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
       return super.importExcel(request, response, SurSurveyLib.class);
   }

}

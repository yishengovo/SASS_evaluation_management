package org.jeecg.modules.survey.survey.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.modules.survey.survey.entity.SurEvaluationWeight;
import org.jeecg.modules.survey.survey.service.ISurEvaluationWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
* @Description: 评价关系的权重关系表
* @Author: jeecg-boot
* @Date:   2022-08-13
* @Version: V1.0
*/
@Api(tags="评价关系的权重关系表")
@RestController
@RequestMapping("/survey/surEvaluationWeight")
@Slf4j
public class SurEvaluationWeightController extends JeecgController<SurEvaluationWeight, ISurEvaluationWeightService> {
   @Autowired
   private ISurEvaluationWeightService surEvaluationWeightService;

   /**
    * 分页列表查询
    *
    * @param surEvaluationWeight
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "评价关系的权重关系表-分页列表查询")
   @ApiOperation(value="评价关系的权重关系表-分页列表查询", notes="评价关系的权重关系表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SurEvaluationWeight>> queryPageList(SurEvaluationWeight surEvaluationWeight,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<SurEvaluationWeight> queryWrapper = QueryGenerator.initQueryWrapper(surEvaluationWeight, req.getParameterMap());
       Page<SurEvaluationWeight> page = new Page<SurEvaluationWeight>(pageNo, pageSize);
       IPage<SurEvaluationWeight> pageList = surEvaluationWeightService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param surEvaluationWeight
    * @return
    */
   @AutoLog(value = "评价关系的权重关系表-设置评价体系的权重")
   @ApiOperation(value="评价关系的权重关系表-设置评价体系的权重", notes="评价关系的权重关系表-设置评价体系的权重")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SurEvaluationWeight surEvaluationWeight) {
       //首先删除该评价体系下的所有权重关系 再插入新的
       surEvaluationWeightService.remove(new LambdaQueryWrapper<SurEvaluationWeight>().eq(SurEvaluationWeight::getProjectId,surEvaluationWeight.getProjectId()));
       surEvaluationWeightService.save(surEvaluationWeight);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param surEvaluationWeight
    * @return
    */
   @AutoLog(value = "评价关系的权重关系表-编辑")
   @ApiOperation(value="评价关系的权重关系表-编辑", notes="评价关系的权重关系表-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SurEvaluationWeight surEvaluationWeight) {
       surEvaluationWeightService.updateById(surEvaluationWeight);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "评价关系的权重关系表-通过id删除")
   @ApiOperation(value="评价关系的权重关系表-通过id删除", notes="评价关系的权重关系表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       surEvaluationWeightService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "评价关系的权重关系表-批量删除")
   @ApiOperation(value="评价关系的权重关系表-批量删除", notes="评价关系的权重关系表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.surEvaluationWeightService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "评价关系的权重关系表-通过id查询")
   @ApiOperation(value="评价关系的权重关系表-通过id查询", notes="评价关系的权重关系表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SurEvaluationWeight> queryById(@RequestParam(name="id",required=true) String id) {
       SurEvaluationWeight surEvaluationWeight = surEvaluationWeightService.getById(id);
       if(surEvaluationWeight==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(surEvaluationWeight);
   }


    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "评价关系的权重关系表-通过id查询")
    @ApiOperation(value="评价关系的权重关系表-通过项目id查询", notes="评价关系的权重关系表-通过项目id查询")
    @GetMapping(value = "/queryByProject/{id}")
    public Result<SurEvaluationWeight> queryByProjectId(@PathVariable("id") String id) {
        SurEvaluationWeight surEvaluationWeight = surEvaluationWeightService.getOne(new LambdaQueryWrapper<SurEvaluationWeight>().eq(SurEvaluationWeight::getProjectId,id));
        if(surEvaluationWeight==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(surEvaluationWeight);
    }
   /**
   * 导出excel
   *
   * @param request
   * @param surEvaluationWeight
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SurEvaluationWeight surEvaluationWeight) {
       return super.exportXls(request, surEvaluationWeight, SurEvaluationWeight.class, "评价关系的权重关系表");
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
       return super.importExcel(request, response, SurEvaluationWeight.class);
   }

}

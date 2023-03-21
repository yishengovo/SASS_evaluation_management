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
import org.jeecg.modules.survey.survey.dto.*;
import org.jeecg.modules.survey.survey.entity.SurEvaluationRelationship;
import org.jeecg.modules.survey.survey.req.*;
import org.jeecg.modules.survey.survey.resp.R;
import org.jeecg.modules.survey.survey.service.ISurEvaluationRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: 评价关系表
* @Author: jeecg-boot
* @Date:   2022-08-05
* @Version: V1.0
*/
@Api(tags="评价关系表")
@RestController
@RequestMapping("/survey/surEvaluationRelationship")
@Slf4j
public class SurEvaluationRelationshipController extends JeecgController<SurEvaluationRelationship, ISurEvaluationRelationshipService> {
   @Autowired
   private ISurEvaluationRelationshipService surEvaluationRelationshipService;



    /**
     *   根据360度项目id查询被评价人
     * @return
     */
    @AutoLog(value = "根据360度项目id查询被评价人")
    @ApiOperation(value="根据360度项目id查询被评价人", notes="根据360度项目id查询被评价人")
    @GetMapping(value = "/getEvaluatedInfo/{id}")
    public R getEvaluatedInfo(@PathVariable("id") String id) {
        List<ReportDto> list = surEvaluationRelationshipService.getEvaluated(id);
        return R.ok().put("data", list);
    }

    /**
     *   查询360度项目列表
     * @return
     */
    @AutoLog(value = "查询360度项目列表")
    @ApiOperation(value="查询360度项目列表", notes="查询360度项目列表")
    @GetMapping(value = "/360List")
    public R get360Project() {
        List<ReportDto> list = surEvaluationRelationshipService.get360Project();
        return R.ok().put("data", list);
    }

    /**
     *   查询题目得分结果
     *
     * @param req
     * @return
     */
    @AutoLog(value = "查询题目得分结果")
    @ApiOperation(value="查询题目得分结果", notes="查询题目得分结果")
    @GetMapping(value = "/getQuestionScore")
    public R getQuestionScore(EvaluatedInfoReq req) {
        List<QuestionScoreDto> questionScore = surEvaluationRelationshipService.getQuestionScore(req);
        return R.ok().put("data", questionScore);
    }

    /**
     *   查询被评价人的同事、上级等得分信息
     *
     * @param req
     * @return
     */
    @AutoLog(value = "查询被评价人的同事、上级等得分信息")
    @ApiOperation(value="查询被评价人的同事、上级等得分信息", notes="查询被评价人的同事、上级等得分信息")
    @GetMapping(value = "/getLevelScore")
    public R getLevelInfo(EvaluatedInfoReq req) {
        Map<String, Object> othersScore = surEvaluationRelationshipService.getOthersScore(req);
        List<Object> list = new ArrayList<>();
        list.add(othersScore);
        return R.ok().put("data", list);
    }

    /**
     *   查询被评价人的得分信息
     *
     * @param req
     * @return
     */
    @AutoLog(value = "查询被评价人的得分信息")
    @ApiOperation(value="查询被评价人的得分信息", notes="查询被评价人的得分信息")
    @GetMapping(value = "/getEvaluatedScore")
    public R getEvaluatedInfo(EvaluatedInfoReq req) {
        EvaluatedScoreDto evaluatedScore = surEvaluationRelationshipService.getEvaluatedScore(req);
        List<EvaluatedScoreDto> list = new ArrayList<>();
        list.add(evaluatedScore);
        return R.ok().put("data", list);
    }

   /**
    * 分页列表查询
    *
    * @param surEvaluationRelationship
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "评价关系表-分页列表查询")
   @ApiOperation(value="评价关系表-分页列表查询", notes="评价关系表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SurEvaluationRelationship>> queryPageList(SurEvaluationRelationship surEvaluationRelationship,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<SurEvaluationRelationship> queryWrapper = QueryGenerator.initQueryWrapper(surEvaluationRelationship, req.getParameterMap());
       Page<SurEvaluationRelationship> page = new Page<SurEvaluationRelationship>(pageNo, pageSize);
       IPage<SurEvaluationRelationship> pageList = surEvaluationRelationshipService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param surEvaluationRelationship
    * @return
    */
   @AutoLog(value = "评价关系表-添加")
   @ApiOperation(value="评价关系表-添加", notes="评价关系表-添加")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SurEvaluationRelationship surEvaluationRelationship) {
       surEvaluationRelationshipService.save(surEvaluationRelationship);
       return Result.OK("添加成功！");
   }



   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "评价关系表-通过id删除")
   @ApiOperation(value="评价关系表-通过id删除", notes="评价关系表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       surEvaluationRelationshipService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "评价关系表-批量删除")
   @ApiOperation(value="评价关系表-批量删除", notes="评价关系表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.surEvaluationRelationshipService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "评价关系表-通过id查询")
   @ApiOperation(value="评价关系表-通过id查询", notes="评价关系表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SurEvaluationRelationship> queryById(@RequestParam(name="id",required=true) String id) {
       SurEvaluationRelationship surEvaluationRelationship = surEvaluationRelationshipService.getById(id);
       if(surEvaluationRelationship==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(surEvaluationRelationship);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param surEvaluationRelationship
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SurEvaluationRelationship surEvaluationRelationship) {
       return super.exportXls(request, surEvaluationRelationship, SurEvaluationRelationship.class, "评价关系表");
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
       return super.importExcel(request, response, SurEvaluationRelationship.class);
   }


   //设置评价关系
    @AutoLog(value = "评价关系表-设置评价关系")
    @ApiOperation(value="评价关系表-设置评价关系", notes="评价关系表-设置评价关系")
    @PostMapping(value = "/setEvaluationRelationship")
    public Result<String> setEvaluationRelationship(@RequestBody EvaluationSetReq req) {
        if (surEvaluationRelationshipService.setEvaluationRelationship(req)) {
            return Result.OK("设置成功！");
        }
        return Result.error("设置失败！");
    }

    //设置评价关系
    @AutoLog(value = "评价关系表-设置有上下级的评价关系")
    @ApiOperation(value="评价关系表-设置有上下级的评价关系", notes="评价关系表-设置有上下级的评价关系")
    @PostMapping(value = "/setLevel")
    public Result<String> setLevel(@RequestBody EvaluationSetReq req) {
        if (surEvaluationRelationshipService.setLevelEvaluationRelationship(req)) {
            return Result.OK("设置成功！");
        }
        return Result.error("设置失败！");
    }

    /**
     *  重新设置评价关系
     *
     * @param req
     * @return
     */
    @AutoLog(value = "评价关系表- 重新设置评价关系")
    @ApiOperation(value="评价关系表-重新设置评价关系", notes="评价关系表-重新设置评价关系")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody EvaluationReq req) {
        if (surEvaluationRelationshipService.resetEvaluationRelationship(req.getList())) {
            return Result.OK("编辑成功!");
        }
        return Result.error("编辑失败!");
    }

    //根据项目id查询评价关系
    @AutoLog(value = "评价关系表-根据项目id查询评价关系")
    @ApiOperation(value="评价关系表-根据项目id查询评价关系", notes="评价关系表-根据项目id查询评价关系")
    @PostMapping(value = "/listByProject")
    public Result<?> queryList(@RequestBody PageReq req) {
        List<EvaluationRelationshipDto> evaluationRelationshipDtoList = surEvaluationRelationshipService.getEvaluationRelationship(req.getId());
        return Result.ok(evaluationRelationshipDtoList);
    }

    //根据项目id查询有上下级的评价关系
    @AutoLog(value = "评价关系表-根据项目id查询有上下级的评价关系")
    @ApiOperation(value="评价关系表-根据项目id查询有上下级的评价关系", notes="评价关系表-根据项目id查询有上下级的评价关系")
    @PostMapping(value = "/levelByProject")
    public Result<?> queryLevelList(@RequestBody PageReq req) {
        List<EvaluationRelationshipLevelDto> evaluationRelationshipLevel = surEvaluationRelationshipService.getEvaluationRelationshipLevel(req.getId());
        return Result.ok(evaluationRelationshipLevel);
    }

}

package org.jeecg.modules.survey.survey.controller;

import com.alibaba.fastjson.JSON;
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
import org.jeecg.modules.survey.survey.entity.SurQuestion;
import org.jeecg.modules.survey.survey.entity.SurQuestionDimension;
import org.jeecg.modules.survey.survey.req.Dimension;
import org.jeecg.modules.survey.survey.req.DimensionSetReq;
import org.jeecg.modules.survey.survey.service.ISurQuestionDimensionService;
import org.jeecg.modules.survey.survey.service.ISurQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @Description: 问卷问题维度表
* @Author: jeecg-boot
* @Date:   2022-08-05
* @Version: V1.0
*/
@Api(tags="问卷问题维度表")
@RestController
@RequestMapping("/survey/surQuestionDimension")
@Slf4j
public class SurQuestionDimensionController extends JeecgController<SurQuestionDimension, ISurQuestionDimensionService> {
   @Autowired
   private ISurQuestionDimensionService surQuestionDimensionService;
    @Autowired
    private ISurQuestionService surQuestionService;



    @AutoLog(value = "问卷问题维度表-返回维度绑定的问题")
    @ApiOperation(value="问卷问题维度表-返回维度绑定的问题", notes="问卷问题维度表-返回问卷的维度数组")
    @PostMapping(value = "/queryBindQuestion/{surveyId}")
    public Result<?> queryBindQuestion(@PathVariable String surveyId) {
        List<Object> result = surQuestionDimensionService.queryBindQuestion(surveyId);
        return Result.OK(result);
    }

    //根据问卷id查询题目维度
    @AutoLog(value = "根据问卷id查询题目维度")
    @ApiOperation(value="根据问卷id查询题目维度", notes="根据问卷id查询题目维度")
    @GetMapping(value = "/dimensionList/{id}")
    public Result<List<SurQuestionDimension>> getQuestionDimensionList(@PathVariable String id) {
        List<SurQuestionDimension> questionDimensionList = surQuestionDimensionService.list(new LambdaQueryWrapper<SurQuestionDimension>().eq(SurQuestionDimension::getSurveyId, id));
        questionDimensionList.forEach(surQuestionDimension -> {
            //根据维度id查询题目
            List<SurQuestion> questionList = surQuestionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getDimensionId, surQuestionDimension.getId()));
            surQuestionDimension.setCount(questionList.size());
            surQuestionDimension.setQuestionList(questionList);
        });
        return Result.OK(questionDimensionList);
    }

    @AutoLog(value = "问卷问题维度表-返回问卷的维度数组")
    @ApiOperation(value="问卷问题维度表-返回问卷的维度数组", notes="问卷问题维度表-返回问卷的维度数组")
    @GetMapping(value = "/listBySurveyId/{surveyId}")
    public Result<?> queryPageList(@PathVariable("surveyId") String surveyId) {
        List<SurQuestionDimension> surQuestionDimensionList = surQuestionDimensionService.list(new LambdaQueryWrapper<SurQuestionDimension>().eq(SurQuestionDimension::getSurveyId, surveyId));
        return Result.OK(surQuestionDimensionList);
    }

   /**
    * 分页列表查询
    *
    * @param surQuestionDimension
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "问卷问题维度表-分页列表查询")
   @ApiOperation(value="问卷问题维度表-分页列表查询", notes="问卷问题维度表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SurQuestionDimension>> queryPageList(SurQuestionDimension surQuestionDimension,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<SurQuestionDimension> queryWrapper = QueryGenerator.initQueryWrapper(surQuestionDimension, req.getParameterMap());
       Page<SurQuestionDimension> page = new Page<SurQuestionDimension>(pageNo, pageSize);
       IPage<SurQuestionDimension> pageList = surQuestionDimensionService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param surQuestionDimension
    * @return
    */
   @AutoLog(value = "问卷问题维度表-添加")
   @ApiOperation(value="问卷问题维度表-添加", notes="问卷问题维度表-添加")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody SurQuestionDimension surQuestionDimension) {
       surQuestionDimensionService.save(surQuestionDimension);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param surQuestionDimension
    * @return
    */
   @AutoLog(value = "问卷问题维度表-编辑")
   @ApiOperation(value="问卷问题维度表-编辑", notes="问卷问题维度表-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SurQuestionDimension surQuestionDimension) {
       surQuestionDimensionService.updateById(surQuestionDimension);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "问卷问题维度表-通过id删除")
   @ApiOperation(value="问卷问题维度表-通过id删除", notes="问卷问题维度表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       //根据id查询是否有绑定的题目
       List<SurQuestion> questionList = surQuestionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getDimensionId, id));
       //如果不为空则 已经绑定了题目
       if (!questionList.isEmpty()){
           return Result.error(500,"此维度设置了题目，不能删除");
       }
       surQuestionDimensionService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "问卷问题维度表-批量删除")
   @ApiOperation(value="问卷问题维度表-批量删除", notes="问卷问题维度表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.surQuestionDimensionService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "问卷问题维度表-通过id查询")
   @ApiOperation(value="问卷问题维度表-通过id查询", notes="问卷问题维度表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SurQuestionDimension> queryById(@RequestParam(name="id",required=true) String id) {
       SurQuestionDimension surQuestionDimension = surQuestionDimensionService.getById(id);
       if(surQuestionDimension==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(surQuestionDimension);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param surQuestionDimension
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SurQuestionDimension surQuestionDimension) {
       return super.exportXls(request, surQuestionDimension, SurQuestionDimension.class, "问卷问题维度表");
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
       return super.importExcel(request, response, SurQuestionDimension.class);
   }

    /**
     * 设置题目维度的权重值
     *
     * @param req
     * @return
     */
    @PostMapping(value = "/setDimensionWeight")
    public Result<?> setDimensionWeight(@RequestBody DimensionSetReq req) {
        List<SurQuestionDimension> surQuestionDimensionList = req.getList();
        //遍历权重数组
        surQuestionDimensionList.forEach(surQuestionDimension -> {
            //根据id查询维度
            SurQuestionDimension questionDimension = surQuestionDimensionService.getById(surQuestionDimension.getId());
            questionDimension.setWeight(surQuestionDimension.getWeight());
            surQuestionDimensionService.updateById(questionDimension);
        });
        return Result.ok("保存成功！");
    }


    //给维度绑定题目
    @AutoLog(value = "给维度绑定题目")
    @ApiOperation(value="给维度绑定题目", notes="给维度绑定题目")
    @PostMapping(value = "/bindQuestion")
    @Transactional
    public Result<?> bindQuestion(@RequestBody Dimension req) {
        //查询出维度对象
        SurQuestionDimension dimension = surQuestionDimensionService.getById(req.getDimensionId());
        //设置维度的问题索引数组
        String rowKeysJson = JSON.toJSONString(req.getQuestionId());
        dimension.setRowKeys(rowKeysJson);
        surQuestionDimensionService.updateById(dimension);
        //查询出之前设置的维度
        List<SurQuestion> questionList = surQuestionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getDimensionId, req.getDimensionId()));
        //删除之前绑定的关系
        for (SurQuestion surQuestion : questionList) {
            surQuestion.setDimensionId(null);
            surQuestionService.updateById(surQuestion);
        }
        //再创建新的绑定关系
        List<SurQuestion> surQuestions = new ArrayList<>();
        //根据问题id查询问题
        for (String s : req.getQuestionId()) {
            SurQuestion surQuestion = surQuestionService.getById(s);
            surQuestions.add(surQuestion);
        }
            //给问题设置维度id
        for (SurQuestion surQuestion : surQuestions) {
            surQuestion.setDimensionId(req.getDimensionId());
            surQuestionService.updateById(surQuestion);
        }
        return Result.ok("操作成功！");
    }

}

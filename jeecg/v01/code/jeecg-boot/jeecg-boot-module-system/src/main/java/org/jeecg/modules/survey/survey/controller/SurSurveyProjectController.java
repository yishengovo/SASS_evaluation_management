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
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.req.*;
import org.jeecg.modules.survey.survey.service.ISurQuestionChoiceProjectService;
import org.jeecg.modules.survey.survey.service.ISurQuestionProjectService;
import org.jeecg.modules.survey.survey.service.ISurSurveyProjectService;
import org.springframework.beans.BeanUtils;
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
* @Description: 存放项目复制出来的问卷表
* @Author: jeecg-boot
* @Date:   2022-08-17
* @Version: V1.0
*/
@Api(tags="存放项目复制出来的问卷表")
@RestController
@RequestMapping("/survey/surSurveyProject")
@Slf4j
public class SurSurveyProjectController extends JeecgController<SurSurveyProject, ISurSurveyProjectService> {
   @Autowired
   private ISurSurveyProjectService surSurveyProjectService;
   @Autowired
   private ISurQuestionProjectService surQuestionProjectService;
   @Autowired
   private ISurQuestionChoiceProjectService surQuestionChoiceProjectService;


    //根据问卷ID查询问题和选项
    @AutoLog(value = "survey-根据问卷ID查询问题和选项")
    @ApiOperation(value="survey-根据问卷ID查询问题和选项", notes="survey-根据问卷ID查询问题和选项")
    @GetMapping(value = "/questionById")
    public Result<List<SurveyDto>> getQuestionAndChoice(@RequestParam(name="id",required=true) String id) {
        List<SurveyDto> result = surSurveyProjectService.getQuestionAndChoice(id);
        return Result.ok(result);
    }

    //根据选项ID赋分
    @AutoLog(value = "survey-根据选项ID赋分")
    @ApiOperation(value="survey-根据选项ID赋分", notes="survey-根据选项ID赋分")
    @PostMapping(value = "/setScore")
    @Transactional
    public Result<SurQuestionChoiceProject> getQuestionList(@RequestBody ScoreSetReq req) {
        //先根据选项id查询选项实体
        SurQuestionChoiceProject choice = surQuestionChoiceProjectService.getById(req.getId());
//		 String content = choice.getContent();
//		 List<String> choiceList = Arrays.asList(content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
        choice.setBasicScore(req.getScore().toString());
        //更新数据库
        surQuestionChoiceProjectService.updateById(choice);
        return Result.ok(choice);
    }

    //根据问卷ID查询问题列表
    @AutoLog(value = "survey-根据问题ID查询问题列表和分数")
    @ApiOperation(value="survey-根据问题ID查询选项列表和分数", notes="survey-根据问题ID查询问题列表和分数")
    @PostMapping(value = "/choice")
    @Transactional
    public Result<List<ChoiceDto>> getQuestionList(@RequestBody ChoiceByQuestionIdReq req) {
        SurQuestionChoiceProject choice = surQuestionChoiceProjectService.getOne(new LambdaQueryWrapper<SurQuestionChoiceProject>().eq(SurQuestionChoiceProject::getQuestionId, req.getQuestionId()));
        //获取选项数组
        String content = choice.getContent();
        List<String> choiceList = Arrays.asList(content.substring(1, content.length() - 1).replaceAll("\\s", "").split(","));
        //获取答案数组
        String basicScore = choice.getBasicScore();
        List<ChoiceDto> result = new ArrayList<>();
        if (basicScore == null){
            //如果选项为空
            if (choiceList.size() == 1){
                return Result.ok(null);
            }
            //如果选项不为空
            for (String s : choiceList) {
                ChoiceDto choiceDto = new ChoiceDto();
                BeanUtils.copyProperties(choice,choiceDto);
                choiceDto.setChoice(s);
                choiceDto.setBasicScore(0);
                result.add(choiceDto);
            }
            return Result.ok(result);
        }
        List<String> scoreList = Arrays.asList(basicScore.substring(1, basicScore.length() - 1).replaceAll("\\s", "").split(","));
        //将选项数组拆分成单个的
        if (choiceList.size()!= 0){
            for (int i = 0; i < choiceList.size(); i++) {
                if (scoreList.get(i) != null){
                    ChoiceDto choiceDto = new ChoiceDto();
                    BeanUtils.copyProperties(choice,choiceDto);
                    choiceDto.setChoice(choiceList.get(i));
                    choiceDto.setBasicScore(scoreList.get(i));
                    result.add(choiceDto);
                }
            }
        }
        else {
            return Result.ok(null);
        }
        return Result.ok(result);
    }

    //根据问题ID查询问题选项和分数
    @AutoLog(value = "survey-根据问卷ID查询问题列表")
    @ApiOperation(value="survey-根据问卷ID查询问题列表", notes="survey-根据问卷ID查询问题列表")
    @PostMapping(value = "/question")
    @Transactional
    public Result<?> getQuestionList(@RequestBody QuestionBySurveyIdReq req) {
        Page<SurQuestionProject> page = surQuestionProjectService.page(new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<SurQuestionProject>().eq(SurQuestionProject::getSurveyUid, req.getId()));
        return Result.ok(page);
    }
    //保存json
    @AutoLog(value = "survey-保存问卷json")
    @ApiOperation(value="survey-保存问卷json", notes="survey-保存问卷json")
    @PostMapping(value = "/saveJson")
    @Transactional
    public Result<String> saveJson(@RequestBody SaveJsonReq req) {
        if (surSurveyProjectService.saveJsonPreview(req)){
            return Result.OK("保存成功！");
        }
        return Result.error(500,"操作失败!");
    }

   /**
    * 分页列表查询
    *
    * @param surSurveyProject
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "存放项目复制出来的问卷表-分页列表查询")
   @ApiOperation(value="存放项目复制出来的问卷表-分页列表查询", notes="存放项目复制出来的问卷表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SurSurveyProject>> queryPageList(SurSurveyProject surSurveyProject,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<SurSurveyProject> queryWrapper = QueryGenerator.initQueryWrapper(surSurveyProject, req.getParameterMap());
       Page<SurSurveyProject> page = new Page<SurSurveyProject>(pageNo, pageSize);
       IPage<SurSurveyProject> pageList = surSurveyProjectService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param req
    * @return
    */
   @AutoLog(value = "存放项目复制出来的问卷表-新建空白问卷")
   @ApiOperation(value="存放项目复制出来的问卷表-新建空白问卷", notes="存放项目复制出来的问卷表-新建空白问卷")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody EmptySurveyReq req) {
       if (surSurveyProjectService.createSurvey(req)) {
           return Result.OK("新建成功！");
       } else {
           return Result.error("新建失败！");
       }
   }

   /**
    *  编辑
    *
    * @param surSurveyProject
    * @return
    */
   @AutoLog(value = "存放项目复制出来的问卷表-编辑")
   @ApiOperation(value="存放项目复制出来的问卷表-编辑", notes="存放项目复制出来的问卷表-编辑")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody SurSurveyProject surSurveyProject) {
       surSurveyProjectService.updateById(surSurveyProject);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "存放项目复制出来的问卷表-通过id删除")
   @ApiOperation(value="存放项目复制出来的问卷表-通过id删除", notes="存放项目复制出来的问卷表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       surSurveyProjectService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "存放项目复制出来的问卷表-批量删除")
   @ApiOperation(value="存放项目复制出来的问卷表-批量删除", notes="存放项目复制出来的问卷表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.surSurveyProjectService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   //@AutoLog(value = "存放项目复制出来的问卷表-通过id查询")
   @ApiOperation(value="存放项目复制出来的问卷表-通过id查询", notes="存放项目复制出来的问卷表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SurSurveyProject> queryById(@RequestParam(name="id",required=true) String id) {
       SurSurveyProject surSurveyProject = surSurveyProjectService.getById(id);
       if(surSurveyProject==null) {
           return Result.error("未找到对应数据");
       }
       return Result.OK(surSurveyProject);
   }

    /**
     * 通过项目id查询空白问卷
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "存放项目复制出来的问卷表-通过id查询")
    @ApiOperation(value="存放项目复制出来的问卷表-通过项目id查询空白问卷", notes="存放项目复制出来的问卷表-通过项目id查询空白问卷")
    @GetMapping(value = "/projectId/{id}")
    public Result<SurSurveyProject> querySurveyByProjectId(@PathVariable("id") String id) {
        SurSurveyProject survey = surSurveyProjectService.getSurveyByProjectId(id);
        return Result.OK(survey);
    }

   /**
   * 导出excel
   *
   * @param request
   * @param surSurveyProject
   */
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, SurSurveyProject surSurveyProject) {
       return super.exportXls(request, surSurveyProject, SurSurveyProject.class, "存放项目复制出来的问卷表");
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
       return super.importExcel(request, response, SurSurveyProject.class);
   }

}

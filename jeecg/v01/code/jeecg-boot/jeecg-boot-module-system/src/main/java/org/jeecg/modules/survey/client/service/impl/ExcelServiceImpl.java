package org.jeecg.modules.survey.client.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.survey.client.req.ExportReq;
import org.jeecg.modules.survey.client.service.ExcelService;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired
    private ISurProjectService surProjectService;
    @Autowired
    private ISurveyService surveyService;
    @Autowired
    private ISurUserService surUserService;
    @Autowired
    private ISurSurveyProjectService surSurveyProjectService;
    @Autowired
    private ISurResultService surResultService;
    @Autowired
    private ISurQuestionService surQuestionService;
    @Autowired
    private ISurQuestionService questionService;
    @Autowired
    private ISurUserResultService surUserResultService;
    @Autowired
    private ISurQuestionProjectService surQuestionProjectService;
    @Autowired
    private ISurQuestionChoiceProjectService surQuestionChoiceProjectService;
    @Autowired
    private ISurEvaluationRelationshipService surEvaluationRelationshipService;
    @Autowired
    private ISurEvaluationUserService surEvaluationUserService;
    @Override
    public void exportExcel(HttpServletResponse response, List<List<String>> head, List<List<Object>> data) throws Exception {
        // 文件输出位置
        ServletOutputStream out = response.getOutputStream();
        ExcelWriter writer = EasyExcelFactory.write(out).build();
        // 动态添加表头
        WriteSheet sheet1 = new WriteSheet();
        sheet1.setSheetName("Sheet1");
        sheet1.setSheetNo(0);
        // 创建一个表格，用于 Sheet 中使用
        WriteTable table = new WriteTable();
        table.setTableNo(1);
        table.setHead(head);
        // 写数据
        writer.write(data, sheet1, table);
        writer.finish();
        out.close();
    }

    @Override
    public void exportEvaluation(HttpServletResponse response, ExportReq req) throws Exception {
        List<List<String>> headTitles = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        //查询项目和问卷
        SurProject project = surProjectService.getById(req.getProjectId());
        Survey survey = surveyService.getById(req.getSurveyId());
        SurUser user = surUserService.getById(req.getUserId());
        SurResult result = surResultService.getOne(new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, req.getProjectId()).eq(SurResult::getSurveyUid, req.getSurveyId()).eq(SurResult::getUserUid, req.getUserId()));
        //查询问卷题目
        List<SurQuestion> questionList = questionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()));
        //转换为题目名称
        List<String> questionContentList = questionList.stream().map(SurQuestion::getContent).collect(Collectors.toList());
        String projectName = project.getProjectName();
        String surveyName = survey.getSurName();
        String userName = user.getName();
        String phone = user.getPhone();
        Date createDate = result.getCreateDate();
        List<Object> firstLine = new ArrayList<>();
        List<Object> secondLine = new ArrayList<>();
        //查询用户的答案
        List<SurUserResult> userResultList = surUserResultService.list(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getUserId, req.getUserId()).eq(SurUserResult::getSurveyId, req.getSurveyId()));
        Collections.addAll(firstLine, "项目名称", "问卷名称", "填写人", "手机号", "提交时间");
        Collections.addAll(secondLine, projectName, surveyName, userName, phone, createDate.toString());
        double sum = 0;
        for (SurUserResult surUserResult : userResultList) {
            if (!Objects.isNull(surUserResult.getQuestionTitle())) {
                firstLine.add(surUserResult.getQuestionTitle());
            }
            else {
                firstLine.add(surUserResult.getQuestionKey());
            }
            secondLine.add(surUserResult.getBasicScore());
            sum += Double.parseDouble((String) surUserResult.getBasicScore());
        }
        firstLine.add("总分");
        secondLine.add(sum);
        data.add(firstLine);
        data.add(secondLine);
        exportExcel(response,headTitles,data);
    }

    @Override
    public void exportInvestigation(HttpServletResponse response, ExportReq req) throws Exception {
        List<List<String>> headTitles = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        //查询项目和问卷
        SurProject project = surProjectService.getById(req.getProjectId());
        SurSurveyProject survey = surSurveyProjectService.getById(req.getSurveyId());
        SurUser user = surUserService.getById(req.getUserId());
        SurResult result = surResultService.getOne(new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, req.getProjectId()).eq(SurResult::getSurveyUid, req.getSurveyId()).eq(SurResult::getUserUid, req.getUserId()));
        //查询问卷题目
        List<SurQuestion> questionList = questionService.list(new LambdaQueryWrapper<SurQuestion>().eq(SurQuestion::getSurveyUid, req.getSurveyId()));
        //转换为题目名称
        List<String> questionContentList = questionList.stream().map(SurQuestion::getContent).collect(Collectors.toList());
        String projectName = project.getProjectName();
        String surveyName = survey.getSurName();
        String userName = user.getName();
        String phone = user.getPhone();
        Date createDate = result.getCreateDate();
        List<Object> firstLine = new ArrayList<>();
        List<Object> secondLine = new ArrayList<>();
        //查询用户的答案
        List<SurUserResult> userResultList = surUserResultService.list(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getUserId, req.getUserId()).eq(SurUserResult::getSurveyId, req.getSurveyId()));
        Collections.addAll(firstLine, "项目名称", "问卷名称", "填写人", "手机号", "提交时间");
        Collections.addAll(secondLine, projectName, surveyName);
        if (userName != null) {
            secondLine.add(userName);
        } else {
            secondLine.add("匿名");
        }
        if (phone != null) {
            secondLine.add(phone);
        } else {
            secondLine.add("未知");
        }
        secondLine.add(createDate.toString());
        for (SurUserResult surUserResult : userResultList) {
            if (!Objects.isNull(surUserResult.getQuestionTitle())) {
                firstLine.add(surUserResult.getQuestionTitle());
            }
            else {
                firstLine.add(surUserResult.getQuestionKey());
            }
            secondLine.add(surUserResult.getQuestionValue());
        }
        data.add(firstLine);
        data.add(secondLine);
        exportExcel(response,headTitles,data);
    }

    @Override
    public void export360(HttpServletResponse response, ExportReq req) throws Exception {
        List<List<String>> headTitles = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();
        //查询项目和问卷
        SurProject project = surProjectService.getById(req.getProjectId());
        SurSurveyProject survey = surSurveyProjectService.getById(req.getSurveyId());
        SurUser user = surUserService.getById(req.getUserId());
        SurResult result = surResultService.getOne(new LambdaQueryWrapper<SurResult>().eq(SurResult::getProjectUid, req.getProjectId()).eq(SurResult::getSurveyUid, req.getSurveyId()).eq(SurResult::getUserUid, req.getUserId()).eq(SurResult::getEvaluatedId, req.getEvaluatedId()));
        String projectName = project.getProjectName();
        String surveyName = survey.getSurName();
        String userName = user.getName();
        String phone = user.getPhone();
        //查询被评价人
        SurEvaluationUser e = surEvaluationUserService.getOne(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, req.getEvaluatedId()).eq(SurEvaluationUser::getEvaluatorId, req.getUserId()).eq(SurEvaluationUser::getProjectId, req.getProjectId()));
        //跟据e查询被评价用户
        SurUser evaluateUser = surUserService.getById(e.getUserId());
        //被评价人姓名
        String evaluatedName = evaluateUser.getName();
        //被评价人手机号
        String evaluatedPhone = evaluateUser.getPhone();
        //评价级别
        String level = "";
        switch (e.getEvaluatorLevel()) {
            case 0:
                level = "自评";
                break;
            case 1:
                level = "上级";
                break;
            case 2:
                level = "同事";
                break;
            case 3:
                level = "下级";
                break;
            case 4:
                level = "其他";
                break;
        }
        Date createDate = result.getCreateDate();
        List<Object> firstLine = new ArrayList<>();
        List<Object> secondLine = new ArrayList<>();
        //查询用户的答案
        List<SurUserResult> userResultList = surUserResultService.list(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getUserId, req.getUserId()).eq(SurUserResult::getEvaluatedId,req.getEvaluatedId()).eq(SurUserResult::getSurveyId, req.getSurveyId()));
        Collections.addAll(firstLine, "项目名称", "问卷名称", "填写人", "手机号", "被评价人", "被评价人手机号", "评价级别", "提交时间");
        Collections.addAll(secondLine, projectName, surveyName, userName, phone, evaluatedName, evaluatedPhone, level, createDate.toString());
        for (SurUserResult surUserResult : userResultList) {
            if (!Objects.isNull(surUserResult.getQuestionTitle())) {
                firstLine.add(surUserResult.getQuestionTitle());
            }
            else {
                firstLine.add(surUserResult.getQuestionKey());
            }
            secondLine.add(surUserResult.getQuestionValue());
        }
        data.add(firstLine);
        data.add(secondLine);
        exportExcel(response,headTitles,data);
    }

    //查询
}

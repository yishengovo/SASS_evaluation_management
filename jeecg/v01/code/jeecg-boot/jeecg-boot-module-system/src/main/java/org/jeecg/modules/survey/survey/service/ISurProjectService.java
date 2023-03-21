package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.client.req.ProjectAdvancedQueryReq;
import org.jeecg.modules.survey.survey.dto.CollectDto;
import org.jeecg.modules.survey.survey.dto.DashBordDto;
import org.jeecg.modules.survey.survey.dto.ProjectResultDto;
import org.jeecg.modules.survey.survey.entity.SurProject;
import org.jeecg.modules.survey.survey.req.CollectReq;
import org.jeecg.modules.survey.survey.req.ProjectEditReq;
import org.jeecg.modules.survey.survey.req.SetProjectSurveyReq;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 问卷项目表 @Author: jeecg-boot @Date: 2022-07-01 @Version: V1.0
 */
public interface ISurProjectService extends IService<SurProject> {
  // 保存 项目中的设计问卷
  //    Boolean saveSurveyInProject(NewSurveyReq req);
  // 给项目绑定问卷
  @Transactional(rollbackFor = Exception.class)
  Boolean setSurvey(SetProjectSurveyReq req);
  // 编辑项目问卷
  Boolean editSurvey(ProjectEditReq req);
  // 给项目中设置用户信息
  Boolean setUser(ProjectEditReq req);
  // 查询收集详情
  CollectDto getCollectData(CollectReq req);
  // 替换用户的问卷json
  Boolean replaceUserJson();
  // 新建360度类型的项目
  Boolean new360Survey(SurProject req);
  // 返回问卷的题目json和结果
  ProjectResultDto getSurveyResult(String projectId);
  // 返回问卷的每个题目的统计信息
  List<Object> getStatistics(String projectId);
  // 后台首页数据
  DashBordDto getDashBordData();
  // 条件查询
  Page<SurProject> getProjectList(ProjectAdvancedQueryReq req);
}

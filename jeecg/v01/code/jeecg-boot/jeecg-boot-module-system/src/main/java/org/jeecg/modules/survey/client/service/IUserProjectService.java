package org.jeecg.modules.survey.client.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.mail.iap.ConnectionException;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.survey.client.dto.*;
import org.jeecg.modules.survey.client.entity.UserProject;
import org.jeecg.modules.survey.client.req.*;
import org.jeecg.modules.survey.client.resp.PageResp;
import org.jeecg.modules.survey.survey.dto.ChoiceDto;
import org.jeecg.modules.survey.survey.dto.CollectDto;
import org.jeecg.modules.survey.survey.dto.ProjectResultDto;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.req.ChoiceByQuestionIdReq;
import org.jeecg.modules.survey.survey.req.CollectReq;
import org.jeecg.modules.survey.survey.req.ScoreSetReq;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 用户端项目表 @Author: jeecg-boot @Date: 2022-09-26 @Version: V1.0
 */
public interface IUserProjectService extends IService<UserProject> {

  // 根据项目id查询项目信息
  Object queryProjectInfo(String projectId);
  // 创建项目
  @Transactional(rollbackFor = Exception.class)
  SurProject createProject(CreateProjectReq req);
  // 根据租户返回项目列表
  Page<SurProject> getProjectList(ProjectAdvancedQueryReq req);
  // 根据项目id返回题目列表
  Page getQuestionList(EvaluationQuestionReq req);
  // 修改项目的发布状态
  Boolean updateStatus(ProjectStatusReq req);
  // 根据问题ID查询问题列表和分数
  List<ChoiceDto> getQuestionListByQuestionId(ChoiceByQuestionIdReq req);
  // 根据题目id赋分
  @Transactional(rollbackFor = Exception.class)
  Boolean setScore(ScoreSetReq req);
  // 删除项目
  @Transactional(rollbackFor = Exception.class)
  Boolean deleteProjectById(String id);
  // 查询360度项目收集详情
  AssessmentDto get360CollectData(CollectReq req);
  // 查询测评项目收集详情
  CollectDto getInvestigationCollectData(CollectReq req);
  // 返回问卷的题目json和结果
  ProjectResultDto getSurveyResult(String projectId);
  // 返回问卷的每个题目的统计信息
  ProjectCollectDto getStatistics(String projectId);

  @Transactional(rollbackFor = Exception.class)
  // 调查项目保存用户填写的问卷答案
  Boolean saveInvestigationResult(SaveCommonResultReq req);
  // 测评项目保存用户填写的问卷答案
  @Transactional(rollbackFor = Exception.class)
  Boolean saveEvaluationResult(SaveCommonResultReq req);
  // 360度考核项目保存用户填写的问卷答案
  @Transactional(rollbackFor = Exception.class)
  Boolean save360Result(SaveCommonResultReq req) throws JeecgBootException;
  // 批量添加360度项目的评价人员
  Boolean add360User(Add360UserReq req);
  // 删除一些360度项目的评价人员
  @Transactional(rollbackFor = Exception.class)
  Boolean remove360User(Add360UserReq req);
  // 360项目判断身份
  JudgeDto judge360User(JudgeReq req);
  // 返回360度项目的评价人员列表
  List<SurUser> get360UserList(String projectId);
  // 设置有上下级的评价关系
  @Transactional(rollbackFor = Exception.class)
  Boolean setLevelEvaluationRelationship(EvaluationRelSetReq req);
  // 设置没有上下级的评价关系
  @Transactional(rollbackFor = Exception.class)
  Boolean setCommonEvaluationRelationship(EvaluationRelSetReq req);
  // 删除评价关系
  @Transactional(rollbackFor = Exception.class)
  Boolean removeEvaluationRelationship(String id);
  // 修改评价关系
  @Transactional(rollbackFor = Exception.class)
  Boolean updateEvaluation(EvaluationRelSetReq req);
  // 查询评价关系
  List<EvaluationRelationDto> getEvaluationRelationship(String projectId);
  // 复制问卷
  @Transactional(rollbackFor = Exception.class)
  SurSurveyProject copySurvey(SurveyCopyReq req);
  // 删除问卷
  @Transactional(rollbackFor = Exception.class)
  Boolean deleteSurvey(String id);
  // 复制项目
  @Transactional(rollbackFor = Exception.class)
  SurProject copyProject(SurveyCopyReq req);
  // 查询项目权重
  SurEvaluationWeight getProjectWeight(String projectId);
  // 为评价关系分配权重
  Boolean setWeight(WeightSetReq req);
  // 逻辑删除项目
  Boolean logicDeleteProject(String id);
  // 查询回收站项目
  Page<SurProject> getBinProjects(PageReq req);
  // 清空回收站
  @Transactional(rollbackFor = Exception.class)
  Boolean cleanBinProject();
  // 查询项目答卷人员
  Page<SurUser> getProjectUserList(PageReq req);

  // 测评项目相关接口
  // 创建测评项目
  EvaluationProjectDto createEvaluationProject(CreateEvaluationProjectReq req);
  // 根据项目id查询问卷
  List<Survey> getEvaluationSurveyList(String projectId);
  // 查询问卷模板
  PageResp<Survey> getSurveyTemplateList(ProjectAdvancedQueryReq req, String tenantId);
  // 查询租户自己的问卷模板
  PageResp<Survey> getExclusiveSurveyTemplateList(ProjectAdvancedQueryReq req, String tenantId);
  // 辅助查询，查询租户和问卷的关系
  Boolean getTenantAndSurveyRelation(PurchaseReq req, String tenantId);
  // 通过积分购买问卷模板
  @Transactional(rollbackFor = Exception.class)
  Boolean purchaseByPoint(PurchaseReq req, String tenantId);
  // 根据问卷模板id查询问卷模板
  Survey getSurveyTemplateById(String id);
  // 测评项目设置测评问卷
  Boolean setEvaluationSurveyList(EvaluationSurveySetReq req);
  // 测评项目设置测评人员
  @Transactional(rollbackFor = Exception.class)
  Boolean addEvaluationUser(Add360UserReq req);
  // 通过Excel导入问卷填写人
  Boolean importSurveyUser(MultipartFile file, String projectId) throws IOException;
  // 测评项目删除一些测评人员
  Boolean removeEvaluationUser(Add360UserReq req);
  // 查询测评项目人员列表
  List<SurUser> getEvaluationUserList(String projectId);
  // 测评项目判断身份
  EvaluationJudgeDto judgeEvaluation(JudgeReq req);
  // 调查项目判断身份
  InvestigationJudgeDto judgeInvestigation(JudgeReq req);
  // 测评项目查询人自己的答卷及其状态
  EvaluationJudgeDto getUserSurveyResult(JudgeReq req);
  // 360项目查询人自己的答卷及其状态
  JudgeDto get360UserSurveyResult(JudgeReq req);
  // 测评项目统计&&分析
  ProjectCollectDto getEvaluationStatistics(String projectId);
  // 根据问卷id查询单张问卷结果
  List<Object> getEvaluationSurvey(EvaluationSurveyReq req);
  // 测评项目收集情况
  EvaluationCollectProjectDto getEvaluationCollectData(CollectReq req);
  // 测评项目数据大屏
  EvaluationProjectResultDto getEvaluationSurveyResult(String projectId);
  // 获取 管理风格测评测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getManagementStyleReport(String projectId, String userId) throws ConnectionException;
  // 获取 团队角色测评测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getTeamRoleReport(String projectId, String userId) throws ConnectionException;
  // 获取个人事业驱动力测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getPersonalCareerDrivingForceReport(String projectId, String userId)
      throws ConnectionException;
  // 获取 管理者沟通自我认知测评测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getManagersCommunicateSelfPerceptionReport(String projectId, String userId);
  // 获取 人本化测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getHumanizationReport(String projectId, String userId);
  // 获取 人才创造力测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getTalentCreativityReport(String projectId, String userId) throws ConnectionException;
  // 获取 人才思维方式测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getAssessmentOfTalentThinkingModeReport(String projectId, String userId)
      throws ConnectionException;
  // 获取 团队能量测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getTeamEnergyReport(String projectId, String userId);
  // 获取 自我驱动力测评测评报告结果
  @Transactional(rollbackFor = Exception.class)
  void getSelfDriveReport(String projectId, String userId) throws ConnectionException;

  @Transactional(rollbackFor = Exception.class)
  // 为用户生成测评报告
  Boolean generateReportForUser(ReportFormCreateReq req) throws ConnectionException;

  // 数据迁移
  // 管理风格测评迁移
  @Transactional(rollbackFor = Exception.class)
  void transferManagementStyleEvaluation(MultipartFile file, String projectId, String surveyId)
      throws IOException;
  // 插入sur_result
  @Transactional(rollbackFor = Exception.class)
  void insertSurResult(MultipartFile file) throws IOException;
  // 插入sur_user_result
  @Transactional(rollbackFor = Exception.class)
  void insertSurUserResult(MultipartFile file) throws IOException;
  // 迁移满意度调查
  @Transactional(rollbackFor = Exception.class)
  void transferSatisfactionSurvey(MultipartFile file) throws IOException;
}

package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.dto.*;
import org.jeecg.modules.survey.survey.entity.SurEvaluationRelationship;
import org.jeecg.modules.survey.survey.req.EvaluatedInfoReq;
import org.jeecg.modules.survey.survey.req.Evaluation;
import org.jeecg.modules.survey.survey.req.EvaluationSetReq;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 评价关系表
 * @Author: jeecg-boot
 * @Date:   2022-08-05
 * @Version: V1.0
 */
public interface ISurEvaluationRelationshipService extends IService<SurEvaluationRelationship> {
    @Transactional(rollbackFor = Exception.class)
    //设置评价关系
    Boolean setEvaluationRelationship(EvaluationSetReq req);
    //设置有上下级的评价关系
    @Transactional(rollbackFor = Exception.class)
    Boolean setLevelEvaluationRelationship(EvaluationSetReq req);
    //重新设置评价关系
    Boolean resetEvaluationRelationship(List<Evaluation> req);
    //根据项目id查询评价关系
    List<EvaluationRelationshipDto> getEvaluationRelationship(String projectId);
    //根据项目id查询有上下级的评价关系
    List<EvaluationRelationshipLevelDto> getEvaluationRelationshipLevel(String projectId);
    //根据被评级人id查询得分情况
    EvaluatedScoreDto getEvaluatedScore(EvaluatedInfoReq req);
//    查询被评价人的同事、上级等得分信息
    Map<String, Object> getOthersScore(EvaluatedInfoReq req);
    //查询题目得分结果
    List<QuestionScoreDto> getQuestionScore(EvaluatedInfoReq req);
    //查询360度项目列表
    List<ReportDto> get360Project();
    //查询被评价人
    List<ReportDto> getEvaluated(String id);
}

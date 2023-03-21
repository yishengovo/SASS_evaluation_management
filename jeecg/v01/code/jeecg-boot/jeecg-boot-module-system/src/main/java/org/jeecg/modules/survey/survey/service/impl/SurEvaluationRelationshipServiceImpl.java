package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.dto.*;
import org.jeecg.modules.survey.survey.entity.*;
import org.jeecg.modules.survey.survey.mapper.*;
import org.jeecg.modules.survey.survey.req.EvaluatedInfoReq;
import org.jeecg.modules.survey.survey.req.Evaluation;
import org.jeecg.modules.survey.survey.req.EvaluationSetReq;
import org.jeecg.modules.survey.survey.service.ISurEvaluationRelationshipService;
import org.jeecg.modules.survey.survey.utils.NumUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 评价关系表
 * @Author: jeecg-boot
 * @Date: 2022-08-05
 * @Version: V1.0
 */
@Service
public class SurEvaluationRelationshipServiceImpl extends ServiceImpl<SurEvaluationRelationshipMapper, SurEvaluationRelationship> implements ISurEvaluationRelationshipService {

    @Autowired
    private SurEvaluationUserMapper evaluationUserMapper;
    @Autowired
    private SurEvaluationWeightMapper evaluationWeightMapper;
    @Autowired
    private SurUserMapper userMapper;
    @Autowired
    private SurProjectUserMapper projectUserMapper;
    @Autowired
    private SurProjectMapper projectMapper;
    @Autowired
    private SurUserResultMapper userResultMapper;
    @Autowired
    private SurQuestionProjectMapper questionMapper;

    @Transactional
    @Override
    public Boolean setEvaluationRelationship(EvaluationSetReq req) {
        List<Evaluation> evaluationList = req.getEvaluationList();
        SurProject exitProject = projectMapper.selectById(evaluationList.get(0).getProjectId());
        exitProject.setEvaluationType(0);
        projectMapper.updateById(exitProject);
        evaluationList.forEach(evaluation ->{
            //判断是否有id 没有id为添加
            if (evaluation.getId() == null){
                //添加评价关系
                SurEvaluationRelationship evaluationRelationship = new SurEvaluationRelationship();
                BeanUtils.copyProperties(evaluation, evaluationRelationship);
                //根据项目id查询项目
                SurProject project = projectMapper.selectById(evaluation.getProjectId());
                evaluationRelationship.setSurveyId(project.getSurveyCurrentId());
                //根据被评者id查询该用户
                SurUser user = userMapper.selectById(evaluation.getEvaluatedName());
                evaluationRelationship.setUserId(user.getId());
                evaluationRelationship.setUserName(user.getName());
                evaluationRelationship.setType(0);
                this.save(evaluationRelationship);
                String evaluatorName = evaluation.getEvaluatorName();
                //根据逗号分割evaluatorName并转换为list
                List<String> evaluatorIdList = Arrays.asList(evaluatorName.split(","));
                //添加评价人员关系
                //首先删除有关他的评价人员关系
                evaluationUserMapper.delete(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, user.getId()).eq(SurEvaluationUser::getProjectId, evaluation.getProjectId()));
                evaluatorIdList.forEach(evaluatorId ->{
                    //根据评价人id查询评价人
                    SurUser evaluator = userMapper.selectById(evaluatorId);
                    //添加评价人员关系
                    SurEvaluationUser evaluationUser = new SurEvaluationUser();
                    evaluationUser.setUserId(user.getId());
                    evaluationUser.setUserName(user.getName());
                    evaluationUser.setProjectId(evaluation.getProjectId());
                    evaluationUser.setEvaluatorId(evaluator.getId());
                    evaluationUser.setEvaluatorName(evaluator.getName());
                    evaluationUserMapper.insert(evaluationUser);
                });
            }
            //如果有id则覆盖之前的
            else {
                //根据id查询评价关系
                SurEvaluationRelationship evaluationRelationship = this.getById(evaluation.getId());
                //根据被评者id查询该用户
                SurUser user = userMapper.selectById(evaluation.getEvaluatedName());
                //首先删除有关他的评价人员关系
                evaluationUserMapper.delete(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, user.getId()).eq(SurEvaluationUser::getProjectId, evaluation.getProjectId()));
                evaluationRelationship.setUserId(user.getId());
                evaluationRelationship.setUserName(user.getName());
                this.updateById(evaluationRelationship);
                String evaluatorName = evaluation.getEvaluatorName();
                //根据逗号分割evaluatorName并转换为list
                List<String> evaluatorIdList = Arrays.asList(evaluatorName.split(","));
                //添加评价人员关系
                this.setEvaluationUser(evaluatorIdList,user,evaluation,null);
            }
        });
        return true;
    }

    @Override
    public Boolean setLevelEvaluationRelationship(EvaluationSetReq req) {
        List<Evaluation> evaluationList = req.getEvaluationList();
        SurProject exitProject = projectMapper.selectById(evaluationList.get(0).getProjectId());
        exitProject.setEvaluationType(1);
        projectMapper.updateById(exitProject);
        evaluationList.forEach(evaluation ->{
            //判断是否有id 没有id为添加
            if (evaluation.getId() == null){
                //添加评价关系
                SurEvaluationRelationship evaluationRelationship = new SurEvaluationRelationship();
                BeanUtils.copyProperties(evaluation, evaluationRelationship);
                //根据项目id查询项目
                SurProject project = projectMapper.selectById(evaluation.getProjectId());
                evaluationRelationship.setSurveyId(project.getSurveyCurrentId());
                //根据被评者id查询该用户
                SurUser user = userMapper.selectById(evaluation.getEvaluatedName());
                evaluationRelationship.setUserId(user.getId());
                evaluationRelationship.setUserName(user.getName());
                evaluationRelationship.setType(1);
                List<String> superiorIdList = new ArrayList<>();
                List<String> colleagueIdList = new ArrayList<>();
                List<String> subordinateIdList = new ArrayList<>();
                List<String> otherIdList = new ArrayList<>() ;
                //上级用户id数组
                String superiorName = evaluation.getSuperiorName();
                //同事用户id数组
                String colleagueName = evaluation.getColleagueName();
                //下级用户id数组
                String subordinateName = evaluation.getSubordinateName();
                //其他用户id数组
                String otherName = evaluation.getOtherName();
                if (evaluation.getSuperiorName() != null && superiorName.length() > 0) {
                    superiorIdList = Arrays.asList(superiorName.split(","));
                }
                if (evaluation.getColleagueName() != null && colleagueName.length() > 0) {
                    colleagueIdList = Arrays.asList(colleagueName.split(","));
                }
                if (evaluation.getSubordinateName() != null && subordinateName.length() > 0) {
                    subordinateIdList = Arrays.asList(subordinateName.split(","));
                }
                if (evaluation.getOtherName() != null && otherName.length() > 0) {
                    otherIdList = Arrays.asList(otherName.split(","));
                }
                evaluationRelationship.setSuperiorId(superiorIdList);
                evaluationRelationship.setColleagueId(colleagueIdList);
                evaluationRelationship.setSubordinateId(subordinateIdList);
                evaluationRelationship.setOtherId(otherIdList);
                evaluationRelationship.setUserId(user.getId());
                evaluationRelationship.setUserName(user.getName());
                //如果开启自评则手动为user插入数据
                if (evaluation.getIsSelf()) {
                    SurEvaluationUser evaluationUser = new SurEvaluationUser();
                    evaluationUser.setUserId(user.getId());
                    evaluationUser.setUserName(user.getName());
                    evaluationUser.setProjectId(evaluation.getProjectId());
                    evaluationUser.setEvaluatorId(user.getId());
                    evaluationUser.setEvaluatorName(user.getName());
                    evaluationUser.setEvaluatorLevel(0);
                    evaluationUserMapper.insert(evaluationUser);
                }
                this.save(evaluationRelationship);
                //添加评价人员关系
                if ( !superiorIdList.isEmpty()) {
                    this.setEvaluationUser(superiorIdList, user, evaluation,1);
                }
                if ( !colleagueIdList.isEmpty()) {
                    this.setEvaluationUser(colleagueIdList, user, evaluation,2);
                }
                if ( !subordinateIdList.isEmpty()) {
                    this.setEvaluationUser(subordinateIdList, user, evaluation,3);
                }
                if ( !otherIdList.isEmpty()) {
                    this.setEvaluationUser(otherIdList, user, evaluation,4);
                }
            }

            //如果有id则覆盖之前的
            else {
                //根据id查询评价关系
                SurEvaluationRelationship evaluationRelationship = this.getById(evaluation.getId());
                //根据被评者id查询该用户
                SurUser user = userMapper.selectById(evaluation.getEvaluatedName());
                List<String> superiorIdList = new ArrayList<>();
                List<String> colleagueIdList = new ArrayList<>();
                List<String> subordinateIdList = new ArrayList<>();
                List<String> otherIdList = new ArrayList<>() ;
                //上级用户id数组
                String superiorName = evaluation.getSuperiorName();
                //同事用户id数组
                String colleagueName = evaluation.getColleagueName();
                //下级用户id数组
                String subordinateName = evaluation.getSubordinateName();
                //其他用户id数组
                String otherName = evaluation.getOtherName();
                if (evaluation.getSuperiorName() != null && superiorName.length() > 0) {
                    superiorIdList = Arrays.asList(superiorName.split(","));
                }
                if (evaluation.getColleagueName() != null && colleagueName.length() > 0) {
                    colleagueIdList = Arrays.asList(colleagueName.split(","));
                }
                if (evaluation.getSubordinateName() != null && subordinateName.length() > 0) {
                    subordinateIdList = Arrays.asList(subordinateName.split(","));
                }
                if (evaluation.getOtherName() != null && otherName.length() > 0) {
                    otherIdList = Arrays.asList(otherName.split(","));
                }
                evaluationRelationship.setSuperiorId(superiorIdList);
                evaluationRelationship.setColleagueId(colleagueIdList);
                evaluationRelationship.setSubordinateId(subordinateIdList);
                evaluationRelationship.setOtherId(otherIdList);
                evaluationRelationship.setUserId(user.getId());
                evaluationRelationship.setUserName(user.getName());
                evaluationRelationship.setIsSelf(evaluation.getIsSelf());
                this.updateById(evaluationRelationship);
                //首先删除有关他的评价人员关系
                evaluationUserMapper.delete(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, user.getId()).eq(SurEvaluationUser::getProjectId, evaluation.getProjectId()));
                //如果开启自评则手动为user插入数据
                if (evaluation.getIsSelf()) {
                    SurEvaluationUser evaluationUser = new SurEvaluationUser();
                    evaluationUser.setUserId(user.getId());
                    evaluationUser.setUserName(user.getName());
                    evaluationUser.setProjectId(evaluation.getProjectId());
                    evaluationUser.setEvaluatorId(user.getId());
                    evaluationUser.setEvaluatorName(user.getName());
                    evaluationUser.setEvaluatorLevel(0);
                    evaluationUserMapper.insert(evaluationUser);
                }
                //添加评价人员关系
                if ( !superiorIdList.isEmpty()) {
                    this.setEvaluationUser(superiorIdList, user, evaluation,1);
                }
                if ( !colleagueIdList.isEmpty()) {
                    this.setEvaluationUser(colleagueIdList, user, evaluation,2);
                }
                if ( !subordinateIdList.isEmpty()) {
                    this.setEvaluationUser(subordinateIdList, user, evaluation,3);
                }
                if ( !otherIdList.isEmpty()) {
                    this.setEvaluationUser(otherIdList, user, evaluation,4);
                }
            }
        });
        return true;
    }

    @Transactional
    @Override
    public Boolean resetEvaluationRelationship(List<Evaluation> req) {
        //遍历req 先删除原来的数据
//        req.forEach(evaluation -> {
//                //删除原来的数据
//                remove(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getProjectId, evaluation.getProjectId()));
//                //删除原来的副表数据
//                evaluationUserMapper.delete(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getProjectId, evaluation.getProjectId()));
//                //添加新的数据
//                setEvaluationRelationship(req);
//        });
        return true;
    }

    @Override
    public List<EvaluationRelationshipDto> getEvaluationRelationship(String projectId) {
        List<SurEvaluationRelationship> evaluationRelationshipList = list(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getProjectId, projectId).eq(SurEvaluationRelationship::getType, 0));
        //构造EvaluationRelationshipDto
        List<EvaluationRelationshipDto> evaluationRelationshipDtoList = new ArrayList<>();
        evaluationRelationshipList.forEach(evaluationRelationship -> {
            EvaluationRelationshipDto evaluationRelationshipDto = new EvaluationRelationshipDto();
            BeanUtils.copyProperties(evaluationRelationship, evaluationRelationshipDto);
            List<SurEvaluationUser> reviewers = evaluationUserMapper.selectList(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, evaluationRelationship.getUserId()));
            evaluationRelationshipDto.setUserList(reviewers);
            //被评价人id
            List<String> evaluatedName = new ArrayList<>();
            evaluatedName.add(evaluationRelationship.getUserId());
            evaluationRelationshipDto.setEvaluatedName(evaluatedName);
            //评价人id
            List<String> evaluatorName = new ArrayList<>();
              reviewers.forEach(reviewer -> {
                evaluatorName.add(reviewer.getEvaluatorId());
            });
              evaluationRelationshipDto.setEvaluatorName(evaluatorName);
            evaluationRelationshipDtoList.add(evaluationRelationshipDto);
        });
        return evaluationRelationshipDtoList;
    }

    @Override
    public List<EvaluationRelationshipLevelDto> getEvaluationRelationshipLevel(String projectId) {
        List<SurEvaluationRelationship> evaluationRelationshipList = list(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getProjectId, projectId).eq(SurEvaluationRelationship::getType, 1));
        //构造EvaluationRelationshipDto
        List<EvaluationRelationshipLevelDto> evaluationRelationshipDtoList = new ArrayList<>();
        evaluationRelationshipList.forEach(evaluationRelationship -> {
            EvaluationRelationshipLevelDto evaluationRelationshipDto = new EvaluationRelationshipLevelDto();
            BeanUtils.copyProperties(evaluationRelationship, evaluationRelationshipDto);
            //被评价人id
            List<String> evaluatedName = new ArrayList<>();
            evaluatedName.add(evaluationRelationship.getUserId());
            evaluationRelationshipDto.setEvaluatedName(evaluatedName);
            //上级
            List<String> superiorId = evaluationRelationship.getSuperiorId();
            //同事
            List<String> colleagueId = evaluationRelationship.getColleagueId();
            //下级
            List<String> subordinateId = evaluationRelationship.getSubordinateId();
            //其他
            List<String> otherId = evaluationRelationship.getOtherId();
            //上级数组
            List<SurEvaluationUser> superiorList = new ArrayList<>();
            //同事数组
            List<SurEvaluationUser> colleagueList = new ArrayList<>();
            //下级数组
            List<SurEvaluationUser> subordinateList = new ArrayList<>();
            //其他数组
            List<SurEvaluationUser> otherList = new ArrayList<>();
            evaluationRelationshipDto.setSuperiorName(superiorId);
            evaluationRelationshipDto.setColleagueName(colleagueId);
            evaluationRelationshipDto.setSubordinateName(subordinateId);
            evaluationRelationshipDto.setOtherName(otherId);
            for (String superior : evaluationRelationship.getSuperiorId()) {
                SurEvaluationUser superiorOne = evaluationUserMapper.selectOne(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId, evaluationRelationship.getUserId()).eq(SurEvaluationUser::getEvaluatorId, superior));
                superiorList.add(superiorOne);
            }
            for (String colleague : evaluationRelationship.getColleagueId()) {
                SurEvaluationUser colleagueOne = evaluationUserMapper.selectOne(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId,evaluationRelationship.getUserId()).eq(SurEvaluationUser::getEvaluatorId,colleague));
                colleagueList.add(colleagueOne);
            }
            for (String subordinate : evaluationRelationship.getSubordinateId()) {
                SurEvaluationUser subordinateOne =  evaluationUserMapper.selectOne(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId,evaluationRelationship.getUserId()).eq(SurEvaluationUser::getEvaluatorId,subordinate));
                subordinateList.add(subordinateOne);
            }
            for (String other : evaluationRelationship.getOtherId()) {
                SurEvaluationUser otherOne = evaluationUserMapper.selectOne(new LambdaQueryWrapper<SurEvaluationUser>().eq(SurEvaluationUser::getUserId,evaluationRelationship.getUserId()).eq(SurEvaluationUser::getEvaluatorId,other));
                otherList.add(otherOne);
            }
            evaluationRelationshipDto.setSuperiorList(superiorList);
            evaluationRelationshipDto.setColleagueList(colleagueList);
            evaluationRelationshipDto.setSubordinateList(subordinateList);
            evaluationRelationshipDto.setOtherList(otherList);
            evaluationRelationshipDtoList.add(evaluationRelationshipDto);
        });
        return evaluationRelationshipDtoList;
    }

    @Override
    public EvaluatedScoreDto getEvaluatedScore(EvaluatedInfoReq req) {
        //查询项目对象
        SurProject project = projectMapper.selectById(req.getProjectId());
        //查询评价关系
        SurEvaluationRelationship evaluationRelationship = this.getOne(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getProjectId, req.getProjectId()).eq(SurEvaluationRelationship::getUserId, req.getEvaluatedId()));
        EvaluatedScoreDto evaluatedScoreDto = new EvaluatedScoreDto();
        evaluatedScoreDto.setName(evaluationRelationship.getUserName());
        evaluatedScoreDto.setProjectName(project.getProjectName());
        List<SurUserResult> surUserResults = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()));
        //获取评级人的id数组
        List<String> idList = surUserResults.stream().map(SurUserResult::getUserId).distinct().collect(Collectors.toList());
        List<List<SurUserResult>> deviceUserResultList = new ArrayList<>();
        //获取该用户的同事上级等
        Map<String, Object> levelScore = this.getLevelScore(req);
        //上级的得分情况
        Map<String, Object> superior = (Map<String, Object>) levelScore.get("superior");
        //同事的得分情况
        Map<String, Object> colleague = (Map<String, Object>) levelScore.get("colleague");
        //下级的得分情况
        Map<String, Object> subordinate = (Map<String, Object>) levelScore.get("subordinate");
        //其他的得分情况
        Map<String, Object> other = (Map<String, Object>) levelScore.get("other");
        int selfScore = 0;
        int superiorScore = 0;
        int colleagueScore = 0;
        int subordinateScore = 0;
        int otherScore = 0;
        int sum = 0;
        //个人得分
        Double personScore = 0.00;
        Double v = 0.00;
        double deviceAll = 0.00;
        //获取平均分
        String average1 = (String) levelScore.get("average");
        //上级未加权的平均得分
        Double superiorScoreSrc = (Double) superior.get("score");
        //同事未加权的平均得分
        Double colleagueScoreSrc = (Double) colleague.get("score");
        //下级未加权的平均得分
        Double subordinateScoreSrc = (Double) subordinate.get("score");
        //其他未加权的平均得分
        Double otherScoreSrc = (Double) other.get("score");
        evaluatedScoreDto.setAverageScore(average1);
        //判断项目是否是有上下级的
        if (project.getEvaluationType() == 0) {
            //如果为无上下级的 则无需计算权重 直接计算平均分
               //直接查询所有有关此用户的答案
            if (!idList.isEmpty()) {
                for (String id : idList) {
                    List<SurUserResult> oneResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, id));
                    deviceUserResultList.add(oneResult);
                    int oneScore = 0;
                    for (SurUserResult surUserResult : surUserResults) {
                        oneScore += Integer.parseInt(surUserResult.getBasicScore().toString());
                    }
                    sum += oneScore;
                }
                //计算平均分
                if (deviceUserResultList.size() != 0) {
                    personScore = Double.parseDouble(NumUtils.device(sum, deviceUserResultList.size()));
                }
                evaluatedScoreDto.setScore(personScore.toString());
                //计算总的平均分
//                 v = (personScore + superiorScoreSrc + subordinateScoreSrc + colleagueScoreSrc + otherScoreSrc) / 5;
//                evaluatedScoreDto.setAverageScore(String.format("%.2f", v));

            }
        }
        //如果是有上下级的 则需要计算权重
        else {
            //查询每一个级别的分数和
            //查询自评的分数和

            List<SurUserResult> selfResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserLevel, 0));
            for (SurUserResult surUserResult : selfResult) {
                selfScore += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            //查询上级的分数和
            List<SurUserResult> superiorResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserLevel, 1));
            for (SurUserResult surUserResult : superiorResult) {
                superiorScore += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            //查询同级的分数和

            List<SurUserResult> colleagueResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserLevel, 2));
            for (SurUserResult surUserResult : colleagueResult) {
                colleagueScore += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            //查询下级的分数和

            List<SurUserResult> subordinateResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserLevel, 3));
            for (SurUserResult surUserResult : subordinateResult) {
                subordinateScore += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            //查询其他的分数和
            List<SurUserResult> otherResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserLevel, 4));
            for (SurUserResult surUserResult : otherResult) {
                otherScore += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            //查询评价关系
            SurEvaluationWeight surEvaluationWeight = evaluationWeightMapper.selectOne(new LambdaQueryWrapper<SurEvaluationWeight>().eq(SurEvaluationWeight::getProjectId, req.getProjectId()));
            //如果还没有设置权重
            if (null == surEvaluationWeight) {

                int addScore = selfScore + superiorScore + colleagueScore + subordinateScore + otherScore;
                if (addScore != 0) {
                    deviceAll = Double.parseDouble(NumUtils.device(addScore, selfResult.size() + superiorResult.size() + colleagueResult.size() + subordinateResult.size() + otherResult.size()));
                    evaluatedScoreDto.setScore(String.format("%.2f", deviceAll));
                } else {
                    evaluatedScoreDto.setScore("0");
                }
            }
            //如果有设置权重
            else {
                double selfWeight = selfScore * NumUtils.getDouble(surEvaluationWeight.getSelfWeight());
                double superiorWeight = superiorScore * NumUtils.getDouble(surEvaluationWeight.getSuperiorWeight());
                double colleagueWeight = colleagueScore * NumUtils.getDouble(surEvaluationWeight.getColleagueWeight());
                double subordinateWeight = subordinateScore * NumUtils.getDouble(surEvaluationWeight.getSubordinateWeight());
                double otherWeight = otherScore * NumUtils.getDouble(surEvaluationWeight.getOtherWeight());
                double all = selfWeight + superiorWeight + colleagueWeight + subordinateWeight + otherWeight;
                double average = (superiorScoreSrc + subordinateScoreSrc + colleagueScoreSrc + otherScoreSrc + deviceAll) / 5;
                evaluatedScoreDto.setScore(String.format("%.2f", all));
            }
        }
        return evaluatedScoreDto;
    }

    @Override
    public Map<String, Object> getOthersScore(EvaluatedInfoReq req) {
//        return this.getLevelScore(req);
        //获取该用户的同事上级等
        Map<String, Object> levelScore = this.getLevelScore(req);
        //上级的得分情况
        Map<String, Object> superior = (Map<String, Object>) levelScore.get("superior");
        //同事的得分情况
        Map<String, Object> colleague = (Map<String, Object>) levelScore.get("colleague");
        //下级的得分情况
        Map<String, Object> subordinate = (Map<String, Object>) levelScore.get("subordinate");
        //其他的得分情况
        Map<String, Object> other = (Map<String, Object>) levelScore.get("other");
        //获取平均分
        String average = (String) levelScore.get("average");
        //上级未加权的平均得分
        Double superiorScoreSrc = (Double) superior.get("score");
        Integer superiorNum = (Integer) superior.get("num");
        //同事未加权的平均得分
        Double colleagueScoreSrc = (Double) colleague.get("score");
        Integer colleagueNum = (Integer) colleague.get("num");
        //下级未加权的平均得分
        Double subordinateScoreSrc = (Double) subordinate.get("score");
        Integer subordinateNum = (Integer) subordinate.get("num");
        //其他未加权的平均得分
        Double otherScoreSrc = (Double) other.get("score");
        Integer otherNum = (Integer) other.get("num");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("average", average);
        result.put("superiorScore", superiorScoreSrc);
        result.put("superiorNum", superiorNum);
        result.put("colleagueScore", colleagueScoreSrc);
        result.put("colleagueNum", colleagueNum);
        result.put("subordinateScore", subordinateScoreSrc);
        result.put("subordinateNum", subordinateNum);
        result.put("otherScore", otherScoreSrc);
        result.put("otherNum", otherNum);
        return result;
    }

    @Override
    public List<QuestionScoreDto> getQuestionScore(EvaluatedInfoReq req) {
        //查询项目对象
        SurProject surProject = projectMapper.selectById(req.getProjectId());
        List<QuestionScoreDto> resultList = new ArrayList<>();
        if (null == surProject) {
            return null;
        }
        //查询该项目的题目列表
        List<SurQuestionProject> questionList = questionMapper.selectList(new LambdaQueryWrapper<SurQuestionProject>().eq(SurQuestionProject::getSurveyUid, surProject.getSurveyCurrentId()));
        for (SurQuestionProject question : questionList) {
            QuestionScoreDto questionScoreDto = new QuestionScoreDto();
            questionScoreDto.setName(question.getContent());
            //查询个人得分
            List<SurUserResult> selfResults = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getQuestionKey, question.getContent()));
            //获取评级人的id数组
            List<String> idList = selfResults.stream().map(SurUserResult::getUserId).distinct().collect(Collectors.toList());
            //评价自己的人的个数
            List<List<SurUserResult>> evaluateSelfNum = new ArrayList<>();
            int selfScore = 0;
            for (String othersId : idList) {
                List<SurUserResult> oneResults = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getQuestionKey, question.getContent()).eq(SurUserResult::getUserId, othersId));
                evaluateSelfNum.add(oneResults);
                for (SurUserResult surUserResult : oneResults) {
                    selfScore += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
            }
            if (evaluateSelfNum.size() != 0) {
                questionScoreDto.setSelfScore(String.format("%.2f", Double.parseDouble(NumUtils.device(selfScore, evaluateSelfNum.size()))));
            } else {
                questionScoreDto.setSelfScore("0");
            }
            //计算同事得分
            List<List<SurUserResult>> superiorScore = new ArrayList<>();
            List<List<SurUserResult>> colleagueScore = new ArrayList<>();
            List<List<SurUserResult>> subordinateScore = new ArrayList<>();
            List<List<SurUserResult>> otherScore = new ArrayList<>();
            int superiorSum = 0;
            int colleagueSum = 0;
            int subordinateSum = 0;
            int otherSum = 0;
            SurEvaluationRelationship relationship = this.getOne(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getUserId, req.getEvaluatedId()).eq(SurEvaluationRelationship::getProjectId, req.getProjectId()));
            List<List<SurUserResult>> deviceUserResultList = new ArrayList<>();
            int sum = 0;
            //计算总体平均分
            List<SurUserResult> allResults = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getQuestionKey, question.getContent()));
            List<String> allIds = allResults.stream().map(SurUserResult::getUserId).distinct().collect(Collectors.toList());
            for (String allId : allIds) {
                List<SurUserResult> allResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getUserId, allId).eq(SurUserResult::getQuestionKey, question.getContent()));
                deviceUserResultList.add(allResult);
                for (SurUserResult surUserResult : allResult) {
                    sum += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
                if (deviceUserResultList.size() > 0) {
                    questionScoreDto.setAverageScore(String.format("%.2f", Double.parseDouble(NumUtils.device(sum, deviceUserResultList.size()))));
                } else {
                    questionScoreDto.setAverageScore("0");
                }
            }
            //如果已经设置上下级的权重
            if (null != relationship) {
                //上级
                List<String> superiorIdList = relationship.getSuperiorId();
                for (String superiorId : superiorIdList) {
                    List<SurUserResult> superiorOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, superiorId).eq(SurUserResult::getQuestionKey, question.getContent()));
                    superiorScore.add(superiorOneScore);
                    for (SurUserResult surUserResult : superiorOneScore) {
                        superiorSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                    }
                }
                //同事
                List<String> colleagueIdList = relationship.getColleagueId();
                for (String colleagueId : colleagueIdList) {
                    List<SurUserResult> colleagueOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, colleagueId).eq(SurUserResult::getQuestionKey, question.getContent()));
                    colleagueScore.add(colleagueOneScore);
                    for (SurUserResult surUserResult : colleagueOneScore) {
                        colleagueSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                    }
                }
                //下级
                List<String> subordinateIdList = relationship.getSubordinateId();
                for (String subordinateId : subordinateIdList) {
                    List<SurUserResult> subordinateOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, subordinateId).eq(SurUserResult::getQuestionKey, question.getContent()));
                    subordinateScore.add(subordinateOneScore);
                    for (SurUserResult surUserResult : subordinateOneScore) {
                        subordinateSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                    }
                }
                //其他
                List<String> otherIdList = relationship.getOtherId();
                for (String otherId : otherIdList) {
                    List<SurUserResult> otherOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, otherId).eq(SurUserResult::getQuestionKey, question.getContent()));
                    otherScore.add(otherOneScore);
                    for (SurUserResult surUserResult : otherOneScore) {
                        otherSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                    }
                }
                //上级平均分
                if (superiorScore.size() > 0) {
                    questionScoreDto.setSuperiorScore(String.format("%.2f", Double.parseDouble(NumUtils.device(superiorSum, superiorScore.size()))));
                } else {
                    questionScoreDto.setSuperiorScore("0");
                }
                //同事平均分
                if (colleagueScore.size() > 0) {
                    questionScoreDto.setColleagueScore(String.format("%.2f", Double.parseDouble(NumUtils.device(colleagueSum, colleagueScore.size()))));
                } else {
                    questionScoreDto.setColleagueScore("0");
                }
                //下级平均分
                if (subordinateScore.size() > 0) {
                    questionScoreDto.setSubordinateScore(String.format("%.2f", Double.parseDouble(NumUtils.device(subordinateSum, subordinateScore.size()))));
                } else {
                    questionScoreDto.setSubordinateScore("0");
                }
                //其他平均分
                if (otherScore.size() > 0) {
                    questionScoreDto.setOtherScore(String.format("%.2f", Double.parseDouble(NumUtils.device(otherSum, otherScore.size()))));
                } else {
                    questionScoreDto.setOtherScore("0");
                }
            }
            resultList.add(questionScoreDto);
        }
        return resultList;
    }

    @Override
    public List<ReportDto> get360Project() {
        List<ReportDto> reportDtoList = new ArrayList<>();
        List<SurProject> surProjectList = projectMapper.selectList(new LambdaQueryWrapper<SurProject>().eq(SurProject::getType, "360度评估"));
        for (SurProject surProject : surProjectList) {
            ReportDto reportDto = new ReportDto();
            reportDto.setValue(surProject.getId());
            reportDto.setText(surProject.getProjectName());
            reportDto.setTitle(surProject.getProjectName());
            reportDtoList.add(reportDto);
        }
        return reportDtoList;
    }

    @Override
    public List<ReportDto> getEvaluated(String id) {
        List<ReportDto> reportDtoList = new ArrayList<>();
        this.list(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getProjectId, id)).forEach(surUserResult -> {
            ReportDto reportDto = new ReportDto();
            reportDto.setValue(surUserResult.getUserId());
            reportDto.setText(surUserResult.getUserName());
            reportDto.setTitle(surUserResult.getUserName());
            reportDtoList.add(reportDto);
        });
        return reportDtoList;
    }

    private void setEvaluationUser(List<String> idList,SurUser user,Evaluation evaluation,Integer level){
        //添加评价人员关系
        idList.forEach(evaluatorId ->{
            //根据评价人id查询评价人
            SurUser evaluator = userMapper.selectById(evaluatorId);
            //添加评价人员关系
            SurEvaluationUser evaluationUser = new SurEvaluationUser();
            evaluationUser.setUserId(user.getId());
            evaluationUser.setUserName(user.getName());
            evaluationUser.setProjectId(evaluation.getProjectId());
            evaluationUser.setEvaluatorId(evaluator.getId());
            evaluationUser.setEvaluatorName(evaluator.getName());
            evaluationUser.setEvaluatorLevel(level);
            evaluationUserMapper.insert(evaluationUser);
        });
    }
    //根据项目id查询同事的人数和得分
    private Map<String, Object> getLevelScore(EvaluatedInfoReq req) {
        Map<String, Object> map = new HashMap<>();
        SurEvaluationRelationship relationship = this.getOne(new LambdaQueryWrapper<SurEvaluationRelationship>().eq(SurEvaluationRelationship::getUserId, req.getEvaluatedId()).eq(SurEvaluationRelationship::getProjectId, req.getProjectId()));
        Map<String, Object> superiorMap = new HashMap<>();
        Map<String, Object> colleagueMap = new HashMap<>();
        Map<String, Object> subordinateMap = new HashMap<>();
        Map<String, Object> otherMap = new HashMap<>();
        List<List<SurUserResult>> superiorScore = new ArrayList<>();
        List<List<SurUserResult>> colleagueScore = new ArrayList<>();
        List<List<SurUserResult>> subordinateScore = new ArrayList<>();
        List<List<SurUserResult>> otherScore = new ArrayList<>();
        int superiorSum = 0;
        int colleagueSum = 0;
        int subordinateSum = 0;
        int otherSum = 0;
        List<List<SurUserResult>> deviceUserResultList = new ArrayList<>();
        int sum = 0;
        //计算总体平均分
        List<SurUserResult> allResults = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()));
        List<String> allIds = allResults.stream().map(SurUserResult::getUserId).distinct().collect(Collectors.toList());
        for (String allId : allIds) {
            List<SurUserResult> allResult = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getUserId, allId));
            deviceUserResultList.add(allResult);
            for (SurUserResult surUserResult : allResult) {
                sum += Integer.parseInt(surUserResult.getBasicScore().toString());
            }
            if (deviceUserResultList.size() > 0) {
                map.put("average", String.format("%.2f", Double.parseDouble(NumUtils.device(sum, deviceUserResultList.size()))));
            } else {
                map.put("average", "0");
            }
        }
        //如果已经设置上下级的权重
        if (null != relationship) {
            //上级
            List<String> superiorIdList = relationship.getSuperiorId();
            for (String superiorId : superiorIdList) {
                List<SurUserResult> superiorOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, superiorId));
                superiorScore.add(superiorOneScore);
                for (SurUserResult surUserResult : superiorOneScore) {
                    superiorSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
            }
            //同事
            List<String> colleagueIdList = relationship.getColleagueId();
            for (String colleagueId : colleagueIdList) {
                List<SurUserResult> colleagueOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, colleagueId));
                colleagueScore.add(colleagueOneScore);
                for (SurUserResult surUserResult : colleagueOneScore) {
                    colleagueSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
            }
            //下级
            List<String> subordinateIdList = relationship.getSubordinateId();
            for (String subordinateId : subordinateIdList) {
                List<SurUserResult> subordinateOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, subordinateId));
                subordinateScore.add(subordinateOneScore);
                for (SurUserResult surUserResult : subordinateOneScore) {
                    subordinateSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
            }
            //其他
            List<String> otherIdList = relationship.getOtherId();
            for (String otherId : otherIdList) {
                List<SurUserResult> otherOneScore = userResultMapper.selectList(new LambdaQueryWrapper<SurUserResult>().eq(SurUserResult::getProjectId, req.getProjectId()).eq(SurUserResult::getEvaluatedId, req.getEvaluatedId()).eq(SurUserResult::getUserId, otherId));
                otherScore.add(otherOneScore);
                for (SurUserResult surUserResult : otherOneScore) {
                    otherSum += Integer.parseInt(surUserResult.getBasicScore().toString());
                }
            }
            //上级人数
            superiorMap.put("num",superiorScore.size());
            //同事人数
            colleagueMap.put("num",colleagueScore.size());
            //下级人数
            subordinateMap.put("num",subordinateScore.size());
            //其他人数
            otherMap.put("num",otherScore.size());
            //上级平均分
            if (superiorScore.size() > 0) {
                superiorMap.put("score", Double.parseDouble(NumUtils.device(superiorSum, superiorScore.size())));
            } else {
                superiorMap.put("score", 0);
            }
            //同事平均分
            if (colleagueScore.size() > 0) {
                colleagueMap.put("score", Double.parseDouble(NumUtils.device(colleagueSum, colleagueScore.size())));
            } else {
                colleagueMap.put("score", 0);
            }
            //下级平均分
            if (subordinateScore.size() > 0) {
                subordinateMap.put("score", Double.parseDouble(NumUtils.device(subordinateSum, subordinateScore.size())));
            } else {
                subordinateMap.put("score", 0);
            }
            //其他平均分
            if (otherScore.size() > 0) {
                otherMap.put("score", Double.parseDouble(NumUtils.device(otherSum, otherScore.size())));
            } else {
                otherMap.put("score", 0);
            }

        }
        map.put("superior",superiorMap);
        map.put("colleague",colleagueMap);
        map.put("subordinate",subordinateMap);
        map.put("other",otherMap);
        return map;
    }

}

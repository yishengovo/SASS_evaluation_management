package org.jeecg.modules.survey.survey.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.survey.survey.entity.SurProjectSurvey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.survey.survey.entity.SurProjectUser;
import org.jeecg.modules.survey.survey.entity.SurResult;

/**
* @author 12602
* @description 针对表【sur_project_survey(项目和问卷的关系表)】的数据库操作Mapper
* @createDate 2022-07-01 12:16:22
* @Entity org.jeecg.modules.demo.survey.entity.SurProjectSurvey
*/
public interface SurProjectSurveyMapper extends BaseMapper<SurProjectSurvey> {

    SurResult selectOne(LambdaQueryWrapper<SurProjectUser> eq);
}





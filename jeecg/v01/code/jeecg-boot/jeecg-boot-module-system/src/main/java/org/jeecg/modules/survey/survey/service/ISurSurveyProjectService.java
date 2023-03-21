package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.SurSurveyProject;
import org.jeecg.modules.survey.survey.req.EmptySurveyReq;
import org.jeecg.modules.survey.survey.req.SaveJsonReq;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 存放项目复制出来的问卷表
 * @Author: jeecg-boot
 * @Date:   2022-08-17
 * @Version: V1.0
 */
public interface ISurSurveyProjectService extends IService<SurSurveyProject> {
    //新建空白问卷
    @Transactional(rollbackFor = Exception.class)
    Boolean createSurvey(EmptySurveyReq req);
    //根据项目id查询问卷
    SurSurveyProject getSurveyByProjectId(String projectId);
    //保存预览问卷的json
    @Transactional(rollbackFor = Exception.class)
    Boolean saveJsonPreview(SaveJsonReq req);
    //根据问卷id查询问题和选项
    List<SurveyDto> getQuestionAndChoice(String id);

}

package org.jeecg.modules.survey.survey.service;

import org.jeecg.modules.survey.survey.dto.SurveyDto;
import org.jeecg.modules.survey.survey.entity.Survey;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.req.SaveJsonReq;
import org.jeecg.modules.survey.survey.req.SurveyCreateReq;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: survey
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
public interface ISurveyService extends IService<Survey> {
    //新建问卷
    @Transactional(rollbackFor = Exception.class)
    Boolean createSurvey(SurveyCreateReq req);
   //保存预览问卷的json
    Boolean saveJsonPreview(SaveJsonReq req);
   //根据问卷id查询问题和选项
    List<SurveyDto> getQuestionAndChoice(String id);
    //根据问卷id查询积分
    String getSurveyCredit(String id);
}

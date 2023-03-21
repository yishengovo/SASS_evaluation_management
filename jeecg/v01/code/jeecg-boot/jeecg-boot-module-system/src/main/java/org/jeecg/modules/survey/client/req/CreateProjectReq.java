package org.jeecg.modules.survey.client.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "CreateProjectReq", description = "创建项目的请求类")
public class CreateProjectReq {

    //项目id
    private String id;
    /**
     * 项目名称
     */

    private String name;
    /**
     * 项目描述
     */
    private String content;
    /**
     * 项目负责人
     */

    private String leader;
    /**
     * 项目类型
     */

    private String type;
    /**
     * 0为未发布 1未发布
     */

    private Boolean isPublish;
    /**
     * 0为无上下级的评价关系模式 1为有上下级的模式
     */

    private Integer evaluationType;
    /**
     * 复制模板后的问卷id
     */

    private String surveyCurrentId;
    /**
     * 源问卷模板的id
     */

    private String surveySrcId;
    /**
     * 项目选择答题的用户数
     */

    private Integer selectNumber;
    /**
     * 已经提交过的问卷数
     */

    private Integer collectNumber;
    /**
     * 存放问卷模板数组的索引
     */

    private String rowKeys;
    /**
     * 存放部门用户的rowkeys
     */

    private String userRowKeys;
    //问卷id
    private String surveyId;
    //预览json
    private String jsonPreview;
    //问题和选项
    private List<SurveyQuestionReq> question;
}

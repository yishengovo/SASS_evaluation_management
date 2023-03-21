package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

@Data
@ApiModel(value = "ProjectEditReq", description = "修改project的请求类")
public class ProjectEditReq {
    //项目id
    private String id;
    //项目负责人
    private String leader;
    //部门
    private String orgUid;
    //项目名字
    private String projectName;
    //问卷id
    private String surveyId;
    //问卷json
    private String surveyJson;
    //所属租户
    private String tenantUid;
    //rowkeys
    private List<Long> rowKeys;
    //部门用户的索引
    private List<Object> userRowKeys;
    //部门用户数组
    private List<SysUser> depUserList;
    //用户id
    private List<SurUser> userList;
}

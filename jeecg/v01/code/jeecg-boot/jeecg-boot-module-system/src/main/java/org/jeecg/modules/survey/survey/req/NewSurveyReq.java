package org.jeecg.modules.survey.survey.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.jeecg.modules.survey.survey.entity.SurProject;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

@Data
@ApiModel( value = "NewSurveyReq", description = "新建项目请求参数" )
public class NewSurveyReq {
    //项目
    private SurProject surProject;
    //项目id
    private String id;
    //问卷模板id
    private String surveyId;
    //问卷模板数组的索引
    private List<Object> rowKeys;
    //部门用户的索引
    private List<Object> userRowKeys;
    //用户数组
    private List<SurUser> userList;
    //部门用户数组
    private List<SysUser> depUserList;
    //创建人
    private String userName;
    //问卷用户数
    private Integer selectNumber;
    //问卷类型(调查、测评、360度)
    private String type;
//    存放修改问卷后的json
    private String surveyJson;

}

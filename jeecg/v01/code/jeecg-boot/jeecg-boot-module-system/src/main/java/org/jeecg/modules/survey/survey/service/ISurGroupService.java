package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.entity.SurGroup;

import java.util.List;

public interface ISurGroupService extends IService<SurGroup> {
    /*新建群组*/
    Boolean addGroup(String name);
    /*修改群组名*/
    Boolean updateGroup(String id, String name);
    /*删除群组*/
    Boolean deleteGroup(String id);
    /*查询群组*/
    List<SurGroup> getGroups();
    /*通过群组id获取群组名*/
    String getGroupNameById(String id);
}

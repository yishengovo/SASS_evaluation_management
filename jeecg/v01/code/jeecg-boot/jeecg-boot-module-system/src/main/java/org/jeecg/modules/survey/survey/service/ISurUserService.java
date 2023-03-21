package org.jeecg.modules.survey.survey.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.survey.survey.entity.SurUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.survey.req.PageReq;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 保存填写问卷的用户表
 * @Author: jeecg-boot
 * @Date:   2022-07-12
 * @Version: V1.0
 */
public interface ISurUserService extends IService<SurUser> {
     //根据部门id查询对应部门的员工
    Page<SysUser> getUserListByDep(PageReq req);
}

package org.jeecg.modules.system.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.system.entity.SysUserRole;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRole> {
    public void insertSysUserRole(SysUserRole sysUserRole);

    //通过用户名获取角色代码
    public String getRoleCodeByUserId(String userId);

    /*通过角色代码获取用户群*/
    IPage<SysUserRole> getUserIdsByRoleCode(String roleCode, Integer pageSize, Integer pageNo);

    /*通过用户id删除数据*/
    Boolean deleteByUserId(String userId);
}

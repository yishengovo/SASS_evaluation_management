package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.mapper.SysRoleMapper;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.service.ISysRoleService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    SysRoleMapper sysRoleMapper;

    @Autowired
    ISysRoleService sysRoleService;

    @Override
    public void insertSysUserRole(SysUserRole sysUserRole) {
        sysUserRoleMapper.insert(sysUserRole);
    }

    @Override
    public String getRoleCodeByUserId(String userId) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        SysUserRole sysUserRole = sysUserRoleMapper.selectOne(queryWrapper);

        SysRole sysRole = sysRoleMapper.selectById(sysUserRole.getRoleId());


        return sysRole.getRoleCode();
    }

    @Override
    public IPage<SysUserRole> getUserIdsByRoleCode(String roleCode, Integer pageSize, Integer pageNo){
        //获取角色代码对应的id
        String roleId = sysRoleService.getRoleIdByRoleCode(roleCode);
        //获取该角色代码的数据
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        IPage<SysUserRole> page = new Page<>(pageNo, pageSize);
        sysUserRoleMapper.selectPage(page, queryWrapper);
        return sysUserRoleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Boolean deleteByUserId(String userId) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return sysUserRoleMapper.delete(queryWrapper)>0;
    }
}

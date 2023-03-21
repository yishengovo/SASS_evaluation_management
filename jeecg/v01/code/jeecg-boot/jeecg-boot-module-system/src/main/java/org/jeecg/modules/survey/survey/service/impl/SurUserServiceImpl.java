package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.survey.survey.mapper.SurUserMapper;
import org.jeecg.modules.survey.survey.req.PageReq;
import org.jeecg.modules.survey.survey.service.ISurUserService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.mapper.SysUserDepartMapper;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 保存填写问卷的用户表
 * @Author: jeecg-boot
 * @Date:   2022-07-12
 * @Version: V1.0
 */
@Service
public class SurUserServiceImpl extends ServiceImpl<SurUserMapper, SurUser> implements ISurUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserDepartMapper userDepartMapper;
    @Override
    public Page<SysUser> getUserListByDep(PageReq req) {
        //根据部门id查询 部门和用户关系列表 并转换为用户id列表
        //如果没有id 则查所有部门的用户
        if (req.getId() == null){
            Page<SysUser> sysUserPage = userMapper.selectPage(new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<SysUser>().ne(SysUser::getUserIdentity, 2));
            return sysUserPage;
        }
        List<String> userIdList = userDepartMapper.selectList(new LambdaQueryWrapper<SysUserDepart>().eq(SysUserDepart::getDepId, req.getId())).stream()
                .map(SysUserDepart::getUserId).collect(Collectors.toList());
        //根据id数组查询用户
        //如果userIdList为空
        if (userIdList.isEmpty()){
            return new Page<>(req.getPageNum(), req.getPageSize());
        }
        Page<SysUser> userPage = userMapper.selectPage(new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIdList));
        return userPage;
    }
}

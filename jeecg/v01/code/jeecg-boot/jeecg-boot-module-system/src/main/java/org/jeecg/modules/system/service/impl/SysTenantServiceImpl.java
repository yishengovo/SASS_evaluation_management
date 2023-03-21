package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysTenantMapper;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 租户实现类
 * @author: jeecg-boot
 */
@Service("sysTenantServiceImpl")
@Slf4j
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Autowired
    ISysUserService userService;
    @Autowired
    SysTenantMapper sysTenantMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SysTenant> queryEffectiveTenant(Collection<Integer> idList) {
        LambdaQueryWrapper<SysTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysTenant::getId, idList);
        queryWrapper.eq(SysTenant::getStatus, Integer.valueOf(CommonConstant.STATUS_1));
        //此处查询忽略时间条件
        return super.list(queryWrapper);
    }

    @Override
    public Long countUserLinkTenant(String id) {
        LambdaQueryWrapper<SysUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SysUser::getRelTenantIds, id);
        userQueryWrapper.or().like(SysUser::getRelTenantIds, "%," + id);
        userQueryWrapper.or().like(SysUser::getRelTenantIds, id + ",%");
        userQueryWrapper.or().like(SysUser::getRelTenantIds, "%," + id + ",%");
        // 查找出已被关联的用户数量
        return userService.count(userQueryWrapper);
    }

    @Override
    public boolean removeTenantById(String id) {
        // 查找出已被关联的用户数量
        Long userCount = this.countUserLinkTenant(id);
        if (userCount > 0) {
            throw new JeecgBootException("该租户已被引用，无法删除！");
        }
        return super.removeById(Integer.parseInt(id));
    }

    @Override
    public String existRealmName(String realmName) {
        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realm_name", realmName);
        SysTenant sysTenant = sysTenantMapper.selectOne(queryWrapper);
        if(sysTenant==null){
            return null;
        }else {
            return sysTenant.getName();
        }

    }

    @Override
    public boolean bindRealName(String id, String realmName) {
        SysTenant sysTenant = sysTenantMapper.selectById(id);
        sysTenant.setRealmName(realmName);
        return sysTenantMapper.updateById(sysTenant)>0;
    }

    @Override
    public String cancelBindRealmName(String id) {
        SysTenant sysTenant = sysTenantMapper.selectById(id);
        String realmName = sysTenant.getRealmName();
        sysTenant.setRealmName("");
        if(sysTenantMapper.updateById(sysTenant)>0){
            return realmName;
        }else {
            return null;
        }

    }

    @Override
    public Integer getIdByRealmName(String realmName) {
        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realm_name", realmName);
        SysTenant sysTenant = sysTenantMapper.selectOne(queryWrapper);
        if(sysTenant==null){
            return null;
        }else {
            return sysTenant.getId();
        }
    }

    @Override
    public String getRealmNameById(String id) {
        SysTenant sysTenant = sysTenantMapper.selectById(id);

        return sysTenant.getRealmName();
    }

    @Override
    public Boolean updateNameById(Integer id, String name) {
        SysTenant sysTenant = sysTenantMapper.selectById(id);
        sysTenant.setName(name);
        return sysTenantMapper.updateById(sysTenant)>0;
    }

    @Override
    public String getUserNameByHost(String host) {
        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realm_name", host);
        SysTenant sysTenant = sysTenantMapper.selectOne(queryWrapper);
        if(sysTenant==null){
            return null;
        }else {
            String userName = userService.getUserNameByTenantId(String.valueOf(sysTenant.getId()));
            return userName;
        }

    }

    @Override
    public Boolean addRealmNameToRedis() {
        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
        List<SysTenant> sysTenantList = sysTenantMapper.selectList(queryWrapper);
        for (SysTenant i :
                sysTenantList) {
            if(oConvertUtils.isNotEmpty(i.getRealmName())){
                if(oConvertUtils.isNotEmpty(redisUtil.get(i.getRealmName()))){
                    return true;
                }else {
                    break;
                }
            }
        }
        sysTenantList.stream().forEach(i->{
            if(oConvertUtils.isNotEmpty(i.getRealmName())){
                String userName = userService.getUserNameByTenantId(String.valueOf(i.getId()));
                redisUtil.set(i.getRealmName(), userName);
            }
        });
        return true;
    }

}

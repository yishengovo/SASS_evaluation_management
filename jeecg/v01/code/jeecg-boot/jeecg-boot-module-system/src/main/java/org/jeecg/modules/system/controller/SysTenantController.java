package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 租户配置信息
 * @author: jeecg-boot
 */
@Slf4j
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {

    @Autowired
    private ISysTenantService sysTenantService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ISysUserService sysUserService;

    /**
     * 获取列表数据
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/TenantList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysTenant>> queryPageList(SysTenant sysTenant,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysTenant>> result = new Result<IPage<SysTenant>>();
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
        Date beginDate=null;
        Date endDate=null;
        if(oConvertUtils.isNotEmpty(sysTenant)) {
            beginDate=sysTenant.getBeginDate();
            endDate=sysTenant.getEndDate();
            sysTenant.setBeginDate(null);
            sysTenant.setEndDate(null);
        }
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
        QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
        if(oConvertUtils.isNotEmpty(sysTenant)){
            queryWrapper.ge(oConvertUtils.isNotEmpty(beginDate),"begin_date",beginDate);
            queryWrapper.le(oConvertUtils.isNotEmpty(endDate),"end_date",endDate);
        }
        //---author:zhangyafei---date:20210916-----for: 租户管理添加日期范围查询---
		Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
		IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysTenant> add(@RequestBody SysTenant sysTenant) {
        Result<SysTenant> result = new Result();
        if(sysTenantService.getById(sysTenant.getId())!=null){
            return result.error500("该编号已存在!");
        }
        try {
            sysTenantService.save(sysTenant);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param
     * @return
     */
    @RequestMapping(value = "/edit", method ={RequestMethod.PUT, RequestMethod.POST})
    public Result<SysTenant> edit(@RequestBody SysTenant tenant) {
        Result<SysTenant> result = new Result();
        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if(sysTenant==null) {
           return result.error500("未找到对应实体");
        }
        boolean ok = sysTenantService.updateById(tenant);
        if(ok) {
            result.success("修改成功!");
        }
        return result;
    }

    /**
     *   通过id删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method ={RequestMethod.DELETE, RequestMethod.POST})
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        sysTenantService.removeTenantById(id);
        return Result.ok("删除成功");
    }

    /**
     *  批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<?> result = new Result<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.error500("未选中租户！");
        }else {
            String[] ls = ids.split(",");
            // 过滤掉已被引用的租户
            List<Integer> idList = new ArrayList<>();
            for (String id : ls) {
                Long userCount = sysTenantService.countUserLinkTenant(id);
                if (userCount == 0) {
                    idList.add(Integer.parseInt(id));
                }
            }
            if (idList.size() > 0) {
                sysTenantService.removeByIds(idList);
                if (ls.length == idList.size()) {
                    result.success("删除成功！");
                } else {
                    result.success("部分删除成功！（被引用的租户无法删除）");
                }
            }else  {
                result.error500("选择的租户都已被引用，无法删除！");
            }
        }
        return result;
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysTenant> queryById(@RequestParam(name="id",required=true) String id) {
        Result<SysTenant> result = new Result<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(id);
        if(sysTenant==null) {
            result.error500("未找到对应实体");
        }else {
            result.setResult(sysTenant);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查询有效的 租户数据
     * @return
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Result<List<SysTenant>> queryList(@RequestParam(name="ids",required=false) String ids) {
        Result<List<SysTenant>> result = new Result<List<SysTenant>>();
        LambdaQueryWrapper<SysTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysTenant::getStatus, 1);
        if(oConvertUtils.isNotEmpty(ids)){
            query.in(SysTenant::getId, ids.split(","));
        }
        //此处查询忽略时间条件
        List<SysTenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }
    /**
     *  查询当前用户的所有有效租户 【当前用于vue3版本】
     * @return
     */
    @RequestMapping(value = "/getCurrentUserTenant", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserTenant() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String tenantIds = sysUser.getRelTenantIds();
            Map<String,Object> map = new HashMap(5);
            if (oConvertUtils.isNotEmpty(tenantIds)) {
                List<Integer> tenantIdList = new ArrayList<>();
                for(String id: tenantIds.split(",")){
                    tenantIdList.add(Integer.valueOf(id));
                }
                // 该方法仅查询有效的租户，如果返回0个就说明所有的租户均无效。
                List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
                map.put("list", tenantList);
            }
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }

    //域名是否存在
    @GetMapping("/existRealmName")
    public Result<String> existRealmName(HttpServletRequest request,@RequestParam(name = "realmName", required = false) String realmName){
        Result<String> result = new Result<>();
        if(oConvertUtils.isEmpty(realmName)){
            realmName = request.getServerName();
        }
        String companyName = sysTenantService.existRealmName(realmName);
        if(oConvertUtils.isEmpty(companyName)){
            result.setSuccess(false);
            result.setCode(413);
            result.setMessage("不存在这个租户域名");
            return result;
        }else {
            return result.ok(companyName);
        }

    }

    @PostMapping("/bindRealName")
    public Result<String> bindRealmName(HttpServletRequest request,@RequestBody JSONObject jsonObject){
        Result<String> result = new Result<>();
        String realmName = jsonObject.getString("realmName").toLowerCase();

        String id = null;
        if(oConvertUtils.isEmpty(id)){
            id = request.getHeader(CommonConstant.TENANT_ID);
        }
        if(oConvertUtils.isNotEmpty(sysTenantService.existRealmName(realmName))){
            result.error500("该域名已存在，请更换域名");
            result.setSuccess(false);
            return result;
        }
        String beforeRealmName = sysTenantService.getRealmNameById(id);
        if(sysTenantService.bindRealName(id,realmName)){
            if (oConvertUtils.isNotEmpty(beforeRealmName)) {
                redisUtil.del(beforeRealmName);
            }
            String userName = sysUserService.getUserNameByTenantId(id);
            redisUtil.set(realmName,userName);
            result.setSuccess(true);
            return result.ok("绑定域名成功");
        }else {
            result.error500("绑定失败！");
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * @description: 取消绑定租户个性化域名,如果取消成功则在redis删除对应的记录，否则返回取消绑定失败
     * @author: GGB
     * @date: 2022/10/28 23:04
     * @param: request
     * @param: id
     * @return: Result<String>
     **/
    @PutMapping("cancelBindRealmName")
    public Result<String> cancelBindRealmName(HttpServletRequest request){
        Result<String> result = new Result<>();
        String id = null;
        if(oConvertUtils.isEmpty(id)){
            id = request.getHeader(CommonConstant.TENANT_ID);
        }
        String realmName = sysTenantService.cancelBindRealmName(id);
        if(oConvertUtils.isEmpty(realmName)){
            result.error500("取消绑定失败");
            result.setSuccess(false);
            return result;

        }else {
            redisUtil.del(realmName);
            result.setSuccess(true);
            return result.ok("取消绑定成功!");
        }
    }

    @PutMapping("updateCompanyName")
    public Result<String> updateCompanyName(HttpServletRequest request, @RequestBody JSONObject jsonObject){
        Result<String> result = new Result<>();
        String companyName = jsonObject.getString("companyName");
        String id = request.getHeader(CommonConstant.TENANT_ID);
        if(oConvertUtils.isEmpty(companyName)){
            result.error500("公司名不能为空！");
            result.setSuccess(false);
            return result;
        }
        if(oConvertUtils.isEmpty(id)){
            result.error500("租户id为空！");
            result.setSuccess(false);
            return result;
        }
        if(sysTenantService.updateNameById(Integer.valueOf(id),companyName)){
            return result.ok("修改公司名成功");
        }else {
            result.error500("修改公司名失败！");
            result.setSuccess(false);
            return result;
        }
    }

    @GetMapping("addRealmNameToRedis")
    public Result<String> addRealmNameToRedis(){
        sysTenantService.addRealmNameToRedis();
        return Result.ok();
    }

    @PutMapping("updateUserIntegral")
    public Result<String> updateUserIntegral(@RequestBody JSONObject jsonObject){
        String userId = jsonObject.getString("userId");
        Integer integral = jsonObject.getInteger("integral");
        if(integral==null||userId==null){
            return Result.error("请求参数不能为null");
        }
        if(sysUserService.putUserIntegral(userId, integral)){
            return Result.ok();
        }else {
            return Result.error("修改失败");
        }
    }
}

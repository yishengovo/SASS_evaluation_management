package org.jeecg.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;

import javax.servlet.http.HttpServletRequest;

/**
 * 多租户 tenant_id存储器
 * @author: jeecg-boot
 */
@Slf4j
public class TenantContext {
    private    static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public    static void setTenant(String tenant) {
        log.debug(" setting tenant to " + tenant);
        System.out.println("设置当前租户的id："+tenant);
        currentTenant.set(tenant);
    }

    public  static String getTenant() {
        if(currentTenant.get()==null){
            System.out.println();
        }
        System.out.println("获取当前租户的id:"+currentTenant.get());

        return currentTenant.get();


    }

    public static void clear(){
        currentTenant.remove();
    }
}
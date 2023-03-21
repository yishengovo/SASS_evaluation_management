package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysTenant;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 租户service接口
 * @author: jeecg-boot
 */
public interface ISysTenantService extends IService<SysTenant> {

    /**
     * 查询有效的租户
     *
     * @param idList
     * @return
     */
    List<SysTenant> queryEffectiveTenant(Collection<Integer> idList);

    /**
     * 返回某个租户被多少个用户引用了
     *
     * @param id
     * @return
     */
    Long countUserLinkTenant(String id);

    /**
     * 根据ID删除租户，会判断是否已被引用
     *
     * @param id
     * @return
     */
    boolean removeTenantById(String id);

    //该租户域名是否存在
    String existRealmName(String realmName);

    //绑定新域名
    boolean bindRealName(String id, String realmName);

    //取消绑定域名
    String cancelBindRealmName(String id);

    //通过域名获取租户id
    Integer getIdByRealmName(String realmName);

    //通过id获取域名
    String getRealmNameById(String id);

    //通过Id修改公司名
    Boolean updateNameById(Integer id, String name);

    //通过域名返回用户名
    String getUserNameByHost(String host);

    //如果服务器重启，redis数据为Null后执行该方法
    Boolean addRealmNameToRedis();
}

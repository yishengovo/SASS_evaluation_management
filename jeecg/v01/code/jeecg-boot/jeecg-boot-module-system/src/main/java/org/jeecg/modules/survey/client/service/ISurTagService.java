package org.jeecg.modules.survey.client.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.client.entity.SurTag;
import org.jeecg.modules.survey.client.req.TagQueryReq;

/**
 * @Description: 问卷标签表
 * @Author: jeecg-boot
 * @Date: 2023-03-23
 * @Version: V1.0
 */
public interface ISurTagService extends IService<SurTag> {
    // 条件分页查询
    Page<SurTag> queryPage(TagQueryReq req);
}
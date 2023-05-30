package org.jeecg.modules.survey.client.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.survey.client.entity.SurTag;
import org.jeecg.modules.survey.client.mapper.SurTagMapper;
import org.jeecg.modules.survey.client.req.TagQueryReq;
import org.jeecg.modules.survey.client.service.ISurTagService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 问卷标签表
 * @Author: jeecg-boot
 * @Date:   2023-03-23
 * @Version: V1.0
 */
@Service
public class SurTagServiceImpl extends ServiceImpl<SurTagMapper, SurTag> implements ISurTagService {

    @Override
    public Page<SurTag> queryPage(TagQueryReq req) {
        LambdaQueryWrapper<SurTag> wrapper = new LambdaQueryWrapper<>();
        if(req.getTagName() != null && StringUtils.isNotEmpty(req.getTagName())) {
            wrapper.like(SurTag::getTagName, req.getTagName());
        }
        if(req.getDescription() != null && StringUtils.isNotEmpty(req.getDescription())) {
            wrapper.like(SurTag::getDescription, req.getDescription());
        }
        if(req.getStatus() != null && StringUtils.isNotEmpty(req.getStatus())) {
            wrapper.like(SurTag::getStatus, req.getStatus());
        }
        wrapper.orderByDesc(SurTag::getCreateTime);
        return page(new Page<>(req.getPageNum(), req.getPageSize()), wrapper);
    }
}
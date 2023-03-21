package org.jeecg.modules.survey.client.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.client.entity.SurTopUp;
import org.jeecg.modules.survey.client.mapper.SurTopUpMapper;
import org.jeecg.modules.survey.client.service.ISurTopUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-30  15:31
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurTopUpServiceImpl extends ServiceImpl<SurTopUpMapper, SurTopUp> implements ISurTopUpService {
    @Autowired
    SurTopUpMapper surTopUpMapper;

    @Override
    @Transactional
    public Boolean addTopUp(Integer integral) {
        QueryWrapper<SurTopUp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable",true);
        SurTopUp surTopUpAgo = surTopUpMapper.selectOne(queryWrapper);
        if(surTopUpAgo!=null){
            surTopUpAgo.setEnable(false);
            surTopUpMapper.updateById(surTopUpAgo);
        }
        SurTopUp surTopUp = new SurTopUp();
        surTopUp.setIntegral(integral);
        surTopUp.setEnable(true);

        return surTopUpMapper.insert(surTopUp)>0;
    }

    @Override
    public Integer getTopUp() {
        QueryWrapper<SurTopUp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable",true);
        SurTopUp surTopUp = surTopUpMapper.selectOne(queryWrapper);
        return surTopUp.getIntegral();
    }

    @Override
    public SurTopUp getSurTopUp() {
        QueryWrapper<SurTopUp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable",true);
        SurTopUp surTopUp = surTopUpMapper.selectOne(queryWrapper);
        return surTopUp;
    }
}

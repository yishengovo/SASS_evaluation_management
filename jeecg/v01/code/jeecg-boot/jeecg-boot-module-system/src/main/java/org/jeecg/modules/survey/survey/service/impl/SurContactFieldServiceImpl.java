package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.entity.SurContactField;
import org.jeecg.modules.survey.survey.mapper.SurContactFieldMapper;
import org.jeecg.modules.survey.survey.service.ISurContactFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-02  20:45
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurContactFieldServiceImpl extends ServiceImpl<SurContactFieldMapper, SurContactField> implements ISurContactFieldService {
    @Autowired
    SurContactFieldMapper surContactFieldMapper;

    @Override
    public Boolean addContactField(String name) {
        SurContactField surContactField = new SurContactField();
        surContactField.setName(name);
        return surContactFieldMapper.insert(surContactField)>0;
    }

    @Override
    public Boolean updateContactField(String id, String name) {
        SurContactField surContactField = surContactFieldMapper.selectById(id);
        surContactField.setName(name);
        return surContactFieldMapper.updateById(surContactField)>0;
    }

    @Override
    public Boolean deleteContactField(String id) {
        return surContactFieldMapper.deleteById(id)>0;
    }

    @Override
    public List<SurContactField> getContactFields() {
        QueryWrapper<SurContactField> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return surContactFieldMapper.selectList(queryWrapper);
    }
}

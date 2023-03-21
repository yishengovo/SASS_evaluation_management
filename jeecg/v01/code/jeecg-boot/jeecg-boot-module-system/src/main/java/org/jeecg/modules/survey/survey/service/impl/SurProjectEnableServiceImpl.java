package org.jeecg.modules.survey.survey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.survey.survey.entity.SurProject;
import org.jeecg.modules.survey.survey.entity.SurProjectEnable;
import org.jeecg.modules.survey.survey.mapper.SurProjectEnableMapper;
import org.jeecg.modules.survey.survey.service.ISurProjectEnableService;
import org.jeecg.modules.survey.survey.service.ISurProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-09  16:42
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurProjectEnableServiceImpl extends ServiceImpl<SurProjectEnableMapper, SurProjectEnable> implements ISurProjectEnableService {
    @Autowired
    SurProjectEnableMapper surProjectEnableMapper;
    @Autowired
    ISurProjectService surProjectService;
    @Override
    public SurProjectEnable getSurProjectEnable(String projectId) {
        QueryWrapper<SurProjectEnable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        return surProjectEnableMapper.selectOne(queryWrapper);
    }

    @Override
    public void deleteProjectEnable(String projectId) {
        QueryWrapper<SurProjectEnable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        surProjectEnableMapper.delete(queryWrapper);
    }


    @Override
    @Transactional
    public Boolean saveProjectEnable(SurProjectEnable surProjectEnable) {
        SurProject surProject = surProjectService.getById(surProjectEnable.getProjectId());

/*        if(!surProject.getType().equals("调查")){
            if(!(surProjectEnable.getTimeEnable()||surProjectEnable.getPasswordEnable()||surProjectEnable.getPhoneCaptchaEnable())){
                surProjectEnable.setPhoneCaptchaEnable(true);
            }
        }*/

        if(surProjectEnable.getTimeEnable()==null){
            surProjectEnable.setTimeEnable(false);
        }
        if (surProjectEnable.getPasswordEnable()==null){
            surProjectEnable.setPasswordEnable(false);
        }
        if(surProjectEnable.getPhoneCaptchaEnable()==null){
            surProjectEnable.setPhoneCaptchaEnable(false);
        }
        //如果存在以前的数据则删除
        QueryWrapper<SurProjectEnable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", surProjectEnable.getProjectId());
        SurProjectEnable surProjectEnable1 = surProjectEnableMapper.selectOne(queryWrapper);
        if(surProjectEnable1!=null){
            surProjectEnableMapper.delete(queryWrapper);
        }
        surProjectEnable.setId(null);

        return surProjectEnableMapper.insert(surProjectEnable)>0;
    }
}

package org.jeecg.modules.survey.client.service.impl;

import org.jeecg.modules.survey.client.entity.UserResult;
import org.jeecg.modules.survey.client.mapper.UserResultMapper;
import org.jeecg.modules.survey.client.service.IUserResultService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户端问卷结果表
 * @Author: jeecg-boot
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Service
public class UserResultServiceImpl extends ServiceImpl<UserResultMapper, UserResult> implements IUserResultService {

}

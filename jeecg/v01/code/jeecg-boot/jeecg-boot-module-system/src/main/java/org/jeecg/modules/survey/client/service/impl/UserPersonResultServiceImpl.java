package org.jeecg.modules.survey.client.service.impl;

import org.jeecg.modules.survey.client.entity.UserPersonResult;
import org.jeecg.modules.survey.client.mapper.UserPersonResultMapper;
import org.jeecg.modules.survey.client.service.IUserPersonResultService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户端填写人每题的答案表
 * @Author: jeecg-boot
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Service
public class UserPersonResultServiceImpl extends ServiceImpl<UserPersonResultMapper, UserPersonResult> implements IUserPersonResultService {

}

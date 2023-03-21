package org.jeecg.modules.survey.client.service.impl;

import org.jeecg.modules.survey.client.entity.UserPerson;
import org.jeecg.modules.survey.client.mapper.UserPersonMapper;
import org.jeecg.modules.survey.client.service.IUserPersonService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户端项目参与者表
 * @Author: jeecg-boot
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Service
public class UserPersonServiceImpl extends ServiceImpl<UserPersonMapper, UserPerson> implements IUserPersonService {

}

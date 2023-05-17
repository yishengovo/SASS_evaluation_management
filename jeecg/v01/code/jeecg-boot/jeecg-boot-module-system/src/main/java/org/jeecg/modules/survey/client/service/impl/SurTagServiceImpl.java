package org.jeecg.modules.survey.client.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.jeecg.modules.survey.client.entity.SurPayment;
import org.jeecg.modules.survey.client.entity.SurTag;
import org.jeecg.modules.survey.client.mapper.SurPaymentMapper;
import org.jeecg.modules.survey.client.mapper.SurTagMapper;
import org.jeecg.modules.survey.client.resp.IntegralModel;
import org.jeecg.modules.survey.client.resp.ReportHome;
import org.jeecg.modules.survey.client.service.ISurPaymentService;
import org.jeecg.modules.survey.client.service.ISurTagService;
import org.jeecg.modules.survey.client.service.ISurTopUpService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 问卷标签表
 * @Author: jeecg-boot
 * @Date:   2023-03-26
 * @Version: V1.0
 */
@Service
public class SurTagServiceImpl extends ServiceImpl<SurTagMapper, SurTag> implements ISurTagService {

}

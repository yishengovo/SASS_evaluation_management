package org.jeecg.modules.survey.client.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.jeecg.modules.survey.client.entity.SurPayment;
import org.jeecg.modules.survey.client.mapper.SurPaymentMapper;
import org.jeecg.modules.survey.client.resp.IntegralModel;
import org.jeecg.modules.survey.client.resp.ReportHome;
import org.jeecg.modules.survey.client.service.ISurPaymentService;
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
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.service.impl
 * @Author: GGB
 * @CreateTime: 2022-11-29  14:44
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SurPaymentServiceImpl extends ServiceImpl<SurPaymentMapper, SurPayment> implements ISurPaymentService {
    @Autowired
    SurPaymentMapper surPaymentMapper;
    @Autowired
    ISurTopUpService surTopUpService;
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    ISysUserRoleService sysUserRoleService;
    @Autowired
    ISysTenantService sysTenantService;



    @Override
    public String saveSurPayment(SurPayment surPayment) {
        surPaymentMapper.insert(surPayment);
        return surPayment.getId();
    }

    @Override
    public String payAsyncNotifyVerificationSign(HttpServletRequest httpServletRequest) {
        /*  WXPay wxPay = new WXPay(myWxPayConfig);*/
        String returnXmlMessage = null;
        String notifyXmlData = null;
        try {
            notifyXmlData = readXmlFromStream(httpServletRequest);

            Map<String, String> notifyMapData = WXPayUtil.xmlToMap(notifyXmlData);
            //logger.info("微信支付成功回调notifyMapData={}", notifyMapData);
            // 验证签名
            /* boolean signatureValid = wxPay.isPayResultNotifySignatureValid(notifyMapData);*/
            // 验证是否支付成功
            if("SUCCESS".equals(notifyMapData.get( "result_code"))){
                // 获取支付成功的订单号
                String out_trade_no = notifyMapData.get("out_trade_no");
                QueryWrapper<SurPayment> queryWrapper = new QueryWrapper();
                queryWrapper.eq("out_trade_no",out_trade_no);
                SurPayment surPayment = surPaymentMapper.selectOne(queryWrapper);
                Integer amount = surPayment.getAmount();
                Integer integral = surTopUpService.getTopUp() * amount;
                String userId = surPayment.getUserId();
                sysUserService.updateIntegral(userId, integral);
                surPayment.setSucceeded(true);
                surPaymentMapper.updateById(surPayment);
                //订单支付成功之后相关业务逻辑...
                //logger.info("支付成功回调订单号out_trade_no={}",out_trade_no);
                // 一切正常返回的xml数据
                returnXmlMessage = setReturnXml(WXPayConstants.SUCCESS, "OK");
                //logger.info("[out_trade_no:{}] [支付成功异步消息处理成功:{}]", notifyMapData.get("out_trade_no"),  WXPayUtil.xmlToMap(returnXmlMessage));
            } else {
                returnXmlMessage = setReturnXml(WXPayConstants.FAIL, "Verification sign failed!");
                //logger.info("[out_trade_no:{}] [验签失败:{}]", notifyMapData.get("out_trade_no"), WXPayUtil.xmlToMap(returnXmlMessage));
            }
        } catch (IOException e) {
            //logger.error("[读取微信服务器返回流中xml数据时发生异常:{}] ", ExceptionUtils.getStackTrace(e));
            returnXmlMessage = setReturnXml(WXPayConstants.FAIL, "An exception occurred while reading the WeChat server returning xml data in the stream.");
        } catch (Exception e) {
            //logger.error("[xml数据:{}] [异常:{}] ", notifyXmlData, ExceptionUtils.getStackTrace(e));

            returnXmlMessage = setReturnXml(WXPayConstants.FAIL, "Payment successful, exception occurred during asynchronous notification processing.");
            //logger.warn("[支付成功异步消息处理失败:{}]", returnXmlMessage);
        }
        return returnXmlMessage;

    }

    @Override
    public SurPayment getSurPayment(String outTradeNo) {
        QueryWrapper<SurPayment> queryWrapper = new QueryWrapper();
        queryWrapper.eq("out_trade_no",outTradeNo);
        SurPayment surPayment = surPaymentMapper.selectOne(queryWrapper);
        return surPayment;
    }

    @Override
    public IPage<SurPayment> getAllOrder(Integer pageSize, Integer pageNo) {
        QueryWrapper<SurPayment> queryWrapper = new QueryWrapper();
        queryWrapper.eq("succeeded",true).orderByDesc("create_time");
        IPage<SurPayment> page = new Page<>(pageNo, pageSize);
        return surPaymentMapper.selectPage(page,queryWrapper);
    }

    @Override
    public ReportHome getReportHomeValue() {
        ReportHome reportHome = new ReportHome();
        /*获取总销售额相关数据*/

        QueryWrapper<SurPayment> salesQueryWrapper = new QueryWrapper<>();
        salesQueryWrapper.select("IFNULL(sum(amount),0) as totalSales").eq("succeeded",true);
        //Map<String, Object> map = surPaymentService.getMap(salesQueryWrapper);



        return null;
    }

    @Override
    public IPage<IntegralModel> getIntegralInfo(Integer pageSize, Integer pageNo) {
        List<IntegralModel> integralModelList = null;

        /*获取saas用户群id*/
        IPage<SysUserRole> sysUserRolePage = sysUserRoleService.getUserIdsByRoleCode("saas", pageSize, pageNo);
        List<SysUserRole> sysUserRoleList = sysUserRolePage.getRecords();

        /*通过用户id获取积分模型数据的信息*/
        integralModelList = sysUserRoleList.stream().map(i->{
            IntegralModel integralModel = new IntegralModel();

            //通过用户id获取对应的用户
            SysUser sysUser = sysUserService.getById(i.getUserId());
            if(sysUser==null){
                sysUserRoleService.deleteByUserId(i.getUserId());
                return integralModel;
            }

            /*设置用户id*/
            integralModel.setUserId(i.getUserId());

            /*获取用户名*/
            integralModel.setUserName(sysUser.getUsername());

            /*通过租户id获取租户名*/
            integralModel.setTenantName(sysTenantService.getById(sysUser.getRelTenantIds()).getName());

            /*获取积分余额*/
            if(sysUser.getIntegral()==null){
                integralModel.setIntegral(0);
            }else {
                integralModel.setIntegral(sysUser.getIntegral());
            }


            /*通过用户id获取已经充值积分*/
            QueryWrapper<SurPayment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", i.getUserId()).eq("succeeded", true).select("sum(integral) as sumAll");
            Map<String, Object> map = this.getMap(queryWrapper);
            if(map == null){
                integralModel.setTopUpIntegraled(0);
            }else {
                String sumAll = String.valueOf(map.get("sumAll"));
                integralModel.setTopUpIntegraled(Integer.valueOf(sumAll));
            }

            /*通过用户id获取上次充值时间*/
            QueryWrapper<SurPayment> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id", i.getUserId()).orderByDesc("create_time").last("limit 1");
            SurPayment surPayment = surPaymentMapper.selectOne(queryWrapper1);
            if(surPayment==null){
                integralModel.setLastTopUp(null);
            }else {
                integralModel.setLastTopUp(surPayment.getCreateTime());
            }
            return integralModel;
        }).collect(Collectors.toList());

        /*设置分页信息*/
        IPage<IntegralModel> integralModelIPage = new Page<>();
        integralModelIPage.setCurrent(sysUserRolePage.getCurrent());
        integralModelIPage.setPages(sysUserRolePage.getPages());
        integralModelIPage.setSize(sysUserRolePage.getSize());
        integralModelIPage.setTotal(sysUserRolePage.getTotal());
        integralModelIPage.setRecords(integralModelList);
        return integralModelIPage;
    }

    /**
     * 从流中读取微信返回的xml数据
     *
     * @param httpServletRequest
     * @return
     * @throws IOException
     */
    private String readXmlFromStream(HttpServletRequest httpServletRequest) throws IOException {
        InputStream inputStream = httpServletRequest.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            bufferedReader.close();
            inputStream.close();
        }

        return sb.toString();
    }
    /**
     * 设置返回给微信服务器的xml信息
     *
     * @param returnCode
     * @param returnMsg
     * @return
     */
    private String setReturnXml(String returnCode, String returnMsg) {
        return "<xml><return_code><![CDATA[" + returnCode + "]]></return_code><return_msg><![CDATA[" + returnMsg + "]]></return_msg></xml>";
    }
}

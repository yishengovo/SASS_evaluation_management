package org.jeecg.modules.survey.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.survey.client.entity.SurPayment;
import org.jeecg.modules.survey.client.resp.IntegralModel;
import org.jeecg.modules.survey.client.resp.ReportHome;

import javax.servlet.http.HttpServletRequest;

public interface ISurPaymentService extends IService<SurPayment> {
    /*增加数据，并返回id*/
    String saveSurPayment(SurPayment surPayment);

    /**
     * 微信支付异步通知验证签名
     * @param httpServletRequest
     * @return
     */
    String payAsyncNotifyVerificationSign(HttpServletRequest httpServletRequest);

    SurPayment getSurPayment(String outTradeNo);

    IPage<SurPayment> getAllOrder(Integer pageSize, Integer pageNo);

    /*获取首页数据*/
    ReportHome getReportHomeValue();

    /*获取积分管理信息*/
    IPage<IntegralModel> getIntegralInfo(Integer pageSize, Integer pageNo);
}

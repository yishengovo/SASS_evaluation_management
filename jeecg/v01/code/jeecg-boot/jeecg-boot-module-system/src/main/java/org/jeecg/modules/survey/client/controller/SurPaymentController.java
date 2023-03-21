package org.jeecg.modules.survey.client.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.client.entity.SurPayment;
import org.jeecg.modules.survey.client.resp.IntegralModel;
import org.jeecg.modules.survey.client.service.ISurPaymentService;
import org.jeecg.modules.survey.client.service.ISurTopUpService;
import org.jeecg.modules.survey.client.utils.CommonUtil;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @BelongsProject: jeecg-boot @BelongsPackage: org.jeecg.modules.demo.client.controller @Author:
 * GGB @CreateTime: 2022-11-29 14:45 @Description: TODO @Version: 1.0
 */
@Api(tags = "支付模块")
@RestController
@RequestMapping("/client/surPayment")
@Slf4j
public class SurPaymentController extends JeecgController<SurPayment, ISurPaymentService> {
  @Autowired ISurPaymentService surPaymentService;
  @Autowired ISysUserService sysUserService;
  @Autowired ISurTopUpService surTopUpService;

  @Value("${payment.WECHAT_PAYMENT.appid}")
  String appid;

  @Value("${payment.WECHAT_PAYMENT.mch_id}")
  String mch_id;

  @Value("${payment.WECHAT_PAYMENT.sign}")
  String sign;

  @Value("${payment.WECHAT_PAYMENT.notify_url}")
  String notify_url;
  /**
   * 生成微信支付二维码
   *
   * @param totalFee 金额(分)
   */
  @GetMapping(value = "/createNative")
  public Result<?> createNative(
      @RequestParam("totalFee") Integer totalFee,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (totalFee <= 0) {
      return Result.error("充值金额不能为负数!");
    }

    // 新增订单支付数据保存在数据库
    String username = JwtUtil.getUserNameByToken(request);
    String userId = sysUserService.getUserByName(username).getId();
    String out_trade_no = IdUtil.simpleUUID();
    SurPayment surPayment = new SurPayment();
    surPayment.setOutTradeNo(out_trade_no);
    surPayment.setAmount(totalFee);
    surPayment.setSucceeded(false);
    surPayment.setProductDescription("福星问卷充值中心-积分充值");
    surPayment.setUserId(userId);
    surPayment.setIntegral(surTopUpService.getTopUp() * totalFee);
    surPayment.setId(surPaymentService.saveSurPayment(surPayment));

    try {
      // todo 创建请求参数
      SortedMap<String, String> req = new TreeMap<String, String>();
      req.put("appid", appid); // 公众号appid
      req.put("attach", "支付测试"); // 公众号appid
      req.put("body", surPayment.getProductDescription()); // 商品描述
      req.put("mch_id", mch_id); // 商户号
      req.put("nonce_str", WXPayUtil.generateNonceStr()); // 32位随机字符串
      req.put("notify_url", notify_url); // 回调地址
      req.put("out_trade_no", out_trade_no); // 商户订单号
      req.put("spbill_create_ip", CommonUtil.getIp(request)); // 终端IP
      req.put("total_fee", String.valueOf(totalFee)); // 标价金额(元)
      req.put("trade_type", "NATIVE"); // 交易类型
      req.put("sign", WXPayUtil.generateSignature(req, sign, WXPayConstants.SignType.MD5)); // 签名
      System.out.println(WXPayUtil.generateSignature(req, sign, WXPayConstants.SignType.MD5));
      req.put("product_id", surPayment.getId()); // 商品id,系统内部订单号
      // 生成要发送的 xml
      String xmlBody = WXPayUtil.generateSignedXml(req, sign);

      // 发送 POST 请求 统一下单 API 并携带 xmlBody 内容,然后获得返回结果
      String result =
          CommonUtil.httpsRequest(
              "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", xmlBody);
      System.out.println(result + "\n=====");
      // 将返回结果从 xml 格式转换为 map 格式
      Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
      Map<String, String> map = new HashMap<>();
      String result_code = resultMap.get("result_code");
      if (result_code.equals("FAIL")) {
        // map.put("err_code_des",resultMap.get("err_code_des"));
        return Result.error(resultMap.get("err_code_des"));
      }
      map.put("code_url", resultMap.get("code_url")); // 支付地址
      map.put("total_fee", String.valueOf(totalFee)); // 总金额
      map.put("out_trade_no", out_trade_no); // 随机生成商户订单号
      //            map.put("status","200");
      return Result.ok(map);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @PostMapping(value = "/callback")
  public Result<?> callback(HttpServletRequest request, HttpServletResponse response) {
    System.out.println("回调接口已执行");
    /*微信支付异步通知验证签名*/
    String resultXml = surPaymentService.payAsyncNotifyVerificationSign(request);
    BufferedOutputStream bufferedOutputStream = null;
    try {
      bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
      bufferedOutputStream.write(resultXml.getBytes());
      bufferedOutputStream.flush();
      bufferedOutputStream.close();
    } catch (IOException e) {
      return Result.error("运行异常!");
    }
    return Result.ok();
  }

  /**
   * 查询订单支付状态
   *
   * @author: fuqin
   * @date: 2022/1/7
   * @param outTradeNo 订单号
   * @return 支付状态
   */
  @GetMapping(value = "/queryOrder")
  public Result<?> queryOrder(@RequestParam("outTradeNo") String outTradeNo) {
    int x = 0;
    // 调用查询微信支付订单状态方法
    Map<String, String> map = this.queryPayStatus(outTradeNo);
    if (map.isEmpty()) {
      return Result.error("支付出错");
    }
    if (map.get("trade_state").equals("SUCCESS")) {
      return Result.ok("支付成功");
    }
    if (map.get("trade_state").equals("CLOSED")) {
      Result result = new Result();
      result.setCode(512);
      result.setSuccess(false);
      result.setMessage("订单已关闭");
      return result;
    }

    Date date = DateUtil.date();
    SurPayment surPayment = surPaymentService.getSurPayment(outTradeNo);
    Date createTime = surPayment.getCreateTime();
    long betweenMinutes = DateUtil.between(date, createTime, DateUnit.MINUTE);
    Result result = new Result();
    if (betweenMinutes >= 5) {
      result.setCode(511);
      result.setSuccess(false);
      result.setMessage("二维码超时！");
      closeOrderMethods(outTradeNo);
      return result;
    }
    result.setCode(513);
    result.setSuccess(false);
    result.setMessage("订单未支付!");
    return result;
  }

  /**
   * 查询微信支付订单状态
   *
   * @author: fuqin
   * @date: 2022/1/7
   * @param outTradeNo 订单号
   * @return 支付状态
   */
  private Map<String, String> queryPayStatus(String outTradeNo) {
    try {
      // todo 创建请求参数
      SortedMap<String, String> req = new TreeMap<String, String>();
      req.put("appid", appid); // 公众号ID
      req.put("mch_id", mch_id); // 商户号
      req.put("out_trade_no", outTradeNo); // 订单号
      req.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机字符串
      req.put("sign", WXPayUtil.generateSignature(req, sign, WXPayConstants.SignType.MD5));
      // 生成要发送的 xml
      String xmlBody = WXPayUtil.generateSignedXml(req, sign);
      // 调用查询订单支付状态 API
      String result =
          CommonUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/orderquery", "POST", xmlBody);
      // 返回解析后的 map 数据 转map
      Map<String, String> map = WXPayUtil.xmlToMap(result);
      return map;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 关闭交易订单
   *
   * @author: fuqin
   * @date: 2022/1/7
   * @param outTradeNo
   * @return 交易关闭状态
   */
  @GetMapping(value = "/closeOrder")
  public Result<?> closeOrder(@RequestParam("outTradeNo") String outTradeNo) {
    Boolean isClose = closeOrderMethods(outTradeNo);
    if (isClose) {
      return Result.ok("订单关闭成功!");
    } else {
      return Result.ok("订单关闭失败!");
    }
  }

  @GetMapping("/queryPayment")
  public Result<SurPayment> queryPayment(@RequestParam String outTradeNo) {
    if (oConvertUtils.isEmpty(outTradeNo)) {
      return Result.error("订单号不能为空!");
    }
    SurPayment surPayment = surPaymentService.getSurPayment(outTradeNo);
    if (surPayment != null) {
      return Result.ok(surPayment);
    } else {
      return Result.error("未查到该订单!");
    }
  }

  @GetMapping("/queryAllOrder")
  public Result<IPage<SurPayment>> queryAllOrder(
      @RequestParam Integer pageNo, @RequestParam Integer pageSize, HttpServletRequest request) {
    /*        String userName = JwtUtil.getUserNameByToken(request);
    SysUser sysUser = sysUserService.getUserByName(userName);*/

    IPage<SurPayment> surPaymentIPage = surPaymentService.getAllOrder(pageSize, pageNo);
    return Result.ok(surPaymentIPage);
  }
  /*
   * @description: 关闭订单
   * @author: GGB
   * @date: 2022/12/1 19:14
   * @param: null
   * @return: null
   **/
  private Boolean closeOrderMethods(String outTradeNo) {
    try {
      SortedMap<String, String> req = new TreeMap<>();
      req.put("appid", appid); // 公众号appid
      req.put("mch_id", mch_id); // 商户号
      req.put("nonce_str", WXPayUtil.generateNonceStr()); // 32位随机字符串
      req.put("out_trade_no", outTradeNo); // 订单号
      req.put("sign", WXPayUtil.generateSignature(req, sign, WXPayConstants.SignType.MD5)); // 签名
      // 生成发送请求的xml
      String xmlBody = WXPayUtil.generateSignedXml(req, sign);
      String result =
          CommonUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/closeorder", "POST", xmlBody);

      // 将返回结果从 xml 格式转换为 map 格式
      Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
      // Map<String, String> map = new HashMap<>();

      String result_code = resultMap.get("result_code");
      if (result_code.equals("FAIL")) {
        // map.put("err_code_des", resultMap.get("err_code_des"));
        log.error("订单关闭失败:" + resultMap.get("err_code_des"));
        return false;
      }
      // map.put("msg", "交易关闭成功");
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 分页列表查询
   *
   * @param surPayment
   * @param pageNo
   * @param pageSize
   * @param req
   * @return
   */
  // @AutoLog(value = "sur_payment-分页列表查询")
  @ApiOperation(value = "sur_payment-分页列表查询", notes = "sur_payment-分页列表查询")
  @GetMapping(value = "/list")
  public Result<IPage<SurPayment>> queryPageList(
      SurPayment surPayment,
      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      HttpServletRequest req) {
    QueryWrapper<SurPayment> queryWrapper =
        QueryGenerator.initQueryWrapper(surPayment, req.getParameterMap());
    queryWrapper.eq("succeeded", true);
    Page<SurPayment> page = new Page<SurPayment>(pageNo, pageSize);
    IPage<SurPayment> pageList = surPaymentService.page(page, queryWrapper);
    return Result.OK(pageList);
  }

  /**
   * 通过id删除
   *
   * @param id
   * @return
   */
  @AutoLog(value = "sur_payment-通过id删除")
  @ApiOperation(value = "sur_payment-通过id删除", notes = "sur_payment-通过id删除")
  @DeleteMapping(value = "/delete")
  public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
    surPaymentService.removeById(id);
    return Result.OK("删除成功!");
  }
  /**
   * 批量删除
   *
   * @param ids
   * @return
   */
  @AutoLog(value = "sur_payment-批量删除")
  @ApiOperation(value = "sur_payment-批量删除", notes = "sur_payment-批量删除")
  @DeleteMapping(value = "/deleteBatch")
  public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
    this.surPaymentService.removeByIds(Arrays.asList(ids.split(",")));
    return Result.OK("批量删除成功!");
  }

  /**
   * 通过id查询
   *
   * @param id
   * @return
   */
  // @AutoLog(value = "sur_payment-通过id查询")
  @ApiOperation(value = "sur_payment-通过id查询", notes = "sur_payment-通过id查询")
  @GetMapping(value = "/queryById")
  public Result<SurPayment> queryById(@RequestParam(name = "id", required = true) String id) {
    SurPayment surPayment = surPaymentService.getById(id);
    if (surPayment == null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(surPayment);
  }

  @ApiOperation(value="查询用户积分", notes="查询用户积分")
  @GetMapping(value = "/queryUserIntegral")
  public Result<?> queryUserIntegral(@RequestParam(name="pageSize",required=true) Integer pageSize, @RequestParam(name="pageNo",required=true) Integer pageNo) {
    IPage<IntegralModel> integralModelIPage = service.getIntegralInfo(pageSize, pageNo);
    if(integralModelIPage==null) {
      return Result.error("未找到对应数据");
    }
    return Result.OK(integralModelIPage);
  }
  /**
   * 导出excel
   *
   * @param request
   * @param surPayment
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SurPayment surPayment) {
    return super.exportXls(request, surPayment, SurPayment.class, "sur_payment");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
    return super.importExcel(request, response, SurPayment.class);
  }
}

package org.jeecg.modules.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.common.util.encryption.EncryptedString;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.message.handle.impl.EmailSendMsgHandle;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.jeecg.modules.system.util.RandImageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	private ISysLogService logService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
    private ISysDepartService sysDepartService;
	@Autowired
	private ISysTenantService sysTenantService;
	@Autowired
    private ISysDictService sysDictService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;
	@Resource
	private BaseCommonService baseCommonService;
	@Autowired
	ISysThirdAccountService sysThirdAccountService;


	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
		//前端密码加密，后端进行密码解密
		//password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

		//update-begin-author:taoyan date:20190828 for:校验验证码
        String captcha = sysLoginModel.getCaptcha();
        if(captcha==null){
            result.error500("验证码无效");
            return result;
        }
        String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+sysLoginModel.getCheckKey(), "utf-8");
		Object checkCode = redisUtil.get(realKey);
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if(checkCode==null || !checkCode.toString().equals(lowerCaseCaptcha)) {
            log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("验证码错误");
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校验验证码
		
		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		System.out.println(userpassword);
		System.out.println(syspassword);
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		//校验改用户是否有权力登录到管理/saas端
		String roleCode = sysUserRoleService.getRoleCodeByUserId(sysUser.getId());
		Integer loginEnd = sysLoginModel.getLoginEnd();
		if(!((roleCode.equals("saas")&&loginEnd==1)||(roleCode.equals("admin")&&loginEnd==0))){
			result.error500("该用户无权登录");
			return result;
		}

		//用户登录信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		redisUtil.del(realKey);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}


	/**
	 * 【vue3专用】获取用户信息
	 */
	@GetMapping("/user/getUserInfo")
	public Result<JSONObject> getUserInfo(HttpServletRequest request){
		Result<JSONObject> result = new Result<JSONObject>();
		String  username = JwtUtil.getUserNameByToken(request);
		if(oConvertUtils.isNotEmpty(username)) {
			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			JSONObject obj=new JSONObject();
			obj.put("userInfo",sysUser);
			obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
			result.setResult(obj);
			result.success("");
		}
		return result;

	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return Result.error("退出登录失败！");
	    }
	    String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseApi.getUserByName(username);
	    if(sysUser!=null) {
			//update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			baseCommonService.addLog("用户名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
			//update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
	    	log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
	    	//清空用户登录Token缓存
	    	redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
	    	//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
	    	return Result.ok("退出登录成功！");
	    }else {
	    	return Result.error("Token无效!");
	    }
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String,Object>>> visitInfo() {
		Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}
	
	
	/**
	 * 登陆成功选择用户当前部门
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode= user.getOrgCode();
		this.sysUserService.updateUserDepart(username, orgCode);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		return result;
	}

	/**
	 * 短信登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public Result<String> sms(@RequestBody JSONObject jsonObject) throws IOException {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 获取绑定微信手机号验证码:"6" 获取换绑手机号验证码:"5" 获取原先手机号验证码:"4" saas登录模式:"3" 管理员登录模式: "2"  注册模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
		//查询验证码是否仍然有效
		Object object = null;
		if(smsmode.equals("1")){
			object = redisUtil.get("registPhone"+mobile);
		}else{
			object = redisUtil.get("loginPhone"+mobile);
		}
		if (object != null) {
			result.setMessage("验证码5分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
    	obj.put("code", captcha);

		boolean b = false;
		//注册模板
		if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
			SysUser sysUser = sysUserService.getUserByPhone(mobile);
			if(sysUser!=null) {
				result.error500(" 手机号已经注册，请直接登录！");
				baseCommonService.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
				return result;
			}

		}else {
			//登录模式，校验用户有效性
			SysUser sysUser = sysUserService.getUserByPhone(mobile);
			result = sysUserService.checkUserIsEffective(sysUser);
			if(!result.isSuccess()) {
				String message = result.getMessage();
				if("该用户不存在，请注册".equals(message)){
					result.error500("该用户不存在或未绑定手机号");
				}
				return result;
			}

			//校验改用户是否有权力登录到管理/saas端
			String roleCode = sysUserRoleService.getRoleCodeByUserId(sysUser.getId());
			if(!((roleCode.equals("saas")&&smsmode.equals("3"))||(roleCode.equals("admin")&&smsmode.equals("2")))){
				result.error500("该用户无权登录");
				return result;
			}

		}

		b = sendPhoneCOde(captcha, mobile);

		if (b == false) {
			result.setMessage("短信验证码发送失败,请稍后重试");
			result.setSuccess(false);
			return result;
		}
		//验证码10分钟内有效
		if(smsmode.equals("1")){
			redisUtil.set("registPhone"+mobile, captcha, 300);
		}else {
			redisUtil.set("loginPhone"+mobile, captcha, 300);
		}

		result.setSuccess(true);

		return result;
	}

	/**
	 * 微信短信绑定接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/smsWechatOpen")
	public Result<String> smsWechatOpen(@RequestBody JSONObject jsonObject) throws IOException {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//获取微信绑定接口: "0"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			return result.error500("手机号不能为Null");
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
		obj.put("code", captcha);

		boolean b = false;
		//判断该手机号是否已经被绑定
		SysUser user = sysUserService.getUserByPhone(mobile);
		if(user!=null){
			SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneBySysUserId(user.getId(),"wechat_open");
			if(sysThirdAccount!=null){
				return result.error500("该账户已绑定微信!");
			}
		}


		b = sendPhoneCOde(captcha, mobile);

		if (b == false) {
			return result.error500("短信验证码发送失败,请稍后重试");
		}
		//验证码10分钟内有效
		redisUtil.set("bindWechatOpenSms"+mobile, captcha, 300);

		result.setSuccess(true);

		return result;
	}



	/**
	 * @description: 获取邮箱登录验证码
	 * @author: GGB
	 * @date: 2022/10/22 12:39
	 * @param: request
	 * @param: jsonObject
	 * @return: Result<String>
	 **/
	@PostMapping("/getEmailCode")
	public Result<String> getEmailCode(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String email = jsonObject.get("email").toString();
		log.info(email);

		if(oConvertUtils.isEmpty(email)){
			result.error500("邮箱不允许为空！");
			result.setSuccess(false);
			return result;
		}
		Object object = redisUtil.get("loginEmail"+email);
		if (object != null) {
			result.error500("验证码5分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}

		//判断改邮箱是否已经绑定过账号
		SysUser sysUser = sysUserService.getUserByEmail(email);
		if(oConvertUtils.isEmpty(sysUser)){
			result.error500("该邮箱暂未绑定账号！");
			result.setSuccess(false);
			return result;
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);

		//发送邮箱
		EmailSendMsgHandle emailSendMsgHandle = new EmailSendMsgHandle();
		String es_title = "【朗新天霁】邮箱登录验证";
		String es_content = "尊敬的用户,您好:\n" +
				"    您正在朗新天霁进行邮箱登录的操作，本次请求的邮件验证码是："+captcha+" (为了保证您账号的安全性，请您在5分钟内完成验证).\n" +
				"本验证码5分钟内有效，请及时输入。\n" +
				"\n" +
				"    为保证账号安全，请勿泄漏此验证码。\n" +
				"    祝在【朗新天霁】使用愉快！\n" +
				"\n" +
				"    ( ゜- ゜)つロ乾杯~ - 朗新天霁\n" +
				"    （这是一封自动发送的邮件，请不要直接回复）";

		emailSendMsgHandle.SendMsg(email ,es_title ,es_content);

		redisUtil.set("loginEmail"+email, captcha, 300);
		result.setSuccess(true);
		return result;
	}
	/**
	 * 手机号登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手机号登录接口")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("mobile");
		
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//校验改用户是否有权力登录到管理/saas端
		String roleCode = sysUserRoleService.getRoleCodeByUserId(sysUser.getId());
		Integer loginEnd = Integer.valueOf(jsonObject.getString("loginEnd"));
		if(!((roleCode.equals("saas")&&loginEnd==1)||(roleCode.equals("admin")&&loginEnd==0))){
			result.setMessage("该用户无权登录");
			return result;
		}

		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get("loginPhone"+phone);
		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		redisUtil.del("loginPhone"+phone);
		//添加日志
		baseCommonService.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}

	/**
	 * @description:邮箱登录接口
	 * @author: GGB
	 * @date: 2022/10/22 12:39
	 * @param: jsonObject
	 * @return: Result<JSONObject>
	 **/
	@ApiOperation("邮箱登录接口")
	@PostMapping("/emailLogin")
	public Result<JSONObject> emailLogin(@RequestBody JSONObject jsonObject){
		Result<JSONObject> result = new Result<JSONObject>();
		String email = jsonObject.getString("email");
		String emailCode = jsonObject.getString("emailCode");

		//用户信息
		SysUser sysUser = sysUserService.getUserByEmail(email);
		//校验改用户是否有权力登录到管理/saas端
		String roleCode = sysUserRoleService.getRoleCodeByUserId(sysUser.getId());
		Integer loginEnd = Integer.valueOf(jsonObject.getString("loginEnd"));
		if(!((roleCode.equals("saas")&&loginEnd==1)||(roleCode.equals("admin")&&loginEnd==0))){
			result.error500("该用户无权登录");
			return result;
		}

		//判断验证码是否正确
		Object code = redisUtil.get("loginEmail"+email);
		if(!emailCode.equals(code)){
			result.error500("邮箱验证码错误");
			return result;
		}

		userInfo(sysUser, result);
		baseCommonService.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}

	/**
	 * 用户信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String syspassword = sysUser.getPassword();
		String username = sysUser.getUsername();
		// 获取用户部门信息
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			obj.put("multi_depart", 1);
		} else {
			//查询当前是否有登录部门
			// update-begin--Author:wangshuai Date:20200805 for：如果用戶为选择部门，数据库为存在上一次登录部门，则取一条存进去
			SysUser sysUserById = sysUserService.getById(sysUser.getId());
			if(oConvertUtils.isEmpty(sysUserById.getOrgCode())){
				sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			}
			// update-end--Author:wangshuai Date:20200805 for：如果用戶为选择部门，数据库为存在上一次登录部门，则取一条存进去
			obj.put("multi_depart", 2);
		}
		// update-begin--Author:sunjianlei Date:20210802 for：获取用户租户信息
		String tenantIds = sysUser.getRelTenantIds();
		if (oConvertUtils.isNotEmpty(tenantIds)) {
			List<Integer> tenantIdList = new ArrayList<>();
			for(String id: tenantIds.split(",")){
				tenantIdList.add(Integer.valueOf(id));
			}
			// 该方法仅查询有效的租户，如果返回0个就说明所有的租户均无效。
			List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
			if (tenantList.size() == 0) {
				result.error500("与该用户关联的租户均已被冻结，无法登录！");
				return result;
			} else {
				obj.put("tenantList", tenantList);
			}
		}
		// update-end--Author:sunjianlei Date:20210802 for：获取用户租户信息
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 获取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap(5);
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 * @param response
	 * @param key
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(HttpServletResponse response,@PathVariable("key") String key){
		Result<String> res = new Result<String>();
		try {
			//生成验证码
			final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);

			//存到redis中
			String lowerCaseCode = code.toLowerCase();
			String realKey = Md5Util.md5Encode(lowerCaseCode+key, "utf-8");
            log.info("获取验证码，Redis checkCode = {}，key = {}", code, key);
			redisUtil.set(realKey, lowerCaseCode, 60);

			//返回前端
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setResult(base64);
		} catch (Exception e) {
			res.error500("获取验证码出错"+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * app登录
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
		
		String orgCode = sysUser.getOrgCode();
		if(oConvertUtils.isEmpty(orgCode)) {
			//如果当前用户无选择部门 查看部门关联信息
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			//update-begin-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
			if (departs == null || departs.size() == 0) {
				/*result.error500("用户暂未归属部门,不可登录!");
				return result;*/
			}else{
				orgCode = departs.get(0).getOrgCode();
				sysUser.setOrgCode(orgCode);
				this.sysUserService.updateUserDepart(username, orgCode);
			}
			//update-end-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
		}
		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);
		
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 图形验证码
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return Result.error("验证码无效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("验证码错误");
		}
		return Result.ok();
	}
	/**
	 * 登录二维码
	 */
	@ApiOperation(value = "登录二维码", notes = "登录二维码")
	@GetMapping("/getLoginQrcode")
	public Result<?>  getLoginQrcode() {
		String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE+IdWorker.getIdStr();
		//定义二维码参数
		Map params = new HashMap(5);
		params.put("qrcodeId", qrcodeId);
		//存放二维码唯一标识30秒有效
		redisUtil.set(CommonConstant.LOGIN_QRCODE + qrcodeId, qrcodeId, 30);
		return Result.OK(params);
	}
	/**
	 * 扫码二维码
	 */
	@ApiOperation(value = "扫码登录二维码", notes = "扫码登录二维码")
	@PostMapping("/scanLoginQrcode")
	public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
		Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isNotEmpty(check)) {
			//存放token给前台读取
			redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN+qrcodeId, token, 60);
		} else {
			return Result.error("二维码已过期,请刷新后重试");
		}
		return Result.OK("扫码成功");
	}


	/**
	 * 获取用户扫码后保存的token
	 */
	@ApiOperation(value = "获取用户扫码后保存的token", notes = "获取用户扫码后保存的token")
	@GetMapping("/getQrcodeToken")
	public Result getQrcodeToken(@RequestParam String qrcodeId) {
		Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
		Map result = new HashMap(5);
		Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
			//二维码过期通知前台刷新
			result.put("token", "-2");
			return Result.OK(result);
		}
		if (oConvertUtils.isNotEmpty(token)) {
			result.put("success", true);
			result.put("token", token);
		} else {
			result.put("token", "-1");
		}
		return Result.OK(result);
	}

	@PostMapping("/tenantLogin")
	public Result<JSONObject> tenantLogin(HttpServletRequest request,@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		String captcha = sysLoginModel.getCaptcha();
		String realmName = sysLoginModel.getRealmName();
		if(oConvertUtils.isEmpty(realmName)){
			realmName = request.getServerName();
		}
		if(captcha==null){
			result.error500("验证码无效");
			return result;
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+sysLoginModel.getCheckKey(), "utf-8");
		Object checkCode = redisUtil.get(realKey);
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if(checkCode==null || !checkCode.toString().equals(lowerCaseCaptcha)) {
			log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("验证码错误");
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校验验证码

		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);

		//通过域名获取该域名的用户
		Integer tenantId = sysTenantService.getIdByRealmName(realmName);
		String tenantUserName = sysUserService.getUserNameByTenantId(String.valueOf(tenantId));
		if(!tenantUserName.equals(sysUser.getUsername())){
			result.error500("该用户非该租户的用户！");
			return result;
		}
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			result.error500("该用户已失效！");
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		System.out.println(userpassword);
		System.out.println(syspassword);
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}

		//校验改用户是否有权力登录到管理/saas端
		String roleCode = sysUserRoleService.getRoleCodeByUserId(sysUser.getId());
		Integer loginEnd = sysLoginModel.getLoginEnd();
		if(!((roleCode.equals("saas")&&loginEnd==1)||(roleCode.equals("admin")&&loginEnd==0))){
			result.error500("该用户无权登录");
			return result;
		}

		//用户登录信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		redisUtil.del(realKey);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
		//update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}

	@PostMapping("/getRebindPhoneCode")
	public Result<String> getRebindPhoneCode(HttpServletRequest request, @RequestBody JSONObject jsonObject) throws IOException {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 忘记密码获取验证码:"3" 获取换绑手机号验证码:"2" 获取原先手机号验证码:"1"
		Integer rebindFlag=jsonObject.getInteger("rebindFlag");
		String username = JwtUtil.getUserNameByToken(request);
		SysUser sysUser = sysUserService.getUserByName(username);
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
		//查询验证码是否仍然有效
		Object object = null;
		if(rebindFlag == 1){
			if(!sysUser.getPhone().equals(mobile)){
				result.error500("改手机号非该用户的手机号！");
				result.setSuccess(false);
				return result;
			}
			object = redisUtil.get("beforePhone"+mobile);
		}else if(rebindFlag == 2){
			object = redisUtil.get("rebindPhone"+mobile);
		}else {
			object = redisUtil.get("forgetphone"+mobile);
		}
		if (object != null) {
			result.error500("验证码5分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}
		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
		obj.put("code", captcha);
		boolean b = false;

		if(rebindFlag == 2){
			String beforePhone = sysUser.getPhone();
			object = redisUtil.get("beforePhoneVaild"+beforePhone);
			if(object == null){
				result.error500("长时间未操作，原手机验证已失效！");
				result.setSuccess(false);
				return result;
			}
			//判断该手机号是否已经被注册
			sysUser = sysUserService.getUserByPhone(mobile);
			if(oConvertUtils.isNotEmpty(sysUser)){
				result.error500("该手机号已被绑定，请更换手机号!");
				result.setSuccess(false);
				return result;
			}
		}
		//发送验证码
		b = sendPhoneCOde(captcha, mobile);
		if (b == false) {
			result.setMessage("短信验证码发送失败,请稍后重试");
			result.setSuccess(false);
			return result;
		}
		if(rebindFlag == 1){
			redisUtil.set("beforePhone"+mobile, captcha, 300);
			redisUtil.set("beforePhoneVaild"+mobile, captcha, 1200);
		}else if(rebindFlag == 2){
			redisUtil.set("rebindPhone"+mobile, captcha, 300);
		}else {
			redisUtil.set("forgetphone"+mobile, captcha, 300);
		}
		return result.ok("验证码发送成功!");
	}

	@PostMapping("/rebindPhone")
	public Result<String> rebindEmail(HttpServletRequest request, @RequestBody JSONObject jsonObject){
		Result<String> result = new Result<>();
		String phone = jsonObject.getString("phone");
		String phoneCode = jsonObject.getString("phoneCode");
		Integer rebindFlag = jsonObject.getInteger("rebindFlag");
		Object code = null;
		if(rebindFlag == 1){
			code = redisUtil.get("beforePhone"+phone);
		}else {
			code = redisUtil.get("rebindPhone"+phone);
		}

		if(code == null){
			result.error500("手机号验证码失效，请重新获取!");
			result.setSuccess(false);
			return result;
		}
		if(!phoneCode.equals(code.toString())){
			result.error500("手机号验证码错误!");
			result.setSuccess(false);
			return result;
		}
		if(rebindFlag == 1){
			redisUtil.del("beforePhone"+phone);
			return result.ok("手机验证码正确");
		}
		try {
			//获取当前登录用户
			String username = JwtUtil.getUserNameByToken(request);
			SysUser user = sysUserService.getUserByName(username);
			if(sysUserService.setUserPhone(user,phone)){
				result.success("手机号换绑成功");
				redisUtil.del("rebindPhone"+phone);
				redisUtil.del("beforePhoneVaild"+phone);
			}else {
				result.error500("手机号换绑失败");
				result.setSuccess(false);
			}
		} catch (Exception e) {
			result.error500("绑定失败");
		}
		return result;
	}

	/**
	 * @description: 发送手机验证码
	 * @author: GGB
	 * @date: 2022/11/2 11:23
	 * @param: captcha
	 * @param: mobile
	 * @return: Boolean
	 **/
	private Boolean sendPhoneCOde(String captcha, String mobile) throws IOException {
		Boolean b = false;
		//发送验证码
		String  content = "【朗新天霁】验证码"+captcha+"(5分钟有效)。如非本人操作，请忽略本短信。";
		//内容转成GBK
		StringBuilder sb = new StringBuilder();
		byte[] bytes = content.getBytes("GBK");
		for(byte by : bytes) {
			sb.append("%" + Integer.toHexString((by & 0xff)).toUpperCase());
		}
		String gbk = sb.toString();

		//时间
		String now = DateUtil.now();
		String nowStr = now.replaceFirst(" ","&nbsp");
		System.out.println(nowStr);
		String url = "http://api.sms1086.com/Api/verifycode.aspx?username=18059257022&password=r12345&mobiles="+mobile+"&content="+gbk;
		System.out.println(url);
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet();
		//设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000)
				.setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
		httpGet.setConfig(requestConfig);
		httpGet.setURI(URI.create(url));
		httpGet.setHeader("Accept", "application/x-www-form-urlencoded");
		httpGet.setHeader("Content-Type", "application/json;charset=GBK");
		httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		HttpEntity entity = response.getEntity();
		String result1 = null;
		if(entity != null){
			result1 = EntityUtils.toString(entity,"UTF-8");
			System.out.println("这里是短信验证返回的内容");
			String str = result1.substring(0, result1.indexOf("&"));
			System.out.println(result1);
			System.out.println(str);
			if(str.equals("result=0")){
				b=true;
			}
		}
		httpGet.abort();
		return b;
	}
}
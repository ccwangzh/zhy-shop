/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/*
import com.esotericsoftware.minlog.Log;
*/
import com.google.common.collect.Maps;

import net.shopxx.Message;
import net.shopxx.Setting;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.exception.ApplicationException;
import net.shopxx.service.CaptchaService;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.RSAService;
import net.shopxx.service.VerifyCodeService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtil;

/**
 * Controller - 会员注册
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopRegisterController")
@RequestMapping("/register")
public class RegisterController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;
	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;
	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;
	@Resource(name = "verifyCodeServiceImpl")
	private VerifyCodeService verifyCodeService;
	@Value("${clearingUrl}")
	private String clearingUrl;
	@Resource
	private RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);


	/**
	 * 检查用户名是否被禁用或已存在
	 * 暂且跳过
	 */
	@RequestMapping(value = "/check_username", method = RequestMethod.GET)
	public @ResponseBody boolean checkUsername(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		return !memberService.usernameDisabled(mobile) && !memberService.usernameExists(mobile);
	}
	/**
	 * 用户名验证
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/checkMobile.jhtml", method = RequestMethod.POST)
	public @ResponseBody Boolean checkMobile(String username){
		return !isMobileExist(username);
	}
	
	
	/**
	 * 注册页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		return "/shop/${theme}/register/index";
	}

	/**
	 * 注册提交
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody Message submit(String captchaId, String captcha, String username, String verifyCode ,HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Setting setting = SystemUtils.getSetting();

		//验证码
		if (!captchaService.isValid(Setting.CaptchaType.memberRegister, captchaId, captcha)) {
			return Message.error("shop.captcha.invalid");
		}
		//是否开放注册
		if (!setting.getIsRegisterEnabled()) {
			return Message.error("shop.register.disabled");
		}
		if(isMobileExist(username)){
			return Message.error("shop.register.mobileExist");
		}
		if(!checkVerityCode(username,verifyCode)){
			return Message.error("shop.register.wrongverifyCode");
		}
		String mobile = username;
		
		// 向清算系统注册用户
		Map<String, Object> clearingMap = registerToExchange(username,request.getParameter("enPassword"));
		if (!(boolean) clearingMap.get(SUCCESS)) {
			return Message.error("shop.register.failRegisterToClearing");
		}
		 username = clearingMap.get("userId").toString();
		rsaService.removePrivateKey(request);//移除私钥
		if (!isValid(Member.class, "username", username, BaseEntity.Save.class)) {
			return Message.error("shop.common.invalid");
		}

		Member member = new Member();
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return Message.error("shop.common.invalid");
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		member.setMobile(mobile);
		member.setUsername(username);
		member.setNickname(null);
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setFrozenFunds(BigDecimal.ZERO);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginPluginId(null);
		member.setOpenId(null);
		member.setLockKey(null);
		member.setSafeKey(null);
		member.setMemberRank(memberRankService.findDefault());
		member.setCart(null);
		member.setOrders(null);
		member.setPaymentTransactions(null);
		member.setDepositLogs(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setFavoriteGoods(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		member.setPointLogs(null);
		memberService.register(member);
		return Message.success("shop.register.success");
	}
	
	/**
	 * 获取验证码
	 * @param username
	 * @param request
	 * @return Map
	 */
	@RequestMapping(value = "/getVerityCode.jhtml", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> sendVerityCode(String username,HttpServletRequest request){
		Map<String,Object> result=Maps.newHashMap();
		try{
		verifyCodeService.sendVerifyCode(username, username, "P", "resigter", request.getRemoteAddr(), 1);
		result.put(SUCCESS, true);
		}catch(Exception e){
			logger.error("发送验证码失败",e);
			result.put(SUCCESS, false);
			result.put("msg","发送验证码失败,请稍后再试！");
			
		}
		return result;
	}
	
	/**
	 * 清算中心 注册
	 * 
	 * @param user
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> registerToExchange(String mobile, String password) {
		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, Object> clearingRegister = new HashMap<>();
		UriComponentsBuilder builder;
		Setting setting = SystemUtils.getSetting();
		// 向清算中心注册
		builder = UriComponentsBuilder
				.fromHttpUrl(String.format("%s/clearing-web/mgmt/user/create",
						clearingUrl))
				.queryParam("mobile", mobile).queryParam("password", password)
				.queryParam("existingOnly", "");

		try {
			clearingRegister = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
		} catch (HttpStatusCodeException e) {
			logger.error("向清算系统注册失败, details: {}", e);
			throw new ApplicationException("注册失败");
		}
		return clearingRegister;
	}
	/**
	 * // 向清算中心 验证手机号
	 * 
	 * @param mobile
	 * @return true 手机号存在 false:手机号不存在或者异常
	 */
	@SuppressWarnings("unchecked")
	private boolean isMobileExist(String mobile) {
		HashMap<String, Object> resultMap = new HashMap<>();
		Setting setting = SystemUtils.getSetting();

		// 向清算中心 验证密码
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(String.format("%s/clearing-web/mgmt/user/checkMobile",
						clearingUrl))
				.queryParam("mobile", mobile);
		try {
			resultMap = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
		} catch (HttpStatusCodeException e) {
			String responseString = e.getResponseBodyAsString();
			logger.warn("检查手机号是否存在异常, details: {}", responseString);
			return false;
		}
		return (boolean) resultMap.get(SUCCESS);
	}
	
	/**
	 * 校对验证码
	 * @param username
	 * @param verifyCode
	 * @return
	 */
	private boolean checkVerityCode(String username,String verifyCode){
		if(verifyCode.length()!=6){
			return false;
		}
		String VCode = verifyCodeService.getVerifyCode(username, username, "P", "resigter");
		if(!VCode.equals(verifyCode)){
			return false;
		}
		verifyCodeService.invaildCode(username);
		return true;
	}
}
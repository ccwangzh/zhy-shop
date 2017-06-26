/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Maps;

import net.shopxx.Message;
import net.shopxx.Principal;
import net.shopxx.Setting;
import net.shopxx.entity.Member;
import net.shopxx.event.MemberLoggedInEvent;
import net.shopxx.exception.ApplicationException;
import net.shopxx.plugin.LoginPlugin;
import net.shopxx.service.CaptchaService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.PluginService;
import net.shopxx.service.RSAService;
import net.shopxx.service.SmsService;
import net.shopxx.service.VerifyCodeService;
import net.shopxx.service.impl.VerifyCodeServiceImpl;
import net.shopxx.util.HttpRequestHelper;
import net.shopxx.util.SystemUtils;

/**
 * Controller - 会员登录
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopLoginController")
@RequestMapping("/login")
public class LoginController extends BaseController {

	@Resource
	private ApplicationEventPublisher applicationEventPublisher;
	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;
	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;
	@Value("${clearingUrl}")
	private String clearingUrl;
	@Value("${exchangeId}")
    private String exchangeId;


	@Resource
	private RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


	/**
	 * 登录检测
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public @ResponseBody boolean check() {
		return memberService.isAuthenticated();
	}

	/**
	 * 登录页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(String redirectUrl, HttpServletRequest request, ModelMap model) {
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.equalsIgnoreCase(redirectUrl, setting.getSiteUrl()) || StringUtils.startsWithIgnoreCase(redirectUrl, request.getContextPath() + "/") || StringUtils.startsWithIgnoreCase(redirectUrl, setting.getSiteUrl() + "/")) {
			model.addAttribute("redirectUrl", redirectUrl);
		}
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("loginPlugins", pluginService.getLoginPlugins(true));
		return "/shop/${theme}/login/index";
	}

	/**
	 * 登录提交
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody Message submit(String captchaId, String captcha, String username, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String password = request.getParameter("enPassword");
		String mobile = username;
		Setting setting = SystemUtils.getSetting();
		//rsaService.decryptParameter("enPassword", request);
		
		rsaService.removePrivateKey(request);

		if (!captchaService.isValid(Setting.CaptchaType.memberLogin, captchaId, captcha)) {
			return Message.error("shop.captcha.invalid");
		}//验证码
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return Message.error("shop.common.invalid");
		}//空户名密码空验证
		
		Member member;
		Map<String,Object> clearingLogin = loginToClearing(username, password);
		if (clearingLogin.get("userId") != null){
			username = clearingLogin.get("userId").toString();
		}
		if((boolean) clearingLogin.get(SUCCESS) && username.length() != 8){
		    logger.error("清算登陆返回的结果:{}",clearingLogin);
		    logger.error("user login http request,{}",HttpRequestHelper.extractParams(request));
		    //no session
			return Message.error("shop.login.failLoginToClearing");
		}
		member = memberService.findByUsername(username);
		if((boolean) clearingLogin.get(SUCCESS)){
			if (member == null) {
				saveNewMember(username,request,mobile);
				member = memberService.findByUsername(username);
				}	
		}
		if(!(boolean) clearingLogin.get(SUCCESS)){
			if (member == null) {
				return Message.error("shop.login.unknownAccount");
			}
		}
		if (!member.getIsEnabled()) {
			return Message.error("shop.login.disabledAccount");
		}
		if (member.getIsLocked()) {
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				int loginFailureLockTime = setting.getAccountLockTime();
				if (loginFailureLockTime == 0) {
					return Message.error("shop.login.lockedAccount");
				}
				Date lockedDate = member.getLockedDate();
				Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
				if (new Date().after(unlockDate)) {
					member.setLoginFailureCount(0);
					member.setIsLocked(false);
					member.setLockedDate(null);
					memberService.update(member);
				} else {
					return Message.error("shop.login.lockedAccount");
				}
			} else {
				member.setLoginFailureCount(0);
				member.setIsLocked(false);
				member.setLockedDate(null);
				memberService.update(member);
			}
		}
		if (!(boolean) clearingLogin.get(SUCCESS)) { //用户名或者密码错误
			int loginFailureCount = member.getLoginFailureCount() + 1;
			if (loginFailureCount >= setting.getAccountLockCount()) {
				member.setIsLocked(true);
				member.setLockedDate(new Date());
			}
			member.setLoginFailureCount(loginFailureCount);
			memberService.update(member);
			if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
				return Message.error("shop.login.accountLockCount", setting.getAccountLockCount());
			} else {
				return Message.error("shop.login.incorrectCredentials");
			}
		}
		member.setLoginIp(request.getRemoteAddr());
		member.setLoginDate(new Date());
		member.setLoginFailureCount(0);
		memberService.update(member);
		session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
		applicationEventPublisher.publishEvent(new MemberLoggedInEvent(this, member));

		return SUCCESS_MESSAGE;
	}

	/**
	 * 插件提交
	 */
	@RequestMapping(value = "/plugin_submit", method = RequestMethod.GET)
	public String pluginSubmit(String pluginId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(pluginId);
		if (loginPlugin == null || !loginPlugin.getIsEnabled()) {
			return ERROR_VIEW;
		}
		model.addAttribute("requestUrl", loginPlugin.getRequestUrl());
		model.addAttribute("requestMethod", loginPlugin.getRequestMethod());
		model.addAttribute("requestCharset", loginPlugin.getRequestCharset());
		model.addAttribute("parameterMap", loginPlugin.getParameterMap(request));
		if (StringUtils.isNotEmpty(loginPlugin.getRequestCharset())) {
			response.setContentType("text/html; charset=" + loginPlugin.getRequestCharset());
		}
		return "/shop/${theme}/login/plugin_submit";
	}

	/**
	 * 插件通知
	 */
	@RequestMapping(value = "/plugin_notify/{pluginId}", method = RequestMethod.GET)
	public String pluginNotify(@PathVariable String pluginId, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap model) {
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(pluginId);
		if (loginPlugin != null && loginPlugin.getIsEnabled() && loginPlugin.verifyNotify(request)) {
			Setting setting = SystemUtils.getSetting();
			String openId = loginPlugin.getOpenId(request);
			if (StringUtils.isEmpty(openId)) {
				model.addAttribute("errorMessage", message("shop.login.pluginError"));
				return ERROR_VIEW;
			}
			Member member = memberService.find(pluginId, openId);
			if (member != null) {
				if (!member.getIsEnabled()) {
					model.addAttribute("errorMessage", message("shop.login.disabledAccount"));
					return ERROR_VIEW;
				}
				if (member.getIsLocked()) {
					if (ArrayUtils.contains(setting.getAccountLockTypes(), Setting.AccountLockType.member)) {
						int loginFailureLockTime = setting.getAccountLockTime();
						if (loginFailureLockTime == 0) {
							model.addAttribute("errorMessage", message("shop.login.lockedAccount"));
							return ERROR_VIEW;
						}
						Date lockedDate = member.getLockedDate();
						Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);
						if (new Date().after(unlockDate)) {
							member.setLoginFailureCount(0);
							member.setIsLocked(false);
							member.setLockedDate(null);
							memberService.update(member);
						} else {
							model.addAttribute("errorMessage", message("shop.login.lockedAccount"));
							return ERROR_VIEW;
						}
					} else {
						member.setLoginFailureCount(0);
						member.setIsLocked(false);
						member.setLockedDate(null);
						memberService.update(member);
					}
				}
				member.setLoginIp(request.getRemoteAddr());
				member.setLoginDate(new Date());
				member.setLoginFailureCount(0);
				memberService.update(member);

				session.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
				applicationEventPublisher.publishEvent(new MemberLoggedInEvent(this, member));
			} else {
				if (!setting.getIsRegisterEnabled()) {
					model.addAttribute("errorMessage", message("shop.login.registerDisabled"));
					return ERROR_VIEW;
				}
				String email = loginPlugin.getEmail(request);
				String nickname = loginPlugin.getNickname(request);
				member = new Member();
				String username = openId;
				for (int i = 0; memberService.usernameExists(username); i++) {
					username = openId + i;
				}
				member.removeAttributeValue();
				member.setUsername(username);
				member.setEmail(email);
				member.setNickname(nickname);
				member.setPoint(0L);
				member.setBalance(BigDecimal.ZERO);
				member.setAmount(BigDecimal.ZERO);
				member.setIsEnabled(true);
				member.setIsLocked(false);
				member.setLoginFailureCount(0);
				member.setLockedDate(null);
				member.setRegisterIp(request.getRemoteAddr());
				member.setLoginIp(request.getRemoteAddr());
				member.setLoginDate(new Date());
				member.setLoginPluginId(pluginId);
				member.setOpenId(openId);
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
			}
		}
		return "redirect:/";
	}
	
	/**
	 * 向清算中心登录
	 * @param userId
	 * @param password
     * @return
     */
	@SuppressWarnings("unchecked")
	private Map<String,Object>loginToClearing(String userId,String password)
	{
		Map<String, Object> clearingLogin;
		Setting setting = SystemUtils.getSetting();
		logger.info("clearingUrl+"+clearingUrl);
		// 向清算中心 验证密码
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(String.format("%s/clearing-web/mgmt/user/login",
						clearingUrl))
				.queryParam("userId", userId).queryParam("password", password)
				.queryParam("exchangeId",exchangeId);
		try {
			clearingLogin = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
		} catch (HttpStatusCodeException e) {
			logger.warn("Failed to login, details: {}", e.getResponseBodyAsString());
			throw new ApplicationException("登录失败");
		} catch (Exception e) {
			logger.warn(String.format("到清算系统的请求失败: %s", builder.build().toUriString()), e);
			throw new ApplicationException("登录失败");
		}
		return  clearingLogin;
	}
	
	private void saveNewMember(String username,HttpServletRequest request,String mobile){
		
		Member member = new Member();
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
		memberService.loginAdd(member);
	}
	
	@RequestMapping(value = "/mgmt/login.jhtml", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> login(String username,String mobile ,HttpServletRequest request, HttpServletResponse response, HttpSession session){
		Map<String, Object> result = Maps.newHashMap();
		try {
			Member member = memberService.findByUsername(username);
			if (member == null) {
				saveNewMember(username, request, mobile);
				member = memberService.findByUsername(username);
			}
			result.put(SUCCESS, true);
			result.put("id", member.getId());
			result.put("username", username);
		} catch (Exception e) {
			result.put(SUCCESS, false);
			logger.error("login to shop failed:{}", e);
		}
		return result;
	}
	
	@SuppressWarnings("finally")
	@RequestMapping(value = "/mgmt/registerToShop.jhtml", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> mgmtAddUser(String username,String mobile,HttpServletRequest request){
		Map<String, Object> result = Maps.newHashMap();
		try {
			saveNewMember(username, request, mobile);
			result.put(SUCCESS, true);
		} catch (Exception e) {
			result.put(SUCCESS, false);
			logger.error("register to shop failed:{}", e);
		}
		return result;
		
	}
}
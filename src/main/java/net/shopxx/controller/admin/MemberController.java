/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.exception.ApplicationException;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.PluginService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtil;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controller - 会员
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminMemberController")
@RequestMapping("/admin/member")
public class MemberController extends BaseController {
	// 成功
    public static final String SUCCESS = "success";

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;
	@Resource(name = "memberAttributeServiceImpl")
	private MemberAttributeService memberAttributeService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Resource
	private RestTemplate restTemplate;
	@Value("${clearingUrl}")
	private String clearingUrl;
	Setting setting = SystemUtils.getSetting();
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	/**
	 * 检查用户名是否被禁用或已存在
	 */
	@RequestMapping(value = "/check_username", method = RequestMethod.GET)
	public @ResponseBody boolean checkUsername(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}
		return !memberService.usernameDisabled(username) && !memberService.usernameExists(username);
	}

	/**
	 * 检查E-mail是否唯一
	 */
	@RequestMapping(value = "/check_email", method = RequestMethod.GET)
	public @ResponseBody boolean checkEmail(String previousEmail, String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		return memberService.emailUnique(previousEmail, email);
	}

	/**
	 * 查看
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		Member member = memberService.find(id);
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		model.addAttribute("member", member);
		model.addAttribute("loginPlugin", pluginService.getLoginPlugin(member.getLoginPluginId()));
		return "/admin/member/view";
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		return "/admin/member/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Member member, Long memberRankId, HttpServletRequest request, RedirectAttributes redirectAttributes,ModelMap model) {
		member.setMemberRank(memberRankService.find(memberRankId));
		//前台验证框架有兼容性问题，用户名、密码后台再验证一次
		if(!WebUtil.regPhoneNum(member.getUsername())){
			model.addAttribute("errorMessage", message("admin.validate.illegal"));
			return ERROR_VIEW;
		}
		if(!WebUtil.regPwd(request.getParameter("password"))){
			model.addAttribute("errorMessage", message("admin.member.pwdErorr"));
			return ERROR_VIEW;
		}
		//是否开放注册
		if (!setting.getIsRegisterEnabled()) {
			return ERROR_VIEW;
		}
		if(isMobileExist(member.getUsername())){
			model.addAttribute("errorMessage", message("admin.member.disabledExist"));
			return ERROR_VIEW;
		}
		// 向清算系统注册用户
		Map<String, Object> clearingMap = registerToExchange(member.getUsername(),WebUtil.getMD5(request.getParameter("password")));
		if (!(boolean) clearingMap.get(SUCCESS)) {
			return ERROR_VIEW;
		}
		
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return ERROR_VIEW;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		member.setMobile(member.getUsername());
		member.setUsername(clearingMap.get("userId").toString());
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setFrozenFunds(BigDecimal.ZERO);
		member.setIsLocked(false);
		member.setLoginFailureCount(0);
		member.setLockedDate(null);
		member.setRegisterIp(request.getRemoteAddr());
		member.setLoginIp(null);
		member.setLoginDate(null);
		member.setLoginPluginId(null);
		member.setOpenId(null);
		member.setLockKey(null);
		member.setSafeKey(null);
		member.setCart(null);
		member.setBusiness(null);
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
		memberService.save(member);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}
	
    
	/**
	 * 清算中心 注册
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> registerToExchange(String mobile, String password) {
		HashMap<String, Object> resultMap = new HashMap<>();
		HashMap<String, Object> clearingRegister = new HashMap<>();
		UriComponentsBuilder builder;
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
	public boolean isMobileExist(String mobile) {
		HashMap<String, Object> resultMap = new HashMap<>();
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
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		Member member = memberService.find(id);
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		model.addAttribute("member", member);
		model.addAttribute("loginPlugin", pluginService.getLoginPlugin(member.getLoginPluginId()));
		return "/admin/member/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Member member, Long memberRankId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		member.setMemberRank(memberRankService.find(memberRankId));
		if (!isValid(member)) {
			return ERROR_VIEW;
		}
		Setting setting = SystemUtils.getSetting();
		
		Member pMember = memberService.find(member.getId());
		if (pMember == null) {
			return ERROR_VIEW;
		}
		if (!setting.getIsDuplicateEmail() && !memberService.emailUnique(pMember.getEmail(), member.getEmail())) {
			return ERROR_VIEW;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return ERROR_VIEW;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		
		if (pMember.getIsLocked() && !member.getIsLocked()) {
			member.setLoginFailureCount(0);
			member.setLockedDate(null);
		} else {
			member.setIsLocked(pMember.getIsLocked());
			member.setLoginFailureCount(pMember.getLoginFailureCount());
			member.setLockedDate(pMember.getLockedDate());
		}
		memberService.update(member, "username", "point", "balance", "amount", "frozenFunds", "registerIp", "loginIp", "loginDate", "loginPluginId", "openId", "lockKey", "safeKey", "cart", "orders", "depositLogs", "couponCodes", "receivers", "reviews", "consultations", "favoriteGoods",
				"productNotifies", "inMessages", "outMessages", "pointLogs");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findAll());
		model.addAttribute("page", memberService.findPage(pageable));
		return "/admin/member/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Member member = memberService.find(id);
				if ((member != null && member.getBusiness() != null) || (member != null && member.getBalance().compareTo(BigDecimal.ZERO) > 0)) {
					return Message.error("admin.member.deleteNotAllowed", member.getUsername());
				}
			}
			memberService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Setting;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Business;
import net.shopxx.entity.BusinessAttribute;
import net.shopxx.entity.Member;
import net.shopxx.entity.PlatformSvc;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreRank;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.service.BusinessAttributeService;
import net.shopxx.service.BusinessService;
import net.shopxx.service.CaptchaService;
import net.shopxx.service.MemberService;
import net.shopxx.service.PluginService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.StoreCategoryService;
import net.shopxx.service.StoreRankService;
import net.shopxx.service.StoreService;
import net.shopxx.service.SvcService;

/**
 * Controller - 商家注册
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessRegisterController")
@RequestMapping("/business_register")
public class BusinessRegisterController extends BaseController {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "storeRankServiceImpl")
	private StoreRankService storeRankService;
	@Resource(name = "storeCategoryServiceImpl")
	private StoreCategoryService storeCategoryService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "businessAttributeServiceImpl")
	private BusinessAttributeService businessAttributeService;
	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Resource(name = "svcServiceImpl")
	private SvcService svcService;

	/**
	 * 检查店铺名是否已存在
	 */
	@RequestMapping(value = "/check_name", method = RequestMethod.GET)
	public @ResponseBody boolean checkName(String name, String pName) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		if (!StringUtils.isEmpty(pName) && name.equals(pName)) {
			return true;
		}
		return !storeService.nameExists(name);
	}

	/**
	 * 检查店铺手机是否存在
	 */
	@RequestMapping(value = "/check_mobile", method = RequestMethod.GET)
	public @ResponseBody boolean checkMobile(String mobile, String pMobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		if (!StringUtils.isEmpty(pMobile) && mobile.equals(pMobile)) {
			return true;
		}
		return !storeService.mobileExists(mobile);
	}

	/**
	 * 检查店铺E-mail是否存在
	 */
	@RequestMapping(value = "/check_email", method = RequestMethod.GET)
	public @ResponseBody boolean checkEmail(String email, String pEmail) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		if (!StringUtils.isEmpty(pEmail) && email.equals(pEmail)) {
			return true;
		}
		return !storeService.emailExists(email);
	}

	/**
	 * 检查店铺状态
	 */
	@RequestMapping(value = "/check_store_status", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> checkStoreStatus() {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		if (store != null && store.getStatus().equals(Store.Status.success)) {
			data.put("message", SUCCESS_MESSAGE);
		}else {
			data.put("message", ERROR_MESSAGE);
		}
		return data;
	}

	/**
	 * 计算
	 */
	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> calculate(String paymentPluginId, Integer year, Boolean useBalance) {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		if (store == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal amount = BigDecimal.ZERO;
		if (year != null && store.getStoreRank() != null && store.getStoreRank().getServiceFee() != null) {
			amount = amount.add(store.getStoreRank().getServiceFee().multiply(new BigDecimal(year)));
		}
		if (store.getStoreCategory() != null && BigDecimal.ZERO.compareTo(store.getStoreCategory().getBail()) < 0) {
			amount = amount.add(store.getStoreCategory().getBail());
		}
		PaymentPlugin paymentPlugin = null;
		if (paymentPluginId != null) {
			paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
			if (paymentPlugin == null || !paymentPlugin.getIsEnabled() || amount.compareTo(BigDecimal.ZERO) <= 0) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			data.put("fee", paymentPlugin.calculateFee(amount));
			data.put("amount", paymentPlugin.calculateAmount(amount));
		} else {
			data.put("amount", amount);
			if (useBalance == null || !useBalance) {
				data.put("message", Message.warn("shop.payment.paymentMethodRequired"));
				return data;
			}
			data.put("fee", BigDecimal.ZERO);
		}
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	/**
	 * 商家注册页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String register(RedirectAttributes redirectAttributes, ModelMap model) {
		Member member = memberService.getCurrent();
		if (member == null) {
			return "redirect:/login.jhtml";
		}
		Store store = storeService.getCurrent();
		if (store == null) {
			model.addAttribute("captchaId", UUID.randomUUID().toString());
			model.addAttribute("storeRanks", storeRankService.findList(StoreRank.Type.general, null, null));
			model.addAttribute("storeCategorys", storeCategoryService.findAll());
			model.addAttribute("productCategoryTree", productCategoryService.findTree());
			return "/shop/${theme}/register/business_index";
		} else {
			return "redirect:/business/index.jhtml";
		}
	}

	/**
	 * 商家注册提交
	 */
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public @ResponseBody Message submit(String name, Long storeRankId, String mobile, String email, Long storeCategoryId, Long[] productCategoryIds, String captchaId, String captcha, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		if (!captchaService.isValid(Setting.CaptchaType.businessRegister, captchaId, captcha)) {
			return Message.error("shop.captcha.invalid");
		}
		Member member = memberService.getCurrent();
		if (member == null) {
			return Message.error("shop.common.notLogin");
		}
		if (member.getBusiness() != null) {
			return Message.error("shop.common.invalid");
		}
		Business business = new Business();
		business.setMember(member);
		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				return Message.error("shop.common.invalid");
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		if (!isValid(business, BaseEntity.Save.class)) {
			return Message.error("shop.common.invalid");
		}

		Store store = new Store();
		store.setType(Store.Type.general);
		store.setStatus(Store.Status.pending);
		store.setName(name);
		store.setMobile(mobile);
		store.setEmail(email);
		store.setIsEnabled(true);
		store.setStoreRank(storeRankService.find(storeRankId));
		store.setStoreCategory(storeCategoryService.find(storeCategoryId));
		store.setProductCategorys(new HashSet<ProductCategory>(productCategoryService.findList(productCategoryIds)));

		if (!isValid(store, Store.General.class, BaseEntity.Save.class)) {
			return Message.error("shop.common.invalid");
		}
		storeService.register(store, business, false);
		return Message.success("shop.business.success");
	}

	/**
	 * 生成服务
	 */
	@RequestMapping(value = "/generate_svc", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> generateSvc(Integer year) {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		if (store == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (year != null) {
			BigDecimal amount = BigDecimal.ZERO;
			if (store.getStoreRank() != null && store.getStoreRank().getServiceFee() != null) {
				amount = store.getStoreRank().getServiceFee().multiply(new BigDecimal(year));
			}

			if (amount.compareTo(BigDecimal.ZERO) > 0) {
				PlatformSvc platformSvc = new PlatformSvc();
				platformSvc.setAmount(amount);
				platformSvc.setDurationDays(365 * year);
				platformSvc.setStore(store);
				svcService.save(platformSvc);
				data.put("platformSvcSn", platformSvc.getSn());
			}
		}
		BigDecimal bail = BigDecimal.ZERO;
		if (store.getStoreCategory() != null && BigDecimal.ZERO.compareTo(store.getStoreCategory().getBail()) < 0) {
			bail = store.getStoreCategory().getBail();
		}
		data.put("bail", bail);
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	/**
	 * 商家入驻通知
	 */
	@RequestMapping(value = "/progress", method = RequestMethod.GET)
	public String member(ModelMap model) {
		Store store = storeService.getCurrent();
		if (store == null) {
			return "redirect:/business_register.jhtml";
		} else if (store.isActive()) {
			return "redirect:/business/index.jhtml";
		}
		Store.Status status = store.getStatus();
		if (Store.Status.approved.equals(status)) {
			List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
			if (!paymentPlugins.isEmpty()) {
				model.addAttribute("defaultPaymentPlugin", paymentPlugins.get(0));
				model.addAttribute("paymentPlugins", paymentPlugins);
			}
			model.addAttribute("store", store);
			model.addAttribute("member", store.getBusiness() != null ? store.getBusiness().getMember() : null);
		}
		model.addAttribute("status", status);
		return "/shop/${theme}/common/progress";
	}

	/**
	 * 商家资料编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(ModelMap model) {
		Member member = memberService.getCurrent();
		if (member == null) {
			return "redirect:/login.jhtml";
		}
		Business business = businessService.getCurrent();
		Store store = business != null ? business.getStore() : null;
		if (store == null) {
			return ERROR_VIEW;
		}
		if (Store.Status.failed.equals(store.getStatus())) {
			model.addAttribute("store", store);
			model.addAttribute("business", business);
			model.addAttribute("captchaId", UUID.randomUUID().toString());
			model.addAttribute("storeRanks", storeRankService.findAll());
			model.addAttribute("storeCategorys", storeCategoryService.findAll());
			model.addAttribute("productCategoryTree", productCategoryService.findTree());
			return "/shop/${theme}/register/business_edit";
		} else {
			return "redirect:/business/index.jhtml";
		}
	}

	/**
	 * 商家资料更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody Message update(Long storeId, String name, Long storeRankId, String mobile, String email, Long storeCategoryId, Long[] productCategoryIds, String captchaId, String captcha, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!captchaService.isValid(Setting.CaptchaType.businessRegister, captchaId, captcha)) {
			return Message.error("shop.captcha.invalid");
		}
		Store store = storeService.find(storeId);
		Business business = store != null ? store.getBusiness() : null;
		if (business == null) {
			return Message.error("shop.common.invalid");
		}
		Member member = memberService.getCurrent();
		if (member == null) {
			return Message.error("shop.common.notLogin");
		}
		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				return Message.error("store.common.invalid");
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		if (!isValid(business, BaseEntity.Update.class)) {
			return Message.error("shop.common.invalid");
		}

		store.setStatus(Store.Status.pending);
		store.setName(name);
		store.setMobile(mobile);
		store.setEmail(email);
		store.setStoreRank(storeRankService.find(storeRankId));
		store.setStoreCategory(storeCategoryService.find(storeCategoryId));
		store.setProductCategorys(new HashSet<ProductCategory>(productCategoryService.findList(productCategoryIds)));
		if (!isValid(store, Store.General.class, BaseEntity.Update.class)) {
			return Message.error("shop.common.invalid");
		}
		storeService.update(store, business);
		return Message.success("shop.business.success");
	}

	/**
	 * 店铺开通
	 */
	@RequestMapping(value = "/open", method = RequestMethod.POST)
	public @ResponseBody Message open() {
		Store store = storeService.getCurrent();
		if (store == null) {
			return Message.error("shop.common.invalid");
		}
		storeService.open(store);
		return Message.success("shop.business.success");
	}

	/**
	 * 预存款支付
	 */
	@RequestMapping(value = "/deposit_payment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> depositPayment(Integer year) {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		Member member = memberService.getCurrent();
		if (store == null || member == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal serviceFee = BigDecimal.ZERO;
		BigDecimal bail = BigDecimal.ZERO;
		if (store.getStoreRank().getServiceFee().compareTo(BigDecimal.ZERO) > 0) {
			if (year == null || year <= 0) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			serviceFee = store.getStoreRank().getServiceFee().multiply(new BigDecimal(year));
		}
		if (store.getStoreCategory() != null && BigDecimal.ZERO.compareTo(store.getStoreCategory().getBail()) < 0) {
			bail = store.getStoreCategory().getBail();
		}
		if (member.getBalance() == null || serviceFee.compareTo(BigDecimal.ZERO) == 0 && bail.compareTo(BigDecimal.ZERO) == 0 || member.getBalance().compareTo(serviceFee.add(bail)) <= 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		storeService.open(store, member, year, serviceFee, bail);
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

}
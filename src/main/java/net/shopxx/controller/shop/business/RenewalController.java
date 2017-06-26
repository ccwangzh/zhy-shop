/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Store;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.service.PluginService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 店铺等级续费
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessRenewalController")
@RequestMapping("/business/renewal")
public class RenewalController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;

	/**
	 * 计算
	 */
	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> calculate(String paymentPluginId, Long year) {
		Map<String, Object> data = new HashMap<String, Object>();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		Store store = storeService.getCurrent();
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled() || store == null || year == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal amount = BigDecimal.ZERO;
		if (store.getStoreRank() != null && store.getStoreRank().getServiceFee() != null) {
			amount = amount.add(store.getStoreRank().getServiceFee().multiply(new BigDecimal(year)));
		}
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("fee", paymentPlugin.calculateFee(amount));
		data.put("amount", paymentPlugin.calculateAmount(amount));
		return data;
	}

	/**
	 * 检查支付状态
	 */
	@RequestMapping(value = "/check_pay_status", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> checkStoreStatus() {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		data.put("endDate", store.getEndDate() != null ? store.getEndDate() : 0);
		return data;
	}

	/**
	 * 续费
	 */
	@RequestMapping(value = "/renewal", method = RequestMethod.GET)
	public String renewal(ModelMap model) {
		model.addAttribute("store", storeService.getCurrent());
		List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
		if (!paymentPlugins.isEmpty()) {
			model.addAttribute("defaultPaymentPlugin", paymentPlugins.get(0));
			model.addAttribute("paymentPlugins", paymentPlugins);
		}
		return "/shop/${theme}/business/renewal/renewal";
	}

	/**
	 * 店铺续费
	 */
	@RequestMapping(value = "/store_renewal", method = RequestMethod.POST)
	public @ResponseBody Message storeRenewal(ModelMap model) {
		Store store = storeService.getCurrent();
		if (store == null || store.getStoreRank().getServiceFee().compareTo(BigDecimal.ZERO) > 0) {
			return Message.error("shop.common.invalid");
		}
		store.setEndDate(null);
		store.setIsEnabled(true);
		storeService.update(store);
		return Message.success("shop.business.renewal.storeRenewalSuccee");
	}

}
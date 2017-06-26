/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.entity.PaymentItem;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.entity.PaymentTransaction.LineItem;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.service.PaymentTransactionService;
import net.shopxx.service.PluginService;

/**
 * Controller - 支付
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopPaymentController")
@RequestMapping("/payment")
public class PaymentController extends BaseController {

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Resource(name = "paymentTransactionServiceImpl")
	private PaymentTransactionService paymentTransactionService;

	/**
	 * 插件支付
	 */
	@RequestMapping(value = "/plugin_submit", method = RequestMethod.POST)
	public String pluginSubmit(String paymentPluginId, PaymentItemListForm paymentItemListForm, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			return ERROR_VIEW;
		}
		List<PaymentItem> paymentItems = paymentItemListForm.getPaymentItemList();
		if (CollectionUtils.isEmpty(paymentItems)) {
			return ERROR_VIEW;
		}

		Set<PaymentTransaction.LineItem> lineItems = new HashSet<PaymentTransaction.LineItem>();
		boolean flag = paymentItems.size() > 1 ? true : false;
		for (PaymentItem paymentItem : paymentItems) {
			LineItem lineItem = paymentTransactionService.generate(paymentItem);
			if (lineItem == null) {
				return ERROR_VIEW;
			}
			if (!flag) {
				PaymentTransaction paymentTransaction = paymentTransactionService.generate(lineItem, paymentPlugin);
				switch (paymentItem.getType()) {
				case ORDER_PAYMENT:
					String orderSn = paymentTransaction.getOrder() != null ? paymentTransaction.getOrder().getSn() : null;
					model.addAttribute("parameterMap", paymentPlugin.getParameterMap(paymentTransaction.getSn(), message("shop.payment.orderPaymentDescription", orderSn), request));
					break;
				case SVC_PAYMENT:
					String serviceOrderSn = paymentTransaction.getSvc() != null ? paymentTransaction.getSvc().getSn() : null;
					model.addAttribute("parameterMap", paymentPlugin.getParameterMap(paymentTransaction.getSn(), message("shop.payment.svcPaymentDescription", serviceOrderSn), request));
					break;
				case DEPOSIT_RECHARGE:
					model.addAttribute("parameterMap", paymentPlugin.getParameterMap(paymentTransaction.getSn(), message("shop.payment.depositRechargeDescription"), request));
					break;
				case BAIL_PAYMENT:
					model.addAttribute("parameterMap", paymentPlugin.getParameterMap(paymentTransaction.getSn(), message("shop.payment.bailPaymentDescription"), request));
					break;
				}
			} else {
				lineItems.add(lineItem);
			}
		}
		if (flag) {
			PaymentTransaction parent = paymentTransactionService.generateParent(lineItems, paymentPlugin);
			model.addAttribute("parameterMap", paymentPlugin.getParameterMap(parent.getSn(), message("shop.payment.orderPaymentDescription", parent.getSn()), request));
		}
		model.addAttribute("requestUrl", paymentPlugin.getRequestUrl());
		model.addAttribute("requestMethod", paymentPlugin.getRequestMethod());
		model.addAttribute("requestCharset", paymentPlugin.getRequestCharset());
		if (StringUtils.isNotEmpty(paymentPlugin.getRequestCharset())) {
			response.setContentType("text/html; charset=" + paymentPlugin.getRequestCharset());
		}
		return "/shop/${theme}/payment/plugin_submit";
	}

	/**
	 * 插件通知
	 */
	@RequestMapping("/plugin_notify/{paymentPluginId}/{notifyMethod}")
	public String pluginNotify(@PathVariable String paymentPluginId, @PathVariable PaymentPlugin.NotifyMethod notifyMethod, HttpServletRequest request, ModelMap model) {
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin != null && paymentPlugin.verifyNotify(notifyMethod, request)) {
			String sn = paymentPlugin.getSn(request);
			PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(sn);
			if (paymentTransaction != null) {
				paymentTransactionService.handle(paymentTransaction);
				model.addAttribute("paymentTransaction", paymentTransaction);
				model.addAttribute("notifyMessage", paymentPlugin.getNotifyMessage(notifyMethod, request));
			}
		}
		return "/shop/${theme}/payment/plugin_notify";
	}

	/**
	 * FormBean - 支付项
	 * 
	 * @author SHOP++ Team
	 * @version 5.0
	 */
	public static class PaymentItemListForm {

		/** 支付项 */
		private List<PaymentItem> paymentItemList;

		/**
		 * 获取支付项
		 * 
		 * @return 支付项
		 */
		public List<PaymentItem> getPaymentItemList() {
			return paymentItemList;
		}

		/**
		 * 设置支付项
		 * 
		 * @param paymentItemList
		 *            支付项
		 */
		public void setPaymentItemList(List<PaymentItem> paymentItemList) {
			this.paymentItemList = paymentItemList;
		}
	}

}
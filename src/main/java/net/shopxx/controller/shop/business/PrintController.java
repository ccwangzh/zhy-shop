/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.DeliveryCenter;
import net.shopxx.entity.DeliveryTemplate;
import net.shopxx.entity.Order;
import net.shopxx.entity.Store;
import net.shopxx.service.DeliveryCenterService;
import net.shopxx.service.DeliveryTemplateService;
import net.shopxx.service.OrderService;
import net.shopxx.service.StoreService;

/**
 * Controller - 打印
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessPrintController")
@RequestMapping("/business/print")
public class PrintController extends BaseController {

	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	@Resource(name = "deliveryTemplateServiceImpl")
	private DeliveryTemplateService deliveryTemplateService;
	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 订单打印
	 */
	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Long id, ModelMap model) {
		Order order = orderService.find(id);
		if (order == null || !storeService.getCurrent().equals(order.getStore())) {
			return ERROR_VIEW;
		}

		model.addAttribute("order", order);
		return "/shop/${theme}/business/print/order";
	}

	/**
	 * 购物单打印
	 */
	@RequestMapping(value = "/product", method = RequestMethod.GET)
	public String product(Long id, ModelMap model) {
		Order order = orderService.find(id);
		if (order == null || !storeService.getCurrent().equals(order.getStore())) {
			return ERROR_VIEW;
		}

		model.addAttribute("order", order);
		return "/shop/${theme}/business/print/product";
	}

	/**
	 * 发货单打印
	 */
	@RequestMapping(value = "/shipping", method = RequestMethod.GET)
	public String shipping(Long id, ModelMap model) {
		Order order = orderService.find(id);
		if (order == null || !storeService.getCurrent().equals(order.getStore())) {
			return ERROR_VIEW;
		}

		model.addAttribute("order", order);
		return "/shop/${theme}/business/print/shipping";
	}

	/**
	 * 快递单打印
	 */
	@RequestMapping(value = "/delivery", method = RequestMethod.GET)
	public String delivery(Long orderId, Long deliveryTemplateId, Long deliveryCenterId, ModelMap model) {
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(deliveryTemplateId);
		DeliveryCenter deliveryCenter = deliveryCenterService.find(deliveryCenterId);
		Store currentStore = storeService.getCurrent();
		Order order = orderService.find(orderId);
		if (order == null || !currentStore.equals(order.getStore())) {
			return ERROR_VIEW;
		}
		if (deliveryTemplate == null) {
			deliveryTemplate = deliveryTemplateService.findDefault(currentStore);
		}
		if (deliveryCenter == null) {
			deliveryCenter = deliveryCenterService.findDefault(currentStore);
		}

		model.addAttribute("deliveryTemplates", deliveryTemplateService.findAll(currentStore));
		model.addAttribute("deliveryCenters", deliveryCenterService.findAll(currentStore));
		model.addAttribute("order", order);
		model.addAttribute("deliveryTemplate", deliveryTemplate);
		model.addAttribute("deliveryCenter", deliveryCenter);
		return "/shop/${theme}/business/print/delivery";
	}

}
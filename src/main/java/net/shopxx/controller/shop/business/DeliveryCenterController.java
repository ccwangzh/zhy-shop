/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.DeliveryCenter;
import net.shopxx.entity.Store;
import net.shopxx.service.AreaService;
import net.shopxx.service.DeliveryCenterService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 发货点
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessDeliveryCenterController")
@RequestMapping("/business/delivery_center")
public class DeliveryCenterController extends BaseController {

	@Resource(name = "deliveryCenterServiceImpl")
	private DeliveryCenterService deliveryCenterService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "/shop/${theme}/business/delivery_center/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(DeliveryCenter deliveryCenter, Long areaId, Model model, RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea(areaService.find(areaId));
		if (!isValid(deliveryCenter)) {
			return ERROR_VIEW;
		}
		deliveryCenter.setAreaName(null);
		deliveryCenter.setStore(storeService.getCurrent());
		deliveryCenterService.save(deliveryCenter);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, Model model) {
		DeliveryCenter deliveryCenter = deliveryCenterService.find(id);
		if (deliveryCenter == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(deliveryCenter.getStore())) {
			return ERROR_VIEW;
		}
		model.addAttribute("deliveryCenter", deliveryCenter);
		return "/shop/${theme}/business/delivery_center/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(DeliveryCenter deliveryCenter, Long areaId, Long id, RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea(areaService.find(areaId));
		if (!isValid(deliveryCenter)) {
			return ERROR_VIEW;
		}
		DeliveryCenter pDeliveryCenter = deliveryCenterService.find(id);
		if (pDeliveryCenter == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(pDeliveryCenter.getStore())) {
			return ERROR_VIEW;
		}
		deliveryCenterService.update(deliveryCenter, "store", "areaName");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model, Pageable pageable) {
		model.addAttribute("page", deliveryCenterService.findPage(storeService.getCurrent(), pageable));
		return "/shop/${theme}/business/delivery_center/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		Store store = storeService.getCurrent();
		for (Long id : ids) {
			DeliveryCenter deliveryCenter = deliveryCenterService.find(id);
			if (deliveryCenter == null || !store.equals(deliveryCenter.getStore())) {
				return ERROR_MESSAGE;
			}
			deliveryCenterService.delete(id);
		}
		return SUCCESS_MESSAGE;
	}

}
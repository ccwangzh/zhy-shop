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
import net.shopxx.entity.DeliveryTemplate;
import net.shopxx.entity.Store;
import net.shopxx.service.DeliveryTemplateService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 快递单模板
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessDeliveryTemplateController")
@RequestMapping("/business/delivery_template")
public class DeliveryTemplateController extends BaseController {

	@Resource(name = "deliveryTemplateServiceImpl")
	private DeliveryTemplateService deliveryTemplateService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Pageable pageable) {
		return "/shop/${theme}/business/delivery_template/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(DeliveryTemplate deliveryTemplate, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryTemplate)) {
			return ERROR_VIEW;
		}
		deliveryTemplate.setStore(storeService.getCurrent());
		deliveryTemplateService.save(deliveryTemplate);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String eidt(Long id, Model model) {
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(id);
		if (deliveryTemplate == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(deliveryTemplate.getStore())) {
			return ERROR_VIEW;
		}
		model.addAttribute("deliveryTemplate", deliveryTemplate);
		return "/shop/${theme}/business/delivery_template/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String udpate(DeliveryTemplate deliveryTemplate, Long id, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryTemplate)) {
			return ERROR_VIEW;
		}
		DeliveryTemplate pDeliveryTemplate = deliveryTemplateService.find(id);
		if (pDeliveryTemplate == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(pDeliveryTemplate.getStore())) {
			return ERROR_VIEW;
		}
		deliveryTemplateService.update(deliveryTemplate, "store");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, Model model) {
		model.addAttribute("page", deliveryTemplateService.findPage(storeService.getCurrent(), pageable));
		return "/shop/${theme}/business/delivery_template/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		Store store = storeService.getCurrent();
		for (Long id : ids) {
			DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(id);
			if (deliveryTemplate == null || !store.equals(deliveryTemplate.getStore())) {
				return ERROR_MESSAGE;
			}
			deliveryTemplateService.delete(id);
		}
		return SUCCESS_MESSAGE;
	}
}
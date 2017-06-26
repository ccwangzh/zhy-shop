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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.DefaultFreightConfig;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.entity.Store;
import net.shopxx.service.DefaultFreightConfigService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.service.StoreService;

/**
 * Controller - 运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessFreightConfigController")
@RequestMapping("/business/freight_config")
public class FreightConfigController extends BaseController {

	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;
	@Resource(name = "defaultFreightConfigServiceImpl")
	private DefaultFreightConfigService defaultFreightConfigService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("store", storeService.getCurrent());
		model.addAttribute("page", shippingMethodService.findPage(pageable));
		return "/shop/${theme}/business/freight_config/list";
	}

	/**
	 * 默认运费配置
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long shippingMethodId, ModelMap model) {
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		DefaultFreightConfig defaultFreightConfig = defaultFreightConfigService.find(shippingMethod, storeService.getCurrent());
		if (null != defaultFreightConfig) {
			model.addAttribute("defaultFreightConfig", defaultFreightConfig);
		} else {
			model.addAttribute("defaultFreightConfig", null);
		}
		model.addAttribute("shippingMethod", shippingMethod);
		return "/shop/${theme}/business/freight_config/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(DefaultFreightConfig defaultFreightConfig, Long id, Long shippingMethodId, RedirectAttributes redirectAttributes) {
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Store store = storeService.getCurrent();
		if (id != null) {
			DefaultFreightConfig pDefaultFreightConfig = defaultFreightConfigService.find(id);
			if (!pDefaultFreightConfig.getStore().equals(store)) {
				return ERROR_VIEW;
			}
		}
		defaultFreightConfigService.update(defaultFreightConfig, store, shippingMethod);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}
}
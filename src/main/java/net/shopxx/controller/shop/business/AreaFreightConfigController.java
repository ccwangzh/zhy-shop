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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Area;
import net.shopxx.entity.AreaFreightConfig;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.entity.Store;
import net.shopxx.service.AreaFreightConfigService;
import net.shopxx.service.AreaService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.service.StoreService;

/**
 * Controller - 地区运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessAreaFreightConfigController")
@RequestMapping("/business/area_freight_config")
public class AreaFreightConfigController extends BaseController {

	@Resource(name = "areaFreightConfigServiceImpl")
	private AreaFreightConfigService areaFreightConfigService;
	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 检查地区是否唯一
	 */
	@RequestMapping(value = "/check_area", method = RequestMethod.GET)
	public @ResponseBody boolean checkArea(Long shippingMethodId, Long previousAreaId, Long areaId) {
		if (shippingMethodId == null || areaId == null) {
			return false;
		}
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Area previousArea = areaService.find(previousAreaId);
		Area area = areaService.find(areaId);
		if (shippingMethod == null || area == null) {
			return false;
		}
		return areaFreightConfigService.unique(shippingMethod, storeService.getCurrent(), previousArea, area);
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Long shippingMethodId, ModelMap model) {
		model.addAttribute("shippingMethod", shippingMethodService.find(shippingMethodId));
		return "/shop/${theme}/business/area_freight_config/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(AreaFreightConfig areaFreightConfig, Long shippingMethodId, Long areaId, RedirectAttributes redirectAttributes) {
		Store store = storeService.getCurrent();
		Area area = areaService.find(areaId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		areaFreightConfig.setArea(area);
		areaFreightConfig.setShippingMethod(shippingMethod);
		areaFreightConfig.getStores().add(store);
		if (!isValid(areaFreightConfig, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (areaFreightConfigService.exists(shippingMethod, store, area)) {
			return ERROR_VIEW;
		}
		areaFreightConfigService.save(areaFreightConfig);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml?shippingMethodId=" + shippingMethod.getId();
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("areaFreightConfig", areaFreightConfigService.find(id));
		return "/shop/${theme}/business/area_freight_config/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(AreaFreightConfig areaFreightConfig, Long id, Long areaId, RedirectAttributes redirectAttributes) {
		Store store = storeService.getCurrent();
		Area area = areaService.find(areaId);
		areaFreightConfig.setArea(area);
		if (!isValid(areaFreightConfig, BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		AreaFreightConfig pAreaFreightConfig = areaFreightConfigService.find(id);
		if (!pAreaFreightConfig.getStores().contains(store)) {
			return ERROR_VIEW;
		}
		if (!areaFreightConfigService.unique(pAreaFreightConfig.getShippingMethod(), store, pAreaFreightConfig.getArea(), area)) {
			return ERROR_VIEW;
		}
		areaFreightConfigService.update(areaFreightConfig, "stores", "shippingMethod");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml?shippingMethodId=" + pAreaFreightConfig.getShippingMethod().getId();
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, Long shippingMethodId, ModelMap model) {
		Store store = storeService.getCurrent();
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		model.addAttribute("shippingMethod", shippingMethod);
		model.addAttribute("page", areaFreightConfigService.findPage(shippingMethod, store, pageable));
		return "/shop/${theme}/business/area_freight_config/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		Store store = storeService.getCurrent();
		for (Long id : ids) {
			AreaFreightConfig areaFreightConfig = areaFreightConfigService.find(id);
			if (!store.getAreaFreightConfigs().contains(areaFreightConfig)) {
				return ERROR_MESSAGE;
			}
			areaFreightConfigService.delete(areaFreightConfig);
		}
		return SUCCESS_MESSAGE;
	}
}
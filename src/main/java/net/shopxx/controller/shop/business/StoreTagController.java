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
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreTag;
import net.shopxx.service.StoreService;
import net.shopxx.service.StoreTagService;

/**
 * Controller - 商家中心 - 店铺标签
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessStoreTagController")
@RequestMapping("/business/store_tag")
public class StoreTagController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "storeTagServiceImpl")
	private StoreTagService storeTagService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		return "/shop/${theme}/business/store_tag/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(StoreTag storeTag, RedirectAttributes redirectAttributes) {
		Store store = storeService.getCurrent();
		if (!isValid(storeTag)) {
			return ERROR_VIEW;
		}
		storeTag.setStore(store);
		storeTag.setGoods(null);
		storeTagService.save(storeTag);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		StoreTag storeTag = storeTagService.find(id);
		if (storeTag == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(storeTag.getStore())) {
			return ERROR_VIEW;
		}
		model.addAttribute("storeTag", storeTag);
		return "/shop/${theme}/business/store_tag/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(StoreTag storeTag, Long id, RedirectAttributes redirectAttributes) {
		if (!isValid(storeTag)) {
			return ERROR_VIEW;
		}
		StoreTag pStoreTag = storeTagService.find(id);
		if (pStoreTag == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(pStoreTag.getStore())) {
			return ERROR_VIEW;
		}
		storeTagService.update(storeTag, "store", "goods");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", storeTagService.findPage(storeService.getCurrent(), pageable));
		return "/shop/${theme}/business/store_tag/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		Store store = storeService.getCurrent();
		for (Long id : ids) {
			StoreTag storeTag = storeTagService.find(id);
			if (storeTag == null || !store.equals(storeTag.getStore())) {
				return ERROR_MESSAGE;
			}
			storeTagService.delete(id);
		}
		return SUCCESS_MESSAGE;
	}

}
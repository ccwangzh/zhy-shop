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
import net.shopxx.entity.InstantMessage;
import net.shopxx.entity.Store;
import net.shopxx.service.InstantMessageService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 即时通讯
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessInstantMessageController")
@RequestMapping("/business/instant_message")
public class InstantMessageController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "instantMessageServiceImpl")
	private InstantMessageService instantMessageService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("types", InstantMessage.Type.values());
		return "/shop/${theme}/business/instant_message/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(InstantMessage instantMessage, RedirectAttributes redirectAttributes) {
		Store store = storeService.getCurrent();
		if (!isValid(instantMessage)) {
			return ERROR_VIEW;
		}
		instantMessage.setStore(store);
		instantMessageService.save(instantMessage);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		InstantMessage instantMessage = instantMessageService.find(id);
		if (instantMessage == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(instantMessage.getStore())) {
			return ERROR_VIEW;
		}
		model.addAttribute("types", InstantMessage.Type.values());
		model.addAttribute("instantMessage", instantMessage);
		return "/shop/${theme}/business/instant_message/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(InstantMessage instantMessage, Long id, RedirectAttributes redirectAttributes) {
		if (!isValid(instantMessage)) {
			return ERROR_VIEW;
		}
		InstantMessage pInstantMessage = instantMessageService.find(id);
		if (pInstantMessage == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(pInstantMessage.getStore())) {
			return ERROR_VIEW;
		}
		instantMessageService.update(instantMessage, "store");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", instantMessageService.findPage(storeService.getCurrent(), pageable));
		return "/shop/${theme}/business/instant_message/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		Store store = storeService.getCurrent();
		for (Long id : ids) {
			InstantMessage instantMessage = instantMessageService.find(id);
			if (instantMessage == null || !store.equals(instantMessage.getStore())) {
				return ERROR_MESSAGE;
			}
			instantMessageService.delete(id);
		}
		return SUCCESS_MESSAGE;
	}

}
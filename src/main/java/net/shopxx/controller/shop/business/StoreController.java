/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Business;
import net.shopxx.entity.Store;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 店铺管理
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessStoreController")
@RequestMapping("/business/store")
public class StoreController extends BaseController {

	/** 最大广告图片数 */
	public static final Integer MAX_ADVERTISING_IMAGES_COUNT = 5;

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

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
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(ModelMap model) {
		Store store = storeService.getCurrent();
		if (store == null) {
			return ERROR_VIEW;
		}
		model.addAttribute("store", store);
		return "/shop/${theme}/business/store/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Store store, RedirectAttributes redirectAttributes) {
		if (store == null || store.getAdvertisingImages() != null && store.getAdvertisingImages().size() > MAX_ADVERTISING_IMAGES_COUNT) {
			return ERROR_VIEW;
		}
		storeService.filter(store.getAdvertisingImages());
		Store pStore = storeService.find(store.getId());
		Business business = pStore.getBusiness();
		if (store == null || business == null) {
			return ERROR_VIEW;
		}
		store.setType(pStore.getType());
		store.setIsEnabled(pStore.getIsEnabled());
		store.setEndDate(pStore.getEndDate());
		store.setStoreRank(pStore.getStoreRank());
		store.setStoreCategory(pStore.getStoreCategory());
		store.setProductCategorys(pStore.getProductCategorys());
		if (!isValid(store, getValidationGroup(store.getType()), BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		storeService.update(store, business);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:edit.jhtml";
	}

	/**
	 * 根据类型获取验证组
	 * 
	 * @param type
	 *            类型
	 * @return 验证组
	 */
	private Class<?> getValidationGroup(Store.Type type) {
		Assert.notNull(type);

		switch (type) {
		case general:
			return Store.General.class;
		case self:
			return Store.Self.class;
		}
		return null;
	}
}
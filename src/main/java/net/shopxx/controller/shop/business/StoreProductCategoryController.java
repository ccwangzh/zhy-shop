/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.List;
import java.util.Set;

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
import net.shopxx.entity.Goods;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreProductCategory;
import net.shopxx.service.StoreProductCategoryService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 店铺商品分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessStoreProductCategoryController")
@RequestMapping("/business/store_product_category")
public class StoreProductCategoryController extends BaseController {

	@Resource(name = "storeProductCategoryServiceImpl")
	private StoreProductCategoryService storeProductCategoryService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(storeService.getCurrent()));
		return "/shop/${theme}/business/store_product_category/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(StoreProductCategory storeProductCategory, Long parentId, RedirectAttributes redirectAttributes) {
		StoreProductCategory pStoreProductCategory = storeProductCategoryService.find(parentId);
		Store store = storeService.getCurrent();
		if (parentId != null && pStoreProductCategory != null && pStoreProductCategory.getParent() != null) {
			if (!store.equals(pStoreProductCategory.getStore())) {
				return ERROR_VIEW;
			}
		}
		storeProductCategory.setParent(pStoreProductCategory);
		if (!isValid(storeProductCategory)) {
			return ERROR_VIEW;
		}
		storeProductCategory.setTreePath(null);
		storeProductCategory.setGrade(null);
		storeProductCategory.setChildren(null);
		storeProductCategory.setStore(store);
		storeProductCategoryService.save(storeProductCategory);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(id);
		Store store = storeService.getCurrent();
		if (storeProductCategory == null) {
			return ERROR_VIEW;
		}
		if (!store.equals(storeProductCategory.getStore())) {
			return ERROR_VIEW;
		}
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		model.addAttribute("storeProductCategory", storeProductCategory);
		model.addAttribute("children", storeProductCategoryService.findChildren(storeProductCategory, store, true, null));
		return "/shop/${theme}/business/store_product_category/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(StoreProductCategory storeProductCategory, Long id, Long parentId, RedirectAttributes redirectAttributes) {
		StoreProductCategory pParen = storeProductCategoryService.find(parentId);
		storeProductCategory.setParent(pParen);
		if (!isValid(storeProductCategory)) {
			return ERROR_VIEW;
		}
		StoreProductCategory pStoreProductCategory = storeProductCategoryService.find(id);
		if (pStoreProductCategory == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		if (!pStoreProductCategory.getStore().equals(store)) {
			return ERROR_VIEW;
		}
		if (storeProductCategory.getParent() != null) {
			StoreProductCategory parent = storeProductCategory.getParent();
			if (parent.equals(storeProductCategory)) {
				return ERROR_VIEW;
			}
			List<StoreProductCategory> children = storeProductCategoryService.findChildren(storeProductCategory, store, true, null);
			if (children != null && children.contains(parent)) {
				return ERROR_VIEW;
			}
			if (!store.equals(pParen.getStore())) {
				return ERROR_VIEW;
			}
		}
		storeProductCategoryService.update(storeProductCategory, "grade", "treePath", "store", "children", "goods");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("store", storeService.getCurrent());
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(storeService.getCurrent()));
		return "/shop/${theme}/business/store_product_category/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long id) {
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(id);
		if (storeProductCategory == null) {
			return ERROR_MESSAGE;
		}
		Store store = storeService.getCurrent();
		if (!store.equals(storeProductCategory.getStore())) {
			return ERROR_MESSAGE;
		}
		Set<StoreProductCategory> children = storeProductCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			return Message.error("shop.business.storeProductCategory.deleteExistChildrenNotAllowed");
		}
		Set<Goods> goods = storeProductCategory.getGoods();
		if (goods != null && !goods.isEmpty()) {
			return Message.error("shop.business.storeProductCategory.deleteExistProductNotAllowed");
		}
		storeProductCategoryService.delete(id);
		return SUCCESS_MESSAGE;
	}

}
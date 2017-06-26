/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.CategoryApplication;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Store;
import net.shopxx.service.CategoryApplicationService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 经营分类申请
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessCategoryApplicationController")
@RequestMapping("/business/category_application")
public class CategoryApplicationController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "categoryApplicationServiceImpl")
	private CategoryApplicationService categoryApplicationService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		List<ProductCategory> productCategoryList = productCategoryService.findTree(storeService.getCurrent());
		if (productCategoryList.size() > 0) {
			List<Long> productCategorys = new ArrayList<Long>();
			for (ProductCategory productCategory : productCategoryList) {
				productCategorys.add(productCategory.getId());
			}
			model.addAttribute("productCategoryShop", productCategorys);
		}
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("store", storeService.getCurrent());
		return "/shop/${theme}/business/category_application/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Long productCategoryId, RedirectAttributes redirectAttributes) {
		if (productCategoryId == null) {
			return ERROR_VIEW;
		}
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Store store = storeService.getCurrent();
		if (productCategory == null || store.getStatus() != Store.Status.success) {
			return ERROR_VIEW;
		}
		if (store.getProductCategorys().contains(productCategory)) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:list.jhtml";
		}
		CategoryApplication categoryApplication = new CategoryApplication();
		categoryApplication.setStatus(CategoryApplication.Status.pending);
		if (store.getType().equals(Store.Type.self)) {
			categoryApplication.setRate(productCategory.getSelfRate());
		} else {
			categoryApplication.setRate(productCategory.getGeneralRate());
		}
		categoryApplication.setStore(store);
		categoryApplication.setProductCategory(productCategory);
		categoryApplicationService.save(categoryApplication);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		Store store = storeService.getCurrent();
		model.addAttribute("store", store);
		model.addAttribute("page", categoryApplicationService.findPage(store, pageable));
		return "/shop/${theme}/business/category_application/list";
	}
}
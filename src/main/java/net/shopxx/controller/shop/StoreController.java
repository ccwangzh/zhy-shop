/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.Pageable;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreProductCategory;
import net.shopxx.exception.ResourceNotFoundException;
import net.shopxx.service.GoodsService;
import net.shopxx.service.StoreProductCategoryService;
import net.shopxx.service.StoreService;
import net.shopxx.service.StoreTagService;

/**
 * Controller - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopStoreController")
@RequestMapping("/store")
public class StoreController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "storeProductCategoryServiceImpl")
	private StoreProductCategoryService storeProductCategoryService;
	@Resource(name = "storeTagServiceImpl")
	private StoreTagService storeTagService;

	/**
	 * 首页
	 */
	@RequestMapping(value = "/store", method = RequestMethod.GET)
	public String index(Long storeId, ModelMap model) {
		Store store = storeService.find(storeId);
		if (store == null) {
			return ERROR_VIEW;
		}
		model.addAttribute("storeTags", storeTagService.findList(store, true));
		model.addAttribute("store", store);
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		return "/shop/${theme}/store/index";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list/{storeProductCategoryId}", method = RequestMethod.GET)
	public String list(@PathVariable Long storeProductCategoryId, Long storeId, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Integer pageNumber, Integer pageSize, ModelMap model) {
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		if (storeProductCategory == null) {
			throw new ResourceNotFoundException();
		}
		Store store = storeService.find(storeId);
		if (store == null) {
			return ERROR_VIEW;
		}
		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", Goods.OrderType.values());
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		model.addAttribute("storeProductCategory", storeProductCategory);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("store", store);
		model.addAttribute("page", goodsService.findPage(store, null, storeProductCategory, true, Goods.Status.onTheshelf, true, orderType, startPrice, endPrice, pageable));
		return "/shop/${theme}/store/list";
	}

	/**
	 * 搜索
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(Long storeId, String keyword, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Integer pageNumber, Integer pageSize, ModelMap model) {
		Store store = storeService.find(storeId);
		if (store == null) {
			return ERROR_VIEW;
		}
		if (StringUtils.isEmpty(keyword)) {
			return ERROR_VIEW;
		}
		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		model.addAttribute("storeKeyword", keyword);
		model.addAttribute("store", store);
		model.addAttribute("orderTypes", Goods.OrderType.values());
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("page", goodsService.findPage(store, keyword, null, true, Goods.Status.onTheshelf, true, orderType, startPrice, endPrice, pageable));
		return "/shop/${theme}/store/search";
	}
}
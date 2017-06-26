/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Business;
import net.shopxx.entity.Operator;
import net.shopxx.entity.Product;
import net.shopxx.entity.StockLog;
import net.shopxx.entity.Store;
import net.shopxx.service.BusinessService;
import net.shopxx.service.ProductService;
import net.shopxx.service.StockLogService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 库存
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessStockController")
@RequestMapping("/business/stock")
public class StockController extends BaseController {

	@Resource(name = "stockLogServiceImpl")
	private StockLogService stockLogService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 商品选择
	 */
	@RequestMapping(value = "/product_select", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> productSelect(@RequestParam("q") String keyword, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		Business business = businessService.getCurrent();
		List<Product> products = productService.search(business.getStore(), null, keyword, null, count);
		for (Product product : products) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("sn", product.getSn());
			item.put("name", product.getName());
			item.put("stock", product.getStock());
			item.put("allocatedStock", product.getAllocatedStock());
			item.put("specifications", product.getSpecifications());
			data.add(item);
		}
		return data;
	}

	/**
	 * 入库
	 */
	@RequestMapping(value = "/stock_in", method = RequestMethod.GET)
	public String stockIn(String productSn, ModelMap model) {
		Product product = productService.findBySn(productSn);
		Store store = storeService.getCurrent();
		if (product != null) {
			if (!store.hasContained(product)) {
				return ERROR_VIEW;
			}
			model.addAttribute("product", product);
		}
		return "/shop/${theme}/business/stock/stock_in";
	}

	/**
	 * 入库
	 */
	@RequestMapping(value = "/stock_in", method = RequestMethod.POST)
	public String stockIn(String productSn, Integer quantity, String memo, RedirectAttributes redirectAttributes) {
		Product product = productService.findBySn(productSn);
		Store store = storeService.getCurrent();
		if (!store.hasContained(product)) {
			return ERROR_VIEW;
		}
		if (quantity == null || quantity <= 0) {
			return ERROR_VIEW;
		}
		productService.addStock(product, quantity, StockLog.Type.stockIn, new Operator(store.getBusiness()), memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log.jhtml";
	}

	/**
	 * 出库
	 */
	@RequestMapping(value = "/stock_out", method = RequestMethod.GET)
	public String stockOut(String productSn, ModelMap model) {
		Product product = productService.findBySn(productSn);
		Store store = storeService.getCurrent();
		if (product != null) {
			if (!store.hasContained(product)) {
				return ERROR_VIEW;
			}
			model.addAttribute("product", product);
		}
		return "/shop/${theme}/business/stock/stock_out";
	}

	/**
	 * 出库
	 */
	@RequestMapping(value = "/stock_out", method = RequestMethod.POST)
	public String stockOut(String productSn, Integer quantity, String memo, RedirectAttributes redirectAttributes) {
		Product product = productService.findBySn(productSn);
		Store store = storeService.getCurrent();
		if (!store.hasContained(product)) {
			return ERROR_VIEW;
		}
		if (quantity == null || quantity <= 0) {
			return ERROR_VIEW;
		}
		if (product.getStock() - quantity < 0) {
			return ERROR_VIEW;
		}
		productService.addStock(product, -quantity, StockLog.Type.stockOut, new Operator(store.getBusiness()), memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log.jhtml";
	}

	/**
	 * 记录
	 */
	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public String log(Pageable pageable, ModelMap model) {
		Business business = businessService.getCurrent();
		model.addAttribute("page", stockLogService.findPage(business.getStore(), pageable));
		return "/shop/${theme}/business/stock/log";
	}

}
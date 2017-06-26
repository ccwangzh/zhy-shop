/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Store;
import net.shopxx.service.GoodsService;
import net.shopxx.service.OrderService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessController")
@RequestMapping("/business")
public class BusinessController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	/**
	 * 首页
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		Store store = storeService.getCurrent();
		model.addAttribute("store", store);
		model.addAttribute("goodsCount", goodsService.count(null, store, null, null, null, null, null, null, null));
		model.addAttribute("marketableGoodsCount", goodsService.count(null, store, null, null, Goods.Status.onTheshelf, null, null, null, null));
		model.addAttribute("outOfStockGoodsCount", goodsService.count(null, store, null, null, null, null, null, true, null));
		model.addAttribute("notActiveGoodsCount", goodsService.count(null, store, null, false, null, null, null, null, null));
		model.addAttribute("isTopGoodsCount", goodsService.count(null, store, null, null, null, null, true, null, null));
		model.addAttribute("isStockAlertGoodsCount", goodsService.count(null, store, null, null, null, null, null, null, true));

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginCalendar.set(Calendar.MINUTE, beginCalendar.getActualMinimum(Calendar.MINUTE));
		beginCalendar.set(Calendar.SECOND, beginCalendar.getActualMinimum(Calendar.SECOND));
		beginCalendar.add(Calendar.DAY_OF_MONTH, -1);
		Date beginDate = beginCalendar.getTime();

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE, beginCalendar.getActualMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND, beginCalendar.getActualMaximum(Calendar.SECOND));
		endCalendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endDate = endCalendar.getTime();

		model.addAttribute("yesterdayOrderCount", orderService.CompleteOrderCount(store, beginDate, endDate));
		model.addAttribute("yesterdayOrderAmount", orderService.completeOrderAmount(store, beginDate, endDate));

		Calendar beginMonthCalendar = Calendar.getInstance();
		beginMonthCalendar.set(Calendar.HOUR_OF_DAY, beginMonthCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginMonthCalendar.set(Calendar.MINUTE, beginMonthCalendar.getActualMinimum(Calendar.MINUTE));
		beginMonthCalendar.set(Calendar.SECOND, beginMonthCalendar.getActualMinimum(Calendar.SECOND));
		beginMonthCalendar.add(Calendar.MONTH, -1);
		Date beginMonthDate = beginCalendar.getTime();

		Calendar endMonthCalendar = Calendar.getInstance();
		endMonthCalendar.set(Calendar.HOUR_OF_DAY, beginMonthCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endMonthCalendar.set(Calendar.MINUTE, beginMonthCalendar.getActualMaximum(Calendar.MINUTE));
		endMonthCalendar.set(Calendar.SECOND, beginMonthCalendar.getActualMaximum(Calendar.SECOND));
		endMonthCalendar.add(Calendar.MONTH, -1);
		Date endMonthDate = endCalendar.getTime();
		model.addAttribute("monthOrderCount", orderService.CompleteOrderCount(store, beginMonthDate, endMonthDate));
		model.addAttribute("monthOrderAmount", orderService.completeOrderAmount(store, beginMonthDate, endMonthDate));
		return "/shop/${theme}/business/index";
	}

}
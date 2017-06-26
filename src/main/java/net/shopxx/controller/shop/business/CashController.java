/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Business;
import net.shopxx.entity.Cash;
import net.shopxx.service.BusinessService;
import net.shopxx.service.CashService;

/**
 * Controller - 商家中心 - 提现
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessCashController")
@RequestMapping("/business/cash")
public class CashController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "cashServiceImpl")
	private CashService cashService;

	/**
	 * 检查余额
	 */
	@RequestMapping(value = "/check_balance", method = RequestMethod.GET)
	public @ResponseBody boolean checkBalance(BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		Business business = businessService.getCurrent();
		return business.getMember().getBalance().compareTo(amount) >= 0;
	}

	/**
	 * 申请提现
	 */
	@RequestMapping(value = "/application", method = RequestMethod.GET)
	public String cash() {
		return "/shop/${theme}/business/cash/application";
	}

	/**
	 * 申请提现
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String applyCash(Cash cash) {
		Business business = businessService.getCurrent();
		if (business.getMember().getBalance().compareTo(cash.getAmount()) < 0) {
			return ERROR_VIEW;
		}
		if (!isValid(cash)) {
			return ERROR_VIEW;
		}
		cashService.applyCash(cash, business);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Business business = businessService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", cashService.findPage(business, pageable));
		return "/shop/${theme}/business/cash/list";
	}

}
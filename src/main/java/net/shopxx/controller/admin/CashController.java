/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Cash;
import net.shopxx.entity.Operator;
import net.shopxx.service.AdminService;
import net.shopxx.service.CashService;

/**
 * Controller - 提现
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminCashController")
@RequestMapping("/admin/cash")
public class CashController extends BaseController {

	@Resource(name = "cashServiceImpl")
	private CashService cashService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	/**
	 * 审核
	 */
	@RequestMapping(value = "/review", method = RequestMethod.POST)
	public String review(Long id, Boolean isPassed, RedirectAttributes redirectAttributes) {
		Cash cash = cashService.find(id);
		if (isPassed == null || cash == null || !Cash.Status.pending.equals(cash.getStatus())) {
			return ERROR_VIEW;
		}
		cashService.review(cash, isPassed, new Operator(adminService.getCurrent()));
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", cashService.findPage(pageable));
		return "/admin/cash/list";
	}

}
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
import net.shopxx.entity.CategoryApplication;
import net.shopxx.service.CategoryApplicationService;

/**
 * Controller - 经营分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminCategoryApplicationController")
@RequestMapping("/admin/category_application")
public class CategoryApplicationController extends BaseController {

	@Resource(name = "categoryApplicationServiceImpl")
	private CategoryApplicationService categoryApplicationService;

	/**
	 * 审核
	 */
	@RequestMapping(value = "/review", method = RequestMethod.POST)
	public String review(Long id, Boolean isPassed, RedirectAttributes redirectAttributes) {
		CategoryApplication categoryApplication = categoryApplicationService.find(id);
		if (isPassed == null || categoryApplication == null || !CategoryApplication.Status.pending.equals(categoryApplication.getStatus())) {
			return ERROR_VIEW;
		}
		categoryApplicationService.review(categoryApplication, isPassed);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", categoryApplicationService.findPage(pageable));
		return "/admin/category_application/list";
	}

}
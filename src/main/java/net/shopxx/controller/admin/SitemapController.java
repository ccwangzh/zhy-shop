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

import net.shopxx.TemplateConfig;
import net.shopxx.service.StaticService;
import net.shopxx.util.SystemUtils;

/**
 * Controller - Sitemap
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminSitemapController")
@RequestMapping("/admin/sitemap")
public class SitemapController extends BaseController {

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	/**
	 * 生成Sitemap
	 */
	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public String generate(ModelMap model) {
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("sitemapIndex");
		model.addAttribute("sitemapIndexPath", templateConfig.resolveStaticPath());
		return "/admin/sitemap/generate";
	}

	/**
	 * 生成Sitemap
	 */
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public String generate(RedirectAttributes redirectAttributes) {
		staticService.generateSitemap();
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:generate.jhtml";
	}

}
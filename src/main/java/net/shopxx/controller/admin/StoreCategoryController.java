/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.StoreCategory;
import net.shopxx.service.StoreCategoryService;

/**
 * Controller - 店铺分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminStoreCategoryController")
@RequestMapping("/admin/store_category")
public class StoreCategoryController extends BaseController {

	@Resource(name = "storeCategoryServiceImpl")
	private StoreCategoryService storeCategoryService;

	/**
	 * 检查用户名是否存在
	 */
	@RequestMapping(value = "/check_name", method = RequestMethod.GET)
	public @ResponseBody boolean checkName(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		return !storeCategoryService.nameExists(name);
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		return "/admin/store_category/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(StoreCategory storeCategory, RedirectAttributes redirectAttributes) {
		if (!isValid(storeCategory)) {
			return ERROR_VIEW;
		}
		storeCategory.setStores(null);
		storeCategoryService.save(storeCategory);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("storeCategory", storeCategoryService.find(id));
		return "/admin/store_category/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(StoreCategory storeCategory, RedirectAttributes redirectAttributes) {
		if (!isValid(storeCategory)) {
			return ERROR_VIEW;
		}
		storeCategoryService.update(storeCategory, "stores");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", storeCategoryService.findPage(pageable));
		return "/admin/store_category/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				StoreCategory storeCategory = storeCategoryService.find(id);
				if (storeCategory != null && storeCategory.getStores() != null && !storeCategory.getStores().isEmpty()) {
					return Message.error("admin.storeCategory.deleteExistNotAllowed", storeCategory.getName());
				}
			}
			long totalCount = storeCategoryService.count();
			if (ids.length >= totalCount) {
				return Message.error("admin.common.deleteAllNotAllowed");
			}
			storeCategoryService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

}
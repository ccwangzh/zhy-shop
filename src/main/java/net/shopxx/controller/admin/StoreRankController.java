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
import net.shopxx.entity.StoreRank;
import net.shopxx.service.StoreRankService;

/**
 * Controller - 店铺等级
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminStoreRankController")
@RequestMapping("/admin/store_rank")
public class StoreRankController extends BaseController {

	@Resource(name = "storeRankServiceImpl")
	private StoreRankService storeRankService;

	/**
	 * 检查名称是否唯一
	 */
	@RequestMapping(value = "/check_name", method = RequestMethod.GET)
	public @ResponseBody boolean checkName(StoreRank.Type type, String previousName, String name) {
		if (type == null || StringUtils.isEmpty(name)) {
			return false;
		}
		return storeRankService.nameUnique(type, previousName, name);
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("types", StoreRank.Type.values());
		return "/admin/store_rank/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(StoreRank storeRank, RedirectAttributes redirectAttributes) {
		if (!isValid(storeRank)) {
			return ERROR_VIEW;
		}
		if (storeRankService.nameExists(storeRank.getType(), storeRank.getName())) {
			return ERROR_VIEW;
		}
		storeRank.setStores(null);
		storeRankService.save(storeRank);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("storeRank", storeRankService.find(id));
		return "/admin/store_rank/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(StoreRank storeRank, RedirectAttributes redirectAttributes) {
		if (!isValid(storeRank)) {
			return ERROR_VIEW;
		}
		StoreRank pStoreRank = storeRankService.find(storeRank.getId());
		if (pStoreRank == null) {
			return ERROR_VIEW;
		}
		if (!storeRankService.nameUnique(storeRank.getType(), pStoreRank.getName(), storeRank.getName())) {
			return ERROR_VIEW;
		}
		if (pStoreRank.getIsDefault()) {
			storeRank.setIsDefault(true);
		}
		storeRankService.update(storeRank, "type", "stores");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", storeRankService.findPage(pageable));
		return "/admin/store_rank/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				StoreRank storeRank = storeRankService.find(id);
				if (storeRank != null && storeRank.getStores() != null && !storeRank.getStores().isEmpty()) {
					return Message.error("admin.storeRank.deleteExistNotAllowed", storeRank.getName());
				}
			}
			long totalCount = storeRankService.count();
			if (ids.length >= totalCount) {
				return Message.error("admin.common.deleteAllNotAllowed");
			}
			storeRankService.delete(ids);
		}
		storeRankService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
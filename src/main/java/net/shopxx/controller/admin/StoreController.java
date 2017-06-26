/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Business;
import net.shopxx.entity.BusinessAttribute;
import net.shopxx.entity.Member;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Store;
import net.shopxx.service.BusinessAttributeService;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.StoreCategoryService;
import net.shopxx.service.StoreRankService;
import net.shopxx.service.StoreService;

/**
 * Controller - 店铺管理
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminStoreController")
@RequestMapping("/admin/store")
public class StoreController extends BaseController {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "businessAttributeServiceImpl")
	private BusinessAttributeService businessAttributeService;
	@Resource(name = "storeRankServiceImpl")
	private StoreRankService storeRankService;
	@Resource(name = "storeCategoryServiceImpl")
	private StoreCategoryService storeCategoryService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;

	/**
	 * 会员选择
	 */
	@RequestMapping(value = "/member_select", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> memberSelect(@RequestParam("q") String keyword, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		List<Member> members = memberService.search(keyword, count);
		for (Member member : members) {
			if (member.getBusiness() == null) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", member.getId());
				item.put("sn", member.getSn());
				item.put("username", member.getUsername());
				data.add(item);
			}
		}
		return data;
	}

	/**
	 * 检查店铺名是否已存在
	 */
	@RequestMapping(value = "/check_name", method = RequestMethod.GET)
	public @ResponseBody boolean checkName(String name, String pName) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		if (!StringUtils.isEmpty(pName) && name.equals(pName)) {
			return true;
		}
		return !storeService.nameExists(name);
	}

	/**
	 * 检查店铺Mobile是否存在
	 */
	@RequestMapping(value = "/check_mobile", method = RequestMethod.GET)
	public @ResponseBody boolean checkMobile(String mobile, String pMobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		if (!StringUtils.isEmpty(pMobile) && mobile.equals(pMobile)) {
			return true;
		}
		return !storeService.mobileExists(mobile);
	}

	/**
	 * 检查店铺E-mail是否存在
	 */
	@RequestMapping(value = "/check_email", method = RequestMethod.GET)
	public @ResponseBody boolean checkEmail(String email, String pEmail) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		if (!StringUtils.isEmpty(pEmail) && email.equals(pEmail)) {
			return true;
		}
		return !storeService.emailExists(email);
	}

	/**
	 * 查看
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String view(Long id, ModelMap model) {
		Store store = storeService.find(id);
		Business business = store != null ? store.getBusiness() : null;
		if (business == null) {
			return ERROR_VIEW;
		}
		model.addAttribute("store", store);
		model.addAttribute("business", business);
		model.addAttribute("businessAttributes", businessAttributeService.findList(true, true));
		return "/admin/store/view";
	}

	/**
	 * 审核
	 */
	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public String review(Long id, ModelMap model) {
		Store store = storeService.find(id);
		Business business = store != null ? store.getBusiness() : null;
		if (business == null) {
			return ERROR_VIEW;
		}
		model.addAttribute("store", store);
		model.addAttribute("business", business);
		model.addAttribute("businessAttributes", businessAttributeService.findList(true, true));
		return "/admin/store/review";
	}

	/**
	 * 审核
	 */
	@RequestMapping(value = "/reviewed", method = RequestMethod.POST)
	public String reviewed(Long id, Boolean review, String content, RedirectAttributes redirectAttributes) {
		Store store = storeService.find(id);
		if (store == null || review == null || (review == false && StringUtils.isEmpty(content))) {
			return ERROR_VIEW;
		}
		storeService.review(store, review, content);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		model.addAttribute("types", Store.Type.values());
		model.addAttribute("storeRanks", storeRankService.findAll());
		model.addAttribute("storeCategorys", storeCategoryService.findAll());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("businessAttributes", businessAttributeService.findList(true, true));
		return "/admin/store/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Store store, Long memberId, Long storeRankId, Long storeCategoryId, Long[] productCategoryIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Business business = new Business();
		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				return ERROR_VIEW;
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		business.setMember(memberService.find(memberId));
		if (!isValid(business, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		store.setIsEnabled(true);
		store.setStoreRank(storeRankService.find(storeRankId));
		store.setStoreCategory(storeCategoryService.find(storeCategoryId));
		store.setProductCategorys(new HashSet<ProductCategory>(productCategoryService.findList(productCategoryIds)));
		if (!isValid(store, getValidationGroup(store.getType()), BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		storeService.register(store, business, Store.Type.self.equals(store.getType()) == true ? false : true);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		Store store = storeService.find(id);
		Business business = store != null ? store.getBusiness() : null;
		if (business == null) {
			return ERROR_VIEW;
		}
		model.addAttribute("store", store);
		model.addAttribute("business", business);
		model.addAttribute("businessAttributes", businessAttributeService.findList(true, true));
		model.addAttribute("storeRanks", storeRankService.findAll());
		model.addAttribute("storeCategorys", storeCategoryService.findAll());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		return "/admin/store/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Store store, Long storeRankId, Long storeCategoryId, Long[] productCategoryIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Store pStore = storeService.find(store.getId());
		Business business = pStore != null ? pStore.getBusiness() : null;
		if (business == null) {
			return ERROR_VIEW;
		}

		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				return ERROR_VIEW;
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		if (!isValid(business, BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		store.setType(pStore.getType());
		store.setLogo(pStore.getLogo());
		store.setAddress(pStore.getAddress());
		store.setZipCode(pStore.getZipCode());
		store.setPhone(pStore.getPhone());
		store.setIntroduction(pStore.getIntroduction());
		store.setKeyword(pStore.getKeyword());
		store.setSeoTitle(pStore.getSeoTitle());
		store.setSeoKeywords(pStore.getSeoKeywords());
		store.setSeoDescription(pStore.getSeoDescription());
		store.setStoreRank(storeRankService.find(storeRankId));
		store.setStoreCategory(storeCategoryService.find(storeCategoryId));
		store.setProductCategorys(new HashSet<ProductCategory>(productCategoryService.findList(productCategoryIds)));
		if (!isValid(store, getValidationGroup(store.getType()), BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		storeService.update(store, business);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Store.Type type, Store.Status status, Boolean isEnabled, Pageable pageable, ModelMap model) {
		model.addAttribute("type", type);
		model.addAttribute("status", status);
		model.addAttribute("isEnabled", isEnabled);
		model.addAttribute("page", storeService.findPage(type, status, isEnabled, pageable));
		return "/admin/store/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Store store = storeService.find(id);
				if (store != null && !orderService.canDeleteOrder(store) || store != null && store.getBusiness().getMember().getFrozenFunds().compareTo(store.getBail()) > 0) {
					return Message.error("admin.store.deleteUnfinishedNotAllowed", store.getName());
				}
			}
			storeService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

	/**
	 * 根据类型获取验证组
	 * 
	 * @param type
	 *            类型
	 * @return 验证组
	 */
	private Class<?> getValidationGroup(Store.Type type) {
		Assert.notNull(type);

		switch (type) {
		case general:
			return Store.General.class;
		case self:
			return Store.Self.class;
		}
		return null;
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.BusinessAttribute;
import net.shopxx.service.BusinessAttributeService;

/**
 * Controller - 商家注册项
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("businessAttributeController")
@RequestMapping("/admin/business_attribute")
public class BusinessAttributeController extends BaseController {

	@Resource(name = "businessAttributeServiceImpl")
	private BusinessAttributeService businessAttributeService;

	/**
	 * 检查配比语法是否正确
	 */
	@RequestMapping(value = "/check_pattern", method = RequestMethod.GET)
	public @ResponseBody boolean checkPattern(String pattern) {
		if (StringUtils.isEmpty(pattern)) {
			return false;
		}
		try {
			Pattern.compile(pattern);
		} catch (PatternSyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		return "/admin/business_attribute/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(BusinessAttribute businessAttribute, RedirectAttributes redirectAttributes) {
		if (!isValid(businessAttribute)) {
			return ERROR_VIEW;
		}
		if (BusinessAttribute.Type.select.equals(businessAttribute.getType()) || BusinessAttribute.Type.checkbox.equals(businessAttribute.getType())) {
			List<String> options = businessAttribute.getOptions();
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				return ERROR_VIEW;
			}
			businessAttribute.setPattern(null);
		} else if (BusinessAttribute.Type.text.equals(businessAttribute.getType()) || BusinessAttribute.Type.image.equals(businessAttribute.getType()) || BusinessAttribute.Type.date.equals(businessAttribute.getType())) {
			businessAttribute.setOptions(null);
		} else {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(businessAttribute.getPattern())) {
			try {
				Pattern.compile(businessAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				return ERROR_VIEW;
			}
		}

		Integer propertyIndex = businessAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			return ERROR_VIEW;
		}
		businessAttribute.setPropertyIndex(null);
		businessAttributeService.save(businessAttribute);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("businessAttribute", businessAttributeService.find(id));
		return "/admin/business_attribute/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BusinessAttribute businessAttribute, RedirectAttributes redirectAttributes) {
		if (!isValid(businessAttribute)) {
			return ERROR_VIEW;
		}
		BusinessAttribute pBusinessAttribute = businessAttributeService.find(businessAttribute.getId());
		if (pBusinessAttribute == null) {
			return ERROR_VIEW;
		}
		if (BusinessAttribute.Type.select.equals(pBusinessAttribute.getType()) || BusinessAttribute.Type.checkbox.equals(pBusinessAttribute.getType())) {
			List<String> options = businessAttribute.getOptions();
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				return ERROR_VIEW;
			}
			businessAttribute.setPattern(null);
		} else {
			businessAttribute.setOptions(null);
		}
		if (StringUtils.isNotEmpty(businessAttribute.getPattern())) {
			try {
				Pattern.compile(businessAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				return ERROR_VIEW;
			}
		}
		businessAttributeService.update(businessAttribute);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", businessAttributeService.findPage(pageable));
		return "/admin/business_attribute/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		businessAttributeService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
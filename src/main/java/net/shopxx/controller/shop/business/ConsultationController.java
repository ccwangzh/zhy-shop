/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Consultation;
import net.shopxx.entity.Store;
import net.shopxx.service.ConsultationService;
import net.shopxx.service.StoreService;

/**
 * Controller - 咨询
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessConsultationController")
@RequestMapping("/business/consultation")
public class ConsultationController extends BaseController {

	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 回复
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.GET)
	public String reply(Long id, ModelMap model) {
		model.addAttribute("consultation", consultationService.find(id));
		return "/shop/${theme}/business/consultation/reply";
	}

	/**
	 * 回复
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.POST)
	public String reply(Long id, String content, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!isValid(Consultation.class, "content", content)) {
			return ERROR_VIEW;
		}
		Consultation consultation = consultationService.find(id);
		if (consultation == null || !consultation.getStore().equals(storeService.getCurrent())) {
			return ERROR_VIEW;
		}

		Consultation replyConsultation = new Consultation();
		replyConsultation.setContent(content);
		replyConsultation.setIp(request.getRemoteAddr());
		consultationService.reply(consultation, replyConsultation);

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:reply.jhtml?id=" + id;
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("consultation", consultationService.find(id));
		return "/shop/${theme}/business/consultation/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Long id, @RequestParam(defaultValue = "false") Boolean isShow, RedirectAttributes redirectAttributes) {
		Consultation consultation = consultationService.find(id);
		if (consultation == null || !consultation.getStore().equals(storeService.getCurrent())) {
			return ERROR_VIEW;
		}
		if (isShow != consultation.getIsShow()) {
			consultation.setIsShow(isShow);
			consultationService.update(consultation);
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		Store store = storeService.getCurrent();
		model.addAttribute("page", consultationService.findPage(null, null, store, null, pageable));
		return "/shop/${theme}/business/consultation/list";
	}

	/**
	 * 删除回复
	 */
	@RequestMapping(value = "/delete_reply", method = RequestMethod.POST)
	public @ResponseBody Message deleteReply(Long id) {
		Consultation consultation = consultationService.find(id);
		if (consultation == null || consultation.getForConsultation() == null || !consultation.getStore().equals(storeService.getCurrent())) {
			return ERROR_MESSAGE;
		}
		consultationService.delete(consultation);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Consultation consultation = consultationService.find(id);
				if (consultation == null || !consultation.getStore().equals(storeService.getCurrent())) {
					return ERROR_MESSAGE;
				}
			}
			consultationService.delete(ids);
			return SUCCESS_MESSAGE;
		}
		return ERROR_MESSAGE;
	}

}
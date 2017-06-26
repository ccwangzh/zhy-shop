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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Review;
import net.shopxx.entity.Store;
import net.shopxx.service.ReviewService;
import net.shopxx.service.StoreService;

/**
 * Controller - 评论
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessReviewController")
@RequestMapping("/business/review")
public class ReviewController extends BaseController {

	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 回复
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.GET)
	public String reply(Long id, ModelMap model) {
		model.addAttribute("review", reviewService.find(id));
		return "/shop/${theme}/business/review/reply";
	}

	/**
	 * 回复
	 */
	@RequestMapping(value = "/reply", method = RequestMethod.POST)
	public String reply(Long id, String content, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!isValid(Review.class, "content", content)) {
			return ERROR_VIEW;
		}
		Review review = reviewService.find(id);
		if (review == null || !review.getStore().equals(storeService.getCurrent())) {
			return ERROR_VIEW;
		}
		Review replyReview = new Review();
		replyReview.setContent(content);
		replyReview.setIp(request.getRemoteAddr());
		review.setIsShow(true);
		reviewService.reply(review, replyReview);
		reviewService.update(review);

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:reply.jhtml?id=" + id;
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		model.addAttribute("review", reviewService.find(id));
		return "/shop/${theme}/business/review/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Long id, RedirectAttributes redirectAttributes) {
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Review.Type type, Pageable pageable, ModelMap model) {
		Store store = storeService.getCurrent();
		model.addAttribute("type", type);
		model.addAttribute("types", Review.Type.values());
		model.addAttribute("page", reviewService.findPage(null, null, store, type, null, pageable));
		return "/shop/${theme}/business/review/list";
	}

	/**
	 * 删除回复
	 */
	@RequestMapping(value = "/delete_reply", method = RequestMethod.POST)
	public @ResponseBody Message deleteReply(Long id) {
		Review review = reviewService.find(id);
		if (review == null || review.getForReview() == null || !review.getStore().equals(storeService.getCurrent())) {
			return ERROR_MESSAGE;
		}
		reviewService.delete(review);
		return SUCCESS_MESSAGE;
	}

}
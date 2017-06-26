/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.member;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Member;
import net.shopxx.entity.Store;
import net.shopxx.service.MemberService;
import net.shopxx.service.StoreService;

/**
 * Controller - 会员中心 - 店铺收藏
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopMemberStoreFavoriteController")
@RequestMapping("/member/store_favorite")
public class StoreFavoriteController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody Message add(Long storeId) {
		Store store = storeService.find(storeId);
		if (store == null) {
			return ERROR_MESSAGE;
		}
		Member member = memberService.getCurrent();
		if (member.getFavoriteStore().contains(store)) {
			return Message.warn("shop.member.storeFavorite.exist");
		}
		if (store.getBusiness().getMember().equals(member)) {
			return Message.warn("shop.member.storeFavorite.notStore");
		}
		if (Member.MAX_STORE_FAVORITE_COUNT != null && member.getFavoriteStore().size() >= Member.MAX_STORE_FAVORITE_COUNT) {
			return Message.warn("shop.member.storeFavorite.addCountNotAllowed", Member.MAX_STORE_FAVORITE_COUNT);
		}
		member.getFavoriteStore().add(store);
		memberService.update(member);
		return Message.success("shop.member.storeFavorite.success");
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", storeService.findPage(member, pageable));
		return "/shop/${theme}/member/store_favorite/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long id) {
		Store store = storeService.find(id);
		if (store == null) {
			return ERROR_MESSAGE;
		}

		Member member = memberService.getCurrent();
		if (!member.getFavoriteStore().contains(store)) {
			return ERROR_MESSAGE;
		}
		member.getFavoriteStore().remove(store);
		memberService.update(member);
		return SUCCESS_MESSAGE;
	}

}
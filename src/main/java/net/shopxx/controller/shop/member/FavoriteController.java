/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Member;
import net.shopxx.service.GoodsService;
import net.shopxx.service.MemberService;

/**
 * Controller - 会员中心 - 商品收藏
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopMemberFavoriteController")
@RequestMapping("/member/favorite")
public class FavoriteController extends BaseController {

	/** 每页记录数 */
	private static final int PAGE_SIZE = 10;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> add(Long goodsId) {
		Map<String, Object> data = new HashMap<String, Object>();
		Boolean isAdd = true;
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}

		Member member = memberService.getCurrent();
		if (member == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (member.getFavoriteGoods().contains(goods)) {//再次点击收藏时取消收藏
			member.getFavoriteGoods().remove(goods);
			data.put("message", Message.success("shop.member.favorite.cancel"));
			isAdd = false;
		}else{
			member.getFavoriteGoods().add(goods);
			data.put("message", Message.success("shop.member.favorite.success"));
		}
		if(isAdd){
			if (member.getBusiness() != null && member.getBusiness().getStore() != null && member.getBusiness().getStore().equals(goods.getStore())) {
				data.put("message",Message.warn("shop.member.favorite.addGoodsNotAllowed"));
				return data;
			}
			
			if (Member.MAX_FAVORITE_COUNT != null && member.getFavoriteGoods().size() >= Member.MAX_FAVORITE_COUNT) {
				data.put("message", Message.warn("shop.member.favorite.addCountNotAllowed", Member.MAX_FAVORITE_COUNT));
				return data;
			}
		}
		memberService.update(member);
		data.put("isAdd",isAdd);
		return data;
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", goodsService.findPage(member, pageable));
		return "/shop/${theme}/member/favorite/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long id) {
		Goods goods = goodsService.find(id);
		if (goods == null) {
			return ERROR_MESSAGE;
		}

		Member member = memberService.getCurrent();
		if (!member.getFavoriteGoods().contains(goods)) {
			return ERROR_MESSAGE;
		}
		member.getFavoriteGoods().remove(goods);
		memberService.update(member);
		return SUCCESS_MESSAGE;
	}
	
	/**
	 * 商品详情页获取收藏的状态
	 */
	@RequestMapping(value = "/getStatus", method = RequestMethod.GET)
	public @ResponseBody String getStatus(Long goodsId) {
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			return ERROR;
		}
		Member member = memberService.getCurrent();
		if (member == null) {
			return ERROR;
		}
		if (member.getBusiness() != null && member.getBusiness().getStore() != null && member.getBusiness().getStore().equals(goods.getStore())) {
			return message("shop.member.favorite.addGoodsNotAllowed");
		}
		if (member.getFavoriteGoods().contains(goods)) {
			return "true";
		}
		return "false";
	}
	
	/**
	 * 查询用户收藏的所用商品
	 */
	@RequestMapping(value = "/myfavorites", method = RequestMethod.GET)
	public @ResponseBody List<Long> getFavorite() {
		List<Long> goodsIds = new ArrayList<Long>();
		Set<Goods> set = memberService.getCurrent().getFavoriteGoods();
		for(Goods goods : set){
			goodsIds.add(goods.getId());
		}
		return goodsIds;
	}

}
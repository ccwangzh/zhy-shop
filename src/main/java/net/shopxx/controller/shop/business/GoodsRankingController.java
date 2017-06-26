/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Goods;
import net.shopxx.service.GoodsService;
import net.shopxx.service.StoreService;

/**
 * Controller - 商家中心 - 商品排名
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopGoodsRankingController")
@RequestMapping("/business/goods_ranking")
public class GoodsRankingController extends BaseController {

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Goods.RankingType rankingType, Pageable pageable, Model model) {
		if (rankingType == null) {
			rankingType = Goods.RankingType.sales;
		}
		model.addAttribute("rankingTypes", Goods.RankingType.values());
		model.addAttribute("rankingType", rankingType);
		model.addAttribute("page", goodsService.findPage(rankingType, storeService.getCurrent(), pageable));
		return "/shop/${theme}/business/goods_ranking/list";
	}

}
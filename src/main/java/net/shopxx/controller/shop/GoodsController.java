/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Pageable;
import net.shopxx.entity.Attribute;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Goods;
import net.shopxx.entity.InstantMessage;
import net.shopxx.entity.Member;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Store;
import net.shopxx.entity.Tag;
import net.shopxx.exception.ResourceNotFoundException;
import net.shopxx.service.AttributeService;
import net.shopxx.service.BrandService;
import net.shopxx.service.GoodsService;
import net.shopxx.service.MemberService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.SearchService;
import net.shopxx.service.StoreService;
import net.shopxx.service.TagService;

/**
 * Controller - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopGoodsController")
@RequestMapping("/goods")
public class GoodsController extends BaseController {

	/** 最大对比货品数 */
	public static final Integer MAX_COMPARE_GOODS_COUNT = 4;

	/** 最大浏览记录货品数 */
	public static final Integer MAX_HISTORY_GOODS_COUNT = 10;

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "brandServiceImpl")
	private BrandService brandService;
	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "attributeServiceImpl")
	private AttributeService attributeService;
	@Resource(name = "searchServiceImpl")
	private SearchService searchService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	/**
	 * 对比栏
	 */
	@RequestMapping(value = "/compare_bar", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> compareBar(Long[] goodsIds) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_COMPARE_GOODS_COUNT) {
			return data;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		for (Goods goods : goodsList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", goods.getId());
			item.put("name", goods.getName());
			item.put("price", goods.getPrice());
			item.put("marketPrice", goods.getMarketPrice());
			item.put("thumbnail", goods.getThumbnail());
			item.put("url", goods.getUrl());
			data.add(item);
		}
		return data;
	}

	/**
	 * 添加对比
	 */
	@RequestMapping(value = "/add_compare", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> addCompare(Long goodsId) {
		Map<String, Object> data = new HashMap<String, Object>();
		Goods goods = goodsService.find(goodsId);
		if (goods == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}

		data.put("message", SUCCESS_MESSAGE);
		data.put("id", goods.getId());
		data.put("name", goods.getName());
		data.put("price", goods.getPrice());
		data.put("marketPrice", goods.getMarketPrice());
		data.put("thumbnail", goods.getThumbnail());
		data.put("url", goods.getUrl());
		return data;
	}

	/**
	 * 浏览记录
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> history(Long[] goodsIds) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_HISTORY_GOODS_COUNT) {
			return data;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		for (Goods goods : goodsList) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", goods.getName());
			item.put("price", goods.getPrice());
			item.put("thumbnail", goods.getThumbnail());
			item.put("url", goods.getUrl());
			data.add(item);
		}
		return data;
	}

	/**
	 * 获取即时通讯
	 */
	@RequestMapping(value = "/get_instant_message", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> getInstantMessage(Long storeId) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (storeId == null) {
			return data;
		}
		Store store = storeService.find(storeId);
		Set<InstantMessage> instantMessages = store != null ? store.getInstantMessages() : null;
		if (store == null || instantMessages.size() < 1) {
			return data;
		}
		for (InstantMessage instantMessage : instantMessages) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", instantMessage.getName());
			item.put("account", instantMessage.getAccount());
			item.put("type", instantMessage.getType().toString());
			data.add(item);
		}
		return data;
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list{listIndex}/{productCategoryId}", method = RequestMethod.GET)
	public String list(@PathVariable String listIndex,@PathVariable Long productCategoryId, Goods.Type type, Long brandId, Long promotionId, Long tagId, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Integer pageNumber, Integer pageSize, HttpServletRequest request, ModelMap model) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}

		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Tag tag = tagService.find(tagId);
		Map<Attribute, String> attributeValueMap = new HashMap<Attribute, String>();
		Set<Attribute> attributes = productCategory.getAttributes();
		if (CollectionUtils.isNotEmpty(attributes)) {
			for (Attribute attribute : attributes) {
				String value = request.getParameter("attribute_" + attribute.getId());
				String attributeValue = attributeService.toAttributeValue(attribute, value);
				if (attributeValue != null) {
					attributeValueMap.put(attribute, attributeValue);
				}
			}
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", Goods.OrderType.values());
		model.addAttribute("productCategory", productCategory);
		model.addAttribute("type", type);
		model.addAttribute("brand", brand);
		model.addAttribute("promotion", promotion);
		model.addAttribute("tag", tag);
		model.addAttribute("attributeValueMap", attributeValueMap);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("page", goodsService.findPage(null, type, productCategory, brand, promotion, tag, attributeValueMap, startPrice, endPrice, true, Goods.Status.onTheshelf, true, null, null, null, null, orderType, pageable));
		return "/shop/${theme}/goods/list" + (listIndex==null ? "" : listIndex);
	}


	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Goods.Type type, Long brandId, Long promotionId, Long tagId, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Integer pageNumber, Integer pageSize, ModelMap model) {
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Tag tag = tagService.find(tagId);

		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", Goods.OrderType.values());
		model.addAttribute("type", type);
		model.addAttribute("brand", brand);
		model.addAttribute("promotion", promotion);
		model.addAttribute("tag", tag);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("page", goodsService.findPage(null, type, null, brand, promotion, tag, null, startPrice, endPrice, true, Goods.Status.onTheshelf, true, null, null, null, null, orderType, pageable));
		return "/shop/${theme}/goods/list";
	}

	/**
	 * 搜索
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(String keyword, BigDecimal startPrice, BigDecimal endPrice, Goods.OrderType orderType, Integer pageNumber, Integer pageSize, ModelMap model) {
		if (StringUtils.isEmpty(keyword)) {
			return ERROR_VIEW;
		}
		Pageable pageable = new Pageable(pageNumber, pageSize);
		model.addAttribute("orderTypes", Goods.OrderType.values());
		model.addAttribute("goodsKeyword", keyword);
		model.addAttribute("startPrice", startPrice);
		model.addAttribute("endPrice", endPrice);
		model.addAttribute("orderType", orderType);
		model.addAttribute("page", searchService.search(keyword, startPrice, endPrice, orderType, pageable));
		model.addAttribute("stores", searchService.searchStore(keyword));
		return "/shop/${theme}/goods/search";
	}

	/**
	 * 对比
	 */
	@RequestMapping(value = "/compare", method = RequestMethod.GET)
	public String compare(Long[] goodsIds, ModelMap model) {
		if (ArrayUtils.isEmpty(goodsIds) || goodsIds.length > MAX_COMPARE_GOODS_COUNT) {
			return ERROR_VIEW;
		}

		List<Goods> goodsList = goodsService.findList(goodsIds);
		if (CollectionUtils.isEmpty(goodsList)) {
			return ERROR_VIEW;
		}

		model.addAttribute("goodsList", goodsList);
		return "/shop/${theme}/goods/compare";
	}

	/**
	 * 点击数
	 */
	@RequestMapping(value = "/hits/{goodsId}", method = RequestMethod.GET)
	public @ResponseBody Long hits(@PathVariable Long goodsId) {
		if (goodsId == null) {
			return 0L;
		}

		return goodsService.viewHits(goodsId);
	}
}
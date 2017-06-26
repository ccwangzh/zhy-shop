/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
import net.shopxx.entity.Coupon;
import net.shopxx.entity.Goods;
import net.shopxx.entity.MemberRank;
import net.shopxx.entity.Product;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.PromotionPluginSvc;
import net.shopxx.entity.Store;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.plugin.PromotionPlugin;
import net.shopxx.service.CouponService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.PluginService;
import net.shopxx.service.ProductService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.StoreService;
import net.shopxx.service.SvcService;

/**
 * Controller - 促销折扣
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessDiscountController")
@RequestMapping("/business/discount")
public class DiscountController extends BaseController {

	/** 促销折扣ID */
	private static final String DISCOUNT = "discount";

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "couponServiceImpl")
	private CouponService couponService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Resource(name = "svcServiceImpl")
	private SvcService svcService;

	/**
	 * 检查支付状态
	 */
	@RequestMapping(value = "/check_pay_status", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> checkStoreStatus() {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		data.put("discountTime", store.getDiscount() != null ? store.getDiscount() : 0);
		return data;
	}

	/**
	 * 计算
	 */
	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> calculate(String paymentPluginId, Integer year, Boolean useBalance) {
		Map<String, Object> data = new HashMap<String, Object>();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		if (promotionPlugin == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal pluginPrice = promotionPlugin.getPrice();
		BigDecimal amount = BigDecimal.ZERO;
		if (year != null && pluginPrice != null) {
			amount = amount.add(pluginPrice.multiply(new BigDecimal(year)));
		}
		PaymentPlugin paymentPlugin = null;
		if (paymentPluginId != null) {
			paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
			if (paymentPlugin == null || !paymentPlugin.getIsEnabled() || amount.compareTo(BigDecimal.ZERO) < 0) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			if (amount.compareTo(BigDecimal.ZERO) == 0) {
				data.put("message", Message.warn("shop.payment.selectBalancePayment"));
				return data;
			}
			data.put("fee", paymentPlugin.calculateFee(amount));
			data.put("amount", paymentPlugin.calculateAmount(amount));
		} else {
			data.put("amount", amount);
			if (useBalance == null || !useBalance) {
				data.put("message", Message.warn("shop.payment.paymentMethodRequired"));
				return data;
			}
			data.put("fee", BigDecimal.ZERO);
		}
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	/**
	 * 生成服务
	 */
	@RequestMapping(value = "/generate_svc", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> generateSvc(Integer year) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (year == null || year < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Store store = storeService.getCurrent();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		if (promotionPlugin == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal amount = promotionPlugin.getPrice();
		if (BigDecimal.ZERO.compareTo(amount) >= 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}

		PromotionPluginSvc promotionPluginSvc = new PromotionPluginSvc();
		promotionPluginSvc.setAmount(amount);
		promotionPluginSvc.setDurationDays(30 * year);
		promotionPluginSvc.setStore(store);
		promotionPluginSvc.setPromotionPluginId(DISCOUNT);
		svcService.save(promotionPluginSvc);
		data.put("promotionPluginSvcSn", promotionPluginSvc.getSn());
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	/**
	 * 购买
	 */
	@RequestMapping(value = "/buy", method = RequestMethod.GET)
	public String buy(ModelMap model) {
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		if (promotionPlugin == null) {
			return ERROR_VIEW;
		}
		Store store = storeService.getCurrent();
		List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
		if (!paymentPlugins.isEmpty()) {
			model.addAttribute("defaultPaymentPlugin", paymentPlugins.get(0));
			model.addAttribute("paymentPlugins", paymentPlugins);
		}
		BigDecimal pluginPrice = promotionPlugin.getPrice();
		model.addAttribute("pluginPrice", pluginPrice);
		model.addAttribute("member", store.getBusiness().getMember());
		model.addAttribute("store", store);
		return "/shop/${theme}/business/discount/buy";
	}

	/**
	 * 预存款支付
	 */
	@RequestMapping(value = "/deposit_payment", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> depositPayment(Integer year) {
		Map<String, Object> data = new HashMap<String, Object>();
		Store store = storeService.getCurrent();
		if (year == null || year < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		if (promotionPlugin == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		BigDecimal pluginPrice = promotionPlugin.getPrice();
		if (promotionPlugin.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		storeService.buyPromotionPlugin(store, DISCOUNT, year, pluginPrice);
		data.put("message", SUCCESS_MESSAGE);
		return data;
	}

	/**
	 * 赠品选择
	 */
	@RequestMapping(value = "/gift_select", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> giftSelect(@RequestParam("q") String keyword, Long[] excludeIds, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		Store store = storeService.getCurrent();
		Set<Product> excludes = new HashSet<Product>(productService.findList(excludeIds));
		List<Product> products = productService.search(store, Goods.Type.gift, keyword, excludes, count);
		for (Product product : products) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", product.getId());
			item.put("sn", product.getSn());
			item.put("name", product.getName());
			item.put("specifications", product.getSpecifications());
			item.put("url", product.getUrl());
			data.add(item);
		}
		return data;
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		Store store = storeService.getCurrent();
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("coupons", couponService.findList(store));
		return "/shop/${theme}/business/discount/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Promotion promotion, Long[] memberRankIds, Long[] couponIds, Long[] giftIds, RedirectAttributes redirectAttributes) {
		promotion.setMemberRanks(new HashSet<MemberRank>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new HashSet<Coupon>(couponService.findList(couponIds)));
		promotion.setType(Promotion.Type.discount);
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Product> gifts = productService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Product gift = (Product) object;
					return gift != null && Goods.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new HashSet<Product>(gifts));
		} else {
			promotion.setGifts(null);
		}
		promotion.setStore(storeService.getCurrent());
		if (!isValid(promotion)) {
			return ERROR_VIEW;
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		String priceExpression = promotionPlugin.getPriceExpression(promotion, null, null);
		String pointExpression = promotionPlugin.getPointExpression(promotion);
		if (StringUtils.isNotEmpty(priceExpression) && !promotionService.isValidPriceExpression(priceExpression)) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(pointExpression) && !promotionService.isValidPointExpression(pointExpression)) {
			return ERROR_VIEW;
		}
		promotion.setPriceExpression(priceExpression);
		promotion.setPointExpression(pointExpression);
		promotion.setGoods(null);
		promotion.setProductCategories(null);
		promotion.setStore(storeService.getCurrent());
		promotionService.save(promotion);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(Long id, ModelMap model) {
		Store store = storeService.getCurrent();
		Promotion promotion = promotionService.find(id);
		if (!promotion.getStore().equals(store)) {
			return ERROR_VIEW;
		}
		model.addAttribute("promotion", promotionService.find(id));
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("coupons", couponService.findList(store));
		return "/shop/${theme}/business/discount/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Promotion promotion, Long id, Long[] memberRankIds, Long[] couponIds, Long[] giftIds, RedirectAttributes redirectAttributes) {
		Store store = storeService.getCurrent();
		Promotion pPromotion = promotionService.find(id);
		if (pPromotion == null || !store.equals(pPromotion.getStore())) {
			return ERROR_VIEW;
		}
		promotion.setMemberRanks(new HashSet<MemberRank>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new HashSet<Coupon>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Product> gifts = productService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Product gift = (Product) object;
					return gift != null && Goods.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new HashSet<Product>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}

		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		String priceExpression = promotionPlugin.getPriceExpression(promotion, null, null);
		String pointExpression = promotionPlugin.getPointExpression(promotion);
		if (StringUtils.isNotEmpty(priceExpression) && !promotionService.isValidPriceExpression(priceExpression)) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(pointExpression) && !promotionService.isValidPointExpression(pointExpression)) {
			return ERROR_VIEW;
		}
		promotion.setPriceExpression(priceExpression);
		promotion.setPointExpression(pointExpression);

		promotionService.update(promotion, "type", "store", "goods", "productCategories");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Pageable pageable, ModelMap model) {
		Store store = storeService.getCurrent();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
		model.addAttribute("store", store);
		model.addAttribute("isEnabled", promotionPlugin.getIsEnabled());
		model.addAttribute("page", promotionService.findPage(store, Promotion.Type.discount, pageable));
		return "/shop/${theme}/business/discount/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		for (Long id : ids) {
			Promotion promotion = promotionService.find(id);
			if (promotion == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!store.equals(promotion.getStore())) {
				return ERROR_MESSAGE;
			}
		}
		promotionService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}
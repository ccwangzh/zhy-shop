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

import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.service.ConsultationService;
import net.shopxx.service.CouponCodeService;
import net.shopxx.service.GoodsService;
import net.shopxx.service.MemberService;
import net.shopxx.service.MessageService;
import net.shopxx.service.OrderService;
import net.shopxx.service.ProductNotifyService;
import net.shopxx.service.ReviewService;
import net.shopxx.service.StoreService;

/**
 * Controller - 会员中心
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopMemberController")
@RequestMapping(value={"/member", "/h5/member"})
public class MemberController extends BaseController {

	/** 最新订单数 */
	private static final int NEW_ORDER_COUNT = 3;

	/** 每页记录数 */
	private static final int PAGE_SIZE = 3;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;
	@Resource(name = "messageServiceImpl")
	private MessageService messageService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;
	@Resource(name = "reviewServiceImpl")
	private ReviewService reviewService;
	@Resource(name = "consultationServiceImpl")
	private ConsultationService consultationService;

	/**
	 * 首页
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Integer pageNumber, ModelMap model) {
		Member member = memberService.getCurrent();
		//购物车
		model.addAttribute("cart", member.getCart());
		//待付款
		model.addAttribute("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, member, null, null, null, null, null, null, null));
		//待收货
		model.addAttribute("shippedOrderCount", orderService.count(null, Order.Status.shipped, member, null, null, null, null, null, null, null));
		//已完成
		model.addAttribute("completedOrderCount", orderService.count(null, Order.Status.completed, member, null, null, null, null, null, null, null));
		//待发货
		model.addAttribute("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, member, null, null, null, null, null, null, null));
		
		model.addAttribute("messageCount", messageService.count(member, false));
		model.addAttribute("couponCodeCount", couponCodeService.count(null, member, null, false, false));
		model.addAttribute("favoriteCount", goodsService.count(null, null, member, null, null, null, null, null, null));
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("goodsPage", goodsService.findPage(member, pageable));
		model.addAttribute("storePage", storeService.findPage(member, pageable));
		model.addAttribute("newOrders", orderService.findList(null, null, null, member, null, null, null, null, null, null, null, NEW_ORDER_COUNT, null, null));
		return "/shop/${theme}/member/index";
	}

}
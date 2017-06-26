/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import net.shopxx.Message;
import net.shopxx.Setting;
import net.shopxx.entity.*;
import net.shopxx.exception.ApplicationException;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.service.*;
import net.shopxx.util.SystemUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controller - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopOrderController")
@RequestMapping("/order")
public class OrderController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "areaServiceImpl")
	private AreaService areaService;
	@Resource(name = "receiverServiceImpl")
	private ReceiverService receiverService;
	@Resource(name = "cartServiceImpl")
	private CartService cartService;
	@Resource(name = "paymentMethodServiceImpl")
	private PaymentMethodService paymentMethodService;
	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;
	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
	@Value("${exchangeUrl}")
	private String exchangeUrl;
	@Value("${exchangeId}")
	private String exchangeId;
	@Resource
	private RestTemplate restTemplate;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	
	Setting setting = SystemUtils.getSetting();
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	/**
	 * 检查积分兑换
	 */
	@RequestMapping(value = "/check_exchange", method = RequestMethod.GET)
	public @ResponseBody Message checkExchange(Long productId, Integer quantity) {
		if (quantity == null || quantity < 1) {
			return ERROR_MESSAGE;
		}
		Product product = productService.find(productId);
		if (product == null) {
			return ERROR_MESSAGE;
		}
		Store store = storeService.getCurrent();
		if (store != null && store.hasContained(product)) {
			return Message.warn("shop.order.productNotExchange");
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			return ERROR_MESSAGE;
		}
		if (!product.getIsMarketable()) {
			return Message.warn("shop.order.productNotMarketable");
		}
		if (quantity > product.getAvailableStock()) {
			return Message.warn("shop.order.productLowStock");
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			return Message.warn("shop.order.lowPoint");
		}
		return SUCCESS_MESSAGE;
	}
	
	/**
	 * 保存收货地址
	 */
	@RequestMapping(value = "/save_receiver", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveReceiver(Receiver receiver, Long areaId) {
		Map<String, Object> data = new HashMap<String, Object>();
		receiver.setArea(areaService.find(areaId));
		if (!isValid(receiver)) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		if (Receiver.MAX_RECEIVER_COUNT != null && member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			data.put("message", Message.error("shop.order.addReceiverCountNotAllowed", Receiver.MAX_RECEIVER_COUNT));
			return data;
		}
		receiver.setAreaName(null);
		receiver.setMember(member);
		receiverService.save(receiver);
		data.put("message", SUCCESS_MESSAGE);
		data.put("id", receiver.getId());
		data.put("consignee", receiver.getConsignee());
		data.put("areaName", receiver.getAreaName());
		data.put("address", receiver.getAddress());
		data.put("zipCode", receiver.getZipCode());
		data.put("phone", receiver.getPhone());
		return data;
	}

	/**
	 * 订单锁定
	 */
	@RequestMapping(value = "/lock", method = RequestMethod.POST)
	public @ResponseBody void lock(String[] orderSns) {
		Member member = memberService.getCurrent();
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order != null && member.equals(order.getMember()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0) {
				orderService.lock(order, member.getLockKey());
			}
		}
	}

	/**
	 * 检查等待付款
	 */
	@RequestMapping(value = "/check_pending_payment", method = RequestMethod.GET)
	public @ResponseBody boolean checkPendingPayment(String[] orderSns) {
		Member member = memberService.getCurrent();
		boolean flag = false;
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			flag = order != null && member.equals(order.getMember()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0;
		}
		return flag;
	}

	/**
	 * 检查优惠券
	 */

	@RequestMapping(value = "/check_coupon", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> checkCoupon(String code) {
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent();
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null) {
			Coupon coupon = couponCode.getCoupon();
			Store store = coupon.getStore();
			if (store != null && cart.getStores().contains(store) && !cart.isCouponAllowed(store)) {
				data.put("message", Message.warn("shop.order.couponNotAllowed"));
				return data;
			}
			if (couponCode.getIsUsed()) {
				data.put("message", Message.warn("shop.order.couponCodeUsed"));
				return data;
			}
			if (!coupon.getIsEnabled()) {
				data.put("message", Message.warn("shop.order.couponDisabled"));
				return data;
			}
			if (!coupon.hasBegun()) {
				data.put("message", Message.warn("shop.order.couponNotBegin"));
				return data;
			}
			if (coupon.hasExpired()) {
				data.put("message", Message.warn("shop.order.couponHasExpired"));
				return data;
			}
			if (!cart.isValid(coupon, store)) {
				data.put("message", Message.warn("shop.order.couponInvalid"));
				return data;
			}
			data.put("message", SUCCESS_MESSAGE);
			data.put("couponName", coupon.getName());
			return data;
		} else {
			data.put("message", Message.warn("shop.order.couponCodeNotExist"));
			return data;
		}
	}


	
	/**
	 * 结算
	 */
	@RequestMapping(value = "/checkout", method = RequestMethod.GET)
	public String checkout(ModelMap model) {
		Cart cart = cartService.getCurrent();
		if (cart == null || cart.isEmpty()) {
			return "redirect:/cart/list.jhtml";
		}
		Member member = memberService.getCurrent();
		Receiver defaultReceiver = receiverService.findDefault(member);
		List<Order> orders = orderService.generate(Order.Type.general, cart, defaultReceiver, null, null, null, null, null, null);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			rewardPoint = rewardPoint + order.getRewardPoint();
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		model.addAttribute("price", price);
		model.addAttribute("fee", fee);
		model.addAttribute("freight", freight);
		model.addAttribute("tax", tax);
		model.addAttribute("promotionDiscount", promotionDiscount);
		model.addAttribute("couponDiscount", couponDiscount);
		model.addAttribute("amount", amount);
		model.addAttribute("amountPayable", amountPayable);
		model.addAttribute("isDelivery", isDelivery);
		model.addAttribute("rewardPoint", rewardPoint);
		model.addAttribute("orderType", Order.Type.general);
		model.addAttribute("orders", orders);
		model.addAttribute("defaultReceiver", defaultReceiver);
		model.addAttribute("cartTag", cart.getTag());
		model.addAttribute("agreement", goodsService.getCommonAgreement());
		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<PaymentMethod>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (cart.isContainGeneral()) {
				if (paymentMethod.getMethod().equals(PaymentMethod.Method.online) 
						|| paymentMethod.getMethod().equals(PaymentMethod.Method.exChangeAccount)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				availablePaymentMethods.add(paymentMethod);
			}
		}
		model.addAttribute("member", member);
		model.addAttribute("paymentMethods", availablePaymentMethods);
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		return "/shop/${theme}/order/checkout";
	}
	
	/**
	 * 直接够买（针对于支持挂牌交易、不支持物流的商品）
	 */
	@RequestMapping(value = "/checkout",params = "type=directBuy", method = RequestMethod.GET)
	public String directCheckout(Long productId, Integer quantity, ModelMap model) {
		if (quantity == null || quantity < 1) {
			return ERROR_VIEW;
		}
		Product product = productService.find(productId);
		if (product == null) {
			return ERROR_VIEW;
		}
		//只能是普通商品或挂牌商品
		Order.Type type = Order.Type.general;
		if (Goods.Type.listedtrade.equals(product.getType())){
			type = Order.Type.listedtrade;
		}else if(!Goods.Type.general.equals(product.getType())){
			return ERROR_VIEW;
		}
		
		Store store = storeService.getCurrent();
		if (store != null && store.hasContained(product)) {
			model.addAttribute("errorMessage", message("shop.cart.addProductNotAllowed"));
			return ERROR_VIEW;
		}
		if (!product.getIsMarketable()) {
			model.addAttribute("errorMessage", message("shop.order.productNotMarketable"));
			return ERROR_VIEW;
		}
		if (quantity > product.getAvailableStock()) {
		    model.addAttribute("errorMessage", message("shop.order.productLowStock"));
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		Receiver defaultReceiver = receiverService.findDefault(member);
	
		
		List<Order> orders = orderService.generate(type, cart, defaultReceiver, null, null, null, null, null, null);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			rewardPoint = rewardPoint + order.getRewardPoint();
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		model.addAttribute("productId", productId);
		model.addAttribute("quantity", quantity);
		model.addAttribute("price", price);
		model.addAttribute("fee", fee);
		model.addAttribute("freight", freight);
		model.addAttribute("tax", tax);
		model.addAttribute("promotionDiscount", promotionDiscount);
		model.addAttribute("couponDiscount", couponDiscount);
		model.addAttribute("amount", amount);
		model.addAttribute("amountPayable", amountPayable);
		model.addAttribute("isDelivery", isDelivery);
		model.addAttribute("rewardPoint", rewardPoint);
		model.addAttribute("orderType", "directBuy");
		model.addAttribute("isListedtrade", type.equals(Order.Type.listedtrade));
		model.addAttribute("orders", orders);
		model.addAttribute("defaultReceiver", defaultReceiver);
		model.addAttribute("cartTag", cart.getTag());
		model.addAttribute("agreement", goodsService.getCommonAgreement());
		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<PaymentMethod>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (cart.isContainGeneral()) {
				if (paymentMethod.getMethod().equals(PaymentMethod.Method.online) 
						|| paymentMethod.getMethod().equals(PaymentMethod.Method.exChangeAccount)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				availablePaymentMethods.add(paymentMethod);
			}
		}
		model.addAttribute("member", member);
		model.addAttribute("paymentMethods", availablePaymentMethods);
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		return "/shop/${theme}/order/checkout";
	}


	/**
	 * 积分结算
	 */
	@RequestMapping(value = "/checkout", params = "type=exchange", method = RequestMethod.GET)
	public String checkout(Long productId, Integer quantity, ModelMap model) {
		if (quantity == null || quantity < 1) {
			return ERROR_VIEW;
		}
		Product product = productService.find(productId);
		if (product == null) {
			return ERROR_VIEW;
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			return ERROR_VIEW;
		}
		if (!product.getIsMarketable()) {
			return ERROR_VIEW;
		}
		if (quantity > product.getAvailableStock()) {
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			return ERROR_VIEW;
		}
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		Receiver defaultReceiver = receiverService.findDefault(member);
		List<Order> orders = orderService.generate(Order.Type.exchange, cart, defaultReceiver, null, null, null, null, null, null);

		Long exchangePoint = 0L;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			exchangePoint = exchangePoint + order.getExchangePoint();
			rewardPoint = rewardPoint + order.getRewardPoint();
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		model.addAttribute("orders", orders);
		model.addAttribute("exchangePoint", exchangePoint);
		model.addAttribute("rewardPoint", rewardPoint);
		model.addAttribute("fee", fee);
		model.addAttribute("freight", freight);
		model.addAttribute("tax", tax);
		model.addAttribute("promotionDiscount", promotionDiscount);
		model.addAttribute("couponDiscount", couponDiscount);
		model.addAttribute("amount", amount);
		model.addAttribute("amountPayable", amountPayable);
		model.addAttribute("productId", productId);
		model.addAttribute("quantity", quantity);
		model.addAttribute("isDelivery", isDelivery);
		model.addAttribute("orderType", Order.Type.exchange);
		model.addAttribute("defaultReceiver", defaultReceiver);
		model.addAttribute("paymentMethods", paymentMethodService.findAll());
		model.addAttribute("agreement", goodsService.getCommonAgreement());
		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<PaymentMethod>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (cart.isContainGeneral()) {
				if (paymentMethod.getMethod().equals(PaymentMethod.Method.online)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				availablePaymentMethods.add(paymentMethod);
			}
		}
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		return "/shop/${theme}/order/checkout";
	}

	/**
	 * 计算
	 */
	@RequestMapping(value = "/calculate", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> calculate(Long receiverId, Long paymentMethodId, Long shippingMethodId, String code, String invoiceTitle, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent();
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !member.equals(receiver.getMember())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}

		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		CouponCode couponCode = couponCodeService.findByCode(code);
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<Order> orders = orderService.generate(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
		}

		data.put("message", SUCCESS_MESSAGE);
		data.put("price", price);
		data.put("fee", fee);
		data.put("freight", freight);
		data.put("tax", tax);
		data.put("promotionDiscount", promotionDiscount);
		data.put("couponDiscount", couponDiscount);
		data.put("amount", amount);
		data.put("amountPayable", amountPayable);
		return data;
	}
	
	/**
	 * 直接购买计算
	 */
	@RequestMapping(value = "/calculate",params = "type=directBuy", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> calculate(Long productId, Integer quantity, Long receiverId, Long paymentMethodId, String code, String invoiceTitle, Long shippingMethodId, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		
		//只能是普通商品或挂牌商品
		Order.Type type = Order.Type.general;
		if (Goods.Type.listedtrade.equals(product.getType())){
			type = Order.Type.listedtrade;
		}else if(!Goods.Type.general.equals(product.getType())){
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		
		Member member = memberService.getCurrent();
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !member.equals(receiver.getMember())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		CouponCode couponCode = couponCodeService.findByCode(code);
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		List<Order> orders = orderService.generate(type, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);
		
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
		}

		data.put("message", SUCCESS_MESSAGE);
		data.put("price", price);
		data.put("fee", fee);
		data.put("freight", freight);
		data.put("tax", tax);
		data.put("promotionDiscount", promotionDiscount);
		data.put("couponDiscount", couponDiscount);
		data.put("amount", amount);
		data.put("amountPayable", amountPayable);
		return data;
	}

	/**
	 * 计算
	 */
	@RequestMapping(value = "/calculate", params = "type=exchange", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> calculate(Long productId, Integer quantity, Long receiverId, Long paymentMethodId, Long shippingMethodId, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !member.equals(receiver.getMember())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		List<Order> orders = orderService.generate(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, null, null, balance, null);
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
		}

		data.put("message", SUCCESS_MESSAGE);
		data.put("price", price);
		data.put("fee", fee);
		data.put("freight", freight);
		data.put("tax", tax);
		data.put("promotionDiscount", promotionDiscount);
		data.put("couponDiscount", couponDiscount);
		data.put("amount", amount);
		data.put("amountPayable", amountPayable);
		return data;
	}

	/**
	 * 创建
	 */

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> create(String cartTag, Long receiverId, Long paymentMethodId, Long shippingMethodId, String code, String invoiceTitle, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		Cart cart = cartService.getCurrent();
		if (cart == null || cart.isEmpty()) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (!StringUtils.equals(cart.getTag(), cartTag)) {
			data.put("message", Message.warn("shop.order.cartHasChanged"));
			return data;
		}
		if (cart.hasNotActive(null)) {
			data.put("message", Message.warn("shop.order.hasNotActive"));
			return data;
		}
		if (cart.hasNotMarketable(null)) {
			data.put("message", Message.warn("shop.order.hasNotMarketable"));
			return data;
		}
		if (cart.getIsLowStock(null)) {
			data.put("message", Message.warn("shop.order.cartLowStock"));
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (cart.getIsDelivery(null)) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.equals(receiver.getMember())) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null && !cart.isValid(couponCode, couponCode.getCoupon().getStore())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<Order> orders = orderService.create(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, new Operator(member), balance, memo);
		List<String> orderSns = new ArrayList<String>();
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("orderSns", orderSns);
		return data;
	}
	
	/**
	 * 直接购买创建
	 */
	@RequestMapping(value = "/create", params = "type=directBuy", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> create(Boolean isListedtrade, Long productId, Integer quantity, Long receiverId, Long paymentMethodId, Long shippingMethodId, String code, String invoiceTitle, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		//只能是普通商品或挂牌商品
		Order.Type type = Order.Type.general;
		if (Goods.Type.listedtrade.equals(product.getType())){
			type = Order.Type.listedtrade;
		}else if(!Goods.Type.general.equals(product.getType())){
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("shop.order.productNotMarketable"));
			return data;
		}
		if (quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.order.productLowStock"));
			return data;
		}
		
		Member member = memberService.getCurrent();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (product.getIsDelivery() && !isListedtrade) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.equals(receiver.getMember())) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
		} 
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			data.put("message", Message.warn("shop.order.lowPoint"));
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		cart.setIsListedtrade(isListedtrade);
		
		if (couponCode != null && couponCode.getCoupon() != null && !cart.isValid(couponCode, couponCode.getCoupon().getStore())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<String> orderSns = new ArrayList<String>();
		List<Order> orders = orderService.create(type, cart, receiver, paymentMethod, shippingMethod, couponCode, invoice, new Operator(member), balance, memo);
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("orderSns", orderSns);
		return data;
	}

	/**
	 * 创建
	 */
	@RequestMapping(value = "/create", params = "type=exchange", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> create(Long productId, Integer quantity, Long receiverId, Long paymentMethodId, Long shippingMethodId, BigDecimal balance, String memo) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(productId);
		if (product == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (!Goods.Type.exchange.equals(product.getType())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (!product.getIsMarketable()) {
			data.put("message", Message.warn("shop.order.productNotMarketable"));
			return data;
		}
		if (quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.order.productLowStock"));
			return data;
		}
		Member member = memberService.getCurrent();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (product.getIsDelivery()) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !member.equals(receiver.getMember())) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
		}
		if (member.getPoint() < product.getExchangePoint() * quantity) {
			data.put("message", Message.warn("shop.order.lowPoint"));
			return data;
		}
		if (balance != null && balance.compareTo(member.getBalance()) > 0) {
			data.put("message", Message.warn("shop.order.insufficientBalance"));
			return data;
		}
		Set<CartItem> cartItems = new HashSet<CartItem>();
		CartItem cartItem = new CartItem();
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(member);
		cart.setCartItems(cartItems);
		List<String> orderSns = new ArrayList<String>();
		List<Order> orders = orderService.create(Order.Type.exchange, cart, receiver, paymentMethod, shippingMethod, null, null, new Operator(member), balance, memo);
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("orderSns", orderSns);
		return data;
	}

	/**
	 * 支付
	 */
	@RequestMapping(value = "/payment", method = RequestMethod.GET)
	public String payment(String[] orderSns, ModelMap model, RedirectAttributes redirectAttributes) {
		if (orderSns.length <= 0) {
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		List<PaymentPlugin> paymentPlugins = pluginService.getPaymentPlugins(true);
		PaymentPlugin defaultPaymentPlugin = null;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		List<String> pOrderSn = new ArrayList<String>();
		boolean online = false;
		List<Order> orders = new ArrayList<Order>();
		String shippingMethodName = null;
		String paymentMethodName = null;
		Date expireDate = null;
		Boolean isListedtrade = false;//是否是挂牌交易交货
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null || !member.equals(order.getMember()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
				return ERROR_VIEW;
			}
			if (order.getAmount().compareTo(order.getAmountPaid()) != 0) {
				if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod())) {
					if (orderService.isLocked(order, member.getLockKey(), true)) {
						addFlashMessage(redirectAttributes, Message.warn("shop.order.locked"));
						return "redirect:/member/order/list.jhtml";
					}
					if (CollectionUtils.isNotEmpty(paymentPlugins)) {
						defaultPaymentPlugin = paymentPlugins.get(0);
						amount = amount.add(order.getAmountPayable());
					}
					online = true;
				} else {
					amount = amount.add(order.getAmountPayable());
					fee = fee.add(order.getFee());
					online = false;
				}
				shippingMethodName = order.getShippingMethodName();
				paymentMethodName = order.getPaymentMethodName();
				expireDate = order.getExpire();
				pOrderSn.add(order.getSn());
				orders.add(order);
			}
			if(order.getIsListedtrade()){//挂牌交易配送
				isListedtrade = true;
			}
		}
		if (defaultPaymentPlugin != null) {
			amount = defaultPaymentPlugin.calculateFee(amount).add(amount);
			model.addAttribute("fee", defaultPaymentPlugin.calculateFee(amount));
			model.addAttribute("online", online);
			model.addAttribute("fee", fee);
			model.addAttribute("defaultPaymentPlugin", defaultPaymentPlugin);
			model.addAttribute("paymentPlugins", paymentPlugins);
		}
		model.addAttribute("fee", online && defaultPaymentPlugin != null ? defaultPaymentPlugin.calculateFee(amount) : fee);
		model.addAttribute("amount", amount);
		model.addAttribute("shippingMethodName", shippingMethodName);
		model.addAttribute("paymentMethodName", paymentMethodName);
		model.addAttribute("expireDate", expireDate);
		model.addAttribute("orders", orders);
		model.addAttribute("orderSns", pOrderSn);
		model.addAttribute("isListedtrade", isListedtrade);
		return "/shop/${theme}/order/payment";
	}

	/**
	 * 计算支付金额
	 */
	@RequestMapping(value = "/calculate_amount", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> calculateAmount(String paymentPluginId, String[] orderSns) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (orderSns.length <= 0) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Member member = memberService.getCurrent();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		BigDecimal amount = BigDecimal.ZERO;
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null || !member.equals(order.getMember()) || paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
				data.put("message", ERROR_MESSAGE);
				return data;
			}
			amount = amount.add(order.getAmountPayable());
		}
		BigDecimal fee = paymentPlugin.calculateFee(amount);
		data.put("message", SUCCESS_MESSAGE);
		data.put("fee", fee);
		data.put("amount", amount.add(fee));
		return data;
	}
	
	/**
	 * 交易所账户支付
	 */
	@RequestMapping(value = "/exAcountPayment", method = RequestMethod.GET)
	public String exAcountPayment(String[] orderSns, ModelMap model, RedirectAttributes redirectAttributes,String info) {
		if (orderSns.length <= 0) {
			return ERROR_VIEW;
		}
		Member member = memberService.getCurrent();
		List<String> pOrderSn = new ArrayList<String>();
		List<Order> orders = new ArrayList<Order>();
		BigDecimal amount = BigDecimal.ZERO;//应付金额
		BigDecimal fee = BigDecimal.ZERO;//手续费
		String shippingMethodName = null;//配送方式
		String paymentMethodName = null;//支付方式
		Date expireDate = null;//过期时间
		Boolean isListedtrade = false;//是否是挂牌交易交货
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null || !member.equals(order.getMember()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
				return ERROR_VIEW;
			}
			if (order.getAmount().compareTo(order.getAmountPaid()) != 0) {
				if (orderService.isLocked(order, member.getLockKey(), true)) {
					addFlashMessage(redirectAttributes, Message.warn("shop.order.locked"));
					return "redirect:/member/order/list.jhtml";
				}
				amount = amount.add(order.getAmountPayable());
				fee = fee.add(order.getFee());
				BigDecimal currentAmount = order.getAmountPayable();//当前订单应付金额
				Map<String, Object> moneyMap = getUserMoney(member.getUsername());
				BigDecimal userMoney = new BigDecimal(moneyMap.get("free").toString());
				if(userMoney.compareTo(currentAmount)<0){
					model.addAttribute("errorMessage", message("shop.business.order.insufficientBalance"));
					return ERROR_VIEW;
				}else{
					Map<String, Object> resultMap = updateUserMoney(member.getUsername(),order.getStore().getBusiness().getMember().getUsername(),currentAmount,false,null);
					if(!SUCCESS.equalsIgnoreCase((resultMap.get("code").toString()))){
                        model.addAttribute("errorMessage", message(resultMap.get("msg").toString()));
                        return ERROR_VIEW;
					}
					if(order.getIsListedtrade() && order.getShippingMethod()==null){
					    //设置订单为等待挂牌交易状态
					    order.setStatus(Order.Status.pendingListedTrade);
					    order.setShippingMethodName("挂牌交易");
					}else{
					    //设置订单为待发货状态
					    order.setStatus(Order.Status.pendingShipment);
					}
					order.setExpire(null);
					orderService.update(order);
					
					//生成支付记录
					Operator operator = new Operator(order.getStore().getBusiness());
					OrderPayment orderPayment = new OrderPayment();
					orderPayment.setMethod(OrderPayment.Method.exChangeAccount);
					orderPayment.setPaymentMethod(order.getPaymentMethodName());
					orderPayment.setAmount(currentAmount);
					orderPayment.setFee(order.getFee());
					orderPayment.setOrder(order);
					orderService.payment(order, orderPayment, operator);
				}
				shippingMethodName = order.getShippingMethodName();
				paymentMethodName = order.getPaymentMethodName();
				expireDate = order.getExpire();
				pOrderSn.add(order.getSn());
				orders.add(order);
			}
			if(order.getIsListedtrade()){//挂牌交易配送
				isListedtrade = true;
			}
			
		}
		model.addAttribute("amount", amount);
		model.addAttribute("fee", fee);
		model.addAttribute("shippingMethodName", shippingMethodName);
		model.addAttribute("paymentMethodName", paymentMethodName);
		model.addAttribute("expireDate", expireDate);
		model.addAttribute("orders", orders);
		model.addAttribute("orderSns", pOrderSn);
		model.addAttribute("isListedtrade", isListedtrade);

		return "/shop/${theme}/order/payment";
	}
	
	/**
	 * 商品买卖  交易所账户余额更变
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> updateUserMoney(String buyerId,String sellerId,BigDecimal amount,Boolean isRefund,String info) {
		HashMap<String, Object> resultMap = new HashMap<>();
		UriComponentsBuilder builder;
		// 向交易所修改余额
		builder = UriComponentsBuilder
				.fromHttpUrl(String.format("%s/exchange/mgmt/shop/money/balance",
                        exchangeUrl)).queryParam("buyerId", buyerId)
						.queryParam("sellerId", sellerId)
						.queryParam("amount", amount)
						.queryParam("isRefund", isRefund)
						.queryParam("info", info);
		try {
			resultMap = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
		} catch (HttpStatusCodeException e) {
			logger.error("商品买卖-交易所账户余额更变失败, details: {}", e);
			throw new ApplicationException("商品买卖-交易所账户余额更变失败");
		}
		return resultMap;
	}

	
	
	/**
	 * 向交易所查余额接口
	 * @return 
	 */
	@RequestMapping(value = "/userMoney", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> userMoney () {
		Member member = memberService.getCurrent();
		//向交易所查余额
		Map<String, Object> moneyMap = getUserMoney(member.getUsername());
		return moneyMap;
	}
	
	/**
	 * 向交易所查余额
	 * 
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getUserMoney(String userId) {
		HashMap<String, Object> userMoney = new HashMap<>();
		UriComponentsBuilder builder;
		// 向交易所查余额
		builder = UriComponentsBuilder
				.fromHttpUrl(String.format("%s/exchange/mgmt/userMoney",
				        exchangeUrl)).queryParam("userId", userId)
						.queryParam("exchangeId",exchangeId);

		try {
			userMoney = restTemplate.postForObject(builder.build().toUriString(), null, HashMap.class);
		} catch (HttpStatusCodeException e) {
			logger.error("向交易所获取用户余额失败, details: {}", e);
			throw new ApplicationException("获取用户余额失败");
		}
		return userMoney;
	}

}
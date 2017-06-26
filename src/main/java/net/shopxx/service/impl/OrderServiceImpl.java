/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import net.shopxx.Filter;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.controller.shop.PaymentController;
import net.shopxx.dao.*;
import net.shopxx.entity.*;
import net.shopxx.exception.ApplicationException;
import net.shopxx.exception.ShopException;
import net.shopxx.service.*;
import net.shopxx.util.SystemUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Service - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("orderServiceImpl")
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;
	@Resource(name = "orderLogDaoImpl")
	private OrderLogDao orderLogDao;
	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;
	@Resource(name = "snDaoImpl")
	private SnDao snDao;
	@Resource(name = "orderPaymentDaoImpl")
	private OrderPaymentDao orderPaymentDao;
	@Resource(name = "refundsDaoImpl")
	private RefundsDao refundsDao;
	@Resource(name = "shippingDaoImpl")
	private ShippingDao shippingDao;
	@Resource(name = "returnsDaoImpl")
	private ReturnsDao returnsDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "couponCodeServiceImpl")
	private CouponCodeService couponCodeService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "shippingMethodServiceImpl")
	private ShippingMethodService shippingMethodService;
	@Resource(name = "mailServiceImpl")
	private MailService mailService;
	@Resource(name = "smsServiceImpl")
	private SmsService smsService;
	@Resource
    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Value("${exchangeUrl}")
    private String exchangeUrl;
	// 成功
	public static final String SUCCESS = "success";

	@Transactional(readOnly = true)
	public Order findBySn(String sn) {
		return orderDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public List<Order> findList(Order.Type type, Order.Status status, Store store, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
			List<net.shopxx.Order> orders) {
		return orderDao.findList(type, status, store, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(Order.Type type, Order.Status status, Store store, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPage(type, status, store, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Order.Type type, Order.Status status, Member member, Goods goods, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		return orderDao.count(type, status, member, goods, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired);
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateTax(BigDecimal price, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsTaxPriceEnabled()) {
			return BigDecimal.ZERO;
		}
		BigDecimal amount = price;
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		BigDecimal tax = amount.multiply(new BigDecimal(String.valueOf(setting.getTaxRate())));
		return tax.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(tax) : BigDecimal.ZERO;
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateTax(Order order) {
		Assert.notNull(order);

		if (order.getInvoice() == null) {
			return BigDecimal.ZERO;
		}
		return calculateTax(order.getPrice(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateAmount(BigDecimal price, BigDecimal fee, BigDecimal freight, BigDecimal tax, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(fee == null || fee.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(freight == null || freight.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(tax == null || tax.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		BigDecimal amount = price;
		if (fee != null) {
			amount = amount.add(fee);
		}
		if (freight != null) {
			amount = amount.add(freight);
		}
		if (tax != null) {
			amount = amount.add(tax);
		}
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		return amount.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(amount) : BigDecimal.ZERO;
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateAmount(Order order) {
		Assert.notNull(order);

		return calculateAmount(order.getPrice(), order.getFee(), order.getFreight(), order.getTax(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	public boolean isLocked(Order order, String lockKey, boolean autoLock) {
		Assert.notNull(order);
		Assert.notNull(lockKey);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), lockKey);
		if (autoLock && !isLocked && StringUtils.isNotEmpty(lockKey)) {
			order.setLockKey(lockKey);
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
		return isLocked;
	}

	public void lock(Order order, String lockKey) {
		Assert.notNull(order);
		Assert.notNull(lockKey);

		boolean isLocked = order.getLockExpire() != null && order.getLockExpire().after(new Date()) && StringUtils.isNotEmpty(order.getLockKey()) && !StringUtils.equals(order.getLockKey(), lockKey);
		if (!isLocked && StringUtils.isNotEmpty(lockKey)) {
			order.setLockKey(lockKey);
			order.setLockExpire(DateUtils.addSeconds(new Date(), Order.LOCK_EXPIRE));
		}
	}

	public void undoExpiredUseCouponCode() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, true, null, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoUseCouponCode(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	public void undoExpiredExchangePoint() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, true, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoExchangePoint(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	public void releaseExpiredAllocatedStock() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, null, true, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					releaseAllocatedStock(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Order> generate(Order.Type type, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.notNull(cart.getStores());
		Assert.state(!cart.isEmpty());

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		Long effectiveRewardPoint = 0L;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		List<Store> stores = cart.getStores();
		List<Order> orders = new ArrayList<Order>();
		for (Store store : stores) {
			price = cart.getPrice(store);
			discount = cart.getDiscount(store);
			effectiveRewardPoint = cart.getEffectiveRewardPoint(store);
			freight = shippingMethod != null && shippingMethod.isSupported(paymentMethod) && cart.getIsDelivery(store) ? shippingMethodService.calculateFreight(shippingMethod, store, receiver, cart.getTotalWeight(store)) : BigDecimal.ZERO;
			couponDiscount = couponCode != null && cart.isCouponAllowed(store) && cart.isValid(couponCode, store) ? cart.getEffectivePrice(store).subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(store), cart.getProductQuantity(store))) : BigDecimal.ZERO;
			Order order = new Order();
			order.setType(type);
			order.setPrice(price);
			order.setFee(BigDecimal.ZERO);
			order.setPromotionDiscount(discount);
			order.setOffsetAmount(BigDecimal.ZERO);
			order.setRefundAmount(BigDecimal.ZERO);
			order.setRewardPoint(effectiveRewardPoint);
			order.setExchangePoint(cart.getExchangePoint(store));
			order.setWeight(cart.getTotalWeight(store));
			order.setQuantity(cart.getTotalQuantity(store));
			order.setShippedQuantity(0);
			order.setReturnedQuantity(0);
			order.setMemo(memo);
			order.setIsUseCouponCode(false);
			order.setIsExchangePoint(false);
			order.setIsAllocatedStock(false);
			order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
			order.setPaymentMethod(paymentMethod);
			order.setMember(member);
			order.setStore(store);
			order.setPromotionNames(new ArrayList<String>(cart.getPromotionNames(store)));
			order.setCoupons(new ArrayList<Coupon>(cart.getCoupons(store)));

			if (shippingMethod != null && shippingMethod.isSupported(paymentMethod) && cart.getIsDelivery(store)) {
				order.setFreight(freight);
				order.setShippingMethod(shippingMethod);
			} else {
				order.setFreight(BigDecimal.ZERO);
				order.setShippingMethod(null);
			}

			if (couponCode != null && cart.isCouponAllowed(store) && cart.isValid(couponCode, store)) {
				order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
				order.setCouponCode(couponCode);
			} else {
				order.setCouponDiscount(BigDecimal.ZERO);
				order.setCouponCode(null);
			}

			order.setTax(calculateTax(order));
			order.setAmount(calculateAmount(order));

			if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(member.getBalance()) <= 0) {
				if (balance.compareTo(order.getAmount()) <= 0) {
					order.setAmountPaid(balance);
				} else {
					order.setAmountPaid(order.getAmount());
					balance = balance.subtract(order.getAmount());
				}
			} else {
				order.setAmountPaid(BigDecimal.ZERO);
			}

			if (cart.getIsDelivery(store) && receiver != null) {
				order.setConsignee(receiver.getConsignee());
				order.setAreaName(receiver.getAreaName());
				order.setAddress(receiver.getAddress());
				order.setZipCode(receiver.getZipCode());
				order.setPhone(receiver.getPhone());
				order.setArea(receiver.getArea());
			}

			List<OrderItem> orderItems = order.getOrderItems();
			for (CartItem cartItem : cart.getCartItems(store)) {
				Product product = cartItem.getProduct();
				if (product != null) {
					OrderItem orderItem = new OrderItem();
					orderItem.setSn(product.getSn());
					orderItem.setName(product.getName());
					orderItem.setType(product.getType());
					orderItem.setPrice(cartItem.getPrice());
					orderItem.setWeight(product.getWeight());
					orderItem.setIsDelivery(product.getIsDelivery());
					orderItem.setThumbnail(product.getThumbnail());
					orderItem.setQuantity(cartItem.getQuantity());
					orderItem.setShippedQuantity(0);
					orderItem.setReturnedQuantity(0);
					orderItem.setProduct(cartItem.getProduct());
					orderItem.setOrder(order);
					orderItem.setSpecifications(product.getSpecifications());
					orderItems.add(orderItem);
				}
			}

			for (Product gift : cart.getGifts(store)) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setType(gift.getType());
				orderItem.setPrice(BigDecimal.ZERO);
				orderItem.setWeight(gift.getWeight());
				orderItem.setIsDelivery(gift.getIsDelivery());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setQuantity(1);
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setProduct(gift);
				orderItem.setOrder(order);
				orderItem.setSpecifications(gift.getSpecifications());
				orderItems.add(orderItem);
			}
			orders.add(order);
		}
		return orders;
	}

	public List<Order> create(Order.Type type, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, Operator operator, BigDecimal balance, String memo) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());
		if (cart.getIsDelivery(null) && !cart.getIsListedtrade()) {
			Assert.notNull(receiver);
			Assert.notNull(shippingMethod);
			Assert.state(shippingMethod.isSupported(paymentMethod));
		} else {
			Assert.isNull(receiver);
			Assert.isNull(shippingMethod);
		}
		List<Store> stores = cart.getStores();
		List<Order> orders = new ArrayList<Order>();

		for (CartItem cartItem : cart.getCartItems()) {
			Product product = cartItem.getProduct();
			if (product == null || !product.getIsMarketable() || cartItem.getQuantity() > product.getAvailableStock()) {
				throw new IllegalArgumentException();
			}
		}

		for (Store store : stores) {
			for (Product gift : cart.getGifts(store)) {
				if (!gift.getIsMarketable() || gift.getIsOutOfStock()) {
					throw new IllegalArgumentException();
				}
			}

			Setting setting = SystemUtils.getSetting();
			Member member = cart.getMember();

			Order order = new Order();
			order.setIsListedtrade(cart.getIsListedtrade());
			order.setSn(snDao.generate(Sn.Type.order));
			order.setType(type);
			order.setPrice(cart.getPrice(store));
			order.setFee(BigDecimal.ZERO);
			order.setFreight(cart.getIsDelivery(store) ? shippingMethodService.calculateFreight(shippingMethod, store, receiver, cart.getTotalWeight(store)) : BigDecimal.ZERO);
			order.setPromotionDiscount(cart.getDiscount(store));
			order.setOffsetAmount(BigDecimal.ZERO);
			order.setAmountPaid(BigDecimal.ZERO);
			order.setRefundAmount(BigDecimal.ZERO);
			order.setRewardPoint(cart.getEffectiveRewardPoint(store));
			order.setExchangePoint(cart.getExchangePoint(store));
			order.setWeight(cart.getTotalWeight(store));
			order.setQuantity(cart.getTotalQuantity(store));
			order.setShippedQuantity(0);
			order.setReturnedQuantity(0);
			if (cart.getIsDelivery(store)) {
				order.setConsignee(receiver.getConsignee());
				order.setAreaName(receiver.getAreaName());
				order.setAddress(receiver.getAddress());
				order.setZipCode(receiver.getZipCode());
				order.setPhone(receiver.getPhone());
				order.setArea(receiver.getArea());
			}
			order.setMemo(memo);
			order.setIsUseCouponCode(false);
			order.setIsExchangePoint(false);
			order.setIsAllocatedStock(false);
			order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
			order.setShippingMethod(shippingMethod);
			order.setMember(member);
			order.setStore(store);
			order.setPromotionNames(cart.getPromotionNames(store));
			order.setCoupons(new ArrayList<Coupon>(cart.getCoupons(store)));

			if (couponCode != null && couponCode.getCoupon().getStore().equals(store)) {
				if (!cart.isCouponAllowed(store) || !cart.isValid(couponCode, store)) {
					throw new IllegalArgumentException();
				}
				BigDecimal couponDiscount = cart.getEffectivePrice(store).subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(store), cart.getProductQuantity(store)));
				order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
				order.setCouponCode(couponCode);
				useCouponCode(order);
			} else {
				order.setCouponDiscount(BigDecimal.ZERO);
			}

			order.setTax(calculateTax(order));
			order.setAmount(calculateAmount(order));
			if (balance != null && (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(member.getBalance()) > 0)) {
				throw new IllegalArgumentException();
			}
			BigDecimal amountPayable = balance != null ? order.getAmount().subtract(balance) : order.getAmount();
			if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
				if (paymentMethod == null) {
					throw new IllegalArgumentException();
				}
				order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getType()) ? Order.Status.pendingPayment : Order.Status.pendingReview);
				order.setPaymentMethod(paymentMethod);
				if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatus())) {
					order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
				}
				if (PaymentMethod.Method.online.equals(paymentMethod.getMethod())) {
					lock(order, member.getLockKey());
				}
			} else {
				order.setStatus(Order.Status.pendingReview);
				order.setPaymentMethod(null);
			}

			List<OrderItem> orderItems = order.getOrderItems();
			for (CartItem cartItem : cart.getCartItems(store)) {
				Product product = cartItem.getProduct();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(product.getSn());
				orderItem.setName(product.getName());
				orderItem.setType(product.getType());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(product.getWeight());
				orderItem.setIsDelivery(product.getIsDelivery());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setCommissionTotals(product.getCommission(store.getType()).multiply(new BigDecimal(cartItem.getQuantity().toString())));
				orderItem.setProduct(cartItem.getProduct());
				orderItem.setOrder(order);
				orderItem.setSpecifications(product.getSpecifications());
				orderItems.add(orderItem);
			}

			for (Product gift : cart.getGifts(store)) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setType(gift.getType());
				orderItem.setPrice(BigDecimal.ZERO);
				orderItem.setWeight(gift.getWeight());
				orderItem.setIsDelivery(gift.getIsDelivery());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setQuantity(1);
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setCommissionTotals(gift.getCommission(store.getType()).multiply(new BigDecimal("1")));
				orderItem.setProduct(gift);
				orderItem.setOrder(order);
				orderItem.setSpecifications(gift.getSpecifications());
				orderItems.add(orderItem);
			}

			orderDao.persist(order);

			OrderLog orderLog = new OrderLog();
			orderLog.setType(OrderLog.Type.create);
			orderLog.setOperator(operator);
			orderLog.setOrder(order);
			orderLogDao.persist(orderLog);

			exchangePoint(order);
			if (Setting.StockAllocationTime.order.equals(setting.getStockAllocationTime())
					|| (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime()) && (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 || order.getExchangePoint() > 0 || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0))) {
				allocateStock(order);
			}

			if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
				OrderPayment orderPayment = new OrderPayment();
				orderPayment.setMethod(OrderPayment.Method.deposit);
				orderPayment.setFee(BigDecimal.ZERO);
				orderPayment.setOrder(order);
				if (balance.compareTo(order.getAmount()) >= 0) {
					balance = balance.subtract(order.getAmount());
					orderPayment.setAmount(order.getAmount());
				} else {
					orderPayment.setAmount(balance);
					balance = BigDecimal.ZERO;
				}
				payment(order, orderPayment, operator);
			}

			mailService.sendCreateOrderMail(order);
			smsService.sendCreateOrderSms(order);
			orders.add(order);
		}

		if (!cart.isNew()) {
			cartDao.remove(cart);
		}
		return orders;
	}

	public void update(Order order, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatus()) || Order.Status.pendingReview.equals(order.getStatus())));
		Assert.notNull(operator);

		order.setAmount(calculateAmount(order));
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview);
			order.setExpire(null);
		} else {
			if (order.getPaymentMethod() != null && PaymentMethod.Type.deliveryAgainstPayment.equals(order.getPaymentMethod().getType())) {
				order.setStatus(Order.Status.pendingPayment);
			} else {
				order.setStatus(Order.Status.pendingReview);
				order.setExpire(null);
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.update);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendUpdateOrderMail(order);
		smsService.sendUpdateOrderSms(order);
	}

	public void cancel(Order order, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatus()) || Order.Status.pendingReview.equals(order.getStatus())));
		Assert.notNull(operator);

		order.setStatus(Order.Status.canceled);
		order.setExpire(null);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);
		
		//如果是交易所余额支付的方式，不增加商户的账户余额
		if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
				memberService.addBalance(order.getStore().getBusiness().getMember(), order.getAmountPaid(), DepositLog.Type.orderPayment, operator, null);
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.cancel);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendCancelOrderMail(order);
		smsService.sendCancelOrderSms(order);
	}

	public void review(Order order, boolean passed, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.pendingReview.equals(order.getStatus()));
		Assert.notNull(operator);

		if (passed) {
			order.setStatus(Order.Status.pendingShipment);
		} else {
			order.setStatus(Order.Status.denied);
			//如果是交易所余额支付的方式，不增加商户的账户余额
			if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
				if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
					memberService.addBalance(order.getStore().getBusiness().getMember(), order.getAmountPaid(), DepositLog.Type.orderPayment, operator, null);
				}
			}
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.review);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReviewOrderMail(order);
		smsService.sendReviewOrderSms(order);
	}

	public void payment(Order order, OrderPayment orderPayment, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(orderPayment);
		Assert.isTrue(orderPayment.isNew());
		Assert.notNull(orderPayment.getAmount());
		Assert.state(orderPayment.getAmount().compareTo(BigDecimal.ZERO) > 0);

		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));
		orderPayment.setOrder(order);
		orderPayment.setOperator(operator);
		orderPaymentDao.persist(orderPayment);
		
		//如果是交易所余额支付的方式，不增加商户的账户余额
		if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (order.getMember() != null && OrderPayment.Method.deposit.equals(orderPayment.getMethod())) {
				memberService.addBalance(order.getMember(), orderPayment.getEffectiveAmount().negate(), DepositLog.Type.orderPayment, operator, null);
				if (operator != null && operator.getType().equals(Operator.Type.business)) {
					memberService.addBalance(order.getStore().getBusiness().getMember(), orderPayment.getEffectiveAmount(), DepositLog.Type.orderPayment, operator, null);
				}
			}
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		order.setAmountPaid(order.getAmountPaid().add(orderPayment.getEffectiveAmount()));
		order.setFee(order.getFee().add(orderPayment.getFee()));
		if (!order.hasExpired() && Order.Status.pendingPayment.equals(order.getStatus()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview);
			order.setExpire(null);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.payment);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendPaymentOrderMail(order);
		smsService.sendPaymentOrderSms(order);
	}

	@Transactional(rollbackFor = ShopException.class)
	public void refunds(Order order, Refunds refunds, Operator operator) throws ShopException {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0);
		Assert.notNull(refunds);
		Assert.isTrue(refunds.isNew());
		Assert.notNull(refunds.getAmount());
		Assert.state(refunds.getAmount().compareTo(BigDecimal.ZERO) > 0 && refunds.getAmount().compareTo(order.getRefundableAmount()) <= 0);
		Assert.notNull(operator);

		refunds.setSn(snDao.generate(Sn.Type.refunds));
		refunds.setOrder(order);
		refundsDao.persist(refunds);
		
		//如果是交易所余额支付的方式，不增加买家余额
		if(order.getPaymentMethod()!=null && order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (Refunds.Method.deposit.equals(refunds.getMethod())) {
				memberService.addBalance(order.getMember(), refunds.getAmount(), DepositLog.Type.refunds, operator, null);
			}
		}

		Member storeMember = order.getStore().getBusiness().getMember();
		BigDecimal storeBalance = storeMember.getBalance();
		if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (storeBalance.compareTo(BigDecimal.ZERO) > 0 && storeBalance.compareTo(order.getRefundableAmount()) > 0) {
				memberService.addBalance(storeMember, refunds.getAmount().negate(), DepositLog.Type.refunds, operator, null);
			}
		}

		order.setAmountPaid(order.getAmountPaid().subtract(refunds.getAmount()));
		order.setRefundAmount(order.getRefundAmount().add(refunds.getAmount()));

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.refunds);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendRefundsOrderMail(order);
		smsService.sendRefundsOrderSms(order);

		if(order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){//如果是交易所余额支付，调交易所余额接口退款
		    Map<String, Object> resultMap= updateUserMoney(order.getMember().getUsername(),
                    order.getStore().getBusiness().getMember().getUsername(),refunds.getAmount(),true,null);
			if(!SUCCESS.equalsIgnoreCase((resultMap.get("code").toString()))){
                throw new ShopException(resultMap.get("msg").toString());
			}
        }
	}
	

    /**
     * 商品买卖  交易所账户余额更变
     *
     * @param
     * @return
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
            logger.error("商品退款-交易所账户余额更变失败, details: {}", e);
            throw new ApplicationException("商品退款-交易所账户余额更变失败");
        }
        return resultMap;
    }

	public void shipping(Order order, Shipping shipping, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getShippableQuantity() > 0);
		Assert.notNull(shipping);
		Assert.isTrue(shipping.isNew());
		Assert.notEmpty(shipping.getShippingItems());
		Assert.notNull(operator);

		shipping.setSn(snDao.generate(Sn.Type.shipping));
		shipping.setOrder(order);
		shippingDao.persist(shipping);

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.ship.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		for (ShippingItem shippingItem : shipping.getShippingItems()) {
			OrderItem orderItem = order.getOrderItem(shippingItem.getSn());
			if (orderItem == null || shippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + shippingItem.getQuantity());
			Product product = shippingItem.getProduct();
			if (product != null) {
				if (shippingItem.getQuantity() > product.getStock()) {
					throw new IllegalArgumentException();
				}
				productService.addStock(product, -shippingItem.getQuantity(), StockLog.Type.stockOut, operator, null);
				if (BooleanUtils.isTrue(order.getIsAllocatedStock())) {
					productService.addAllocatedStock(product, -shippingItem.getQuantity());
				}
			}
		}

		order.setShippedQuantity(order.getShippedQuantity() + shipping.getQuantity());
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setStatus(Order.Status.shipped);
			order.setIsAllocatedStock(false);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.shipping);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendShippingOrderMail(order);
		smsService.sendShippingOrderSms(order);
	}

	public void returns(Order order, Returns returns, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getReturnableQuantity() > 0);
		Assert.notNull(returns);
		Assert.isTrue(returns.isNew());
		Assert.notEmpty(returns.getReturnsItems());
		Assert.notNull(operator);

		returns.setSn(snDao.generate(Sn.Type.returns));
		returns.setOrder(order);
		returnsDao.persist(returns);

		for (ReturnsItem returnsItem : returns.getReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(returnsItem.getSn());
			if (orderItem == null || returnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + returnsItem.getQuantity());
		}

		order.setReturnedQuantity(order.getReturnedQuantity() + returns.getQuantity());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.returns);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReturnsOrderMail(order);
		smsService.sendReturnsOrderSms(order);
	}

	public void receive(Order order, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.shipped.equals(order.getStatus()));
		Assert.notNull(operator);

		order.setStatus(Order.Status.received);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.receive);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReceiveOrderMail(order);
		smsService.sendReceiveOrderSms(order);
	}

	@CacheEvict(value = { "goods", "productCategory" }, allEntries = true)
	public void complete(Order order, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.received.equals(order.getStatus()) ||
				Order.Status.listed.equals(order.getStatus())));
		Assert.notNull(operator);

		Member member = order.getMember();
		Store store = order.getStore();
		if (order.getRewardPoint() > 0) {
			memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, operator, null);
		}
		if (CollectionUtils.isNotEmpty(order.getCoupons())) {
			for (Coupon coupon : order.getCoupons()) {
				couponCodeService.generate(coupon, member);
			}
		}
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			memberService.addAmount(member, order.getAmountPaid());
		}
		//如果是交易所余额支付的方式，不增加商户的账户余额
		if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (order.getSettlementAmount().compareTo(BigDecimal.ZERO) > 0) {
				memberService.addBalance(store.getBusiness().getMember(), order.getSettlementAmount(), DepositLog.Type.settlement, operator, null);
			}
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			Product product = orderItem.getProduct();
			if (product != null && product.getGoods() != null) {
				goodsService.addSales(product.getGoods(), orderItem.getQuantity());
			}
		}

		order.setStatus(Order.Status.completed);
		order.setCompleteDate(new Date());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.complete);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendCompleteOrderMail(order);
		smsService.sendCompleteOrderSms(order);
	}
	
   @CacheEvict(value = { "goods", "productCategory" }, allEntries = true)
    public void listed(Order order, Operator operator) {
        Assert.notNull(order);
        Assert.isTrue(!order.isNew());
        Assert.state(!order.hasExpired() && (Order.Status.pendingListedTrade.equals(order.getStatus())));
        Assert.notNull(operator);

        Member member = order.getMember();
        Store store = order.getStore();
        if (order.getRewardPoint() > 0) {
            memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, operator, null);
        }
        if (CollectionUtils.isNotEmpty(order.getCoupons())) {
            for (Coupon coupon : order.getCoupons()) {
                couponCodeService.generate(coupon, member);
            }
        }
        if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            memberService.addAmount(member, order.getAmountPaid());
        }
        //如果是交易所余额支付的方式，不增加商户的账户余额
        if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
            if (order.getSettlementAmount().compareTo(BigDecimal.ZERO) > 0) {
                memberService.addBalance(store.getBusiness().getMember(), order.getSettlementAmount(), DepositLog.Type.settlement, operator, null);
            }
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            if (product != null && product.getGoods() != null) {
                goodsService.addSales(product.getGoods(), orderItem.getQuantity());
            }
        }

        order.setStatus(Order.Status.listed);
        order.setCompleteDate(new Date());

        OrderLog orderLog = new OrderLog();
        orderLog.setType(OrderLog.Type.listed);
        orderLog.setOperator(operator);
        orderLog.setOrder(order);
        orderLogDao.persist(orderLog);

        mailService.sendCompleteOrderMail(order);
        smsService.sendCompleteOrderSms(order);
    }

	public void fail(Order order, Operator operator) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingShipment.equals(order.getStatus()) || Order.Status.shipped.equals(order.getStatus()) 
		        || Order.Status.received.equals(order.getStatus()) || Order.Status.pendingListedTrade.equals(order.getStatus())));
		Assert.notNull(operator);

		order.setStatus(Order.Status.failed);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);
		
		//如果是交易所余额支付的方式，不增加商户的账户余额
		if(order.getPaymentMethod()!=null && !order.getPaymentMethod().getMethod().equals(PaymentMethod.Method.exChangeAccount)){
			if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
				memberService.addBalance(order.getStore().getBusiness().getMember(), order.getAmountPaid(), DepositLog.Type.orderPayment, operator, null);
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.fail);
		orderLog.setOperator(operator);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendFailOrderMail(order);
		smsService.sendFailOrderSms(order);
	}

	@Override
	@Transactional
	public void delete(Order order) {
		if (order != null && !Order.Status.completed.equals(order.getStatus())) {
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}

		super.delete(order);
	}

	/**
	 * 优惠码使用
	 * 
	 * @param order
	 *            订单
	 */
	private void useCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(true);
		couponCode.setUsedDate(new Date());
		order.setIsUseCouponCode(true);
	}

	/**
	 * 优惠码使用撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoUseCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(false);
		couponCode.setUsedDate(null);
		order.setIsUseCouponCode(false);
		order.setCouponCode(null);
	}

	/**
	 * 积分兑换
	 * 
	 * @param order
	 *            订单
	 */
	private void exchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), -order.getExchangePoint(), PointLog.Type.exchange, null, null);
		order.setIsExchangePoint(true);
	}

	/**
	 * 积分兑换撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoExchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), order.getExchangePoint(), PointLog.Type.undoExchange, null, null);
		order.setIsExchangePoint(false);
	}

	/**
	 * 分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void allocateStock(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Product product = orderItem.getProduct();
				if (product != null) {
					productService.addAllocatedStock(product, orderItem.getQuantity() - orderItem.getShippedQuantity());
				}
			}
		}
		order.setIsAllocatedStock(true);
	}

	/**
	 * 释放已分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void releaseAllocatedStock(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Product product = orderItem.getProduct();
				if (product != null) {
					productService.addAllocatedStock(product, -(orderItem.getQuantity() - orderItem.getShippedQuantity()));
				}
			}
		}
		order.setIsAllocatedStock(false);
	}

	public Long CompleteOrderCount(Store store, Date beginDate, Date endDate) {
		return orderDao.completeOrderCount(store, beginDate, endDate);
	}

	public BigDecimal completeOrderAmount(Store store, Date beginDate, Date endDate) {
		return orderDao.completeOrderAmount(store, beginDate, endDate);
	}

	public void automaticReceive() {
		Date currentTime = new Date();
		while (true) {
			List<Order> orders = orderDao.findList(null, Order.Status.shipped, null, null, null, null, null, null, null, null, false, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					Shipping shipping = shippingDao.findLast(order);
					Date automaticReceiveTime = DateUtils.addDays(shipping.getCreatedDate(), SystemUtils.getSetting().getAutomaticReceiveTime());
					if (automaticReceiveTime.compareTo(currentTime) < 0) {
						order.setStatus(Order.Status.received);
					}
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	public boolean canDeleteOrder(Store store) {
		if (store != null) {
			while (true) {
				List<Order> orders = orderDao.findList(null, null, store, null, null, null, null, null, null, null, false, 100, null, null);
				if (CollectionUtils.isNotEmpty(orders)) {
					for (Order order : orders) {
						if(!order.canDelete()){
							return false;
						}
					}
					orderDao.flush();
					orderDao.clear();
				}
				if (orders.size() < 100) {
					break;
				}
			}
		}
		return true;
	}
	
	
	public List<Order> downloadOrder(Date beginDate, Date endDate){
	    return orderDao.findOrder(beginDate,endDate);
	}

}
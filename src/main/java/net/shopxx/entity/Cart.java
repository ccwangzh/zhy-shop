/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

import net.shopxx.Setting;
import net.shopxx.util.JsonUtils;
import net.shopxx.util.SystemUtils;

/**
 * Entity - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_cart")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_cart")
public class Cart extends BaseEntity<Long> implements Iterable<CartItem> {

	private static final long serialVersionUID = -6565967051825794561L;

	/** 超时时间 */
	public static final int TIMEOUT = 604800;

	/** 最大购物车项数量 */
	public static final Integer MAX_CART_ITEM_SIZE = 100;

	/** "密钥"Cookie名称 */
	public static final String KEY_COOKIE_NAME = "cartKey";

	/** "标签"Cookie名称 */
	public static final String TAG_COOKIE_NAME = "cartTag";

	/** 密钥 */
	private String key;

	/** 过期时间 */
	private Date expire;

	/** 会员 */
	private Member member;

	/** 购物车项 */
	private Set<CartItem> cartItems = new HashSet<CartItem>();
	
	/** 购物车是否为挂牌交易*/
	private Boolean isListedtrade;

	/**
	 * 获取购物车是否为挂牌交易
	 * @return
	 */
	public Boolean getIsListedtrade() {
		if(isListedtrade==null){
			return false;
		}
		return isListedtrade;
	}

	/**
	 * 设置购物车是否为挂牌交易
	 * @param isListedtrade
	 */
	public void setIsListedtrade(Boolean isListedtrade) {
		this.isListedtrade = isListedtrade;
	}

	/**
	 * 获取密钥
	 * 
	 * @return 密钥
	 */
	@Column(name = "cart_key", nullable = false, updatable = false, unique = true)
	public String getKey() {
		return key;
	}

	/**
	 * 设置密钥
	 * 
	 * @param key
	 *            密钥
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return 过期时间
	 */
	@Column(updatable = false)
	public Date getExpire() {
		return expire;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param expire
	 *            过期时间
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	@OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	public Set<CartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * 获得店铺
	 * 
	 * @return 店铺
	 */
	@Transient
	public List<Store> getStores() {
		Set<Store> stores = new HashSet<Store>();
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getStore() != null) {
					stores.add(cartItem.getStore());
				}
			}
			return new ArrayList<Store>(stores);
		}
		return Collections.emptyList();
	}

	/**
	 * 获取购物车项
	 * 
	 * @param store
	 *            店铺
	 * @return 购物车项
	 */
	@Transient
	public Set<CartItem> getCartItems(final Store store) {
		if (store == null) {
			return this.getCartItems();
		}
		Set<CartItem> cartItems = new HashSet<CartItem>();
		if (getCartItems() != null) {
			CollectionUtils.select(getCartItems(), new Predicate() {
				public boolean evaluate(Object object) {
					CartItem cartItem = (CartItem) object;
					if (cartItem == null) {
						return false;
					} else {
						return store.equals(cartItem.getStore());
					}
				}
			}, cartItems);
		}
		return cartItems;
	}

	/**
	 * 获取商品重量
	 * 
	 * @param store
	 *            店铺
	 * @return 商品重量
	 */
	@Transient
	public int getProductWeight(Store store) {
		int productWeight = 0;
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				productWeight += cartItem.getWeight();
			}
		}
		return productWeight;
	}

	/**
	 * 获取商品数量
	 * 
	 * @param store
	 *            店铺
	 * @return 商品数量
	 */
	@Transient
	public int getProductQuantity(Store store) {
		int productQuantity = 0;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems(store)) {
				if (cartItem.getQuantity() != null) {
					productQuantity += cartItem.getQuantity();
				}
			}
		}
		return productQuantity;
	}

	/**
	 * 获取赠品重量
	 * 
	 * @param store
	 *            店铺
	 * @return 赠品重量
	 */
	@Transient
	public int getGiftWeight(Store store) {
		int giftWeight = 0;
		for (Product gift : getGifts(store)) {
			if (gift.getWeight() != null) {
				giftWeight += gift.getWeight();
			}
		}
		return giftWeight;
	}

	/**
	 * 获取赠品数量
	 * 
	 * @param store
	 *            店铺
	 * @return 赠品数量
	 */
	@Transient
	public int getGiftQuantity(Store store) {
		return getGifts(store).size();
	}

	/**
	 * 获取总重量
	 * 
	 * @param store
	 *            店铺
	 * @return 总重量
	 */
	@Transient
	public int getTotalWeight(Store store) {
		return getProductWeight(store) + getGiftWeight(store);
	}

	/**
	 * 获取总数量
	 * 
	 * @param store
	 *            店铺
	 * @return 总数量
	 */
	@Transient
	public int getTotalQuantity(Store store) {
		return getProductQuantity(store) + getGiftQuantity(store);
	}

	/**
	 * 获取赠送积分
	 * 
	 * @param store
	 *            店铺
	 * @return 赠送积分
	 */
	@Transient
	public long getRewardPoint(Store store) {
		long rewardPoint = 0L;
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				rewardPoint += cartItem.getRewardPoint();
			}
		}
		return rewardPoint;
	}

	/**
	 * 获取兑换积分
	 * 
	 * @param store
	 *            店铺
	 * @return 兑换积分
	 */
	@Transient
	public long getExchangePoint(Store store) {
		long exchangePoint = 0L;
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				exchangePoint += cartItem.getExchangePoint();
			}
		}
		return exchangePoint;
	}

	/**
	 * 获取赠送积分增加值
	 * 
	 * @param store
	 *            店铺
	 * @return 赠送积分增加值
	 */
	@Transient
	public long getAddedRewardPoint(Store store) {
		Map<CartItem, Long> cartItemRewardPointMap = new HashMap<CartItem, Long>();
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				cartItemRewardPointMap.put(cartItem, cartItem.getRewardPoint());
			}
		}
		Long addedRewardPoint = 0L;
		for (Promotion promotion : getPromotions(store)) {
			long originalRewardPoint = 0;
			Set<CartItem> cartItems = getCartItems(promotion, store);
			for (CartItem cartItem : cartItems) {
				originalRewardPoint += cartItemRewardPointMap.get(cartItem);
			}
			int quantity = getQuantity(promotion, store);
			long currentRewardPoint = promotion.calculatePoint(originalRewardPoint, quantity);
			if (originalRewardPoint > 0) {
				BigDecimal rate = new BigDecimal(currentRewardPoint).divide(new BigDecimal(originalRewardPoint), RoundingMode.DOWN);
				for (CartItem cartItem : cartItems) {
					cartItemRewardPointMap.put(cartItem, new BigDecimal(cartItemRewardPointMap.get(cartItem)).multiply(rate).longValue());
				}
			} else {
				for (CartItem cartItem : cartItems) {
					cartItemRewardPointMap.put(cartItem, new BigDecimal(currentRewardPoint).divide(new BigDecimal(quantity)).longValue());
				}
			}
			addedRewardPoint += currentRewardPoint - originalRewardPoint;
		}
		return addedRewardPoint;
	}

	/**
	 * 获取有效赠送积分
	 * 
	 * @param store
	 *            店铺
	 * @return 有效赠送积分
	 */
	@Transient
	public long getEffectiveRewardPoint(Store store) {
		long effectiveRewardPoint = getRewardPoint(store) + getAddedRewardPoint(store);
		return effectiveRewardPoint >= 0L ? effectiveRewardPoint : 0L;
	}

	/**
	 * 获取有效赠送总积分
	 * 
	 * @param stores
	 *            店铺
	 * @return 有效赠送总积分
	 */
	@Transient
	public long getEffectiveRewardPointTotal(List<Store> stores) {
		long effectiveRewardPoint = 0L;
		for (Store store : stores) {
			effectiveRewardPoint = effectiveRewardPoint + getEffectiveRewardPoint(store);
		}
		return effectiveRewardPoint >= 0L ? effectiveRewardPoint : 0L;
	}

	/**
	 * 获取价格
	 * 
	 * @param store
	 *            店铺
	 * @return 价格
	 */
	@Transient
	public BigDecimal getPrice(Store store) {
		BigDecimal price = BigDecimal.ZERO;
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				price = price.add(cartItem.getSubtotal());
			}
		}
		return price;
	}

	/**
	 * 获取折扣总额
	 * 
	 * @param stores
	 *            店铺
	 * @return 折扣总额
	 */
	@Transient
	public BigDecimal getDiscountTotal(List<Store> stores) {
		BigDecimal discountTotal = BigDecimal.ZERO;
		for (Store store : stores) {
			discountTotal = discountTotal.add(getDiscount(store));
		}
		return discountTotal.compareTo(BigDecimal.ZERO) > 0 ? discountTotal : BigDecimal.ZERO;
	}

	/**
	 * 获取折扣
	 * 
	 * @param store
	 *            店铺
	 * @return 折扣
	 */
	@Transient
	public BigDecimal getDiscount(Store store) {
		Map<CartItem, BigDecimal> cartItemPriceMap = new HashMap<CartItem, BigDecimal>();
		if (getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				cartItemPriceMap.put(cartItem, cartItem.getSubtotal());
			}
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (Promotion promotion : getPromotions(store)) {
			BigDecimal originalPrice = BigDecimal.ZERO;
			BigDecimal currentPrice = BigDecimal.ZERO;
			Set<CartItem> cartItems = getCartItems(promotion, store);
			for (CartItem cartItem : cartItems) {
				originalPrice = originalPrice.add(cartItemPriceMap.get(cartItem));
			}
			if (originalPrice.compareTo(BigDecimal.ZERO) > 0) {
				int quantity = getQuantity(promotion, store);
				currentPrice = promotion.calculatePrice(originalPrice, quantity);
				BigDecimal rate = currentPrice.divide(originalPrice, MathContext.DECIMAL128);
				for (CartItem cartItem : cartItems) {
					cartItemPriceMap.put(cartItem, cartItemPriceMap.get(cartItem).multiply(rate));
				}
			} else {
				for (CartItem cartItem : cartItems) {
					cartItemPriceMap.put(cartItem, BigDecimal.ZERO);
				}
			}
			discount = discount.add(originalPrice.subtract(currentPrice));
		}
		Setting setting = SystemUtils.getSetting();
		return setting.setScale(discount);
	}

	/**
	 * 获取有效价格
	 * 
	 * @param store
	 *            店铺
	 * @return 有效价格
	 */
	@Transient
	public BigDecimal getEffectivePrice(Store store) {
		BigDecimal effectivePrice = getPrice(store).subtract(getDiscount(store));
		return effectivePrice.compareTo(BigDecimal.ZERO) >= 0 ? effectivePrice : BigDecimal.ZERO;
	}

	/**
	 * 获取有效价格总额
	 * 
	 * @param stores
	 *            店铺
	 * @return 有效价格总额
	 */
	@Transient
	public BigDecimal getEffectivePriceTotal(List<Store> stores) {
		BigDecimal effectivePrice = BigDecimal.ZERO;
		for (Store store : stores) {
			effectivePrice = effectivePrice.add(getPrice(store).subtract(getDiscount(store)));
		}
		return effectivePrice.compareTo(BigDecimal.ZERO) >= 0 ? effectivePrice : BigDecimal.ZERO;
	}

	/**
	 * 获取赠品
	 * 
	 * @return 赠品
	 */
	@Transient
	public Set<Product> getGifts(Store store) {
		Set<Product> gifts = new HashSet<Product>();
		for (Promotion promotion : getPromotions(store)) {
			if (CollectionUtils.isNotEmpty(promotion.getGifts())) {
				for (Product gift : promotion.getGifts()) {
					if (gift.getIsMarketable() && !gift.getIsOutOfStock()) {
						gifts.add(gift);
					}
				}
			}
		}
		return gifts;
	}

	/**
	 * 获取赠品名称
	 * 
	 * @param store
	 *            店铺
	 * @return 赠品名称
	 */
	@Transient
	public List<String> getGiftNames(Store store) {
		List<String> giftNames = new ArrayList<String>();
		for (Product gift : getGifts(store)) {
			giftNames.add(gift.getName());
		}
		return giftNames;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	@Transient
	public Set<Promotion> getPromotions(Store store) {
		Set<Promotion> allPromotions = new HashSet<Promotion>();
		if (getCartItems(store) != null) {
			for (CartItem cartItem : this) {
				if (cartItem.getProduct() != null && cartItem.getStore().equals(store)) {
					allPromotions.addAll(cartItem.getProduct().getValidPromotions());
				}
			}
		}
		Set<Promotion> promotions = new TreeSet<Promotion>();
		for (Promotion promotion : allPromotions) {
			if (isValid(promotion, store)) {
				promotions.add(promotion);
			}
		}
		return promotions;
	}

	/**
	 * 获取促销名称
	 * 
	 * @param store
	 *            店铺
	 * @return 促销名称
	 */
	@Transient
	public List<String> getPromotionNames(Store store) {
		List<String> promotionNames = new ArrayList<String>();
		for (Promotion promotion : getPromotions(store)) {
			promotionNames.add(promotion.getName());
		}
		return promotionNames;
	}

	/**
	 * 获取赠送优惠券
	 * 
	 * @param store
	 *            店铺
	 * @return 赠送优惠券
	 */
	@Transient
	public Set<Coupon> getCoupons(Store store) {
		Set<Coupon> coupons = new HashSet<Coupon>();
		for (Promotion promotion : getPromotions(store)) {
			if (CollectionUtils.isNotEmpty(promotion.getCoupons())) {
				coupons.addAll(promotion.getCoupons());
			}
		}
		return coupons;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @param store
	 *            店铺
	 * @return 是否需要物流
	 */
	@Transient
	public boolean getIsDelivery(Store store) {
		if(getIsListedtrade()){
			return false;
		}
		return CollectionUtils.exists(getCartItems(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && cartItem.getIsDelivery();
			}
		}) || CollectionUtils.exists(getGifts(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Product product = (Product) object;
				return product != null && product.getIsDelivery();
			}
		});
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @param store
	 *            店铺
	 * @return 是否库存不足
	 */
	@Transient
	public boolean getIsLowStock(Store store) {
		return CollectionUtils.exists(getCartItems(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && cartItem.getIsLowStock();
			}
		});
	}

	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	@Transient
	public String getTag() {
		Set<Map<String, Object>> items = new HashSet<Map<String, Object>>();
		for (CartItem cartItem : this) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("productId", cartItem.getProduct().getId());
			item.put("quantity", cartItem.getQuantity());
			item.put("price", cartItem.getPrice());
			items.add(item);
		}
		return DigestUtils.md5Hex(JsonUtils.toJson(items));
	}

	/**
	 * 获取购物车项
	 * 
	 * @param product
	 *            商品
	 * @param store
	 *            店铺
	 * @return 购物车项
	 */
	@Transient
	public CartItem getCartItem(final Product product, Store store) {
		if (product == null || getCartItems(store) == null) {
			return null;
		}

		return (CartItem) CollectionUtils.find(getCartItems(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && product.equals(cartItem.getProduct());

			}
		});
	}

	/**
	 * 判断是否包含商品
	 * 
	 * @param product
	 *            商品
	 * @param store
	 *            店铺
	 * @return 是否包含商品
	 */
	@Transient
	public boolean contains(Product product, Store store) {
		return getCartItem(product, store) != null;
	}

	/**
	 * 判断是否包含购物车项
	 * 
	 * @param cartItem
	 *            购物车项
	 * @param store
	 *            店铺
	 * @return 是否包含购物车项
	 */
	@Transient
	public boolean contains(CartItem cartItem, Store store) {
		if (cartItem != null && getCartItems(store) != null) {
			return getCartItems(store).contains(cartItem);
		}
		return false;
	}

	/**
	 * 获取购物车项
	 * 
	 * @param promotion
	 *            促销
	 * @param store
	 *            店铺
	 * @return 购物车项
	 */
	@Transient
	private Set<CartItem> getCartItems(Promotion promotion, Store store) {
		Set<CartItem> cartItems = new HashSet<CartItem>();
		if (promotion != null && getCartItems(store) != null) {
			for (CartItem cartItem : getCartItems(store)) {
				if (cartItem.getProduct() != null && cartItem.getProduct().isValid(promotion)) {
					cartItems.add(cartItem);
				}
			}
		}
		return cartItems;
	}

	/**
	 * 获取商品数量
	 * 
	 * @param promotion
	 *            促销
	 * @param store
	 *            店铺
	 * @return 商品数量
	 */
	@Transient
	private int getQuantity(Promotion promotion, Store store) {
		int quantity = 0;
		for (CartItem cartItem : getCartItems(promotion, store)) {
			if (cartItem.getQuantity() != null) {
				quantity += cartItem.getQuantity();
			}
		}
		return quantity;
	}

	/**
	 * 获取赠送积分
	 * 
	 * @param promotion
	 *            促销
	 * @param store
	 *            店铺
	 * @return 赠送积分
	 */
	@Transient
	private long getRewardPoint(Promotion promotion, Store store) {
		long rewardPoint = 0L;
		for (CartItem cartItem : getCartItems(promotion, store)) {
			rewardPoint += cartItem.getRewardPoint();
		}
		return rewardPoint;
	}

	/**
	 * 获取价格
	 * 
	 * @param promotion
	 *            促销
	 * @param store
	 *            店铺
	 * @return 价格
	 */
	@Transient
	private BigDecimal getPrice(Promotion promotion, Store store) {
		BigDecimal price = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems(promotion, store)) {
			price = price.add(cartItem.getSubtotal());
		}
		return price;
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @param store
	 *            店铺
	 * @return 促销是否有效
	 */
	@Transient
	private boolean isValid(Promotion promotion, Store store) {
		if (promotion.getType().equals(Promotion.Type.discount)) {
			if (!store.getIsDiscount() && store.getType().equals(Store.Type.general)) {
				return false;
			}
		}
		if (promotion.getType().equals(Promotion.Type.fullReduction) && store.getType().equals(Store.Type.general)) {
			if (!store.getIsFullReduction()) {
				return false;
			}
		}
		if (!promotion.getIsEnabled() || !promotion.hasBegun() || promotion.hasEnded()) {
			return false;
		}
		if (CollectionUtils.isEmpty(promotion.getMemberRanks()) || getMember() == null || getMember().getMemberRank() == null || !promotion.getMemberRanks().contains(getMember().getMemberRank())) {
			return false;
		}
		Integer quantity = getQuantity(promotion, store);
		if ((promotion.getMinimumQuantity() != null && promotion.getMinimumQuantity() > quantity) || (promotion.getMaximumQuantity() != null && promotion.getMaximumQuantity() < quantity)) {
			return false;
		}
		BigDecimal price = getPrice(promotion, store);
		if ((promotion.getMinimumPrice() != null && promotion.getMinimumPrice().compareTo(price) > 0) || (promotion.getMaximumPrice() != null && promotion.getMaximumPrice().compareTo(price) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠券是否有效
	 * 
	 * @param coupon
	 *            优惠券
	 * @param store
	 *            店铺
	 * @return 优惠券是否有效
	 */
	@Transient
	public boolean isValid(Coupon coupon, Store store) {
		if (coupon == null || !coupon.getIsEnabled() || !coupon.hasBegun() || coupon.hasExpired()) {
			return false;
		}
		if ((coupon.getMinimumQuantity() != null && coupon.getMinimumQuantity() > getProductQuantity(store)) || (coupon.getMaximumQuantity() != null && coupon.getMaximumQuantity() < getProductQuantity(store))) {
			return false;
		}
		if ((coupon.getMinimumPrice() != null && coupon.getMinimumPrice().compareTo(getEffectivePrice(store)) > 0) || (coupon.getMaximumPrice() != null && coupon.getMaximumPrice().compareTo(getEffectivePrice(store)) < 0)) {
			return false;
		}
		if (!isCouponAllowed(store)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠码是否有效
	 * 
	 * @param couponCode
	 *            优惠码
	 * @param store
	 *            店铺
	 * @return 优惠码是否有效
	 */
	@Transient
	public boolean isValid(CouponCode couponCode, Store store) {
		if (couponCode == null || couponCode.getIsUsed() || couponCode.getCoupon() == null || !couponCode.getCoupon().getStore().equals(store)) {
			return false;
		}
		return isValid(couponCode.getCoupon(), store);
	}

	/**
	 * 判断是否存在已下架商品
	 * 
	 * @param store
	 *            店铺
	 * @return 是否存在已下架商品
	 */
	@Transient
	public boolean hasNotMarketable(Store store) {
		return CollectionUtils.exists(getCartItems(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && !cartItem.getIsMarketable();
			}
		});
	}

	/**
	 * 判断是否存在已失效商品
	 * 
	 * @param store
	 *            店铺
	 * @return 是否存在已失效商品
	 */
	@Transient
	public boolean hasNotActive(Store store) {
		return CollectionUtils.exists(getCartItems(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && !cartItem.getIsActive();
			}
		});
	}

	/**
	 * 判断是否免运费
	 * 
	 * @param store
	 *            店铺
	 * @return 是否免运费
	 */
	@Transient
	public boolean isFreeShipping(Store store) {
		return CollectionUtils.exists(getPromotions(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && BooleanUtils.isTrue(promotion.getIsFreeShipping());
			}
		});
	}

	/**
	 * 判断是否允许使用优惠券
	 * 
	 * @return 是否允许使用优惠券
	 */
	@Transient
	public boolean isCouponAllowed(Store store) {
		if (store == null) {
			return false;
		}
		return !CollectionUtils.exists(getPromotions(store), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && BooleanUtils.isFalse(promotion.getIsCouponAllowed());
			}
		});
	}

	/**
	 * 购物车项数量
	 * 
	 * @return 购物车项数量
	 */
	@Transient
	public int size() {
		return getCartItems() != null ? getCartItems().size() : 0;
	}

	/**
	 * 判断购物车项是否为空
	 * 
	 * @return 购物车项是否为空
	 */
	@Transient
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(getCartItems());
	}

	/**
	 * 判断购物车中是否包含普通店铺
	 * 
	 * @return 购物车中是否包含普通店铺
	 */
	@Transient
	public boolean isContainGeneral() {
		for (Store store : getStores()) {
			if (store.getType().equals(Store.Type.general)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加购物车项
	 * 
	 * @param cartItem
	 *            购物车项
	 */
	@Transient
	public void add(CartItem cartItem) {
		if (cartItem == null) {
			return;
		}

		if (getCartItems() == null) {
			setCartItems(new HashSet<CartItem>());
		}
		getCartItems().add(cartItem);
	}

	/**
	 * 移除购物车项
	 * 
	 * @param cartItem
	 *            购物车项
	 */
	@Transient
	public void remove(CartItem cartItem) {
		if (getCartItems() != null) {
			getCartItems().remove(cartItem);
		}
	}

	/**
	 * 清空购物车项
	 */
	@Transient
	public void clear() {
		if (getCartItems() != null) {
			getCartItems().clear();
		}
	}

	/**
	 * 实现iterator方法
	 * 
	 * @return Iterator
	 */
	@Override
	@Transient
	public Iterator<CartItem> iterator() {
		return getCartItems() != null ? getCartItems().iterator() : Collections.<CartItem> emptyIterator();
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		setKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		if (getMember() == null) {
			setExpire(DateUtils.addSeconds(new Date(), Cart.TIMEOUT));
		} else {
			setExpire(null);
		}
	}

}
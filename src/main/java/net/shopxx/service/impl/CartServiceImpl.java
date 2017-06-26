/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.CartDao;
import net.shopxx.dao.CartItemDao;
import net.shopxx.entity.Business;
import net.shopxx.entity.Cart;
import net.shopxx.entity.CartItem;
import net.shopxx.entity.Member;
import net.shopxx.entity.Product;
import net.shopxx.event.CartAddedEvent;
import net.shopxx.event.CartClearedEvent;
import net.shopxx.event.CartMergedEvent;
import net.shopxx.event.CartModifiedEvent;
import net.shopxx.event.CartRemovedEvent;
import net.shopxx.service.BusinessService;
import net.shopxx.service.CartService;
import net.shopxx.service.MemberService;
import net.shopxx.util.WebUtils;

/**
 * Service - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("cartServiceImpl")
public class CartServiceImpl extends BaseServiceImpl<Cart, Long> implements CartService {

	@Resource
	private ApplicationEventPublisher applicationEventPublisher;
	@Resource(name = "cartDaoImpl")
	private CartDao cartDao;
	@Resource(name = "cartItemDaoImpl")
	private CartItemDao cartItemDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;

	@Transactional(readOnly = true)
	public Cart getCurrent() {
		Member currentMember = memberService.getCurrent();
		return currentMember != null ? currentMember.getCart() : getAnonymousCart();
	}

	public Cart create() {
		Member currentMember = memberService.getCurrent(true);
		if (currentMember != null && currentMember.getCart() != null) {
			return currentMember.getCart();
		}
		Cart cart = new Cart();
		if (currentMember != null) {
			cart.setMember(currentMember);
			currentMember.setCart(cart);
		}
		cartDao.persist(cart);
		return cart;
	}

	public void add(Cart cart, Product product, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.state(quantity > 0);

		addInternal(cart, product, quantity);

		applicationEventPublisher.publishEvent(new CartAddedEvent(this, cart, product, quantity));
	}

	public void modify(Cart cart, Product product, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(cart.contains(product, null));
		Assert.state(quantity > 0);

		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			return;
		}

		CartItem cartItem = cart.getCartItem(product, null);
		cartItem.setQuantity(quantity);

		applicationEventPublisher.publishEvent(new CartModifiedEvent(this, cart, product, quantity));
	}

	public void remove(Cart cart, Product product) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(cart.contains(product, null));

		CartItem cartItem = cart.getCartItem(product, null);
		cartItemDao.remove(cartItem);
		cart.remove(cartItem);

		applicationEventPublisher.publishEvent(new CartRemovedEvent(this, cart, product));
	}

	public void clear(Cart cart) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());

		for (CartItem cartItem : cart) {
			cartItemDao.remove(cartItem);
		}
		cart.clear();

		applicationEventPublisher.publishEvent(new CartClearedEvent(this, cart));
	}

	public void merge(Cart cart) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(cart.getMember());

		Cart anonymousCart = getAnonymousCart();
		if (anonymousCart != null) {
			for (CartItem cartItem : anonymousCart) {
				Product product = cartItem.getProduct();
				int quantity = cartItem.getQuantity();
				addInternal(cart, product, quantity);
			}
			cartDao.remove(anonymousCart);
		}

		applicationEventPublisher.publishEvent(new CartMergedEvent(this, cart));
	}

	public void deleteExpired() {
		while (true) {
			List<Cart> carts = cartDao.findList(true, 100);
			if (CollectionUtils.isNotEmpty(carts)) {
				for (Cart cart : carts) {
					cartDao.remove(cart);
				}
				cartDao.flush();
				cartDao.clear();
			}
			if (carts.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 获取匿名购物车
	 * 
	 * @return 匿名购物车，若不存在则返回null
	 */
	private Cart getAnonymousCart() {
		HttpServletRequest request = WebUtils.getRequest();
		if (request == null) {
			return null;
		}
		String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);
		Cart cart = StringUtils.isNotEmpty(key) ? cartDao.findByKey(key) : null;
		return cart != null && cart.getMember() == null ? cart : null;
	}

	/**
	 * 添加购物车商品
	 * 
	 * @param cart
	 *            购物车
	 * @param product
	 *            商品
	 * @param quantity
	 *            数量
	 */
	private void addInternal(Cart cart, Product product, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.state(quantity > 0);

		Business business = businessService.getCurrent();
		if (business != null && business.getStore() != null && business.getStore().hasContained(product)) {
			return;
		}

		if (cart.contains(product, null)) {
			CartItem cartItem = cart.getCartItem(product, null);
			if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
				return;
			}
			cartItem.add(quantity);
		} else {
			if (Cart.MAX_CART_ITEM_SIZE != null && cart.size() >= Cart.MAX_CART_ITEM_SIZE) {
				return;
			}
			if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
				return;
			}
			CartItem cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItemDao.persist(cartItem);
			cart.add(cartItem);
		}
	}

}
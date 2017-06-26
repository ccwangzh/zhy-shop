/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.event;

import net.shopxx.entity.Cart;
import net.shopxx.entity.Product;

/**
 * Event - 移除购物车商品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class CartRemovedEvent extends CartEvent {

	private static final long serialVersionUID = 6638396637072338544L;

	/** 商品 */
	private Product product;

	/**
	 * 构造方法
	 * 
	 * @param source
	 *            事件源
	 * @param cart
	 *            购物车
	 * @param product
	 *            商品
	 */
	public CartRemovedEvent(Object source, Cart cart, Product product) {
		super(source, cart);
		this.product = product;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		return product;
	}

}
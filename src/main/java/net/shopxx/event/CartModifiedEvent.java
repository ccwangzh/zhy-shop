/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.event;

import net.shopxx.entity.Cart;
import net.shopxx.entity.Product;;

/**
 * Event - 修改购物车商品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class CartModifiedEvent extends CartEvent {

	private static final long serialVersionUID = 2317148734805788101L;

	/** 商品 */
	private Product product;

	/** 数量 */
	private int quantity;

	/**
	 * 构造方法
	 * 
	 * @param source
	 *            事件源
	 * @param cart
	 *            购物车
	 * @param product
	 *            商品
	 * @param quantity
	 *            数量
	 */
	public CartModifiedEvent(Object source, Cart cart, Product product, int quantity) {
		super(source, cart);
		this.product = product;
		this.quantity = quantity;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		return quantity;
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import net.shopxx.entity.Cart;
import net.shopxx.entity.Product;

/**
 * Service - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface CartService extends BaseService<Cart, Long> {

	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车，若不存在则返回null
	 */
	Cart getCurrent();

	/**
	 * 创建购物车
	 * 
	 * @return 购物车
	 */
	Cart create();

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
	void add(Cart cart, Product product, int quantity);

	/**
	 * 修改购物车商品
	 * 
	 * @param cart
	 *            购物车
	 * @param product
	 *            商品
	 * @param quantity
	 *            数量
	 */
	void modify(Cart cart, Product product, int quantity);

	/**
	 * 移除购物车商品
	 * 
	 * @param cart
	 *            购物车
	 * @param product
	 *            商品
	 */
	void remove(Cart cart, Product product);

	/**
	 * 清空购物车商品
	 * 
	 * @param cart
	 *            购物车
	 */
	void clear(Cart cart);

	/**
	 * 合并购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	void merge(Cart cart);

	/**
	 * 删除过期购物车
	 */
	void deleteExpired();

}
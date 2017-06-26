/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.entity.Order;
import net.shopxx.entity.Shipping;

/**
 * Dao - 发货单
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface ShippingDao extends BaseDao<Shipping, Long> {

	/**
	 * 根据编号查找发货单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 发货单，若不存在则返回null
	 */
	Shipping findBySn(String sn);

	/**
	 * 查找最后一条发货单
	 * 
	 * @param order
	 *            订单
	 * @return 发货单，若不存在则返回null
	 */
	Shipping findLast(Order order);

}
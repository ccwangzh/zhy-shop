/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.ShippingItemDao;
import net.shopxx.entity.ShippingItem;

/**
 * Dao - 发货项
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("shippingItemDaoImpl")
public class ShippingItemDaoImpl extends BaseDaoImpl<ShippingItem, Long> implements ShippingItemDao {

}
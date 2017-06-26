/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import org.springframework.stereotype.Service;

import net.shopxx.entity.OrderItem;
import net.shopxx.service.OrderItemService;

/**
 * Service - 订单项
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("orderItemServiceImpl")
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem, Long> implements OrderItemService {

}
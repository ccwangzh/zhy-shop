/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.OrderPaymentDao;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.OrderPayment;
import net.shopxx.entity.Sn;
import net.shopxx.service.OrderPaymentService;

/**
 * Service - 订单支付
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("orderPaymentServiceImpl")
public class OrderPaymentServiceImpl extends BaseServiceImpl<OrderPayment, Long> implements OrderPaymentService {

	@Resource(name = "orderPaymentDaoImpl")
	private OrderPaymentDao orderPaymentDao;
	@Resource(name = "snDaoImpl")
	private SnDao snDao;

	@Transactional(readOnly = true)
	public OrderPayment findBySn(String sn) {
		return orderPaymentDao.findBySn(sn);
	}

	@Override
	@Transactional
	public OrderPayment save(OrderPayment orderPayment) {
		Assert.notNull(orderPayment);

		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));

		return super.save(orderPayment);
	}

}
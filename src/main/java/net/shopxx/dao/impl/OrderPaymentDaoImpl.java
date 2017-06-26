/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.shopxx.dao.OrderPaymentDao;
import net.shopxx.entity.OrderPayment;

/**
 * Dao - 订单支付
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("orderPaymentDaoImpl")
public class OrderPaymentDaoImpl extends BaseDaoImpl<OrderPayment, Long> implements OrderPaymentDao {

	public OrderPayment findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String jpql = "select orderPayment from OrderPayment orderPayment where lower(orderPayment.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, OrderPayment.class).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
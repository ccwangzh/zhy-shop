/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import freemarker.template.TemplateException;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.Sn;
import net.shopxx.util.FreeMarkerUtils;

/**
 * Dao - 序列号
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("snDaoImpl")
public class SnDaoImpl implements SnDao, InitializingBean {

	/** 货品编号生成器 */
	private HiloOptimizer goodsHiloOptimizer;

	/** 订单编号生成器 */
	private HiloOptimizer orderHiloOptimizer;

	/** 支付事务编号生成器 */
	private HiloOptimizer paymentTransactionHiloOptimizer;

	/** 订单支付编号生成器 */
	private HiloOptimizer orderPaymentHiloOptimizer;

	/** 退款单编号生成器 */
	private HiloOptimizer refundsHiloOptimizer;

	/** 发货单编号生成器 */
	private HiloOptimizer shippingHiloOptimizer;

	/** 退货单编号生成器 */
	private HiloOptimizer returnsHiloOptimizer;

	/** 会员编号生成器 */
	private HiloOptimizer memberHiloOptimizer;

	/** 店铺编号生成器 */
	private HiloOptimizer shopHiloOptimizer;

	/** 平台服务编号生成器 */
	private HiloOptimizer platformServiceHiloOptimizer;

	@PersistenceContext
	private EntityManager entityManager;
	@Value("${sn.goods.prefix}")
	private String goodsPrefix;
	@Value("${sn.goods.maxLo}")
	private int goodsMaxLo;
	@Value("${sn.order.prefix}")
	private String orderPrefix;
	@Value("${sn.order.maxLo}")
	private int orderMaxLo;
	@Value("${sn.paymentTransaction.prefix}")
	private String paymentTransactionPrefix;
	@Value("${sn.paymentTransaction.maxLo}")
	private int paymentTransactionMaxLo;
	@Value("${sn.orderPayment.prefix}")
	private String orderPaymentPrefix;
	@Value("${sn.orderPayment.maxLo}")
	private int orderPaymentMaxLo;
	@Value("${sn.refunds.prefix}")
	private String refundsPrefix;
	@Value("${sn.refunds.maxLo}")
	private int refundsMaxLo;
	@Value("${sn.shipping.prefix}")
	private String shippingPrefix;
	@Value("${sn.shipping.maxLo}")
	private int shippingMaxLo;
	@Value("${sn.returns.prefix}")
	private String returnsPrefix;
	@Value("${sn.returns.maxLo}")
	private int returnsMaxLo;
	@Value("${sn.member.prefix}")
	private String memberPrefix;
	@Value("${sn.member.maxLo}")
	private int memberMaxLo;
	@Value("${sn.shop.prefix}")
	private String shopPrefix;
	@Value("${sn.shop.maxLo}")
	private int shopMaxLo;
	@Value("${sn.platformService.prefix}")
	private String platformServicePrefix;
	@Value("${sn.platformService.maxLo}")
	private int platformServiceMaxLo;

	/**
	 * 初始化
	 */
	public void afterPropertiesSet() throws Exception {
		goodsHiloOptimizer = new HiloOptimizer(Sn.Type.goods, goodsPrefix, goodsMaxLo);
		orderHiloOptimizer = new HiloOptimizer(Sn.Type.order, orderPrefix, orderMaxLo);
		paymentTransactionHiloOptimizer = new HiloOptimizer(Sn.Type.paymentTransaction, paymentTransactionPrefix, paymentTransactionMaxLo);
		orderPaymentHiloOptimizer = new HiloOptimizer(Sn.Type.orderPayment, orderPaymentPrefix, orderPaymentMaxLo);
		refundsHiloOptimizer = new HiloOptimizer(Sn.Type.refunds, refundsPrefix, refundsMaxLo);
		shippingHiloOptimizer = new HiloOptimizer(Sn.Type.shipping, shippingPrefix, shippingMaxLo);
		returnsHiloOptimizer = new HiloOptimizer(Sn.Type.returns, returnsPrefix, returnsMaxLo);
		memberHiloOptimizer = new HiloOptimizer(Sn.Type.member, memberPrefix, memberMaxLo);
		shopHiloOptimizer = new HiloOptimizer(Sn.Type.store, shopPrefix, shopMaxLo);
		platformServiceHiloOptimizer = new HiloOptimizer(Sn.Type.platformService, platformServicePrefix, platformServiceMaxLo);
	}

	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	public String generate(Sn.Type type) {
		Assert.notNull(type);

		switch (type) {
		case goods:
			return goodsHiloOptimizer.generate();
		case order:
			return orderHiloOptimizer.generate();
		case paymentTransaction:
			return paymentTransactionHiloOptimizer.generate();
		case orderPayment:
			return orderPaymentHiloOptimizer.generate();
		case refunds:
			return refundsHiloOptimizer.generate();
		case shipping:
			return shippingHiloOptimizer.generate();
		case returns:
			return returnsHiloOptimizer.generate();
		case member:
			return memberHiloOptimizer.generate();
		case store:
			return shopHiloOptimizer.generate();
		case platformService:
			return platformServiceHiloOptimizer.generate();
		}
		return null;
	}

	/**
	 * 获取末值
	 * 
	 * @param type
	 *            类型
	 * @return 末值
	 */
	private long getLastValue(Sn.Type type) {
		String jpql = "select sn from Sn sn where sn.type = :type";
		Sn sn = entityManager.createQuery(jpql, Sn.class).setLockMode(LockModeType.PESSIMISTIC_WRITE).setParameter("type", type).getSingleResult();
		long lastValue = sn.getLastValue();
		sn.setLastValue(lastValue + 1);
		return lastValue;
	}

	/**
	 * 高低位算法生成器
	 */
	private class HiloOptimizer {

		/** 类型 */
		private Sn.Type type;

		/** 前缀 */
		private String prefix;

		/** 最大低位值 */
		private int maxLo;

		/** 低位值 */
		private int lo;

		/** 高位值 */
		private long hi;

		/** 末值 */
		private long lastValue;

		/**
		 * 构造方法
		 * 
		 * @param type
		 *            类型
		 * @param prefix
		 *            前缀
		 * @param maxLo
		 *            最大低位值
		 */
		public HiloOptimizer(Sn.Type type, String prefix, int maxLo) {
			this.type = type;
			this.prefix = prefix != null ? prefix.replace("{", "${") : "";
			this.maxLo = maxLo;
			this.lo = maxLo + 1;
		}

		/**
		 * 生成序列号
		 * 
		 * @return 序列号
		 */
		public synchronized String generate() {
			if (lo > maxLo) {
				lastValue = getLastValue(type);
				lo = lastValue == 0 ? 1 : 0;
				hi = lastValue * (maxLo + 1);
			}
			try {
				return FreeMarkerUtils.process(prefix) + (hi + lo++);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (TemplateException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

}
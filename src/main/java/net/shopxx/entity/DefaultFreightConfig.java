/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity - 默认运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_default_freight_config")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_default_freight_config")
public class DefaultFreightConfig extends BaseEntity<Long> {

	private static final long serialVersionUID = 8497620734330641125L;

	/** 首重量 */
	private Integer firstWeight;

	/** 续重量 */
	private Integer continueWeight;

	/** 默认首重价格 */
	private BigDecimal defaultFirstPrice;

	/** 默认续重价格 */
	private BigDecimal defaultContinuePrice;

	/** 配送方式 */
	private ShippingMethod shippingMethod;

	/** 店铺 */
	private Store store;

	/**
	 * 获取首重量
	 * 
	 * @return 首重量
	 */
	@NotNull
	@Min(0)
	@Column(nullable = false)
	public Integer getFirstWeight() {
		return firstWeight;
	}

	/**
	 * 设置首重量
	 * 
	 * @param firstWeight
	 *            首重量
	 */
	public void setFirstWeight(Integer firstWeight) {
		this.firstWeight = firstWeight;
	}

	/**
	 * 获取续重量
	 * 
	 * @return 续重量
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	public Integer getContinueWeight() {
		return continueWeight;
	}

	/**
	 * 设置续重量
	 * 
	 * @param continueWeight
	 *            续重量
	 */
	public void setContinueWeight(Integer continueWeight) {
		this.continueWeight = continueWeight;
	}

	/**
	 * 获取默认首重价格
	 * 
	 * @return 默认首重价格
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getDefaultFirstPrice() {
		return defaultFirstPrice;
	}

	/**
	 * 设置默认首重价格
	 * 
	 * @param defaultFirstPrice
	 *            默认首重价格
	 */
	public void setDefaultFirstPrice(BigDecimal defaultFirstPrice) {
		this.defaultFirstPrice = defaultFirstPrice;
	}

	/**
	 * 获取默认续重价格
	 * 
	 * @return 默认续重价格
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getDefaultContinuePrice() {
		return defaultContinuePrice;
	}

	/**
	 * 设置默认续重价格
	 * 
	 * @param defaultContinuePrice
	 *            默认续重价格
	 */
	public void setDefaultContinuePrice(BigDecimal defaultContinuePrice) {
		this.defaultContinuePrice = defaultContinuePrice;
	}

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Store getStore() {
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}
}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 店铺分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_store_category")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_store_category")
public class StoreCategory extends OrderEntity<Long> {

	private static final long serialVersionUID = -7932873038066360102L;

	/** 名称 */
	private String name;

	/** 保证金 */
	private BigDecimal bail;

	/** 店铺 */
	private Set<Store> stores = new HashSet<Store>();

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取保证金
	 * 
	 * @return 保证金
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getBail() {
		return bail;
	}

	/**
	 * 设置保证金
	 * 
	 * @param bail
	 *            保证金
	 */
	public void setBail(BigDecimal bail) {
		this.bail = bail;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	@OneToMany(mappedBy = "storeCategory", fetch = FetchType.LAZY)
	public Set<Store> getStores() {
		return stores;
	}

	/**
	 * 设置店铺
	 * 
	 * @param stores
	 *            店铺
	 */
	public void setStores(Set<Store> stores) {
		this.stores = stores;
	}

}
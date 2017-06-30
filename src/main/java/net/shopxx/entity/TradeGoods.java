/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import net.shopxx.BigDecimalNumericFieldBridge;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * Entity - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Indexed
@Entity
@Table(name = "xx_trade_goods")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_trade_goods")
public class TradeGoods extends BaseEntity<Long> {

	private static final long serialVersionUID = -6977025562650992419L;

	/** 名称 */
	private String name;

	/** 代码 */
	private String code;

	/** 最新价 */
	private BigDecimal price;

	/** 昨收价 */
	private BigDecimal prePrice;

	/** 展示图片 */
	private String image;

	/** 开盘价 */
	private BigDecimal openPrice;

	/** 最高价 */
	private BigDecimal highestPrice;

	/** 最低价 */
	private BigDecimal lowestPrice;

	/** 成交量 */
	private BigDecimal volume;

	/** 总额 */
	private BigDecimal totalAmount;

	/** 涨幅 */
	private BigDecimal up;

	/** 涨跌 */
	private BigDecimal upDown;

	/** 振幅 */
	private BigDecimal amplitude;

	/** 排序*/
	private Integer order;

	/** 是否启用*/
	private Boolean isEnable;

	/**
	 * 获取名称
	 *
	 * @return 名称
	 */
	@Field(store = Store.YES, index = Index.YES, analyze = Analyze.YES)
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
	 * 获取展示图片
	 *
	 * @return 展示图片
	 */
	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@Length(max = 200)
	@Pattern(regexp = "^(?i)(http:\\/\\/|https:\\/\\/|\\/).*$")
	public String getImage() {
		return image;
	}

	/**
	 * 设置展示图片
	 *
	 * @param image
	 *            展示图片
	 */
	public void setImage(String image) {
		this.image = image;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@Length(max = 200)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getPrePrice() {
		return prePrice;
	}

	public void setPrePrice(BigDecimal prePrice) {
		this.prePrice = prePrice;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getUp() {
		return up;
	}

	public void setUp(BigDecimal up) {
		this.up = up;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getUpDown() {
		return upDown;
	}

	public void setUpDown(BigDecimal upDown) {
		this.upDown = upDown;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@NumericField
	@FieldBridge(impl = BigDecimalNumericFieldBridge.class)
	@Column(precision = 21, scale = 6)
	public BigDecimal getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(BigDecimal amplitude) {
		this.amplitude = amplitude;
	}

	@Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
	@Column(nullable = false)
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}


    @Field(store = Store.YES, index = Index.NO, analyze = Analyze.NO)
    @NumericField
    @Min(0)
    @Column(name = "orders",nullable = false)
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
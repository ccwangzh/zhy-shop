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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Entity - 提现
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_cash")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_cash")
public class Cash extends BaseEntity<Long> {

	private static final long serialVersionUID = -1129619429301847081L;

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待审核 */
		pending,

		/** 审核通过 */
		approved,

		/** 审核失败 */
		failed
	}

	/** 状态 */
	private Cash.Status status;

	/** 金额 */
	private BigDecimal amount;

	/** 收款银行 */
	private String bank;

	/** 收款账号 */
	private String account;

	/** 操作员 */
	private Operator operator;

	/** 商家 */
	private Business business;

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	@NotNull(groups = Save.class)
	@Column(nullable = false)
	public Cash.Status getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(Cash.Status status) {
		this.status = status;
	}

	/**
	 * 获取金额
	 * 
	 * @return 金额
	 */
	@NotNull
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * 设置金额
	 * 
	 * @param amount
	 *            金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * 获取收款银行
	 * 
	 * @return 收款银行
	 */
	@NotNull
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getBank() {
		return bank;
	}

	/**
	 * 设置收款银行
	 * 
	 * @param bank
	 *            收款银行
	 */
	public void setBank(String bank) {
		this.bank = bank;
	}

	/**
	 * 获取收款账号
	 * 
	 * @return 收款账号
	 */
	@NotNull
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getAccount() {
		return account;
	}

	/**
	 * 设置收款账号
	 * 
	 * @param account
	 *            收款账号
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 获取操作员
	 * 
	 * @return 操作员
	 */
	@NotNull(groups = Save.class)
	@Column(nullable = false, updatable = false)
	public Operator getOperator() {
		return operator;
	}

	/**
	 * 设置操作员
	 * 
	 * @param operator
	 *            操作员
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	/**
	 * 获取商家
	 * 
	 * @return 商家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Business getBusiness() {
		return business;
	}

	/**
	 * 设置商家
	 * 
	 * @param business
	 *            商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}

}
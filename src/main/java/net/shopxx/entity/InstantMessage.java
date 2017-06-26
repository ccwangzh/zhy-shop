/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 即时通讯
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_instant_message")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_instant_message")
public class InstantMessage extends OrderEntity<Long> {

	private static final long serialVersionUID = 163292786603104144L;

	/**
	 * 类型
	 */
	public enum Type {

		/** QQ */
		qq,
		/** 阿里旺旺 */
		aliTalk
	}

	/** 名称 */
	private String name;

	/** 类型 */
	private InstantMessage.Type type;

	/** 账号 */
	private String account;

	/** 工作时间 */
	private String workingHours;

	/** 店铺 */
	private Store store;

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
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@NotNull
	@Column(nullable = false)
	public InstantMessage.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(InstantMessage.Type type) {
		this.type = type;
	}

	/**
	 * 获取账号
	 * 
	 * @return 账号
	 */
	@NotNull
	@Length(max = 200)
	@Column(nullable = false)
	public String getAccount() {
		return account;
	}

	/**
	 * 设置账号
	 * 
	 * @param account
	 *            账号
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 获取工作时间
	 * 
	 * @return 工作时间
	 */
	@Lob
	public String getWorkingHours() {
		return workingHours;
	}

	/**
	 * 设置工作时间
	 * 
	 * @param workingHours
	 *            工作时间
	 */
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	@ManyToOne(fetch = FetchType.LAZY)
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
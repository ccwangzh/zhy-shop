/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entity - 操作员
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Embeddable
public class Operator implements Serializable {

	private static final long serialVersionUID = -6435696972312457667L;

	/**
	 * 构造方法
	 */
	public Operator() {
	}

	/**
	 * 构造方法
	 * 
	 * @param admin
	 *            传入对象
	 */
	public Operator(Admin admin) {
		if (admin != null) {
			this.type = Type.admin;
			this.username = admin.getUsername();
		}
	}

	/**
	 * 构造方法
	 * 
	 * @param business
	 *            传入对象
	 */
	public Operator(Business business) {
		if (business != null) {
			this.type = Type.business;
			this.username = business.getMember().getUsername();
		}
	}

	/**
	 * 构造方法
	 * 
	 * @param member
	 *            传入对象
	 */
	public Operator(Member member) {
		if (member != null) {
			this.type = Type.member;
			this.username = member.getUsername();
		}
	}

	/**
	 * 类型
	 */
	public enum Type {

		/** 管理员 */
		admin,

		/** 商家 */
		business,

		/** 会员 */
		member
	}

	/** 类型 */
	private Type type;

	/** 用户名 */
	private String username;

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@Column(name = "operator_type", updatable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取用户名
	 * 
	 * @return 用户名
	 */
	@Column(name = "operator_username", updatable = false)
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 * 
	 * @param username
	 *            用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.event;

import org.springframework.context.ApplicationEvent;

import net.shopxx.entity.Member;

/**
 * Event - 会员
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public abstract class MemberEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7432438231982667326L;

	/** 会员 */
	private Member member;

	/**
	 * 构造方法
	 * 
	 * @param source
	 *            事件源
	 * @param member
	 *            会员
	 */
	public MemberEvent(Object source, Member member) {
		super(source);
		this.member = member;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return member;
	}

}
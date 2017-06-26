/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.event;

import net.shopxx.entity.Member;

/**
 * Event - 会员注销
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class MemberLoggedOutEvent extends MemberEvent {

	private static final long serialVersionUID = 8560275705072178478L;

	/**
	 * 构造方法
	 * 
	 * @param source
	 *            事件源
	 * @param member
	 *            会员
	 */
	public MemberLoggedOutEvent(Object source, Member member) {
		super(source, member);
	}

}
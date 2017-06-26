/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.event;

import net.shopxx.entity.Member;

/**
 * Event - 会员注册
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class MemberRegisteredEvent extends MemberEvent {

	private static final long serialVersionUID = 3930447081075693577L;

	/**
	 * 构造方法
	 * 
	 * @param source
	 *            事件源
	 * @param member
	 *            会员
	 */
	public MemberRegisteredEvent(Object source, Member member) {
		super(source, member);
	}

}
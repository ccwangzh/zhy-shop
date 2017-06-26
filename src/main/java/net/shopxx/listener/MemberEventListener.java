/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.listener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.shopxx.Principal;
import net.shopxx.Setting;
import net.shopxx.entity.Cart;
import net.shopxx.entity.Member;
import net.shopxx.entity.Operator;
import net.shopxx.entity.PointLog;
import net.shopxx.event.MemberLoggedInEvent;
import net.shopxx.event.MemberLoggedOutEvent;
import net.shopxx.event.MemberRegisteredEvent;
import net.shopxx.service.CartService;
import net.shopxx.service.MemberService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtils;

/**
 * Listener - 用户事件
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Component("memberEventListener")
public class MemberEventListener {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	/**
	 * 事件处理
	 * 
	 * @param memberRegisteredEvent
	 *            用户注册事件
	 */
	@EventListener
	public void handle(MemberRegisteredEvent memberRegisteredEvent) {
		Member member = memberRegisteredEvent.getMember();
		if (!(member instanceof Member)) {
			return;
		}
		HttpServletRequest request = WebUtils.getRequest();
		HttpServletResponse response = WebUtils.getResponse();

		sessionFixationProtection(request);

		Setting setting = SystemUtils.getSetting();
		if (setting.getRegisterPoint() > 0) {
			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, new Operator(), null);
		}
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute(Member.PRINCIPAL_ATTRIBUTE_NAME, new Principal(member.getId(), member.getUsername()));
		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		if (StringUtils.isNotEmpty(member.getNickname())) {
			WebUtils.addCookie(request, response, Member.NICKNAME_COOKIE_NAME, member.getNickname());
		}
	}

	/**
	 * 事件处理
	 * 
	 * @param memberLoggedInEvent
	 *            用户登录事件
	 */
	@EventListener
	public void handle(MemberLoggedInEvent memberLoggedInEvent) {
		Member member = memberLoggedInEvent.getMember();
		if (!(member instanceof Member)) {
			return;
		}

		HttpServletRequest request = WebUtils.getRequest();
		HttpServletResponse response = WebUtils.getResponse();

		sessionFixationProtection(request);

		Cart cart = member.getCart();
		cartService.merge(cart != null ? cart : cartService.create());

		WebUtils.addCookie(request, response, Member.USERNAME_COOKIE_NAME, member.getUsername());
		if (StringUtils.isNotEmpty(member.getNickname())) {
			WebUtils.addCookie(request, response, Member.NICKNAME_COOKIE_NAME, member.getNickname());
		}
	}

	/**
	 * 事件处理
	 * 
	 * @param memberLoggedOutEvent
	 *            用户注销事件
	 */
	@EventListener
	public void handle(MemberLoggedOutEvent memberLoggedOutEvent) {
		Member member = memberLoggedOutEvent.getMember();
		if (!(member instanceof Member)) {
			return;
		}

		HttpServletRequest request = WebUtils.getRequest();
		HttpServletResponse response = WebUtils.getResponse();

		WebUtils.removeCookie(request, response, Member.USERNAME_COOKIE_NAME);
		WebUtils.removeCookie(request, response, Member.NICKNAME_COOKIE_NAME);
	}

	/**
	 * Session固定攻击防护
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	private void sessionFixationProtection(HttpServletRequest request) {
		Assert.notNull(request);

		HttpSession session = request.getSession();
		Map<String, Object> attributes = new HashMap<String, Object>();
		Enumeration<?> keys = session.getAttributeNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			attributes.put(key, session.getAttribute(key));
		}
		session.invalidate();
		session = request.getSession();
		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			session.setAttribute(entry.getKey(), entry.getValue());
		}
	}

}
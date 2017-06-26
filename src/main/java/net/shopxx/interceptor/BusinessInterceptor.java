/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.interceptor;

import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.shopxx.entity.Store;
import net.shopxx.service.BusinessService;
import net.shopxx.service.StoreService;

/**
 * Interceptor - 商家权限
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class BusinessInterceptor extends HandlerInterceptorAdapter {

	/** 重定向视图名称前缀 */
	private static final String REDIRECT_VIEW_NAME_PREFIX = "redirect:";

	/** "重定向URL"参数名称 */
	private static final String REDIRECT_URL_PARAMETER_NAME = "redirectUrl";

	/** "商家"属性名称 */
	private static final String BUSINESS_ATTRIBUTE_NAME = "business";

	/** 默认拒绝访问URL */
	private static final String DEFAULT_ACCESS_DENIED_URL = "/common/access_denied.jhtml";

	/** 拒绝访问URL */
	private String accessDeniedUrl = DEFAULT_ACCESS_DENIED_URL;

	/** 默认店铺续费URL */
	private static final String DEFAULT_RENEWAL_ADD_URL = "/business/renewal/renewal.jhtml";

	/** 店铺续费URL */
	private String renewalAddUrl = DEFAULT_RENEWAL_ADD_URL;

	/** AntPathMatcher */
	private static AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Value("${url_escaping_charset}")
	private String urlEscapingCharset;

	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 请求前处理
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            处理器
	 * @return 是否继续执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Store store = storeService.getCurrent();
		String path = request.getServletPath();
		if (store != null && BooleanUtils.isTrue(store.getHasExpired()) && antPathMatcher.match(renewalAddUrl, path)) {
			return true;
		}
		if (store == null || !store.isActive()) {
			String requestType = request.getHeader("X-Requested-With");
			if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
				response.addHeader("businessStatus", "accessDenied");
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return false;
			} else {
				if (request.getMethod().equalsIgnoreCase("GET")) {
					String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
					response.sendRedirect(request.getContextPath() + accessDeniedUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
				} else {
					response.sendRedirect(request.getContextPath() + accessDeniedUrl);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 请求后处理
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            处理器
	 * @param modelAndView
	 *            数据视图
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			String viewName = modelAndView.getViewName();
			if (!StringUtils.startsWith(viewName, REDIRECT_VIEW_NAME_PREFIX)) {
				modelAndView.addObject(BUSINESS_ATTRIBUTE_NAME, businessService.getCurrent());
			}
		}
	}

}
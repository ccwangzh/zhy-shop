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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.shopxx.controller.shop.business.DiscountController;
import net.shopxx.controller.shop.business.FullReductionController;
import net.shopxx.entity.Store;
import net.shopxx.plugin.PromotionPlugin;
import net.shopxx.service.PluginService;
import net.shopxx.service.StoreService;

/**
 * PluginInterceptor - 插件权限
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class PluginInterceptor extends HandlerInterceptorAdapter {

	/** 促销折扣ID */
	private static final String DISCOUNT = "discount";

	/** 促销满减ID */
	private static final String FULL_REDUCTION = "fullReduction";

	/** "重定向URL"参数名称 */
	private static final String REDIRECT_URL_PARAMETER_NAME = "redirectUrl";

	/** 默认折扣插件购买URL */
	private static final String DEFAULT_DISCOUNT_PLUGIN_BUY_URL = "/business/discount/buy.jhtml";

	/** 折扣插件购买URL */
	private String discountPluginBuyUrl = DEFAULT_DISCOUNT_PLUGIN_BUY_URL;

	/** 默认满减插件购买URL */
	private static final String DEFAULT_FULL_REDUCTION_PLUGIN_BUY_URL = "/business/full_reduction/buy.jhtml";

	/** 满减插件购买URL */
	private String fullReductionPluginBuyUrl = DEFAULT_FULL_REDUCTION_PLUGIN_BUY_URL;

	/** 默认错误页面 */
	private static final String DEFAULT_ERROR_VIEW = "/common/resource_not_found.jhtml";

	/** 错误页面 */
	private String errorView = DEFAULT_ERROR_VIEW;

	@Value("${url_escaping_charset}")
	private String urlEscapingCharset;

	@Resource(name = "pluginServiceImpl")
	private PluginService pluginService;
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
		if (store == null) {
			return false;
		}

		HandlerMethod method = (HandlerMethod) handler;
		Object controller = method.getBean();
		if (controller instanceof DiscountController) {
			PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DISCOUNT);
			if (promotionPlugin.getIsInstalled() && promotionPlugin.getIsEnabled()) {
				if (store.getType().equals(Store.Type.self) || store.getIsDiscount()) {
					return true;
				} else {
					if (request.getMethod().equalsIgnoreCase("GET")) {
						String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
						response.sendRedirect(request.getContextPath() + discountPluginBuyUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
					} else {
						response.sendRedirect(request.getContextPath() + discountPluginBuyUrl);
					}
					return false;
				}
			} else {
				response.sendRedirect(request.getContextPath() + errorView);
			}
		} else if (controller instanceof FullReductionController) {
			PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(FULL_REDUCTION);
			if (promotionPlugin.getIsInstalled() && promotionPlugin.getIsEnabled()) {
				if (store.getType().equals(Store.Type.self) || store.getIsFullReduction()) {
					return true;
				} else {
					if (request.getMethod().equalsIgnoreCase("GET")) {
						String redirectUrl = request.getQueryString() != null ? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI();
						response.sendRedirect(request.getContextPath() + fullReductionPluginBuyUrl + "?" + REDIRECT_URL_PARAMETER_NAME + "=" + URLEncoder.encode(redirectUrl, urlEscapingCharset));
					} else {
						response.sendRedirect(request.getContextPath() + fullReductionPluginBuyUrl);
					}
					return false;
				}
			} else {
				response.sendRedirect(request.getContextPath() + errorView);
			}
		}
		return false;
	}

	/**
	 * 获取错误试图
	 * 
	 * @return 错误试图
	 */
	public String getErrorView() {
		return errorView;
	}

	/**
	 * 设置错误试图L
	 * 
	 * @param errorView
	 *            错误试图
	 */
	public void setErrorView(String errorView) {
		this.errorView = errorView;
	}

}
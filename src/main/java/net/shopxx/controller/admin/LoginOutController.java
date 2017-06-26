/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.shopxx.Message;
import net.shopxx.Setting;
import net.shopxx.entity.Admin;
import net.shopxx.service.AdminService;
import net.shopxx.service.RSAService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtils;

/**
 * Controller - 管理员登录
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminLoginOutController")
@RequestMapping("/admin/loginOut")
public class LoginOutController extends BaseController {

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	/**
	 * 登录
	 */
	@RequestMapping
	public @ResponseBody String index(HttpServletRequest request) {
	    Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Enumeration em = request.getSession().getAttributeNames();
            while (em.hasMoreElements()) {
                request.getSession().removeAttribute(em.nextElement().toString());
            }
            HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
            Cookie cookie = new Cookie(Admin.LOGIN_TOKEN_COOKIE_NAME, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "/admin/login";
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import net.shopxx.entity.Business;

/**
 * Service - 商家
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface BusinessService extends BaseService<Business, Long> {

	/**
	 * 获取当前登录商家
	 * 
	 * @return 当前登录商家，若不存在则返回null
	 */
	Business getCurrent();
}
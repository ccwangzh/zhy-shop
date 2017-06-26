/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.BusinessDao;
import net.shopxx.entity.Business;

/**
 * Dao - 商家
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("businessDaoImpl")
public class BusinessDaoImpl extends BaseDaoImpl<Business, Long> implements BusinessDao {

}
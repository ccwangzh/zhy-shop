/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.dao.StoreCategoryDao;
import net.shopxx.entity.StoreCategory;
import net.shopxx.service.StoreCategoryService;

/**
 * Service - 店铺分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("storeCategoryServiceImpl")
public class StoreCategoryServiceImpl extends BaseServiceImpl<StoreCategory, Long> implements StoreCategoryService {

	@Resource(name = "storeCategoryDaoImpl")
	private StoreCategoryDao storeCategoryDao;

	@Transactional(readOnly = true)
	public boolean nameExists(String name) {
		return storeCategoryDao.nameExists(name);
	}

}
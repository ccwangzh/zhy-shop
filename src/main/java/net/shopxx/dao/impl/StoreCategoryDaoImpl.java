/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.shopxx.dao.StoreCategoryDao;
import net.shopxx.entity.StoreCategory;

/**
 * Dao - 店铺分类
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("storeCategoryDaoImpl")
public class StoreCategoryDaoImpl extends BaseDaoImpl<StoreCategory, Long> implements StoreCategoryDao {

	public boolean nameExists(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		String jpql = "select count(*) from StoreCategory storeCategory where lower(storeCategory.name) = lower(:name)";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("name", name).getSingleResult();
		return count > 0;
	}

}
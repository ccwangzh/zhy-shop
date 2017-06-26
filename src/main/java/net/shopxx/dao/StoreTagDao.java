/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreTag;

/**
 * Dao - 店铺标签
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface StoreTagDao extends BaseDao<StoreTag, Long> {

	/**
	 * 查找店铺标签
	 * 
	 * @param store
	 *            店铺
	 * @param isShow
	 *            是否显示
	 * @return 店铺标签
	 */
	List<StoreTag> findList(Store store, Boolean isShow);

	/**
	 * 查找店铺标签分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 店铺标签分页
	 */
	Page<StoreTag> findPage(Store store, Pageable pageable);

}
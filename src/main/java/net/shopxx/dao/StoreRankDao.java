/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.entity.StoreRank;

/**
 * Dao - 店铺等级
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface StoreRankDao extends BaseDao<StoreRank, Long> {

	/**
	 * 判断名称是否存在
	 * 
	 * @param type
	 *            类型
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	boolean nameExists(StoreRank.Type type, String name);

	/**
	 * 查找默认店铺等级
	 * 
	 * @return 默认店铺等级，若不存在则返回null
	 */
	StoreRank findDefault(StoreRank.Type type);

	/**
	 * 查找店铺等级
	 * 
	 * @param type
	 *            类型
	 * @return 店铺等级
	 */
	/**
	 * 查找店铺等级
	 * 
	 * @param type
	 *            类型
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 店铺等级
	 */
	List<StoreRank> findList(StoreRank.Type type, List<Filter> filters, List<Order> orders);

	/**
	 * 清除默认
	 * 
	 * @param type
	 *            类型
	 */
	void clearDefault(StoreRank.Type type);

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除店铺等级
	 * 
	 * @param type
	 *            类型
	 */
	void clearDefault(StoreRank exclude, StoreRank.Type type);

}
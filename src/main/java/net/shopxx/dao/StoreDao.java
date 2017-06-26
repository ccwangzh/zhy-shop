/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Member;
import net.shopxx.entity.Store;

/**
 * Dao - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface StoreDao extends BaseDao<Store, Long> {

	/**
	 * 根据编号查找店铺
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 店铺，若不存在则返回null
	 */
	Store findBySn(String sn);

	/**
	 * 判断店铺名是否存在
	 * 
	 * @param name
	 *            店铺名(忽略大小写)
	 * @return 店铺名是否存在
	 */
	boolean nameExists(String name);

	/**
	 * 判断手机号是否存在
	 * 
	 * @param mobile
	 *            手机号
	 * @return 手机号是否存在
	 */
	boolean mobileExists(String mobile);

	/**
	 * 判断电子邮箱是否存在
	 * 
	 * @param email
	 *            电子邮箱
	 * @return 电子邮箱是否存在
	 */
	boolean emailExists(String email);

	/**
	 * 根据店铺名称查找店铺
	 * 
	 * @param name
	 *            店铺名称
	 * @return 店铺
	 */
	Store findByName(String name);

	/**
	 * 查找店铺分页
	 * 
	 * @param type
	 *            店铺类型
	 * @param status
	 *            店铺状态
	 * @param isEnabled
	 *            是否启用
	 * @param pageable
	 *            分页信息
	 * @return 店铺分页
	 */
	Page<Store> findPage(Store.Type type, Store.Status status, Boolean isEnabled, Pageable pageable);

	/**
	 * 查找店铺分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 店铺分页
	 */
	Page<Store> findPage(Member member, Pageable pageable);

	/**
	 * 查找过期店铺
	 * 
	 * @return 店铺
	 */
	List<Store> findList(Integer first, Integer count);

	/**
	 * 查找店铺
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param isEnabled
	 *            是否启用
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 店铺
	 */
	List<Store> findList(Store.Type type, Store.Status status, Boolean isEnabled, Integer first, Integer count);

}
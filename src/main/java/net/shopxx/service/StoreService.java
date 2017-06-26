/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.math.BigDecimal;
import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.AdvertisingImage;
import net.shopxx.entity.Business;
import net.shopxx.entity.Member;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.entity.Store;

/**
 * Service - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface StoreService extends BaseService<Store, Long> {

	/**
	 * 广告图片过滤
	 * 
	 * @param advertisingImages
	 *            广告图片
	 */
	void filter(List<AdvertisingImage> advertisingImages);

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
	 * @param shopName
	 *            店铺名(忽略大小写)
	 * @return 店铺名是否存在
	 */
	boolean nameExists(String shopName);

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
	 * 注册
	 * 
	 * @param store
	 *            店铺
	 * @param business
	 *            商家
	 * @param automaticReview
	 *            是否需要自动审核
	 */
	void register(Store store, Business business, Boolean automaticReview);

	/**
	 * 更新
	 * 
	 * @param store
	 *            店铺
	 * @param business
	 *            商家
	 */
	void update(Store store, Business business);

	/**
	 * 获取当前登录商家店铺
	 * 
	 * @return 当前登录商家店铺，若不存在则返回null
	 */
	Store getCurrent();

	/**
	 * 审核
	 * 
	 * @param store
	 *            店铺
	 * @param passed
	 *            是否审核成功
	 * @param content
	 *            审核失败消息
	 */
	void review(Store store, Boolean passed, String content);

	/**
	 * 交纳保证金
	 * 
	 * @param store
	 *            店铺
	 * @param bail
	 *            保证金
	 */
	void bailPayment(Store store, BigDecimal bail);

	/**
	 * 过期店铺处理
	 */
	void expiredStoreProcessing();

	/**
	 * 刷新店铺
	 * 
	 * @param store
	 *            店铺
	 * @param paymentTransaction
	 *            支付事务
	 */
	void storeRefresh(Store store, PaymentTransaction paymentTransaction);

	/**
	 * 开通新店
	 * 
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param year
	 *            开通年数
	 * @param serviceFee
	 *            服务费
	 * @param bail
	 *            保证金
	 */
	void open(Store store);

	/**
	 * 开通新店
	 * 
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param year
	 *            开通年数
	 * @param serviceFee
	 *            服务费
	 * @param bail
	 *            保证金
	 */
	void open(Store store, Member member, Integer year, BigDecimal serviceFee, BigDecimal bail);

	/**
	 * 购买促销插件
	 * 
	 * @param store
	 *            店铺
	 * @param promotionPluginId
	 *            促销插件ID
	 * @param year
	 *            时长
	 * @param pluginPrice
	 *            促销价格
	 */
	void buyPromotionPlugin(Store store, String promotionPluginId, Integer year, BigDecimal pluginPrice);

}
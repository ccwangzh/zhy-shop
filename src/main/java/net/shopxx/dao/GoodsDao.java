/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.*;

/**
 * Dao - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface GoodsDao extends BaseDao<Goods, Long> {

	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	boolean snExists(String sn);

	/**
	 * 根据编号查找货品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 货品，若不存在则返回null
	 */
	Goods findBySn(String sn);

	/**
	 * 查找货品
	 * 
	 * @param store
	 *            店铺
	 * @param productCategorys
	 *            经营分类
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 货品
	 */
	List<Goods> findList(Store store, Set<ProductCategory> productCategorys, Integer first, Integer count);

	/**
	 * 查找货品
	 * 
	 * @param store
	 *            店铺
	 * @param storeTag
	 *            店铺标签
	 * @param type
	 *            类型
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tag
	 *            标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isActive
	 *            是否有效
	 * @param status
	 *            商品状态
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 货品
	 */
	List<Goods> findList(Store store, StoreTag storeTag, Goods.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isActive,Goods.Status status, Boolean isList,
			Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找货品
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param status
	 *            商品状态
	 * @param generateMethod
	 *            静态生成方式
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 货品
	 */
	List<Goods> findList(ProductCategory productCategory, Goods.Status status, Date beginDate, Date endDate, Integer first, Integer count);

	/**
	 * 查找货品分页
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param productCategory
	 *            商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param tag
	 *            标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isActive
	 *            是否有效
	 * @param status
	 *            商品状态
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 货品分页
	 */
	Page<Goods> findPage(Store store, Goods.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isActive, Goods.Status status, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Pageable pageable);

	/**
	 * 查找货品分页
	 * 
	 * @param store
	 *            店铺
	 * @param keyword
	 *            关键词
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param isActive
	 *            是否有效
	 * @param status
	 *            商品状态
	 * @param isList
	 *            是否列出
	 * @param orderType
	 *            排序类型
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格 分页信息
	 * @return 货品分页
	 */
	Page<Goods> findPage(Store store, String keyword, StoreProductCategory storeProductCategory, Boolean isActive, Goods.Status status, Boolean isList, Goods.OrderType orderType, BigDecimal startPrice, BigDecimal endPrice, Pageable pageable);

	/**
	 * 查找货品分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 货品分页
	 */
	Page<Goods> findPage(Goods.RankingType rankingType, Store store, Pageable pageable);

	/**
	 * 查找收藏货品分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收藏货品分页
	 */
	Page<Goods> findPage(Member member, Pageable pageable);

	/**
	 * 查询货品数量
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param favoriteMember
	 *            收藏会员
	 * @param isActive
	 *            是否有效
	 * @param status
	 *            商品状态
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @return 货品数量
	 */
	Long count(Goods.Type type, Store store, Member favoriteMember, Boolean isActive, Goods.Status status, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert);

	/**
	 * 清空货品属性值
	 * 
	 * @param attribute
	 *            属性
	 */
	void clearAttributeValue(Attribute attribute);

	/**
     * 增加审核意见
	 * @param entity
     */
    void addApprovalComment(GoodsComment entity);
    
    /**
     * 查询审核意见
     * @param goodsId
     */
    List<GoodsComment> getApprovalComments(Long goodsId);

	/**
	 * 查询商品通用协议
	 *
	 */
	List<GoodsCommonAgreement> getCommonAgreement();

	/**
	 * 保存商品通用协议
	 *
	 */
	void saveCommonAgreement(GoodsCommonAgreement agreement);
	
	/**
	 * 查询商品通用协议
	 *
	 */
	GoodsCommonAgreement getCommonAgreement(long id);
}
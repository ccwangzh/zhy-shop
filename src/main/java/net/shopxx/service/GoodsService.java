/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

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
 * Service - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface GoodsService extends BaseService<Goods, Long> {

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
	List<Goods> findList(Store store, StoreTag storeTag, Goods.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, Tag tag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isActive, Goods.Status status, Boolean isList,
			Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders);

	/**
	 * 查找货品
	 * 
	 * @param type
	 *            类型
	 * @param storeId
	 *            店铺ID
	 * @param storeTagId
	 *            店铺标签ID
	 * @param productCategoryId
	 *            商品分类ID
	 * @param brandId
	 *            品牌ID
	 * @param promotionId
	 *            促销ID
	 * @param tagId
	 *            标签ID
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
	 * @param useCache
	 *            是否使用缓存
	 * @return 货品
	 */
	List<Goods> findList(Goods.Type type, Long storeId, Long storeTagId, Long productCategoryId, Long brandId, Long promotionId, Long tagId, Map<Long, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isActive, Goods.Status status, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Goods.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, boolean useCache);

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
	 * @param shop
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
	 * 查看点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	long viewHits(Long id);

	/**
	 * 增加点击数
	 * 
	 * @param goods
	 *            货品
	 * @param amount
	 *            值
	 */
	void addHits(Goods goods, long amount);

	/**
	 * 增加销量
	 * 
	 * @param goods
	 *            货品
	 * @param amount
	 *            值
	 */
	void addSales(Goods goods, long amount);

	/**
	 * 增加审核意见
	 * @param comment
	 */
    void addApprovalComment(long goodsId, String comment);

	/**
	 * 查询审核意见
	 * @param goodsId
	 */
    List<GoodsComment> getApprovalComments(long goodsId);

	/**
	 * 查询商品通用协议
	 *
	 */
	GoodsCommonAgreement getCommonAgreement();

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

	/**
	 * 保存
	 * 
	 * @param goods
	 *            货品
	 * @param product
	 *            商品
	 * @param business
	 *            商户
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	Goods save(Goods goods, Product product, Business business, Operator operator);

	/**
	 * 保存
	 * 
	 * @param goods
	 *            货品
	 * @param products
	 *            商品
	 * @param business
	 *            商户
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	Goods save(Goods goods, List<Product> products, Business business, Operator operator);

	/**
	 * 更新
	 * 
	 * @param goods
	 *            货品
	 * @param product
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	Goods update(Goods goods, Product product, Operator operator);

	/**
	 * 更新
	 * 
	 * @param goods
	 *            货品
	 * @param products
	 *            商品
	 * @param operator
	 *            操作员
	 * @return 货品
	 */
	Goods update(Goods goods, List<Product> products, Operator operator);

	/**
	 * 设置货品有效性
	 * 
	 * @param store
	 *            店铺
	 * @param productCategorys
	 *            经营分类
	 */
	void setActive(Store store, Set<ProductCategory> productCategorys);

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import net.shopxx.BaseAttributeConverter;
import net.shopxx.util.FreeMarkerUtils;

/**
 * Entity - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Indexed
@Entity
@Table(name = "xx_store")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_store")
public class Store extends OrderEntity<Long> {

	private static final long serialVersionUID = -406440213727498768L;

	/**
	 * 普通店铺验证组
	 */
	public interface General extends Default {

	}

	/**
	 * 自营店铺验证组
	 */
	public interface Self extends Default {

	}

	/**
	 * 类型
	 */
	public enum Type {

		/** 普通 */
		general,

		/** 自营 */
		self
	}

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待审核 */
		pending,

		/** 审核失败 */
		failed,

		/** 审核通过 */
		approved,

		/** 开店成功 */
		success
	}

	/** 类型 */
	private Store.Type type;

	/** 状态 */
	private Store.Status status;

	/** 编号 */
	private String sn;

	/** 名称 */
	private String name;

	/** logo */
	private String logo;

	/** 地址 */
	private String address;

	/** 邮编 */
	private String zipCode;

	/** 电话 */
	private String phone;

	/** 手机 */
	private String mobile;

	/** E-mail */
	private String email;

	/** 简介 */
	private String introduction;

	/** 是否启用 */
	private Boolean isEnabled;

	/** 搜索关键词 */
	private String keyword;

	/** 页面标题 */
	private String seoTitle;

	/** 页面关键词 */
	private String seoKeywords;

	/** 页面描述 */
	private String seoDescription;

	/** 开店日期 */
	private Date beginDate;

	/** 到期日期 */
	private Date endDate;

	/** 保证金 */
	private BigDecimal bail;

	/** 已付保证金 */
	private BigDecimal bailPaid;

	/** 折扣 */
	private Date discount;

	/** 满减 */
	private Date fullReduction;

	/** 店铺等级 */
	private StoreRank storeRank;

	/** 商家 */
	private Business business;

	/** 店铺分类 */
	private StoreCategory storeCategory;

	/** 广告图片 */
	private List<AdvertisingImage> advertisingImages = new ArrayList<AdvertisingImage>();

	/** 店铺商品分类 */
	private Set<StoreProductCategory> storeProductCategorys = new HashSet<StoreProductCategory>();

	/** 经营分类 */
	private Set<ProductCategory> productCategorys = new HashSet<ProductCategory>();

	/** 经营分类申请 */
	private Set<CategoryApplication> categoryApplications = new HashSet<CategoryApplication>();

	/** 店铺标签 */
	private Set<StoreTag> storeTags = new HashSet<StoreTag>();

	/** 即时通讯 */
	private Set<InstantMessage> instantMessages = new HashSet<InstantMessage>();

	/** 优惠券 */
	private Set<Coupon> coupons = new HashSet<Coupon>();

	/** 促销 */
	private Set<Promotion> promotions = new HashSet<Promotion>();

	/** 订单 */
	private Set<Order> orders = new HashSet<Order>();

	/** 货品 */
	private Set<Goods> goods = new HashSet<Goods>();

	/** 收藏会员 */
	private Set<Member> favoriteMembers = new HashSet<Member>();

	/** 快递单模板 */
	private Set<DeliveryTemplate> deliveryTemplates = new HashSet<DeliveryTemplate>();

	/** 发货点 */
	private Set<DeliveryCenter> deliveryCenters = new HashSet<DeliveryCenter>();

	/** 默认运费配置 */
	private Set<DefaultFreightConfig> defaultFreightConfigs = new HashSet<DefaultFreightConfig>();

	/** 运费配置 */
	private Set<AreaFreightConfig> areaFreightConfigs = new HashSet<AreaFreightConfig>();

	/** 服务 */
	private Set<Svc> svcs = new HashSet<Svc>();

	/** 支付事务 */
	private Set<PaymentTransaction> paymentTransactions = new HashSet<PaymentTransaction>();

	/** 统计 */
	private Set<Statistic> statistics = new HashSet<Statistic>();

	/** 咨询 */
	private Set<Consultation> consultations = new HashSet<Consultation>();

	/** 评论 */
	private Set<Review> reviews = new HashSet<Review>();

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@NotNull
	@Column(nullable = false, updatable = false)
	public Store.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Store.Type type) {
		this.type = type;
	}

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	@Field(store = org.hibernate.search.annotations.Store.YES, index = Index.YES, analyze = Analyze.NO)
	@Column(nullable = false)
	public Store.Status getStatus() {
		return status;
	}

	/**
	 * 设置申请状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(Store.Status status) {
		this.status = status;
	}

	/**
	 * 获取编号
	 * 
	 * @return 店铺编号
	 */
	@Column(nullable = false, updatable = false, unique = true)
	public String getSn() {
		return sn;
	}

	/**
	 * 获取编号
	 * 
	 * @return 编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	@Field(store = org.hibernate.search.annotations.Store.YES, index = Index.YES, analyze = Analyze.NO)
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取logo
	 * 
	 * @return logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * 设置logo
	 * 
	 * @param logo
	 *            logo
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * 获取地址
	 * 
	 * @return 地址
	 */
	@Length(max = 200)
	public String getAddress() {
		return address;
	}

	/**
	 * 设置地址
	 * 
	 * @param address
	 *            地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取邮编
	 * 
	 * @return 邮编
	 */
	@Length(max = 200)
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 设置邮编
	 * 
	 * @param zipCode
	 *            邮编
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * 获取电话
	 * 
	 * @return 电话
	 */
	@Length(max = 200)
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置电话
	 * 
	 * @param phone
	 *            电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取手机号
	 * 
	 * @return 手机号
	 */
	@NotEmpty
	@Pattern(regexp = "^1[3|4|5|7|8]\\d{9}$")
	@Length(max = 200)
	public String getMobile() {
		return mobile;
	}

	/**
	 * 设置手机号
	 * 
	 * @param mobile
	 *            手机号
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取邮箱
	 * 
	 * @return 邮箱
	 */
	@NotEmpty
	@Email
	@Length(max = 200)
	public String getEmail() {
		return email;
	}

	/**
	 * 设置邮箱
	 * 
	 * @param email
	 *            邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取简介
	 * 
	 * @return 简介
	 */
	@Length(max = 200)
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * 设置简介
	 * 
	 * @param introduction
	 *            简介
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * 获取是否启用
	 * 
	 * @return 是否启用
	 */
	@Field(store = org.hibernate.search.annotations.Store.YES, index = Index.YES, analyze = Analyze.NO)
	@NotNull
	@Column(nullable = false)
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * 设置是否启用
	 * 
	 * @param isEnabled
	 *            是否启用
	 */
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * 获取搜索关键词
	 * 
	 * @return 搜索关键词
	 */
	@Length(max = 200)
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置搜索关键词
	 * 
	 * @param keyword
	 *            搜索关键词
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.keyword = keyword;
	}

	/**
	 * 获取页面标题
	 * 
	 * @return 页面标题
	 */
	@Length(max = 200)
	public String getSeoTitle() {
		return seoTitle;
	}

	/**
	 * 设置页面标题
	 * 
	 * @param seoTitle
	 *            页面标题
	 */
	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	/**
	 * 获取页面关键词
	 * 
	 * @return 页面关键词
	 */
	@Length(max = 200)
	public String getSeoKeywords() {
		return seoKeywords;
	}

	/**
	 * 设置页面关键词
	 * 
	 * @param seoKeywords
	 *            页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.seoKeywords = seoKeywords;
	}

	/**
	 * 获取页面描述
	 * 
	 * @return 页面描述
	 */
	@Length(max = 200)
	public String getSeoDescription() {
		return seoDescription;
	}

	/**
	 * 设置页面描述
	 * 
	 * @param seoDescription
	 *            页面描述
	 */
	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	/**
	 * 获取店铺开店日期
	 * 
	 * @return 店铺开店日期
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * 设置店铺开店日期
	 * 
	 * @param beginDate
	 *            店铺开店日期
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * 获取店铺到期日期
	 * 
	 * @return 店铺到期日期
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 设置店铺到期日期
	 * 
	 * @param endDate
	 *            店铺到期日期
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 获取保证金
	 * 
	 * @return 保证金
	 */
	@Column(nullable = false, precision = 27, scale = 12)
	public BigDecimal getBail() {
		return bail;
	}

	/**
	 * 设置保证金
	 * 
	 * @param bail
	 *            保证金
	 */
	public void setBail(BigDecimal bail) {
		this.bail = bail;
	}

	/**
	 * 获取已付保证金
	 * 
	 * @return 已付保证金
	 */
	@Column(nullable = false, precision = 27, scale = 12)
	public BigDecimal getBailPaid() {
		return bailPaid;
	}

	/**
	 * 设置已付保证金
	 * 
	 * @param bailPaid
	 *            已付保证金
	 */
	public void setBailPaid(BigDecimal bailPaid) {
		this.bailPaid = bailPaid;
	}

	/**
	 * 获取折扣
	 * 
	 * @return 折扣
	 */

	public Date getDiscount() {
		return discount;
	}

	/**
	 * 设置折扣
	 * 
	 * @param discount
	 *            折扣
	 */
	public void setDiscount(Date discount) {
		this.discount = discount;
	}

	/**
	 * 获取满减
	 * 
	 * @return 满减
	 */
	public Date getFullReduction() {
		return fullReduction;
	}

	/**
	 * 设置满减
	 * 
	 * @param fullReduction
	 *            满减
	 */
	public void setFullReduction(Date fullReduction) {
		this.fullReduction = fullReduction;
	}

	/**
	 * 获取店铺等级
	 * 
	 * @return 店铺等级
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public StoreRank getStoreRank() {
		return storeRank;
	}

	/**
	 * 设置店铺等级
	 * 
	 * @param storeRank
	 *            店铺等级
	 */
	public void setStoreRank(StoreRank storeRank) {
		this.storeRank = storeRank;
	}

	/**
	 * 获取商家
	 * 
	 * @return 商家
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(nullable = false)
	public Business getBusiness() {
		return business;
	}

	/**
	 * 设置商家
	 * 
	 * @param business
	 *            商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}

	/**
	 * 获取店铺分类
	 * 
	 * @return 店铺分类
	 */
	@NotNull(groups = General.class)
	@ManyToOne(fetch = FetchType.LAZY)
	public StoreCategory getStoreCategory() {
		return storeCategory;
	}

	/**
	 * 设置店铺分类
	 * 
	 * @param storeCategory
	 *            店铺分类
	 */
	public void setStoreCategory(StoreCategory storeCategory) {
		this.storeCategory = storeCategory;
	}

	/**
	 * 获取广告图片
	 * 
	 * @return 广告图片
	 */
	@Valid
	@Column(length = 4000)
	@Convert(converter = AdvertisingImageConverter.class)
	public List<AdvertisingImage> getAdvertisingImages() {
		return advertisingImages;
	}

	/**
	 * 设置广告图片
	 * 
	 * @param advertisingImages
	 *            广告图片
	 */
	public void setAdvertisingImages(List<AdvertisingImage> advertisingImages) {
		this.advertisingImages = advertisingImages;
	}

	/**
	 * 获取店商品铺分类
	 * 
	 * @return 店铺商品分类
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<StoreProductCategory> getStoreProductCategorys() {
		return storeProductCategorys;
	}

	/**
	 * 设置店铺商品分类
	 * 
	 * @param storeProductCategorys
	 *            店铺商品分类
	 */
	public void setStoreProductCategorys(Set<StoreProductCategory> storeProductCategorys) {
		this.storeProductCategorys = storeProductCategorys;
	}

	/**
	 * 获取经营分类
	 * 
	 * @return 经营分类
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_store_category_management")
	public Set<ProductCategory> getProductCategorys() {
		return productCategorys;
	}

	/**
	 * 设置经营分类
	 * 
	 * @param productCategorys
	 *            经营分类
	 */
	public void setProductCategorys(Set<ProductCategory> productCategorys) {
		this.productCategorys = productCategorys;
	}

	/**
	 * 获取经营分类申请
	 * 
	 * @return 经营分类申请
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<CategoryApplication> getCategoryApplications() {
		return categoryApplications;
	}

	/**
	 * 设置经营分类申请
	 * 
	 * @param categoryApplications
	 *            经营分类申请
	 */
	public void setCategoryApplications(Set<CategoryApplication> categoryApplications) {
		this.categoryApplications = categoryApplications;
	}

	/**
	 * 获取店铺标签
	 * 
	 * @return 店铺标签
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<StoreTag> getStoreTags() {
		return storeTags;
	}

	/**
	 * 设置店铺标签
	 * 
	 * @param storeTags
	 *            店铺标签
	 */
	public void setStoreTags(Set<StoreTag> storeTags) {
		this.storeTags = storeTags;
	}

	/**
	 * 获取即时通讯
	 * 
	 * @return 即时通讯
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<InstantMessage> getInstantMessages() {
		return instantMessages;
	}

	/**
	 * 设置即时通讯
	 * 
	 * @param instantMessages
	 *            即时通讯
	 */
	public void setInstantMessages(Set<InstantMessage> instantMessages) {
		this.instantMessages = instantMessages;
	}

	/**
	 * 获取优惠券
	 * 
	 * @return 优惠券
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * 设置优惠券
	 * 
	 * @param coupons
	 *            优惠券
	 */
	public void setCoupons(Set<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Promotion> getPromotions() {
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Order> getOrders() {
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取货品
	 * 
	 * @return 货品
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Goods> getGoods() {
		return goods;
	}

	/**
	 * 设置货品
	 * 
	 * @param goods
	 *            货品
	 */
	public void setGoods(Set<Goods> goods) {
		this.goods = goods;
	}

	/**
	 * 获取收藏会员
	 * 
	 * @return 收藏会员
	 */
	@ManyToMany(mappedBy = "favoriteStore", fetch = FetchType.LAZY)
	public Set<Member> getFavoriteMembers() {
		return favoriteMembers;
	}

	/**
	 * 设置收藏会员
	 * 
	 * @param favoriteMembers
	 *            收藏会员
	 */
	public void setFavoriteMembers(Set<Member> favoriteMembers) {
		this.favoriteMembers = favoriteMembers;
	}

	/**
	 * 获取快递单模板
	 * 
	 * @return 快递单模板
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<DeliveryTemplate> getDeliveryTemplates() {
		return deliveryTemplates;
	}

	/**
	 * 设置快递单模板
	 * 
	 * @param deliveryTemplates
	 *            快递单模板
	 */
	public void setDeliveryTemplates(Set<DeliveryTemplate> deliveryTemplates) {
		this.deliveryTemplates = deliveryTemplates;
	}

	/**
	 * 获取发货点
	 * 
	 * @return 发货点
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<DeliveryCenter> getDeliveryCenters() {
		return deliveryCenters;
	}

	/**
	 * 设置发货点
	 * 
	 * @param deliveryCenters
	 *            发货点
	 */
	public void setDeliveryCenters(Set<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	/**
	 * 获取默认运费配置
	 * 
	 * @return 默认运费配置
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<DefaultFreightConfig> getDefaultFreightConfigs() {
		return defaultFreightConfigs;
	}

	/**
	 * 设置默认运费配置
	 * 
	 * @param defaultFreightConfigs
	 *            默认运费配置
	 */
	public void setDefaultFreightConfigs(Set<DefaultFreightConfig> defaultFreightConfigs) {
		this.defaultFreightConfigs = defaultFreightConfigs;
	}

	/**
	 * 获取地区运费配置
	 * 
	 * @return 地区运费配置
	 */
	@ManyToMany(mappedBy = "stores", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<AreaFreightConfig> getAreaFreightConfigs() {
		return areaFreightConfigs;
	}

	/**
	 * 设置地区运费配置
	 * 
	 * @param areaFreightConfigs
	 *            地区运费配置
	 */
	public void setAreaFreightConfigs(Set<AreaFreightConfig> areaFreightConfigs) {
		this.areaFreightConfigs = areaFreightConfigs;
	}

	/**
	 * 获取服务
	 * 
	 * @return 服务
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Svc> getSvcs() {
		return svcs;
	}

	/**
	 * 设置服务
	 * 
	 * @param svcs
	 *            服务
	 */
	public void setSvcs(Set<Svc> svcs) {
		this.svcs = svcs;
	}

	/**
	 * 获取支付事务
	 * 
	 * @return 支付事务
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<PaymentTransaction> getPaymentTransactions() {
		return paymentTransactions;
	}

	/**
	 * 设置支付事务
	 * 
	 * @param paymentTransactions
	 *            支付事务
	 */
	public void setPaymentTransactions(Set<PaymentTransaction> paymentTransactions) {
		this.paymentTransactions = paymentTransactions;
	}

	/**
	 * 获取统计
	 * 
	 * @return 统计
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Statistic> getStatistics() {
		return statistics;
	}

	/**
	 * 设置统计
	 * 
	 * @param statistics
	 *            统计
	 */
	public void setStatistics(Set<Statistic> statistics) {
		this.statistics = statistics;
	}

	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Consultation> getConsultations() {
		return consultations;
	}

	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(Set<Consultation> consultations) {
		this.consultations = consultations;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Review> getReviews() {
		return reviews;
	}

	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * 解析页面标题
	 * 
	 * @return 页面标题
	 */
	@Transient
	public String resolveSeoTitle() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoTitle(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面关键词
	 * 
	 * @return 页面关键词
	 */
	@Transient
	public String resolveSeoKeywords() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoKeywords(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面描述
	 * 
	 * @return 页面描述
	 */
	@Transient
	public String resolveSeoDescription() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoDescription(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 获得店铺是否已到期
	 * 
	 * @return 店铺是否已到期
	 */
	@Transient
	public boolean getHasExpired() {
		return getEndDate() != null && !getEndDate().after(new Date());
	}

	/**
	 * 判断店铺是否支付保证金
	 * 
	 * @return 店铺是否支付保证金
	 */
	@Transient
	public boolean hasPaid() {
		BigDecimal bail = getBail();
		BigDecimal bailPaid = getBailPaid();

		if (bail != null && bailPaid != null && bailPaid.compareTo(bail) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为自营店铺
	 * 
	 * @return 是否为自营店铺
	 */
	@Transient
	public boolean isSelf() {
		return getType() != null && Type.self.equals(getType());
	}

	/**
	 * 判断店铺是否包含该商品
	 * 
	 * @return 否包含该商品
	 */
	@Transient
	public boolean hasContained(Product product) {
		return product != null && product.getGoods() != null && product.getGoods().getStore() != null && product.getGoods().getStore().equals(this);
	}

	/**
	 * 判断店铺是否有效
	 * 
	 * @return 店铺是否有效
	 */
	@Transient
	public boolean isActive() {
		if (BooleanUtils.isTrue(getIsEnabled()) && Store.Status.success.equals(getStatus()) && !getHasExpired()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断促销折扣是否可用
	 * 
	 * @return 促销折扣是否可用
	 */
	@Transient
	public boolean getIsDiscount() {
		return getDiscount() != null && getDiscount().after(new Date());
	}

	/**
	 * 判断促销满减是否可用
	 * 
	 * @return 促销满减是否可用
	 */
	@Transient
	public boolean getIsFullReduction() {
		return getFullReduction() != null && getFullReduction().after(new Date());
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		if (CollectionUtils.isNotEmpty(getAdvertisingImages())) {
			Collections.sort(getAdvertisingImages());
		}
	}

	/**
	 * 更新前处理
	 */
	@PreUpdate
	public void preUpdate() {
		if (CollectionUtils.isNotEmpty(getAdvertisingImages())) {
			Collections.sort(getAdvertisingImages());
		}
	}

	/**
	 * 删除前处理
	 */
	@PreRemove
	public void preRemove() {
		Set<Member> favoriteMembers = getFavoriteMembers();
		if (favoriteMembers != null) {
			for (Member favoriteMember : favoriteMembers) {
				favoriteMember.getFavoriteStore().remove(this);
			}
		}
		Set<ProductCategory> productCategories = getProductCategorys();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				productCategory.getStores().remove(this);
			}
		}
	}

	/**
	 * 类型转换 - 广告图片
	 * 
	 * @author SHOP++ Team
	 * @version 5.0
	 */
	@Converter
	public static class AdvertisingImageConverter extends BaseAttributeConverter<List<AdvertisingImage>> {
	}
}
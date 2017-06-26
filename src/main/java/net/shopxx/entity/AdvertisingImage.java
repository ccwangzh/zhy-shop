/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity - 广告图片
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class AdvertisingImage implements Serializable, Comparable<AdvertisingImage> {

	private static final long serialVersionUID = -2643112399584260120L;

	/** 标题 */
	private String title;

	/** 图片 */
	private String image;

	/** 链接地址 */
	private String url;

	/** 排序 */
	private Integer order;

	/**
	 * 获取标题
	 * 
	 * @return 标题
	 */
	@Length(max = 200)
	public String getTitle() {
		return title;
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 *            标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取图片
	 * 
	 * @return 图片
	 */
	@Length(max = 200)
	public String getImage() {
		return image;
	}

	/**
	 * 设置图片
	 * 
	 * @param image
	 *            图片
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 获取链接地址
	 * 
	 * @return 链接地址
	 */
	@Length(max = 200)
	@Pattern(regexp = "^(?i)(http:\\/\\/|https:\\/\\/|ftp:\\/\\/|mailto:|\\/|#).*$")
	public String getUrl() {
		return url;
	}

	/**
	 * 设置链接地址
	 * 
	 * @param url
	 *            链接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取排序
	 * 
	 * @return 排序
	 */
	@Min(0)
	public Integer getOrder() {
		return order;
	}

	/**
	 * 设置排序
	 * 
	 * @param order
	 *            排序
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * 判断是否为空
	 * 
	 * @return 是否为空
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return StringUtils.isEmpty(getImage());
	}

	/**
	 * 实现compareTo方法
	 * 
	 * @param advertisingImage
	 *            广告图片
	 * @return 比较结果
	 */
	public int compareTo(AdvertisingImage advertisingImage) {
		if (advertisingImage == null) {
			return 1;
		}
		return new CompareToBuilder().append(getOrder(), advertisingImage.getOrder()).toComparison();
	}

}
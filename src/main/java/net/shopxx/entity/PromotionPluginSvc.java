/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 促销插件服务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_promotion_plugin_svc")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_promotion_plugin_svc")
public class PromotionPluginSvc extends Svc {

	private static final long serialVersionUID = 7240764880070217374L;

	/** 促销插件Id */
	private String promotionPluginId;

	/**
	 * 获取促销插件Id
	 * 
	 * @return 促销插件Id
	 */
	public String getPromotionPluginId() {
		return promotionPluginId;
	}

	/**
	 * 设置促销插件Id
	 * 
	 * @param promotionPluginId
	 *            促销插件Id
	 */
	public void setPromotionPluginId(String promotionPluginId) {
		this.promotionPluginId = promotionPluginId;
	}
}
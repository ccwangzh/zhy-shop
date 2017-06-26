/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.plugin.discount;

import org.springframework.stereotype.Component;

import net.shopxx.entity.Promotion;
import net.shopxx.plugin.PromotionPlugin;

/**
 * Plugin - 折扣
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Component("discount")
public class Discount extends PromotionPlugin {

	@Override
	public String getName() {
		return "折扣";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "SHOP++";
	}

	@Override
	public String getInstallUrl() {
		return "discount/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "discount/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "discount/setting.jhtml";
	}

	@Override
	public String getPriceExpression(Promotion promotion, Boolean useAmountPromotion, Boolean useNumberPromotion) {
		if (promotion.getDiscount() == null) {
			return "";
		}
		if (promotion.getDiscount() < 1) {
			return "price*" + String.valueOf(promotion.getDiscount());
		} else {
			return "price-" + String.valueOf(promotion.getDiscount());
		}
	}

	@Override
	public String getPointExpression(Promotion promotion) {
		if (promotion.getAdjustmentPoint() == null) {
			return "";
		}
		return "point+" + String.valueOf(promotion.getAdjustmentPoint());
	}

	public boolean getIsAvailable() {
		return getIsInstalled() && getIsEnabled();
	}
}
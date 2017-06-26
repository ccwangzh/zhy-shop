/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.plugin.fullReduction;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import net.shopxx.entity.Promotion;
import net.shopxx.plugin.PromotionPlugin;

/**
 * Plugin - 满减
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Component("fullReduction")
public class FullReduction extends PromotionPlugin {

	@Override
	public String getName() {
		return "满减";
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
		return "full_reduction/install.jhtml";
	}

	@Override
	public String getUninstallUrl() {
		return "full_reduction/uninstall.jhtml";
	}

	@Override
	public String getSettingUrl() {
		return "full_reduction/setting.jhtml";
	}

	@Override
	public String getPriceExpression(Promotion promotion, Boolean useAmountPromotion, Boolean useNumberPromotion) {
		if (useAmountPromotion != null && useAmountPromotion) {
			BigDecimal conditionsAmoun = promotion.getConditionsAmount();
			BigDecimal creditAmount = promotion.getCreditAmount();
			if (conditionsAmoun != null && creditAmount != null && conditionsAmoun.compareTo(BigDecimal.ZERO) > 0 && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
				return "price-((price/" + conditionsAmoun.toString() + ") as int) *" + creditAmount.toString();
			}
		} else if (useNumberPromotion != null && useNumberPromotion) {
			Integer conditionsNumber = promotion.getConditionsNumber();
			Integer creditNumber = promotion.getCreditNumber();
			if (conditionsNumber != null && creditNumber != null && conditionsNumber > 0 && creditNumber > 0) {
				return "price-(quantity.intdiv(" + conditionsNumber + "))*" + "(" + creditNumber + "*" + "(price/quantity)" + ")";
			}
		}
		return "";
	}

	@Override
	public String getPointExpression(Promotion promotion) {
		Integer conditionsPoint = promotion.getConditionsPoint();
		Integer adjustmentPoint = promotion.getAdjustmentPoint();
		if (conditionsPoint != null && adjustmentPoint != null && conditionsPoint > 0 && adjustmentPoint > 0) {
			return "point-((point/" + conditionsPoint.toString() + ") as int) *" + adjustmentPoint.toString();
		}
		return "";
	}

	public boolean getIsAvailable() {
		return getIsInstalled() && getIsEnabled();
	}
}
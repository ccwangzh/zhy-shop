/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.template.directive;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import net.shopxx.entity.Tag;
import net.shopxx.entity.TradeGoods;
import net.shopxx.service.GoodsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 模板指令 - 交易商品列表
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Component("tradeGoodsDirective")
public class TradeGoodsDirective extends BaseDirective {

	/** 变量名称 */
	private static final String VARIABLE_NAME = "tradeGoodsList";

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;

	/**
	 * 执行
	 * 
	 * @param env
	 *            环境变量
	 * @param params
	 *            参数
	 * @param loopVars
	 *            循环变量
	 * @param body
	 *            模板内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Integer count = getCount(params);
		List<TradeGoods> tradeGoodsList = goodsService.findTradeGoodsList(true,count);
		setLocalVariable(VARIABLE_NAME, tradeGoodsList, env, body);
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.template.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import net.shopxx.entity.Goods;
import net.shopxx.service.GoodsService;
import net.shopxx.util.FreeMarkerUtils;

/**
 * 模板指令 - 店铺货品列表
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Component("storeGoodsListDirective")
public class StoreGoodsListDirective extends BaseDirective {

	/** "店铺ID"参数名称 */
	private static final String STORE_ID_PARAMETER_NAME = "storeId";

	/** "店铺标签ID"参数名称 */
	private static final String STORE_TAG_ID_PARAMETER_NAME = "storeTagId";

	/** 变量名称 */
	private static final String VARIABLE_NAME = "storeGoodsList";

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
		Long storeId = FreeMarkerUtils.getParameter(STORE_ID_PARAMETER_NAME, Long.class, params);
		Long storeTagId = FreeMarkerUtils.getParameter(STORE_TAG_ID_PARAMETER_NAME, Long.class, params);
		List<Goods> goodsList = goodsService.findList(null, storeId, storeTagId, null, null, null, null, null, null, null, true, Goods.Status.onTheshelf, true, null, null, null, null, null, null, null, null, false);
		setLocalVariable(VARIABLE_NAME, goodsList, env, body);
	}

}
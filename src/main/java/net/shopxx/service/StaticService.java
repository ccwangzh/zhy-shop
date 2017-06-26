/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.Date;
import java.util.Map;

import net.shopxx.entity.Article;
import net.shopxx.entity.ArticleCategory;
import net.shopxx.entity.Goods;
import net.shopxx.entity.ProductCategory;

/**
 * Service - 静态化
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface StaticService {

	/**
	 * 生成静态
	 * 
	 * @param templatePath
	 *            模板文件路径
	 * @param staticPath
	 *            静态文件路径
	 * @param model
	 *            数据
	 */
	void generate(String templatePath, String staticPath, Map<String, Object> model);

	/**
	 * 生成静态
	 * 
	 * @param article
	 *            文章
	 */
	void generate(Article article);

	/**
	 * 生成静态
	 * 
	 * @param goods
	 *            货品
	 */
	void generate(Goods goods);

	/**
	 * 生成文章静态
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 */
	void generateArticle(ArticleCategory articleCategory, Date beginDate, Date endDate);

	/**
	 * 生成文章静态
	 */
	void generateArticle();

	/**
	 * 生成货品静态
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 */
	void generateGoods(ProductCategory productCategory, Date beginDate, Date endDate);

	/**
	 * 生成货品静态
	 */
	void generateGoods();

	/**
	 * 生成首页静态
	 */
	void generateIndex();

	/**
	 * 生成Sitemap
	 */
	void generateSitemap();

	/**
	 * 生成其它静态
	 */
	void generateOther();

	/**
	 * 删除静态
	 * 
	 * @param staticPath
	 *            静态文件路径
	 */
	void delete(String staticPath);

	/**
	 * 删除静态
	 * 
	 * @param article
	 *            文章
	 */
	void delete(Article article);

	/**
	 * 删除静态
	 * 
	 * @param goods
	 *            货品
	 */
	void delete(Goods goods);

	/**
	 * 删除首页静态
	 */
	void deleteIndex();

	/**
	 * 删除其它静态
	 */
	void deleteOther();

}
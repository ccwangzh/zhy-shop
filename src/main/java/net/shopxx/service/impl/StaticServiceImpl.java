/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.shopxx.Setting;
import net.shopxx.TemplateConfig;
import net.shopxx.dao.ArticleDao;
import net.shopxx.dao.GoodsDao;
import net.shopxx.entity.Article;
import net.shopxx.entity.ArticleCategory;
import net.shopxx.entity.Goods;
import net.shopxx.entity.ProductCategory;
import net.shopxx.service.StaticService;
import net.shopxx.util.SystemUtils;

/**
 * Service - 静态化
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("staticServiceImpl")
public class StaticServiceImpl implements StaticService, ServletContextAware {

	/** Sitemap最大URL数量 */
	private static final Integer SITEMAP_URL_MAX_SIZE = 10000;

	/** ServletContext */
	private ServletContext servletContext;

	@Resource(name = "freeMarkerConfigurer")
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;
	@Resource(name = "goodsDaoImpl")
	private GoodsDao goodsDao;

	/**
	 * 设置ServletContext
	 * 
	 * @param servletContext
	 *            ServletContext
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Transactional(readOnly = true)
	public void generate(String templatePath, String staticPath, Map<String, Object> model) {
		Assert.hasText(templatePath);
		Assert.hasText(staticPath);

		Writer writer = null;
		try {
			Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templatePath);
			File staticFile = new File(servletContext.getRealPath(staticPath));
			File staticDir = staticFile.getParentFile();
			if (staticDir != null) {
				staticDir.mkdirs();
			}
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(staticFile), "UTF-8"));
			template.process(model, writer);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	@Transactional(readOnly = true)
	public void generate(Article article) {
		if (article == null) {
			return;
		}
		delete(article);
		if (!article.getIsPublication()) {
			return;
		}
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("articleContent");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("article", article);
		for (int i = 1; i <= article.getTotalPages(); i++) {
			model.put("pageNumber", i);
			generate(templateConfig.resolveTemplatePath(), article.getPath(i), model);
		}
	}

	@Transactional(readOnly = true)
	public void generate(Goods goods) {
		if (goods == null) {
			return;
		}
//		delete(goods);
		if (!goods.getIsMarketable() || !goods.getIsActive()) {
			return;
		}
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("goodsContent");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("goods", goods);
		generate(templateConfig.resolveTemplatePath(), goods.getPath(), model);
		
		templateConfig = SystemUtils.getTemplateConfig("h5_goodsContent");
		generate(templateConfig.resolveTemplatePath(), "/h5"+goods.getPath(), model);
	}

	@Transactional(readOnly = true)
	public void generateArticle(ArticleCategory articleCategory, Date beginDate, Date endDate) {
		for (int i = 0;; i += 100) {
			List<Article> articles = articleDao.findList(articleCategory, true, beginDate, endDate, i, 100);
			if (CollectionUtils.isNotEmpty(articles)) {
				for (Article article : articles) {
					generate(article);
				}
				articleDao.flush();
				articleDao.clear();
			}
			if (articles.size() < 100) {
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public void generateArticle() {
		generateArticle(null, null, null);
	}

	@Transactional(readOnly = true)
	public void generateGoods(ProductCategory productCategory, Date beginDate, Date endDate) {
		for (int i = 0;; i += 100) {
			List<Goods> goodsList = goodsDao.findList(productCategory, Goods.Status.onTheshelf, beginDate, endDate, i, 100);
			if (CollectionUtils.isNotEmpty(goodsList)) {
				for (Goods goods : goodsList) {
					generate(goods);
				}
				goodsDao.flush();
				goodsDao.clear();
			}
			if (goodsList.size() < 100) {
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public void generateGoods() {
		generateGoods(null, null, null);
	}

	@Transactional(readOnly = true)
	public void generateIndex() {
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("index");
		generate(templateConfig.resolveTemplatePath(), templateConfig.resolveStaticPath(), null);
		templateConfig = SystemUtils.getTemplateConfig("h5_index");
		generate(templateConfig.resolveTemplatePath(), templateConfig.resolveStaticPath(), null);
	}

	@Transactional(readOnly = true)
	public void generateSitemap() {
		TemplateConfig sitemapIndexTemplateConfig = SystemUtils.getTemplateConfig("sitemapIndex");
		TemplateConfig sitemapTemplateConfig = SystemUtils.getTemplateConfig("sitemap");
		List<SitemapUrl> sitemapUrls = new ArrayList<SitemapUrl>();

		Setting setting = SystemUtils.getSetting();
		SitemapUrl indexSitemapUrl = new SitemapUrl();
		indexSitemapUrl.setLoc(setting.getSiteUrl());
		indexSitemapUrl.setLastmod(new Date());
		indexSitemapUrl.setChangefreq(SitemapUrl.Changefreq.hourly);
		indexSitemapUrl.setPriority(1);
		sitemapUrls.add(indexSitemapUrl);

		for (int i = 0;; i += 100) {
			List<Article> articles = articleDao.findList(i, 100, null, null);
			if (CollectionUtils.isNotEmpty(articles)) {
				for (Article article : articles) {
					SitemapUrl articleSitemapUrl = new SitemapUrl();
					articleSitemapUrl.setLoc(article.getUrl());
					articleSitemapUrl.setLastmod(article.getLastModifiedDate());
					articleSitemapUrl.setChangefreq(SitemapUrl.Changefreq.daily);
					articleSitemapUrl.setPriority(0.6F);
					sitemapUrls.add(articleSitemapUrl);
				}
				articleDao.flush();
				articleDao.clear();
			}
			if (articles.size() < 100) {
				break;
			}
		}
		for (int i = 0;; i += 100) {
			List<Goods> goodsList = goodsDao.findList(i, 100, null, null);
			if (CollectionUtils.isNotEmpty(goodsList)) {
				for (Goods goods : goodsList) {
					SitemapUrl goodsSitemapUrl = new SitemapUrl();
					goodsSitemapUrl.setLoc(goods.getUrl());
					goodsSitemapUrl.setLastmod(goods.getLastModifiedDate());
					goodsSitemapUrl.setChangefreq(SitemapUrl.Changefreq.daily);
					goodsSitemapUrl.setPriority(0.8F);
					sitemapUrls.add(goodsSitemapUrl);
				}
				goodsDao.flush();
				goodsDao.clear();
			}
			if (goodsList.size() < 100) {
				break;
			}
		}

		List<String> sitemapPaths = new ArrayList<String>();
		for (int i = 0, index = 0; i < sitemapUrls.size(); i += SITEMAP_URL_MAX_SIZE, index++) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("index", index);
			model.put("sitemapUrls", sitemapUrls.subList(i, i + SITEMAP_URL_MAX_SIZE <= sitemapUrls.size() ? i + SITEMAP_URL_MAX_SIZE : sitemapUrls.size()));
			String sitemapPath = sitemapTemplateConfig.resolveStaticPath(model);
			sitemapPaths.add(sitemapPath);
			generate(sitemapTemplateConfig.resolveTemplatePath(), sitemapPath, model);
		}

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("sitemapPaths", sitemapPaths);
		generate(sitemapIndexTemplateConfig.resolveTemplatePath(), sitemapIndexTemplateConfig.resolveStaticPath(), model);
	}

	@Transactional(readOnly = true)
	public void generateOther() {
		TemplateConfig shopCommonJsTemplateConfig = SystemUtils.getTemplateConfig("shopCommonJs");
		TemplateConfig adminCommonJsTemplateConfig = SystemUtils.getTemplateConfig("adminCommonJs");
		generate(shopCommonJsTemplateConfig.resolveTemplatePath(), shopCommonJsTemplateConfig.resolveStaticPath(), null);
		generate(adminCommonJsTemplateConfig.resolveTemplatePath(), adminCommonJsTemplateConfig.resolveStaticPath(), null);
	}

	@Transactional(readOnly = true)
	public void delete(String staticPath) {
		if (StringUtils.isEmpty(staticPath)) {
			return;
		}
		File staticFile = new File(servletContext.getRealPath(staticPath));
		FileUtils.deleteQuietly(staticFile);
	}

	@Transactional(readOnly = true)
	public void delete(Article article) {
		if (article == null || StringUtils.isEmpty(article.getPath())) {
			return;
		}
		for (int i = 1;; i++) {
			String staticPath = article.getPath(i);
			File staticFile = new File(servletContext.getRealPath(staticPath));
			if (!staticFile.exists() || !staticFile.isFile()) {
				break;
			}
			delete(staticPath);
		}
	}

	@Transactional(readOnly = true)
	public void delete(Goods goods) {
		if (goods == null || StringUtils.isEmpty(goods.getPath())) {
			return;
		}
		delete(goods.getPath());
	}

	@Transactional(readOnly = true)
	public void deleteIndex() {
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("index");
		delete(templateConfig.resolveStaticPath());
	}

	@Transactional(readOnly = true)
	public void deleteOther() {
		TemplateConfig shopCommonJsTemplateConfig = SystemUtils.getTemplateConfig("shopCommonJs");
		TemplateConfig adminCommonJsTemplateConfig = SystemUtils.getTemplateConfig("adminCommonJs");
		delete(shopCommonJsTemplateConfig.resolveStaticPath());
		delete(adminCommonJsTemplateConfig.resolveStaticPath());
	}

	/**
	 * SitemapUrl
	 * 
	 * @author SHOP++ Team
	 * @version 5.0
	 */
	public static class SitemapUrl {

		/**
		 * 更新频率
		 */
		public enum Changefreq {

			/** 经常 */
			always,

			/** 每小时 */
			hourly,

			/** 每天 */
			daily,

			/** 每周 */
			weekly,

			/** 每月 */
			monthly,

			/** 每年 */
			yearly,

			/** 从不 */
			never
		}

		/** 链接地址 */
		private String loc;

		/** 最后修改日期 */
		private Date lastmod;

		/** 更新频率 */
		private Changefreq changefreq;

		/** 权重 */
		private float priority;

		/**
		 * 获取链接地址
		 * 
		 * @return 链接地址
		 */
		public String getLoc() {
			return loc;
		}

		/**
		 * 设置链接地址
		 * 
		 * @param loc
		 *            链接地址
		 */
		public void setLoc(String loc) {
			this.loc = loc;
		}

		/**
		 * 获取最后修改日期
		 * 
		 * @return 最后修改日期
		 */
		public Date getLastmod() {
			return lastmod;
		}

		/**
		 * 设置最后修改日期
		 * 
		 * @param lastmod
		 *            最后修改日期
		 */
		public void setLastmod(Date lastmod) {
			this.lastmod = lastmod;
		}

		/**
		 * 获取更新频率
		 * 
		 * @return 更新频率
		 */
		public Changefreq getChangefreq() {
			return changefreq;
		}

		/**
		 * 设置更新频率
		 * 
		 * @param changefreq
		 *            更新频率
		 */
		public void setChangefreq(Changefreq changefreq) {
			this.changefreq = changefreq;
		}

		/**
		 * 获取权重
		 * 
		 * @return 权重
		 */
		public float getPriority() {
			return priority;
		}

		/**
		 * 设置权重
		 * 
		 * @param priority
		 *            权重
		 */
		public void setPriority(float priority) {
			this.priority = priority;
		}

	}

}
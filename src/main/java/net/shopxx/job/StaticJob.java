/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.job;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.shopxx.service.StaticService;

/**
 * Job - 静态化
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Lazy(false)
@Component("staticJob")
public class StaticJob {

	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	/**
	 * 生成首页静态
	 */
	@Scheduled(fixedDelayString = "${job.static_generate_index.delay}")
	public void generateIndex() {
		staticService.generateIndex();
	}

	/**
	 * 生成文章静态
	 */
	@Scheduled(cron = "${job.static_generate_article.cron}")
	public void generateArticle() {
		staticService.generateArticle();
	}

	/**
	 * 生成货品静态
	 */
//	@Scheduled(cron = "${job.static_generate_goods.cron}")
	@Scheduled(fixedDelayString = "${job.static_generate_index.delay}")
	public void generateGoods() {
		staticService.generateGoods();
	}

	/**
	 * 生成Sitemap
	 */
	@Scheduled(cron = "${job.static_generate_sitemap.cron}")
	public void generateSitemap() {
		staticService.generateSitemap();
	}

	/**
	 * 生成其它静态
	 */
	@Scheduled(cron = "${job.static_generate_other.cron}")
	public void generateOther() {
		staticService.generateOther();
	}

}
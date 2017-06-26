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

import net.shopxx.service.StoreService;

/**
 * Job - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Lazy(false)
@Component("storeJob")
public class StoreJob {

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 过期店铺处理
	 */
	@Scheduled(cron = "${job.store_expired_processing.cron}")
	public void expiredStoreProcessing() {
		storeService.expiredStoreProcessing();
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.StockLogDao;
import net.shopxx.entity.StockLog;
import net.shopxx.entity.Store;
import net.shopxx.service.StockLogService;

/**
 * Service - 库存记录
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("stockLogServiceImpl")
public class StockLogServiceImpl extends BaseServiceImpl<StockLog, Long> implements StockLogService {

	@Resource(name = "stockLogDaoImpl")
	private StockLogDao stockLogDao;

	@Transactional(readOnly = true)
	public Page<StockLog> findPage(Store store, Pageable pageable) {
		return stockLogDao.findPage(store, pageable);
	}

}
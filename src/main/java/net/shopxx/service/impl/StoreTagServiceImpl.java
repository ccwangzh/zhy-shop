/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.StoreTagDao;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreTag;
import net.shopxx.service.StoreTagService;

/**
 * Service - 店铺标签
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("storeTagServiceImpl")
public class StoreTagServiceImpl extends BaseServiceImpl<StoreTag, Long> implements StoreTagService {

	@Resource(name = "storeTagDaoImpl")
	private StoreTagDao storeTagDao;

	@Transactional(readOnly = true)
	public List<StoreTag> findList(Store store, Boolean isShow) {
		return storeTagDao.findList(store, isShow);
	}

	@Transactional(readOnly = true)
	public Page<StoreTag> findPage(Store store, Pageable pageable) {
		return storeTagDao.findPage(store, pageable);
	}

}
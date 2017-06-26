/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.StoreRankDao;
import net.shopxx.entity.StoreRank;
import net.shopxx.entity.StoreRank.Type;
import net.shopxx.service.StoreRankService;

/**
 * Service - 店铺等级
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("storeRankServiceImpl")
public class StoreRankServiceImpl extends BaseServiceImpl<StoreRank, Long> implements StoreRankService {

	@Resource(name = "storeRankDaoImpl")
	private StoreRankDao storeRankDao;

	@Transactional(readOnly = true)
	public boolean nameExists(StoreRank.Type type, String name) {
		return storeRankDao.nameExists(type, name);
	}

	@Transactional(readOnly = true)
	public boolean nameUnique(StoreRank.Type type, String previousName, String currentName) {
		return StringUtils.equalsIgnoreCase(previousName, currentName) || !storeRankDao.nameExists(type, currentName);
	}

	@Transactional(readOnly = true)
	public StoreRank findDefault(Type type) {
		return storeRankDao.findDefault(type);
	}

	@Transactional(readOnly = true)
	public List<StoreRank> findList(Type type, List<Filter> filters, List<Order> orders) {
		return storeRankDao.findList(type, filters, orders);
	}

	@Override
	@Transactional
	public StoreRank save(StoreRank storeRank) {
		Assert.notNull(storeRank);
		Assert.notNull(storeRank.getType());

		if (BooleanUtils.isTrue(storeRank.getIsDefault())) {
			storeRankDao.clearDefault(storeRank.getType());
		}
		return super.save(storeRank);
	}

	@Override
	@Transactional
	public StoreRank update(StoreRank storeRank) {
		Assert.notNull(storeRank);
		Assert.notNull(storeRank.getType());

		StoreRank pStoreRank = super.update(storeRank);
		if (BooleanUtils.isTrue(pStoreRank.getIsDefault())) {
			storeRankDao.clearDefault(pStoreRank, storeRank.getType());
		}
		return pStoreRank;
	}

}
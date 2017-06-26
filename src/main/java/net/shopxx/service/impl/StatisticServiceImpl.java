/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.MemberDao;
import net.shopxx.dao.OrderDao;
import net.shopxx.dao.StatisticDao;
import net.shopxx.dao.StoreDao;
import net.shopxx.entity.Statistic;
import net.shopxx.entity.Store;
import net.shopxx.service.StatisticService;

/**
 * Service - 统计
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("statisticServiceImpl")
public class StatisticServiceImpl extends BaseServiceImpl<Statistic, Long> implements StatisticService {

	@Resource(name = "statisticDaoImpl")
	private StatisticDao statisticDao;
	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;
	@Resource(name = "orderDaoImpl")
	private OrderDao orderDao;
	@Resource(name = "storeDaoImpl")
	private StoreDao storeDao;

	@Transactional(readOnly = true)
	public boolean exists(Store store, int year, int month, int day) {
		return statisticDao.exists(store, year, month, day);
	}

	public void collect(int year, int month, int day) {
		if (!statisticDao.exists(null, year, month, day)) {
			Statistic statistic = collectData(null, year, month, day);
			statisticDao.persist(statistic);
		}

		for (int i = 0;; i += 100) {
			List<Store> storeList = storeDao.findList(null, Store.Status.success, null, i, 100);
			if (CollectionUtils.isEmpty(storeList)) {
				return;
			}
			for (Store store : storeList) {
				if (!statisticDao.exists(store, year, month, day)) {
					Statistic pStatistic = collectData(store, year, month, day);
					statisticDao.persist(pStatistic);
				}
			}
			storeDao.flush();
			storeDao.clear();
			if (storeList.size() < 100) {
				return;
			}
		}
	}

	@Transactional(readOnly = true)
	public Statistic collectData(Store store, int year, int month, int day) {
		Assert.state(month >= 0);
		Assert.state(day >= 0);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(year, month, day);
		beginCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginCalendar.set(Calendar.MINUTE, beginCalendar.getActualMinimum(Calendar.MINUTE));
		beginCalendar.set(Calendar.SECOND, beginCalendar.getActualMinimum(Calendar.SECOND));
		Date beginDate = beginCalendar.getTime();

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(year, month, day);
		endCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE, beginCalendar.getActualMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND, beginCalendar.getActualMaximum(Calendar.SECOND));
		Date endDate = endCalendar.getTime();

		Statistic statistics = new Statistic();
		statistics.setYear(year);
		statistics.setMonth(month);
		statistics.setDay(day);
		statistics.setRegisterMemberCount(memberDao.registerMemberCount(beginDate, endDate));
		statistics.setCreateOrderCount(orderDao.createOrderCount(store, beginDate, endDate));
		statistics.setCompleteOrderCount(orderDao.completeOrderCount(store, beginDate, endDate));
		statistics.setCreateOrderAmount(orderDao.createOrderAmount(store, beginDate, endDate));
		statistics.setCompleteOrderAmount(orderDao.completeOrderAmount(store, beginDate, endDate));
		statistics.setStore(store);
		return statistics;
	}

	@Transactional(readOnly = true)
	public List<Statistic> analyze(Store store, Statistic.Period period, Date beginDate, Date endDate) {
		return statisticDao.analyze(store, period, beginDate, endDate);
	}
}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.StoreRankDao;
import net.shopxx.entity.StoreRank;

/**
 * Dao - 店铺等级
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("storeRankDaoImpl")
public class StoreRankDaoImpl extends BaseDaoImpl<StoreRank, Long> implements StoreRankDao {

	public boolean nameExists(StoreRank.Type type, String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		String jpql = "select count(*) from StoreRank storeRank where storeRank.type = :type and lower(storeRank.name) = lower(:name)";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("type", type).setParameter("name", name).getSingleResult();
		return count > 0;
	}

	public StoreRank findDefault(StoreRank.Type type) {
		try {
			String jpql = "select storeRank from StoreRank storeRank where  storeRank.type = :type and storeRank.isDefault = true";
			return entityManager.createQuery(jpql, StoreRank.class).setParameter("type", type).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<StoreRank> findList(StoreRank.Type type, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<StoreRank> criteriaQuery = criteriaBuilder.createQuery(StoreRank.class);
		Root<StoreRank> root = criteriaQuery.from(StoreRank.class);
		criteriaQuery.select(root);
		if (type != null) {
			criteriaQuery.where(criteriaBuilder.equal(root.get("type"), type));
		}
		return super.findList(criteriaQuery, null, null, filters, orders);
	}

	public void clearDefault(StoreRank.Type type) {
		String jpql = "update StoreRank storeRank set storeRank.isDefault = false where storeRank.type = :type and storeRank.isDefault = true";
		entityManager.createQuery(jpql).setParameter("type", type).executeUpdate();
	}

	public void clearDefault(StoreRank exclude, StoreRank.Type type) {
		Assert.notNull(exclude);
		String jpql = "update StoreRank storeRank set storeRank.isDefault = false where storeRank.type = :type and storeRank.isDefault = true and storeRank != :exclude";
		entityManager.createQuery(jpql).setParameter("type", type).setParameter("exclude", exclude).executeUpdate();
	}

}
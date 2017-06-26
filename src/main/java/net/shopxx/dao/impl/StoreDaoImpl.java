/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.StoreDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.Store;
import net.shopxx.entity.Store.Status;
import net.shopxx.entity.Store.Type;

/**
 * Dao - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("storeDaoImpl")
public class StoreDaoImpl extends BaseDaoImpl<Store, Long> implements StoreDao {

	public Store findBySn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return null;
		}
		String jpql = "select shop from Store store where lower(store.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Store.class).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public boolean nameExists(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		String jpql = "select count(*) from Store store where lower(store.name) = lower(:name)";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("name", name).getSingleResult();
		return count > 0;
	}

	public boolean mobileExists(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		String jpql = "select count(*) from Store store where lower(store.mobile) = lower(:mobile)";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("mobile", mobile).getSingleResult();
		return count > 0;
	}

	public boolean emailExists(String email) {
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		String jpql = "select count(*) from Store store where lower(store.email) = lower(:email)";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("email", email).getSingleResult();
		return count > 0;
	}

	public Store findByName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		try {
			String jpql = "select stores from Store stores where lower(stores.name) = lower(:name)";
			return entityManager.createQuery(jpql, Store.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Store> findPage(Store.Type type, Store.Status status, Boolean isEnabled, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Store> criteriaQuery = criteriaBuilder.createQuery(Store.class);
		Root<Store> root = criteriaQuery.from(Store.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		if (isEnabled != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Store> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return Page.emptyPage(pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Store> criteriaQuery = criteriaBuilder.createQuery(Store.class);
		Root<Store> root = criteriaQuery.from(Store.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.join("favoriteMembers"), member));
		return super.findPage(criteriaQuery, pageable);
	}

	public List<Store> findList(Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Store> criteriaQuery = criteriaBuilder.createQuery(Store.class);
		Root<Store> root = criteriaQuery.from(Store.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("endDate").isNotNull(), criteriaBuilder.lessThanOrEqualTo(root.<Date> get("endDate"), new Date())));
		criteriaQuery.where(restrictions);
		return findList(criteriaQuery, first, count);
	}

	public List<Store> findList(Type type, Status status, Boolean isEnabled, Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Store> criteriaQuery = criteriaBuilder.createQuery(Store.class);
		Root<Store> root = criteriaQuery.from(Store.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		if (isEnabled != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
		}
		criteriaQuery.where(restrictions);
		return findList(criteriaQuery, first, count);
	}

}
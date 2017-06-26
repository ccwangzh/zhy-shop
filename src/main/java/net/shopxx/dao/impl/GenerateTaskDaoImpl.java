/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.GenerateTaskDao;
import net.shopxx.entity.GenerateTask;

/**
 * Dao - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Repository("generateTaskDaoImpl")
public class GenerateTaskDaoImpl extends BaseDaoImpl<GenerateTask, Long> implements GenerateTaskDao {

	public boolean exists(GenerateTask.Type type, GenerateTask.Status status, Long targetId) {
		if (type == null || status == null || targetId == null) {
			return false;
		}
		String jpql = "select count(*) from GenerateTask generateTask where generateTask.type = :type and generateTask.status = :status and generateTask.targetId = :targetId";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("type", type).setParameter("status", status).setParameter("targetId", targetId).getSingleResult();
		return count > 0;
	}

	public List<GenerateTask> findList(GenerateTask.Type type, GenerateTask.Status status, Integer first, Integer count, LockModeType lockModeType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<GenerateTask> criteriaQuery = criteriaBuilder.createQuery(GenerateTask.class);
		Root<GenerateTask> root = criteriaQuery.from(GenerateTask.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		criteriaQuery.where(restrictions);
		TypedQuery<GenerateTask> query = entityManager.createQuery(criteriaQuery);
		if (first != null) {
			query.setFirstResult(first);
		}
		if (count != null) {
			query.setMaxResults(count);
		}
		if (lockModeType != null) {
			query.setLockMode(lockModeType);
		}
		return query.getResultList();
	}

	public void remove(GenerateTask.Type type, GenerateTask.Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaDelete<GenerateTask> criteriaDelete = criteriaBuilder.createCriteriaDelete(GenerateTask.class);
		Root<GenerateTask> root = criteriaDelete.from(GenerateTask.class);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		entityManager.createQuery(criteriaDelete).executeUpdate();
	}

}
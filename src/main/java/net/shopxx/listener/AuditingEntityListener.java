/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import net.shopxx.AuditingMetadata;
import net.shopxx.AuditorAware;
import net.shopxx.util.SpringUtils;

/**
 * Listener - 审计
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class AuditingEntityListener {

	/** 审计者Aware缓存 */
	@SuppressWarnings("rawtypes")
	private static final Map<Class<?>, AuditorAware> AUDITOR_AWARE_CACHE = new ConcurrentHashMap<Class<?>, AuditorAware>();

	/**
	 * 保存前处理
	 * 
	 * @param entity
	 *            实体对象
	 */
	@SuppressWarnings("unchecked")
	@PrePersist
	public void prePersist(Object entity) {
		AuditingMetadata auditingMetadata = AuditingMetadata.getAuditingMetadata(entity.getClass());
		if (!auditingMetadata.isAuditable()) {
			return;
		}

		List<AuditingMetadata.Property> createdByProperties = auditingMetadata.getCreatedByProperties();
		List<AuditingMetadata.Property> createdDateProperties = auditingMetadata.getCreatedDateProperties();
		List<AuditingMetadata.Property> lastModifiedByProperties = auditingMetadata.getLastModifiedByProperties();
		List<AuditingMetadata.Property> lastModifiedDateProperties = auditingMetadata.getLastModifiedDateProperties();

		List<AuditingMetadata.Property> byProperties = (List<AuditingMetadata.Property>) CollectionUtils.union(createdByProperties, lastModifiedByProperties);
		List<AuditingMetadata.Property> dateProperties = (List<AuditingMetadata.Property>) CollectionUtils.union(createdDateProperties, lastModifiedDateProperties);

		if (CollectionUtils.isNotEmpty(byProperties)) {
			for (AuditingMetadata.Property property : byProperties) {
				AuditorAware<?> auditorAware = findAuditorAware(property.getType());
				if (auditorAware != null) {
					Object currentAuditor = auditorAware.getCurrentAuditor();
					if (currentAuditor != null) {
						property.setValue(entity, currentAuditor);
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(dateProperties)) {
			Date now = new Date();
			for (AuditingMetadata.Property property : dateProperties) {
				property.setValue(entity, now);
			}
		}
	}

	/**
	 * 更新前处理
	 * 
	 * @param entity
	 *            实体对象
	 */
	@PreUpdate
	public void preUpdate(Object entity) {
		AuditingMetadata auditingMetadata = AuditingMetadata.getAuditingMetadata(entity.getClass());
		if (!auditingMetadata.isAuditable()) {
			return;
		}

		List<AuditingMetadata.Property> lastModifiedByProperties = auditingMetadata.getLastModifiedByProperties();
		List<AuditingMetadata.Property> lastModifiedDateProperties = auditingMetadata.getLastModifiedDateProperties();

		if (CollectionUtils.isNotEmpty(lastModifiedByProperties)) {
			for (AuditingMetadata.Property property : lastModifiedByProperties) {
				AuditorAware<?> auditorAware = findAuditorAware(property.getType());
				if (auditorAware != null) {
					Object currentAuditor = auditorAware.getCurrentAuditor();
					if (currentAuditor != null) {
						property.setValue(entity, currentAuditor);
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(lastModifiedDateProperties)) {
			Date now = new Date();
			for (AuditingMetadata.Property property : lastModifiedDateProperties) {
				property.setValue(entity, now);
			}
		}
	}

	/**
	 * 查找审计者Aware
	 * 
	 * @param auditorClass
	 *            审计者类型
	 * @return 审计者Aware，若不存在则返回null
	 */
	@SuppressWarnings("rawtypes")
	private AuditorAware<?> findAuditorAware(Class<?> auditorClass) {
		Assert.notNull(auditorClass);

		AuditorAware result = AUDITOR_AWARE_CACHE.get(auditorClass);
		if (result == null) {
			Map<String, AuditorAware> auditorAwareMap = SpringUtils.getBeansOfType(AuditorAware.class);
			if (auditorAwareMap != null) {
				for (AuditorAware<?> auditorAware : auditorAwareMap.values()) {
					ResolvableType resolvableType = ResolvableType.forClass(ClassUtils.getUserClass(auditorAware));
					Class<?> genericClass = resolvableType.as(AuditorAware.class).getGeneric().resolve();
					if (genericClass != null && auditorClass.isAssignableFrom(genericClass)) {
						result = auditorAware;
						AUDITOR_AWARE_CACHE.put(auditorClass, auditorAware);
						break;
					}
				}
			}
		}
		return result;
	}

}
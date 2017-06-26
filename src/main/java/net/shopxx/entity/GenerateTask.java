/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Entity
@Table(name = "xx_generate_task")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "seq_generate_task")
public class GenerateTask extends BaseEntity<Long> {

	private static final long serialVersionUID = 7714890675097577640L;

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 文章
		 */
		article,

		/**
		 * 货品
		 */
		goods
	}

	/**
	 * 状态
	 */
	public enum Status {

		/** 等待处理 */
		pending,

		/**
		 * 已处理
		 */
		processed
	}

	/** 类型 */
	private GenerateTask.Type type;

	/** 状态 */
	private GenerateTask.Status status;

	/** 目标ID */
	private Long targetId;

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@Column(nullable = false, updatable = false)
	public GenerateTask.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(GenerateTask.Type type) {
		this.type = type;
	}

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	@Column(nullable = false)
	public GenerateTask.Status getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(GenerateTask.Status status) {
		this.status = status;
	}

	/**
	 * 获取目标ID
	 * 
	 * @return 目标ID
	 */
	@Column(nullable = false, updatable = false)
	public Long getTargetId() {
		return targetId;
	}

	/**
	 * 设置目标ID
	 * 
	 * @param targetId
	 *            目标ID
	 */
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

}
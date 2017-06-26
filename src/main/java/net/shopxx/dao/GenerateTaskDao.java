/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import javax.persistence.LockModeType;

import net.shopxx.entity.GenerateTask;

/**
 * Dao - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface GenerateTaskDao extends BaseDao<GenerateTask, Long> {

	/**
	 * 判断静态生成任务是否存在
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param targetId
	 *            目标ID
	 * @return 静态生成任务是否存在
	 */
	boolean exists(GenerateTask.Type type, GenerateTask.Status status, Long targetId);

	/**
	 * 查找静态生成任务
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param lockModeType
	 *            锁定方式
	 * @return 静态生成任务
	 */
	List<GenerateTask> findList(GenerateTask.Type type, GenerateTask.Status status, Integer first, Integer count, LockModeType lockModeType);

	/**
	 * 移除静态生成任务
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 */
	void remove(GenerateTask.Type type, GenerateTask.Status status);

}
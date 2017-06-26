/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import net.shopxx.entity.GenerateTask;

/**
 * Service - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface GenerateTaskService extends BaseService<GenerateTask, Long> {

	/**
	 * 添加静态生成任务
	 * 
	 * @param type
	 *            类型
	 * @param targetId
	 *            目标ID
	 */
	void add(GenerateTask.Type type, Long targetId);

	/**
	 * 执行静态生成任务
	 * 
	 * @param type
	 *            类型
	 */
	void execute(GenerateTask.Type type);

	/**
	 * 删除静态生成任务
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 */
	void delete(GenerateTask.Type type, GenerateTask.Status status);

}
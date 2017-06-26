/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.job;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.shopxx.entity.GenerateTask;
import net.shopxx.service.GenerateTaskService;

/**
 * Job - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Lazy(false)
@Component("generateTaskJob")
public class GenerateTaskJob {

	@Resource(name = "generateTaskServiceImpl")
	private GenerateTaskService generateTaskService;

	/**
	 * 执行任务
	 */
	@Scheduled(fixedDelayString = "${job.generate_task_execute.delay}")
	public void execute() {
		generateTaskService.execute(GenerateTask.Type.article);
		generateTaskService.execute(GenerateTask.Type.goods);
	}

	/**
	 * 删除已处理任务
	 */
	@Scheduled(cron = "${job.generate_task_delete_processed.cron}")
	public void deleteProcessed() {
		generateTaskService.delete(null, GenerateTask.Status.processed);
	}

}
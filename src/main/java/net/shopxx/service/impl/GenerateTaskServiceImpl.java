/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import net.shopxx.dao.ArticleDao;
import net.shopxx.dao.GenerateTaskDao;
import net.shopxx.dao.GoodsDao;
import net.shopxx.entity.Article;
import net.shopxx.entity.GenerateTask;
import net.shopxx.entity.Goods;
import net.shopxx.service.GenerateTaskService;
import net.shopxx.service.StaticService;

/**
 * Service - 静态生成任务
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("generateTaskServiceImpl")
public class GenerateTaskServiceImpl extends BaseServiceImpl<GenerateTask, Long> implements GenerateTaskService {

	@Resource(name = "transactionTemplate")
	private TransactionTemplate transactionTemplate;
	@Resource(name = "readOnlyTransactionTemplate")
	private TransactionTemplate readOnlyTransactionTemplate;
	@Resource(name = "generateTaskDaoImpl")
	private GenerateTaskDao generateTaskDao;
	@Resource(name = "articleDaoImpl")
	private ArticleDao articleDao;
	@Resource(name = "goodsDaoImpl")
	private GoodsDao goodsDao;
	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	public void add(GenerateTask.Type type, Long targetId) {
		Assert.notNull(type);
		Assert.notNull(targetId);

		if (generateTaskDao.exists(type, GenerateTask.Status.pending, targetId)) {
			return;
		}
		GenerateTask generateTask = new GenerateTask();
		generateTask.setType(type);
		generateTask.setStatus(GenerateTask.Status.pending);
		generateTask.setTargetId(targetId);
		generateTaskDao.persist(generateTask);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void execute(final GenerateTask.Type type) {
		while (true) {
			final List<GenerateTask> generateTasks = transactionTemplate.execute(new TransactionCallback<List<GenerateTask>>() {

				@Override
				public List<GenerateTask> doInTransaction(TransactionStatus status) {
					List<GenerateTask> generateTasks = generateTaskDao.findList(type, GenerateTask.Status.pending, null, 100, LockModeType.PESSIMISTIC_WRITE);
					if (CollectionUtils.isNotEmpty(generateTasks)) {
						for (GenerateTask generateTask : generateTasks) {
							generateTask.setStatus(GenerateTask.Status.processed);
						}
						generateTaskDao.flush();
						generateTaskDao.clear();
					}
					return generateTasks;
				}

			});

			readOnlyTransactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (GenerateTask generateTask : generateTasks) {
						switch (generateTask.getType()) {
						case article:
							Article article = articleDao.find(generateTask.getTargetId());
							if (article != null) {
								staticService.generate(article);
							}
							break;
						case goods:
							Goods goods = goodsDao.find(generateTask.getTargetId());
							if (goods != null) {
								staticService.generate(goods);
							}
							break;
						}
					}
				}

			});

			if (generateTasks.size() < 100) {
				break;
			}
		}
	}

	public void delete(GenerateTask.Type type, GenerateTask.Status status) {
		generateTaskDao.remove(type, status);
	}

}
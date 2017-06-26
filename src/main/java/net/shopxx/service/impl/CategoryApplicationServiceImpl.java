/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.CategoryApplicationDao;
import net.shopxx.entity.CategoryApplication;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Store;
import net.shopxx.service.CategoryApplicationService;
import net.shopxx.service.GoodsService;

/**
 * Service - 经营分类申请
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("categoryApplicationServiceImpl")
public class CategoryApplicationServiceImpl extends BaseServiceImpl<CategoryApplication, Long> implements CategoryApplicationService {

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "categoryApplicationDaoImpl")
	private CategoryApplicationDao categoryApplicationDao;

	@Transactional(readOnly = true)
	public Page<CategoryApplication> findPage(Store store, Pageable pageable) {
		return categoryApplicationDao.findPage(store, pageable);
	}

	public void update(CategoryApplication categoryApplication, Set<ProductCategory> productCategorys, Store store) {
		Assert.notNull(categoryApplication);
		Assert.notNull(productCategorys);
		Assert.notNull(store);

		categoryApplication.setStatus(CategoryApplication.Status.approved);
		store.setProductCategorys(productCategorys);
	}

	public void review(CategoryApplication categoryApplication, Boolean isPassed) {
		Assert.notNull(categoryApplication);
		Assert.notNull(isPassed);
		if (isPassed) {
			Store store = categoryApplication.getStore();
			ProductCategory productCategory = categoryApplication.getProductCategory();
			Assert.notNull(store);
			Assert.notNull(productCategory);

			categoryApplication.setStatus(CategoryApplication.Status.approved);
			store.getProductCategorys().add(productCategory);
			Set<ProductCategory> productCategories = new HashSet<ProductCategory>();
			productCategories.add(productCategory);
			goodsService.setActive(store, productCategories);
		} else {
			categoryApplication.setStatus(CategoryApplication.Status.failed);
		}
	}

}
/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.Set;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.CategoryApplication;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Store;

/**
 * Service - 经营分类申请
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public interface CategoryApplicationService extends BaseService<CategoryApplication, Long> {

	/**
	 * 查找经营分类申请分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 经营分类申请分页
	 */
	Page<CategoryApplication> findPage(Store store, Pageable pageable);

	/**
	 * 更新经营分类申请
	 * 
	 * @param categoryApplication
	 *            经营分类申请
	 * @param productCategorys
	 *            经营分类申请
	 * @param store
	 *            商铺
	 */
	void update(CategoryApplication categoryApplication, Set<ProductCategory> productCategorys, Store store);

	/**
	 * 审核经营分类申请
	 * 
	 * @param categoryApplication
	 *            经营分类申请
	 * @param isPassed
	 *            是否审核通过
	 */
	void review(CategoryApplication categoryApplication, Boolean isPassed);

}
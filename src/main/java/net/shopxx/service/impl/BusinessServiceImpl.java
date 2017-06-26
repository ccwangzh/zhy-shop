/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.entity.Business;
import net.shopxx.entity.Member;
import net.shopxx.service.BusinessService;
import net.shopxx.service.MemberService;

/**
 * Service - 商家
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("businessServiceImpl")
public class BusinessServiceImpl extends BaseServiceImpl<Business, Long> implements BusinessService {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Transactional(readOnly = true)
	public Business getCurrent() {
		Member member = memberService.getCurrent(false);
		Business business = member != null && member.getBusiness() != null ? member.getBusiness() : null;
		if(business!=null){
			business.setLockKey(member.getLockKey());
		}
		return business;
	}

}
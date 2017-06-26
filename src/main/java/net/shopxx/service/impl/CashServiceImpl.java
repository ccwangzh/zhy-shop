/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.CashDao;
import net.shopxx.entity.Business;
import net.shopxx.entity.Cash;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Member;
import net.shopxx.entity.Operator;
import net.shopxx.service.CashService;
import net.shopxx.service.MemberService;

/**
 * Service - 提现
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("cashServiceImpl")
public class CashServiceImpl extends BaseServiceImpl<Cash, Long> implements CashService {

	@Resource(name = "cashDaoImpl")
	private CashDao cashDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Transactional(readOnly = true)
	public Page<Cash> findPage(Business business, Pageable pageable) {
		return cashDao.findPage(business, pageable);
	}

	public void applyCash(Cash cash, Business business) {
		Assert.notNull(cash);
		Assert.notNull(business);
		Assert.isTrue(cash.getAmount().compareTo(BigDecimal.ZERO) > 0);

		Operator operator = new Operator(business);
		cash.setStatus(Cash.Status.pending);
		cash.setOperator(operator);
		cash.setBusiness(business);
		cashDao.persist(cash);

		memberService.addBalance(cash.getBusiness().getMember(), cash.getAmount().negate(), DepositLog.Type.cash, operator, null);

	}

	public void review(Cash cash, Boolean isPassed, Operator operator) {
		Assert.notNull(cash);
		Assert.notNull(isPassed);
		Assert.notNull(operator);

		Member member = cash.getBusiness().getMember();
		if (isPassed) {
			Assert.notNull(cash.getAmount());
			Assert.notNull(cash.getBusiness());
			Assert.notNull(cash.getBusiness().getMember());
			member.setFrozenFunds(member.getFrozenFunds().subtract(cash.getAmount()));
			cash.setStatus(Cash.Status.approved);
		} else {
			cash.setStatus(Cash.Status.failed);
			memberService.addBalance(cash.getBusiness().getMember(), cash.getAmount(), DepositLog.Type.unfrozen, operator, null);
		}
	}

}
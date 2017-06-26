/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.BusinessDao;
import net.shopxx.dao.SnDao;
import net.shopxx.dao.StoreDao;
import net.shopxx.entity.AdvertisingImage;
import net.shopxx.entity.Business;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Member;
import net.shopxx.entity.Operator;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.entity.PlatformSvc;
import net.shopxx.entity.PromotionPluginSvc;
import net.shopxx.entity.Sn;
import net.shopxx.entity.Store;
import net.shopxx.entity.Store.Status;
import net.shopxx.entity.Store.Type;
import net.shopxx.entity.Svc;
import net.shopxx.service.AdminService;
import net.shopxx.service.BusinessService;
import net.shopxx.service.GoodsService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberService;
import net.shopxx.service.SmsService;
import net.shopxx.service.StaticService;
import net.shopxx.service.StoreService;

/**
 * Service - 店铺
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Service("storeServiceImpl")
public class StoreServiceImpl extends BaseServiceImpl<Store, Long> implements StoreService {

	@Resource(name = "storeDaoImpl")
	private StoreDao storeDao;
	@Resource(name = "businessDaoImpl")
	private BusinessDao businessDao;
	@Resource(name = "snDaoImpl")
	private SnDao snDao;
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "mailServiceImpl")
	private MailService mailService;
	@Resource(name = "smsServiceImpl")
	private SmsService smsService;
	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "adminServiceImpl")
	private AdminService adminService;
	@Resource(name = "staticServiceImpl")
	private StaticService staticService;

	@Transactional(readOnly = true)
	public Store findBySn(String sn) {
		return storeDao.findBySn(sn);
	}

	@Transactional(readOnly = true)
	public boolean nameExists(String name) {
		return storeDao.nameExists(name);
	}

	@Transactional(readOnly = true)
	public boolean mobileExists(String mobile) {
		return storeDao.mobileExists(mobile);
	}

	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return storeDao.emailExists(email);
	}

	@Transactional(readOnly = true)
	public Store findByName(String name) {
		return storeDao.findByName(name);
	}

	@Transactional(readOnly = true)
	public List<Store> findList(Type type, Status status, Boolean isEnabled, Integer first, Integer count) {
		return storeDao.findList(type, status, isEnabled, first, count);
	}

	@Transactional(readOnly = true)
	public Page<Store> findPage(Store.Type type, Store.Status status, Boolean isEnabled, Pageable pageable) {
		return storeDao.findPage(type, status, isEnabled, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Store> findPage(Member member, Pageable pageable) {
		return storeDao.findPage(member, pageable);
	}

	public void register(Store store, Business business, Boolean automaticReview) {
		Assert.notNull(store.getType());
		Assert.notNull(store);
		Assert.notNull(business);
		Assert.notNull(automaticReview);
		Assert.isTrue(store.isNew());
		Assert.isTrue(business.isNew());

		switch (store.getType()) {
		case general:
			Assert.notNull(store.getStoreRank());
			Assert.notNull(store.getStoreCategory());
			store.setBeginDate(null);
			store.setStatus(Store.Status.pending);
			break;
		case self:
			store.setStatus(Store.Status.success);
			store.setBeginDate(new Date());
			store.setStoreCategory(null);
			break;
		}

		store.setSn(snDao.generate(Sn.Type.store));
		store.setLogo(null);
		store.setAddress(null);
		store.setZipCode(null);
		store.setPhone(null);
		store.setIntroduction(null);
		store.setKeyword(null);
		store.setSeoTitle(null);
		store.setSeoKeywords(null);
		store.setSeoDescription(null);
		store.setEndDate(null);
		store.setBail(BigDecimal.ZERO);
		store.setBailPaid(BigDecimal.ZERO);
		store.setBusiness(business);
		store.setStoreProductCategorys(null);
		store.setCategoryApplications(null);
		store.setCoupons(null);
		store.setPromotions(null);
		store.setOrder(null);
		store.setGoods(null);
		store.setFavoriteMembers(null);
		store.setDeliveryTemplates(null);
		store.setDeliveryCenters(null);
		store.setSvcs(null);
		store.setPaymentTransactions(null);
		store.setStatistics(null);
		businessDao.persist(business);
		storeDao.persist(store);

		if (automaticReview) {
			review(store, true, null);
		}
	}

	public void update(Store store, Business business) {
		Assert.isTrue(!store.isNew());
		Assert.isTrue(!business.isNew());

		Store pStore = storeDao.find(store.getId());
		copyProperties(store, pStore, "type", "status", "sn", "beginDate", "bail", "bailPaid", "business", "storeProductCategorys", "categoryApplications", "coupons", "promotions", "orders", "goods", "favoriteMembers", "deliveryTemplates", "deliveryCenters", "svcs", "paymentTransactions",
				"statistics");
		goodsService.setActive(pStore, null);
	}

	@Transactional(readOnly = true)
	public Store getCurrent() {
		Business business = businessService.getCurrent();
		return business != null && business.getStore() != null ? business.getStore() : null;
	}

	public void review(Store store, Boolean passed, String content) {
		Assert.notNull(store);
		Assert.notNull(passed);
		if (passed) {
			store.setStatus(Store.Status.approved);
			store.setBail(store.getStoreCategory() != null ? store.getStoreCategory().getBail() : BigDecimal.ZERO);
			smsService.sendApprovalStoreSms(store);
			mailService.sendApprovalStoreMail(store);
		} else if (!passed && !StringUtils.isEmpty(content)) {
			store.setStatus(Store.Status.failed);
			smsService.sendFailStoreSms(store, content);
			mailService.sendFailStoreMail(store, content);
		}
	}

	public void bailPayment(Store store, BigDecimal bail) {
		Assert.notNull(store);

		if (bail.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(storeDao.getLockMode(store))) {
			storeDao.refresh(store, LockModeType.PESSIMISTIC_WRITE);
		}
		Assert.notNull(store.getBail());
		Assert.state(store.getBail().add(bail).compareTo(BigDecimal.ZERO) >= 0);
		store.setBail(store.getBail().add(bail));
		storeDao.flush();
	}

	public void expiredStoreProcessing() {
		for (int i = 0;; i += 100) {
			List<Store> storeList = storeDao.findList(i, 100);
			if (CollectionUtils.isNotEmpty(storeList)) {
				for (Store store : storeList) {
					goodsService.setActive(store, null);
				}
			}
			storeDao.flush();
			storeDao.clear();
			if (storeList.size() < 100) {
				break;
			}
		}
	}

	public void storeRefresh(Store store, PaymentTransaction paymentTransaction) {
		Assert.notNull(store);
		Assert.notNull(store.getStatus());
		Assert.notNull(paymentTransaction);
		Assert.notNull(paymentTransaction.getType());

		BigDecimal effectiveAmount = paymentTransaction.getEffectiveAmount();
		switch (paymentTransaction.getType()) {
		case BAIL_PAYMENT:
			store.setBailPaid(effectiveAmount);
			Member member = store.getBusiness() != null ? store.getBusiness().getMember() : null;
			memberService.addBalance(member, effectiveAmount, DepositLog.Type.frozen, new Operator(member), null);
			break;
		case SVC_PAYMENT:
			Svc svc = paymentTransaction.getSvc();
			if (svc != null) {
				Integer durationDays = svc.getDurationDays();
				if (svc instanceof PlatformSvc) {
					store.setEndDate(DateUtils.addDays(store.getEndDate() != null ? store.getEndDate() : new Date(), durationDays));
				} else if (svc instanceof PromotionPluginSvc) {
					String promotionPluginId = ((PromotionPluginSvc) svc).getPromotionPluginId();
					if (promotionPluginId.equals("discount")) {
						store.setDiscount(DateUtils.addDays(store.getDiscount() != null ? store.getDiscount() : new Date(), durationDays));
					} else if (promotionPluginId.equals("fullReduction")) {
						store.setFullReduction(DateUtils.addDays(store.getFullReduction() != null ? store.getFullReduction() : new Date(), durationDays));
					}
				}
			}
			break;
		default:
			break;
		}

		if (Store.Status.approved.equals(store.getStatus()) && store.getBailPaid().compareTo(BigDecimal.ZERO) > 0 && !store.getHasExpired()) {
			store.setStatus(Store.Status.success);
			store.setBeginDate(new Date());
			store.setIsEnabled(true);
		}
		storeDao.flush();
	}

	public void open(Store store) {
		store.setStatus(Store.Status.success);
		store.setBailPaid(store.getBailPaid());
		store.setBeginDate(new Date());
		store.setEndDate(null);
		store.setIsEnabled(true);
	}

	public void open(Store store, Member member, Integer year, BigDecimal serviceFee, BigDecimal bail) {
		Assert.notNull(store);
		Assert.notNull(member);
		Assert.notNull(serviceFee);
		if (serviceFee.compareTo(BigDecimal.ZERO) > 0) {
			Assert.notNull(year);
		}
		Assert.notNull(bail);
		Assert.isTrue(member.getBalance() != null && member.getBalance().compareTo(serviceFee.add(bail)) >= 0);

		store.setStatus(Store.Status.success);
		store.setBailPaid(bail.add(store.getBailPaid()));
		store.setBeginDate(new Date());
		store.setEndDate(serviceFee.compareTo(BigDecimal.ZERO) > 0 ? DateUtils.addDays(new Date(), year * 365) : null);
		store.setIsEnabled(true);

		memberService.addBalance(member, bail.negate(), DepositLog.Type.frozen, new Operator(member), null);
		memberService.addBalance(member, serviceFee.negate(), DepositLog.Type.svcPayment, new Operator(member), null);
	}

	public void buyPromotionPlugin(Store store, String promotionPluginId, Integer year, BigDecimal pluginPrice) {
		Assert.notNull(store);
		Assert.notNull(promotionPluginId);
		Assert.state(year > 0);
		Assert.state(pluginPrice.compareTo(BigDecimal.ZERO) >= 0);
		BigDecimal price = pluginPrice.multiply(new BigDecimal(year));
		Member member = store.getBusiness().getMember();
		Assert.isTrue(member.getBalance() != null && member.getBalance().compareTo(price) > 0);

		if (promotionPluginId.equals("discount")) {
			store.setDiscount(DateUtils.addDays(store.getDiscount() != null ? store.getDiscount() : new Date(), 30 * year));
		} else if (promotionPluginId.equals("fullReduction")) {
			store.setFullReduction(DateUtils.addDays(store.getFullReduction() != null ? store.getFullReduction() : new Date(), 30 * year));
		}

		memberService.addBalance(member, price.negate(), DepositLog.Type.svcPayment, new Operator(store.getBusiness()), null);
	}

	public void filter(List<AdvertisingImage> advertisingImages) {
		CollectionUtils.filter(advertisingImages, new Predicate() {
			public boolean evaluate(Object object) {
				AdvertisingImage advertisingImage = (AdvertisingImage) object;
				return advertisingImage != null && !advertisingImage.isEmpty();
			}
		});
	}

	@Override
	@Transactional
	public Store save(Store store) {
		Assert.notNull(store);

		store.setSn(snDao.generate(Sn.Type.store));
		return super.save(store);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "goods", "productCategory" }, allEntries = true)
	public void delete(Long... ids) {
		Assert.notEmpty(ids);

		for (Long id : ids) {
			Store store = storeDao.find(id);
			memberService.addBalance(store.getBusiness().getMember(), store.getBail(), DepositLog.Type.returnBail, new Operator(adminService.getCurrent()), null);
			for (Goods goods : store.getGoods()) {
				staticService.delete(goods);
			}
			super.delete(id);
		}
	}
}
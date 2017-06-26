/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Goods;
import net.shopxx.entity.ProductNotify;
import net.shopxx.entity.Store;
import net.shopxx.service.ProductNotifyService;
import net.shopxx.service.StoreService;

/**
 * Controller - 到货通知
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessProductNotifyntroller")
@RequestMapping("/business/product_notify")
public class ProductNotifyController extends BaseController {

	@Resource(name = "productNotifyServiceImpl")
	private ProductNotifyService productNotifyService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	/**
	 * 发送到货通知
	 */
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public @ResponseBody Message send(Long[] ids) {
		if (ids == null) {
			return ERROR_MESSAGE;
		}
		List<ProductNotify> productNotifies = productNotifyService.findList(ids);
		for (ProductNotify productNotify : productNotifies) {
			if (productNotify == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!productNotify.getStore().equals(store)) {
				return ERROR_MESSAGE;
			}
		}
		int count = productNotifyService.send(productNotifies);
		return Message.success("shop.business.productNotify.sentSuccess", count);
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Goods.Status status, Boolean isOutOfStock, Boolean hasSent, Pageable pageable, ModelMap model) {
	    Store store = storeService.getCurrent();
	    Goods.Status[] statusVal = null;
        if(store.isSelf()){
            statusVal = new Goods.Status[]{Goods.Status.offTheshelf,Goods.Status.onTheshelf};
        }else{
            statusVal = Goods.Status.values();
        }
        model.addAttribute("statusVal", statusVal);
		model.addAttribute("status", status);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("hasSent", hasSent);
		model.addAttribute("page", productNotifyService.findPage(storeService.getCurrent(), null, status, isOutOfStock, hasSent, pageable));
		return "/shop/${theme}/business/product_notify/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		if (ids == null) {
			return ERROR_MESSAGE;
		}
		for (Long id : ids) {
			ProductNotify productNotify = productNotifyService.find(id);
			if (productNotify == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!productNotify.getStore().equals(store)) {
				return ERROR_MESSAGE;
			}
			productNotifyService.delete(id);
		}
		return SUCCESS_MESSAGE;
	}
}
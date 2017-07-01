/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import net.shopxx.entity.*;
import net.shopxx.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.service.BrandService;
import net.shopxx.service.GoodsService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.TagService;
import net.shopxx.util.SpringUtils;

/**
 * Controller - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("adminGoodsController")
@RequestMapping("/admin/goods")
public class GoodsController extends BaseController {

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "brandServiceImpl")
	private BrandService brandService;
	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Goods.Type type, Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isActive, Goods.Status status, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert, Pageable pageable, ModelMap model) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Tag tag = tagService.find(tagId);
		model.addAttribute("types", Goods.Type.values());
		model.addAttribute("statusVal", Goods.Status.values());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findAll());
		model.addAttribute("tags", tagService.findList(Tag.Type.goods));
		model.addAttribute("type", type);
		model.addAttribute("productCategoryId", productCategoryId);
		model.addAttribute("brandId", brandId);
		model.addAttribute("promotionId", promotionId);
		model.addAttribute("tagId", tagId);
		model.addAttribute("isActive", isActive);
		model.addAttribute("status", status);
		model.addAttribute("isList", isList);
		model.addAttribute("isTop", isTop);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("isStockAlert", isStockAlert);
		model.addAttribute("page", goodsService.findPage(null, type, productCategory, brand, promotion, tag, null, null, null, isActive, status, isList, isTop, isOutOfStock, isStockAlert, null, null, pageable));
		return "/admin/goods/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		goodsService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 上架商品
	 */
	@RequestMapping(value = "/shelves", method = RequestMethod.POST)
	public @ResponseBody Message shelves(Long[] ids) {
		if (ids == null) {
			return ERROR_MESSAGE;
		}
		int count = 0;
		for (Long id : ids) {
			if (!goodsService.exists(id)) {
				return ERROR_MESSAGE;
			}
			Goods goods = goodsService.find(id);
			if(goods.getStatus()!=Goods.Status.offTheshelf && goods.getStatus()!=Goods.Status.onTheshelf){
                count ++;
                continue;
            }
			if (goods.getStore().getHasExpired() || !goods.getStore().getIsEnabled()) {
				return Message.error("admin.goods.isShelvesProduct", ERROR_MESSAGE);
			}
			if(goods.getStatus()==Goods.Status.offTheshelf){
                goods.setStatus(Goods.Status.onTheshelf);
                goodsService.update(goods);
            }
		}
		if(count!=0){
            String msg = SpringUtils.getMessage("admin.goods.isNotMarketable", count);
            return Message.warn(msg, ERROR_MESSAGE);
        }
		return SUCCESS_MESSAGE;
	}

	/**
	 * 下架商品
	 */
	@RequestMapping(value = "/shelf", method = RequestMethod.POST)
	public @ResponseBody Message shelf(Long[] ids) {
		if (ids == null) {
			return ERROR_MESSAGE;
		}
		for (Long id : ids) {
			if (!goodsService.exists(id)) {
				return ERROR_MESSAGE;
			}
			Goods goods = goodsService.find(id);
			if(goods.getStatus()!=Goods.Status.offTheshelf && goods.getStatus()!=Goods.Status.onTheshelf){
			    continue;
            }
            if(goods.getStatus()==Goods.Status.onTheshelf){
                goods.setStatus(Goods.Status.offTheshelf);
                goodsService.update(goods);
            }
		}
		return SUCCESS_MESSAGE;
	}
	
	
	 /**
     * 审核商品
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String check(@RequestParam(value = "id", required = true)Long id, ModelMap model) {
        if (!goodsService.exists(id)) {
            return ERROR_VIEW;
        }
        Goods goods = goodsService.find(id);
        
        model.addAttribute("comments", goodsService.getApprovalComments(goods.getId()));
        model.addAttribute("goods", goods);
        return "/admin/goods/check";
    }
    
    /**
    * 审核
    */
   @RequestMapping(value = "/checkResult", method = RequestMethod.POST)
   public String checkResult(@RequestParam(value = "id", required = true)Long id,
           @RequestParam(value = "result", required = true)Boolean result,
           String comment) {
       if (!goodsService.exists(id)) {
           return ERROR_VIEW;
       }
       Goods goods = goodsService.find(id);
       if(result){
           goods.setStatus(Goods.Status.offTheshelf);
       }else{
           goods.setStatus(Goods.Status.failed);
       }
       if(StringUtils.isNotEmpty(comment)){
           goodsService.addApprovalComment(goods.getId(), comment);
       }
       goodsService.update(goods);
       return "redirect:list.jhtml";
   }

	/**
	 * 通用协议
	 */
	@RequestMapping(value = "/commomAgreement", method = RequestMethod.GET)
	public String commomAgreement(ModelMap model) {
        GoodsCommonAgreement agreement = goodsService.getCommonAgreement();
        model.addAttribute("agreement",agreement);
		return "/admin/goods/commomAgreement";
	}

	/**
	 * 保存通用协议
	 */
	@RequestMapping(value = "/saveCommomAgreement", method = RequestMethod.POST)
	public @ResponseBody Message saveCommomAgreement(GoodsCommonAgreement agreement) {
        if (agreement==null) {
            return ERROR_MESSAGE;
        }
        
        if(agreement.getId()!=null){
        	GoodsCommonAgreement model = goodsService.getCommonAgreement(agreement.getId());
        	model.setContent(agreement.getContent());
        	model.setTitle(agreement.getTitle());
        	agreement = model;
        }
        goodsService.saveCommonAgreement(agreement);
        return SUCCESS_MESSAGE;
    }

	/**
	 * 交易商品列表
	 */
	@RequestMapping(value = "/listTradeGoods", method = RequestMethod.GET)
	public String listTradeGoods(ModelMap model) {
		model.addAttribute("page", goodsService.findTradeGoodsList(null,null));
		return "/admin/goods/listTradeGoods";
	}

    /**
     * 添加交易商品
     */
    @RequestMapping(value = "/addTradeGoods", method = RequestMethod.GET)
    public String addTradeGoods() {
        return "/admin/goods/addTradeGoods";
    }

    /**
     * 保存交易商品
     */
    @RequestMapping(value = "/saveTradeGoods", method = RequestMethod.POST)
    public String saveTradeGoods(TradeGoods tradeGoods) {
        if(StringUtil.isEmpty(tradeGoods.getName())){
            return ERROR_VIEW;
        }
        if(tradeGoods.getOrder() == null){
            return ERROR_VIEW;
        }
        if(tradeGoods.getIsEnable()==null){
            tradeGoods.setIsEnable(false);
        }
        goodsService.saveTradeGoods(tradeGoods);
        return "redirect:listTradeGoods.jhtml";
    }

    /**
     * 编辑交易商品
     */
    @RequestMapping(value = "/editTradeGoods", method = RequestMethod.GET)
    public String editTradeGoods(Long id,ModelMap model) {
        if(id==null){
            return ERROR_VIEW;
        }
        TradeGoods tradeGoods =goodsService.findTradeGoodsById(id);
        model.addAttribute("tradeGoods", tradeGoods);
        return "/admin/goods/editTradeGoods";
    }

    /**
     * 删除交易商品
     */
    @RequestMapping(value = "/deleteTradeGoods", method = RequestMethod.POST)
    public @ResponseBody Message deleteTradeGoods(Long id) {
        if(id==null){
            return ERROR_MESSAGE;
        }
        TradeGoods tradeGoods =goodsService.findTradeGoodsById(id);
        if(tradeGoods==null){
            return ERROR_MESSAGE;
        }
        goodsService.deleteTradeGoods(id);
        return SUCCESS_MESSAGE;
    }

    /**
     * 更新交易商品
     */
    @RequestMapping(value = "/updateTradeGoods", method = RequestMethod.POST)
    public String updateTradeGoods(TradeGoods tradeGoods) {
        if(tradeGoods.getIsEnable()==null){
            tradeGoods.setIsEnable(false);
        }
        TradeGoods relationObj = goodsService.findTradeGoodsById(tradeGoods.getId());
        try {
            BeanUtils.copyProperties(relationObj,tradeGoods);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if(tradeGoods==null){
            return ERROR_VIEW;
        }
        goodsService.saveTradeGoods(relationObj);

        return "redirect:listTradeGoods.jhtml";
    }

}
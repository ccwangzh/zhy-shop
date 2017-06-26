/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.controller.shop.BaseController;
import net.shopxx.entity.Attribute;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Business;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Operator;
import net.shopxx.entity.Parameter;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Specification;
import net.shopxx.entity.Store;
import net.shopxx.entity.StoreProductCategory;
import net.shopxx.entity.StoreTag;
import net.shopxx.entity.Tag;
import net.shopxx.service.AttributeService;
import net.shopxx.service.BrandService;
import net.shopxx.service.BusinessService;
import net.shopxx.service.GoodsService;
import net.shopxx.service.ParameterValueService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.ProductImageService;
import net.shopxx.service.ProductService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.SpecificationItemService;
import net.shopxx.service.SpecificationService;
import net.shopxx.service.StoreProductCategoryService;
import net.shopxx.service.StoreService;
import net.shopxx.service.StoreTagService;
import net.shopxx.service.TagService;
import net.shopxx.util.SpringUtils;

/**
 * Controller - 商家中心 - 货品
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopBusinessGoodsController")
@RequestMapping("/business/goods")
public class GoodsController extends BaseController {

	/** 最大对比货品数 */
	public static final Integer MAX_COMPARE_GOODS_COUNT = 4;

	/** 最大浏览记录货品数 */
	public static final Integer MAX_HISTORY_GOODS_COUNT = 10;

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;
	@Resource(name = "brandServiceImpl")
	private BrandService brandService;
	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;
	@Resource(name = "storeTagServiceImpl")
	private StoreTagService storeTagService;
	@Resource(name = "tagServiceImpl")
	private TagService tagService;
	@Resource(name = "productImageServiceImpl")
	private ProductImageService productImageService;
	@Resource(name = "parameterValueServiceImpl")
	private ParameterValueService parameterValueService;
	@Resource(name = "specificationItemServiceImpl")
	private SpecificationItemService specificationItemService;
	@Resource(name = "attributeServiceImpl")
	private AttributeService attributeService;
	@Resource(name = "specificationServiceImpl")
	private SpecificationService specificationService;
	@Resource(name = "businessServiceImpl")
	private BusinessService businessService;
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	@Resource(name = "storeProductCategoryServiceImpl")
	private StoreProductCategoryService storeProductCategoryService;

	/**
	 * 检查编号是否存在
	 */
	@RequestMapping(value = "/check_sn", method = RequestMethod.GET)
	public @ResponseBody boolean checkSn(String sn) {
		if (StringUtils.isEmpty(sn)) {
			return false;
		}
		return !goodsService.snExists(sn);
	}

	/**
	 * 检查店铺商品发布数量是否已达上限
	 */
	@RequestMapping(value = "/check_store_goods", method = RequestMethod.GET)
	public @ResponseBody boolean checkStoreGoods() {
		Store store = storeService.getCurrent();
		Long goodsCount = goodsService.count(null, store, null, null, null, null, null, null, null);
		if (store.getStoreRank() != null && store.getStoreRank().getQuantity() != null && goodsCount >= store.getStoreRank().getQuantity()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取参数
	 */
	@RequestMapping(value = "/parameters", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> parameters(Long productCategoryId) {
		Store store = storeService.getCurrent();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getParameters()) || !productCategory.getStores().contains(store)) {
			return data;
		}
		for (Parameter parameter : productCategory.getParameters()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("group", parameter.getGroup());
			item.put("names", parameter.getNames());
			data.add(item);
		}
		return data;
	}

	/**
	 * 获取属性
	 */
	@RequestMapping(value = "/attributes", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> attributes(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Store store = storeService.getCurrent();
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getAttributes()) || !productCategory.getStores().contains(store)) {
			return data;
		}
		for (Attribute attribute : productCategory.getAttributes()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", attribute.getId());
			item.put("name", attribute.getName());
			item.put("options", attribute.getOptions());
			data.add(item);
		}
		return data;
	}

	/**
	 * 获取规格
	 */
	@RequestMapping(value = "/specifications", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> specifications(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Store store = storeService.getCurrent();
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getSpecifications()) || !store.getProductCategorys().contains(productCategory)) {
			return data;
		}
		for (Specification specification : productCategory.getSpecifications()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("name", specification.getName());
			item.put("options", specification.getOptions());
			data.add(item);
		}
		return data;
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
		Store store = storeService.getCurrent();
		Long goodsCount = goodsService.count(null, store, null, null, null, null, null, null, null);
		if (store.getStoreRank() != null && store.getStoreRank().getQuantity() != null && goodsCount >= store.getStoreRank().getQuantity()) {
			return ERROR_VIEW;
		}
		model.addAttribute("store", store);
		model.addAttribute("productCategoryTree", productCategoryService.findTree(store));
		model.addAttribute("types", Goods.Type.values());
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findList(store, null, true));
		model.addAttribute("storeTags", storeTagService.findList(store, null));
		model.addAttribute("tags", tagService.findList(Tag.Type.goods));
		model.addAttribute("specifications", specificationService.findAll());
		return "/shop/${theme}/business/goods/add";
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Goods goods, ProductForm productForm, ProductListForm productListForm, Long productCategoryId, Long brandId, Long[] promotionIds, Long[] storeTagIds, Long[] tagIds, Long storeProductCategoryId, HttpServletRequest request, RedirectAttributes redirectAttributes,String action) {
	    productImageService.filter(goods.getProductImages());
		parameterValueService.filter(goods.getParameterValues());
		specificationItemService.filter(goods.getSpecificationItems());
		productService.filter(productListForm.getProductList());
		
		Store store = storeService.getCurrent();
        if(store.isSelf()){
            if("save".equals(action)){
                goods.setStatus(Goods.Status.offTheshelf);//下架状态
            }
            if("marketable".equals(action)){//自营店铺直接上架，不需要审核
                goods.setStatus(Goods.Status.onTheshelf);
            }
        }else{
            if("save".equals(action)){
                goods.setStatus(Goods.Status.unCheck);
            }
            if("sendCheck".equals(action)){
                goods.setStatus(Goods.Status.pendingCheck);
            }
        }
        
		Long goodsCount = goodsService.count(null, store, null, null, null, null, null, null, null);
		if (store.getStoreRank() != null && store.getStoreRank().getQuantity() != null && goodsCount >= store.getStoreRank().getQuantity()) {
			return ERROR_VIEW;
		}
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || !productCategory.getStores().contains(store)) {
			return ERROR_VIEW;
		}
		if (storeProductCategoryId != null) {
			StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
			if (storeProductCategory == null || !store.equals(storeProductCategory.getStore())) {
				return ERROR_VIEW;
			}
			goods.setStoreProductCategory(storeProductCategory);
		}
		goods.setStore(store);
		goods.setProductCategory(productCategory);
		goods.setBrand(brandService.find(brandId));
		goods.setPromotions(new HashSet<Promotion>(promotionService.findList(promotionIds)));
		goods.setStoreTags(new HashSet<StoreTag>(storeTagService.findList(storeTagIds)));
		goods.setTags(new HashSet<Tag>(tagService.findList(tagIds)));

		goods.removeAttributeValue();
		for (Attribute attribute : goods.getProductCategory().getAttributes()) {
			String value = request.getParameter("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			goods.setAttributeValue(attribute, attributeValue);
		}

		if (!isValid(goods, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(goods.getSn()) && goodsService.snExists(goods.getSn())) {
			return ERROR_VIEW;
		}

		Business business = businessService.getCurrent();
		if (business == null) {
			return ERROR_VIEW;
		}
		if (goods.hasSpecification()) {
			List<Product> products = productListForm.getProductList();
			if (CollectionUtils.isEmpty(products) || !isValid(products, getValidationGroup(goods.getType()), BaseEntity.Save.class)) {
				return ERROR_VIEW;
			}
			goodsService.save(goods, products, business, new Operator(business));
		} else {
			Product product = productForm.getProduct();
			if (product == null || !isValid(product, getValidationGroup(goods.getType()), BaseEntity.Save.class)) {
				return ERROR_VIEW;
			}
			goodsService.save(goods, product, business, new Operator(business));
		}

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(String sn, ModelMap model) {
		Store store = storeService.getCurrent();
		Goods goods = goodsService.findBySn(sn);
		
		model.addAttribute("isPendingCheck", goods.getStatus()==Goods.Status.pendingCheck);
		model.addAttribute("comments", goodsService.getApprovalComments(goods.getId()));
		model.addAttribute("types", Goods.Type.values());
		model.addAttribute("store", store);
		model.addAttribute("productCategoryTree", productCategoryService.findTree(store));
		model.addAttribute("storeProductCategoryTree", storeProductCategoryService.findTree(storeService.getCurrent()));
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findList(store, null, true));
		model.addAttribute("storeTags", storeTagService.findList(store, null));
		model.addAttribute("tags", tagService.findList(Tag.Type.goods));
		model.addAttribute("specifications", specificationService.findAll());
		model.addAttribute("goods", goods);
		return "/shop/${theme}/business/goods/edit";
	}

	/**
	 * 更新
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(Goods goods, ProductForm productForm, ProductListForm productListForm, Long id, Long productCategoryId, Long brandId, Long[] promotionIds, Long[] storeTagIds, Long[] tagIds, Long storeProductCategoryId, HttpServletRequest request, RedirectAttributes redirectAttributes,String action,ModelMap model) {
		productImageService.filter(goods.getProductImages());
		parameterValueService.filter(goods.getParameterValues());
		specificationItemService.filter(goods.getSpecificationItems());
		productService.filter(productListForm.getProductList());
		Store store = storeService.getCurrent();
		
		if(goods.getStatus()==Goods.Status.pendingCheck){//等待审核状态不可编辑
		    model.addAttribute("errorMessage", message("shop.business.goods.noEdit"));
		    return ERROR_VIEW;
		}
		if(!store.isSelf()){
            if("save".equals(action)){
                goods.setStatus(Goods.Status.unCheck);//非自营商品编辑后，重置为未审核状态
            }
            if("sendCheck".equals(action)){
                goods.setStatus(Goods.Status.pendingCheck);
            }
        }
		Business business = store.getBusiness();
		Goods pGoods = goodsService.find(id);
		if (pGoods == null || !store.equals(pGoods.getStore())) {
			return ERROR_VIEW;
		}
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || !productCategory.getStores().contains(store)) {
			return ERROR_VIEW;
		}
		List<Promotion> promotions = promotionService.findList(promotionIds);
		if (CollectionUtils.isNotEmpty(promotions)) {
			if (store.getPromotions() == null || !store.getPromotions().containsAll(promotions)) {
				return ERROR_VIEW;
			}
		}
		if (storeProductCategoryId != null) {
			StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
			if (storeProductCategory == null || !store.equals(storeProductCategory.getStore())) {
				return ERROR_VIEW;
			}
			goods.setStoreProductCategory(storeProductCategory);
		}
		goods.setType(pGoods.getType());
		goods.setProductCategory(productCategory);
		goods.setBrand(brandService.find(brandId));
		goods.setPromotions(new HashSet<Promotion>(promotions));
		goods.setStoreTags(new HashSet<StoreTag>(storeTagService.findList(storeTagIds)));
		goods.setTags(new HashSet<Tag>(tagService.findList(tagIds)));

		goods.removeAttributeValue();
		for (Attribute attribute : goods.getProductCategory().getAttributes()) {
			String value = request.getParameter("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			goods.setAttributeValue(attribute, attributeValue);
		}

		if (!isValid(goods, BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}

		if (goods.hasSpecification()) {
			List<Product> products = productListForm.getProductList();
			if (CollectionUtils.isEmpty(products) || !isValid(products, getValidationGroup(goods.getType()), BaseEntity.Update.class)) {
				return ERROR_VIEW;
			}
			goodsService.update(goods, products, new Operator(business));
		} else {
			Product product = productForm.getProduct();
			if (product == null || !isValid(product, getValidationGroup(goods.getType()), BaseEntity.Update.class)) {
				return ERROR_VIEW;
			}
			goodsService.update(goods, product, new Operator(business));
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Goods.Type type, Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isActive, Goods.Status status, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert, Pageable pageable, ModelMap model) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		Store store = storeService.getCurrent();
		if (productCategory != null) {
			if (!productCategory.getStores().contains(store)) {
				return ERROR_VIEW;
			}
		}
		if (promotion != null) {
			if (!store.equals(promotion.getStore())) {
				return ERROR_VIEW;
			}
		}
		Tag tag = tagService.find(tagId);
		model.addAttribute("types", Goods.Type.values());
		Goods.Status[] statusVal = null;
		if(store.isSelf()){
		    statusVal = new Goods.Status[]{Goods.Status.offTheshelf,Goods.Status.onTheshelf};
		}else{
		    statusVal = Goods.Status.values();
		}
		model.addAttribute("statusVal", statusVal);
		model.addAttribute("productCategoryTree", productCategoryService.findTree(store));
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findList(store, null, true));
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
		model.addAttribute("isStockAlert", isStockAlert);
		model.addAttribute("store", store);
		model.addAttribute("page", goodsService.findPage(store, type, productCategory, brand, promotion, tag, null, null, null, isActive, status, isList, isTop, isOutOfStock, isStockAlert, null, null, pageable));
		return "/shop/${theme}/business/goods/list";
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(Long[] ids) {
		for (Long id : ids) {
			Goods goods = goodsService.find(id);
			if (goods == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!store.equals(goods.getStore())) {
				return ERROR_MESSAGE;
			}
			goodsService.delete(goods.getId());
		}
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
			Goods goods = goodsService.find(id);
			if(goods.getStatus()!=Goods.Status.offTheshelf && goods.getStatus()!=Goods.Status.onTheshelf){
			    count ++;
			    continue;
			}
			if (goods == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!store.equals(goods.getStore())) {
				return ERROR_MESSAGE;
			}
			if (!store.getProductCategorys().contains(goods.getProductCategory())) {
				return Message.warn("shop.business.goods.isNotMarketable", ERROR_MESSAGE);
			}
			if(goods.getStatus()==Goods.Status.offTheshelf){
			    goods.setStatus(Goods.Status.onTheshelf);
			    goodsService.update(goods);
			}
		}
		if(count!=0){
		    String msg = SpringUtils.getMessage("shop.business.goods.isNotMarketable2", count);
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
			Goods goods = goodsService.find(id);
			if(goods.getStatus()!=Goods.Status.offTheshelf && goods.getStatus()!=Goods.Status.onTheshelf){
			    continue;
            }
			if (goods == null) {
				return ERROR_MESSAGE;
			}
			Store store = storeService.getCurrent();
			if (!store.equals(goods.getStore())) {
				return ERROR_MESSAGE;
			}
			if(goods.getStatus()==Goods.Status.onTheshelf){
			    goods.setStatus(Goods.Status.offTheshelf);
			    goodsService.update(goods);
			}
		}
		return SUCCESS_MESSAGE;
	}

	/**
	 * 根据类型获取验证组
	 * 
	 * @param type
	 *            类型
	 * @return 验证组
	 */
	private Class<?> getValidationGroup(Goods.Type type) {
		Assert.notNull(type);

		switch (type) {
		case general:
			return Product.General.class;
		case exchange:
			return Product.Exchange.class;
		case listedtrade:
			return Product.Listedtrade.class;
		case gift:
			return Product.Gift.class;
		}
		return null;
	}

	/**
	 * FormBean - 商品
	 * 
	 * @author SHOP++ Team
	 * @version 5.0
	 */
	public static class ProductForm {

		/** 商品 */
		private Product product;

		/**
		 * 获取商品
		 * 
		 * @return 商品
		 */
		public Product getProduct() {
			return product;
		}

		/**
		 * 设置商品
		 * 
		 * @param product
		 *            商品
		 */
		public void setProduct(Product product) {
			this.product = product;
		}

	}

	/**
	 * FormBean - 商品
	 * 
	 * @author SHOP++ Team
	 * @version 5.0
	 */
	public static class ProductListForm {

		/** 商品 */
		private List<Product> productList;

		/**
		 * 获取商品
		 * 
		 * @return 商品
		 */
		public List<Product> getProductList() {
			return productList;
		}

		/**
		 * 设置商品
		 * 
		 * @param productList
		 *            商品
		 */
		public void setProductList(List<Product> productList) {
			this.productList = productList;
		}

	}
}
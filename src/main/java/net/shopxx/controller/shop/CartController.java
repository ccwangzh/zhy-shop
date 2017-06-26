/*
 * Copyright 2005-2016 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.entity.Cart;
import net.shopxx.entity.CartItem;
import net.shopxx.entity.Goods;
import net.shopxx.entity.Member;
import net.shopxx.entity.Product;
import net.shopxx.entity.Store;
import net.shopxx.service.CartService;
import net.shopxx.service.MemberService;
import net.shopxx.service.ProductService;

/**
 * Controller - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
@Controller("shopCartController")
@RequestMapping("/cart")
public class CartController extends BaseController {

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	@Resource(name = "productServiceImpl")
	private ProductService productService;
	@Resource(name = "cartServiceImpl")
	private CartService cartService;

	/**
	 * 信息
	 */
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> info() {
		Map<String, Object> data = new HashMap<String, Object>();
		Cart currentCart = cartService.getCurrent();
		if (currentCart != null) {
			data.put("tag", currentCart.getTag());
			data.put("productQuantity", currentCart.getProductQuantity(null));
			data.put("effectivePrice", currentCart.getEffectivePrice(null));
			List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
			for (CartItem cartItem : currentCart) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("productId", cartItem.getProduct().getId());
				item.put("productName", cartItem.getProduct().getName());
				item.put("productThumbnail", cartItem.getProduct().getThumbnail());
				item.put("productUrl", cartItem.getProduct().getUrl());
				item.put("price", cartItem.getPrice());
				item.put("quantity", cartItem.getQuantity());
				item.put("subtotal", cartItem.getSubtotal());
				items.add(item);
			}
			data.put("items", items);
		}
		return data;
	}

	/**
	 * 添加
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody Message add(Long productId, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
		if (quantity == null || quantity < 1) {
			return ERROR_MESSAGE;
		}
		Product product = productService.find(productId);
		if(product.getGoods().getType().equals(Goods.Type.listedtrade)){
			return Message.warn("shop.cart.listedTradeNotAllowed");
		}
		if(!product.getGoods().getIsDelivery()){
			return Message.warn("shop.cart.virtualItemNotAllowed");
		}
		if (product == null) {
			return Message.warn("shop.cart.productNotExist");
		}
		if (!Goods.Type.general.equals(product.getType())) {
			return Message.warn("shop.cart.productNotForSale");
		}
		if (!product.getIsMarketable()) {
			return Message.warn("shop.cart.productNotMarketable");
		}

		Cart currentCart = cartService.getCurrent();
		int cartItemSize = 1;
		int productQuantity = quantity;
		Member member = memberService.getCurrent();
		if (member != null && member.getBusiness() != null && member.getBusiness().getStore() != null && member.getBusiness().getStore().hasContained(product)) {
			return Message.warn("shop.cart.addProductNotAllowed");
		}
		if (currentCart != null) {
			if (currentCart.contains(product, null)) {
				CartItem cartItem = currentCart.getCartItem(product, null);
				cartItemSize = currentCart.size();
				productQuantity = cartItem.getQuantity() + quantity;
			} else {
				cartItemSize = currentCart.size() + 1;
				productQuantity = quantity;
			}
		}
		if (Cart.MAX_CART_ITEM_SIZE != null && cartItemSize > Cart.MAX_CART_ITEM_SIZE) {
			return Message.warn("shop.cart.addCartItemCountNotAllowed", Cart.MAX_CART_ITEM_SIZE);
		}
		if (CartItem.MAX_QUANTITY != null && productQuantity > CartItem.MAX_QUANTITY) {
			return Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY);
		}
		if (productQuantity > product.getAvailableStock()) {
			return Message.warn("shop.cart.productLowStock");
		}
		if (currentCart == null) {
			currentCart = cartService.create();
		}
		cartService.add(currentCart, product, quantity);
		return Message.success("shop.cart.addSuccess", currentCart.getProductQuantity(null), currency(currentCart.getEffectivePrice(null), true, false));
	}

	/**
	 * 列表
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute("cart", cartService.getCurrent());
		return "/shop/${theme}/cart/list";
	}

	/**
	 * 编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> edit(Long id, Integer quantity) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (quantity == null || quantity < 1) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Product product = productService.find(id);
		if (product == null) {
			data.put("message", Message.error("shop.cart.productNotExist"));
			return data;
		}
		Cart currentCart = cartService.getCurrent();
		if (currentCart == null || currentCart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			return data;
		}
		Store store = product.getGoods().getStore();
		if (store == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		if (!currentCart.contains(product, null)) {
			data.put("message", Message.error("shop.cart.cartItemNotExist"));
			return data;
		}
		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			data.put("message", Message.warn("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
			return data;
		}
		if (quantity > product.getAvailableStock()) {
			data.put("message", Message.warn("shop.cart.productLowStock"));
			return data;
		}
		cartService.modify(currentCart, product, quantity);
		CartItem cartItem = currentCart.getCartItem(product, store);
		List<Store> stores = currentCart.getStores();

		data.put("message", SUCCESS_MESSAGE);
		data.put("subtotal", cartItem.getSubtotal());
		data.put("isLowStock", cartItem.getIsLowStock());
		data.put("quantity", currentCart.getProductQuantity(store));
		data.put("effectiveRewardPoint", currentCart.getEffectiveRewardPointTotal(stores));
		data.put("effectivePrice", currentCart.getEffectivePriceTotal(stores));
		data.put("promotionDiscount", currentCart.getDiscountTotal(stores));
		data.put("giftNames", currentCart.getGiftNames(store));
		data.put("promotionNames", cartItem.getPromotionNames(store));
		return data;
	}

	/**
	 * 删除
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> delete(Long id) {
		Map<String, Object> data = new HashMap<String, Object>();
		Product product = productService.find(id);
		if (product == null) {
			data.put("message", Message.error("shop.cart.productNotExist"));
			return data;
		}
		Cart cart = cartService.getCurrent();
		if (cart == null || cart.isEmpty()) {
			data.put("message", Message.error("shop.cart.notEmpty"));
			return data;
		}
		if (!cart.contains(product, null)) {
			data.put("message", Message.error("shop.cart.cartItemNotExist"));
			return data;
		}
		Store store = product.getGoods().getStore();
		cartService.remove(cart, product);

		data.put("message", SUCCESS_MESSAGE);
		data.put("quantity", cart.getProductQuantity(store));
		data.put("effectiveRewardPoint", cart.getEffectiveRewardPointTotal(cart.getStores()));
		data.put("effectivePrice", cart.getEffectivePriceTotal(cart.getStores()));
		data.put("giftNames", cart.getGiftNames(store));
		data.put("promotionNames", cart.getPromotionNames(store));
		return data;
	}

	/**
	 * 清空
	 */
	@RequestMapping(value = "/clear", method = RequestMethod.POST)
	public @ResponseBody Message clear() {
		Cart currentCart = cartService.getCurrent();
		if (currentCart != null) {
			cartService.clear(currentCart);
		}
		return SUCCESS_MESSAGE;
	}

}
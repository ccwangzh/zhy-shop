[#escape x as x?html]
<div class="span2">
	<div class="menu">
		<h2>
			<a href="${base}/business/index.jhtml">${message("shop.business.index")}</a>
		</h2>
		<dl>
			<dt>${message("shop.business.index.goods")}</dt>
			<dd[#if current == "goodsList"] class="current"[/#if]>
				<a href="${base}/business/goods/list.jhtml">${message("shop.business.index.goods")}</a>
			</dd>
			<dd[#if current == "stockList"] class="current"[/#if]>
				<a href="${base}/business/stock/log.jhtml">${message("shop.business.index.stock")}</a>
			</dd>
			<dd[#if current == "productNotifyList"] class="current"[/#if]>
				<a href="${base}/business/product_notify/list.jhtml">${message("shop.business.index.productNotify")}</a>
			</dd>
			<dd[#if current == "consultationList"] class="current"[/#if]>
				<a href="${base}/business/consultation/list.jhtml">${message("shop.business.consultation.list")}</a>
			</dd>
			<dd[#if current == "reviewList"] class="current"[/#if]>
				<a href="${base}/business/review/list.jhtml">${message("shop.business.review.list")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.order")}</dt>
			<dd[#if current == "orderList"] class="current"[/#if]>
				<a href="${base}/business/order/list.jhtml">${message("shop.business.index.order")}</a>
			</dd>
			<dd[#if current == "deliveryTemplateList"] class="current"[/#if]>
				<a href="${base}/business/delivery_template/list.jhtml">${message("shop.business.index.deliveryTemplate")}</a>
			</dd>
			<dd[#if current == "deliveryCenterList"] class="current"[/#if]>
				<a href="${base}/business/delivery_center/list.jhtml">${message("shop.business.index.deliveryCenter")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.store")}</dt>
			<dd[#if current == "storeEdit"] class="current"[/#if]>
				<a href="${base}/business/store/edit.jhtml">${message("shop.business.index.store")}</a>
			</dd>
			<dd[#if current == "storeProductCategoryList"] class="current"[/#if]>
				<a href="${base}/business/store_product_category/list.jhtml">${message("shop.business.index.storeProductCategory")}</a>
			</dd>
			<dd[#if current == "storeTagList"] class="current"[/#if]>
				<a href="${base}/business/store_tag/list.jhtml">${message("shop.business.index.storeTag")}</a>
			</dd>
			<dd[#if current == "categoryApplicationList"] class="current"[/#if]>
				<a href="${base}/business/category_application/list.jhtml">${message("shop.business.index.categoryApplication")}</a>
			</dd>
			<dd[#if current == "renewalList"] class="current"[/#if]>
				<a href="${base}/business/renewal/renewal.jhtml">${message("shop.business.index.renewal")}</a>
			</dd>
			<dd[#if current == "freightConfigList"] class="current"[/#if]>
				<a href="${base}/business/freight_config/list.jhtml">${message("shop.business.freightConfig.list")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.promotion")}</dt>
			[@promotion_plugin promotionPluginId = "discount"]
				[#assign discount = promotionPlugin /]
				[#if discount.isAvailable == true]
					<dd[#if current == "discountList"] class="current"[/#if]>
						<a href="${base}/business/discount/list.jhtml">${message("shop.business.discount.list")}</a>
					</dd>
				[/#if]
				[/@promotion_plugin]
				[@promotion_plugin promotionPluginId = "fullReduction"]
					[#assign fullReduction = promotionPlugin /]
					[#if fullReduction.isAvailable == true]
						<dd[#if current == "fullReductionList"] class="current"[/#if]>
							<a href="${base}/business/full_reduction/list.jhtml">${message("shop.business.fullReduction.list")}</a>
						</dd>
					[/#if]
			[/@promotion_plugin]
			<dd[#if current == "couponList"] class="current"[/#if]>
				<a href="${base}/business/coupon/list.jhtml">${message("shop.business.coupon.list")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.customer")}</dt>
			<dd[#if current == "instantMessageList"] class="current"[/#if]>
				<a href="${base}/business/instant_message/list.jhtml">${message("shop.business.index.instantMessage")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.deposit")}</dt>
			<dd[#if current == "depositRecharge"] class="current"[/#if]>
				<a href="${base}/business/deposit/recharge.jhtml">${message("shop.business.deposit.recharge")}</a>
			</dd>
			<dd[#if current == "cashList"] class="current"[/#if]>
				<a href="${base}/business/cash/list.jhtml">${message("shop.business.index.cash")}</a>
			</dd>
			<dd[#if current == "depositLog"] class="current"[/#if]>
				<a href="${base}/business/deposit/log.jhtml">${message("shop.business.deposit.log")}</a>
			</dd>
		</dl>
		<dl>
			<dt>${message("shop.business.index.statistics")}</dt>
			<dd[#if current == "orderStatisticList"] class="current"[/#if]>
				<a href="${base}/business/order_statistic/list.jhtml">${message("shop.business.index.orderStatistic")}</a>
			</dd>
			<dd[#if current == "goodsRankList"] class="current"[/#if]>
				<a href="${base}/business/goods_ranking/list.jhtml">${message("shop.business.index.goodsRank")}</a>
			</dd>
		</dl>
	</div>
</div>
[/#escape]
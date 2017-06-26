[#escape x as x?html]
<script type="text/javascript">
$().ready(function() {

	var $keyword = $("#keyword");
	var $storeId = $("#storeId");
	var $searchSubmit = $("#searchSubmit");
	var $addStoreFavorite = $("#addStoreFavorite");
	var $toggleCollapse = $("[data-toggle='collapse']");
	
	$searchSubmit.submit(function() {
		if ( $keyword.val()== "" ||$storeId.val()=="") {
			return false;
		}
	});
	
	// 添加店铺收藏
	$addStoreFavorite.click(function() {
		$.ajax({
			url: "${base}/member/store_favorite/add.jhtml",
			type: "POST",
			data: {
				storeId: ${store.id}
			},
			dataType: "json",
			cache: false,
			success: function(message) {
				$.message(message);
			}
		});
		return false;
	});
	
	// 折叠
	$toggleCollapse.click(function() {
		var $element = $(this);
		$element.toggleClass("active");
		$target = $($element.data("target"));
		if ($element.hasClass("active")) {
			$target.slideDown();
		} else {
			$target.slideUp();
		}
		return false;
	});

});
</script>
<div class="storeLeft">
	<div class="storeInfo">
		<h2>${message("shop.store.storeInfo")}</h2>
		[#if store.logo?has_content]
			<img src="${store.logo}" class="logo" alt="${store.name}" />
		[/#if]
		<strong>
			${abbreviate(store.name, 16)}
			[#if store.type == "self"]
				<em>${message("shop.goods.platformProprietary")}</em>
			[/#if]
		</strong>
		<p class="clearfix">
			<a href="${base}/store/store.jhtml?storeId=${store.id}">${message("shop.goods.inShop")}</a>
			<a href="javascript:;" id="addStoreFavorite">${message("shop.store.favoriteStore")}</a>
		</p>
		[#if store.instantMessages?has_content]
			<div class="instantMessage">
				[#list store.instantMessages as instantMessage]
					[#if instantMessage.type == "qq"]
						<a href="http://wpa.qq.com/msgrd?v=3&uin=${instantMessage.account}&menu=yes" target="_blank" title="${instantMessage.name}">
							<img src="${base}/resources/shop/default/images/instant_message_qq.png" alt="${instantMessage.name}" />
						</a>
					[#elseif instantMessage.type == "aliTalk"]
						<a href="http://amos.alicdn.com/getcid.aw?v=2&uid=${instantMessage.account}&s=2&groupid=0&charset=utf-8" target="_blank" title="${instantMessage.name}">
							<img src="${base}/resources/shop/default/images/instant_message_wangwang.png" alt="${instantMessage.name}" />
						</a>
					[/#if]
				[/#list]
			</div>
		[/#if]
	</div>
	<div class="storeSearch">
		<form id="searchSubmit" action="${base}/store/search.jhtml" method="get">
			<input type="hidden" id="storeId" name="storeId" value="${store.id}">
			<input type="text" id="keyword" name="keyword" placeholder="${message("shop.store.searchStoreGoods")}" maxlength="30" /><button type="submit">${message("shop.store.search")}</button>
		</form>
	</div>
	[@store_product_category_root_list storeId = store.id]
		[#if storeProductCategories?has_content]
			<div class="storeClassify">
				<dl>
					<dt>${message("shop.store.goodsCategory")}</dt>
					[#list storeProductCategories as storeProductCategory]
						<dd>
							<a href="${base}${storeProductCategory.path}?storeId=${store.id}">
								${abbreviate(storeProductCategory.name, 15)}
								[#if storeProductCategory.isChildren]
									<span class="caret active" data-toggle="collapse" data-target="#storeProductCategory${storeProductCategory.id}"></span>
								[/#if]
							</a>
							[@store_product_category_children_list storeProductCategoryId = storeProductCategory.id  storeId = store.id recursive = false]
								[#if storeProductCategories?has_content]
									<ul id="storeProductCategory${storeProductCategory.id}">
										[#list storeProductCategories as storeProductCategory]
											<li>
												<a href="${base}${storeProductCategory.path}?storeId=${store.id}">${abbreviate(storeProductCategory.name, 15)}</a>
											</li>
										[/#list]
									</ul>
								[/#if]
							[/@store_product_category_children_list]
						</dd>
					[/#list]
				</dl>
			</div>
		[/#if]
	[/@store_product_category_root_list]
</div>
[/#escape]
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
[@seo type = "storeIndex"]
	<title>[#if store.resolveSeoTitle()?has_content]${store.resolveSeoTitle()}[#else]${seo.resolveTitle()}[/#if][#if showPowered][/#if]</title>
	<meta name="author" content="SHOP++ Team" />
	<meta name="copyright" content="SHOP++" />
	[#if seo.resolveKeywords()?has_content]
		<meta name="keywords" content="${seo.resolveKeywords()}" />
	[/#if]
	[#if seo.resolveDescription()?has_content]
		<meta name="description" content="${seo.resolveDescription()}" />
	[/#if]
[/@seo]
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/bxslider/bxslider.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/goods.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/store.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/bxslider/bxslider.min.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $slider = $("#slider");
	var $shoppingCar = $("#shoppingCar");
	var $compareBar = $("#compareBar");
	var $compareForm = $("#compareBar form");
	var $compareSubmit = $("#compareBar a.submit");
	var $clearCompare = $("#compareBar a.clear");
	var $result = $("#result");
	var $productImage = $("#result img");
	var $addCart = $("#result a.addCart");
	var $exchange = $("#result a.exchange");
	var $addFavorite = $("#result a.addFavorite");
	var $addCompare = $("#result a.addCompare");
	var $storeId = $("#storeId");
	
	$slider.bxSlider({
		mode: "vertical",
		auto: true,
		controls: false
	});
	
	$productImage.lazyload({
		threshold: 100,
		effect: "fadeIn"
	});
	
	// 加入购物车
	$addCart.click(function() {
		var $this = $(this);
		var productId = $this.attr("productId");
		$.ajax({
			url: "${base}/cart/add.jhtml",
			type: "POST",
			data: {productId: productId, quantity: 1},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.type == "success" && $shoppingCar.size() > 0 && window.XMLHttpRequest) {
					var $image = $this.closest("li").find("img");
					var cartOffset = $shoppingCar.offset();
					var imageOffset = $image.offset();
					$image.clone().css({
						width: 170,
						height: 170,
						position: "absolute",
						"z-index": 20,
						top: imageOffset.top,
						left: imageOffset.left,
						opacity: 0.8,
						border: "1px solid #dddddd",
						"background-color": "#eeeeee"
					}).appendTo("body").animate({
						width: 30,
						height: 30,
						top: cartOffset.top,
						left: cartOffset.left,
						opacity: 0.2
					}, 1000, function() {
						$(this).remove();
					});
				}
				$.message(message);
			}
		});
		return false;
	});
	
	// 积分兑换
	$exchange.click(function() {
		var productId = $(this).attr("productId");
		$.ajax({
			url: "${base}/order/check_exchange.jhtml",
			type: "GET",
			data: {productId: productId, quantity: 1},
			dataType: "json",
			cache: false,
			success: function(message) {
				if (message.type == "success") {
					location.href = "${base}/order/checkout.jhtml?type=exchange&productId=" + productId + "&quantity=1";
				} else {
					$.message(message);
				}
			}
		});
		return false;
	});
	
	// 添加商品收藏
	$addFavorite.click(function() {
		var goodsId = $(this).attr("goodsId");
		$.ajax({
			url: "${base}/member/favorite/add.jhtml",
			type: "POST",
			data: {goodsId: goodsId},
			dataType: "json",
			cache: false,
			success: function(message) {
				$.message(message.message);
			}
		});
		return false;
	});
	
	// 对比栏
	var compareGoods = getCookie("compareGoods");
	var compareGoodsIds = compareGoods != null ? compareGoods.split(",") : [];
	if (compareGoodsIds.length > 0) {
		$.ajax({
			url: "${base}/goods/compare_bar.jhtml",
			type: "GET",
			data: {goodsIds: compareGoodsIds},
			dataType: "json",
			cache: true,
			success: function(data) {
				$.each(data, function (i, item) {
					var thumbnail = item.thumbnail != null ? item.thumbnail : "${setting.defaultThumbnailProductImage}";
					$compareBar.find("dt").after(
						[@compress single_line = true]
							'<dd>
								<input type="hidden" name="goodsIds" value="' + item.id + '" \/>
								<a href="' + escapeHtml(item.url) + '" target="_blank">
									<img src="' + escapeHtml(thumbnail) + '" \/>
									<span title="' + escapeHtml(item.name) + '">' + escapeHtml(abbreviate(item.name, 50)) + '<\/span>
								<\/a>
								<strong>' + currency(item.price, true) + '[#if setting.isShowMarketPrice]<del>' + currency(item.marketPrice, true) + '<\/del>[/#if]<\/strong>
								<a href="javascript:;" class="remove" goodsId="' + item.id + '">[${message("shop.common.remove")}]<\/a>
							<\/dd>'
						[/@compress]
					);
				});
				$compareBar.fadeIn();
			}
		});
		
		$.each(compareGoodsIds, function(i, goodsId) { 
			$addCompare.filter("[goodsId='" + goodsId + "']").addClass("selected");
		});
	}
	
	// 移除对比
	$compareBar.on("click", "a.remove", function() {
		var $this = $(this);
		var goodsId = $this.attr("goodsId");
		$this.closest("dd").remove();
		for (var i = 0; i < compareGoodsIds.length; i ++) {
			if (compareGoodsIds[i] == goodsId) {
				compareGoodsIds.splice(i, 1);
				break;
			}
		}
		$addCompare.filter("[goodsId='" + goodsId + "']").removeClass("selected");
		if (compareGoodsIds.length == 0) {
			$compareBar.fadeOut();
			removeCookie("compareGoods");
		} else {
			addCookie("compareGoods", compareGoodsIds.join(","));
		}
		return false;
	});
	
	$compareSubmit.click(function() {
		if (compareGoodsIds.length < 2) {
			$.message("warn", "${message("shop.goods.compareNotAllowed")}");
			return false;
		}
		
		$compareForm.submit();
		return false;
	});
	
	// 清除对比
	$clearCompare.click(function() {
		$addCompare.removeClass("selected");
		$compareBar.fadeOut().find("dd:not(.action)").remove();
		compareGoodsIds = [];
		removeCookie("compareGoods");
		return false;
	});
	
	// 添加对比
	$addCompare.click(function() {
		var $this = $(this);
		var goodsId = $this.attr("goodsId");
		if ($.inArray(goodsId, compareGoodsIds) >= 0) {
			return false;
		}
		if (compareGoodsIds.length >= 4) {
			$.message("warn", "${message("shop.goods.addCompareNotAllowed")}");
			return false;
		}
		$.ajax({
			url: "${base}/goods/add_compare.jhtml",
			type: "GET",
			data: {goodsId: goodsId},
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.message.type == "success") {
					$this.addClass("selected");
					var thumbnail = data.thumbnail != null ? data.thumbnail : "${setting.defaultThumbnailProductImage}";
					$compareBar.show().find("dd.action").before(
						[@compress single_line = true]
							'<dd>
								<input type="hidden" name="goodsIds" value="' + data.id + '" \/>
								<a href="' + escapeHtml(data.url) + '" target="_blank">
									<img src="' + escapeHtml(thumbnail) + '" \/>
									<span title="' + escapeHtml(data.name) + '">' + escapeHtml(abbreviate(data.name, 50)) + '<\/span>
								<\/a>
								<strong>' + currency(data.price, true) + '[#if setting.isShowMarketPrice]<del>' + currency(data.marketPrice, true) + '<\/del>[/#if]<\/strong>
								<a href="javascript:;" class="remove" goodsId="' + data.id + '">[${message("shop.common.remove")}]<\/a>
							<\/dd>'
						[/@compress]
					);
					compareGoodsIds.unshift(goodsId);
					addCookie("compareGoods", compareGoodsIds.join(","));
				} else {
					$.message(data.message);
				}
			}
		});
		return false;
	});
	
})
</script>
</head>
<body>
[#include "/shop/${theme}/include/header.ftl" /]
<div class="container goodsList">
	<div id="compareBar" class="compareBar">
		<form action="${base}/goods/compare.jhtml" method="get">
			<dl>
				<dt>${message("shop.goods.compareBar")}</dt>
				<dd class="action">
					<a href="javascript:;" class="submit">${message("shop.goods.compareSubmit")}</a>
					<a href="javascript:;" class="clear">${message("shop.goods.clearCompare")}</a>
				</dd>
			</dl>
		</form>
	</div>
	[#include "/shop/${theme}/include/store_left.ftl" /]
	<div id="result" class="storeRight">
		[#if store.advertisingImages?has_content]
			<div class="storeBanner">
				<div id="slider" class="slider">
					[#list store.advertisingImages as ad]
						<li>
							<a href="${ad.url}" title="${ad.title}">
								<img src="${ad.image}" alt="${ad.title}" />
							</a>
						</li>
					[/#list]
				</div>
			</div>
		[/#if]
		[#list storeTags as storeTag]
			[@store_goods_list storeId = store.id storeTagId = storeTag.id]
				[#if storeGoodsList?has_content]
					<div class="storeTag">
						<div>
							[#if storeTag.icon?has_content]
								<img src="${storeTag.icon}" alt="${storeTag.name}" />
							[/#if]
						</div>
						<span>${storeTag.name}</span>
					</div>
					<div class="result grid clearfix">
						[#list storeGoodsList as goods]
							<ul>
								[#assign defaultProduct = goods.defaultProduct /]
								<li>
									<a href="${goods.url}">
										<img src="${base}/upload/image/blank.gif" data-original="${goods.thumbnail!setting.defaultThumbnailProductImage}" />
										<div>
											${abbreviate(goods.name, 48)}
										</div>
									</a>
									<strong>
										[#if goods.type == "general" || goods.type == "listedtrade"]
											${currency(defaultProduct.price, true)}
										[#elseif goods.type == "exchange"]
											<em>${message("Product.exchangePoint")}:</em>
											${defaultProduct.exchangePoint}
										[/#if]
									</strong>
									<div class="listShop">
										<div class="shopName">
											<a href="${base}/store/store.jhtml?storeId=${goods.store.id}" title="${goods.store.name}">${abbreviate(goods.store.name, 16)}</a>
											[#if goods.store != null && goods.store.type == "self"]
												<em>${message("shop.goods.platformProprietary")}</em>
											[/#if]
										</div>
										<div class="shopInfo">
											<p>
												${message("Goods.sales")}:
												<span>${goods.sales}</span>
											</p>
											<p>
												${message("Goods.reviews")}:
												<span>${goods.reviewNumber}</span>
											</p>
										</div>
									</div>
									<div class="action">
										[#if goods.type == "general"]
											<a href="javascript:;" class="addCart" productId="${defaultProduct.id}">${message("shop.goods.addCart")}</a>
										[#elseif goods.type == "exchange"]
											<a href="javascript:;" class="exchange" productId="${defaultProduct.id}">${message("shop.goods.exchange")}</a>
										[/#if]
										<a href="javascript:;" class="addFavorite" title="${message("shop.goods.addFavorite")}" goodsId="${goods.id}">&nbsp;</a>
										<a href="javascript:;" class="addCompare" title="${message("shop.goods.addCompare")}" goodsId="${goods.id}">&nbsp;</a>
									</div>
								</li>
							[/#list]
						</ul>
					</div>
				[/#if]
			[/@store_goods_list]
		[/#list]
	</div>
</div>
[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
[#assign defaultProduct = goods.defaultProduct /]
[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		[@seo type = "goodsContent"]
			<title>[#if goods.resolveSeoTitle()?has_content]${goods.resolveSeoTitle()}[#else]${seo.resolveTitle()}[/#if][#if showPowered][/#if]</title>
			<meta name="author" content="SHOP++ Team" />
			<meta name="copyright" content="SHOP++" />
			[#if goods.resolveSeoKeywords()?has_content]
				<meta name="keywords" content="${goods.resolveSeoKeywords()}" />
			[#elseif seo.resolveKeywords()?has_content]
				<meta name="keywords" content="${seo.resolveKeywords()}" />
			[/#if]
			[#if goods.resolveSeoDescription()?has_content]
				<meta name="description" content="${goods.resolveSeoDescription()}" />
			[#elseif seo.resolveDescription()?has_content]
				<meta name="description" content="${seo.resolveDescription()}" />
			[/#if]
		[/@seo]
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
		<link href="${base}/resources/h5/${theme}/css/sp_detail.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.jqzoom.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script>Zepto.init()</script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
		<script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>
		
		<script type="text/javascript">
		Zepto(function() {
			Zepto(".swiper-container").swiper();
			Zepto.modal({
				title:"到货通知",
				text:'<input type="email" id="email" name="email"/>',
				buttons:[
					{
						text:"取消",
						onClick:function(){
							Zepto.closeModal();
						}
					},
					{
						text:"确定",
						onClick:function(){
							if(bool){
								$.ajax({
								url: "${base}/product_notify/save.jhtml",
								type: "POST",
								data: {productId: productId, email: $("#email").val()},
								dataType: "json",
								cache: false,
								beforeSend: function() {
									bool=false;
								},
								success: function(data) {
									Zepto.toast(data.message);
								},
								complete: function() {
									bool=true;
								}
							});
							}
							
						}
					}
				]
			})
		});
$().ready(function() {
	[#assign h5Base = "/shop/h5" /]
	var $shoppingCar = $("#shoppingCar");
	var $historyGoods = $("#historyGoods");
	var $clearHistoryGoods = $("#historyGoods a.clear");
	var $zoom = $("#zoom");
	var $thumbnailScrollable = $("#thumbnailScrollable");
	var $thumbnail = $("#thumbnailScrollable a");
	var $dialogOverlay = $("#dialogOverlay");
	var $preview = $("#preview");
	var $previewClose = $("#preview a.close");
	var $previewScrollable = $("#previewScrollable");
	var $price = $("#price");
	var $marketPrice = $("#marketPrice");
	var $rewardPoint = $("#rewardPoint");
	var $exchangePoint = $("#exchangePoint");
	var $specification = $("#specification dl");
	var $specificationTips = $("#specification div");
	var $specificationValue = $("#specification a");
	var $productNotifyForm = $("#productNotifyForm");
	var $productNotify = $("#productNotify");
	var $productNotifyEmail = $("#productNotify input");
	var $addProductNotify = $("#addProductNotify");
	var $quantity = $("#quantity");
	var $increase = $("#increase");
	var $decrease = $("#decrease");
	var $addCart = $("#addCart");
	var $exchange = $("#exchange");
	var $addFavorite = $("#addFavorite");
	var $addStoreFavorite = $("#addStoreFavorite");
	var $window = $(window);
	var $headerCart = $("#headerCart");
	var $headerCartQuantity = $("#headerCart em");
	
	var $introductionTab = $("#introductionTab");
	var $parameterTab = $("#parameterTab");
	var $reviewTab = $("#reviewTab");
	var $consultationTab = $("#consultationTab");
	var $introduction = $("#introduction");
	var $parameter = $("#parameter");
	var $review = $("#review");
	var $addReview = $("#addReview");
	var $consultation = $("#consultation");
	var $addConsultation = $("#addConsultation");
	var $storeInfo = $("#storeInfo");
	var stock=${defaultProduct.getAvailableStock()};
	var productId = ${defaultProduct.id};
	var productData = {};
	var $buy=$(".detailBuy");
	
	
	[#if goods.hasSpecification()]
		[#list goods.products as product]
			productData["${product.specificationValueIds?join(",")}"] = {
				id: ${product.id},
				price: ${product.price},
				marketPrice: ${product.marketPrice},
				rewardPoint: ${product.rewardPoint},
				exchangePoint: ${product.exchangePoint},
					stock:${product.getAvailableStock()},
				isOutOfStock: ${product.isOutOfStock?string("true", "false")}
			};
		[/#list]
		
		// 锁定规格值
		lockSpecificationValue();
	[/#if]
	
	
	// 规格值选择
	$specificationValue.click(function() {
		var $this = $(this);
		if ($this.hasClass("locked")) {
			return false;
		}
		
		$this.toggleClass("button-fill").parent().siblings().children("a").removeClass("button-fill");
		lockSpecificationValue();
		return false;
	});
	
	// 锁定规格值
	function lockSpecificationValue() {
		var currentSpecificationValueIds = $specification.map(function() {
			$selected = $(this).find("a.button-fill");
			return $selected.size() > 0 ? $selected.attr("val") : [null];
		}).get();
		$specification.each(function(i) {
			$(this).find("a").each(function(j) {
				var $this = $(this);
				var specificationValueIds = currentSpecificationValueIds.slice(0);
				specificationValueIds[i] = $this.attr("val");
				if (isValid(specificationValueIds)) {
					$this.removeClass("locked");
				} else {
					$this.addClass("locked");
				}
			});
		});
		var product = productData[currentSpecificationValueIds.join(",")];
		if (product != null) {
			productId = product.id;
			$price.text(currency(product.price, true));
			$marketPrice.text(currency(product.marketPrice, true));
			$rewardPoint.text(product.rewardPoint);
			$exchangePoint.text(product.exchangePoint);
			if (product.isOutOfStock) {
				if ($addProductNotify.val() == "${message("shop.goods.productNotifySubmit")}") {
					$productNotify.show();
				}
				$addProductNotify.show();
				$quantity.closest("dl").hide();
				$addCart.hide();
				$exchange.hide();
			} else {
				$productNotify.hide();
				$addProductNotify.hide();
				$quantity.closest("dl").show();
				$addCart.show();
				$exchange.show();
			}
		} else {
			productId = null;
		}
	}
	
	// 判断规格值ID是否有效
	function isValid(specificationValueIds) {
		for(var key in productData) {
			var ids = key.split(",");
			if (match(specificationValueIds, ids)) {
				return true;
			}
		}
		return false;
	}
	
	// 判断数组是否配比
	function match(array1, array2) {
		if (array1.length != array2.length) {
			return false;
		}
		for(var i = 0; i < array1.length; i ++) {
			if (array1[i] != null && array2[i] != null && array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}
	
	// 到货通知
	$addProductNotify.click(function() {
		if (productId == null) {
			$specificationTips.fadeIn(150).fadeOut(150).fadeIn(150);
			return false;
		}
		if ($addProductNotify.val() == "${message("shop.goods.addProductNotify")}") {
			$addProductNotify.val("${message("shop.goods.productNotifySubmit")}");
			var bool=true;
			Zepto.modal({
				title:"到货通知",
				text:'<input type="email" id="email" name="email"/>',
				buttons:[
					{
						text:"取消",
						onClick:function(){
							Zepto.closeModal();
						}
					},
					{
						text:"确定",
						onClick:function(){
							if(bool){
								$.ajax({
								url: "${base}/product_notify/save.jhtml",
								type: "POST",
								data: {productId: productId, email: $("#email").val()},
								dataType: "json",
								cache: false,
								beforeSend: function() {
									bool=false;
								},
								success: function(data) {
									Zepto.toast(data.message);
								},
								complete: function() {
									bool=true;
								}
							});
							}
							
						}
					}
				]
			})
			$.ajax({
					url: "${base}/product_notify/email.jhtml",
					type: "GET",
					dataType: "json",
					cache: false,
					success: function(data) {
						$("#email").val(data.email);
					}
		   });
		} 
		return false;
	});
	
	// 到货通知表单验证
	$productNotifyForm.validate({
		rules: {
			email: {
				required: true,
				email: true
			}
		},
		submitHandler: function(form) {
			$.ajax({
				url: "${base}/product_notify/save.jhtml",
				type: "POST",
				data: {productId: productId, email: $productNotifyEmail.val()},
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$addProductNotify.prop("disabled", true);
				},
				success: function(data) {
					if (data.message.type == "success") {
						$addProductNotify.val("${message("shop.goods.addProductNotify")}");
						$productNotify.hide();
					}
		Zepto.toast(data.message);
				},
				complete: function() {
					$addProductNotify.prop("disabled", false);
				}
			});
		}
	});
	
	// 购买数量
	$quantity.keypress(function(event) {
		return (event.which >= 48 && event.which <= 57) || event.which == 8;
	});
	
	// 增加购买数量
	$increase.click(function() {
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity)) {
			$quantity.val(parseInt(quantity) + 1);
		} else {
			$quantity.val(1);
		}
	});
	
	// 减少购买数量
	$decrease.click(function() {
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity) && parseInt(quantity) > 1) {
			$quantity.val(parseInt(quantity) - 1);
		} else {
			$quantity.val(1);
		}
	});
	
	[#if goods.type == "general"]
		// 加入购物车
		$addCart.click(function() {
			if (productId == null) {
				$specificationTips.fadeIn(150).fadeOut(150).fadeIn(150);
				return false;
			}
			var quantity = $quantity.val();
			if(quantity>stock){
				Zepto.toast('库存不足',1000);
				return false;
			}
			if (/^\d*[1-9]\d*$/.test(quantity)) {
				$.ajax({
					url: "${base}/cart/add.jhtml",
					type: "POST",
					data: {productId: productId, quantity: quantity},
					dataType: "json",
					cache: false,
					success: function(message) {
						if (message.type == "success" && $headerCart.size() > 0 && window.XMLHttpRequest) {
							Zepto.toast(message.content,1000);
							hidePopup()
						}
						else{
						Zepto.toast(message.content,1000);}
						
					}
				});
			} else {
				Zepto.toast("${message("shop.goods.quantityPositive")}",1000)
				
			}
		});
	[#elseif goods.type == "exchange"]
		// 积分兑换
		$exchange.click(function() {
			if (productId == null) {
				$specificationTips.fadeIn(150).fadeOut(150).fadeIn(150);
				return false;
			}
			var quantity = $quantity.val();
			if(quantity>stock){
				Zepto.toast('库存不足',1000);
				return false;
			}
			if (/^\d*[1-9]\d*$/.test(quantity)) {
				//得到openId
				var openId =getCookie('openId');
				if(openId){
					$.ajax({
						url: "/wx/checkBindWx.html",
						type: "POST",
						data: {openId: openId},
						dataType: "json",
						cache: false,
						success: function(message) {
							if(message.success){
								if(message.binded == 'y'){
									$.ajax({
										url: "/shop/order/check_exchange.jhtml",
										type: "GET",
										data: {productId: productId, quantity: quantity},
										dataType: "json",
										cache: false,
										success: function(message) {
											if (message.type == "success") {
												location.href = "/h5shop/h5/order/checkout.jhtml?type=exchange&productId=" + productId + "&quantity=" + quantity;
											} else {
												Zepto.toast(message.content,1000)
											}
										}
									});
								}else{
									location.href = "/exchange/h5/login.html?callback=/shop/h5/index.html&ub=0&uid="+message.uid;
								}
							}
						}
					});
				}else{
					$.ajax({
						url: "/shop/order/check_exchange.jhtml",
						type: "GET",
						data: {productId: productId, quantity: quantity},
						dataType: "json",
						cache: false,
						success: function(message) {
							if (message.type == "success") {
								location.href = "/h5shop/h5/order/checkout.jhtml?type=exchange&productId=" + productId + "&quantity=" + quantity;
							} else {
								Zepto.toast(message.content,1000)
								
							}
						}
					});
				}
			} else {
				Zepto.toast("${message("shop.goods.quantityPositive")}",1000)
			}
		});
	[/#if]
	
	// 购物车信息
	$window.on("cartInfoLoad", function(event, cartInfo) {
		var productQuantity = cartInfo != null && cartInfo.productQuantity != null ? cartInfo.productQuantity : 0;
		var effectivePrice = cartInfo != null && cartInfo.effectivePrice != null ? cartInfo.effectivePrice : 0;
		if ($headerCartQuantity.text() != productQuantity && "opacity" in document.documentElement.style) {
			$headerCartQuantity.fadeOut(function() {
				$headerCartQuantity.text(productQuantity).fadeIn();
			});
		} else {
			$headerCartQuantity.text(productQuantity);
		}
	});
	
	// 添加商品收藏
	$addFavorite.click(function() {
		//得到openId
		var openId =getCookie('openId');
		if(openId){
			$.ajax({
				url: "/wx/checkBindWx.html",
				type: "POST",
				data: {openId: openId},
				dataType: "json",
				cache: false,
				success: function(message) {
					if(message.success){
						if(message.binded == 'y'){
							$.ajax({
								url: "/shop/member/favorite/add.jhtml",
								type: "POST",
								data: {goodsId: ${goods.id}},
								dataType: "json",
								cache: false,
								success: function(message) {
									if(message.isAdd){
										$addFavorite.find(".font").removeClass("font-favorite").addClass("font-favorited")
									}else{
										$addFavorite.find(".font").removeClass("font-favorited").addClass("font-favorite")
									}
									Zepto.toast(message.message.content,1000);
								}
							});
						}else{
							location.href = "/exchange/h5/login.html?callback=/shop/h5/index.html&ub=0&uid="+message.uid;
						}
					}
				}
			});
		}else{
			$.ajax({
				url: "/shop/member/favorite/add.jhtml",
				type: "POST",
				data: {goodsId: ${goods.id}},
				dataType: "json",
				cache: false,
				success: function(message) {
					if(message.isAdd){
						$addFavorite.find(".font").removeClass("font-favorite").addClass("font-favorited")
					}else{
						$addFavorite.find(".font").removeClass("font-favorited").addClass("font-favorite")
					}
					Zepto.toast(message.message.content,1000);
				}
			});
		}
		return false;
	});
	//是否收藏
	function getCollection(){
		if ($.checkLogin()) {
			$.ajax({
				url: "${base}/member/favorite/getStatus.jhtml",
				type: "GET",
				data: {goodsId: ${goods.id}},
				dataType: "json",
				cache: true,
				success: function(data) {
					if(data=="true"){
						$addFavorite.find(".font").removeClass("font-favorite").addClass("font-favorited")
					}
				}
			});
		}
	}
	getCollection();
	
	// 添加店铺收藏
	$addStoreFavorite.click(function() {
		$.ajax({
			url: "${base}/member/store_favorite/add.jhtml",
			type: "POST",
			data: {storeId: ${goods.store.id}},
			dataType: "json",
			cache: false,
			success: function(message) {
				$.message(message);
			}
		});
		return false;
	});
	
	
	
	[#if setting.isReviewEnabled && setting.reviewAuthority != "anyone"]
		// 发表商品评论
		$addReview.click(function() {
			if ($.checkLogin()) {
				return true;
			} else {
				$.redirectLogin("${base}/review/add/${goods.id}.jhtml", "${message("shop.goods.addReviewNotAllowed")}");
				return false;
			}
		});
	[/#if]
	
	[#if setting.isConsultationEnabled && setting.consultationAuthority != "anyone"]
		// 发表商品咨询
		$addConsultation.click(function() {
			if ($.checkLogin()) {
				return true;
			} else {
				$.redirectLogin("${base}/consultation/add/${goods.id}.jhtml", "${message("shop.goods.addConsultationNotAllowed")}");
				return false;
			}
		});
	[/#if]
	
	// 浏览记录
	var historyGoods = getCookie("historyGoods");
	var historyGoodsIds = historyGoods != null ? historyGoods.split(",") : [];
	for (var i = 0; i < historyGoodsIds.length; i ++) {
		if (historyGoodsIds[i] == ${goods.id}) {
			historyGoodsIds.splice(i, 1);
			break;
		}
	}
	historyGoodsIds.unshift(${goods.id});
	if (historyGoodsIds.length > 6) {
		historyGoodsIds.pop();
	}
	addCookie("historyGoods", historyGoodsIds.join(","));
	$.ajax({
		url: "${base}/goods/history.jhtml",
		type: "GET",
		data: {goodsIds: historyGoodsIds},
		dataType: "json",
		cache: true,
		success: function(data) {
			$.each(data, function (i, item) {
				var thumbnail = item.thumbnail != null ? item.thumbnail : "${setting.defaultThumbnailProductImage}";
				$clearHistoryGoods.parent().before(
					[@compress single_line = true]
						'<dd>
							<img src="' + escapeHtml(thumbnail) + '" \/>
							<a href="' + escapeHtml(item.url) + '" title="' + escapeHtml(item.name) + '">' + escapeHtml(abbreviate(item.name, 30)) + '<\/a>
							<strong>' + currency(item.price, true) + '<\/strong>
						<\/dd>'
					[/@compress]
				);
			});
		}
	});
	
	// 清空浏览记录
	$clearHistoryGoods.click(function() {
		$historyGoods.remove();
		removeCookie("historyGoods");
	});
	
			//监听浏览器滚动
				   $("#maincon").scroll(function(){  
                 
                    if($("#maincon").scrollTop()>0)
                    {  
                       $(".biaoti").css("display","inline")
                        $(".bar-nav").css("background","#00aaee")
                     
                     } else{
                        $(".biaoti").css("display","none")
                           $(".bar-nav").css("background","transparent")
                     }
                      
                    })  
	
	
	// 获取即时通讯
	$.ajax({
		url: "${base}/goods/get_instant_message.jhtml",
		type: "GET",
		data: {storeId: ${goods.store.id}},
		dataType: "json",
		cache: true,
		success: function(data) {
			$.each(data, function (i, item) {
				if (item.type == "qq") {
					$(
						[@compress single_line = true]
							'<a href="http://wpa.qq.com/msgrd?v=3&uin=' + escapeHtml(item.account) + '&site=qq&menu=yes" target="_blank">
								<img border="0" src="${base}/resources/shop/default/images/instant_message_qq.png" alt="'+ escapeHtml(item.name) + '" />
							<\/a>'
						[/@compress]
					).appendTo($storeInfo);
				} else {
					$(
						[@compress single_line = true]
							'<a href="http://amos.alicdn.com/getcid.aw?v=2&uid=' + escapeHtml(item.account) + '&site=cntaobao&s=2&groupid=0&charset=utf-8" target="_blank" >
								<img border="0" src="${base}/resources/shop/default/images/instant_message_wangwang.png" alt="'+ escapeHtml(item.name) + '" />
							<\/a>'
						[/@compress]
					).appendTo($storeInfo);
				}
			});
		}
	});
	
	// 点击数
	$.ajax({
		url: "${base}/goods/hits/${goods.id}.jhtml",
		type: "GET",
		cache: true
	});
	
	//立即购买
	$buy.on("click",function(){
		var $this = $(this);
		var quantity=$quantity.val();
		var productId = $this.attr("productId");
		if(quantity>stock){
			Zepto.toast('库存不足',1000);
			return false;
		}
		location.href = "/h5shop/h5/order/checkout.jhtml?type=directBuy&productId=" + productId + "&quantity="+quantity;
	})
	
	//购买弹出层
	var $buyBtn=$("#buyBtn");
	var $buyClose=$("#buyClose")
	var $addCartBtn=$("#addCartBtn");
	var $exchangeBtn=$("#exchangeBtn")
	$buyBtn.click(function(){
		$(".detailBuy").show();
		$("#addCart").hide();
		showPopup()
	})
	
	$addCartBtn.click(function(){
		$(".detailBuy").hide();
		$("#addCart").show();
		showPopup()
	})
	
	$exchangeBtn.click(function(){
		showPopup();
	})
	
	$buyClose.click(function(){
		$(".buyNumBox").removeClass("modal-in");
		$(".popup-overlay").removeClass('modal-overlay-visible');
	})
	
	function showPopup(){
		$(".buyNumBox").addClass("modal-in");
		$(".popup-overlay").addClass('modal-overlay-visible');
	}
	
	function hidePopup(){
		$(".buyNumBox").removeClass("modal-in");
		$(".popup-overlay").removeClass('modal-overlay-visible');
	}

});
</script>
	</head>

	<body>
		[#assign productCategory = goods.productCategory /]
		<div class="page-group">
			<div class="page page-current">
			
			
				<div class="content" id="maincon">
					<div style="position:fixed;top:0" class="bar bar-nav  spdetai">
					<a class="button button-link button-nav pull-left back" >
					<span class="font font-leftjiantou cofff"></span> 
					</a>
					<h1 class="title biaoti">商品详情</h1>
				</div>
					<!-- Slider -->
					<div class="swiper-container" data-space-between='10'>
						<div class="swiper-wrapper">

							[#if goods.productImages?has_content]
								[#list goods.productImages as productImage]
								<div class="swiper-slide">
									<img src="${productImage.thumbnail}" title="${productImage.title}">
								</div>
								[/#list]
							[#else]
								<div class="swiper-slide">
									<img class="medium" src="${setting.defaultMediumProductImage}" />
								</div>
								
							[/#if]
						</div>
						<div class="swiper-pagination"></div>
					</div>

					<div class="basicInfo">
						<p class="price">
							[#if goods.type == "general" || goods.type == "listedtrade"]
								<strong id="price">${currency(defaultProduct.price, true)}</strong>
							[#elseif goods.type == "exchange"]
								<em>${message("Product.exchangePoint")}:</em>
								<strong id="exchangePoint">${defaultProduct.exchangePoint}</strong>
							[/#if]
							
						</p>
						<p class="cpname">${goods.name}</p>
						<p class="cpdesc">
							[#if goods.caption?has_content]
									${goods.caption}
							[/#if]
						</p>
						<div class="btns">
							<a href="javascript:;" class="favorite" id="addFavorite"><i class="font font-favorite co9"></i></a>
						
						</div>
					</div>
					<ul class="cpul">
						[#if defaultProduct.rewardPoint > 0]
						<li>${message("Product.rewardPoint")}：<span id="rewardPoint">${defaultProduct.rewardPoint}</span></li>
						[/#if]
						<li>${message("Product.stock")}：${defaultProduct.getAvailableStock()}</li>
						<li>${message("Goods.sn")}：${goods.sn}</li>

					</ul>
					<div class="gwc">
						<ul>
							<li id="headerCart"><a href="${h5Base}/cart/list.jhtml"><i class="font font-cart"></i><em></em></a></li>
							
						[#if goods.type != "exchange"]
							<li><a href="javascript:;" class="button  button-fill blueee" productId="${defaultProduct.id}" id="buyBtn">立即购买</a></li>
						[#else]
							<li><input type="button" id="exchangeBtn" class="button button-fill redee button-danger[#if defaultProduct.isOutOfStock] hidden[/#if]" value="${message("shop.goods.exchange")}" /></li>
						[/#if]
						[#if goods.type == "general" && goods.isDelivery && goods.type != "listedtrade"]
							<li><input type="button" id="addCartBtn" class="button button-fill redee button-danger addCart[#if defaultProduct.isOutOfStock] hidden[/#if]" value="${message("shop.goods.addCart")}" /></li>
						[/#if]
						</ul>
					</div>
					
					<!--确认弹出层-->
					<div class="popup-overlay"></div>
					<div class="buyNumBox">
						<i class="font font-close" id="buyClose"></i>
						<div class="info">
							[#if goods.productImages?has_content]
							<a href="${goods.productImages[0].large}" id="zoom" rel="gallery">
								<img class="medium" src="${goods.productImages[0].medium}" />
							</a>
						[#else]
							<a href="${setting.defaultLargeProductImage}" id="zoom" rel="gallery">
								<img class="medium" src="${setting.defaultMediumProductImage}" />
							</a>
						[/#if]
							<ul>
								<li>
									[#if goods.type == "general" || goods.type == "listedtrade"]
										<strong id="price" class="price">${currency(defaultProduct.price, true)}</strong>
									[#elseif goods.type == "exchange"]
										<em>${message("Product.exchangePoint")}:</em>
										<strong id="exchangePoint" class="price">${defaultProduct.exchangePoint}</strong>
									[/#if]
								</li>
								<li>${message("Product.stock")}：${defaultProduct.getAvailableStock()}</li>
							<ul>
						</div>
						<div class="params">
						[#if goods.hasSpecification()]
							[#assign defaultSpecificationValueIds = defaultProduct.specificationValueIds /]
							<div id="specification" class="specification clearfix">
								[#list goods.specificationItems as specificationItem]
									<dl>
										<dt>
											<span title="${specificationItem.name}">${abbreviate(specificationItem.name, 8)}:</span>
										</dt>
										[#list specificationItem.entries as entry]
											[#if entry.isSelected]
												<dd>
													<a href="javascript:;" class="button [#if defaultSpecificationValueIds[specificationItem_index] == entry.id]button-fill[/#if]" val="${entry.id}">
														${entry.value}
													</a>
												</dd>
											[/#if]
										[/#list]
									</dl>
								[/#list]
							</div>
						[/#if]
						<div class="numInput">
							<label>数量:</label>
							<div style="width:50%" class="buttons-row"><a href="#" class="button" id="decrease"><i class="font font-minus"></i></a><input style="width:50px" type="text" id="quantity" name="quantity" value="1"/><a href="#" class="button" id="increase"><i class="font font-add"></i></a></div>
						</div>
						</div>
						<input type="button" id="addProductNotify" class="button button-fill[#if !defaultProduct.isOutOfStock] hidden[/#if]" value="${message("shop.goods.addProductNotify")}" />
						[#if goods.type != "exchange"]
							<a href="javascript:;" class="button  button-fill detailBuy" productId="${defaultProduct.id}">立即购买</a>
						[#else]
							<input type="button" id="exchange" class="button button-fill redee button-danger[#if defaultProduct.isOutOfStock] hidden[/#if]" value="${message("shop.goods.exchange")}" />
						[/#if]
						[#if goods.type == "general" && goods.isDelivery && goods.type != "listedtrade"]
							<input type="button" id="addCart" class="button button-fill redee button-danger addCart[#if defaultProduct.isOutOfStock] hidden[/#if]" value="${message("shop.goods.addCart")}" />
						[/#if]
					</div>
					
					
					
					
				</div>
			</div>

		</div>

		
		
	</body>

</html>
[/#escape]
[#escape x as x?html]
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>${message("shop.cart.title")}[#if showPowered][/#if]</title>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <link rel="shortcut icon" href="/favicon.ico">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
    <link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
    <link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
    <link href="${base}/resources/h5/${theme}/css/cartLists.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
   	<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
   	<script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>
   	<script type="text/javascript">
$().ready(function() {

	var $quantity = $("#cartLists input[name='quantity']");
	var $increase = $("#cartLists a.increase");
	var $decrease = $("#cartLists a.decrease");
	var $delete = $("#cartLists a.font-delete");
	var $promotion = $("#promotion");
	var $promotionDiscount = $("#promotionDiscount");
	var $effectiveRewardPoint = $("#effectiveRewardPoint");
	var $effectivePrice = $("#effectivePrice");
	var $clear = $("#clear");
	var $submit = $("#submit");
	var $window = $(window);
	var timeouts = {};
	
	// 初始数量
	$quantity.each(function() {
		var $this = $(this);
		$this.data("value", $this.val());
	});
	
	// 数量
	$quantity.keypress(function(event) {
		return (event.which >= 48 && event.which <= 57) || event.which == 8;
	});
	
	// 增加数量
	$increase.click(function() {
	
		var $quantity = $(this).siblings("input");
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity)) {
			$quantity.val(parseInt(quantity) + 1);
		} else {
			$quantity.val(1);
		}
		
			edit($quantity);

		
	});
	
	// 减少数量
	$decrease.click(function() {
	
		var $quantity = $(this).siblings("input");
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity) && parseInt(quantity) > 1) {
			$quantity.val(parseInt(quantity) - 1);
		} else {
			$quantity.val(1);
		}
		
			edit($quantity);
		
	});
	
	// 编辑数量
	$quantity.on("input propertychange change", function(event) {
		if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
			edit($(this));
		}
	});
	
	// 编辑数量
	function edit($quantity) {
		var quantity = $quantity.val();
		var $gift = $quantity.closest("ul").find("dl.gift");
		var $promotion = $quantity.closest("ul").find("em.promotion");
		if (/^\d*[1-9]\d*$/.test(quantity)) {
			var $li = $quantity.closest("li");
			var id = $li.find("input[name='id']").val();
			clearTimeout(timeouts[id]);
			timeouts[id] = setTimeout(function() {
				$.ajax({
					url: "edit.jhtml",
					type: "POST",
					data: {id: id, quantity: quantity},
					dataType: "json",
					cache: false,
					beforeSend: function() {
						$submit.prop("disabled", true);
					},
					success: function(data) {
						if (data.message.type == "success") {
							$quantity.data("value", quantity);
							if (data.giftNames != null && data.giftNames.length > 0) {
								$gift.html('<dt>${message("Cart.gifts")}:<\/dt>');
								$.each(data.giftNames, function(i, giftName) {
									$gift.append('<dd title="' + escapeHtml(giftName) + '">' + escapeHtml(abbreviate(giftName, 50)) + ' &times; 1<\/dd>');
								});
								"opacity" in document.documentElement.style ? $gift.fadeIn() : $gift.show();
							} else {
								"opacity" in document.documentElement.style ? $gift.fadeOut() : $gift.hide();
							}
							$promotion.text(data.promotionNames.join(", "));
							$effectiveRewardPoint.text(data.effectiveRewardPoint);
							$effectivePrice.text(currency(data.effectivePrice, true, true));
							$promotionDiscount.text(currency(data.promotionDiscount, true, true));
						} else if (data.message.type == "warn") {
							$.message(data.message);
							$quantity.val($quantity.data("value"));
						} else if (data.message.type == "error") {
							$.message(data.message);
							$quantity.val($quantity.data("value"));
							setTimeout(function() {
								location.reload(true);
							}, 3000);
						}
					},
					complete: function() {
						$submit.prop("disabled", false);
					}
				});
			}, 500);
		} else {
			$quantity.val($quantity.data("value"));
		}
	}
	
	// 删除
	$delete.click(function() {
		if (confirm("${message("shop.dialog.deleteConfirm")}")) {
			var $this = $(this);
			var $li = $this.closest("li");
			var id = $li.find("input[name='id']").val();
			var $gift = $this.closest("ul").find("dl.gift");
			var $promotion = $this.closest("ul").find("em.promotion");
			$.ajax({
				url: "delete.jhtml",
				type: "POST",
				data: {id: id},
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$submit.prop("disabled", true);
				},
				success: function(data) {
					if (data.message.type == "success") {
						if (data.quantity > 0) {
							$li.remove();
							if (data.giftNames != null && data.giftNames.length > 0) {
								$gift.html('<dt>${message("Cart.gifts")}:<\/dt>');
								$.each(data.giftNames, function(i, giftName) {
									$gift.append('<dd title="' + escapeHtml(giftName) + '">' + escapeHtml(abbreviate(giftName, 50)) + ' &times; 1<\/dd>');
								});
								"opacity" in document.documentElement.style ? $gift.fadeIn() : $gift.show();
							} else {
								"opacity" in document.documentElement.style ? $gift.fadeOut() : $gift.hide();
							}
							$promotion.text(data.promotionNames.join(", "));
							$effectiveRewardPoint.text(data.effectiveRewardPoint);
							$effectivePrice.text(currency(data.effectivePrice, true, true));
							$promotionDiscount.text(currency(data.promotionDiscount, true, true));
						} else {
							location.reload(true);
						}
					} else {
						$.message(data.message);
						setTimeout(function() {
							location.reload(true);
						}, 3000);
					}
				},
				complete: function() {
					$submit.prop("disabled", false);
				}
			});
		}
		return false;
	});
	
	// 清空
	$clear.click(function() {
		if (confirm("${message("shop.dialog.clearConfirm")}")) {
			$.each(timeouts, function(i, timeout) {
				clearTimeout(timeout);
			});
			$.ajax({
				url: "clear.jhtml",
				type: "POST",
				dataType: "json",
				cache: false,
				success: function(data) {
					location.reload(true);
				}
			});
		}
		return false;
	});
	
	
	// 购物车信息
	$window.on("cartInfoLoad", function(event, cartInfo) {
		var productQuantity = cartInfo != null && cartInfo.productQuantity != null ? cartInfo.productQuantity : 0;
		var effectivePrice = cartInfo != null && cartInfo.effectivePrice != null ? cartInfo.effectivePrice : 0;
		if ($submit.find("span").text() != productQuantity) {
			$submit.find("span").fadeOut(function() {
				$submit.find("span").text(productQuantity).fadeIn();
			});
		} else {
			$submit.find("span").text(productQuantity);
		}
	});
	
	// 提交
	$submit.click(function() {
		if (!$.checkLogin()) {
			$.redirectLogin("${base}/cart/list.jhtml", "${message("shop.cart.accessDenied")}");
			return false;
		}
	});
	
	
	
	
	
	
	
	
	

});
</script>

</head>
<body>
<div class="list-block media-list shoppingCart">
	[#if cart?? && cart.cartItems?has_content && cart.stores?has_content]
	[#list cart.stores as store]
    <ul id="cartLists">
    	[#list cart.getCartItems(store) as cartItem]
        <li>
        	<input type="hidden" name="id" value="${cartItem.product.id}" />
            <div class="item-content cartCon">
                
                
                <div class="item-media">
                    <img src="${cartItem.product.thumbnail!setting.defaultThumbnailProductImage}" alt="${abbreviate(cartItem.product.name, 50, "...")}">
                </div>
                <div class="item-inner">
                	<div class="item-title-row">
		                <div class="item-title"></div>
		                <div class="item-after">
		                	<span data-price="${cartItem.price}">
	                        	${currency(cartItem.price, true)}
								<span class="total">
									<em class="promotion">${cartItem.getPromotionNames(store)?join(", ")}</em>
								</span>	
							</span>
		                </div>
		             </div>
		             <div class="item-subtitle">
		             	<div class="des">
                        	${abbreviate(cartItem.product.name, 50, "...")}
                        	[#if cartItem.product.specifications?has_content]
								<span class="silver">[${cartItem.product.specifications?join(", ")}]</span>
							[/#if]
							[#if !cartItem.isMarketable]
								<span class="red">[${message("shop.cart.notMarketable")}]</span>
							[/#if]
							[#if cartItem.isLowStock]
								<span class="red lowStock">[${message("shop.cart.lowStock")}]</span>
							[/#if]
							[#if !cartItem.isActive]
								<span class="red">[${message("shop.cart.isActive")}]</span>
							[/#if]
                        </div>
		             </div>
		             <div class="item-text cart">
                            <a href="javascript:;" class="decrease">-</a>
                            <input type="text" name="quantity" value="${cartItem.quantity}" maxlength="4" onpaste="return false;">
                            <a href="javascript:;" class="increase">+</a>
                        </div>
                    <a class="font font-delete"></a>
                </div>
            </div>
        </li>
        [/#list]
        [#if cart.getGiftNames(store)?has_content || cart.getPromotionNames(store)?has_content]
			<li[#if !cart.getGiftNames(store)?has_content] hidden[/#if]>
				
					<dl class="gift clearfix">
						[#if cart.getGiftNames(store)?has_content]
							<dt>${message("Cart.gifts")}:</dt>
							[#list cart.getGiftNames(store) as giftName]
								<dd title="${giftName}">${abbreviate(giftName, 50)} &times; 1</dd>
							[/#list]
						[/#if]
					</dl>
				
			</li>
		[/#if]
    </ul>
    [/#list]
    
    [#else]
		<p style="text-align:center;margin:1rem 0;color:#666;font-size:0.8rem;">
			<a href="${base}/h5/">${message("shop.cart.empty")}</a>
		</p>
	[/#if]
	
	<div class="calculate">
	[#if cart?? && cart.cartItems?has_content && cart.stores?has_content]
	<div  class="mytotal">合计金额：<span id="effectivePrice">${currency(cart.getEffectivePriceTotal(cart.stores), true, true)}</span></div>	
		<a href="${base}/h5/order/checkout.jhtml?wxtype=wx" class="external button button-fill" id="submit">结算(<span></span>)</a>[/#if]
	</div>

</div>

<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>

</body>
</html>
[/#escape]
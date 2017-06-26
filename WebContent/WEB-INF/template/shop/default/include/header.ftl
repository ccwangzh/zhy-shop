[#escape x as x?html]
<script type="text/javascript">
$().ready(function() {

	var $window = $(window);
	var $headerName = $("#headerName");
	var $headerLogin = $("#headerLogin");
	var $headerRegister = $("#headerRegister");
	var $headerLogout = $("#headerLogout");
	var $goodsSearchForm = $("#goodsSearchForm");
	var $keyword = $("#goodsSearchForm input");
	var defaultKeyword = "${message("shop.header.keyword")}";
	var $headerCart = $("#headerCart");
	var $headerCartQuantity = $("#headerCart a.cartButton em");
	var $headerCartDetail = $("#headerCart div.detail");
	var $headerCartItems = $("#headerCart div.items");
	var $headerCartSummary = $("#headerCart div.summary");
	var $broadsideNav = $("#broadsideNav");
	
	var username = getCookie("username");
	var nickname = getCookie("nickname");
	if ($.trim(nickname) != "") {
		$headerName.text(nickname).show();
		$headerLogout.show();
	} else if ($.trim(username) != "") {
		$headerName.text(username).show();
		$headerLogout.show();
	} else {
		$headerLogin.show();
		$headerRegister.show();
	}
	
	$keyword.focus(function() {
		if ($.trim($keyword.val()) == defaultKeyword) {
			$keyword.val("");
		}
	});
	
	$keyword.blur(function() {
		if ($.trim($keyword.val()) == "") {
			$keyword.val(defaultKeyword);
		}
	});
	
	$goodsSearchForm.submit(function() {
		if ($.trim($keyword.val()) == "" || $keyword.val() == defaultKeyword) {
			return false;
		}
	});
	
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
		var cartItems = cartInfo.items;
		if(cartItems == null || cartItems.length <= 0){
			$headerCartItems.html(
				[@compress single_line = true]
					'<table>
						<tr>
							<td>${message("shop.header.cartEmpty")}<\/td>
						<\/tr>
					<\/table>'
				[/@compress]
			);
		} else {
			var $headerCartTable = $headerCartItems.html('<table id="cartTable"><\/table>');
			$.each(cartItems, function(i, cartItem) {
				$('#cartTable').append(
					[@compress single_line = true]
						'<tr>
							<td>
								<a href="' + cartItem.productUrl + '">
									<img src="' + cartItem.productThumbnail + '" \/>
								<\/a>
							<\/td>
							<td>
								<a href="' + cartItem.productUrl + '">' + escapeHtml(abbreviate(cartItem.productName, 20, "...")) + '<\/a>
							<\/td>
							<td>
								<span>' + currency(cartItem.price, true, false) + '<\/span>&nbsp; &nbsp;<em>x' + cartItem.quantity + '<\/em>
							<\/td>
						<\/tr>'
					[/@compress]
				);
			});
		}
		$headerCartSummary.html(message('[#noescape]${message("shop.header.totalQuantity")}[/#noescape]', productQuantity) + '&nbsp;&nbsp;&nbsp;&nbsp;${message("shop.header.totalPrice")}: <em>' + currency(effectivePrice, true, true) + '<\/em><a href="${base}/cart/list.jhtml">${message("shop.header.checkout")}<\/a>');
	});
	
	// 购物车详情
	$headerCart.hover(
		function() {
			if ($headerCartDetail.is(":hidden")) {
				$headerCart.addClass("active");
				$headerCartDetail.slideDown("fast");
			}
		},
		function() {
			if ($headerCartDetail.is(":visible")) {
				$headerCart.removeClass("active");
				$headerCartDetail.slideUp("fast");
			}
		}
	);
	
	$broadsideNav.find("li").hover(
		function() {
			$(this).find("em").show();
		},function(){
			$(this).find("em").hide();
		}
	);
	
	var $headerLoginA=$headerLogin.find("a");
	var $headerRegisterA=$headerRegister.find("a");
	var $headerLogoutA=$headerLogout.find("a");
	var encodeUrl=encodeURIComponent("/shop");
	var host=window.location.protocol+"//"+window.location.host;
	$headerLoginA.attr("href",host+"/login.html?callback="+encodeUrl);
	$headerRegisterA.attr("href",host+"/register.html?callback="+encodeUrl);
	$headerLogoutA.on("click",function(){
		$.ajax({
			url:host+"/user/loginOut.html",
			type: "POST",
			dataType: "json",
			cache: false,
			success: function(message) {
				 var expires = new Date(); 
                 expires.setTime(expires.getTime() - 1000);
                 document.cookie = "logined=;path=/;expires=" + expires.toGMTString() + "";
                 document.cookie = "mobile=;path=/;expires=" + expires.toGMTString() + "";
                 document.cookie = "userId=;path=/;expires=" + expires.toGMTString() + "";

                 window.location.reload();
			}
		});
   	 })
})
</script>
<div class="header">
	<div class="top">
		<div class="topNav">
			<ul class="left">
				<li>
					<span>${message("shop.header.welcome", setting.siteName)}</span>
					<span id="headerName" class="headerName">&nbsp;</span>
				</li>
				<li id="headerLogin" class="headerLogin">
					<a href="#">${message("shop.header.login")}</a>|
				</li>
				<li id="headerRegister" class="headerRegister">
					<a href="#">${message("shop.header.register")}</a>|
				</li>
				<li id="headerLogout" class="headerLogout">
					<a href="#">[${message("shop.header.logout")}]</a>
				</li>
				
			</ul>
			<ul class="right">
				[@navigation_list position = "top"]
					[#list navigations as navigation]
						<li>
							<a href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
							[#if navigation_has_next]|[/#if]
						</li>
					[/#list]
				[/@navigation_list]
			</ul>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="span4">
				<a href="${base}/">
					<img src="${setting.logo}" alt="${setting.siteName}" />
				</a>
			</div>
			<div class="span6">
				<div class="search">
					<form id="goodsSearchForm" action="${base}/goods/search.jhtml" method="get">
						<input name="keyword" class="keyword" value="${goodsKeyword!message("shop.header.keyword")}" autocomplete="off" x-webkit-speech="x-webkit-speech" x-webkit-grammar="builtin:search" maxlength="30" />
						<button type="submit">搜索</button>
					</form>
				</div>
				<div class="hotSearch">
					[#if setting.hotSearches?has_content]
						${message("shop.header.hotSearch")}:
						[#list setting.hotSearches as hotSearch]
							<a href="${base}/goods/search.jhtml?keyword=${hotSearch?url}">${hotSearch}</a>
						[/#list]
					[/#if]
				</div>
			</div>
			<div id="headerCart" class="headerCart">
				<a class="cartButton" href="${base}/cart/list.jhtml"><span>${message("shop.header.cart")}(<em></em>)</span></a>
				<div class="detail">
					<div class="title">${message("shop.header.cartItemTitle")}</div>
					<div class="items"></div>
					<div class="summary"></div>
				</div>
			</div>
		</div>
		
		
	</div>
	<div class="nav">
		<div class="container">
			<div class="row">
				<div class="span12">
					<dl class="mainNav">
						<dt>
							<a href="${base}/product_category.jhtml">${message("shop.header.allProductCategory")}</a>
						</dt>
						[@navigation_list position = "middle"]
							[#list navigations as navigation]
								<dd[#if navigation.url = url] class="current"[/#if]>
									<a href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
								</dd>
							[/#list]
						[/@navigation_list]
					</dl>
				</div>
			</div>
		</div>
	</div>
</div>
[/#escape]
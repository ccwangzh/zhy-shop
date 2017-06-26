[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.goods.title")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
		<link href="${base}/resources/h5/${theme}/css/sp_list.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>
		<script type="text/javascript">
$().ready(function() {
	[#assign h5Base = "/shop/h5" /]
	
	var $goodsForm = $("#goodsForm");
	var $brandId = $("#brandId");
	var $promotionId = $("#promotionId");
	var $orderType = $("#orderType");
	var $pageNumber = $("#pageNumber");
	var $pageSize = $("#pageSize");
	var $filter = $("#filter dl");
	var $hiddenFilter = $("#filter dl:hidden");
	
	var $brand = $("#filter a.brand");
	var $attribute = $("#filter a.attribute");
	var $gridType = $("#gridType");
	var $size = $("#layout a.size");
	var $previousPage = $("#previousPage");
	var $nextPage = $("#nextPage");
	var $sort = $("#sort a, #sort li");
	var $orderMenu = $("#orderMenu");
	var $startPrice = $("#startPrice");
	var $endPrice = $("#endPrice");
	var $result = $("#result");
	var $productImage = $("#result img");



	
	[#if productCategory??]
		$filter.each(function() {
			var $this = $(this);
			var scrollHeight = this.scrollHeight > 0 ? this.scrollHeight: $.swap(this, {display: "block", position: "absolute", visibility: "hidden"}, function() {
				return this.scrollHeight;
			});
			if (scrollHeight > 30) {
				if ($this.find("a.current").size() > 0) {
					$this.height("auto");
				} else {
					//$this.find("dd.moreOption").show();
				}
			}
		});

		$brand.click(function() {
			var $this = $(this);
			if ($this.hasClass("current")) {
				$brandId.val("");
			} else {
				$brandId.val($this.attr("brandId"));
			}
			$pageNumber.val(1);
			$goodsForm.submit();
			return false;
		});
		
      $("#shaixuan").click(function() {
           $("#filter").css("display","inherit")
           $("#filter").css("background","#fff")
           $("#shaixuan i").addClass("font-downjt");
           $(".nav-bar").css("margin-bottom","0") 
           
		});
		
		$attribute.click(function() {
			var $this = $(this);
			if ($this.hasClass("current")) {
				$this.closest("dl").find("input").prop("disabled", true);
			} else {
				$this.closest("dl").find("input").prop("disabled", false).val($this.text());
			}
			$pageNumber.val(1);
			$goodsForm.submit();
			return false;
		});
	[/#if]

	
	
	
	
	$gridType.click(function() {
		var $this = $(this);
		if (!$this.hasClass("currentGrid")) {
			$this.addClass("currentGrid");
			$listType.removeClass("currentList");
			$result.removeClass("list").addClass("grid");
			addCookie("layoutType", "gridType");
		}
		return false;
	});
	
	
	
	$size.click(function() {
		var $this = $(this);
		$pageNumber.val(1);
		$pageSize.val($this.attr("pageSize"));
		$goodsForm.submit();
		return false;
	});
	
	$previousPage.click(function() {
		$pageNumber.val(${page.pageNumber - 1});
		$goodsForm.submit();
		return false;
	});
	
	$nextPage.click(function() {
		$pageNumber.val(${page.pageNumber + 1});
		$goodsForm.submit();
		return false;
	});
	
	$orderMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$sort.click(function() {
		var $this = $(this);
		if ($this.hasClass("current")) {
			$orderType.val($this.attr("orderType"));
		} else {
			$orderType.val($this.attr("orderType"));
		}
		$pageNumber.val(1);
		$goodsForm.submit();
		return false;
	});
	
	$startPrice.add($endPrice).focus(function() {
		$(this).siblings("button").show();
	});
	
	$startPrice.add($endPrice).keypress(function(event) {
		return (event.which >= 48 && event.which <= 57) || (event.which == 46 && $(this).val().indexOf(".") < 0) || event.which == 8 || event.which == 13;
	});
	
	$goodsForm.submit(function() {
		if ($brandId.val() == "") {
			$brandId.prop("disabled", true);
		}
		if ($promotionId.val() == "") {
			$promotionId.prop("disabled", true);
		}
		if ($orderType.val() == "" || $orderType.val() == "topDesc") {
			$orderType.prop("disabled", true);
		}
		if ($pageNumber.val() == "" || $pageNumber.val() == "1") {
			$pageNumber.prop("disabled", true);
		}
		if ($pageSize.val() == "" || $pageSize.val() == "20") {
			$pageSize.prop("disabled", true);
		}
		if ($startPrice.val() == "" || !/^\d+(\.\d+)?$/.test($startPrice.val())) {
			$startPrice.prop("disabled", true);
		}
		if ($endPrice.val() == "" || !/^\d+(\.\d+)?$/.test($endPrice.val())) {
			$endPrice.prop("disabled", true);
		}
		if ($goodsForm.serializeArray().length < 1) {
			location.href = location.pathname;
			return false;
		}
	});
	
	$.pageSkip = function(pageNumber) {
		$pageNumber.val(pageNumber);
		$goodsForm.submit();
		return false;
	}
	
	$productImage.lazyload({
	    threshold: 1000,
		effect: "fadeIn",
		skip_invisible: false
	});
	
	$resultA=$("#result a");
	$resultA.each(function(index,value){
		var _href=$(this).attr("href");
		var _newHref=_href.replace("/shop","/shop/h5");
		$(this).attr('href',_newHref);
	})
	
	
	
});
</script>
	</head>

	<body>

		<div class="page-group" id="pagefir">
			<div class="page page-current">
					[#include "/h5/${theme}/include/header.ftl" /]
					
			
				<div class="content" id="maincon">
					<form id="goodsForm" action="${h5Base}${(productCategory.path)!"/goods/list.jhtml"}" method="get">
					<input type="hidden" id="brandId" name="brandId" value="${(brand.id)!}" />
					<input type="hidden" id="promotionId" name="promotionId" value="${(promotion.id)!}" />
					<input type="hidden" id="orderType" name="orderType" value="${orderType}" />
					<input type="hidden" id="type" name="type" value="${type}" />
					<input type="hidden" id="pageNumber" name="pageNumber" value="${page.pageNumber}" />
					<input type="hidden" id="pageSize" name="pageSize" value="${page.pageSize}" />
					<div id="sort">
						<div id="orderMenu" style="display:none;">
							[#if orderType??]
								<span>${message("Goods.OrderType." + orderType)}</span>
							[#else]
								<span>${message("Goods.OrderType." + orderTypes[0])}</span>
							[/#if]
							<ul>
								[#list orderTypes as type]
									<li[#if type == orderType] class="current"[/#if] orderType="${type}">${message("Goods.OrderType." + type)}</li>
								[/#list]
							</ul>
						</div>
						<ul class="nav-bar">
							<li>
								<a href="javascript:;"[#if orderType == "priceAsc"] class="currentAsc current" title="${message("shop.goods.priceAsc")}"orderType="priceDesc" [#elseif  orderType == "priceDesc"] class="currentDesc current" title="${message("shop.goods.priceAsc")}" orderType="priceAsc" [#else] class="asc" orderType="priceAsc"[/#if] >
									${message("shop.goods.priceAsc")}<i [#if orderType == "priceAsc"] class="font font-up" [#elseif  orderType == "priceDesc"] class="font font-down" [#else] class="font font-down"[/#if]></i>
								</a>
							</li>
							<li>
								<a href="javascript:;"[#if orderType == "salesDesc"] class="currentDesc current" title="${message("shop.goods.cancel")}"[#else] class="desc"[/#if] orderType="salesDesc">${message("shop.goods.salesDesc")}<i class="font font-down"></i></a>
							</li>
							<li>
								<a style="border-right:1px solid #ddd;" href="javascript:;"[#if orderType == "scoreDesc"] class="currentDesc current" title="${message("shop.goods.cancel")}"[#else] class="desc"[/#if] orderType="scoreDesc">${message("shop.goods.scoreDesc")}<i class="font font-down"></i></a>
							</li>
						

						</ul><span id="shaixuan" style="position: absolute;top: 7px;right: 20px;color:#999">筛选 <i class="font font-upjt"></i><span>
					</div>
					[#if productCategory??]
						[@product_category_children_list productCategoryId = productCategory.id recursive = false]
							[#assign filterProductCategories = productCategories /]
						[/@product_category_children_list]
						[@brand_list productCategoryId = productCategory.id]
							[#assign filterBrands = brands /]
						[/@brand_list]
						[@attribute_list productCategoryId = productCategory.id]
							[#assign filterAttributes = attributes /]
						[/@attribute_list]
						[#if filterProductCategories?has_content || filterBrands?has_content || filterAttributes?has_content]
						<div id="filter" style="display:none">
						[#assign rows = 0 /]
						 [#if filterProductCategories?has_content]
						 [#assign rows = rows + 1 /]
							<dl class="bon-bar">
								<p>${message("shop.goods.productCategory")}:</p>
								[#list filterProductCategories as filterProductCategory]
									<dd>
										<a  href="${h5Base}${filterProductCategory.path}">${filterProductCategory.name}</a>
									</dd>
								[/#list]
							</dl>
						[/#if]
						[#if filterBrands?has_content]
							[#assign rows = rows + 1 /]
								<dl class="bon-bar">
									<p>${message("shop.goods.brand")}:</p>
									[#list filterBrands as filterBrand]
										<dd>
											<a href="javascript:;"[#if filterBrand == brand] class="brand current" title="${message("shop.goods.cancel")}"[#else] class="brand"[/#if] brandId="${filterBrand.id}">${filterBrand.name}</a>	
										</dd>
									[/#list]
								</dl>
						[/#if]
						[#assign hasHiddenAttribute = false /]
						[#list filterAttributes as filterAttribute]
							[#assign rows = rows + 1 /]
							<dl class="bon-bar[#if rows > 3 && !attributeValueMap?keys?seq_contains(filterAttribute)][#assign hasHiddenAttribute = true /] hidden[/#if]">
							
									<input type="hidden" name="attribute_${filterAttribute.id}"[#if attributeValueMap?keys?seq_contains(filterAttribute)] value="${attributeValueMap.get(filterAttribute)}"[#else] disabled="disabled"[/#if] />
									<p title="${filterAttribute.name}">${abbreviate(filterAttribute.name, 12)}:</p>
							
								[#list filterAttribute.options as option]
									<dd>
										<a href="javascript:;"[#if attributeValueMap.get(filterAttribute) == option] class="attribute current" title="${message("shop.goods.cancel")}"[#else] class="attribute"[/#if]>${option}</a>
									</dd>
								[/#list]
								
							</dl>
						[/#list]
						</div>
						[/#if]
					[/#if]
					[#if page.content?has_content]
					<div class="row lists" id="result">
						[#list page.content as goods]
							[#assign defaultProduct = goods.defaultProduct /]
						<div class="col-50">
							<a class="external" href="${goods.url}">
								<div class="list-img"><img src="${base}/upload/image/blank.gif" data-original="${goods.thumbnail!setting.defaultThumbnailProductImage}"></div>
								<h4 class="list-title">${abbreviate(goods.name,40)}</h4>
								<p class="list-price">
									[#if goods.type == "general" || goods.type == "listedtrade"]
										${currency(defaultProduct.price, true)}
										[#elseif goods.type == "exchange"]
											<em>${message("Product.exchangePoint")}:</em>
										${defaultProduct.exchangePoint}
									[/#if]
								</p>
							</a>
						</div>
						[/#list]
					</div>
					[#else]
						[#noescape]
							${message("shop.goods.noListResult")}
						[/#noescape]
					[/#if]
					[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "javascript: $.pageSkip({pageNumber});"]
						[#if totalPages > 1]
							<div class="pagination">
								[#if isFirst]
									<span class="firstPage">${message("shop.page.firstPage")}</span>
								[#else]
									<a href="[@pattern?replace("{pageNumber}", "${firstPageNumber}")?interpret /]" class="firstPage">${message("shop.page.firstPage")}</a>
								[/#if]
								[#if hasPrevious]
									<a href="[@pattern?replace("{pageNumber}", "${previousPageNumber}")?interpret /]" class="previousPage">${message("shop.page.previousPage")}</a>
								[#else]
									<span class="previousPage">${message("shop.page.previousPage")}</span>
								[/#if]
								[#list segment as segmentPageNumber]
									[#if segmentPageNumber_index == 0 && segmentPageNumber > firstPageNumber + 1]
										<span class="pageBreak">...</span>
									[/#if]
									[#if segmentPageNumber != pageNumber]
										<a href="[@pattern?replace("{pageNumber}", "${segmentPageNumber}")?interpret /]">${segmentPageNumber}</a>
									[#else]
										<span class="currentPage">${segmentPageNumber}</span>
									[/#if]
									[#if !segmentPageNumber_has_next && segmentPageNumber < lastPageNumber - 1]
										<span class="pageBreak">...</span>
									[/#if]
								[/#list]
								[#if hasNext]
									<a href="[@pattern?replace("{pageNumber}", "${nextPageNumber}")?interpret /]" class="nextPage">${message("shop.page.nextPage")}</a>
								[#else]
									<span class="nextPage">${message("shop.page.nextPage")}</span>
								[/#if]
								[#if isLast]
									<span class="lastPage">${message("shop.page.lastPage")}</span>
								[#else]
									<a href="[@pattern?replace("{pageNumber}", "${lastPageNumber}")?interpret /]" class="lastPage">${message("shop.page.lastPage")}</a>
								[/#if]
							</div>
						[/#if]
					[/@pagination]
				</form>

				</div>
			</div>
[#include "/h5/${theme}/include/daohang.ftl" /]
[#include "/h5/${theme}/include/leftNav.ftl" /]

		[@product_category_root_list count = 6]
			<div class="productType" id="productType">
			<header class="bar bar-nav">
			  <a class="icon icon-left pull-left" id="closeRight"></a>
			  <h1 class="title">商品分类</h1>
			</header>
            <div class="typeLeft">
                <ul>
                	[#list productCategories as productCategory]
                    	<li class="typeSelected">
                    		[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 3]
                    			<a href="#"><p>${productCategory.name}</p></a>
                    		[/@product_category_children_list]
                    	</li>
                    [/#list]
                   
                </ul>
            </div>
            <div class="typeRight">
            	[#list productCategories as productCategory]
            	
                <div class="item">
                    
                    <div class="item-content">
                        <ul>
                        	[@product_category_children_list productCategoryId = productCategory.id recursive = false]
                        		[#list productCategories as productCategory]
                            		<li><a class="external" href="${base}/h5/${productCategory.path}">${productCategory.name}</a></li>
                            	[/#list]
                            [/@product_category_children_list]
                        </ul>
                    </div>
                </div>
                [/#list]
            </div>
        </div>
			[/@product_category_root_list]



			<div class="popup popup-search productSearch">
			  
			  		<!--搜索栏-->
			    	<div class="bar bar-header-secondary">
			            <div class="searchbar">
			                <a class="searchbar-cancel button button-fill" id="searchBtn">搜索</a>
			                <div class="search-input">
			                    <label class="icon icon-search" for="search"></label>
			                    <input type="search" id='searchKeyword' placeholder='输入关键字...'/>
			                </div>
			            </div>
			        </div>
			        <!--最近搜索-->
			        
			        
			        <div class="recentSearch" id="searchHistory">
			            <div class="rs-header">
			                <span>最近搜索</span>
			                <a href="#" id="clearHistory" class="font font-delete"></a>
			            </div>
			            <div class="rs-content">
			                <ul>
			              
			                </ul>
			            </div>
			        </div>
			       
			        
			        <!--热门搜索-->
			        [#if setting.hotSearches?has_content]
			        <div class="hotSearch">
			            <div class="rs-header">
			            	
			                <span>${message("shop.header.hotSearch")}</span>
			                
			            </div>
			            <div class="rs-content">
			                <ul>
			                	[#list setting.hotSearches as hotSearch]
			                    	<li><a class="external" href="${base}/h5/goods/search.jhtml?keyword=${hotSearch?url}">${hotSearch}</a></li>
			                    [/#list]
			                </ul>
			            </div>
			        </div>
			        [/#if]
			        
			        <i class="font font-close close-popup"></i>

			</div>

		</div>

		


		</div>
		
		
	</body>

</html>
[/#escape]
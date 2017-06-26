[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
[@seo type = "index"]
	<title>${seo.resolveTitle()}[#if showPowered][/#if]</title>
	<meta name="author" content="SHOP++ Team" />
	<meta name="copyright" content="SHOP++" />
	[#if seo.resolveKeywords()?has_content]
		<meta name="keywords" content="${seo.resolveKeywords()}" />
	[/#if]
	[#if seo.resolveDescription()?has_content]
		<meta name="description" content="${seo.resolveDescription()}" />
	[/#if]
[/@seo]
<link href="${base}/favicon.ico" rel="icon" type="image/x-icon" />
<link href="${base}/resources/shop/${theme}/jslides/jslides.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/index.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/jslides/jslides.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<style type="text/css">
.header {
	margin-bottom: 0px;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $productCategoryMenuItem = $("#productCategoryMenu li");
	var $newArticleTab = $("#newArticle ul.tab");
	var $hotGoodsImage = $("div.hotGoods img");
	
	$productCategoryMenuItem.hover(
		function() {
			$(this).children("div.menu").show();
		}, function() {
			$(this).children("div.menu").hide();
		}
	);

	$newArticleTab.tabs("ul.tabContent", {
		tabs: "li",
		event: "mouseover"
	});
	
	$hotGoodsImage.lazyload({
		threshold: 100,
		effect: "fadeIn",
		skip_invisible: false
	});
	
});
</script>
</head>
[#assign current = "indexMember" /]
<body style="background:#f7f7f7">
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="containerIndex index">
		[@ad_position id = 1]
			[#noescape]
				    ${adPosition.resolveTemplate()}
			[/#noescape]
		[/@ad_position]
	</div>
	<div class="container index">
		<div class="row">
			<div class="span2">
				[@product_category_root_list count = 6]
					<div id="productCategoryMenu" class="productCategoryMenu">
						<ul>
							[#list productCategories as productCategory]
								<li>
									[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 3]
										<div class="item[#if !productCategory_has_next] last[/#if]">
											<div>
												[#list productCategories as productCategory]
													<a href="${base}${productCategory.path}">
														<strong>${productCategory.name}</strong>
													</a>
												[/#list]
											</div>
											<div>
												[@brand_list productCategoryId = productCategory.id count = 4]
													[#list brands as brand]
														<a href="${base}${brand.path}">${brand.name}</a>
													[/#list]
												[/@brand_list]
											</div>
										</div>
									[/@product_category_children_list]
									[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 8]
										<div class="menu">
											[#list productCategories as productCategory]
												<dl class="clearfix[#if !productCategory_has_next] last[/#if]">
													<dt>
														<a href="${base}${productCategory.path}">${productCategory.name}</a>
													</dt>
													[@product_category_children_list productCategoryId = productCategory.id recursive = false]
														[#list productCategories as productCategory]
															<dd>
																<a href="${base}${productCategory.path}">${productCategory.name}</a>[#if productCategory_has_next]|[/#if]
															</dd>
														[/#list]
													[/@product_category_children_list]
												</dl>
											[/#list]
											<div class="auxiliary">
												[@brand_list productCategoryId = productCategory.id count = 8]
													[#if brands?has_content]
														<div>
															<strong>${message("shop.index.recommendBrand")}</strong>
															[#list brands as brand]
																<a href="${base}${brand.path}">${brand.name}</a>
															[/#list]
														</div>
													[/#if]
												[/@brand_list]
												[@promotion_list productCategoryId = productCategory.id hasEnded = false count = 4]
													[#if promotions?has_content]
														<div>
															<strong>${message("shop.index.hotPromotion")}</strong>
															[#list promotions as promotion]
																[#if promotion.image?has_content]
																	<a href="${base}${promotion.path}" title="${promotion.title}">
																		<img src="${promotion.image}" alt="${promotion.title}" />
																	</a>
																[/#if]
															[/#list]
														</div>
													[/#if]
												[/@promotion_list]
											</div>
										</div>
									[/@product_category_children_list]
								</li>
							[/#list]
						</ul>
					</div>
				[/@product_category_root_list]
			</div>
		</div>
		
		
		[@product_category_root_list count = 3]
			[@ad_position id = 4]
				[#if adPosition??]
					[#assign adIterator = adPosition.ads.iterator() /]
				[/#if]
			[/@ad_position]
			[#list productCategories as productCategory]
				[@goods_list productCategoryId = productCategory.id tagId = 3 count = 10]
					<div class="row">
						<div class="span12">
							<div class="hotGoods">
								[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 8]
									<dl class="title${productCategory_index + 1}">
										<dt>
											<a href="javascript:;" style="cursor:default;">${productCategory.name}</a>
										</dt>
										<dd>|<a href="${base}${productCategory.path}" style="margin-left:10px;">更多</a></dd>
										[#list productCategories as productCategory]
											<dd>
												<a href="${base}${productCategory.path}">${productCategory.name}</a>
											</dd>	
										[/#list]
										
									</dl>
								[/@product_category_children_list]
								<div class="goods-ad">
									[#if adIterator?? && adIterator.hasNext()]
										[#assign ad = adIterator.next() /]
										[#if ad.type == "image" && ad.hasBegun() && !ad.hasEnded()]
											[#if ad.url??]
												<a href="${ad.url}">
													<img src="${ad.path}" alt="${ad.title}" title="${ad.title}" />
												</a>
											[#else]
												<img src="${ad.path}" alt="${ad.title}" title="${ad.title}" />
											[/#if]
										[/#if]
									[/#if]
								</div>
								<ul>
									[#list goodsList as goods]
										[#assign defaultProduct = goods.defaultProduct /]
										[#if goods_index < 3]
											<li>
												<a href="${goods.url}" title="${goods.name}" target="_blank">
													<div class="goods-img">
														<img src="${base}/upload/image/blank.gif" data-original="${goods.image!setting.defaultThumbnailProductImage}" />
													</div>
													<div class="goods-desc">
														${goods.name}
													</div>
													
													<strong class="text-ellipsis">
														[#if goods.type == "general" || goods.type == "listedtrade"]
															${currency(defaultProduct.price, true)}
														[#elseif goods.type == "exchange"]
															<em>${message("Product.exchangePoint")}:</em>
															${defaultProduct.exchangePoint}
														[/#if]
													</strong>
												</a>
											</li>
										[#else]
											
										[/#if]
									[/#list]
								</ul>
							</div>
						</div>
					</div>
				[/@goods_list]
			[/#list]
		[/@product_category_root_list]
		
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
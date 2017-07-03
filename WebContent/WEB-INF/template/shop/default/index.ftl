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
<link href="${base}/resources/shop/${theme}/css/zhyIndex.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/zhyBase.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/jslides/jslides.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/effect.js"></script>
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
	[#include "/shop/${theme}/include/zhyHeader.ftl" /]
	<div class="containerIndex index" style="height:420px;">
		[@ad_position id = 1]
			[#noescape]
				    ${adPosition.resolveTemplate()}
			[/#noescape]
		[/@ad_position]
	</div>
	
	
	<!--内容 -->
	<div class="container index">
		[@product_category_root_list count = 3]
			[@ad_position id = 3]
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
								
								<ul>
								<li class="goods-ad" style="width: 393px;">
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
								</li>
									[#list goodsList as goods]
										[#assign defaultProduct = goods.defaultProduct /]
										[#if goods_index < 10]
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
	<div class="con-5">
			<div class="wrap">
				<div class="hd clearfix">
				<div class="fl">
					<h2>交易商品</h2><span>商品发售，财富升级</span>
				</div>
				<div class="fr">
					<span>第一页</span>
				</div>
			</div>
			<div class="bd">
				<div class="bd-hd">已挂牌人参珍品<span> / LISTED GINSENG PRODUCTS</span></div>
				[@tradeGoods_list count = 3]
					[#list tradeGoodsList as tradeGoods]
						<div class="box clearfix">
							<div class="fl l"><a href=""><img src="${tradeGoods.image}"></a></div>
							<div class="fl c">
								<h3>${tradeGoods.name}</h3>
								<h4>代码：${tradeGoods.code}</h4>
								<div class="txt">
									
								</div>
								<div class="btn">
									<a href="">询价</a>
								</div>
							</div>
							<ul class="fl r">
								<li>最新价：${tradeGoods.price}</li>
								<li>成交量：${tradeGoods.volume}</li>
								<li>昨收盘：${tradeGoods.prePrice}</li>
								<li>总 额：${tradeGoods.totalAmount}</li>
								<li>开盘价：${tradeGoods.openPrice}</li>
								<li>涨 幅：${tradeGoods.up}</li>
								<li>最高价：${tradeGoods.highestPrice}</li>
								<li>涨 跌：${tradeGoods.upDown}</li>
								<li>最低价：${tradeGoods.lowestPrice}</li>
								<li>振 幅：${tradeGoods.amplitude}</li>
							</ul>				
						</div>	
					[/#list]
    			[/@tradeGoods_list]
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
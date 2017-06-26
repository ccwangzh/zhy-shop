[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title></title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/index.css" rel="stylesheet" type="text/css" />
		<link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
		<script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>
		<script type="text/javascript">
			Zepto(function() {
				Zepto(".swiper-container").swiper();

			});
			$().ready(function() {
				[#assign h5Base = "/shop/h5" /]
				var $lazyImg = $("img.lazy");
				$lazyImg.lazyload({
				    threshold: 1000,
					effect: "fadeIn",
					skip_invisible: false
				});
				
				var $hotgoodsA=$("#hotgoods a");
				$hotgoodsA.each(function(index,value){ 
					var _href=$(this).attr("href");
					var _newHref=_href.replace("/shop","/shop/h5");
					$(this).attr('href',_newHref);
				})
				
				
				
				
			})
			
		</script>
	</head>

	<body>
		[#assign current = "indexMember" /]
		<div class="page-group" id="pagefir">
			<div class="page page-current">
				[#include "/h5/${theme}/include/header.ftl" /]
				<div class="content" id="maincon">
					[@ad_position id = 8]
						[#noescape]
						  [#if adPosition?has_content]
							${adPosition.resolveTemplate()}
					      [/#if]
						[/#noescape]
					[/@ad_position]
				
					<div class="row no-gutter indexNav">
						[@product_category_root_list count = 4]
							[#list productCategories as productCategory]
								[@goods_list productCategoryId = productCategory.id tagId = 3 count = 10]
									<div class="col-50">
										<p>${productCategory.name}</p>
										<a href="${h5Base}${productCategory.path}" class="button external">查看更多<i style="color:#999999" class="font font-right"></i></a>
									</div>
								[/@goods_list]
							[/#list]
						[/@product_category_root_list]
					</div>
					<div class="goods" id="hotgoods">
					[@product_category_root_list count = 4]
						[#list productCategories as productCategory]
							[@goods_list productCategoryId = productCategory.id tagId = 3 count = 10]
								<div class="ygp"><span class="name"><i class="font font-lists"></i>${productCategory.name}</span><span class="line"></span></div>
								<div class="row">
									[#list goodsList as goods]
										[#assign defaultProduct = goods.defaultProduct /]
										[#if goods_index <2]
											<div class="col-50 hotGoods">
												<a href="${goods.url}" class="external">
												<div class="goods-img"><img class="lazy" src="${base}/upload/image/blank.gif" data-original="${goods.image!setting.defaultThumbnailProductImage}"></div>
												<h4 class="goods-title"><span>${abbreviate(goods.name,40)}</span></h4>
												<p class="goods-price">
													[#if goods.type == "general" || goods.type == "listedtrade"]
														${currency(defaultProduct.price, true)}
													[#elseif goods.type == "exchange"]
														<em>${message("Product.exchangePoint")}:</em>
														${defaultProduct.exchangePoint}
													[/#if]
												</p>
												</a>
											</div>
										[#else]
											
										[/#if]
									[/#list]
								</div>
							[/@goods_list]
						[/#list]
					[/@product_category_root_list]
				</div>
				</div>
			</div>
			[#include "/h5/${theme}/include/leftNav.ftl" /]
			[#include "/h5/${theme}/include/daohang.ftl" /]
			
			

		</div>
		

		
	</body>

</html>
[/#escape]
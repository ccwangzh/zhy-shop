[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
[#if productCategory??]
	[@seo type = "goodsList"]
		<title>[#if productCategory.resolveSeoTitle()?has_content]${productCategory.resolveSeoTitle()}[#else]${seo.resolveTitle()}[/#if][#if showPowered][/#if]</title>
		<meta name="author" content="SHOP++ Team" />
		<meta name="copyright" content="SHOP++" />
		[#if productCategory.resolveSeoKeywords()?has_content]
			<meta name="keywords" content="${productCategory.resolveSeoKeywords()}" />
		[#elseif seo.resolveKeywords()?has_content]
			<meta name="keywords" content="${seo.resolveKeywords()}" />
		[/#if]
		[#if productCategory.resolveSeoDescription()?has_content]
			<meta name="description" content="${productCategory.resolveSeoDescription()}" />
		[#elseif seo.resolveDescription()?has_content]
			<meta name="description" content="${seo.resolveDescription()}" />
		[/#if]
	[/@seo]
[#else]
	<title>${message("shop.goods.title")}[#if showPowered][/#if]</title>
	<meta name="author" content="SHOP++ Team" />
	<meta name="copyright" content="SHOP++" />
[/#if]
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/goods.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/zhyIndex.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/zhyBase.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lazyload.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<style>
	.goodsList .grid li:hover {
	    border: 1px solid #e0e0e0
	}
	.goodsList .grid a {
	    position: relative;
	}
	.goodsList .grid a div {
	    height: 26px;
	    line-height: 26px;
	    position: absolute;
	    bottom: 0;
	    left: 0;
	    margin: 0;
	    width: 100%;
	    text-align: center;
	    color: #fff;
	    background-color: rgba(0,0,0,0.35);
	}
	.goodsList .grid strong {
	    margin-top: 10px;
	    font-size:20px;
	    padding:0 0 0 10px;
	}
	.goodsList .grid strong em {
	    color: #E4393C;
	}
	.goodsList .grid .integral {
	   	position:relative;
	   	padding:20px 0 0 0;
	}
	.goodsList .grid .integral strong {
	   	width:74px;
	   	border-right: solid 1px #d8d8d8;
	   	margin:0;
	}
	.goodsList .grid .integral .action{
    	width: 152px;
	    bottom: 4px;
	    left: 101px;
	}
	.goodsList .grid .action a.exchange {
	    width: 76px;
	    background: #E60012;
	    color: #fff;
	    border:none;
	}
</style>

</head>
<body style="background: #fff;">
	[#include "/shop/${theme}/include/zhyHeader.ftl" /]
	<div class="containerIndex index" style="height:420px;">
		[@ad_position id = 1]
			[#noescape]
				    ${adPosition.resolveTemplate()}
			[/#noescape]
		[/@ad_position]
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
				[@tradeGoods_list]
					[#list tradeGoodsList as tradeGoods]
						<div class="box clearfix">
							<div class="fl l"><a href=""><img style="width:90%;height:90%;" src="${tradeGoods.image}"></a></div>
							<div class="fl c" style="margin-left: 8%;">
								<h3>${tradeGoods.name}</h3>
								<h4>代码：${tradeGoods.code}</h4>
								<div class="txt">
									
								</div>
								<div class="btn">
									<a href="">询价</a>
								</div>
							</div>
							<ul class="fl r" style="margin-left: 8%;">
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
</body>
</html>
[/#escape]
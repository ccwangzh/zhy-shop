[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.member.order.list")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/myOrders.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" href="${base}/resources/h5/${theme}/css/fonts.css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/list.js"></script>
		<script>
			var orderStatus=getUrlParam('orderStatus');
			[#assign orderStatus = orderStatus /];
			
		</script>
	</head>

	<body>
		[#assign current = "orderList" /]
		<div class="page-group">
			<div id="page-list-card" class="page">
				
				<div class="content">
				
					<ul class="nav-bar">
						<li class="[#if !orderStatus]cur[/#if]">
							<a href="list.jhtml" class="external">全部</a>
						</li>
						<li class="[#if orderStatus=='pendingPayment']cur[/#if]">
							<a href="list.jhtml?orderStatus=pendingPayment" class="external">待付款</a>
						</li>
						<li class="[#if orderStatus=='pendingShipment']cur[/#if]">
							<a  href="list.jhtml?orderStatus=pendingShipment" class="external">待发货</a>
						</li>
						<li class="[#if orderStatus=='shipped']cur[/#if]">
							<a href="list.jhtml?orderStatus=shipped" class="external">待收货</a>
						</li>
						<li class="[#if orderStatus=='completed']cur[/#if]">
							<a href="list.jhtml?orderStatus=completed" class="external">已完成</a>
						</li>
						
					</ul>
					<div class="list-block cards-list collection">
						<ul>
							[#list page.content as order]
							[#list order.orderItems as orderItem]
							<li class="card[#if order.orderItems?size>1 && orderItem_index < (order.orderItems?size-1)] f-nomargin[/#if]">
								[#if orderItem_index == 0 ]
									<div class="card-header"><span>订单号：<i class="orderNo">${order.sn}</i></span><span class="staus">${message("Order.Status." + order.status)}</span></div>
								[/#if]
								<div class="card-content">
									<div class="card-content-inner">
										<img src="${orderItem.thumbnail}">
										<div class="scwp">
											<p>${abbreviate(orderItem.name, 50, "...")}</p>
											<p>${order.createdDate}</p>
										</div>
										<div class="wpprice">
											<p>${currency(orderItem.price, true)}</p>
											<p class="num">X${orderItem.quantity}</p>
										</div>
									</div>
								</div>
								[#if orderItem_index == (order.orderItems?size-1)]
								<div class="card-footer">
									<div></div>
									<div>共计：<span class="totmoney">${currency(order.amount, true)}</span></div>

								</div>
								<ul class="bon-bar">
									<li>
										<a class="external"  href="view.jhtml?sn=${order.sn}" >订单详情</a>
									</li>
								</ul>
								[/#if]
							</li>

							[/#list]
							[/#list]	

						</ul>
						[#if !page.content?has_content]
							<p>${message("shop.member.noResult")}</p>
						[/#if]
					</div>
				</div>
			</div>

		</div>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>

		<script>
		</script>
		[#include "/h5/${theme}/include/daohang.ftl" /]
	</body>

</html>
[/#escape]
[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.member.order.view")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/order_detail.css" rel="stylesheet" type="text/css" />
		<link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script type="text/javascript">
			$().ready(function() {
			
				var $payment = $("#payment");
				var $cancel = $("#cancel");
				var $receive = $("#receive");
				
				
				[@flash_message /]
				
				// 订单支付
				$payment.click(function() {
					$.ajax({
						url: "check_lock.jhtml",
						type: "POST",
						data: {id: ${order.id}},
						dataType: "json",
						cache: false,
						success: function(message) {
							if (message.type == "success") {
								if("${order.paymentMethod.method}"=="exChangeAccount"){
										location.href = "${base}/order/exAcountPayment.jhtml?orderSns=${order.sn}";
									}else{
										location.href = "${base}/order/payment.jhtml?orderSns=${order.sn}";
									}
							} else {
								$.message(message);
							}
						}
					});
					return false;
				});
				
				// 订单取消
				$cancel.click(function() {
					if (confirm("${message("shop.member.order.cancelConfirm")}")) {
						$.ajax({
							url: "cancel.jhtml?sn=${order.sn}",
							type: "POST",
							dataType: "json",
							cache: false,
							success: function(message) {
								if (message.type == "success") {
									location.reload(true);
								} else {
									$.message(message);
								}
							}
						});
					}
					return false;
				});
				
				// 订单收货
				$receive.click(function() {
					if (confirm("${message("shop.member.order.receiveConfirm")}")) {
						$.ajax({
							url: "receive.jhtml?sn=${order.sn}",
							type: "POST",
							dataType: "json",
							cache: false,
							success: function(message) {
								if (message.type == "success") {
									location.reload(true);
								} else {
									$.message(message);
								}
							}
						});
					}
					return false;
				});
			});
	</script>
	</head>

	<body>
		[#assign current = "orderList" /]
		<div class="page-group">

			<div id="page-list-card" class="page">
				
				<div class="content">
					<div style="height: 4rem;text-align: center;">
						<p>
							[#if order.hasExpired()]
								${message("shop.member.order.hasExpired")}
							[#else]
								${message("Order.Status." + order.status)}
							[/#if]
						</p>
						<p>[#if order.expire?? && !order.hasExpired()]
								<em>(${message("Order.expire")}: ${order.expire?string("yyyy-MM-dd HH:mm:ss")})</em>
							[/#if]</p>
					</div>
					<div class="list-block cards-list collection">

						<ul>
							[#if order.isDelivery]
							<li class="card">
								<div class="card-content">
									<div class="card-content-inner">
										<p>${message("Order.address")}</p>
										<span>${order.consignee}</span><span style="float: right;">${order.phone}</span>
										<p style="width: 85%;">${order.areaName}${order.address}</p>
										<p>${order.zipCode}</p>
									</div>
								</div>
							</li>
							[/#if]
						</ul>
						<ul>
							[#list order.orderItems as orderItem]
							<li class="card[#if order.orderItems?size>1 && orderItem_index < (order.orderItems?size-1)] f-nomargin[/#if]">
								[#if orderItem_index == 0 ]
									<div class="card-header">商品信息</div>
								[/#if]
								<div class="card-content">
									<div class="card-content-inner">
										<img src="${orderItem.thumbnail}">
										<div class="scwp">
											<p>
												[#if orderItem.product??]
													<a href="${orderItem.product.url}" title="${orderItem.name}" target="_blank">${abbreviate(orderItem.name, 30)}</a>
												[#else]
													<span title="${orderItem.name}">${abbreviate(orderItem.name, 30)}</span>
												[/#if]
												[#if orderItem.specifications?has_content]
													<span class="silver">[${orderItem.specifications?join(", ")}]</span>
												[/#if]
												[#if orderItem.type != "general"]
													<span class="red">[${message("Goods.Type." + orderItem.type)}]</span>
												[/#if]
											</p>
											<p>${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}</p>
										</div>
										
										<div class="wpprice">
											<p>
												[#if orderItem.type == "general"]
													${currency(orderItem.price, true)}
												[#else]
													-
												[/#if]
											</p>
											<p class="num">X${orderItem.quantity}</p>
										</div>
										
									</div>
								</div>
								[#if orderItem_index == (order.orderItems?size-1)]
								<div class="card-footer">
									<div></div>
									<div>需支付：<span class="totmoney">${currency(order.amount, true)}</span></div>

								</div>
								[/#if]
							</li>
							[/#list]
						</ul>

						<ul class="cpul">
							<li><span class="labletit">${message("Order.sn")}</span> <span class="lableconte">${order.sn}</span> </li>
							<li><span class="labletit">${message("shop.common.createdDate")} </span> <span class="lablecon">${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}</span></li>
							<li><span class="labletit">${message("Order.shippingMethod")} </span> <span class="lablecon">${order.shippingMethodName}</span></li>

						</ul>
						[#if !order.hasExpired() && order.status == "shipped" || !order.hasExpired() && (order.status == "pendingPayment" || order.status == "pendingReview") || order.paymentMethod?? && order.amountPayable > 0]
						<ul class="bon-bar">
							[#if order.paymentMethod?? && order.amountPayable > 0]
								<li id="payment"><a  href="javascript:;" class="ljfk">${message("shop.member.order.payment")}</a></li>
							[/#if]
							[#if !order.hasExpired() && (order.status == "pendingPayment" || order.status == "pendingReview")]
								<li  id="cancel"><a href="javascript:;">${message("shop.member.order.cancel")}</a></li>
							[/#if]
							[#if !order.hasExpired() && order.status == "shipped"]
								<li  id="receive"><a href="javascript:;">${message("shop.member.order.receive")}</a></li>
							[/#if]
						</ul>
						[/#if]
					</div>
				</div>
			</div>

		</div>
		[#include "/h5/${theme}/include/daohang.ftl" /]
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
	</body>

</html>
[/#escape]
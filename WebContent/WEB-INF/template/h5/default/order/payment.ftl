[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>订单提交</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link rel="stylesheet" href="${base}/resources/h5/${theme}/css/order_detail.css" />

		<link rel="stylesheet" href="${base}/resources/h5/${theme}/css/fonts.css" />

		<script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>

		<script type="text/javascript">
$().ready(function() {

	var $dialogOverlay = $("#dialogOverlay");
	var $dialog = $("#dialog");
	var $other = $("#other");
	var $amountPayable = $("#amountPayable");
	var $fee = $("#fee");
	var $paymentForm = $("#paymentForm");
	var $paymentPluginId = $("#paymentPlugin input:radio");
	var $paymentButton = $("#paymentButton");
   	var sns = new Array();
	
	[#list orderSns as orderSn]
    	sns.push(${orderSn});
	[/#list]	
	
	[#if online]
		// 订单锁定
		setInterval(function() {
			$.ajax({
				url: "lock.jhtml",
				type: "POST",
				data: {orderSns: sns},
				dataType: "json",
				cache: false
			});
		}, 50000);
		
		// 检查等待付款
		setInterval(function() {
			$.ajax({
				url: "check_pending_payment.jhtml",
				type: "GET",
				data: {orderSns: sns},
				dataType: "json",
				cache: false,
				success: function(data) {
					if (!data) {
						location.href = "${base}/member/order/list.jhtml";
					}
				}
			});
		}, 10000);
	[/#if]
	
	// 选择其它支付方式
	$other.click(function() {
		$dialogOverlay.hide();
		$dialog.hide();
	});
	
	// 支付插件
	$paymentPluginId.click(function() {
		$.ajax({
			url: "calculate_amount.jhtml",
			type: "GET",
			data: {paymentPluginId: $(this).val(), orderSns: sns},
			dataType: "json",
		   	traditional: true,  
			cache: false,
			success: function(data) {
				if (data.message.type == "success") {
					$amountPayable.text(currency(data.amount, true, true));
					if (data.fee > 0) {
						$fee.text(currency(data.fee, true)).parent().show();
					} else {
						$fee.parent().hide();
					}
				} else {
					$.message(data.message);
					setTimeout(function() {
						location.reload(true);
					}, 3000);
				}
			}
		});
	});
	
	// 支付
	$paymentForm.submit(function() {
		$dialogOverlay.show();
		$dialog.show();
	});

});
</script>
	</head>

	<body>

		<div class="page-group">

			<div id="page-list-card" class="page">
				<header class="bar bar-nav">
					<a class="button button-link button-nav pull-left back" >
					<span class="font font-leftjiantou cofff"></span> 
					</a>
					<h1 class="title biaoti">订单详情</h1>
				</header>
				<div class="content">
				<div style="height: 4rem;text-align: center;">
						<p style="    background: #fff;margin: 0;padding: 1rem 0;border-bottom: 1px solid #e4e4e4;font-size:14px">[#if order.status == "pendingPayment"]
					    ${message("shop.order.pendingPayment")}
				     	[#elseif order.status == "pendingReview"]
						${message("shop.order.pendingReview")}
					    [#else]
				        ${message("shop.order.pending")}
				    	[/#if]</p>
					</div>
					[#list orders as order]
					<div class="list-block cards-list collection">

					<div class="result">			
				
					<p style="padding-top:20px">${message("Order.sn")}:
					<span>
				
			    	${order.sn}
				
				  
				    </span>
				    </p>
		
						<p>${message("Order.shippingMethod")}:
						<span>	
						[#if isListedtrade]
								挂牌交易
							[#else]
								${shippingMethodName}
						[/#if]
						</span>
						</p>
						
						<p style="padding-bottom:20px">	${message("Order.amountPayable")}:<span style="color:#00aaee;font-weight:bold" id="amountPayable">	[#if amount??]
								${currency(amount, true, true)}
								[/#if]
								[#if fee??]
									<span[#if fee <= 0] class="hidden"[/#if]>(${message("Order.fee")}: <span id="fee">${currency(fee, true)}</span>)</span>
								[/#if]</span></p>
					
					
					</div>
						<a class="vieworder" href="${base}/h5/member/order/view.jhtml?sn=${order.sn}">${message("shop.order.view")}</a>
					  [/#list]
				</div>
			</div>
			
			
			
[#include "/h5/${theme}/include/daohang.ftl" /]
		</div>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>

		<script>
		</script>
	</body>

</html>
[/#escape]
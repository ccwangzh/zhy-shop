[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.member.index")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
</head>
<body>
	[#assign current = "indexMember" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="wrap">
		<div class="container member">
			<div class="row">
				[#include "/shop/${theme}/member/include/navigation.ftl" /]
				<div class="span10">
					<div class="index">
						<div class="top clearfix userCenterIndex" style="padding:0;">
							<div class="uciL" style="width:440px;">
								<span class="logo"></span>
								<p>可用积分：<span class="ucIntegral">${member.point}</span></p>
							</div>
							<div class="uciR" style="width:506px;">
								<ul style="margin:0px">
									<li class="pendPay">
										<a href="order/list.jhtml?orderStatus=pendingPayment"><span></span>待付款(<em>${pendingPaymentOrderCount}</em>)</a>
									</li>
									<li class="pendReceipt">
										<a href="order/list.jhtml?orderStatus=shipped"><span></span>待收货(<em>${shippedOrderCount}</em>)</a>
									</li>
									<li class="complete">
										<a href="order/list.jhtml?orderStatus=completed"><span></span>已完成(<em>${completedOrderCount}</em>)</a>
									</li>
									<li class="pendDeliver">
										<a href="order/list.jhtml?orderStatus=pendingShipment"><span></span>待发货(<em>${pendingShipmentOrderCount}</em>)</a>
									</li>
								</ul>
							</div>	
						</div>
					</div>
					<div class="list orderListWrap">
						<div class="title">最近订单</div>
						[#if newOrders?has_content]
						<table class="list">
							<tr>
								<th style="width:15%">
									${message("OrderItem.product")}
								</th>
								<th style="width:25%">
									${message("OrderItem.name")}
								</th>
								<th style="width:10%">
									收货人
								</th>
								<th style="width:10%">
									${message("Order.amount")}
								</th>
								<th style="width:10%">
									创建日期
								</th>
								<th style="width:10%">
									${message("Order.status")}
								</th>
								<th style="width:30%">
									${message("shop.member.action")}
								</th>
							</tr>
							[#list newOrders as order]
								[#list order.orderItems as orderItem]
									<tr>
										[#if orderItem_index < 1]
											<td class="lhzero" rowspan="${order.orderItems?size}">
												<span>${order.sn}</span>
											</td>
										[/#if]
										
										<td style="text-align: left;" class="goodImageInfo">
											<img src="${orderItem.thumbnail}" width="80" height="80" />
											<p>${abbreviate(orderItem.name, 50, "...")}</p>
										</td>
										[#if orderItem_index < 1]
											<td class="lhzero" rowspan="${order.orderItems?size}">
												<span>${order.consignee}</span>
											</td>
										[/#if]
										[#if orderItem_index < 1]
											<td class="lhzero" rowspan="${order.orderItems?size}">
												${currency(order.amount, true)}
											</td>
										[/#if]
										[#if orderItem_index < 1]
											<td class="lhzero" rowspan="${order.orderItems?size}">
												${order.createdDate}
											</td>
										[/#if]
										<td class="lhzero">
											<span class="setBlock">${message("Order.Status." + order.status)}</span>
											[#if order.hasExpired()]
												<span class="silver">(${message("shop.business.order.hasExpired")})</span>
											[#elseif order.refundAmount==order.amount &&order.refundAmount>0 &&order.status=="failed"]
												<span class="red">(已退款)</span>
											[#elseif order.amount>order.refundAmount &&order.refundAmount>0 &&order.status=="failed"]
												<span class="red">(已部分退款)</span>	
											[/#if]
										</td>
										[#if orderItem_index < 1]
											<td class="lhzero" rowspan="${order.orderItems?size}">
												<a href="order/view.jhtml?sn=${order.sn}">查看</a>
											</td>
										[/#if]	
										
									</tr>	
								[/#list]
							[/#list]						
						</table>	
						[#else]
							<p class="noResult">${message("shop.member.noResult")}</p>
						[/#if]	
					</div>
				</div>
			</div>
		</div>
		[#include "/shop/${theme}/include/footer.ftl" /]
	</div>
</body>
</html>
[/#escape]
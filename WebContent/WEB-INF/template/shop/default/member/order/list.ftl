[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.member.order.list")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {
	
	[@flash_message /]
	
});
</script>
</head>
<body>
	[#assign current = "orderList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="wrap">
		<div class="container member">
			<div class="row">
				[#include "/shop/${theme}/member/include/navigation.ftl" /]
				<div class="span10">
					<div class="list orderListWrap">
						<div class="title">${message("shop.member.order.list")}</div>
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
							
										
							[#list page.content as order]
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
												<a href="view.jhtml?sn=${order.sn}">查看</a>
											</td>
										[/#if]	
										
									</tr>
								[/#list]
							[/#list]						
						</table>		
						[#if !page.content?has_content]
							<p>${message("shop.member.noResult")}</p>
						[/#if]
					</div>
					[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}&orderStatus=${orderStatus}"]
						[#include "/shop/${theme}/include/pagination.ftl"]
					[/@pagination]
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
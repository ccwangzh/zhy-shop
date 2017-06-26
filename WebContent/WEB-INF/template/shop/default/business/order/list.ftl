[#assign shiro = JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.order.list")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/list.js"></script>
<style type="text/css">
.moreTable th {
	width: 80px;
	line-height: 25px;
	padding: 5px 10px 5px 0px;
	text-align: right;
	font-weight: normal;
	color: #333333;
}

.moreTable td {
	line-height: 25px;
	padding: 5px;
	color: #666666;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterMenu = $("#filterMenu");
	var $filterMenuItem = $("#filterMenu li");
	var $moreButton = $("#moreButton");
	var $print = $("#listTable select[name='print']");
	
	[@flash_message /]
	
	// 筛选菜单
	$filterMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	// 筛选
	$filterMenuItem.click(function() {
		var $this = $(this);
		var $dest = $("#" + $this.attr("name"));
		if ($this.hasClass("checked")) {
			$dest.val("");
		} else {
			$dest.val($this.attr("val"));
		}
		$listForm.submit();
	});
	
	// 更多选项
	$moreButton.click(function() {
		$.dialog({
			title: "${message("shop.business.order.moreOption")}",
			content: 
				[@compress single_line = true]
					'<table id="moreTable" class="moreTable">
						<tr>
							<th>
								${message("Order.type")}:
							<\/th>
							<td>
								<select name="type">
									<option value="">${message("shop.common.choose")}<\/option>
									[#list types as value]
										<option value="${value}"[#if value == type] selected="selected"[/#if]>${message("Order.Type." + value)}<\/option>
									[/#list]
								<\/select>
							<\/td>
						<\/tr>
						<tr>
							<th>
								${message("Order.status")}:
							<\/th>
							<td>
								<select name="status">
									<option value="">${message("shop.common.choose")}<\/option>
									[#list statuses as value]
										<option value="${value}"[#if value == status] selected="selected"[/#if]>${message("Order.Status." + value)}<\/option>
									[/#list]
								<\/select>
							<\/td>
						<\/tr>
						<tr>
							<th>
								${message("shop.business.order.memberUsername")}:
							<\/th>
							<td>
								<input type="text" name="memberUsername" class="text" value="${memberUsername}" maxlength="200" \/>
							<\/td>
						<\/tr>
					<\/table>'
				[/@compress]
			,
			width: 470,
			modal: true,
			ok: "${message("shop.dialog.ok")}",
			cancel: "${message("shop.dialog.cancel")}",
			onOk: function() {
				$("#moreTable :input").each(function() {
					var $this = $(this);
					$("#" + $this.attr("name")).val($this.val());
				});
				$listForm.submit();
			}
		});
	});
	
	// 打印选择
	$print.change(function() {
		var $this = $(this);
		if ($this.val() != "") {
			window.open($this.val());
		}
	});

});
</script>
</head>
<body>
	[#assign current = "orderList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.order.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<input type="hidden" id="type" name="type" value="${type}" />
						<input type="hidden" id="status" name="status" value="${status}" />
						<input type="hidden" id="memberUsername" name="memberUsername" value="${memberUsername}" />
						<input type="hidden" id="isPendingReceive" name="isPendingReceive" value="${(isPendingReceive?string("true", "false"))!}" />
						<input type="hidden" id="isPendingRefunds" name="isPendingRefunds" value="${(isPendingRefunds?string("true", "false"))!}" />
						<input type="hidden" id="isAllocatedStock" name="isAllocatedStock" value="${(isAllocatedStock?string("true", "false"))!}" />
						<input type="hidden" id="hasExpired" name="hasExpired" value="${(hasExpired?string("true", "false"))!}" />
						<div class="bar">
							<div class="buttonGroup">
								<a href="javascript:;" id="deleteButton" class="iconButton disabled">
									<span class="deleteIcon">&nbsp;</span>${message("shop.common.delete")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
								<div id="filterMenu" class="dropdownMenu">
									<a href="javascript:;" class="button">
										${message("shop.business.order.filter")}<span class="arrow">&nbsp;</span>
									</a>
									<ul class="check">
										<li name="isPendingReceive"[#if isPendingReceive?? && isPendingReceive] class="checked"[/#if] val="true">${message("shop.business.order.pendingReceive")}</li>
										<li name="isPendingReceive"[#if isPendingReceive?? && !isPendingReceive] class="checked"[/#if] val="false">${message("shop.business.order.unPendingReceive")}</li>
										<li class="divider">&nbsp;</li>
										<li name="isPendingRefunds"[#if isPendingRefunds?? && isPendingRefunds] class="checked"[/#if] val="true">${message("shop.business.order.pendingRefunds")}</li>
										<li name="isPendingRefunds"[#if isPendingRefunds?? && !isPendingRefunds] class="checked"[/#if] val="false">${message("shop.business.order.unPendingRefunds")}</li>
										<li class="divider">&nbsp;</li>
										<li name="isAllocatedStock"[#if isAllocatedStock?? && isAllocatedStock] class="checked"[/#if] val="true">${message("shop.business.order.allocatedStock")}</li>
										<li name="isAllocatedStock"[#if isAllocatedStock?? && !isAllocatedStock] class="checked"[/#if] val="false">${message("shop.business.order.unAllocatedStock")}</li>
										<li class="divider">&nbsp;</li>
										<li name="hasExpired"[#if hasExpired?? && hasExpired] class="checked"[/#if] val="true">${message("shop.business.order.hasExpired")}</li>
										<li name="hasExpired"[#if hasExpired?? && !hasExpired] class="checked"[/#if] val="false">${message("shop.business.order.unexpired")}</li>
									</ul>
								</div>
								<a href="javascript:;" id="moreButton" class="button">${message("shop.business.order.moreOption")}</a>
								<div id="pageSizeMenu" class="dropdownMenu">
									<a href="javascript:;" class="button">
										${message("shop.page.pageSize")}<span class="arrow">&nbsp;</span>
									</a>
									<ul>
										<li[#if page.pageSize == 10] class="current"[/#if] val="10">10</li>
										<li[#if page.pageSize == 20] class="current"[/#if] val="20">20</li>
										<li[#if page.pageSize == 50] class="current"[/#if] val="50">50</li>
										<li[#if page.pageSize == 100] class="current"[/#if] val="100">100</li>
									</ul>
								</div>
							</div>
							<div id="searchPropertyMenu" class="dropdownMenu">
								<div class="search">
									<span class="arrow">&nbsp;</span>
									<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
									<button type="submit">&nbsp;</button>
								</div>
								<ul>
									<li[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Order.sn")}</li>
									<li[#if page.searchProperty == "consignee"] class="current"[/#if] val="consignee">${message("Order.consignee")}</li>
									<li[#if page.searchProperty == "areaName"] class="current"[/#if] val="areaName">${message("Order.area")}</li>
									<li[#if page.searchProperty == "address"] class="current"[/#if] val="address">${message("Order.address")}</li>
									<li[#if page.searchProperty == "zipCode"] class="current"[/#if] val="zipCode">${message("Order.zipCode")}</li>
									<li[#if page.searchProperty == "phone"] class="current"[/#if] val="phone">${message("Order.phone")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th class="check">
									<input type="checkbox" id="selectAll" />
								</th>
								<th>
									<a href="javascript:;" class="sort" name="sn">${message("Order.sn")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="amount">${message("Order.amount")}</a>
								</th>
								<th>
                                    <a href="javascript:;" class="sort" name="amount">${message("Order.quantity")}</a>
                                </th>
								<th>
									<a href="javascript:;" class="sort" name="member">${message("Order.member")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="consignee">${message("Order.consignee")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="paymentMethodName">${message("Order.paymentMethod")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="shippingMethodName">${message("Order.shippingMethod")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="status">${message("Order.status")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="createdDate">${message("shop.common.createdDate")}</a>
								</th>
								<th>
									<span>${message("shop.business.order.print")}</span>
								</th>
								<th>
									<span>${message("shop.common.action")}</span>
								</th>
							</tr>
							[#list page.content as order]
								<tr>
									<td>
										<input type="checkbox" name="ids" value="${order.id}" />
									</td>
									<td>
										${order.sn}
									</td>
									<td>
										${currency(order.amount, true)}
									</td>
									<td>
                                        ${order.quantity}
                                    </td>
									<td>
										${order.member.username}
									</td>
									<td>
										${order.consignee}
									</td>
									<td>
										${order.paymentMethodName}
									</td>
									<td>
										${order.shippingMethodName}
									</td>
									<td>
										${message("Order.Status." + order.status)}
											[#if order.hasExpired()]
												<span class="silver">(${message("shop.business.order.hasExpired")})</span>
											[#elseif order.refundAmount==order.amount &&order.refundAmount>0 &&order.status=="failed"]
												<span class="red">(已退款)</span>
											[#elseif order.amount>order.refundAmount &&order.refundAmount>0 &&order.status=="failed"]
												<span class="red">(已部分退款)</span>	
											[/#if]
										
									</td>
									<td>
										<span title="${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${order.createdDate}</span>
									</td>
									<td>
										<select name="print">
											<option value="">${message("shop.common.choose")}</option>
											<option value="../print/order.jhtml?id=${order.id}">${message("shop.business.order.orderPrint")}</option>
											<option value="../print/product.jhtml?id=${order.id}">${message("shop.business.order.productPrint")}</option>
											<option value="../print/shipping.jhtml?id=${order.id}">${message("shop.business.order.shippingPrint")}</option>
											[#if order.isDelivery]
												<option value="../print/delivery.jhtml?orderId=${order.id}">${message("shop.business.order.deliveryPrint")}</option>
											[/#if]
										</select>
									</td>
									<td>
										<a href="view.jhtml?id=${order.id}">[${message("shop.common.view")}]</a>
										[#if !order.hasExpired() && (order.status == "pendingPayment" || order.status == "pendingReview")]
											<a href="edit.jhtml?id=${order.id}">[${message("shop.common.edit")}]</a>
										[#else]
											<span title="${message("shop.business.order.editNotAllowed")}">[${message("shop.common.edit")}]</span>
										[/#if]
									</td>
								</tr>
							[/#list]
						</table>
						[#if !page.content?has_content]
							<p>${message("shop.business.noResult")}</p>
						[/#if]
						[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "javascript: $.pageSkip({pageNumber});"]
							[#include "/shop/${theme}/include/pagination.ftl"]
						[/@pagination]
					</form>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]	
</body>
</html>
[/#escape]
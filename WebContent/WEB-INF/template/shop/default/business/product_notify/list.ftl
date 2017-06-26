[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.productNotify.list")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $selectAll = $("#selectAll");
	var $ids = $("#listTable input[name='ids']");
	var $sendButton = $("#sendButton");
	var $filterMenu = $("#filterMenu");
	var $filterMenuItem = $("#filterMenu li");
	
	[@flash_message /]
	
	// 发送到货通知
	$sendButton.click(function() {
		if ($sendButton.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $ids.filter(":enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("shop.business.productNofity.sendConfirm")}",
			ok: "${message("shop.dialog.ok")}",
			cancel: "${message("shop.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "send.jhtml",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$checkedIds.closest("td").siblings(".hasSent").html('<span title="${message("shop.business.productNotify.hasSent")}" class="trueIcon">&nbsp;<\/span>');
						}
					}
				});
			}
		});
	});
	
	// 全选
	$selectAll.click(function() {
		var $this = $(this);
		var $enabledIds = $ids.filter(":enabled");
		if ($this.prop("checked")) {
			if ($enabledIds.filter(":checked").size() > 0) {
				$sendButton.removeClass("disabled");
			} else {
				$sendButton.addClass("disabled");
			}
		} else {
			$sendButton.addClass("disabled");
		}
	});
	
	// 选择
	$ids.click(function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$sendButton.removeClass("disabled");
		} else {
			if ($ids.filter(":enabled:checked").size() > 0) {
				$sendButton.removeClass("disabled");
			} else {
				$sendButton.addClass("disabled");
			}
		}
	});
	
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

});
</script>
</head>
<body>
	[#assign current = "productNotifyList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.productNotify.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<input type="hidden" id="status" name="status" value="${status}" />
						<input type="hidden" id="isOutOfStock" name="isOutOfStock" value="[#if isOutOfStock??]${isOutOfStock?string("true", "false")}[/#if]" />
						<input type="hidden" id="hasSent" name="hasSent" value="[#if hasSent??]${hasSent?string("true", "false")}[/#if]" />
						<div class="bar">
							<div class="buttonGroup">
								<a href="javascript:;" id="sendButton" class="button disabled">
									${message("shop.business.productNotify.send")}
								</a>
								<a href="javascript:;" id="deleteButton" class="iconButton disabled">
									<span class="deleteIcon">&nbsp;</span>${message("shop.common.delete")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
								<div id="filterMenu" class="dropdownMenu">
									<a href="javascript:;" class="button">
										${message("shop.business.productNotify.filter")}<span class="arrow">&nbsp;</span>
									</a>
									<ul class="check">
										[#list statusVal as value]
                                            <li name="status" val="${value}"[#if value == status] class="checked"[/#if]>${message("Goods.status." + value)}</li>
                                        [/#list]
										<li class="divider">&nbsp;</li>
										<li name="isOutOfStock"[#if isOutOfStock] class="checked"[/#if] val="true">${message("shop.business.productNotify.outOfStock")}</li>
										<li name="isOutOfStock"[#if isOutOfStock?? && !isOutOfStock] class="checked"[/#if] val="false">${message("shop.business.productNotify.inStock")}</li>
										<li class="divider">&nbsp;</li>
										<li name="hasSent"[#if hasSent] class="checked"[/#if] val="true">${message("shop.business.productNotify.hasSent")}</li>
										<li name="hasSent"[#if hasSent?? && !hasSent] class="checked"[/#if] val="false">${message("shop.business.productNotify.hasNotSent")}</li>
									</ul>
								</div>
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
									<li[#if page.searchProperty == "email"] class="current"[/#if] val="email">${message("ProductNotify.email")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th class="check">
									<input type="checkbox" id="selectAll" />
								</th>
								<th>
									<span>${message("shop.business.productNotify.productName")}</span>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="member">${message("ProductNotify.member")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="email">${message("ProductNotify.email")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="createdDate">${message("shop.business.productNotify.createdDate")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="lastModifiedDate">${message("shop.business.productNotify.notifyDate")}</a>
								</th>
								<th>
									<span>${message("shop.business.productNotify.status")}</span>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="hasSent">${message("shop.business.productNotify.hasSent")}</a>
								</th>
							</tr>
							[#list page.content as productNotify]
								<tr>
									<td>
										<input type="checkbox" name="ids" value="${productNotify.id}" />
									</td>
									<td>
										<a href="${productNotify.product.url}" title="${productNotify.product.name}" target="_blank">${abbreviate(productNotify.product.name, 50, "...")}</a>
										[#if productNotify.product.specifications?has_content]
											<span class="silver">[${productNotify.product.specifications?join(", ")}]</span>
										[/#if]
									</td>
									<td>
										[#if productNotify.member??]
											<a href="../member/view.jhtml?id=${productNotify.member.id}">${productNotify.member.username}</a>
										[#else]
											-
										[/#if]
									</td>
									<td>
										${productNotify.email}
									</td>
									<td>
										<span title="${productNotify.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${productNotify.createdDate}</span>
									</td>
									<td>
										[#if productNotify.hasSent]
											<span title="${productNotify.lastModifiedDate?string("yyyy-MM-dd HH:mm:ss")}">${productNotify.lastModifiedDate}</span>
										[#else]
											-
										[/#if]
									</td>
									<td>
										[#if productNotify.product.isMarketable]
											<span class="green">${message("shop.business.productNotify.marketable")}</span>
										[#else]
											${message("shop.business.productNotify.notMarketable")}
										[/#if]
										[#if productNotify.product.isOutOfStock]
											${message("shop.business.productNotify.outOfStock")}
										[#else]
											<span class="green">${message("shop.business.productNotify.inStock")}</span>
										[/#if]
									</td>
									<td class="hasSent">
										<span title="${productNotify.hasSent?string(message("shop.business.productNotify.hasSent"), message("shop.business.productNotify.hasNotSent"))}" class="${productNotify.hasSent?string("true", "false")}Icon">&nbsp;</span>
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
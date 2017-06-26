[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.goods.list")}[#if showPowered][/#if]</title>
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

.promotion {
	color: #cccccc;
}

.stockAlert {
	color: orange;
}

.outOfStock {
	color: red;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterMenu = $("#filterMenu");
	var $filterMenuItem = $("#filterMenu li");
	var $moreButton = $("#moreButton");
	var $shelvesButton = $("#shelvesButton");
	var $shelfButton = $("#shelfButton");
	var $selectAll = $("#selectAll");
	var $addButton = $("#addButton");
	var $ids = $("#listTable input[name='ids']");
	
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
			title: "${message("shop.goods.moreFilter")}",
			[@compress single_line = true]
				content: '
				<table id="moreTable" class="moreTable">
					<tr>
						<th>
							${message("Goods.productCategory")}:
						<\/th>
						<td>
							<select name="productCategoryId">
								<option value="">${message("shop.common.choose")}<\/option>
								[#list productCategoryTree as productCategory]
									<option value="${productCategory.id}"[#if productCategory.id == productCategoryId] selected="selected"[/#if]>
										[#if productCategory.grade != 0]
											[#list 1..productCategory.grade as i]
												&nbsp;&nbsp;
											[/#list]
										[/#if]
										[#noescape]
											${productCategory.name?html?js_string}
										[/#noescape]
									<\/option>
								[/#list]
							<\/select>
						<\/td>
					<\/tr>
					<tr>
						<th>
							${message("Goods.type")}:
						<\/th>
						<td>
							<select name="type">
								<option value="">${message("shop.common.choose")}<\/option>
								[#list types as value]
									<option value="${value}"[#if value == type] selected="selected"[/#if]>${message("Goods.Type." + value)}<\/option>
								[/#list]
							<\/select>
						<\/td>
					<\/tr>
					<tr>
						<th>
							${message("Goods.brand")}:
						<\/th>
						<td>
							<select name="brandId">
								<option value="">${message("shop.common.choose")}<\/option>
								[#list brands as brand]
									<option value="${brand.id}"[#if brand.id == brandId] selected="selected"[/#if]>
										[#noescape]
											${brand.name?html?js_string}
										[/#noescape]
									<\/option>
								[/#list]
							<\/select>
						<\/td>
					<\/tr>
					<tr>
						<th>
							${message("Goods.tags")}:
						<\/th>
						<td>
							<select name="tagId">
								<option value="">${message("shop.common.choose")}<\/option>
								[#list tags as tag]
									<option value="${tag.id}"[#if tag.id == tagId] selected="selected"[/#if]>
										[#noescape]
											${tag.name?html?js_string}
										[/#noescape]
									<\/option>
								[/#list]
							<\/select>
						<\/td>
					<\/tr>
					<tr>
						<th>
							${message("Goods.promotions")}:
						<\/th>
						<td>
							<select name="promotionId">
								<option value="">${message("shop.common.choose")}<\/option>
								[#list promotions as promotion]
									<option value="${promotion.id}"[#if promotion.id == promotionId] selected="selected"[/#if]>
										[#noescape]
											${promotion.name?html?js_string}
										[/#noescape]
									<\/option>
								[/#list]
							<\/select>
						<\/td>
					<\/tr>
				<\/table>',
			[/@compress]
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
	
	// 上架
	$shelvesButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("shop.dialog.shelvesConfirm")}",
			ok: "${message("shop.dialog.ok")}",
			cancel: "${message("shop.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "shelves.jhtml",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if(message.type == "success"){
							$shelvesButton.addClass("disabled");
							$selectAll.prop("checked", false);
							$checkedIds.prop("checked", false);
							location.reload(true);
						}
					}
				});
			}
		});
		return false;
	});
	
	// 下架
	$shelfButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("shop.dialog.shelfConfirm")}",
			ok: "${message("shop.dialog.ok")}",
			cancel: "${message("shop.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "shelf.jhtml",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if(message.type == "success"){
							$shelfButton.addClass("disabled");
							$selectAll.prop("checked", false);
							$checkedIds.prop("checked", false);
							location.reload(true);
						}
					}
				});
			}
		});
		return false;
	});

	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
			if ($enabledIds.filter(":checked").size() > 0) {
				$shelvesButton.removeClass("disabled");
				$shelfButton.removeClass("disabled");
			} else {
				$shelvesButton.addClass("disabled");
				$shelfButton.addClass("disabled");
			}
		} else {
			$shelvesButton.addClass("disabled");
			$shelfButton.addClass("disabled");
		}
	});
	
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$shelvesButton.removeClass("disabled");
			$shelfButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$shelvesButton.removeClass("disabled");
				$shelfButton.removeClass("disabled");
			} else {
				$shelvesButton.addClass("disabled");
				$shelfButton.addClass("disabled");
			}
		}
	});
	
	//商品发布
	$addButton.click( function() { 
		$.ajax({
			url: "check_store_goods.jhtml",
			type: "GET",
			dataType: "json",
			cache: false,
			success: function(data) {
				if(data){
					$.message("warn", "${message("shop.business.goods.isAddGoods")}");
					return false;
				}else{
					location.href = "add.jhtml";
				}
			}
		});
		
	});

	
});
</script>
</head>
<body>
	[#assign current = "goodsList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.goods.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<input type="hidden" id="type" name="type" value="${type}" />
						<input type="hidden" id="productCategoryId" name="productCategoryId" value="${productCategoryId}" />
						<input type="hidden" id="brandId" name="brandId" value="${brandId}" />
						<input type="hidden" id="tagId" name="tagId" value="${tagId}" />
						<input type="hidden" id="promotionId" name="promotionId" value="${promotionId}" />
						<input type="hidden" id="isActive" name="isActive" value="${(isActive?string("true", "false"))!}" />
						<input type="hidden" id="status" name="status" value="${status}" />
						<input type="hidden" id="isList" name="isList" value="${(isList?string("true", "false"))!}" />
						<input type="hidden" id="isTop" name="isTop" value="${(isTop?string("true", "false"))!}" />
						<input type="hidden" id="isOutOfStock" name="isOutOfStock" value="${(isOutOfStock?string("true", "false"))!}" />
						<input type="hidden" id="isStockAlert" name="isStockAlert" value="${(isStockAlert?string("true", "false"))!}" />
						<div class="bar">
							<a href="javascript:;" id="addButton" class="iconButton">
								<span class="addIcon">&nbsp;</span>${message("shop.common.add")}
							</a>
							<div class="buttonGroup">
								<a href="javascript:;" id="deleteButton" class="iconButton disabled">
									<span class="deleteIcon">&nbsp;</span>${message("shop.common.delete")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
								<a href="javascript:;" id="shelvesButton" class="iconButton disabled">
									<span class="upIcon">&nbsp;</span>${message("shop.common.shelves")}
								</a>
								<a href="javascript:;" id="shelfButton" class="iconButton disabled">
									<span class="downIcon">&nbsp;</span>${message("shop.common.shelf")}
								</a>
								<div id="filterMenu" class="dropdownMenu">
									<a href="javascript:;" class="button">
										${message("shop.goods.filter")}<span class="arrow">&nbsp;</span>
									</a>
									<ul class="check">
										<li name="isActive"[#if isActive?? && isActive] class="checked"[/#if] val="true">${message("shop.business.goods.isActive")}</li>
										<li name="isActive"[#if isActive?? && !isActive] class="checked"[/#if] val="false">${message("shop.business.goods.notActive")}</li>
										<li class="divider">&nbsp;</li>
										[#list statusVal as value]
                                            <li name="status" val="${value}"[#if value == status] class="checked"[/#if]>${message("Goods.status." + value)}</li>
                                        [/#list]
										<li class="divider">&nbsp;</li>
										<li name="isList"[#if isList?? && isList] class="checked"[/#if] val="true">${message("shop.business.goods.isList")}</li>
										<li name="isList"[#if isList?? && !isList] class="checked"[/#if] val="false">${message("shop.business.goods.notList")}</li>
										<li class="divider">&nbsp;</li>
										<li name="isTop"[#if isTop?? && isTop] class="checked"[/#if] val="true">${message("shop.business.goods.isTop")}</li>
										<li name="isTop"[#if isTop?? && !isTop] class="checked"[/#if] val="false">${message("shop.business.goods.notTop")}</li>
										<li class="divider">&nbsp;</li>
										<li name="isOutOfStock"[#if isOutOfStock?? && !isOutOfStock] class="checked"[/#if] val="false">${message("shop.business.goods.isStack")}</li>
										<li name="isOutOfStock"[#if isOutOfStock?? && isOutOfStock] class="checked"[/#if] val="true">${message("shop.business.goods.isOutOfStack")}</li>
										<li class="divider">&nbsp;</li>
										<li name="isStockAlert"[#if isStockAlert?? && !isStockAlert] class="checked"[/#if] val="false">${message("shop.business.goods.normalStore")}</li>
										<li name="isStockAlert"[#if isStockAlert?? && isStockAlert] class="checked"[/#if] val="true">${message("shop.business.goods.isStockAlert")}</li>
									</ul>
								</div>
								<a href="javascript:;" id="moreButton" class="button">${message("shop.business.goods.moreOption")}</a>
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
									<li[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Goods.sn")}</li>
									<li[#if page.searchProperty == "name"] class="current"[/#if] val="name">${message("Goods.name")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th class="check">
									<input type="checkbox" id="selectAll" name="selectAll"/>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="sn">${message("Goods.sn")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="name">${message("Goods.name")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="productCategory">${message("Goods.productCategory")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="price">${message("Goods.price")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="status">${message("Goods.status")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="isActive">${message("Goods.isActive")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="createdDate">${message("shop.common.createdDate")}</a>
								</th>
								<th>
									<span>${message("shop.common.action")}</span>
								</th>
							</tr>
							[#list page.content as goods]
								<tr>
									<td>
										<input type="checkbox" name="ids" value="${goods.id}" class="check"/>
									</td>
									<td>
										<span[#if goods.isOutOfStock] class="red"[#elseif goods.isStockAlert] class="blue"[/#if]>
											${goods.sn}
										</span>
									</td>
									<td>
										<span title="${goods.name}">
											${abbreviate(goods.name, 50, "...")}
										</span>
										[#if goods.type != "general"]
											<span class="red">*</span>
										[/#if]
										[#list goods.validPromotions as promotion]
											<span class="promotion" title="${promotion.title}">${promotion.name}</span>
										[/#list]
									</td>
									<td>
										${goods.productCategory.name}
									</td>
									<td>
										${currency(goods.price, true)}
									</td>
									<td>
										<span name= "statusSpan" 
                                        [#if goods.status=='onTheshelf' ] class="green"[/#if]
                                        [#if goods.status=='offTheshelf'] class="blue"[/#if]
                                        [#if goods.status=='unCheck' || goods.status=='pendingCheck' || goods.status=='failed']class="red" [/#if]>
                                        ${message("Goods.status." + goods.status)}</span>
                									</td>
									<td>
										<span class="${goods.isActive?string("true", "false")}Icon">&nbsp;</span>
									</td>
									<td>
										<span title="${goods.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${goods.createdDate}</span>
									</td>
									<td>
										<a href="edit.jhtml?sn=${goods.sn}">[${message("shop.common.edit")}]</a>
										[#if goods.status=='onTheshelf' && goods.isActive]
											<a href="${goods.url}" target="_blank">[${message("shop.common.view")}]</a>
										[#else]
											<span title="${message("shop.business.goods.notMarketable")}">[${message("shop.common.view")}]</span>
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
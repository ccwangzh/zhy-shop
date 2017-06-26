[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.stock.log")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
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
	[#assign current = "stockList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.stock.log")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="log.jhtml" method="get">
						<div class="bar">
							<div class="buttonGroup">
								<a href="stock_in.jhtml" class="button">
									${message("shop.business.stock.stockIn")}
								</a>
								<a href="stock_out.jhtml" class="button">
									${message("shop.business.stock.stockOut")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
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
									<li[#if page.searchProperty == "product.sn"] class="current"[/#if] val="product.sn">${message("Product.sn")}</li>
									<li[#if page.searchProperty == "operator.username"] class="current"[/#if] val="operator.username">${message("StockLog.operator")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th>
									<a href="javascript:;" class="sort" name="product.sn">${message("Product.sn")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="product">${message("StockLog.product")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="type">${message("StockLog.type")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="inQuantity">${message("StockLog.inQuantity")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="outQuantity">${message("StockLog.outQuantity")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="stock">${message("StockLog.stock")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="operator.username">${message("StockLog.operator")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="operator.type">${message("StockLog.Operator.type")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="memo">${message("StockLog.memo")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="createdDate">${message("shop.common.createdDate")}</a>
								</th>
							</tr>
							[#list page.content as stockLog]
								<tr>
									<td>
										${stockLog.product.sn}
									</td>
									<td>
										<span title="${stockLog.product.name}">${abbreviate(stockLog.product.name, 50, "...")}</span>
										[#if stockLog.product.specifications?has_content]
											<span class="silver">[${stockLog.product.specifications?join(", ")}]</span>
										[/#if]
									</td>
									<td>
										${message("StockLog.Type." + stockLog.type)}
									</td>
									<td>
										${stockLog.inQuantity}
									</td>
									<td>
										${stockLog.outQuantity}
									</td>
									<td>
										${stockLog.stock}
									</td>
									<td>
										${stockLog.operator.username}
									</td>
									<td>
										[#if stockLog.operator.type??]
											${message("Operator.Type."+stockLog.operator.type)}
										[/#if]
									</td>
									<td>
										[#if stockLog.memo??]
											<span title="${stockLog.memo}">${abbreviate(stockLog.memo, 50, "...")}</span>
										[/#if]
									</td>
									<td>
										<span title="${stockLog.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${stockLog.createdDate}</span>
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
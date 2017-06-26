[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.coupon.list")}</title>
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
	[#assign current = "couponList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						 ${message("shop.business.coupon.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<div class="bar">
							<a href="add.jhtml" class="iconButton">
								<span class="addIcon">&nbsp;</span>${message("shop.common.add")}
							</a>
							<div class="buttonGroup">
								<a href="javascript:;" id="deleteButton" class="iconButton disabled">
									<span class="deleteIcon">&nbsp;</span>${message("shop.common.delete")}
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
									<li[#if page.searchProperty == "name"] class="current"[/#if] val="name">${message("Coupon.name")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th class="check">
									<input type="checkbox" id="selectAll" />
								</th>
								<th>
									<a href="javascript:;" class="sort" name="name">${message("Coupon.name")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="prefix">${message("Coupon.prefix")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="beginDate">${message("Coupon.beginDate")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="endDate">${message("Coupon.endDate")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="isEnabled">${message("Coupon.isEnabled")}</a>
								</th>
								<th>
									<span>${message("shop.common.action")}</span>
								</th>
							</tr>
							[#list page.content as coupon]
								<tr>
									<td>
										<input type="checkbox" name="ids" value="${coupon.id}" />
									</td>
									<td>
										${coupon.name}
									</td>
									<td>
										${coupon.prefix}
									</td>
									<td>
										[#if coupon.beginDate??]
											<span title="${coupon.beginDate?string("yyyy-MM-dd HH:mm:ss")}">${coupon.beginDate}</span>
										[#else]
											-
										[/#if]
									</td>
									<td>
										[#if coupon.endDate??]
											<span title="${coupon.endDate?string("yyyy-MM-dd HH:mm:ss")}">${coupon.endDate}</span>
										[#else]
											-
										[/#if]
									</td>
									<td>
										<span class="${coupon.isEnabled?string("true", "false")}Icon">&nbsp;</span>
									</td>
									<td>
										<a href="generate.jhtml?id=${coupon.id}">[${message("shop.business.coupon.generate")}]</a>
										<a href="edit.jhtml?id=${coupon.id}">[${message("shop.common.edit")}]</a>
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
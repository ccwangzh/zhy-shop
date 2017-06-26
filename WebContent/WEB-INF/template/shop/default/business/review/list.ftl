[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.review.list")}[#if showPowered][/#if]</title>
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
	var $type = $("#type");
	var $typeMenu = $("#typeMenu");
	var $typeMenuItem = $("#typeMenu li");

	[@flash_message /]
	
	$typeMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$typeMenuItem.click(function() {
		$type.val($(this).attr("val"));
		$listForm.submit();
	});

});
</script>
</head>
<body>
	[#assign current = "reviewList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						 ${message("shop.business.review.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<input type="hidden" id="type" name="type" value="${type}" />
						<div class="bar">
							<div class="buttonGroup">
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
								<div id="typeMenu" class="dropdownMenu">
									<a href="javascript:;" class="button">
										${message("shop.review.type")}<span class="arrow">&nbsp;</span>
									</a>
									<ul>
										<li[#if type == null] class="current"[/#if] val="">${message("shop.review.allType")}</li>
										[#assign currentType = type]
										[#list types as type]
											<li[#if type == currentType] class="current"[/#if] val="${type}">${message("Review.Type." + type)}</li>
										[/#list]
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
									<li[#if page.searchProperty == "content"] class="current"[/#if] val="content">${message("Review.content")}</li>
								</ul>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th>
									<a href="javascript:;" class="sort" name="goods">${message("Review.goods")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="score">${message("Review.score")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="content">${message("Review.content")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="member">${message("Review.member")}</a>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="isShow">${message("Review.isShow")}</a>
								</th>
								<th>
									<span>${message("shop.review.isReply")}</span>
								</th>
								<th>
									<a href="javascript:;" class="sort" name="createdDate">${message("shop.common.createdDate")}</a>
								</th>
								<th>
									<span>${message("shop.common.action")}</span>
								</th>
							</tr>
							[#list page.content as review]
								<tr>
									<td>
										<a href="${review.goods.url}" title="${review.goods.name}" target="_blank">${abbreviate(review.goods.name, 50, "...")}</a>
									</td>
									<td>
										${review.score}
									</td>
									<td>
										<span title="${review.content}">${abbreviate(review.content, 50, "...")}</span>
									</td>
									<td>
										[#if review.member??]
											${review.member.username}
										[#else]
											${message("shop.review.anonymous")}
										[/#if]
									</td>
									<td>
										<span class="${review.isShow?string("true", "false")}Icon">&nbsp;</span>
									</td>
									<td>
										<span class="${review.replyReviews?has_content?string("true", "false")}Icon">&nbsp;</span>
									</td>
									<td>
										<span title="${review.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${review.createdDate}</span>
									</td>
									<td>
										<a href="reply.jhtml?id=${review.id}">[${message("shop.review.reply")}]</a>
										<a href="edit.jhtml?id=${review.id}">[${message("shop.common.edit")}]</a>
										<a href="${base}${review.path}" target="_blank">[${message("shop.common.view")}]</a>
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
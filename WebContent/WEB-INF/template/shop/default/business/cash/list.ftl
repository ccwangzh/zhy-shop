[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.cash.list")}[#if showPowered][/#if]</title>
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
	[#assign current = "cashList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.cash.list")} <span>(${message("shop.page.total", page.total)})</span>
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<div class="bar">
							<div class="buttonGroup">
								<a href="application.jhtml" class="iconButton">
									<span class="addIcon">&nbsp;</span>${message("shop.business.cash.application")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
							</div>
						</div>
						<table class="list">
							<tr>
								<th>
									${message("Cash.amount")}
								</th>
								<th>
									${message("Cash.bank")}
								</th>
								<th>
									${message("Cash.account")}
								</th>
								<th>
									${message("Cash.status")}
								</th>
								<th>
									${message("shop.common.createdDate")}
								</th>
							</tr>
							[#list page.content as cash]
								<tr[#if !cash_has_next] class="last"[/#if]>
									<td>
										${currency(cash.amount)}
									</td>
									<td>
										${cash.bank}
									</td>
									<td>
										${cash.account}
									</td>
									<td>
										<span [#if cash.status="pending" || cash.status="failed" ]class="red"[#elseif cash.status="approved"]class="green"[/#if]>${message("Cash.Status." + cash.status)}</span>
									</td>
									<td>
										<span title="${cash.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${cash.createdDate}</span>
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
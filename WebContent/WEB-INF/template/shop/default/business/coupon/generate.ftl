[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.coupon.generate")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $totalCount = $("#totalCount");
	var $count = $("#count");
	var totalCount = ${totalCount};
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			count: {
				required: true,
				integer: true,
				min: 1
			}
		},
		submitHandler: function(form) {
			totalCount = totalCount + parseInt($count.val());
			$totalCount.text(totalCount);
			form.submit();
		}
	});

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
						 ${message("shop.business.coupon.list")}
					</div>
					<form id="inputForm" action="download.jhtml" method="post">
						<input type="hidden" name="id" value="${coupon.id}" />
						<table class="input">
							<tr>
								<th>
									${message("Coupon.name")}:
								</th>
								<td>
									${coupon.name}
								</td>
							</tr>
							<tr>
								<th>
									${message("Coupon.beginDate")}:
								</th>
								<td>
									[#if coupon.beginDate??]
										${coupon.beginDate?string("yyyy-MM-dd")}
									[#else]
										-
									[/#if]
								</td>
							</tr>
							<tr>
								<th>
									${message("Coupon.endDate")}:
								</th>
								<td>
									[#if coupon.endDate??]
										${coupon.endDate?string("yyyy-MM-dd")}
									[#else]
										-
									[/#if]
								</td>
							</tr>
							<tr>
								<th>
									${message("shop.business.coupon.totalCount")}:
								</th>
								<td>
									<span id="totalCount">${totalCount}</span>
								</td>
							</tr>
							<tr>
								<th>
									${message("shop.business.coupon.usedCount")}:
								</th>
								<td>
									${usedCount}
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("shop.business.coupon.count")}:
								</th>
								<td>
									<input type="text" id="count" name="count" class="text" value="100" maxlength="9" />
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									<input type="submit" class="button" value="${message("shop.common.submit")}" />
									<input type="button" class="button" value="${message("shop.common.back")}" onclick="history.back(); return false;" />
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
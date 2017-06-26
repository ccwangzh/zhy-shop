[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.freightConfig.edit")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			firstWeight: {
				required: true,
				digits: true
			},
			continueWeight: {
				required: true,
				integer: true,
				min: 1
			},
			defaultFirstPrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			defaultContinuePrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			}
		}
	});

});
</script>
</head>
<body>
	[#assign current = "freightConfigList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.freightConfig.edit")}
					</div>
					<form id="inputForm" action="update.jhtml" method="post">
						<input type="hidden" name="shippingMethodId" value="${shippingMethod.id}" />
						<input type="hidden" name="id" value="${defaultFreightConfig.id}" />
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.firstWeight")}:
								</th>
								<td>
									<input type="text" name="firstWeight" class="text" value="${defaultFreightConfig.firstWeight}" maxlength="9" title="${message("shop.business.freightConfig.weightTitle")}" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.continueWeight")}:
								</th>
								<td>
									<input type="text" name="continueWeight" class="text" value="${defaultFreightConfig.continueWeight}" maxlength="9" title="${message("shop.business.freightConfig.weightTitle")}" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.defaultFirstPrice")}:
								</th>
								<td>
									<input type="text" name="defaultFirstPrice" class="text" value="${defaultFreightConfig.defaultFirstPrice}" maxlength="16" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.defaultContinuePrice")}:
								</th>
								<td>
									<input type="text" name="defaultContinuePrice" class="text" value="${defaultFreightConfig.defaultContinuePrice}" maxlength="16" />
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
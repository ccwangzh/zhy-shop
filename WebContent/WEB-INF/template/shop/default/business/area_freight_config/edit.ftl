[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.areaFreightConfig.edit")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	
	[@flash_message /]
	
	$areaId.lSelect({
		url: "${base}/common/area.jhtml"
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			areaId: {
				required: true,
				remote: {
					url: "check_area.jhtml?shippingMethodId=${areaFreightConfig.shippingMethod.id}&previousAreaId=${areaFreightConfig.area.id}",
					cache: false
				}
			},
			firstPrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			continuePrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			}
		},
		messages: {
			areaId: {
				remote: "${message("shop.business.freightConfig.areaExists")}"
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
						${message("shop.business.areaFreightConfig.edit")}
					</div>
					<form id="inputForm" action="update.jhtml" method="post">
						<input type="hidden" name="id" value="${areaFreightConfig.id}" />
						<table class="input">
							<tr>
								<th>
									${message("FreightConfig.shippingMethod")}:
								</th>
								<td>
									${areaFreightConfig.shippingMethod.name}
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.area")}:
								</th>
								<td>
									<span class="fieldSet">
										<input type="hidden" id="areaId" name="areaId" value="${areaFreightConfig.area.id}" treePath="${areaFreightConfig.area.treePath}" />
									</span>
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.firstPrice")}:
								</th>
								<td>
									<input type="text" name="firstPrice" class="text" value="${areaFreightConfig.firstPrice}" maxlength="16" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("FreightConfig.continuePrice")}:
								</th>
								<td>
									<input type="text" name="continuePrice" class="text" value="${areaFreightConfig.continuePrice}" maxlength="16" />
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
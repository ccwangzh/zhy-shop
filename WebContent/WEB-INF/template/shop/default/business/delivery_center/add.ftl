[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.deliveryCenter.add")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	
	[@flash_message /]
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area.jhtml"
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			contact: "required",
			areaId: "required",
			address: "required",
			zipCode: {
				pattern: /^\d{6}$/
			},
			phone: {
				pattern: /^\d{3,4}-?\d{7,9}$/
			}
		}
	});

});
</script>
</head>
<body>
	[#assign current = "deliveryCenterList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.deliveryCenter.add")}
					</div>
					<form id="inputForm" action="save.jhtml" method="post">
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryCenter.name")}:
								</th>
								<td>
									<input type="text" name="name" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryCenter.contact")}:
								</th>
								<td>
									<input type="text" name="contact" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryCenter.area")}:
								</th>
								<td>
									<span class="fieldSet">
										<input type="hidden" id="areaId" name="areaId" />
									</span>
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryCenter.address")}:
								</th>
								<td>
									<input type="text" name="address" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryCenter.zipCode")}:
								</th>
								<td>
									<input type="text" name="zipCode" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryCenter.phone")}:
								</th>
								<td>
									<input type="text" name="phone" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryCenter.mobile")}:
								</th>
								<td>
									<input type="text" name="mobile" class="text" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryCenter.isDefault")}:
								</th>
								<td>
									<input type="checkbox" name="isDefault" />
									<input type="hidden" name="_isDefault" value="false" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryCenter.memo")}
								</th>
								<td>
									<input type="text" name="memo" class="text" maxlength="200" />
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
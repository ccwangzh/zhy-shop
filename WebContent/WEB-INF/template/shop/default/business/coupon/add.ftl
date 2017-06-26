[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.coupon.add")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/ueditor/ueditor.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $isExchange = $("#isExchange");
	var $point = $("#point");
	var $introduction = $("#introduction");
	
	[@flash_message /]
	
	$introduction.editor();
	
	// 是否允许积分兑换
	$isExchange.click(function() {
		if ($(this).prop("checked")) {
			$point.prop("disabled", false);
		} else {
			$point.val("").prop("disabled", true);
		}
	});
	
	$.validator.addMethod("compare", 
		function(value, element, param) {
			var parameterValue = $(param).val();
			if ($.trim(parameterValue) == "" || $.trim(value) == "") {
				return true;
			}
			try {
				return parseFloat(parameterValue) <= parseFloat(value);
			} catch(e) {
				return false;
			}
		},
		"${message("shop.business.coupon.compare")}"
	);
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			prefix: "required",
			minimumPrice: {
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			maximumPrice: {
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				},
				compare: "#minimumPrice"
			},
			minimumQuantity: "digits",
			maximumQuantity: {
				digits: true,
				compare: "#minimumQuantity"
			},
			priceExpression: {
				remote: {
					url: "check_price_expression.jhtml",
					cache: false
				}
			},
			point: {
				required: true,
				digits: true
			}
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
					<form id="inputForm" action="save.jhtml" method="post">
						<ul id="tab" class="tab">
							<li>
								<input type="button" value="${message("shop.business.coupon.base")}" />
							</li>
							<li>
								<input type="button" value="${message("Coupon.introduction")}" />
							</li>
						</ul>
						<div class="tabContent">
							<table class="input">
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Coupon.name")}:
									</th>
									<td>
										<input type="text" name="name" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Coupon.prefix")}:
									</th>
									<td>
										<input type="text" name="prefix" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.beginDate")}:
									</th>
									<td>
										<input type="text" id="beginDate" name="beginDate" class="text Wdate" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss', maxDate: '#F{$dp.$D(\'endDate\')}'});" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.endDate")}:
									</th>
									<td>
										<input type="text" id="endDate" name="endDate" class="text Wdate" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss', minDate: '#F{$dp.$D(\'beginDate\')}'});" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.minimumPrice")}:
									</th>
									<td colspan="2">
										<input type="text" id="minimumPrice" name="minimumPrice" class="text" maxlength="16" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.maximumPrice")}:
									</th>
									<td colspan="2">
										<input type="text" name="maximumPrice" class="text" maxlength="16" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.minimumQuantity")}:
									</th>
									<td colspan="2">
										<input type="text" id="minimumQuantity" name="minimumQuantity" class="text" maxlength="9" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.maximumQuantity")}:
									</th>
									<td colspan="2">
										<input type="text" name="maximumQuantity" class="text" maxlength="9" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Coupon.priceExpression")}:
									</th>
									<td colspan="2">
										<input type="text" name="priceExpression" class="text" maxlength="255" title="${message("shop.business.coupon.priceExpressionTitle")}" />
									</td>
								</tr>
								<tr>
									<th>
										${message("shop.common.setting")}:
									</th>
									<td>
										<label>
											<input type="checkbox" name="isEnabled" value="true" checked="checked" />${message("Coupon.isEnabled")}
											<input type="hidden" name="_isEnabled" value="false" />
										</label>
										<label>
											<input type="checkbox" id="isExchange" name="isExchange" value="true" checked="checked" />${message("Coupon.isExchange")}
											<input type="hidden" name="_isExchange" value="false" />
										</label>
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Coupon.point")}:
									</th>
									<td>
										<input type="text" id="point" name="point" class="text" maxlength="9" />
									</td>
								</tr>
							</table>
						</div>
						<div class="tabContent">
							<table class="input">
								<tr>
									<td>
										<textarea id="introduction" name="introduction" class="editor" style="width: 100%;"></textarea>
									</td>
								</tr>
							</table>
						</div>
						<table class="input">
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
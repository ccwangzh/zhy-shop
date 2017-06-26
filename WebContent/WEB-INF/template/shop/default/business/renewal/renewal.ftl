[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.renewal.add")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $submitButton = $("#submitButton");
	var $paymentPluginId = $("#paymentPlugin input:radio");
	var $year = $("#year");
	var $amount = $("#amount");
	var serviceFee = ${store.storeRank.serviceFee};
	var amountPayable;
	var $fee = $("#fee");
	var timeout;
	var endDate = [#if store.endDate??] ${store.endDate.getTime()} [#else] 0 [/#if];
	
	[@flash_message /]
	
	// 开店时长
	$year.on("input propertychange change", function(event) {
		if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
			if(serviceFee > 0){
				calculate();
			 }
		}
	});

	// 支付插件
	$paymentPluginId.click(function() {
		 if(serviceFee > 0){
			calculate();
		 }
	});
	
	// 计算
	function calculate() {
		clearTimeout(timeout);
		timeout = setTimeout(function() {
			if ($inputForm.valid()) {
				var paymentPluginId = $paymentPluginId.filter(":checked").val();
				$.ajax({
					url: "calculate.jhtml",
					type: "POST",
					data: {paymentPluginId: paymentPluginId, year: $year.val()},
					dataType: "json",
					cache: false,
					success: function(data) {
						if (data.message.type == "success") {
							amountPayable = data.amount;
							if (data.fee > 0) {
								$fee.text(currency(data.fee, true)).closest("tr").show();
							}else{
								$fee.closest("tr").hide();
							}
							 if(data.amount > 0){
								$amount.text(currency(data.amount, true)).closest("tr").show();
							}else {
								$amount.closest("tr").hide();
							}
						} else {
							$.message(data.message);
						}
					}
				});
			} else {
				$fee.closest("tr").hide();
				$amount.closest("tr").hide();
			}
		}, 500);
	}
	
	// 表单验证
	$inputForm.validate({
		rules: {
			year: {
				required: true,
				min:1,
				digits:true
			}
		},
	});
	
	// 检查支付状态
	setInterval(function() {
		$.ajax({
			url: "check_pay_status.jhtml",
			type: "POST",
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.endDate > endDate) {
					location.href = "${base}/business/index.jhtml";
				}
			}
		});
	}, 10000);
	
	$submitButton.click(function() {
		if($year.val() == "" || $year.val() < 0){
			$.message("warn", "${message("shop.payment.paymentTimeRequired")}");
			return false;
		}
		if(serviceFee == 0){
			$.ajax({
				url: "${base}/business/renewal/store_renewal.jhtml",
				type: "POST",
				data: {year: $year.val()},
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$submitButton.prop("disabled", true);
				},
				success: function(message) {
					if (message.type == "success") {
						$.message(message);
						setTimeout(function() {
							location.href = "${base}/business/index.jhtml";
						}, 1000);
					}else{
						location.href = "${base}/common/error.jhtml";
					}
				}
			});
		}else{
			$.ajax({
				url: "${base}/business_register/generate_svc.jhtml",
				type: "POST",
				data: $inputForm.serializeArray(),
				dataType: "json",
				cache: false,
				async:false,
				beforeSend: function() {
					$submitButton.prop("disabled", true);
				},
				success: function(data) {
					if (data.message.type == "success") {
						var i = 0;
						if(data.platformSvcSn != null){
							$inputForm.append(
								[@compress single_line = true]
									'<input type="hidden" name="paymentItemList['+ i +'].type" value="SVC_PAYMENT" \/>
									<input type="hidden" name="paymentItemList['+ i +'].svcSn" value="'+ data.platformSvcSn +'" \/>'
								[/@compress]
							);
						}
						$inputForm.submit();
					}else{
						location.href = "${base}/common/error.jhtml";
					}
				}	
			});
		}
	});
	
});
</script>
</head>
<body>
	[#assign current = "renewalList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="input deposit">
					<form id="inputForm" action="${base}/payment/plugin_submit.jhtml" method="post" target="_blank">
						<table class="input">
							[#if store.hasExpired]
								<tr>
									<th>
										<span class="requiredField">*</span>
									</th>
									<td>
										<span class="red">${message("shop.business.renewal.storeHasExpired")}</span>
									</td>
								</tr>
							[/#if]	
							<tr>
								<th>
									${message("Store.name")}:
								</th>
								<td>
									${store.name}(${message("shop.business.renewal.storeExpiredDate")}:${(store.endDate)!"${message('shop.business.index.infinite')}"})
								</td>
							</tr>
							[#if store.endDate??]
								[#if store.storeRank.serviceFee > 0]
									<tr>
										<th>
											${message("Store.storeRank")}:
										</th>
										<td>
											${store.storeRank.name}(${message("StoreRank.quantity")}：${(store.storeRank.quantity)!"${message('shop.business.index.infiniteQuantity')}"} &nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(store.storeRank.serviceFee, true)})
										</td>
									</tr>
									<tr>
										<th>
											<span class="requiredField">*</span>${message("shop.store.long")}:
										</th>
										<td>
											<input type="text" class="text" id="year" name="year" maxlength="9" title="${message("shop.store.year")}"/>&nbsp&nbsp<span>${message("shop.store.year")}</span>
										</td>
									</tr>
									<tr>
										<th>
											${message("shop.member.deposit.paymentPlugin")}:
										</th>
										<td>
											<div id="paymentPlugin" class="paymentPlugin clearfix">
												[#if paymentPlugins??]
													[#list paymentPlugins as paymentPlugin]
														<div>
															<input type="radio" id="${paymentPlugin.id}" name="paymentPluginId" value="${paymentPlugin.id}"[#if paymentPlugin == defaultPaymentPlugin] checked="checked"[/#if] />
															<label for="${paymentPlugin.id}">
																[#if paymentPlugin.logo?has_content]
																	<em title="${paymentPlugin.paymentName}" style="background-image: url(${paymentPlugin.logo});">&nbsp;</em>
																[#else]
																	<em>${paymentPlugin.paymentName}</em>
																[/#if]
															</label>
														</div>
													[/#list]
												[/#if]
											</div>
										</td>
									</tr>
								[/#if]
							[/#if]
							<tr class="hidden">
								<th>
									${message("shop.business.payment.fee")}:
								</th>
								<td>
									<span id="fee"></span>
								</td>
							</tr>
							<tr class="hidden">
								<th>
									${message("shop.business.payment.amount")}:
								</th>
								<td>
									<span id="amount"></span>
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									[#if store.endDate??]
										<input type="button" id="submitButton" class="button" value="${message("shop.common.submit")}" />
									[/#if]
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
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.fullReduction.buy")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $submitButton = $("#submitButton");
	var $year = $("#year");
	var $paymentPluginId = $("#paymentPlugin input:radio");
	var $fee = $("#fee");
	var $amount = $("#amount");
	var $useBalance = $("#useBalance");
	var timeout;
	var fullReductionTime = [#if store.fullReduction??] ${store.fullReduction.getTime()} [#else] 0 [/#if];
	var pluginPrice = ${pluginPrice};
	
	[@flash_message /]
	
	// 购买时长
	$year.on("input propertychange change", function(event) {
		if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
			calculate();
		}
	});
	
	// 支付插件
	$paymentPluginId.click(function() {
		$useBalance.prop("checked", false);
		if(pluginPrice > 0){
			calculate();
		} else {
			$.message("warn", "${message("shop.payment.selectBalancePayment")}");
		}
	});
	
	// 使用余额
	$useBalance.click(function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$paymentPluginId.prop("checked", false);
		} else {
			$paymentPluginId.prop("checked", true);
		}
		calculate();
	});
	
	// 计算支付手续费
	function calculate() {
		clearTimeout(timeout);
		timeout = setTimeout(function() {
			if ($inputForm.valid()) {
				var paymentPluginId = $paymentPluginId.filter(":checked").val();
				$.ajax({
					url: "calculate.jhtml",
					type: "POST",
					data: {paymentPluginId: paymentPluginId, year: $year.val(), useBalance: $useBalance.val()},
					dataType: "json",
					cache: false,
					success: function(data) {
						if (data.message.type == "success") {
							if($useBalance.prop("checked")){
								if(${member.balance} >= data.amount){
									$paymentPluginId.prop("checked", false);
								}else{
									$useBalance.prop("checked", false).parent().parent().hide();
								}
								$fee.closest("tr").hide();
								$amount.closest("tr").hide();
							}else{
								if(${member.balance} >= data.amount){
									$useBalance.prop("checked", false).parent().parent().show();
								}else{
									$useBalance.prop("checked", false).parent().parent().hide();
								} 
								if (data.fee > 0) {
									$amount.text(currency(data.amount, true)).closest("tr").show();
									$fee.text(currency(data.fee, true)).closest("tr").show();
								} else {
									$amount.text(currency(data.amount, true)).closest("tr").show();
									$fee.closest("tr").hide();
								}
							}	
						} else {
							$.message(data.message);
						}
					}
				});
			} else {
				$fee.closest("tr").hide();
			}
		}, 500);
	}
	
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"year": {
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
				if (data.fullReductionTime > fullReductionTime) {
					location.href = "${base}/business/full_reduction/list.jhtml";
				}
			}
		});
	}, 10000);

	$submitButton.click(function() {
		if($year.val() == "" || $year.val() < 0){
			$.message("warn", "${message("shop.payment.paymentTimeRequired")}");
			return false;
		}
		if($useBalance.prop("checked")){
			$.ajax({
				url: "${base}/business/full_reduction/deposit_payment.jhtml",
				type: "POST",
				data: {year: $year.val()},
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$submitButton.prop("disabled", true);
				},
				success: function(data) {
					if (data.message.type == "success") {
						$.message(data.message);
						location.href = "${base}/business/full_reduction/list.jhtml";
					}else{
						location.href = "${base}/common/error.jhtml";
					}
				}
			});
		}else{
			if($paymentPluginId.filter(":checked").size() <= 0){
				$.message("warn", "${message("shop.payment.paymentMethodRequired")}");
				return false;
			}
			if(pluginPrice =="" || pluginPrice <= 0){
				$.message("warn", "${message("shop.payment.selectBalancePayment")}");
				return false;
			}
			$.ajax({
				url: "${base}/business/full_reduction/generate_svc.jhtml",
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
						if(data.promotionPluginSvcSn != null){
							$inputForm.append(
								[@compress single_line = true]
									'<input type="hidden" name="paymentItemList['+ i +'].type" value="SVC_PAYMENT" \/>
									<input type="hidden" name="paymentItemList['+ i +'].svcSn" value="'+ data.promotionPluginSvcSn +'" \/>'
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
	[#assign current = "fullReductionList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="input deposit">
					<div class="title">${message("shop.business.fullReduction.buy")}</div>
					<form id="inputForm" action="${base}/payment/plugin_submit.jhtml" method="post" target="_blank">
						<table class="input">
							[#if store.fullReduction??]
								<tr>
									<th>
										${message("shop.business.fullReduction.expire")}:
									</th>
									<td>
										${store.fullReduction}
									</td>
								</tr>
							[/#if]
							<tr>
								<th>
									${message("PromotionPlugin.price")}:
								</th>
								<td>
									${currency(pluginPrice,true)}
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("shop.business.fullReduction.year")}:
								</th>
								<td>
									<input type="text" class="text" id="year" name="year" maxlength="9" title="${message("shop.business.fullReduction.yearUnit")}"/>&nbsp&nbsp<span>${message("shop.business.fullReduction.yearUnit")}</span>
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
							[#if member?? && member.balance > 0]
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="checkbox" id="useBalance" name="useBalance" value="true" />
										<label for="useBalance">
											${message("shop.business.useBalance")}
										</label>
										[#if member.balance > 0]<p>${message("shop.business.balance")}: ${currency(member.balance, true)}</p>[/#if]
									</td>
								</tr>
							[/#if]	
							<tr class="hidden">
								<th>
									${message("shop.member.deposit.fee")}:
								</th>
								<td>
									<span id="fee"></span>
								</td>
							</tr>
							<tr class="hidden">
								<th>
									${message("shop.business.fullReduction.amountPayable")}:
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
									<input type="button" id="submitButton" class="button" value="${message("shop.member.submit")}" />
									<input type="button" class="button" value="${message("shop.member.back")}" onclick="history.back(); return false;" />
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
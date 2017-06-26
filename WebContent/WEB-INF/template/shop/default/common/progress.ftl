[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.register")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/order.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/register.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $submitButton = $("#submitButton");
	var $paymentPlugin = $("#paymentPlugin");
	var $paymentPluginId = $("#paymentPlugin input:radio");
	var $year = $("#year");
	var $amount = $("#amount");
	var $fee = $("#fee");
	var $useBalance = $("#useBalance");
	var timeout;
	
	[@flash_message /]
	
	[#if status == "approved"]
		// 开店时长
		$year.on("input propertychange change", function(event) {
			if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
				calculate();
			}
		});
		
		// 支付插件
		$paymentPluginId.click(function() {
			$useBalance.prop("checked", false);
			calculate();
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
		
		// 计算
		function calculate() {
			clearTimeout(timeout);
			timeout = setTimeout(function() {
				if ($inputForm.valid()) {
					$.ajax({
						url: "calculate.jhtml",
						type: "POST",
						data: $inputForm.serializeArray(),
						dataType: "json",
						cache: false,
						success: function(data) {
							if (data.message.type == "success") {
								if ($useBalance.prop("checked")) {
									if (${member.balance} >= data.amount) {
										$paymentPluginId.prop("checked", false);
									} else {
										$useBalance.prop("checked", false).parent().parent().hide();
										$.message("warn", "${message("shop.payment.insufficientBalance")}");
									}
									if (data.amount > 0) {
										$amount.text(currency(data.amount, true)).closest("tr").show();
									}else {
										$amount.prop("disabled", true).closest("tr").hide();
									}
								}else {
									if (data.amount > 0) {
										$amount.text(currency(data.amount, true)).closest("tr").show();
									}else {
										$amount.prop("disabled", true).closest("tr").hide();
									}
									if (data.fee > 0) {
										$fee.text(currency(data.fee, true)).closest("tr").show();
									} else {
										$fee.prop("disabled", true).closest("tr").hide();
									}
									if (${member.balance} >= data.amount) {
										$useBalance.prop("checked", false).parent().parent().show();
									} else {
										$useBalance.prop("checked", false).parent().parent().hide();
									}
								}
							} else {
								if (${member.balance} >= data.amount) {
									$useBalance.prop("checked", false).parent().parent().show();
								} else {
									$useBalance.prop("checked", false).parent().parent().hide();
								}
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
					integer: true
				}
			}
		});
		
		$submitButton.click(function() {
			[#if (store.storeRank?? && store.storeRank.serviceFee > 0) || (store.storeCategory?? && store.storeCategory.bail > 0)]
				if ($paymentPluginId.filter(":checked").size() <= 0 && $useBalance.filter(":checked").size() <= 0) {
					$.message("warn", "${message("shop.payment.paymentMethodRequired")}");
					return false;
				}
			[/#if]
			[#if store.storeRank?? && store.storeRank.serviceFee > 0]
				if ($year.val() <= 0) {
					$.message("warn", "${message("shop.payment.paymentTimeRequired")}");
					return false;
				}
			[/#if]
			var  isOpened = false;
			[#if store.storeRank?? && store.storeRank.serviceFee <= 0 && store.storeCategory?? && store.storeCategory.bail <= 0]
				isOpened = true;
			[/#if]
			if (isOpened) {
				$.ajax({
					url: "${base}/business_register/open.jhtml",
					type: "POST",
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
						} else {
							location.href = "${base}/common/error.jhtml";
						}
					}
				});
			}else if ($useBalance.prop("checked")) {
				$.ajax({
					url: "${base}/business_register/deposit_payment.jhtml",
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
							location.href = "${base}/business/index.jhtml";
						} else {
							location.href = "${base}/common/error.jhtml";
						}
					}
				});
			} else{
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
							if (data.platformSvcSn != null) {
								$inputForm.append(
									[@compress single_line = true]
										'<input type="hidden" name="paymentItemList['+ i +'].type" value="SVC_PAYMENT" \/>
										<input type="hidden" name="paymentItemList['+ i +'].svcSn" value="'+ data.platformSvcSn +'" \/>'
									[/@compress]
								);
								i++;
							}
							if (data.bail > 0) {
								$inputForm.append(
									[@compress single_line = true]
										'<input type="hidden" name="paymentItemList['+ i +'].type" value="BAIL_PAYMENT" \/>
										<input type="hidden" name="paymentItemList['+ i +'].amount" value="'+ data.bail +'" \/>'
									[/@compress]
								);
							}
							$inputForm.submit();
						} else {
							location.href = "${base}/common/error.jhtml";
						}
					}	
				});
			}	
		});
	[/#if]
	
	// 检查余额
	setInterval(function() {
		$.ajax({
			url: "check_store_status.jhtml",
			type: "POST",
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.message.type == "success") {
					location.href = "${base}/business/index.jhtml";
				}
			}
		});
	}, 10000);
	
});
</script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container progress">
		<div class="step">
			<ul>
				<li [#if status == "pending"]class="current"[/#if]>${message("shop.business.step1")}</li>
				<li [#if status == "approved"]class="current"[/#if]>${message("shop.business.step2")}</li>
				<li>${message("shop.business.step3")}</li>
			</ul>
		</div>
		<div class="title">
			<strong>${message("shop.business.register")}</strong>BUSINESS REGISTER TEST
		</div>
		<div class="row">
			<div class="span12">
				<div class="main clearfix">
					[#if status == "pending"]
						<strong>${message("shop.business.success")}</strong>
					[#elseif status == "approved"]
						<form id="inputForm" action="${base}/payment/plugin_submit.jhtml" method="post" target="_blank">
							<table class="product">
								<tr>
									<th width="60">
										${message("Store.storeRank")}:
									</th>
									<td>
										[#if store.storeRank??]
											${store.storeRank.name}(${message("StoreRank.quantity")}：${(store.storeRank.quantity)!"${message('shop.business.index.infiniteQuantity')}"} &nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(store.storeRank.serviceFee, true)})
										[/#if]
									</td>
								</tr>
								[#if store.storeRank?? && store.storeRank.serviceFee > 0]
									<tr>
										<th>
											${message("shop.store.long")}:
										</th>
										<td>
											<span class="fieldSet">
												<input type="text" class="text" id="year" name="year" maxlength="4" title="${message("shop.store.year")}"/>
												${message("shop.store.year")}
											</span>
										</td>
									</tr>
								[/#if]
								<tr>
									<th>
										${message("Store.storeCategory")}:
									</th>
									<td>
										[#if store.storeCategory??]
											${store.storeCategory.name}(${message("StoreCategory.bail")}：${currency(store.storeCategory.bail, true)})
										[/#if]
									</td>
								</tr>
								[#if (store.storeRank?? && store.storeRank.serviceFee > 0) || (store.storeCategory?? && store.storeCategory.bail > 0)]
									<tr id="paymentPlugin">
										<th>
											${message("shop.member.deposit.paymentPlugin")}:
										</th>
										<td>
											[#if paymentPlugins??]
												[#list paymentPlugins as paymentPlugin]
													<input type="radio" id="${paymentPlugin.id}" name="paymentPluginId" value="${paymentPlugin.id}"[#if paymentPlugin == defaultPaymentPlugin] checked="checked"[/#if] />
													<label for="${paymentPlugin.id}">
														[#if paymentPlugin.logo?has_content]
															<em title="${paymentPlugin.paymentName}" style="background-image: url(${paymentPlugin.logo});">&nbsp;</em>
														[#else]
															<em>${paymentPlugin.paymentName}</em>
														[/#if]
													</label>
												[/#list]
											[/#if]
										</td>
									</tr>
									[#if member?? && member.balance > 0]
										<tr>
											<th>
												${message("shop.business.useBalance")}:
											</th>
											<td>
												<input type="checkbox" id="useBalance" name="useBalance" value="true" />
												[#if member.balance > 0]
													<label for="useBalance">
														<span class="silver">(${message("shop.business.balance")}:${currency(member.balance, true)})</span>
													</label>
												[/#if]
											</td>
										</tr>
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
								[/#if]
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="button" id="submitButton" class="button" value="${message("shop.common.submit")}" />
									</td>
								</tr>
							</table>
						</form>
					[/#if]
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
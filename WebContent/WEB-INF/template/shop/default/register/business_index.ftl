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
<link href="${base}/resources/shop/${theme}/css/register.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/selectmultiple.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.selectmultiple.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $registerForm = $("#registerForm");
	var $captcha = $("#captcha");
	var $captchaImage = $("#captchaImage");
	var $submit = $("input:submit");
	var $agreement = $("#agreement");
	var $browserButton = $("a.browserButton");
	var $selectItem = $("#selectItem");
	var $title = $("#title");
	var $storeTable = $("#storeTable");
	var selectValues = new Array();
	
	[@flash_message /]
	
	// 多选框
	$selectItem.selectMultiple();
	
	// 文件上传
	$browserButton.uploader();
	
	// 更换验证码
	$captchaImage.click(function() {
		$captchaImage.attr("src", "${base}/common/captcha.jhtml?captchaId=${captchaId}&timestamp=" + new Date().getTime());
	});
	
	// 注册协议
	$agreement.hover(function() {
		$(this).height(200);
	});
	
	// 表单验证
	$registerForm.validate({
		rules: {
			name: {
				required: true,
				remote: {
					url: "${base}/business_register/check_name.jhtml",
					cache: false
				}
			},
			mobile:{
				required: true,
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "${base}/business_register/check_mobile.jhtml",
					cache: false
				}
			},
			email: {
				required: true,
				email: true,
				remote: {
					url: "${base}/business_register/check_email.jhtml",
					cache: false
				}
			},
			captcha: "required"
			[@business_attribute_list]
				[#list businessAttributes as businessAttribute]
					[#if businessAttribute.isRequired || businessAttribute.pattern?has_content]
						,businessAttribute_${businessAttribute.id}: {
							[#if businessAttribute.isRequired]
								required: true
								[#if businessAttribute.pattern?has_content],[/#if]
							[/#if]
							[#if businessAttribute.pattern?has_content]
								pattern: /${businessAttribute.pattern}/
							[/#if]
						}
					[/#if]
				[/#list]
			[/@business_attribute_list]
		},
		messages: {
			name: {
					remote: "${message("shop.validate.exist")}"
			},
			mobile: {
				pattern: "${message("shop.validate.pattern")}",
				remote: "${message("shop.validate.exist")}"
			},
			email: {
				remote: "${message("shop.validate.exist")}"
			}
		},
		submitHandler: function(form) {
			$.ajax({
				url: $registerForm.attr("action"),
				type: "POST",
				data: $registerForm.serializeArray(),
				dataType: "json",
				cache: false,
				success: function(message) {
					$.message(message);
					if (message.type == "error" &&　message.content =="${message("shop.common.notLogin")}") {			
						$.redirectLogin("${base}/business_register/edit.jhtml");
						return;
					}else if (message.type == "success") {
						setTimeout(function() {
							$submit.prop("disabled", false);
							location.href = "${base}/business_register/progress.jhtml";
						}, 2000);
					}else{
						$submit.prop("disabled", false);
						[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("businessRegister")]
							$captcha.val("");
							$captchaImage.attr("src", "${base}/common/captcha.jhtml?captchaId=${captchaId}&timestamp=" + new Date().getTime());
						[/#if]
					}
				}
			});
		}
	});

});
</script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container register">
		<div class="row">
			<div class="span12">
				<div class="wrap">
					<div class="main clearfix">
						<div class="title">
							<strong>${message("shop.business.register")}</strong>BUSINESS REGISTER
						</div>
						<div class="step">
							<ul>
								<li class="current">${message("shop.business.step1")}</li>
								<li>${message("shop.business.step2")}</li>
								<li>${message("shop.business.step3")}</li>
							</ul>
						</div>
						<form id="registerForm" action="${base}/business_register/submit.jhtml" method="post">
							<input type="hidden" name="captchaId" value="${captchaId}" />
							<ul id="tab" class="tab">
								<li>
									<input type="button" value="${message("shop.business.base")}" />
								</li>
								<li>
									<input type="button" value="${message("shop.business.store")}" />
								</li>
								<li>
									<input type="button" value="${message("Store.productCategorys")}" />
								</li>
							</ul>
							<table class="input tabContent">
								[@business_attribute_list]
									[#list businessAttributes as businessAttribute]
										[#if businessAttribute.type == "text" || businessAttribute.type == "name"|| businessAttribute.type == "licenseNumber" || businessAttribute.type == "legalPerson" ||  businessAttribute.type == "idCard" || businessAttribute.type == "mobile" || businessAttribute.type == "phone" || businessAttribute.type == "email" || businessAttribute.type == "organizationCode" || businessAttribute.type == "identificationNumber" ||  businessAttribute.type == "bankName" || businessAttribute.type == "bankAccount"]
											<tr>
												<th>
													[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
												</th>
												<td>
													<input type="text" name="businessAttribute_${businessAttribute.id}" class="text" maxlength="200" />
												</td>
											</tr>
										[#elseif businessAttribute.type == "image" || businessAttribute.type == "licenseImage" || businessAttribute.type == "idCardImage" || businessAttribute.type == "organizationImage" || businessAttribute.type == "taxImage"]
											<tr>
												<th>
													[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
												</th>
												<td>
													<span class="fieldSet">
														<input type="hidden" name="businessAttribute_${businessAttribute.id}" class="text" />
														<a href="javascript:;" class="button browserButton">${message("shop.common.upload")}</a>
														<span class="preview"></span>
													</span>
												</td>
											</tr>
										[#elseif businessAttribute.type == "select"]
											<tr>
												<th>
													[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
												</th>
												<td>
													<select name="businessAttribute_${businessAttribute.id}">
														<option value="">${message("shop.common.choose")}</option>
														[#list businessAttribute.options as option]
															<option value="${option}">
																${option}
															</option>
														[/#list]
													</select>
												</td>
											</tr>
										[#elseif businessAttribute.type == "checkbox"]
											<tr>
												<th>
													[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
												</th>
												<td>
													<span class="fieldSet">
														[#list businessAttribute.options as option]
															<label>
																<input type="checkbox" name="businessAttribute_${businessAttribute.id}" value="${option}" />${option}
															</label>
														[/#list]
													</span>
												</td>
											</tr>
										[#elseif businessAttribute.type == "date"]
											<tr>
												<th>
													[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
												</th>
												<td>
													<input type="text" name="businessAttribute_${businessAttribute.id}" class="text Wdate" onfocus="WdatePicker();" />
												</td>
											</tr>
										[/#if]
									[/#list]
								[/@business_attribute_list]
							</table>
							<table class="input tabContent" id="storeTable">
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.name")}:
									</th>
									<td>
										<input type="text" name="name" class="text" maxlength="200" />	
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.storeRank")}:
									</th>
									<td>
										<select name="storeRankId">
											[#list storeRanks as storeRank]
												<option value="${storeRank.id}" [#if storeRank.isDefault] selected="selected"[/#if]>
													${storeRank.name}(${message("StoreRank.quantity")}：${(storeRank.quantity)!'${message("shop.business.index.infiniteQuantity")}'} &nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(storeRank.serviceFee, true)})
												</option>
											[/#list]
										</select>
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.storeCategory")}:
									</th>
									<td>
										<select name="storeCategoryId">
											[#list storeCategorys as storeCategory]
												<option value="${storeCategory.id}">
													${storeCategory.name}(${message("StoreCategory.bail")}：${currency(storeCategory.bail, true)})
												</option>
											[/#list]
										</select>
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.mobile")}:
									</th>
									<td>
										<input type="text" name="mobile" class="text" maxlength="16" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.email")}:
									</th>
									<td>
										<input type="text" name="email" class="text" maxlength="200" />
									</td>
								</tr>
							</table>
							<table class="input tabContent">
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<select id="selectItem" name="productCategoryIds" multiple="multiple">
											[#list productCategoryTree as productCategory]
												<option value="${productCategory.id}" name="${productCategory.generalRate}" class="${productCategory.selfRate}" title="${message("ProductCategory.generalRate")}:${productCategory.generalRate}">
													[#if productCategory.grade != 0]
														[#list 1..productCategory.grade as i]
															&nbsp;&nbsp;
														[/#list]
													[/#if]
													${productCategory.name}
												</option>
											[/#list]
										</select>
									</td>
								</tr>
							</table>
							<table class="input">
								[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("businessRegister")]
									<tr>
										<th>
											<span class="requiredField">*</span>${message("shop.captcha.name")}:
										</th>
										<td>
											<span class="fieldSet">
												<input type="text" id="captcha" name="captcha" class="text captcha" maxlength="4" autocomplete="off" /><img id="captchaImage" class="captchaImage" src="${base}/common/captcha.jhtml?captchaId=${captchaId}" title="${message("shop.captcha.imageTitle")}" />
											</span>
										</td>
									</tr>
								[/#if]
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="submit" class="submit" value="${message("shop.register.submit")}" />
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										${message("shop.register.agreement")}
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										[#noescape]
											<div id="agreement" class="agreement">${setting.registerAgreement}</div>
										[/#noescape]
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
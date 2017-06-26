[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.register.title")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/register.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jsbn.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/prng4.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/rng.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/md5.min.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $registerForm = $("#registerForm");
	var $username = $("#username");
	var $password = $("#password");
	var $email = $("#email");
	var $areaId = $("#areaId");
	var $captcha = $("#captcha");
	var $captchaImage = $("#captchaImage");
	var $submit = $("input:submit");
	var $agreement = $("#agreement");
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area.jhtml"
	});
	
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
			username: {
				required: true,
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
                	url: "/register/checkMobile.jhtml",
                    type: "Post",
                    data: {
                    	username: function () { return $("#username").val(); }
                    },
                    dataFilter: function (data, type) {
                   		//data:false表示存在
	                     return data;
	                 }  
                }
			},
			password: {
				required: true,
				pattern: /^(?![\d]+$)(?![a-zA-Z]+$)(?![^\da-zA-Z]+$).{6,16}$/
			},
			rePassword: {
				required: true,
				equalTo: "#password"
			},
			VerificateCode: {
				required: true
			},
			captcha: "required"
			[@member_attribute_list]
				[#list memberAttributes as memberAttribute]
					[#if memberAttribute.isRequired || memberAttribute.pattern?has_content]
						,memberAttribute_${memberAttribute.id}: {
							[#if memberAttribute.isRequired]
								required: true
								[#if memberAttribute.pattern?has_content],[/#if]
							[/#if]
							[#if memberAttribute.pattern?has_content]
								pattern: /${memberAttribute.pattern}/
							[/#if]
						}
					[/#if]
				[/#list]
			[/@member_attribute_list]
		},
		messages: {
			username: {
				required: "${message("请输入手机号")}",
				pattern: "${message("格式不正确")}",
				remote: "手机号已被使用"
			},
			password: {
				required: "${message("请输入登录密码")}",
				pattern: "${message("6-16个字符，由大小写字母、数字和符号两种及以上组合")}"
			},
			rePassword: {
				required: "${message("请再次确认登录密码")}",
			},
			VerificateCode: {
				required: "${message("请输入6位数字的验证码")}",
			},
			captcha: {
				required: "${message("请输入验证码")}",
			}
		},
		submitHandler: function(form) {
			$submit.prop("disabled", true);
			var enPassword = window.md5($password.val());
			var VerificateCode = $('#VerificateCode').val();
			$.ajax({
				url: $registerForm.attr("action"),
				type: "POST",
				data: {
					username: $username.val(),
					enPassword: enPassword,
					verifyCode: VerificateCode,
					email: $email.val()
					[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberRegister")]
						,captchaId: "${captchaId}",
						captcha: $captcha.val()
					[/#if]
					[@member_attribute_list]
						[#list memberAttributes as memberAttribute]
							,memberAttribute_${memberAttribute.id}: [#if memberAttribute.type == "gender"]$(":input[name='memberAttribute_${memberAttribute.id}']:checked").val()[#else]$(":input[name='memberAttribute_${memberAttribute.id}']").val()[/#if]
						[/#list]
					[/@member_attribute_list]
				},
				dataType: "json",
				cache: false,
				success: function(message) {
					$.message(message);
					if (message.type == "success") {
						setTimeout(function() {
							$submit.prop("disabled", false);
							location.href = "${base}/";
						}, 3000);
					} else {
						$submit.prop("disabled", false);
						[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberRegister")]
							$captcha.val("");
							$captchaImage.attr("src", "${base}/common/captcha.jhtml?captchaId=${captchaId}&timestamp=" + new Date().getTime());
						[/#if]
					}
				}
			});
		}
	});
	
	//手机验证码
	$('#yzm-btn').on('click',function(){
		$.verifiMobile('username','','/register/checkMobile.jhtml','/register/getVerityCode.jhtml','verifyCode','yzm-btn','yuyin','',$('#captcha').val(),"register");
	})

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
							<strong>${message("shop.register.title")}</strong>
						</div>
						<form id="registerForm" action="${base}/register/submit.jhtml" method="post">
							<table>

								<tr>
									<th>
										手机号:
									</th>
									<td>
										<div class="verificateError">
											<input type="text" id="username" ajaxurl="/user/validateMobile.html" name="username" class="text" />
										</div>
									</td>
								</tr>

								[@member_attribute_list]
									[#list memberAttributes as memberAttribute]
										<tr>
											<th>
												${memberAttribute.name}:
											</th>
											<td>
												[#if memberAttribute.type == "gender"]
													<span class="fieldSet">
														[#list genders as gender]
															<label>
																<input type="radio" name="memberAttribute_${memberAttribute.id}" value="${gender}" />${message("Member.Gender." + gender)}
															</label>
														[/#list]
													</span>
												[#elseif memberAttribute.type == "birth"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" onfocus="WdatePicker();" />
												[#elseif memberAttribute.type == "area"]
													<span class="fieldSet">
														<input type="hidden" id="areaId" name="memberAttribute_${memberAttribute.id}" />
													</span>
												[#elseif memberAttribute.type == "text" || memberAttribute.type == "name" || memberAttribute.type == "address" || memberAttribute.type == "zipCode" || memberAttribute.type == "phone" || memberAttribute.type == "mobile"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.type == "select"]
													<select name="memberAttribute_${memberAttribute.id}">
														<option value="">${message("shop.common.choose")}</option>
														[#list memberAttribute.options as option]
															<option value="${option}">
																${option}
															</option>
														[/#list]
													</select>
												[#elseif memberAttribute.type == "checkbox"]
													<span class="fieldSet">
														[#list memberAttribute.options as option]
															<label>
																<input type="checkbox" name="memberAttribute_${memberAttribute.id}" value="${option}" />${option}
															</label>
														[/#list]
													</span>
												[/#if]
											</td>
										</tr>
									[/#list]
								[/@member_attribute_list]
								<tr>
									<th>
										${message("shop.register.password")}:
									</th>
									<td>
										<div class="verificateError">
											<input type="password" id="password" name="password" class="text" maxlength="${setting.passwordMaxLength}" autocomplete="off" />
										<div>
									</td>
								</tr>
								<tr>
									<th>
										${message("shop.register.rePassword")}:
									</th>
									<td>
										<div class="verificateError">
											<input type="password" name="rePassword" class="text" maxlength="${setting.passwordMaxLength}" autocomplete="off" />
										<div>
									</td>
								</tr>
								[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberRegister")]
									<tr>
										<th>
											${message("shop.captcha.name")}:
										</th>
										<td>
											<div class="verificateError">
												<span class="fieldSet">
													<input type="text" id="captcha" name="captcha" class="text captcha" maxlength="4" autocomplete="off" /><img id="captchaImage" class="captchaImage" src="${base}/common/captcha.jhtml?captchaId=${captchaId}" title="${message("shop.captcha.imageTitle")}" />
												</span>
											</div>
										</td>
									</tr>
									<tr>
										<th>
											手机验证码:
										</th>
										<td>
											<div class="verificateError">
												<span class="fieldSet">
													<input type="text" id="VerificateCode" name="VerificateCode" class="text captcha" maxlength="6" autocomplete="off" /><a class="verifyCode" id="yzm-btn">获取验证码</a>
												</span>
											</div>
										</td>
									</tr>
								[/#if]
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="submit" class="submit" maxlength="6" autocomplete="off" value="${message("shop.register.submit")}" />
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
											<div id="agreement" class="agreement" style="width:430px;">${setting.registerAgreement}</div>
										[/#noescape]
									</td>
								</tr>
							</table>
							<div class="login">
								<dl>
									<dt>${message("shop.register.hasAccount")}</dt>
									<dd>
										${message("shop.register.tips")}
										<a href="${base}/login.jhtml">${message("shop.register.login")}</a>
									</dd>
								</dl>
							</div>
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
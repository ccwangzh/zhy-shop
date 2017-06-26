[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.password.find")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/password.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/md5.min.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/validate.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $passwordForm = $("#passwordForm");
	var $username = $("#username");
	var $captcha = $("#captcha");
	var $captchaImage = $("#captchaImage");
	var $submit = $("input:submit");
	var $password = $("#password");
	var $phoneVerifyCode = $("#phoneVerifyCode");
	
	// 更换验证码
	$captchaImage.click(function() {
		$captchaImage.attr("src", "${base}/common/captcha.jhtml?captchaId=${captchaId}&timestamp=" + new Date().getTime());
	});
	
	// 表单验证
	$passwordForm.validate({
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
                   		if(data=="true"){
                   			return "false";
                   		}else if(data=="false"){
                   			return "true";
                   		}
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
		},
		messages: {
			username: {
				required: "${message("请输入手机号")}",
				pattern: "${message("格式不正确")}",
				remote: "用户不存在"
			},
			password: {
				required: "${message("请输入密码")}",
				pattern: "${message("6-16个字符，由大小写字母、数字和符号两种及以上组合")}"
			},
			rePassword: {
				required: "${message("请再次输入密码")}",
			},
			VerificateCode: {
				required: "${message("请输入6位数字的验证码")}",
			},
			captcha: {
				required: "${message("请输入验证码")}",
			}
		},
		submitHandler: function(form) {
			var enPassword = window.md5($password.val());
			var VerificateCode = $('#VerificateCode').val();
			$.ajax({
				url: $passwordForm.attr("action"),
				type: "POST",
				data: {
					username: $username.val(),
					enPassword: enPassword,
					key: VerificateCode
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
				beforeSend: function() {
					$submit.prop("disabled", true);
				},
				success: function(message) {
					$.message(message);
					if (message.type == "success") {
						setTimeout(function() {
							$submit.prop("disabled", false);
							location.href = "${base}/";
						}, 3000);
					} else {
						$submit.prop("disabled", false);
						[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("findPassword")]
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
		$.verifiMobile('username','','/register/checkMobile.jhtml','/password/getVerityCode.jhtml','verifyCode','yzm-btn','yuyin','',$('#captcha').val(),"findPassword");
	})

});
</script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container password">
		<div class="row">
			<div class="span12">
				<div class="wrap">
					<div class="main">
						<div class="title">
							<strong>${message("shop.password.find")}</strong>
						</div>
						<form id="passwordForm" action="find.jhtml" method="post">
							<table>
								<tr>
									<th>
										手机号:
									</th>
									<td>
										<div class="verificateError">
											<input type="text" id="username" name="username" class="text" />
										</div>
									</td>
								</tr>
								<tr>
									<th>
										新密码:
									</th>
									<td>
										<div class="verificateError">
											<input type="password" id="password" name="password" class="text" maxlength="${setting.passwordMaxLength}" autocomplete="off" />
										</div>
									</td>
								</tr>
								<tr>
									<th>
										确认新密码:
									</th>
									<td>
										<div class="verificateError">
											<input type="password" name="rePassword" class="text" maxlength="${setting.passwordMaxLength}" autocomplete="off" />
										</div>
									</td>
								</tr>
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
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="submit" class="submit" value="${message("shop.password.submit")}" />
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
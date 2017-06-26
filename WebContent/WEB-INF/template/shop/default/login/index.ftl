[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.login.title")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/login.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jsbn.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/prng4.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/rng.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/md5.min.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {
	var $loginForm = $("#loginForm");
	var $username = $("#username");
	var $password = $("#password");
	var $captcha = $("#captcha");
	var $captchaImage = $("#captchaImage");
	var $isRememberUsername = $("#isRememberUsername");
	var $submit = $("input:submit");
	
	// 记住用户名
	if (getCookie("memberUsername") != null) {
		$isRememberUsername.prop("checked", true);
		$username.val(getCookie("memberUsername"));
		$password.focus();
	} else {
		$isRememberUsername.prop("checked", false);
		$username.focus();
	}
	
	// 更换验证码
	$captchaImage.click(function() {
		$captchaImage.attr("src", "${base}/common/captcha.jhtml?captchaId=${captchaId}&timestamp=" + new Date().getTime());
	});
	
	// 表单验证、记住用户名
	$loginForm.validate({
		rules: {
			username: {
				required: true,
				pattern: /(^1\d{10}$)|(^\d{8}$)/
			},
			password: "required"
			[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberLogin")]
				,captcha: "required"
			[/#if]
		},
		messages: {
			username: {
				required: "${message("请输入手机号或用户ID")}",
				pattern: "${message("格式不正确")}"
			},
			password: {
				required: "${message("请输入登录密码")}"
			},
			captcha: {
				required: "${message("请输入验证码")}",
			}
		},
		submitHandler: function(form) {
			$submit.prop("disabled", true);
			var enPassword=window.md5($password.val());
			$.ajax({
				url: $loginForm.attr("action"),
				type: "POST",
				data: {
					username: $username.val(),
					enPassword: enPassword
					[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberLogin")]
						,captchaId: "${captchaId}",
						captcha: $captcha.val()
					[/#if]
				},
				dataType: "json",
				cache: false,
				success: function(message) {
					if ($isRememberUsername.prop("checked")) {
						addCookie("memberUsername", $username.val(), {expires: 7 * 24 * 60 * 60});
					} else {
						removeCookie("memberUsername");
					}
					$submit.prop("disabled", false);
					if (message.type == "success") {
						[#noescape]
							[#if redirectUrl??]
								location.href = "${redirectUrl?js_string}";
							[#else]
								location.href = "${base}/";
							[/#if]
						[/#noescape]
					} else {
						$.message(message);
						[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberLogin")]
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
	<div class="container login">
		<div class="row">
			<div class="span6 loginFormLeft">
				[@ad_position id = 6]
					[#noescape]
						${adPosition.resolveTemplate()}
					[/#noescape]
				[/@ad_position]
			</div>
			<div class="span6 loginFormRight">
				<div class="wrap">
					<div class="main">
						<div class="title">
							<strong>${message("shop.login.title")}</strong>
						</div>
						<form id="loginForm" action="${base}/login/submit.jhtml" method="post">
							<table>
								<tr>
									<th>
										手机号 / 账户
									</th>
									<td >
										<div class="verificateError">
											<input type="text" id="username" name="username" class="text" maxlength="200" />
										</div>
									</td>
								</tr>
								<tr>
									<th>
										${message("shop.login.password")}:
									</th>
									<td>
										<div class="verificateError">
											<input type="password" id="password" name="password" class="text" maxlength="30" autocomplete="off" />
										</div>
									</td>
								</tr>
								[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberLogin")]
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
								[/#if]
								<tr>
									<th style="height:16px;">
										&nbsp;
									</th>
									<td>
										<label>
											<input type="checkbox" id="isRememberUsername" name="isRememberUsername" value="true" />${message("shop.login.isRememberUsername")}
										</label>
										<label>
											&nbsp;&nbsp;<a href="${base}/password/find.jhtml">${message("shop.login.findPassword")}</a>
										</label>
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<input type="submit" class="submit" value="${message("shop.login.submit")}" />
									</td>
								</tr>
								[#if loginPlugins?has_content]
									<tr class="loginPlugin">
										<th>
											&nbsp;
										</th>
										<td>
											<ul>
												[#list loginPlugins as loginPlugin]
													<li>
														<a href="${base}/login/plugin_submit.jhtml?pluginId=${loginPlugin.id}"[#if loginPlugin.description??] title="${loginPlugin.description}"[/#if]>
															[#if loginPlugin.logo?has_content]
																<img src="${loginPlugin.logo}" alt="${loginPlugin.loginMethodName}" />
															[#else]
																${loginPlugin.loginMethodName}
															[/#if]
														</a>
													</li>
												[/#list]
											</ul>
										</td>
									</tr>
								[/#if]
								<tr class="register">
									<th>
										&nbsp;
									</th>
									<td>
										<dl>
											<dt>${message("shop.login.noAccount")}</dt>
											<dd>
												${message("shop.login.tips")}
												<a href="${base}/register.jhtml">${message("shop.login.register")}</a>
											</dd>
										</dl>
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
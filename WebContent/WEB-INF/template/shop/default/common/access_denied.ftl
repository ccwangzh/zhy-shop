[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.notice")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container error">
		<div class="row">
			<div class="span12">
				<div class="main">
					<dl>
						[#if store == null]
							<dt>${message("shop.business.noOpen")}</dt>
							<dd>
								${message("shop.business.noOpenContent")}
							</dd>
							<dd>
								<a href="${base}/business_register.jhtml" class="button">${message("shop.business.register")}</a>
								<a href="${base}/" class="button">${message("shop.error.home")}</a>
							</dd>
						[#elseif !store.isEnabled]
							<dt>${message("shop.business.close")}</dt>
							<dd>
								${message("shop.business.closeContent")}
							</dd>
							<dd>
								<a href="${base}/" class="button">${message("shop.error.home")}</a>
							</dd>
						[#elseif store.status != "success"]
							[#if store.status =="pending"]
								<dt>${message("shop.business.pending")}</dt>
								<dd>
									${message("shop.business.pendingContent")}
								</dd>
								<dd>
									<a href="${base}/" class="button">${message("shop.error.home")}</a>
								</dd>	
							[#elseif store.status =="failed"]
								<dt>${message("shop.business.failed")}</dt>
								<dd>
									${message("shop.business.failedContent")}
								</dd>
								<dd>
									<a href="${base}/business_register/edit.jhtml" class="button">${message("shop.business.reEntry")}</a>
									<a href="${base}/" class="button">${message("shop.error.home")}</a>
								</dd>
							[#elseif store.status =="approved"]
								<dt class="red">${message("shop.business.approved")}</dt>
								<dd>
									${message("shop.business.approvedContent")}
								</dd>
								<dd>
									<a href="${base}/business_register/progress.jhtml" class="button">${message("shop.business.payment")}</a>
									<a href="${base}/" class="button">${message("shop.error.home")}</a>
								</dd>
							[/#if]
						[#elseif store.hasExpired]
							<dt>${message("shop.business.expire")}</dt>
							<dd>
								${message("shop.business.expireContent")}
							</dd>
							<dd>
								<a href="${base}/business/renewal/renewal.jhtml" class="button">${message("shop.business.payment")}</a>
								<a href="${base}/" class="button">${message("shop.error.home")}</a>
							</dd>
						[/#if]
					</dl>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.error.title")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/error.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
</head>
<body>
<div class="page-group">

			<div id="page-list-card" class="page">
		
				<div class="content">
				<div>
				<p>${message("shop.error.message")}</p>
						[#if errorMessage?has_content]
							<p>${errorMessage}</p>
						[/#if]
						[#if exception?? && exception.message?has_content]
							<p>${exception.message}</p>
						[/#if]
						[#if constraintViolations?has_content]
							[#list constraintViolations as constraintViolation]
								<p>[${constraintViolation.propertyPath}] ${constraintViolation.message}</p>
							[/#list]
						[/#if]
						<p>
							<a href="javascript:;" onclick="history.back(); return false;">${message("shop.error.back")}</a>
						</p>
						<p>
							<a href="${base}/">&gt;&gt; ${message("shop.error.home")}</a>
						</p>
				<div>
				</div>
			</div>

		</div>
				<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
</body>
</html>
[/#escape]
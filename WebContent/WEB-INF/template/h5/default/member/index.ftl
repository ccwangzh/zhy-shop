[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.member.index")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/myInfo.css" rel="stylesheet" type="text/css" />
		<link href="${base}/resources/h5/${theme}/css/fonts.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script>
			$(function(){
				var $logoutA=$("#logout").find("a");
				var host=window.location.protocol+"//"+window.location.host;
				$logoutA.on("click",function(){
					$.ajax({
						url:host+"/user/loginOut.html",
						type: "POST",
						dataType: "json",
						cache: false,
						success: function(message) {
							 var expires = new Date(); 
			                 expires.setTime(expires.getTime() - 1000);
			                 document.cookie = "logined=;path=/;expires=" + expires.toGMTString() + "";
			                 document.cookie = "mobile=;path=/;expires=" + expires.toGMTString() + "";
			                 document.cookie = "userId=;path=/;expires=" + expires.toGMTString() + "";
			                 window.location.href="/h5/";
						}
					});
				});
			})
		</script>
	</head>

	<body>
		[#assign current = "indexMember" /]
		<div class="page-group">

			<div id="page-list-card" class="page">
				<div class="content">
					<div class="hebg"><img src="${base}/resources/h5/${theme}/images/u1393.png">
						<p>${member.mobile}</p>
						<p>${member.username}</p>
					</div>
					<div class="bar-sp">
						<ul>
							<li>
								<a href="order/list.jhtml?orderStatus=pendingPayment" class="external">
									<i class="font font-pendingPayment"></i><br/>
									待付款
								</a>
							</li>
							<li>
								<a href="order/list.jhtml?orderStatus=shipped" class="external">
									<i class="font font-shipped"></i><br/>
									待收货
								</a>
							</li>
							<li>
								<a href="order/list.jhtml" class="external">
									<i class="font font-allOrders"></i><br/>
									全部订单
								</a>
							</li>
						</ul>
					</div>
					<div class="list-block cards-list collection">

						<ul class="cpul">
							<li><a href="${base}/h5/member/point_log/list.jhtml" class="external"><span class="labletit">${message("shop.member.pointLog.list")}</span> <span class="lableconte">${member.point}</span> </a></li>
							<li><a href="${base}/h5/member/favorite/list.jhtml" class="external"><span class="labletit">${message("shop.member.favorite.list")} </span> </a></li>
						</ul>

					</div>
					<div class="tuic" id="logout">
						<a>安全退出</a>
					</div>
				</div>
			</div>

		</div>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>

		<script>
			
		</script>
	</body>

</html>
[/#escape]
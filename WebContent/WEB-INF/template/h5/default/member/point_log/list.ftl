[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.member.pointLog.list")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/myPoints.css" rel="stylesheet" type="text/css" />
	</head>

	<body>

		<div class="page-group">
			<div id="page-list-card" class="page">
				<div class="content">
					<div class="blockfir">
						<div class="inblock">
							<p>可用积分</p>
							<span>${member.point}</span>
							<a class="gto">前往积分商城</a>
						</div>
						<img src="${base}/resources/h5/${theme}/images/jfbg.png" />
					</div>

					<div class="list-block cards-list points">
						<div class="spxx">${message("shop.member.pointLog.list")}</div>
						<ul>
							[#list page.content as pointLog]
							<li class="card">

								<div class="card-content">
									<div class="card-content-inner">

										<div class="blockone">
											<p>${message("PointLog.Type." + pointLog.type)}</p>
											<p>2015-08-26 15:12:55</p>
										</div>
										<div class="blocktwo">
											<p class="price"><span [#if pointLog.credit-pointLog.debit > 0] class="show"[/#if]>+</span>${pointLog.credit-pointLog.debit}</p>
											<p class="dqjf">当前积分：${pointLog.balance}</p>
										</div>
									</div>
								</div>

							</li>
							[/#list]
						</ul>
						[#if !page.content?has_content]
							<p>${message("shop.member.noResult")}</p>
						[/#if]
					</div>
				</div>
			</div>

		</div>
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
	</body>

</html>
[/#escape]
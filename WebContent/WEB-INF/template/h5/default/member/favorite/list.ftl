[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title>${message("shop.member.favorite.list")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link href="${base}/resources/h5/${theme}/css/myCollection.css" rel="stylesheet" type="text/css" />
		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
		<script>
		$(function(){
			var $favoriteLists = $("#favoriteLists");
			var $delete = $("#favoriteLists a.delete");
			$delete.click(function() {
				if (confirm("${message("shop.dialog.deleteConfirm")}")) {
					var $li = $(this).parents("li");
					var id = $li.find("input[name='id']").val();
					$.ajax({
						url: "/shop/member/favorite/delete.jhtml",
						type: "POST",
						data: {id: id},
						dataType: "json",
						cache: false,
						success: function(message) {
							$.message(message);
							if (message.type == "success") {
								var $siblings = $li.siblings();
								if ($siblings.size() <1) {
									$favoriteLists.after('<p>${message("shop.member.noResult")}<\/p>');
								} else {
									$siblings.last().addClass("last");
								}
								$li.remove();
							}
						}
					});
				}
				return false;
			});
		})
			
		</script>
	</head>

	<body>
		[#assign current = "favoriteList" /]
		<div class="page-group">
			<div id="page-list-card" class="page">
				
				<div class="content">
					<div class="list-block cards-list collection">
						<ul id="favoriteLists">
							[#list page.content as product]
							<li class="card">
								<input type="hidden" name="id" value="${product.id}" />
								<div class="card-header">产品编号：${product.sn}</div>
								<div class="card-content">
									<div class="card-content-inner">
										<img src="${product.thumbnail!setting.defaultThumbnailProductImage}">
										<div class="scwp">
											<p>${abbreviate(product.name, 30)}</p>
										</div>
										<div class="wpprice">
											<p>${currency(product.price, true)}</p>
										</div>
									</div>
								</div>
								<div class="card-footer">
									<a class="btn-sc delete">${message("shop.member.action.delete")}</a>
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
		
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
		[#include "/h5/${theme}/include/daohang.ftl" /]
	</body>

</html>
[/#escape]
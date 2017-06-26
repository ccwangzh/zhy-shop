[#escape x as x?html]
[#assign current = "businessIndex" /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.index")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="index">
					<div class="summary clearfix">
						[#if store.logo?has_content]
							<div>
								<a href="${base}/store/store.jhtml?storeId=${store.id}">
									<img src="${store.logo}" alt="${store.name}" />
								</a>
								<span>
									<a href="${base}/business/store/edit.jhtml">${message("shop.business.index.storeEdit")}</a>
								</span>
							</div>
						[/#if]
						<ul>
							<li>
								<span>${message("shop.business.navigation.welcome")}: </span>
								${store.business.member.username}
							</li>
							<li>
								<span>${message("shop.business.index.name")}: </span>
								<a href="${base}/store/store.jhtml?storeId=${store.id}">${abbreviate(store.name, 10)}</a>
							</li>
							<li>
								<span>${message("shop.business.index.storeRank")}: </span>
								${store.storeRank.name}
							</li>
							<li>
								<span>${message("shop.business.index.balance")}: </span>
								<strong>${currency(store.business.member.balance, true, true)}</strong>
								<span>
									<a href="${base}/business/cash/application.jhtml">&nbsp;&nbsp;${message("shop.business.cash.application")}</a>
								</span>
							</li>
							<li>
								<span>${message("shop.business.index.frozenFunds")}: </span>
								<strong>${currency( store.business.member.frozenFunds, true, true)}</strong>
							</li>
							<li>
								<span>${message("shop.business.index.expiryDate")}: </span>
								[#if !store.endDate??]
									${message("shop.business.index.infinite")}
								[#else]
									<strong>${store.endDate?string("yyyy-MM-dd HH: mm: ss")}</strong>
								[/#if]
							</li>
						</ul>
					</div>
					<div class="item">
						<h3>
							${message("shop.business.index.goodsPrompt")}
							<span>${message("shop.business.index.handleMatter")}</span>
						</h3>
						<a href="${base}/business/goods/list.jhtml?status=onTheshelf" class="button">
							${message("shop.business.goods.isMarketable")}
							<em>${marketableGoodsCount}</em>
						</a>
						<a href="${base}/business/goods/list.jhtml?isOutOfStock=true" class="button">
							${message("shop.business.goods.isOutOfStack")}
							<em>${outOfStockGoodsCount}</em>
						</a>
						<a href="${base}/business/goods/list.jhtml?isActive=false" class="button">
							${message("shop.business.goods.notActive")}
							<em>${notActiveGoodsCount}</em>
						</a>
						<a href="${base}/business/goods/list.jhtml?isTop=true" class="button">
							${message("shop.business.goods.isTop")}
							<em>${isTopGoodsCount}</em>
						</a>
						<a href="${base}/business/goods/list.jhtml?isStockAlert=true" class="button">
							${message("shop.business.goods.isStockAlert")}
							<em>${isStockAlertGoodsCount}</em>
						</a>
						<p>
							${message("shop.business.index.releaseSituation")}:
							<em>${goodsCount}/${(store.storeRank.quantity)!'${message("shop.business.index.infiniteQuantity")}'}</em>
						</p>
					</div>
					<div class="item">
						<h3>${message("shop.business.index.platformContact")}</h3>
						<ul>
							<li>${message("Store.phone")}: ${(store.phone)!"-"}</li>
							<li>${message("Store.email")}: ${(store.email)!"-"}</li>
							<li>${message("Store.address")}: ${(store.address)!"-"}</li>
						</ul>
					</div>
					<div class="item">
						<h3>${message("shop.business.index.transactionAlerts")}</h3>
						<a href="${base}/business/order/list.jhtml?status=pendingPayment" class="button">${message("Order.Status.pendingPayment")}</a>
						<a href="${base}/business/order/list.jhtml?status=pendingReview" class="button">${message("Order.Status.pendingReview")}</a>
						<a href="${base}/business/order/list.jhtml?status=pendingShipment" class="button">${message("Order.Status.pendingShipment")}</a>
						<a href="${base}/business/order/list.jhtml?isPendingRefunds=true" class="button">${message("shop.business.order.pendingRefunds")}</a>
						<a href="${base}/business/order/list.jhtml?status=received" class="button">${message("Order.Status.received")}</a>
						<a href="${base}/business/order/list.jhtml?status=listed" class="button">${message("Order.Status.listed")}</a>
					</div>
					<div class="item">
						<h3>
							${message("shop.business.index.salesStatistics")}
							<span>${message("shop.business.index.periodSalesStatistics")}</span>
						</h3>
						<table>
							<tr>
								<th>${message("shop.business.index.covarianceItem")}</th>
								<th>${message("shop.business.index.orderQuantity")}</th>
								<th>${message("shop.business.index.orderAmount")}</th>
							</tr>
							<tr>
								<td>${message("shop.business.index.yesterdaySales")}</td>
								<td>${yesterdayOrderCount}</td>
								<td>${currency(yesterdayOrderAmount,true,ture)}</td>
							</tr>
							<tr>
								<td>${message("shop.business.index.monthSales")}</td>
								<td>${monthOrderCount}</td>
								<td>${currency(monthOrderAmount,true,ture)}</td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
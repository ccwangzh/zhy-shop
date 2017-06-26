[#escape x as x?html]
<div class="span2">
	<div class="memberInfo userInfoLittle">
		<div class="uleft">
			<span class="userLogo"></span>
		</div>
		<div class="uright">
			<span>${member.mobile}</span>
			<span>账户：${member.username}</span>
		</div>
	</div>
	<div class="menu">
		<dl>
			<dt><a href="${base}/member/index.jhtml">${message("shop.member.navigation.title")}</a></dt>
			
			<dd [#if current == "orderList"] class="current"[/#if]>
				<span></span>
				<a href="${base}/member/order/list.jhtml">${message("shop.member.order.list")}</a>
			</dd>
			<dd [#if current == "pointLogList"] class="current"[/#if]>
				<span></span>
				<a href="${base}/member/point_log/list.jhtml">${message("shop.member.pointLog.list")}</a>
			</dd>
			<dd [#if current == "favoriteList"] class="current"[/#if]>
				<span></span>
				<a href="${base}/member/favorite/list.jhtml">${message("shop.member.favorite.list")}</a>
			</dd>
			<dd [#if current == "profileEdit"] class="current"[/#if]>
				<span></span>
				<a href="${base}/member/profile/edit.jhtml">${message("shop.member.profile.edit")}</a>
			</dd>
		</dl>
	</div>
</div>
[/#escape]
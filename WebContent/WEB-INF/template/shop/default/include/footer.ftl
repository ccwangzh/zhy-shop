[#escape x as x?html]
<div class="footer">
	<!--
	<div class="service clearfix">
		<dl>
			<dt class="icon1">新手指南</dt>
			<dd><a href="#">积分获取</a>
				<a href="#">兑换流程</a>
				<a href="#">交易安全</a>
				<a href="#">24小时在线帮助</a>
            </dd>
		</dl>
		<dl>
			<dt class="icon3">支付方式</dt>
			<dd><a href="#">积分兑换</a>
				<a href="#">快捷支付</a>
				<a href="#">信用卡</a>
				<a href="#">货到付款</a>
            </dd>
		</dl>
		<dl>
			<dt class="icon4">售后服务</dt>
			<dd><a href="#">售后政策</a>
				<a href="#">退款说明</a>
				<a href="#">常见问题</a>
				<a href="#">返修/退换货</a>
			</dd>
		</dl>
		<dl>
			<dt class="icon2">特色服务</dt>
			<dd><a href="#">积分商城</a>
				<a href="#">转让交易</a>
				<a href="#">破损补寄</a>
				<a href="#">藏品预挂牌</a>
            </dd>
		</dl>
		<dl>
			<dt class="icon5">客服电话</dt>
			<dd>
				<strong>${setting.phone}</strong>
			</dd>
		</dl>
	</div>-->
	<div class="bottom">
		<div class="info friendLink">
			[@friend_link_list type="text" count = 8]
				<ul>
					[#list friendLinks as friendLink]
						<li>
							<a href="${friendLink.url}" target="_blank">
								${friendLink.name}
							</a>
						</li>
					[/#list]
				</ul>
			[/@friend_link_list]
		</div>
		<div class="info">
			<p>${message("shop.footer.copyright", setting.siteName)}，${setting.certtext}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统由<a href="http://www.bijietech.com/" target="_blank" style="color:#00AAEE">比捷科技</a>提供</p>
		</div>
	</div>
	[#include "/shop/${theme}/include/statistics.ftl" /]
	<script type="text/javascript" src="${base}/resources/shop/${theme}/js/sensorsdata.js"  charset="utf-8"></script>
</div>
[/#escape]
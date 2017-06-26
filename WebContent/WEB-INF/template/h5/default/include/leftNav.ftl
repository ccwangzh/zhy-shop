			[#escape x as x?html]
			<link href="${base}/resources/h5/${theme}/css/leftNav.css" rel="stylesheet" type="text/css" />
			<script type="text/javascript">
				$().ready(function() {
					var $headerName = $("#headerName");
					var $register=$("#register");
					var mobile = getCookie("mobile");
					var nickname = getCookie("nickname");
					var $logout=$("#logout");
					if ($.trim(nickname) != "") {
						$headerName.text(nickname).show();
						$logout.show();
						$register.hide();
						$("#memberUrl").attr("href","javascript:;")
					} else if ($.trim(mobile) != "") {
						$headerName.text(mobile).show();
						$logout.show();
						$register.hide();
						$("#memberUrl").attr("href","/h5shop/h5/member/index.jhtml?wxtype=wx")
					} else {
						$register.show();
						$logout.hide();
						$("#memberUrl").attr("href","javascript:;")
					}
					
					
					var host=window.location.protocol+"//"+window.location.host;
					$logout.on("click",function(){
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
				
				                 window.location.reload();
							}
						});
				   	 })
				   	 
				   	 var leftNavA=$(".leftNav a");
				   	 leftNavA.each(function(index,value){
				   	 	var _href=$(this).attr("href");
				   	 	if(_href.match("/shop")){
				   	 		var newHref=_href.replace("/shop","/shop/h5");
				   	 		$(this).attr("href",newHref);
				   	 	}
				   	 })
				})
				</script>
			<!-- Left Panel with Reveal effect -->
			<div class="panel-overlay"></div>
			<div class="panel panel-left panel-reveal" id='panel-left-demo'>
				<div class="content-block celan">
				<a href="javascript:;" id="memberUrl" class="external">
				<a class="external" href="/h5shop/h5/member/index.jhtml">
					<img style="width: 3rem;" src="${base}/resources/h5/${theme}/images/userce.png" /></a>
					<div id="headerName" class="userMobile"></div>
					<div id="register" class="register"><a class="external" style="font-weight:normal" href="/h5/login.html">登录</a>/<a style="font-weight:normal" class="external" href="/h5/register.html">注册</a></div>
				</a>
				</div>
				<div class="leftNav">
					<ul>
						[@navigation_list position = "middle"]
							[#list navigations as navigation]
								<li [#if navigation.url = url] class="current"[/#if]>
									<a class="external" href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
								</li>
							[/#list]
						[/@navigation_list]
						
					</ul>
					<div id="logout" class="logout">安全退出</div>
				</div>
			</div>
			[/#escape]
[#escape x as x?html]
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>${message("shop.order.checkout")}[#if showPowered][/#if]</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="/favicon.ico">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">

		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
		<link rel="stylesheet" href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
		<link rel="stylesheet" href="${base}/resources/h5/${theme}/css/subOrder.css" />
		<link rel="stylesheet" href="${base}/resources/h5/${theme}/css/fonts.css" />
		
		<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
        <script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.lSelect.js"></script>
        <script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
        <script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
        <script type="text/javascript" src="${base}/resources/h5/${theme}/js/h5Common.js"></script>
	    <script type="text/javascript">
    $().ready(function() {

	var $dialogOverlay = $("#dialogOverlay");
	var $receiverForm = $("#receiverForm");
	var $receiverItem = $("#receiver div");
	var $otherReceiverButton = $("#otherReceiverButton");
	var $newReceiverButton = $("#newReceiverButton");
	var $newReceiver = $("#newReceiver");
	var $areaId = $("#areaId");
	var $newReceiverSubmit = $("#newReceiverSubmit");
	var $newReceiverCancelButton = $("#newReceiverCancelButton");
	var $orderForm = $("#orderForm");
	var $receiverId = $("#receiverId");
	var $paymentMethod = $("#paymentMethod");
	var $shippingMethod = $("#shippingMethod");
	var $paymentMethodId = $("#paymentMethod input:radio");
	var $shippingMethodId = $("#shippingMethod input:radio");
	var $isInvoice = $("#isInvoice");
	var $invoiceTitle = $("#invoiceTitle");
	var $code = $("#code");
	var $couponCode = $("#couponCode");
	var $couponName = $("#couponName");
	var $couponButton = $("#couponButton");
	var $freight = $("#freight");
	var $tax = $("#tax");
	var $promotionDiscount = $("#promotionDiscount");
	var $couponDiscount = $("#couponDiscount");
	var $amount = $("#amount");
	var $useBalance = $("#useBalance");
	var $balance = $("#balance");
	var $submit = $("#submit");
	var amount = ${amount};
	var amountPayable = ${amountPayable};
	var paymentMethodIds = {};
	[@compress single_line = true]
		[#list shippingMethods as shippingMethod]
			paymentMethodIds["${shippingMethod.id}"] = [
				[#list shippingMethod.paymentMethods as paymentMethod]
					"${paymentMethod.id}"[#if paymentMethod_has_next],[/#if]
				[/#list]
			];
		[/#list]
	[/@compress]
	
	[#if isDelivery]
		[#if !member.receivers?has_content]
			$dialogOverlay.show();
		[/#if]
		
		// 地区选择
		$areaId.lSelect({
			url: "${base}/common/area.jhtml"
		});
		
		// 收货地址
		$(".imgtwo").on("click",function() {	
	    Zepto.router.load('#chooseaddress',true)
	
		});	
		
		// 新收货地址取消
		$newReceiverCancelButton.click(function() {
			if ($receiverId.val() == "") {
				$.message("warn", "${message("shop.order.receiverRequired")}");
				return false;
			}
		
				 Zepto.router.back();
		});
	[/#if]
	
	// 计算
	function calculate() {
		$.ajax({
			url: "calculate.jhtml",
			type: "GET",
			data: $orderForm.serialize(),
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.message.type == "success") {
					$freight.text(currency(data.freight, true));
					if (data.tax > 0) {
						$tax.text(currency(data.tax, true)).parent().show();
					} else {
						$tax.parent().hide();
					}
					if (data.promotionDiscount > 0) {
						$promotionDiscount.text(currency(data.promotionDiscount, true)).parent().show();
					} else {
						$promotionDiscount.parent().hide();
					}
					if (data.couponDiscount > 0) {
						$couponDiscount.text(currency(data.couponDiscount, true)).parent().show();
					} else {
						$couponDiscount.parent().hide();
					}
					if (data.amount != amount) {
						$balance.val("0");
						amountPayable = data.amount;
					} else {
						amountPayable = data.amountPayable;
					}
					amount = data.amount;
					$amount.text(currency(amount, true, true));
					if (amount > 0) {
						$useBalance.parent().show();
					} else {
						$useBalance.parent().hide();
					}
					if (amountPayable > 0) {
						$paymentMethod.show();
					} else {
						$paymentMethod.hide();
					}
				} else {
					$.message(data.message);
					setTimeout(function() {
						location.reload(true);
					}, 3000);
				}
			}
		});
	}
	
	// 交易所账户余额
	function getUserMoney() {
		$.ajax({
			url: "/shop/order/userMoney.jhtml",
			type: "GET",
			dataType: "json",
			cache: false,
			success: function(data) {
				$("#AccountBalance").text(data.free);
				if(amount>data.free){
					$('#moneyNotEnough').show();
					$('input[paymentMethod=exChangeAccount]').attr("disabled","disabled");
				}else{
					$('#moneyNotEnough').hide();
				}
			}
		});
	}
	getUserMoney();
	
	// 支付方式
	$paymentMethodId.click(function() {
		var $this = $(this);
		if ($this.prop("disabled")) {
			return false;
		}
		$this.closest("dd").addClass("selected").siblings().removeClass("selected");
		var paymentMethodId = $this.val();
		$shippingMethodId.each(function() {
			var $this = $(this);
			if ($.inArray(paymentMethodId, paymentMethodIds[$this.val()]) >= 0) {
				$this.prop("disabled", false);
			} else {
				if($this.attr("id")!="isListedtrade"){
					$this.prop("disabled", true).prop("checked", false).closest("dd").removeClass("selected");
				}
			}
		});
		calculate();
	});
	
	[#if isDelivery]
		// 配送方式
		$shippingMethodId.click(function() {
			var $this = $(this);
			var $that = $(this);
			if ($this.prop("disabled")) {
				return false;
			}
			$this.closest("dd").addClass("selected").siblings().removeClass("selected");
			var shippingMethodId = $this.val();
			$paymentMethodId.each(function() {
				var $this = $(this);
				if ($.inArray($this.val(), paymentMethodIds[shippingMethodId]) >= 0) {

					$this.prop("disabled", false);
					
				} else {
					if($that.attr("id")!="isListedtrade"){
						$this.prop("disabled", true).prop("checked", false).closest("dd").removeClass("selected");
					}
					
				}
			});
			calculate();
		});
	[/#if]
	
	//選擇默認地址	
	$("#saveAddress").click(function() {
	var namephone= $("[name=my-radio]:checked").parent().find(".div2 span").text();
	var adddizhi=  $("[name=my-radio]:checked").parent().find(".div3").text();
	var receiveId =  $($("[name=my-radio]:checked").parent().parent().parent()[0]).attr('receiverid');
    $("#namephone").html(namephone);
    $(".address").html(adddizhi);
	$("#receiverId").val(receiveId)	;			
	Zepto.router.back();
	calculate();	
	})
	
	
	// 开具发票
	$isInvoice.click(function() {
		if ($(this).prop("checked")) {
			$invoiceTitle.prop("disabled", false).closest("tr").show();
		} else {
			$invoiceTitle.prop("disabled", true).closest("tr").hide();
		}
		calculate();
	});
	
	// 发票抬头
	$invoiceTitle.focus(function() {
		if ($.trim($invoiceTitle.val()) == "${message("shop.order.defaultInvoiceTitle")}") {
			$invoiceTitle.val("");
		}
	});
	
	// 发票抬头
	$invoiceTitle.blur(function() {
		if ($.trim($invoiceTitle.val()) == "") {
			$invoiceTitle.val("${message("shop.order.defaultInvoiceTitle")}");
		}
	});
	
	// 优惠券
	$couponButton.click(function() {
		if ($code.val() == "") {
			if ($.trim($couponCode.val()) == "") {
				return false;
			}
			$.ajax({
				url: "check_coupon.jhtml",
				type: "GET",
				data: {code : $couponCode.val()},
				dataType: "json",
				cache: false,
				beforeSend: function() {
					$couponButton.prop("disabled", true);
				},
				success: function(data) {
					if (data.message.type == "success") {
						$code.val($couponCode.val());
						$couponCode.hide();
						$couponName.text(data.couponName).show();
						$couponButton.text("${message("shop.order.codeCancel")}");
						calculate();
					} else {
						$.message(data.message);
					}
				},
				complete: function() {
					$couponButton.prop("disabled", false);
				}
			});
		} else {
			$code.val("");
			$couponCode.show();
			$couponName.hide();
			$couponButton.text("${message("shop.order.codeConfirm")}");
			calculate();
		}
	});
	
	// 使用余额
	$useBalance.click(function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$balance.prop("disabled", false).parent().show();
		} else {
			$balance.prop("disabled", true).parent().hide();
		}
		calculate();
	});
	
	// 余额
	$balance.keypress(function(event) {
		return (event.which >= 48 && event.which <= 57) || (event.which == 46 && $(this).val().indexOf(".") < 0) || event.which == 8;
	});
	
	// 余额
	$balance.change(function() {
		var $this = $(this);
		if (/^\d+(\.\d{0,${setting.priceScale}})?$/.test($this.val())) {
			var max = ${member.balance} >= amount ? amount : ${member.balance};
			if (parseFloat($this.val()) > max) {
				$this.val(max);
			}
		} else {
			$this.val("0");
		}
		calculate();
	});
	
	//购买协议
	var $purchaseProA=$(".purchaseProA");
	var $ruleOk=$(".ruleOk");
	var $rule=$("#rule");
	var $agreementTitile=$("#agreementTitile");
	var $agreement=$("#agreement");
	var $agreementTitle=$("#agreementTitle");
	var $agreementCon=$("#agreementCon");
	$purchaseProA.on("click",function(){
		$rule.css("display","inline-block");
		$agreementTitle.html($agreementTitile.val());
		$agreementCon.html($agreement.val());
		return false;
	})
	$ruleOk.on("click",function(){
		$rule.css("display","none");
		$agreementTitle.html($agreementTitile.val());
		$agreementCon.html($agreement.val());
	})
	var $proto=$(".proto");
	var $protoRadio=$(".protoRadio");
	$proto.on("click",function(){
		if($protoRadio.hasClass("checkOk")){
			$protoRadio.removeClass("checkOk");
		}else{
			$protoRadio.addClass("checkOk");
		}
	})
	
	// 订单提交
	$submit.click(function() {
		//判断是否勾选购买协议
		if(!$protoRadio.hasClass("checkOk")&&$proto.length>0){
				Zepto.toast("请阅读并同意购买协议");
				return false;
		}
	
		if (amountPayable > 0) {
			if ($paymentMethodId.filter(":checked").size() <= 0) {
				$.message("warn", "${message("shop.order.paymentMethodRequired")}");
				Zepto.toast("${message("shop.order.paymentMethodRequired")}");
				return false;
			}
		} else {
			$paymentMethodId.prop("disabled", true);
		}
		[#if isDelivery]
			if ($shippingMethodId.filter(":checked").size() <= 0) {
				$.message("warn", "${message("shop.order.shippingMethodRequired")}");
				Zepto.toast("${message("shop.order.shippingMethodRequired")}");
				return false;
			}
		[/#if]
	
		var url = "create.jhtml?isListedtrade=";
		if($("#isListedtrade").attr("checked")=="checked"){
			url += "true";
		}else{
			url += "false";
		}
		$.ajax({
			url: url,
			type: "POST",
			data: $orderForm.serialize(),
			dataType: "json",
			cache: false,
			async:false,
			beforeSend: function() {
				$submit.prop("disabled", true);
			},
			success: function(data) {
				if (data.message.type == "success") {
					if(amountPayable > 0){
				
						if($("#paymentMethod input:checked").attr("paymentmethod")=="exChangeAccount"){
							location.href = "exAcountPayment.jhtml?orderSns="+data.orderSns;
						}else{
							location.href = "payment.jhtml?orderSns="+data.orderSns;
						}
					}else{
						location.href = "${base}/h5/member/order/list.jhtml";
					}
				} else {
					$.message(data.message);
					Zepto.toast(data.message);
					setTimeout(function() {
						location.reload(true);
					}, 3000);
				}
			},
			complete: function() {
				$submit.prop("disabled", false);
			}
		});
	});
	
	[#if isDelivery]
		// 收货地址表单验证 新增地址表单提交
		$receiverForm.validate({
			rules: {
				consignee: "required",
				areaId: "required",
				address: "required",
				zipCode: {
					required: true,
					pattern: /^\d{6}$/
				},
				phone: {
					required: true,
					pattern: /^\d{3,4}-?\d{7,9}$/
				}
			},
			submitHandler: function(form) {
				$.ajax({
					url: "save_receiver.jhtml",
					type: "POST",
					data: $receiverForm.serialize(),
					dataType: "json",
					cache: false,
					beforeSend: function() {
						$newReceiverSubmit.prop("disabled", true);
					},
					success: function(data) {
						if (data.message.type == "success") {
							$receiverId.val(data.id);
						
							$(
								[@compress single_line = true]
								'<div  class="selected" receiverId="' + data.id + '">
								    <img class="imgone" src="${base}/resources/h5/${theme}/images/u2329.png" />
									<div class="perinfo">
						            	<p>'+escapeHtml(data.consignee)+'<span>'+escapeHtml(data.phone)+'<\/span><\/p>
						               	<p class="address">'+escapeHtml(data.areaName+data.address)+'<\/p>
						            <\/div>
								<\/div>'
								[/@compress]
								)					
					
						    Zepto.router.back();
							calculate();	
					setTimeout(function() {
						location.reload(true);
					}, 3000);				
						} else {
							$.message(data.message);
						}
					},
					complete: function() {
						$newReceiverSubmit.prop("disabled", false);
					}
				});
			}
		});
	[/#if]
	
	//选择挂牌交易隐藏收获地址
	var $receiverForm=$("#receiverForm");
	var shippingMethodInput=$shippingMethod.find("input");
	shippingMethodInput.each(function(){
	    $(this).on("click",function(){
	    	if($(this).attr("id")=="isListedtrade"){
	    		$receiverForm.hide();
	    	}else{
	    		$receiverForm.show();
	    	}
	    })
	});
	
	//计算总商品数目
	var $productNum=$(".collection .num");
	function getProductNum(){
		var total=0;
		$productNum.each(function(){
			var num=Number($(this).text().substr(1));
			total+=num;
		})
		$("#totalNum").text(total);
		return total;
	}
	getProductNum()
});
</script>
	</head>

	<body>
		<div class="page-group">
			<div class="page page-current" id="confirm">
				<header class="bar bar-nav">
					<a class="button button-link button-nav pull-left" href="#" data-transition='slide-out'>
						<span class="font font-leftjiantou cofff cofff"></span> 
					</a>
					<h1 class="title biaoti">确认下单</h1>
				</header>
				<div class="content" id="receiver">
				[#if member.receivers?size == 0]
					<div  class="noadd">
						<img src="${base}/resources/h5/${theme}/images/u2329.png" />
						<p>还未添加地址
							<a href="#addaddress" id="newReceiverButton">去添加</a>
						</p>
					</div> 
						[/#if]
						[#if member.receivers?size > 0]
							[#list member.receivers as receiver]
					<div[#if receiver == defaultReceiver] class="selected"[/#if] receiverId="${receiver.id}" class="add"  >
						<img class="imgone" src="${base}/resources/h5/${theme}/images/u2329.png" />
						<div class="perinfo">
							<p id="namephone">${receiver.consignee}<span>${receiver.phone}</span></p>
							<p class="address">${receiver.areaName}${receiver.address}</p>
						</div>
						<img class="imgtwo" src="${base}/resources/h5/${theme}/images/u1434.png" />
					</div>
				[/#list]
						[/#if]
						
						
					<div class="list-block cards-list collection">
					[#list orders as order ]
						<ul>
							[#list order.orderItems as orderItem]
								[#if orderItem.product?? && orderItem.product.goods??]
							<li class="card">
								<div class="card-content">
									<div class="card-content-inner">
										<img  src="${orderItem.product.thumbnail!setting.defaultThumbnailProductImage}" alt="${orderItem.product.name}" >
										<div class="scwp">
												<a href="${orderItem.product.url}" title="${orderItem.product.name}" target="_blank">${abbreviate(orderItem.product.name, 50, "...")}</a>
											[#if orderItem.product.specifications?has_content]
												<span class="silver">[${orderItem.product.specifications?join(", ")}]</span>
											[/#if]
											[#if orderItem.type != "general"]
												<span class="red">[${message("Goods.Type." + orderItem.type)}]</span>
											[/#if]
										</div>
										<div class="wpprice">
											<p>	[#if orderItem.type == "general" || orderItem.type == "listedtrade"]
												${currency(orderItem.price, true)}
											[#else]
												-
											[/#if]</p>
											<p class="num">X${orderItem.quantity}</p>
										</div>
									</div>
								</div>
							</li>
					[/#if]
							[/#list]
					

						</ul>
							[/#list]
					</div>
	<form id="orderForm" action="create.jhtml" method="post">
		[#if orderType == "exchange" || orderType == "directBuy"]
				<input type="hidden" name="type" value="${orderType}" /> 
				<input type="hidden" name="productId" value="${productId}" />
				<input type="hidden" name="quantity" value="${quantity}" />
			[/#if]	
					<div class="liuyan">
						<label>留言</label>
						<input type="text" name="memo" placeholder="输入需要备注的信息">
					</div>
					[#if isDelivery]
						<input type="hidden" id="receiverId" name="receiverId"[#if defaultReceiver??] value="${defaultReceiver.id}"[/#if] />
					[/#if]
						[#if orderType == "general"]
						<input type="hidden" name="cartTag" value="${cartTag}" />
					[/#if]
	
					<div class="list-block media-list radi">
					[#if isDelivery || isListedtrade]
						<ul id="shippingMethod">
	[#if isListedtrade]
							<li>
								<label  for="isListedtrade" class="label-checkbox item-content">
        
            <div class="item-inner">
              <div class="item-title-row">
                <div class="item-title"><span>挂牌交易 </span><span>进入交易市场持仓，之后可进行挂牌卖出操作</span>
                </div>
              </div>
            </div>
               <input type="radio" id="isListedtrade" name="shippingMethodId" value=""/>
            <div class="item-media"><i class="icon icon-form-checkbox"></i></div>
          </label>
							</li>
		[/#if]
		
			[#if isDelivery]
							[#list shippingMethods as shippingMethod]
											<li>
								<label  for="shippingMethod_${shippingMethod.id}" class="label-checkbox item-content youji">
        
            <div class="item-inner youjipad">
              <div class="item-title-row">
                <div class="item-title"><span>${shippingMethod.name} </span><span>  ${abbreviate(shippingMethod.description, 100, "...")}</span>
                </div>
              </div>
            </div>
               <input type="radio" id="shippingMethod_${shippingMethod.id}" name="shippingMethodId" value="${shippingMethod.id}" />
            <div class="item-media"><i class="icon icon-form-checkbox"></i></div>
          </label>
							</li>
								[/#list]
							[/#if]
						</ul>
		[/#if]
						<div class="yezf">
						<ul id="paymentMethod">
							[#list paymentMethods as paymentMethod]
										<li>
								<label  for="paymentMethod_${paymentMethod.id}" class="label-checkbox item-content">
        
            <div class="item-inner paypad">
              <div class="item-title-row">
                <div class="item-title"><span>${paymentMethod.name}</span>[#if paymentMethod.method == "exChangeAccount"]
										<span>账户余额：<i id="AccountBalance"></i> 元</span>&nbsp;&nbsp;&nbsp;<a class="shopMainColor" id="moneyNotEnough" target="_blank" href="${setting.exchangeUrl}/charge.html">余额不足，去充值</a>
									[/#if]
                </div>
              </div>
            </div>
              	[#if paymentMethod.method == "exChangeAccount"]
										<input type="radio" id="paymentMethod_${paymentMethod.id}" name="paymentMethodId" value="${paymentMethod.id}" paymentMethod="exChangeAccount" />
									[#else]
										<input type="radio" id="paymentMethod_${paymentMethod.id}" name="paymentMethodId" value="${paymentMethod.id}"/>
									[/#if]
            <div class="item-media"><i class="icon icon-form-checkbox choicon"></i></div>
          </label>
							</li>
								[/#list]
						</ul>

						

						</div>

						<div class="tjdd">
							[#list orders as order ]
								[#if order.type == "listedtrade"]
									[#list order.orderItems as orderItem]
										[#if orderItem.product.goods.agreementTitile]
											<div class="proto">
												<div class="protoL">
													<i class="icon icon-form-checkbox choicon protoRadio checkOk"></i>
												</div>
										        <div class="protoCon">
										        	<span>已阅读并同意</span><span class="purchaseProA">《${orderItem.product.goods.agreementTitile}》</span>
										        </div>
												<input type="hidden" id="agreementTitile" value="${orderItem.product.goods.agreementTitile}" />
												<input type="hidden" id="agreement" value="${orderItem.product.goods.agreement}" />
											</div>
										[/#if]
									[/#list]
								[/#if]
							[/#list]
							<div class="spxx">
								<p>共<span id="totalNum"></span>件商品</p>
								<p class="pricexx">${currency(amount, true, true)}</p>
							</div>
							<a href="javascript:;" id="submit">提交订单</a>
						</div>
						
					
					</div>
					</form>
				</div>
				
				
				
			</div>
			
			<div class="page" id="addaddress">
	<header class="bar bar-nav">
					<a class="button button-link button-nav pull-left back" href="" data-transition='slide-out'>
							<span class="font font-leftjiantou cofff"></span> 
					</a>
				
					<h1 class="title biaoti">添加地址</h1>
				</header>
				<div class="content" id="newReceiver">
					<form id="receiverForm" action="save_receiver.jhtml" method="post">
				   <div class="list-block" style="margin: 0;">
		
      <ul>
        <!-- Text inputs -->
        <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">收货人</div>
              <div class="item-input">
                <input type="text" placeholder="请输入收货人姓名" id="consignee" name="consignee">
              </div>
            </div>
          </div>
        </li>
        <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">手机号</div>
              <div class="item-input">
                <input type="text" placeholder="请输入收货人手机号" id="phone" name="phone">
              </div>
            </div>
          </div>
        </li>
        <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">邮编</div>
              <div class="item-input">
                <input type="text" placeholder="请输入邮编" class="" id="zipCode" name="zipCode">
              </div>
            </div>
          </div>
        </li>
            <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">所在地</div>
              <div class="item-input">
                <input type="hidden" placeholder="" class="" id="areaId" name="areaId">
              </div>
            </div>
          </div>
        </li>
            <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">详细地址</div>
              <div class="item-input">
                <input type="text" placeholder="请输入详细收货地址" class="" id="address" name="address">
              </div>
            </div>
          </div>
        </li>
        
              <li>
          <div class="item-content">
            <div class="item-inner">
              <div class="item-title label fo16">设置默认</div>
              <div class="item-input">
         		<input type="checkbox" name="isDefault" value="true" />
			    <input type="hidden" name="_isDefault" value="false" />
              </div>
            </div>
          </div>
        </li>
   
      </ul>
   
<input type="submit" id="newReceiverSubmit" class="btnyes" value="${message("shop.dialog.ok")}" />
<input type="button" id="newReceiverCancelButton" class="btnyes" value="${message("shop.dialog.cancel")}" />
    </div>
    </form>

				</div>
			
			</div>
			
			
			//选择地址
			<div class="page" id="chooseaddress">
				<header class="bar bar-nav">
					<a class="button button-link button-nav pull-left back" href="" data-transition="slide-out">
						<span class="font font-leftjiantou cofff"></span> 
					</a>
					<a class="button button-link button-nav back pull-right cofff" href="javascript:;" data-transition="slide-out" id="saveAddress">
						<span class="cofff">完成</span> 
					</a>
					<h1 class="title biaoti">收货地址</h1>
				</header>
				
				<div class="content">

				
					<div  class="list-block media-list contentaddress">
				<ul>
	[#list member.receivers as receiver]
	<li receiverId="${receiver.id}">
		<label class="label-checkbox item-content">
            
            <div class="item-inner div1" >
              <div class="item-title-row div2" >
                <span>${receiver.consignee} ${receiver.phone}</span>
              </div>
              <div  class="item-subtitle div3">${receiver.areaName}${receiver.address}</div>
              <input type="radio" name="my-radio">
            <div class="item-media div4"><i  class="icon icon-form-checkbox choose"></i> <span> 设为默认地址</span><a style="margin-left:4.5rem"><i  class="font font-bianji"></i>编辑</a><a class="deletea"><i class="font font-delete"></i>删除</a></div>
              
            </div>
          </label>
	</li>
		[/#list]
</ul>
					</div>

					<div class="btnchoose">
						<a href="#addaddress">+ 使用新地址</a>
					</div>
				</div>
			</div>
		</div>

		<script type='text/javascript' src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
		<script type='text/javascript' src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>

		<!-- 购买协议 -->
				<div style="position: relative;width: 18.75rem;height: 34rem;background: rgba(0,0,0,.4); z-index: 999998;display: none;" id="rule">
					<div class="ruleInWrap">
						<div class="ruleIn">
							<h2 id="agreementTitle"></h2>
							<div id="agreementCon"></div>
						</div>
						<div class="ruleOk">确定</div>
					</div>
				</div>
	</body>
</html>
[/#escape]
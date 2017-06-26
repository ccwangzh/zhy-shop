[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.deliveryTemplate.edit")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<style type="text/css">
.dropdownMenu {
	position: relative;
	z-index: 1000000;
}

.dropdownMenu ul {
	width: 140px;
	height: 300px;
	overflow-x: hidden;
	overflow-y: auto;
}

.contentStyle {
	width: ${deliveryTemplate.width}px;
	height: ${deliveryTemplate.height}px;
	position: relative;
	overflow: hidden;
	border: 1px solid #dde9f5;
	[#if deliveryTemplate.background??]
		background: url(${deliveryTemplate.background}) 0px 0px no-repeat;
	[/#if]
}

.contentStyle .item {
	float: left;
	position: absolute;
	top: 0px;
	left: 0px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	-o-user-select: none;
	user-select: none;
	cursor: move;
	overflow: hidden;
	border: 1px solid #dddddd;
	background-color: #ffffff;
}

.contentStyle .selected {
	filter: alpha(opacity = 100);
	opacity: 1;
	-webkit-box-shadow: 0px 0px 10px #dddddd;
	-moz-box-shadow: 0px 0px 10px #dddddd;
	box-shadow: 0px 0px 10px #dddddd;
	border-color: #cccccc;
}

.contentStyle .editable {
	-webkit-user-select: text;
	-moz-user-select: text;
	-ms-user-select: text;
	-o-user-select: text;
	user-select: text;
	cursor: text;
	border-color: #7ecbfe;
}

.contentStyle .text {
	width: 100%;
	_width: auto;
	height: 100%;
	line-height: 22px;
	_float: left;
	color: #666666;
	font-size: 12pt;
	word-break: break-all;
	outline: none;
}

.contentStyle .resize {
	width: 6px;
	height: 6px;
	display: none;
	position: absolute;
	bottom: 0px;
	_bottom: -1px;
	right: 0px;
	_right: -1px;
	overflow: hidden;
	cursor: nw-resize;
	background-color: #cccccc;
}

.contentStyle .selected .resize {
	display: block;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $content = $("#content");
	var $tagMenu = $("#tagMenu");
	var $tagMenuItem = $("#tagMenu li");
	var $deleteTag = $("#deleteTag");
	var $contentStyle = $("#contentStyle");
	var $items = $("#contentStyle div.item");
	var $upload = $("a.upload");
	var $background = $("#background");
	var $width = $("#width");
	var $height = $("#height");
	var $filePicker = $("#filePicker");
	var zIndex = 1;
	
	[@flash_message /]
	
	// 文件上传
	$filePicker.uploader({
		extensions: "${setting.uploadImageExtension}",
		complete: function(file, data) {
			$background.val(data.url);
			$contentStyle.css({
				background: "url(" + data.url + ") 0px 0px no-repeat"
			});
		}
	});
	
	$tagMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$tagMenuItem.click(function() {
		var value = $(this).attr("val");
		if (value != "") {
			var $item = $('<div class="item"><div class="text">' + escapeHtml(value) + '<\/div><div class="resize"><\/div><\/div>').appendTo($contentStyle);
			bind($item);
		}
		$tagMenu.children("ul").hide();
	});
	
	$items.each(function() {
		bind($(this));
	});
	
	// 绑定
	function bind($item) {
		var $text = $item.children("div.text");
		var $resize = $item.children("div.resize");
		var dragStart = {};
		var resizeStart = {};
		var dragging = false;
		var resizing = false;
		
		$text.mousedown(function(event) {
			if ($text.attr("contenteditable") == "true") {
				return true;
			}
			$item.css({"z-index": zIndex ++});
			var position = $item.position();
			dragStart.pageX = event.pageX;
			dragStart.pageY = event.pageY;
			dragStart.left = position.left;
			dragStart.top = position.top;
			dragging = true;
			return false;
		}).mouseup(function() {
			dragging = false;
		}).click(function() {
			$item.addClass("selected").siblings().removeClass("selected");
		}).dblclick(function() {
			$item.addClass("editable")
			$text.attr("contenteditable", "true");
		}).focusout(function() {
			$item.removeClass("editable")
			$text.attr("contenteditable", "false");
		});
		
		$resize.mousedown(function(event) {
			resizeStart.pageX = event.pageX;
			resizeStart.pageY = event.pageY;
			resizeStart.width = $item.width();
			resizeStart.height = $item.height();
			resizing = true;
			return false;
		}).mouseup(function() {
			resizing = false;
		});
		
		$(document).mousemove(function(event) {
			if (dragging) {
				$item.css({"left": dragStart.left + event.pageX - dragStart.pageX, "top": dragStart.top + event.pageY - dragStart.pageY});
				return false;
			}
			if (resizing) {
				$item.css({"width": resizeStart.width + event.pageX - resizeStart.pageX, "height": resizeStart.height + event.pageY - resizeStart.pageY});
				return false;
			}
		}).mouseup(function() {
			dragging = false;
			resizing = false;
		});
	};
	
	// 删除标签
	$deleteTag.click(function() {
		$contentStyle.find("div.selected").remove();
		return false;
	});
	
	$background.on("input propertychange change", function() {
		$contentStyle.css({
			background: "url(" + $background.val() + ") 0px 0px no-repeat"
		});
	});
	
	// 宽度
	$width.on("input propertychange change", function() {
		$contentStyle.width($width.val());
	});
	
	// 高度
	$height.on("input propertychange change", function() {
		$contentStyle.height($height.val());
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			background: {
				pattern: /^(http:\/\/|https:\/\/|\/).*$/i
			},
			width: {
				required: true,
				integer: true,
				min: 1
			},
			height: {
				required: true,
				integer: true,
				min: 1
			},
			offsetX: {
				required: true,
				integer: true
			},
			offsetY: {
				required: true,
				integer: true
			}
		},
		submitHandler: function(form) {
			if ($.trim($contentStyle.html()) == "") {
				$.message("warn", "${message("shop.business.deliveryTemplate.emptyNotAllow")}");
				return false;
			}
			$contentStyle.find("div.item").removeClass("selected editable").find("div.text").removeAttr("contenteditable");
			$content.val($contentStyle.html());
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});
</script>
</head>
<body>
	[#assign current = "deliveryTemplateList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.deliveryTemplate.edit")}
					</div>
					<form id="inputForm" action="update.jhtml" method="post">
						<input type="hidden" name="id" value="${deliveryTemplate.id}" />
						<input type="hidden" id="content" name="content" />
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryTemplate.name")}:
								</th>
								<td>
									<input type="text" name="name" class="text" value="${deliveryTemplate.name}" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									${message("shop.common.action")}:
								</th>
								<td>
									<div id="tagMenu" class="dropdownMenu">
										<a href="javascript:;" class="button">${message("shop.business.deliveryTemplate.addTags")}</a>
										<ul>
											<li val="[#noparse]${deliveryCenter.name}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterName")}</li>
											<li val="[#noparse]${deliveryCenter.contact}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterContact")}</li>
											<li val="[#noparse]${deliveryCenter.areaName}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterArea")}</li>
											<li val="[#noparse]${deliveryCenter.address}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterAddress")}</li>
											<li val="[#noparse]${deliveryCenter.zipCode}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterZipCode")}</li>
											<li val="[#noparse]${deliveryCenter.phone}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterPhone")}</li>
											<li val="[#noparse]${deliveryCenter.mobile}[/#noparse]">${message("shop.business.deliveryTemplate.deliveryCenterMobile")}</li>
											<li val="[#noparse]${order.consignee}[/#noparse]">${message("shop.business.deliveryTemplate.orderConsignee")}</li>
											<li val="[#noparse]${order.areaName}[/#noparse]">${message("shop.business.deliveryTemplate.orderAreaName")}</li>
											<li val="[#noparse]${order.address}[/#noparse]">${message("shop.business.deliveryTemplate.orderAddress")}</li>
											<li val="[#noparse]${order.zipCode}[/#noparse]">${message("shop.business.deliveryTemplate.orderZipCode")}</li>
											<li val="[#noparse]${order.phone}[/#noparse]">${message("shop.business.deliveryTemplate.orderPhone")}</li>
											<li val="[#noparse]${order.sn}[/#noparse]">${message("shop.business.deliveryTemplate.orderSn")}</li>
											<li val="[#noparse]${order.freight}[/#noparse]">${message("shop.business.deliveryTemplate.orderFreight")}</li>
											<li val="[#noparse]${order.fee}[/#noparse]">${message("shop.business.deliveryTemplate.orderFee")}</li>
											<li val="[#noparse]${order.amountPaid}[/#noparse]">${message("shop.business.deliveryTemplate.orderAmountPaid")}</li>
											<li val="[#noparse]${order.weight}[/#noparse]">${message("shop.business.deliveryTemplate.orderWeight")}</li>
											<li val="[#noparse]${order.quantity}[/#noparse]">${message("shop.business.deliveryTemplate.orderQuantity")}</li>
											<li val="[#noparse]${currency(order.amount, true)}[/#noparse]">${message("shop.business.deliveryTemplate.orderAmount")}</li>
											<li val="[#noparse]${order.memo}[/#noparse]">${message("shop.business.deliveryTemplate.orderMemo")}</li>
											<li val="[#noparse]${setting.siteName}[/#noparse]">${message("shop.business.deliveryTemplate.siteName")}</li>
											<li val="[#noparse]${setting.siteUrl}[/#noparse]">${message("shop.business.deliveryTemplate.siteUrl")}</li>
											<li val="[#noparse]${setting.address}[/#noparse]">${message("shop.business.deliveryTemplate.siteAddress")}</li>
											<li val="[#noparse]${setting.phone}[/#noparse]">${message("shop.business.deliveryTemplate.sitePhone")}</li>
											<li val="[#noparse]${setting.zipCode}[/#noparse]">${message("shop.business.deliveryTemplate.siteZipCode")}</li>
											<li val="[#noparse]${setting.email}[/#noparse]">${message("shop.business.deliveryTemplate.siteEmail")}</li>
											<li val="[#noparse]${.now?string('yyyy-MM-dd')}[/#noparse]">${message("shop.business.deliveryTemplate.now")}</li>
										</ul>
									</div>
									<a href="javascript:;" id="deleteTag" class="button">${message("shop.business.deliveryTemplate.deleteTags")}</a>
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryTemplate.content")}:
								</th>
								<td>
									[#noescape]
										<div id="contentStyle" class="contentStyle">${deliveryTemplate.content}</div>
									[/#noescape]
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryTemplate.background")}:
								</th>
								<td>
									<span class="fieldSet">
										<input type="text" id="background" name="background" class="text" value="${deliveryTemplate.background}" maxlength="200" />
										<a href="javascript:;" id="filePicker" class="button">${message("shop.upload.filePicker")}</a>
									</span>
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryTemplate.width")}:
								</th>
								<td>
									<input type="text" id="width" name="width" class="text" value="${deliveryTemplate.width}" maxlength="9" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryTemplate.height")}:
								</th>
								<td>
									<input type="text" id="height" name="height" class="text" value="${deliveryTemplate.height}" maxlength="9" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryTemplate.offsetX")}:
								</th>
								<td>
									<input type="text" name="offsetX" class="text" value="${deliveryTemplate.offsetX}" maxlength="9" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("DeliveryTemplate.offsetY")}:
								</th>
								<td>
									<input type="text" name="offsetY" class="text" value="${deliveryTemplate.offsetY}" maxlength="9" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryTemplate.isDefault")}:
								</th>
								<td>
									<input type="checkbox" name="isDefault"[#if deliveryTemplate.isDefault] checked="checked"[/#if] />
									<input type="hidden" name="_isDefault" value="false" />
								</td>
							</tr>
							<tr>
								<th>
									${message("DeliveryTemplate.memo")}
								</th>
								<td>
									<input type="text" name="memo" class="text" value="${deliveryTemplate.memo}" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									<input type="submit" class="button" value="${message("shop.common.submit")}" />
									<input type="button" class="button" value="${message("shop.common.back")}" onclick="history.back(); return false;" />
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>			
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]			
</body>
</html>
[/#escape]
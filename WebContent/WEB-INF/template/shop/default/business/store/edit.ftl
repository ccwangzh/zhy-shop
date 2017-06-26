[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.store.edit")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $name = $("#name");
	var $mobile = $("#mobile");
	var $email = $("#email");
	var $browserButton = $("a.browserButton");
	var $addAdvertisingImage = $("#addAdvertisingImage");
	var $advertisingImageTable = $("#advertisingImageTable");
	var advertisingImageIndex = ${(store.advertisingImages?size)!0};
	
	[@flash_message /]
	
	// 文件上传
	$browserButton.uploader();
	
	// 增加广告图片
	$addAdvertisingImage.click(function() {
		if ($advertisingImageTable.find("tr").size() >= 7) {
			$.message("warn", "${message("shop.business.store.addAdvertisingImageNotAllowed")}");
			return false;
		}
		$([@compress single_line = true]
			'<tr>
				<td>
					<span class="fieldSet">
						<input type="hidden" name="advertisingImages['+ advertisingImageIndex +'].image" class="advertisingImage"\/>
						<a href="javascript:;"  class="button browserButton">${message("shop.common.upload")}<\/a>
						<span class="preview"><\/span>
					<\/span>
				<\/td>
				<td>
					<input type="text" name="advertisingImages[' + advertisingImageIndex + '].title" class="text" maxlength="200" \/>
				<\/td>
				<td>
					<input type="text" name="advertisingImages[' + advertisingImageIndex + '].url" class="text advertisingImageUrl" maxlength="200" \/>
				<\/td>
				<td>
					<input type="text" name="advertisingImages[' + advertisingImageIndex + '].order" class="text advertisingImageOrder" maxlength="9" style="width: 50px;" \/>
				<\/td>
				<td>
					<a href="javascript:;" class="remove">[${message("shop.common.remove")}]<\/a>
				<\/td>
			<\/tr>'
		[/@compress]).appendTo($advertisingImageTable).find("a.browserButton").uploader();
		advertisingImageIndex ++;
	});
	
	// 删除广告图片
	$advertisingImageTable.on("click", "a.remove", function() {
		$(this).closest("tr").remove();
	});
	
	$.validator.addClassRules({
		advertisingImage: {
			extension: "${setting.uploadImageExtension}"
		},
		advertisingImageUrl: {
			pattern: /^(http:\/\/|https:\/\/|ftp:\/\/|mailto:|\/|#).*$/i
		},
		advertisingImageOrder: {
			digits: true
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: {
				required: true,
				remote: {
					url: "check_name.jhtml?pName=${store.name?url}",
					cache: false
				}
			},
			mobile:{
				required: true,
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "check_mobile.jhtml?pMobile=${store.mobile?url}",
					cache: false
				}
			},
			email: {
				required: true,
				email: true,
				remote: {
					url: "check_email.jhtml?pEmail=${store.email?url}",
					cache: false
				}
			}
		},
		messages: {
			name: {
				remote: "${message("shop.validate.exist")}"
			},
			mobile: {
				pattern: "${message("shop.validate.pattern")}",
				remote: "${message("shop.validate.exist")}"
			},
			email: {
				remote: "${message("shop.validate.exist")}"
			}
		}
	});

});
</script>
</head>
<body>
	[#assign current = "storeEdit" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="item-publish">
					<div class="main clearfix">
						<div class="breadcrumb">
							${message("shop.business.store.edit")}
						</div>
						<form id="inputForm" action="${base}/business/store/update.jhtml" method="post">
							<input type="hidden" name="id" value="${store.id}" />
							<table class="input" id="storeTable">
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.name")}:
									</th>
									<td>
										<input type="text" id="name" name="name" class="text" value="${store.name}" maxlength="200" />	
									</td>
								</tr>
								[#if store.storeRank?? ]
									<tr>
										<th>
											${message("Store.storeRank")}:
										</th>
										<td>
											${store.storeRank.name}
										</td>
									</tr>
								[/#if]
								<tr>
									<th>
										${message("Store.logo")}:
									</th>
									<td>
										<span class="fieldSet">
											<input type="hidden" name="logo" value="${store.logo}"/>
											<a href="javascript:;" class="button browserButton">${message("shop.common.upload")}</a>
											[#if store.logo??]
												<span class="preview"><a href="${store.logo}" target="_blank"><img src="${store.logo}" width="56" height="56" /></a></span>
											[#else]
												<span class="preview"></span>
											[/#if]
										</span>
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.address")}:
									</th>
									<td>
										<input type="text"  name="address" class="text" value="${store.address}" maxlength="200" />	
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.zipCode")}:
									</th>
									<td>
										<input type="text"  name="zipCode" class="text" value="${store.zipCode}" maxlength="200" />	
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.phone")}:
									</th>
									<td>
										<input type="text"  name="phone" class="text" value="${store.phone}" maxlength="200" />	
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.introduction")}:
									</th>
									<td>
										<textarea name="introduction" class="text" maxlength="200" >${store.introduction}</textarea>
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.keyword")}:
									</th>
									<td>
										<input type="text" name="keyword" class="text" value="${store.keyword}" maxlength="200" title="${message("shop.business.store.keywordTitle")}"/>
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.seoTitle")}:
									</th>
									<td>
										<input type="text" name="seoTitle" class="text" value="${store.seoTitle}" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										${message("Store.seoKeywords")}:
									</th>
									<td>
										<input type="text" name="seoKeywords" class="text" value="${store.seoKeywords}" maxlength="200"/>
									</td>
								</tr>
								 <tr>
									<th>
										${message("Store.seoDescription")}:
									</th>
									<td>
										<input type="text" name="seoDescription" class="text" value="${store.seoDescription}" maxlength="200"/>
									</td>
								</tr>
								
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.mobile")}:
									</th>
									<td>
										<input type="text" id="mobile" name="mobile" value="${store.mobile}" class="text" maxlength="16" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("Store.email")}:
									</th>
									<td>
										<input type="text" id="email" name="email" value="${store.email}" class="text" maxlength="200"  />
									</td>
								</tr>
								[#if store.productCategorys??]
									<tr>
										<th>
											${message("Store.productCategorys")}:
										</th>
										<td>
											<table  class="item">
												<tr>
													<th>
														${message("ProductCategory.name")}
													</th>
													<th>
														${message("ProductCategory.generalRate")}
													</th>
												</tr>
												[#list store.productCategorys as productCategory]
													<tr>
														<td>
															${productCategory.name}
														</td>
														<td>
															${productCategory.generalRate}
														</td>
													</tr>
												[/#list]
											</table>
										</td>		
									</tr>
								[/#if]
							</table>
						 	<table id="advertisingImageTable" class="item tabContent">
								<tr>
									<td colspan="4">
										<a href="javascript:;" id="addAdvertisingImage" class="button">${message("shop.business.store.addAdvertisingImage")}</a>
									</td>
								</tr>
								<tr>
									<th>
										${message("AdvertisingImage.image")}
									</th>
									<th>
										${message("AdvertisingImage.title")}
									</th>
									<th>
										${message("AdvertisingImage.url")}
									</th>
									<th>
										${message("shop.common.order")}
									</th>
									<th>
										${message("shop.common.action")}
									</th>
								</tr>
								[#list store.advertisingImages as advertisingImage]
									<tr>
										<td>
											<span class="fieldSet">
												<input type="hidden" name="advertisingImages[${advertisingImage_index}].image" value="${advertisingImage.image}" class="advertisingImage"/>
												<a href="javascript:;"  class="button browserButton">${message("shop.common.upload")}</a>
												[#if advertisingImage.image??]
													<span class="preview"><a href="${advertisingImage.image}" target="_blank"><img src="${advertisingImage.image}" width="56" height="56" /></a></span>
												[#else]
													<span class="preview"></span>
												[/#if]
											</span>
										</td>
										<td>
											<input type="text" name="advertisingImages[${advertisingImage_index}].title" class="text" value="${advertisingImage.title}" maxlength="200" />
										</td>
										<td>
											<input type="text" name="advertisingImages[${advertisingImage_index}].url" class="text advertisingImageUrl" value="${advertisingImage.url}" maxlength="200" />
										</td>
										<td>
											<input type="text" name="advertisingImages[${advertisingImage_index}].order" class="text advertisingImageOrder" value="${advertisingImage.order}" maxlength="9" style="width: 50px;" />
										</td>
										<td>
											<a href="javascript:;" class="remove">[${message("shop.common.remove")}]</a>
										</td>
									</tr>
								[/#list]
							</table>
							<table class="input">
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
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
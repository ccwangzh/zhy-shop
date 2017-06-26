[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.categoryApplication.add")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $name = $("#name");
	var $rate = $("#rate");
	var $productCategoryId = $("#productCategoryId");
	var productCategorys=${productCategoryShop}
	
	[@flash_message /]
	
	$productCategoryId.click(function() {
		var $value = $(this).val();
		if(productCategorys!=""){
			for (var i = 0; i < productCategorys.length; i ++) {
				if (productCategorys[i] == $productCategoryId.val()) {
					$name.text("");
					$rate.text("");
					$productCategoryId.val("")
					$.message("warn", "${message("shop.business.categoryApplication.alreadyOwned")}");
					return false;
				}
			}
		}	
		if($productCategoryId.val()!=""){
			$name.text($.trim($(this).find("option:selected").text()));
			$rate.text($.trim($(this).find("option:selected").attr("name")));
		}else{
			$name.text("");
			$rate.text("");
		}
	});
	
	// 提交验证
	$inputForm.validate({
		submitHandler: function(form) {
			if ($productCategoryId.val() == "") {
				$.message("warn", "${message("shop.business.categoryApplication.categoryApplicationRequired")}");
				return false;
			}
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});
</script>
</head>
<body>
	[#assign current = "categoryApplicationList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
			[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="item-publish">
					<form id="inputForm" action="save.jhtml" method="post">
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("Goods.productCategory")}：
								</th>
								<td>
									<select id="productCategoryId" name="productCategoryId" class="sgcategory">
										<option value="">${message("shop.common.choose")}</option>
										[#list productCategoryTree as productCategory]
											<option value="${productCategory.id}" [#if store.isSelf()]name="${productCategory.selfRate}" [#else]name="${productCategory.generalRate}"[/#if]>
												[#if productCategory.grade != 0]
													[#list 1..productCategory.grade as i]
														&nbsp;&nbsp;
													[/#list]
												[/#if]
												${productCategory.name}
											</option>
										[/#list]
									</select>
								</td>
							</tr>
							<tr>
								<th>
									${message("CategoryApplication.productCategory")}:
								</th>
								<td id="name">
								</td>
							</tr>
							<tr>
								<th>
									${message("CategoryApplication.rate")}:
								</th>
								<td id="rate">
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
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.store.review")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $review = $("#review");
	var $content = $("#content");
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			review: "required",
			content: "required"
		}
	});
	
	$review.on("click", "input:radio", function() {
		if($(this).attr("checked") && $(this).val() == "false"){
			$content.prop("disabled", false).closest("tr").show();
		}else{
			$content.prop("disabled", true).closest("tr").hide();
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.store.review")}
	</div>
	<form id="inputForm" action="reviewed.jhtml" method="post">
		<input type="hidden" name="id" value="${store.id}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.business.base")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.business.store")}" />
			</li>
		</ul>
		<table class="input tabContent">
			[#if businessAttributes?has_content]
				[#list businessAttributes as businessAttribute]
					[#if businessAttribute.type == "text" || businessAttribute.type == "date"|| businessAttribute.type == "name"|| businessAttribute.type == "licenseNumber" || businessAttribute.type == "legalPerson" ||  businessAttribute.type == "idCard" || businessAttribute.type == "mobile" || businessAttribute.type == "phone" || businessAttribute.type == "email" || businessAttribute.type == "organizationCode" || businessAttribute.type == "identificationNumber" ||  businessAttribute.type == "bankName" || businessAttribute.type == "bankAccount"]
						<tr>
							<th>
								${businessAttribute.name}:
							</th>
							<td>
								${business.getAttributeValue(businessAttribute)}
							</td>
						</tr>
					[#elseif businessAttribute.type == "image" || businessAttribute.type == "licenseImage" || businessAttribute.type == "idCardImage" || businessAttribute.type == "organizationImage" || businessAttribute.type == "taxImage"]
						<tr>
							<th>
								${businessAttribute.name}:
							</th>
							<td>
								<a href="${business.getAttributeValue(businessAttribute)}" target="_blank"><img src="${business.getAttributeValue(businessAttribute)}" width="56" height="56" /></a>
							</td>
						</tr>
					[#elseif businessAttribute.type == "select"]
						<tr>
							<th>
								${businessAttribute.name}:
							</th>
							<td>
								${business.getAttributeValue(businessAttribute)}
							</td>
						</tr>
					[#elseif businessAttribute.type == "checkbox"]
						<tr>
							<th>
								${businessAttribute.name}:
							</th>
							<td>
								${business.getAttributeValue(businessAttribute)}
							</td>
						</tr>
					[/#if]
				[/#list]
			[/#if]
		</table>
		<table class="input tabContent" id="storeTable">
			<tr>
				<th>
					${message("Store.name")}:
				</th>
				<td>
					${store.name}
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
			[#if store.storeCategory?? ]
				<tr>
					<th>
						${message("Store.storeCategory")}:
					</th>
					<td>
						${store.storeCategory.name}(${message("StoreCategory.bail")}：${currency(store.storeCategory.bail, true)})
					</td>
				</tr>
			[/#if]
			<tr>
				<th>
					${message("Store.mobile")}:
				</th>
				<td>
					${store.mobile}
				</td>
			</tr>
			<tr>
				<th>
					${message("Store.email")}:
				</th>
				<td>
					${store.email}
				</td>
			</tr>
			[#if store.type="general" && store.productCategorys?? ]
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
		<table class="input" id="review">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.store.review")}：
				</th>
				<td>
					<span class="fieldSet">
						<label>
							<input type="radio" name="review" value="true" />${message("admin.store.approved")}
						</label>&nbsp;&nbsp;
						<label>
							<input type="radio" name="review" value="false" />${message("admin.store.failed")}
						</label>
					</span>
				</td>
			</tr>
			<tr class="hidden">
				<th>
					<span class="requiredField">*</span>${message("admin.store.content")}：
				</th>
				<td>
					<textarea id="content" name="content" class="text"></textarea>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
[/#escape]
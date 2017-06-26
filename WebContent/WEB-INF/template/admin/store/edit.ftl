[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.store.edit")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/admin/css/selectmultiple.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.selectmultiple.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $filePicker = $("a.filePicker");
	var $selectItem = $("#selectItem");
	var $title = $("#title");
	var $storeTable = $("#storeTable");
	var $name = $("#name");
	var $mobile = $("#mobile");
	var $email = $("#email");
	var $mySelect = $("#mySelect");
	
	$filePicker.uploader();
	
	$mySelect.selectMultiple();
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: {
				required: true,
				remote: {
					url: "check_name.jhtml",
					data:{
						name:function(){
			           		return $name.val();
						},
				        pName:function(){
			           		return "${store.name}";
						}
			        },
					cache: false
				}
			},
			mobile:{
				required: true,
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "check_mobile.jhtml",
			     	data:{
						mobile:function(){
			           		return $mobile.val();
						},
				        pMobile:function(){
			           		return "${store.mobile}";
						}
			        },
					cache: false
				}
			},
			email: {
				required: true,
				email: true,
				remote: {
					url: "check_email.jhtml",
					email: true,
					data:{
						email:function(){
			           		return $email.val();
						},
				        pEmail:function(){
			           		return "${store.email}";
						}
			        },
					cache: false
				}
			}
			[@business_attribute_list]
				[#list businessAttributes as businessAttribute]
					[#if businessAttribute.isRequired || businessAttribute.pattern?has_content]
						,businessAttribute_${businessAttribute.id}: {
							[#if businessAttribute.isRequired]
								required: true
								[#if businessAttribute.pattern?has_content],[/#if]
							[/#if]
							[#if businessAttribute.pattern?has_content]
								pattern: /${businessAttribute.pattern}/
							[/#if]
						}
					[/#if]
				[/#list]
			[/@business_attribute_list]
		},
		messages: {
			name: {
				remote: "${message("admin.validate.exist")}"
			},
			mobile: {
				pattern: "${message("admin.validate.pattern")}",
				remote: "${message("admin.validate.exist")}"
			},
			email: {
				remote: "${message("admin.validate.exist")}"
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.store.edit")}
	</div>
	<form id="inputForm" action="update.jhtml" method="post">
		<input type="hidden" name="id" value="${store.id}" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.business.base")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.business.store")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.business.productCategory")}" />
			</li>
		</ul>
		<table class="input tabContent">
			[#if businessAttributes?has_content]
				[#list businessAttributes as businessAttribute]
					[#if businessAttribute.type == "text" || businessAttribute.type == "name"|| businessAttribute.type == "licenseNumber" || businessAttribute.type == "legalPerson" ||  businessAttribute.type == "idCard" || businessAttribute.type == "mobile" || businessAttribute.type == "phone" || businessAttribute.type == "email" || businessAttribute.type == "organizationCode" || businessAttribute.type == "identificationNumber" ||  businessAttribute.type == "bankName" || businessAttribute.type == "bankAccount"]
						<tr>
							<th>
								[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
							</th>
							<td>
								<input type="text" name="businessAttribute_${businessAttribute.id}" value="${business.getAttributeValue(businessAttribute)}" class="text" maxlength="200" />
							</td>
						</tr>
					[#elseif businessAttribute.type == "image" || businessAttribute.type == "licenseImage" || businessAttribute.type == "idCardImage" || businessAttribute.type == "organizationImage" || businessAttribute.type == "taxImage"]
						<tr>
							<th>
								[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
							</th>
							<td>
								<span class="fieldSet">
									<input type="text" name="businessAttribute_${businessAttribute.id}" class="text" value="${business.getAttributeValue(businessAttribute)}" maxlength="200"/>
									<a href="javascript:;" class="button filePicker">${message("admin.upload.filePicker")}</a>
									[#if business.getAttributeValue(businessAttribute)??]
										<a href="${business.getAttributeValue(businessAttribute)}" target="_blank">${message("admin.common.view")}</a>
									[/#if]
								</span>
							</td>
						</tr>
					[#elseif businessAttribute.type == "select"]
						<tr>
							<th>
								[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
							</th>
							<td>
								<select name="businessAttribute_${businessAttribute.id}">
									<option value="">${message("admin.common.choose")}</option>
									[#list businessAttribute.options as option]
										<option value="${option}" [#if option == business.getAttributeValue(businessAttribute)] selected="selected"[/#if]>
											${option}
										</option>
									[/#list]
								</select>
							</td>
						</tr>
					[#elseif businessAttribute.type == "checkbox"]
						<tr>
							<th>
								[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
							</th>
							<td>
								<span class="fieldSet">
									[#list businessAttribute.options as option]
										<label>
											<input type="checkbox" name="businessAttribute_${businessAttribute.id}" value="${option}" [#if (business.getAttributeValue(businessAttribute)?seq_contains(option))!] checked="checked"[/#if]/>${option}
										</label>
									[/#list]
								</span>
							</td>
						</tr>
					[#elseif businessAttribute.type == "date"]
						<tr>
							<th>
								[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
							</th>
							<td>
								<input type="text" name="businessAttribute_${businessAttribute.id}"  value="${business.getAttributeValue(businessAttribute)}" class="text Wdate" onfocus="WdatePicker();" />
							</td>
						</tr>
					[/#if]
				[/#list]
			[/#if]
		</table>
		<table class="input tabContent" id="storeTable">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.name")}:
				</th>
				<td>
					<input type="text"  name="name" class="text" value="${store.name}" maxlength="200" />	
				</td>
			</tr>
			<tr>
				<th>
					${message("Store.storeRank")}:
				</th>
				<td>
					<select name="storeRankId">
						[#list storeRanks as storeRank]
							[#if storeRank.type = store.type]
								<option value="${storeRank.id}" [#if store.storeRank.id == storeRank.id]selected="selected"[/#if]>
									${storeRank.name}(${message("StoreRank.quantity")}：${(storeRank.quantity)!'${message("admin.common.indefinitely")}'}&nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(storeRank.serviceFee, true)})
								</option>
							[/#if]	
						[/#list]
					</select>
				</td>
			</tr>
			[#if store.type="general"]
				<tr id="storeCategory">
					<th>
						${message("Store.storeCategory")}:
					</th>
					<td>
						<select name="storeCategoryId">
							[#list storeCategorys as storeCategory]
								<option value="${storeCategory.id}" [#if store.storeCategory.id == storeCategory.id]selected="selected"[/#if]>
									${storeCategory.name}(${message("StoreCategory.bail")}：${currency(storeCategory.bail, true)})
								</option>
							[/#list]
						</select>
					</td>
				</tr>
				<tr  id="beginDate">
					<th>
						${message("Store.beginDate")}:
					</th>
					<td>
						<span>${store.beginDate}</span>
					</td>
				</tr>
				<tr id="endDate">
					<th>
						${message("Store.endDate")}:
					</th>
					<td>
						<input type="text" id="endDate" name="endDate" class="text Wdate" value="[#if store.endDate??]${store.endDate?string("yyyy-MM-dd")}[/#if]" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd', minDate: '${store.beginDate}'});" />
					</td>
				</tr>
			[/#if]	
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.mobile")}:
				</th>
				<td>
					<input type="text" id="mobile" name="mobile" value="${store.mobile}" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.email")}:
				</th>
				<td>
					<input type="text" id="email" name="email" value="${store.email}" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.setting")}
				</th>
				<td>
					<input type="checkbox" name="isEnabled" value="true"[#if store.isEnabled] checked="checked"[/#if] />${message("Store.isEnabled")}
					<input type="hidden" name="isEnabled" value="false" />
				</td>
			</tr>
		</table>
		<table class="input tabContent">
			<tr>
				<th>
					${message("Store.productCategorys")}:
				</th>
				<td>
					<select name="productCategoryIds" id="mySelect" multiple="multiple">
						[#list productCategoryTree as productCategory]
							<option value="${productCategory.id}" class="" name="[#if store.type="general"]${productCategory.generalRate}[#elseif store.type="self"]${productCategory.selfRate}[/#if]" [#if store.productCategorys?seq_contains(productCategory)] selected="selected" [/#if]>
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
		</table>
		<table class="input">
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
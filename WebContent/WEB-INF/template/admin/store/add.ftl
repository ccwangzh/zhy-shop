[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.store.add")}</title>
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
<script type="text/javascript" src="${base}/resources/admin/js/jquery.quicksearch.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $filePicker = $("a.filePicker");
	var $selectItem = $("#selectItem");
	var $title = $("#title");
	var $storeTable = $("#storeTable");
	var $memberSelect = $("#memberSelect");
	var $sn = $("#sn");
	var $username = $("#username");
	var $memberId = $("#memberId");
	var $type = $("#type");
	var $generalStoreRank = $("#generalStoreRank");
	var $selfStoreRank = $("#selfStoreRank");
	var $storeCategory = $("#storeCategory");
	var $generalTitle = $("#generalTitle");
	var $generalProductCategory = $(".generalProductCategory");
	var $generalRate = $("#generalRate");
	var $selfRate = $("#selfRate");
	var $pleaseSelect = $("#pleaseSelect");
	var selectValues = new Array();
	
	[@flash_message /]
	
	$filePicker.uploader();
	
	$selectItem.selectMultiple();
	
	
	// 会员选择
	$memberSelect.autocomplete("member_select.jhtml", {
		dataType: "json",
		max: 20,
		width: 218,
		scrollHeight: 300,
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.name
				}
			});
		},
		formatItem: function(item) {
			return '<span title="' + escapeHtml(item.username) + '">' + escapeHtml(abbreviate(item.username, 50, "...")) + '<\/span>';
		}
	}).result(function(event, item) {
		$memberId.val(item.id);
		$sn.text(item.sn).closest("tr").show();
		$username.html(escapeHtml(item.username)).closest("tr").show();
	});
	
	// 类型
	$type.change(function() {
		selectValues = [];
		$pleaseSelect.attr("selected",true);
		$storeTable.find(".generalProductCategory").each(function(){
			$(this).remove();
		});	
		switch ($type.val()) {
			case "general":
				$generalStoreRank.prop("disabled", false).show();
				$selfStoreRank.prop("disabled", true).hide();
				$storeCategory.prop("disabled", false).closest("tr").show();
				$generalRate.prop("disabled", false).show();
				$selfRate.prop("disabled", true).hide();
				break;
			case "self":
				$selfStoreRank.prop("disabled", false).show();
				$generalStoreRank.prop("disabled", true).hide();
				$storeCategory.prop("disabled", true).closest("tr").hide();
				$selfRate.prop("disabled", false).show();
				$generalRate.prop("disabled", true).hide();
				break;
		}
	});
	
	// 选择经营分类
	$selectItem.change( function() {
		var $value = $(this).val();
		if($value == ""){
			return false;
		}
		if(selectValues != null){
			for (var i = 0; i < selectValues.length; i ++) {
				if (selectValues[i] == $value) {
					return false;
				}
			}
		}
		
		$rate="";
		switch ($type.val()) {
			case "general":
				$rate=$.trim($(this).find("option:selected").attr("name"))
				break;
			case "self":
				$rate=$.trim($(this).find("option:selected").attr("class"))
				break;
		}
		
		$generalTitle.after(
			[@compress single_line = true]
				"<tr class='generalProductCategory'>
					<td>"+
						$.trim($(this).find("option:selected").text())+"
					<\/td>
					<td>"+
						$rate+"
					<\/td>
					<td>
						<a class='remove'>${message("admin.common.delete")}<\/a>
						<input type='hidden' name='productCategoryIds' value='"+$value+"'\/>
					<\/td>
				<\/tr>"
			[/@compress]
		);
		selectValues.push($value);
	});
	
	// 删除经营分类
	$storeTable.on("click", "a.remove", function() {
		$(this).closest("tr").remove();
		if(selectValues != null){
			for (var i = 0; i < selectValues.length; i ++) {
				if (selectValues[i] == $(this).next().val()) {
					selectValues.splice(i, 1);
					break;
				}
			}
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: {
				required: true,
				remote: {
					url: "check_name.jhtml",
					cache: false
				}
			},
			mobile:{
				required: true,
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "check_mobile.jhtml",
					cache: false
				}
			},
			email: {
				required: true,
				email: true,
				remote: {
					url: "check_email.jhtml",
					cache: false
				}
			},
			storeRankId:"required"
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
		},
		submitHandler: function(form) {
			if ($memberId.val() == "") {
				$.message("warn", "${message("admin.store.memberRequired")}");
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
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.store.add")}
	</div>
	<form id="inputForm" action="save.jhtml" method="post">
		<input type="hidden" id="memberId" name="memberId" />
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
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.store.memberSelect")}:
				</th>
				<td>
					<input type="text" id="memberSelect" name="memberSelect" class="text" maxlength="200" title="${message("admin.store.memberSelectTitle")}" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("Member.sn")}:
				</th>
				<td id="sn">
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("Member.username")}:
				</th>
				<td id="username">
				</td>
			</tr>
			[#list businessAttributes as businessAttribute]
				[#if businessAttribute.type == "text" || businessAttribute.type == "name"|| businessAttribute.type == "licenseNumber" || businessAttribute.type == "legalPerson" ||  businessAttribute.type == "idCard" || businessAttribute.type == "mobile" || businessAttribute.type == "phone" || businessAttribute.type == "email" || businessAttribute.type == "organizationCode" || businessAttribute.type == "identificationNumber" ||  businessAttribute.type == "bankName" || businessAttribute.type == "bankAccount"]
					<tr>
						<th>
							[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
						</th>
						<td>
							<input type="text" name="businessAttribute_${businessAttribute.id}" class="text" maxlength="200" />
						</td>
					</tr>
				[#elseif businessAttribute.type == "image" || businessAttribute.type == "licenseImage" || businessAttribute.type == "idCardImage" || businessAttribute.type == "organizationImage" || businessAttribute.type == "taxImage"]
					<tr>
						<th>
							[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
						</th>
						<td>
							<span class="fieldSet">
								<input type="text" name="businessAttribute_${businessAttribute.id}" class="text"  maxlength="200"/>
								<a href="javascript:;" class="button filePicker">${message("admin.upload.filePicker")}</a>
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
									<option value="${option}">
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
										<input type="checkbox" name="businessAttribute_${businessAttribute.id}" value="${option}" />${option}
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
							<input type="text" name="businessAttribute_${businessAttribute.id}" class="text Wdate" onfocus="WdatePicker();" />
						</td>
					</tr>
				[/#if]
			[/#list]
		</table>
		<table class="input tabContent" id="storeTable">
			<tr>
			    <th>
					<span class="requiredField">*</span>${message("Store.type")}:
			    </th>
			    <td>
				    <select id="type" name="type">
						[#list types as type]
							<option value="${type}">${message("Store.Type." + type)}</option>
						[/#list]
				    </select>
			    </td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="200" />	
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.storeRank")}:
				</th>
				<td>
					<select name="storeRankId" id="generalStoreRank">
						[#list storeRanks as storeRank]
							[#if storeRank.type = "general"]
								<option value="${storeRank.id}"[#if storeRank.isDefault] selected="selected"[/#if]>
									${storeRank.name}(${message("StoreRank.quantity")}：${(storeRank.quantity)!'${message("admin.common.indefinitely")}'} &nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(storeRank.serviceFee, true)})
								</option>
							[/#if]
						[/#list]
					</select>
					<select name="storeRankId" id="selfStoreRank" disabled="disabled"  style="display:none">
						[#list storeRanks as storeRank]
							[#if storeRank.type = "self"]
								<option value="${storeRank.id}" [#if storeRank.isDefault] selected="selected"[/#if]>
									${storeRank.name}(${message("StoreRank.quantity")}：${(storeRank.quantity)!'${message("admin.common.indefinitely")}'} &nbsp;&nbsp;${message("StoreRank.serviceFee")}：${currency(storeRank.serviceFee, true)})
								</option>
							[/#if]	
						[/#list]
					</select>
				</td>
			</tr>
			<tr id="storeCategory">
				<th>
					${message("Store.storeCategory")}:
				</th>
				<td>
					<select name="storeCategoryId">
						[#list storeCategorys as storeCategory]
							<option value="${storeCategory.id}">
								${storeCategory.name}(${message("StoreCategory.bail")}：${currency(storeCategory.bail, true)})
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.mobile")}:
				</th>
				<td>
					<input type="text" name="mobile" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Store.email")}:
				</th>
				<td>
					<input type="text" name="email" class="text" maxlength="200" />
				</td>
			</tr>
		</table>
		<table class="input tabContent">
			<tr>
				<th>
					${message("Store.productCategorys")}:
				</th>
				<td>
					<select id="selectItem" name="productCategoryIds" multiple="multiple">
						[#list productCategoryTree as productCategory]
							<option value="${productCategory.id}" name="${productCategory.generalRate}" class="${productCategory.selfRate}">
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
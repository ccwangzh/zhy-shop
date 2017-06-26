[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.static.generate")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $generateType = $("#generateType");
	var $articleCategoryId = $("#articleCategoryId");
	var $productCategoryId = $("#productCategoryId");
	var $beginDate = $("#beginDate");
	var $endDate = $("#endDate");
	var $loadingBar = $("#loadingBar");
	var $submit = $("input:submit");
	
	// 生成类型
	$generateType.change(function() {
		var $this = $(this);
		if ($this.val() == "article") {
			$articleCategoryId.closest("tr").show();
			$productCategoryId.closest("tr").hide();
			$beginDate.closest("tr").show();
			$endDate.closest("tr").show();
		} else if ($this.val() == "goods") {
			$articleCategoryId.closest("tr").hide();
			$productCategoryId.closest("tr").show();
			$beginDate.closest("tr").show();
			$endDate.closest("tr").show();
		} else {
			$articleCategoryId.closest("tr").hide();
			$productCategoryId.closest("tr").hide();
			$beginDate.closest("tr").hide();
			$endDate.closest("tr").hide();
		}
	});
	
	// 表单提交
	$inputForm.submit(function() {
		$.ajax({
			url: "generate.jhtml",
			type: "POST",
			data: $inputForm.serialize(),
			dataType: "json",
			cache: false,
			beforeSend: function() {
				$generateType.prop("disabled", true);
				$articleCategoryId.prop("disabled", true);
				$productCategoryId.prop("disabled", true);
				$beginDate.prop("disabled", true);
				$endDate.prop("disabled", true);
				$submit.prop("disabled", true);
				$loadingBar.closest("tr").show();
			},
			success: function(data) {
				if (data.generateTime < 60000) {
					time = (data.generateTime / 1000).toFixed(2) + "${message("admin.static.second")}";
				} else {
					time = (data.generateTime / 60000).toFixed(2) + "${message("admin.static.minute")}";
				}
				$.message("success", "${message("admin.static.success")} [${message("admin.static.generateTime")}: " + time + "]");
			},
			complete: function() {
				$generateType.prop("disabled", false);
				$articleCategoryId.prop("disabled", false);
				$productCategoryId.prop("disabled", false);
				$beginDate.prop("disabled", false);
				$endDate.prop("disabled", false);
				$submit.prop("disabled", false);
				$loadingBar.closest("tr").hide();
			}
		});
		return false;
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.static.generate")}
	</div>
	<form id="inputForm" action="generate.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					${message("admin.static.generateType")}:
				</th>
				<td>
					<select id="generateType" name="generateType">
						[#list generateTypes as generateType]
							<option value="${generateType}">${message("admin.static." + generateType)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("Article.articleCategory")}:
				</th>
				<td>
					<select id="articleCategoryId" name="articleCategoryId">
						<option value="">${message("admin.static.emptyOption")}</option>
						[#list articleCategoryTree as articleCategory]
							<option value="${articleCategory.id}">
								[#if articleCategory.grade != 0]
									[#list 1..articleCategory.grade as i]
										&nbsp;&nbsp;
									[/#list]
								[/#if]
								${articleCategory.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("Goods.productCategory")}:
				</th>
				<td>
					<select id="productCategoryId" name="productCategoryId">
						<option value="">${message("admin.static.emptyOption")}</option>
						[#list productCategoryTree as productCategory]
							<option value="${productCategory.id}">
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
			<tr class="hidden">
				<th>
					${message("admin.static.beginDate")}:
				</th>
				<td>
					<input type="text" id="beginDate" name="beginDate" class="text Wdate" value="${defaultBeginDate?string("yyyy-MM-dd")}" title="${message("admin.static.beginDateTitle")}" onfocus="WdatePicker({maxDate: '#F{$dp.$D(\'endDate\')}'});" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					${message("admin.static.endDate")}:
				</th>
				<td>
					<input type="text" id="endDate" name="endDate" class="text Wdate" value="${defaultEndDate?string("yyyy-MM-dd")}" title="${message("admin.static.endDateTitle")}" onfocus="WdatePicker({minDate: '#F{$dp.$D(\'beginDate\')}'});" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					&nbsp;
				</th>
				<td>
					<span id="loadingBar" class="loadingBar">&nbsp;</span>
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
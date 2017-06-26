[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.storeProductCategory.list")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $delete = $("#listTable a.delete");
	
	[@flash_message /]
	
	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("shop.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "delete.jhtml",
					type: "POST",
					data: {id: $this.attr("val")},
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$this.closest("tr").remove();
						}
					}
				});
			}
		});
		return false;
	});

});
</script>
</head>
<body>
	[#assign current = "storeProductCategoryList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.storeProductCategory.list")}
					</div>
					<form id="listForm" action="list.jhtml" method="get">
						<div class="bar">
							<div class="buttonGroup">
								<a href="add.jhtml" class="iconButton">
									<span class="addIcon">&nbsp;</span>${message("shop.common.add")}
								</a>
								<a href="javascript:;" id="refreshButton" class="iconButton">
									<span class="refreshIcon">&nbsp;</span>${message("shop.common.refresh")}
								</a>
							</div>
						</div>
						<table id="listTable" class="list">
							<tr>
								<th>
									<span>${message("ProductCategory.name")}</span>
								</th>
								<th>
									<span>${message("StoreProductCategory.order")}</span>
								</th>
								<th>
									<span>${message("shop.common.action")}</span>
								</th>
							</tr>
							[#list storeProductCategoryTree as storeProductCategory]
								<tr>
									<td>
										<span style="margin-left: ${storeProductCategory.grade * 20}px;[#if storeProductCategory.grade == 0] color: #000000;[/#if]">
											${storeProductCategory.name}
										</span>
									</td>
									<td>
										${storeProductCategory.order}
									</td>
									<td>
										<a href="${base}${storeProductCategory.path}?storeId=${store.id}" target="_blank">[${message("shop.common.view")}]</a>
										<a href="edit.jhtml?id=${storeProductCategory.id}">[${message("shop.common.edit")}]</a>
										<a href="javascript:;" class="delete" val="${storeProductCategory.id}">[${message("shop.common.delete")}]</a>
									</td>
								</tr>
							[/#list]
						</table>
						[#if !storeProductCategoryTree?has_content]
							<p>${message("shop.business.noResult")}</p>
						[/#if]
					</form>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
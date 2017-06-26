[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.goods.list")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/ueditor/ueditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
.moreTable th {
    width: 80px;
    line-height: 25px;
    padding: 5px 10px 5px 0px;
    text-align: right;
    font-weight: normal;
    color: #333333;
    background-color: #f8fbff;
}

.moreTable td {
    line-height: 25px;
    padding: 5px;
    color: #666666;
}

.promotion {
    color: #cccccc;
}

.stockAlert {
    color: orange;
}

.outOfStock {
    color: red;
    font-weight: bold;
}
</style>
<style type="text/css">
	.parameterTable table th {
		width: 146px;
	}
	
	.specificationTable span {
		padding: 10px;
	}
	
	.productTable td {
		border: 1px solid #fafafa;
	}
</style>
<script type="text/javascript">
    $(function(){
        var $inputForm = $("#inputForm");
        $("#approve").click(function(){
            $inputForm.attr('action','checkResult.jhtml?result=true');
            $inputForm.submit();
        });
        
        $("#fail").click(function(){
            var comment = $("#comment").val();
            if(comment==null || comment==""){
                $.message("warn", "${message("请填写审核意见！")}");
                return false;
            }
            $inputForm.attr('action','checkResult.jhtml?result=false');
            $inputForm.submit();
        });
        
        //购物协议(展示)
	    if("${message(goods.type)}"==="listedtrade"){
	    	$("#purChaseProtocol").css("display","inline-block");
	    }else{
	    	$("#purChaseProtocol").css("display","none");
	    }
	    
    });
</script>

</head>
<body>
<div class="breadcrumb">
     <a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; 商品审核
</div>
	<div class="row">
		<div class="span10">
			<div class="item-publish">
				<form id="inputForm" action="checkResult.jhtml" method="post" enctype="multipart/form-data">
					<input type="hidden" name="id" value="${goods.id}" />
					<ul id="tab" class="tab">
						<li>
							<input type="button" value="${message("shop.business.goods.base")}" />
						</li>
						<li>
							<input type="button" value="${message("shop.business.goods.introduction")}" />
						</li>
						<li>
							<input type="button" value="${message("shop.business.goods.productImage")}" />
						</li>
						<li>
							<input type="button" value="${message("shop.business.goods.parameter")}" />
						</li>
						<li>
							<input type="button" value="${message("shop.business.goods.attribute")}" />
						</li>
						<li>
							<input type="button" value="${message("shop.business.goods.specification")}" />
						</li>
						<li>
							<input id="purChaseProtocol" type="button" value="购买协议" style="display:none;" />
						</li>
					</ul>
					<table class="input tabContent">
						<tr>
							<th>
								${message("Goods.productCategory")}:
							</th>
							<td>
							    ${goods.productCategory.name}
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.type")}:
							</th>
							<td>
								${message("Goods.Type." + goods.type)}
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.sn")}:
							</th>
							<td>
								${goods.sn}
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.name")}:
							</th>
							<td>
								<input type="text" name="name" class="text" value="${goods.name}" maxlength="200" disabled="disabled"/>
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.caption")}:
							</th>
							<td>
								<input type="text" name="caption" class="text" value="${goods.caption}" maxlength="200" disabled="disabled"/>
							</td>
						</tr>
						[#if goods.type == "general" || goods.type == "listedtrade"]
							<tr[#if goods.hasSpecification()] class="hidden"[/#if]>
								<th>
									${message("Product.price")}:
								</th>
								<td>
									<input type="text" id="price" name="product.price" class="text" value="${goods.defaultProduct.price}" maxlength="16" disabled="disabled"/>
								</td>
							</tr>
						[/#if]
						<tr[#if goods.hasSpecification()] class="hidden"[/#if]>
							<th>
								${message("Product.cost")}:
							</th>
							<td>
								<input type="text" id="cost" name="product.cost" class="text" value="${goods.defaultProduct.cost}" maxlength="16"  disabled="disabled" title="${message("shop.business.goods.costTitle")}" />
							</td>
						</tr>
						<tr[#if goods.hasSpecification()] class="hidden"[/#if]>
							<th>
								${message("Product.marketPrice")}:
							</th>
							<td>
								<input type="text" id="marketPrice" name="product.marketPrice" class="text" value="${goods.defaultProduct.marketPrice}" maxlength="16" disabled="disabled" title="${message("shop.business.goods.marketPriceTitle")}"/>
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.image")}:
							</th>
							<td>
								<span class="fieldSet">
									<input type="text" id="image" name="image" class="text" value="${goods.image}" maxlength="200" disabled="disabled"/>
									[#if goods.image??]
										<span class="preview"><a href="${goods.image}" target="_blank"><img src="${goods.image}" width="56" height="56"/></a></span>
									[#else]
										<span class="preview"></span>
									[/#if]
								</span>
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.unit")}:
							</th>
							<td>
								<input type="text" name="unit" class="text" value="${goods.unit}" maxlength="200" disabled="disabled"/>
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.weight")}:
							</th>
							<td>
								<input type="text" name="weight" class="text" value="${goods.weight}" maxlength="9" title="${message("shop.business.goods.weightTitle")}" disabled="disabled"/>
							</td>
						</tr>
						[#if goods.type == "general" || goods.type == "listedtrade"]
							<tr[#if goods.hasSpecification()] class="hidden"[/#if]>
								<th>
									${message("Product.rewardPoint")}:
								</th>
								<td>
									<input type="text" id="rewardPoint" name="product.rewardPoint" class="text" value="${goods.defaultProduct.rewardPoint}" maxlength="9" disabled="disabled" title="${message("shop.business.goods.rewardPointTitle")}"/>
								</td>
							</tr>
						[/#if]
						[#if goods.type == "exchange"]
							<tr[#if goods.hasSpecification()] class="hidden"[/#if]>
								<th>
									<span class="requiredField">*</span>${message("Product.exchangePoint")}:
								</th>
								<td>
									<input type="text" id="exchangePoint" name="product.exchangePoint" class="text" value="${goods.defaultProduct.exchangePoint}" maxlength="9" disabled="disabled"/>
								</td>
							</tr>
						[/#if]
						[#if goods.hasSpecification()]
							<tr class="hidden">
								<th>
									<span class="requiredField">*</span>${message("Product.stock")}:
								</th>
								<td>
									<input type="text" id="stock" name="product.stock" class="text" value="1" maxlength="9" disabled="disabled"/>
								</td>
							</tr>
						[#else]
							<tr>
								<th>
									${message("Product.stock")}:
								</th>
								<td>
									<input type="text" id="stock" name="product.stock" class="text" value="${goods.defaultProduct.stock}" maxlength="9" title="${message("Product.allocatedStock")}: ${goods.defaultProduct.allocatedStock}" disabled="disabled" />
								</td>
							</tr>
						[/#if]
						<tr>
							<th>
								${message("Goods.brand")}:
							</th>
							<td>
								${goods.brand.name}
							</td>
						</tr>
						[#if goods.type == "general" || goods.type == "listedtrade" && goods.promotion?has_content]
							<tr>
								<th>
									${message("Goods.promotions")}:
								</th>
								<td>
									${goods.promotion.name}
								</td>
							</tr>
						[/#if]
						[#if goods.storeTags?has_content]
							<tr>
								<th>
									<span class="requiredField">*</span>${message("Goods.storeTags")}:
								</th>
								<td>
									[#list goods.storeTags as storeTag]
										<label>
											<input type="checkbox" name="storeTagIds" value="${storeTag.id}" checked="checked"  disabled="disabled" />${storeTag.name}
										</label>
									[/#list]
								</td>
							</tr>
						[/#if]
						[#if goods.tags?has_content]
							<tr>
								<th>
									<span class="requiredField">*</span>${message("Goods.tags")}:
								</th>
								<td>
									[#list goods.tags as tag]
										<label>
											<input type="checkbox" name="tagIds" value="${tag.id}" checked="checked" disabled="disabled" />${tag.name}
										</label>
									[/#list]
								</td>
							</tr>
						[/#if]
						<tr>
							<th>
								${message("Goods.storeProductCategory")}:
							</th>
							<td>
							    ${goods.storeProductCategory.name}
							</td>
						</tr>
						<tr>
							<th>
								${message("shop.common.setting")}:
							</th>
							<td>
								<label>
									<input type="checkbox" name="isList" value="true"[#if goods.isList] checked="checked"[/#if] disabled="disabled" />${message("Goods.isList")}
									<input type="hidden" name="_isList" value="false" />
								</label>
								<label>
									<input type="checkbox" name="isTop" value="true"[#if goods.isTop] checked="checked"[/#if] disabled="disabled" />${message("Goods.isTop")}
									<input type="hidden" name="_isTop" value="false" />
								</label>
								<label>
									<input type="checkbox" name="isDelivery" value="true"[#if goods.isDelivery] checked="checked"[/#if] disabled="disabled" />${message("Goods.isDelivery")}
									<input type="hidden" name="_isDelivery" value="false" />
								</label>
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.memo")}:
							</th>
							<td>
								<input type="text" name="memo" class="text" value="${goods.memo}" maxlength="200" disabled="disabled" />
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.keyword")}:
							</th>
							<td>
								<input type="text" name="keyword" class="text" value="${goods.keyword}" maxlength="200" title="${message("shop.business.goods.keywordTitle")}" disabled="disabled" />
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.seoTitle")}:
							</th>
							<td>
								<input type="text" name="seoTitle" class="text" value="${goods.seoTitle}" maxlength="200" disabled="disabled" />
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.seoKeywords")}:
							</th>
							<td>
								<input type="text" name="seoKeywords" class="text" value="${goods.seoKeywords}" maxlength="200" disabled="disabled" />
							</td>
						</tr>
						<tr>
							<th>
								${message("Goods.seoDescription")}:
							</th>
							<td>
								<input type="text" name="seoDescription" class="text" value="${goods.seoDescription}" maxlength="200" disabled="disabled" />
							</td>
						</tr>
					</table>
					<table class="input tabContent">
						<tr>
							<td>
							 [#noescape]
							     ${goods.introduction}
							 [/#noescape]
							</td>
						</tr>
					</table>
					<table id="productImageTable" class="item tabContent">
						<tr>
							<th>
								${message("ProductImage.file")}
							</th>
							<th>
								${message("ProductImage.title")}
							</th>
							<th>
								${message("shop.common.order")}
							</th>
						</tr>
						[#list goods.productImages as productImage]
							<tr>
								<td>
									<input type="hidden" name="productImages[${productImage_index}].source" value="${productImage.source}" />
									<input type="hidden" name="productImages[${productImage_index}].large" value="${productImage.large}" />
									<input type="hidden" name="productImages[${productImage_index}].medium" value="${productImage.medium}" />
									<input type="hidden" name="productImages[${productImage_index}].thumbnail" value="${productImage.thumbnail}" />
									<a href="${productImage.large}" target="_blank"><img src="${productImage.large}" height="56" width="56"/></a>
								</td>
								<td>
									<input type="text" name="productImages[${productImage_index}].title" class="text" value="${productImage.title}" maxlength="200" disabled="disabled" />
								</td>
								<td>
									<input type="text" name="productImages[${productImage_index}].order" class="text productImageOrder" value="${productImage.order}" maxlength="9" style="width: 50px;" disabled="disabled" />
								</td>
							</tr>
						[/#list]
					</table>
					<table id="parameterTable" class="parameterTable input tabContent">
						[#list goods.parameterValues as parameterValue]
							<tr>
								<td colspan="2">
									<table data-parameter-index="${parameterValue_index}" data-parameter-entry-index="${parameterValue.entries?size}">
										<tr>
											<th>
												${message("Parameter.group")}:
											</th>
											<td>
												<input type="text" name="parameterValues[${parameterValue_index}].group" class="text parameterGroup" value="${parameterValue.group}" maxlength="200"  disabled="disabled" />
											</td>
										</tr>
										[#list parameterValue.entries as entry]
											<tr>
												<th>
													<input type="text" name="parameterValues[${parameterValue_index}].entries[${entry_index}].name" class="text parameterEntryName" value="${entry.name}" maxlength="200" style="width: 50px;" disabled="disabled" />
												</th>
												<td>
													<input type="text" name="parameterValues[${parameterValue_index}].entries[${entry_index}].value" class="text parameterEntryValue" value="${entry.value}" maxlength="200" disabled="disabled" />
												</td>
											</tr>
										[/#list]
									</table>
								</td>
							</tr>
						[/#list]
					</table>
					<table id="attributeTable" class="input tabContent">
						[#list goods.productCategory.attributes as attribute]
							<tr>
								<th>${attribute.name}:</th>
								<td>
									<select name="attribute_${attribute.id}">
										<option value="">${message("shop.common.choose")}</option>
										[#list attribute.options as option]
											<option value="${option}"[#if option == goods.getAttributeValue(attribute)] selected="selected"[/#if] disabled="disabled" >${option}</option>
										[/#list]
									</select>
								</td>
							</tr>
						[/#list]
					</table>
					<div class="tabContent">
						<table id="specificationTable" class="specificationTable input">
							[#list goods.specificationItems as specificationItem]
								<tr>
									<th>
										<input type="text" name="specificationItems[${specificationItem_index}].name" class="text specificationItemName" value="${specificationItem.name}" data-value="${specificationItem.name}" style="width: 50px;" disabled="disabled" />
									</th>
									<td>
										[#list specificationItem.entries as entry]
											<span>
												<input type="checkbox" name="specificationItems[${specificationItem_index}].entries[${entry_index}].isSelected" value="true"[#if entry.isSelected] checked="checked"[/#if] disabled="disabled" />
												<input type="hidden" name="_specificationItems[${specificationItem_index}].entries[${entry_index}].isSelected" value="false" disabled="disabled" />
												<input type="hidden" name="specificationItems[${specificationItem_index}].entries[${entry_index}].id" class="text specificationItemEntryId" value="${entry.id}" disabled="disabled" />
												<input type="text" name="specificationItems[${specificationItem_index}].entries[${entry_index}].value" class="text specificationItemEntryValue" value="${entry.value}" data-value="${entry.value}" style="width: 50px;" maxlength="200" disabled="disabled" />
											</span>
										[/#list]
									</td>
								</tr>
							[/#list]
						</table>
						<table id="productTable" class="productTable item"></table>
					</div>
					<table class="input tabContent" >
								[#if goods.type == "listedtrade"]
										[#if goods.agreementTitile]
											<tr>
												<td>
													协议名称：${goods.agreementTitile}
												</td>
											</tr>
											<tr>
												
												<td>
													协议内容：
													[#noescape]
														${goods.agreement}
													[/#noescape]
												</td>
											</tr>
										[/#if]
								[/#if]
						</table>
					<table class="input" style="margin-bottom:50px;">
					    <tr>
    					    <th>
    					      <Strong class="red">审核意见：</Strong>
    					    </th>
    					    <td>
    					        [#list comments as comment]
                                    <div class="red">${comment.comment}</div>
                                [/#list]
                                <textarea id="comment" name="comment" style="width: 220px; height: 60px;"></textarea>
                            </td>
					    </tr>
						<tr>
							<th>
								&nbsp;
							</th>
							<td>
                                [#if goods.status == 'pendingCheck']
                                    <input type="button" class="button" value="审核通过" id="approve"/>
                                    <input type="button" class="button" value="审核不通过" id="fail"/>
                                [/#if]
								<input type="button" class="button" value="${message("shop.common.back")}" onclick="history.back(); return false;" />
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
[/#escape]
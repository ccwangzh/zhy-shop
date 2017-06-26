[#escape x as x?html]
[@product_category_root_list count = 6]
	<div class="hotProductCategory" id="hotProductCategory">
		<div class="hotProductCategory-title">全部产品分类</div>
		[#list productCategories as productCategory]
			<dl class="[#if (productCategory_index + 1) % 2 == 0]even[#else]odd[/#if] clearfix">
				<dt>
					<a href="${base}${productCategory.path}">${productCategory.name}</a>
					<span class="bar"></span>
				</dt>
				<dd>
					<ul>
						[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 4]
							[#list productCategories as productCategory]
								<li><a href="${base}${productCategory.path}">${productCategory.name}</a></li>
							[/#list]
						[/@product_category_children_list]
					</ul>
				</dd>
			</dl>
		[/#list]
	</div>
[/@product_category_root_list]
[/#escape]
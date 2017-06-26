[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
[@seo type = "articleList"]
	<title>[#if articleCategory.resolveSeoTitle()?has_content]${articleCategory.resolveSeoTitle()}[#else]${seo.resolveTitle()}[/#if][#if showPowered][/#if]</title>
	<meta name="author" content="SHOP++ Team" />
	<meta name="copyright" content="SHOP++" />
	[#if articleCategory.resolveSeoKeywords()?has_content]
		<meta name="keywords" content="${articleCategory.resolveSeoKeywords()}" />
	[#elseif seo.resolveKeywords()?has_content]
		<meta name="keywords" content="${seo.resolveKeywords()}" />
	[/#if]
	[#if articleCategory.resolveSeoDescription()?has_content]
		<meta name="description" content="${articleCategory.resolveSeoDescription()}" />
	[#elseif seo.resolveDescription()?has_content]
		<meta name="description" content="${seo.resolveDescription()}" />
	[/#if]
[/@seo]
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/article.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $articleSearchForm = $("#articleSearchForm");
	var $keyword = $("#articleSearchForm input");
	
	$articleSearchForm.submit(function() {
		if ($.trim($keyword.val()) == "") {
			return false;
		}
	});

});
</script>
</head>
<body>
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container articleList">
		<div class="breadcrumb">
					<ul>
						<li>
							<a href="${base}/">${message("shop.breadcrumb.home")}</a>
							>
						</li>
						[@article_category_parent_list articleCategoryId = articleCategory.id]
							[#list articleCategories as articleCategory]
								<li>
									<a href="${base}${articleCategory.path}">${articleCategory.name}</a>
									>
								</li>
							[/#list]
						[/@article_category_parent_list]
						<li>${articleCategory.name}</li>
					</ul>
				</div>
		<div class="row">
			<div class="span2">
				[#include "/shop/${theme}/include/hot_article_category.ftl" /]
				[#include "/shop/${theme}/include/hot_article.ftl" /]
				[#include "/shop/${theme}/include/article_search.ftl" /]
			</div>
			<div class="span10">
				
				<div class="result">
					[#if page.content?has_content]
						<ul>
							[#list page.content as article]
								<li>
									<a href="${article.url}" title="${article.title}">${abbreviate(article.title, 80, "...")}</a>
									${article.author}
									<span title="${article.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${article.createdDate}</span>
									<p>${abbreviate(article.text, 220, "...")}</p>
								</li>
							[/#list]
						</ul>
					[#else]
						[#noescape]
							${message("shop.article.noListResult")}
						[/#noescape]
					[/#if]
				</div>
				[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "${base}${articleCategory.path}[#if {pageNumber} > 1]?pageNumber={pageNumber}[/#if]"]
					[#include "/shop/${theme}/include/pagination.ftl"]
				[/@pagination]
			</div>
		</div>
	</div>
	[#include "/shop/${theme}/include/footer.ftl" /]
</body>
</html>
[/#escape]
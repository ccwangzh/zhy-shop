[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.business.review.edit")}[#if showPowered][/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/${theme}/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/${theme}/css/business.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/${theme}/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $delete = $("#inputForm a.delete");

	[@flash_message /]
	
	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("shop.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "delete_reply.jhtml",
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
	[#assign current = "reviewList" /]
	[#include "/shop/${theme}/include/header.ftl" /]
	<div class="container business">
		<div class="row">
		[#include "/shop/${theme}/business/include/menu.ftl" /]
			<div class="span10">
				<div class="list clearfix">
					<div class="breadcrumb">
						${message("shop.business.review.edit")}
					</div>
					<form id="inputForm" action="update.jhtml" method="post">
						<input type="hidden" name="id" value="${review.id}" />
						<table class="input">
							<tr>
								<th>
									${message("Review.goods")}:
								</th>
								<td>
									<a href="${review.goods.url}" target="_blank">${review.goods.name}</a>
								</td>
							</tr>
							<tr>
								<th>
									${message("Review.member")}:
								</th>
								<td>
									[#if review.member??]
										<a href="../member/view.jhtml?id=${review.member.id}">${review.member.username}</a>
									[#else]
										${message("shop.review.anonymous")}
									[/#if]
								</td>
							</tr>
							<tr>
								<th>
									${message("Review.ip")}:
								</th>
								<td>
									${review.ip}
								</td>
							</tr>
							<tr>
								<th>
									${message("Review.score")}:
								</th>
								<td>
									${review.score}
								</td>
							</tr>
							<tr>
								<th>
									${message("Review.content")}:
								</th>
								<td>
									${review.content}
								</td>
							</tr>
							[#if review.replyReviews?has_content]
								<tr>
									<td colspan="4">
										&nbsp;
									</td>
								</tr>
								[#list review.replyReviews as replyReview]
									<tr>
										<th>
											&nbsp;
										</th>
										<td>
											${replyReview.content}
										</td>
										<td width="80">
											<span title="${replyReview.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${replyReview.createdDate}</span>
										</td>
										<td width="80">
											<a href="javascript:;" class="delete" val="${replyReview.id}">[${message("shop.common.delete")}]</a>
										</td>
									</tr>
								[/#list]
							[/#if]
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
[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>${message("admin.goods.commomAgreement")}</title>

    <link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
    <script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
    <script type="text/javascript" src="${base}/resources/shop/${theme}/ueditor/ueditor.js"></script>
    <style>
    	.xxDialog{z-index:9999 !important;}
    	.dialogContent{height:80px;line-height:80px;text-align:center;}
    </style>
	<script type="text/javascript">
		$().ready(function() {
		
			[@flash_message /]
			
			var $pcProtocol = $("#pcProtocol");
			$pcProtocol.editor();
			
			//保存
			$save=$("#save");
			$title=$("#title");
			$content=$("#pcProtocol");
			$protocolID=$("#protocolID");
			$save.click(function() {
				$.ajax({
					url: "saveCommomAgreement.jhtml",
					type: "POST",
					data: {title: $title.val(),content: $content.val(),id: $protocolID.val()},
					dataType: "json",
					success: function(data) {
						if(data.type==="success"){
							$.dialog({
								title: "信息",
								content: data.content,
								ok: null,
								cancel: null,
							});
							setTimeout(function(){
								$(".dialogClose").trigger("click");
							},1000)
						}
					}
				});
			});
		});
	</script>
</head>
<body>
	<div class="breadcrumb">
			<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.brand.list")}
		</div>
	<form id="inputForm" action="saveCommomAgreement.jhtml" method="post" enctype="multipart/form-data">
	  	<input type="hidden" id="protocolID" name="id" value="${agreement.id}">
	    <table class="input tabContent" >
			<tr>
				<td>
					名称：<input class="text" id="title" name="title" value="${agreement.title}">
				</td>
			</tr>
			<tr>					
				<td>
					内容：<textarea id="pcProtocol" name="content" class="editor" style="width: 100%;">${agreement.content}</textarea>
				</td>
			</tr>
			<tr>					
				<td>
					<input id="save" type="button" class="button" value="保存"/>
				</td>
			</tr>
		</table>
	    
	    
	</form>
</body>
</html>
[/#escape]
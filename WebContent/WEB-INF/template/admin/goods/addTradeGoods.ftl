[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.productCategory.add")}</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>

<script type="text/javascript">
$().ready(function() {
    var $filePicker = $("#filePicker");
    $filePicker.uploader();

	var $inputForm = $("#inputForm");

	// 表单验证
    $inputForm.validate({
        rules: {
            name: "required",
            price: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            prePrice: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            openPrice: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            highestPrice: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            lowestPrice: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            volume: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            totalAmount: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            up: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            upDown: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            amplitude: {
                required: false,
                decimal: {
                    integer: 20,
                    fraction: 3
                }
            },
            order: {
                required: true,
                min:0,
                decimal: {
                    integer: 5
                }
            },
            order: "digits"
        }
    });

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; 添加交易商品
	</div>
	<form id="inputForm" action="saveTradeGoods.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>名称:
				</th>
				<td>
					<input type="text" id="name" name="name" class="text" maxlength="200" />
				</td>
			</tr>
            <tr>
                <th>
                    <span class="requiredField"></span>代码:
                </th>
                <td>
                    <input type="text" id="code" name="code" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>图片:
                </th>
                <td>
                    <span class="fieldSet">
						<input type="text" name="image" class="text" maxlength="200" />
						<a href="javascript:;" id="filePicker" class="button">${message("admin.upload.filePicker")}</a>
					</span>
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>最新价:
                </th>
                <td>
                    <input type="text" id="price" name="price" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>昨收价:
                </th>
                <td>
                    <input type="text" id="prePrice" name="prePrice" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>开盘价:
                </th>
                <td>
                    <input type="text" id="openPrice" name="openPrice" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>最高价:
                </th>
                <td>
                    <input type="text" id="highestPrice" name="highestPrice" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>最低价:
                </th>
                <td>
                    <input type="text" id="lowestPrice" name="lowestPrice" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>成交量:
                </th>
                <td>
                    <input type="text" id="volume" name="volume" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>总额:
                </th>
                <td>
                    <input type="text" id="totalAmount" name="totalAmount" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>涨幅:
                </th>
                <td>
                    <input type="text" id="up" name="up" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>涨跌:
                </th>
                <td>
                    <input type="text" id="upDown" name="upDown" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>振幅:
                </th>
                <td>
                    <input type="text" id="amplitude" name="amplitude" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField">*</span>排序:
                </th>
                <td>
                    <input type="text" id="order" name="order" class="text" maxlength="200" />
                </td>
            </tr>
            <tr>
                <th>
                    <span class="requiredField"></span>是否启用:
                </th>
                <td>
                    <input type="checkbox" name="isEnable" value="true" />启用
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
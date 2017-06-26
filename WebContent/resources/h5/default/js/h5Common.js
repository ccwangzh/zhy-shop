
function getOpenId(){
	var wxcode = getUrlParam("code");
	var openid=getCookie('openId');
	if(wxcode && !openid){
		var url = "/wx/getOpenIdByCode.html"
		$.ajax({
			url: url,
			type: "POST",
			data: {code: wxcode},
			dataType: "json",
			cache: false,
			success: function(openId) {
				if(openId){//储存openId
					addCookie('openId',openId,{expires:31536000})
				}
			}
		});
	}
}

getOpenId();				
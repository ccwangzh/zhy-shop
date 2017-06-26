//手机验证
$().ready( function() {
	var mobile={};
	var timer=null;
	mobile.flag=true;
	
	mobile.showError=function(phoneInputId,info){
		$("#"+phoneInputId).focus().siblings(".Validform_checktip").text(info).addClass("Validform_wrong")
	}
	
	mobile.picVerifyCode=function(verifyCodeInputId,verifyCodeBoxId){
		$("#"+verifyCodeInputId).val("").focus();
		$("#"+verifyCodeBoxId).click();
	}
	
	mobile.showVoice=function(type){
		if(type==1){
			$.message("验证码将以电话形式通知到您，来电号码<br/>400-9600-366，请注意接听！");
		}
	}
	
	mobile.clearCountDown=function(verifyCodeInputId,getCodeBtnId,voiceBtnId,type){
		mobile.flag = true;
		$("#"+getCodeBtnId).text("重新获取").removeClass("btn-disabled").addClass("btn-gold");
		if(type==1){
			$("#"+voiceBtnId).text("重新获取语音验证码")
		}else{
			$("#"+voiceBtnId).text("点此立即获取语音验证码")
		}
		clearInterval(timer);
	}
	
	mobile.countDown=function(verifyCodeInputId,getCodeBtnId,voiceBtnId,type){
		$("#"+verifyCodeInputId).val("");
		$("#"+getCodeBtnId).addClass("btn-disabled");
		var time=60;
		timer = setInterval(function() {
			$("#"+getCodeBtnId).text("重新获取("+time+")").removeClass("btn-gold");
			$("#"+voiceBtnId).parent().show();
			if(type==1){
				$("#"+voiceBtnId).siblings("span").text("没有接到语音电话？")
			}else{
				$("#"+voiceBtnId).siblings("span").text("没有收到短信？")
			}
			$("#"+voiceBtnId).text(time + "秒后重新获取语音验证码")
			time--;
			if (time == -1) {
				mobile.clearCountDown(verifyCodeInputId,getCodeBtnId,voiceBtnId,type);
			}

		}, 1000)
	}
	
	mobile.sendVerifyCode=function(url,phone,type,verifyCodeInputId,getCodeBtnId,voiceBtnId,modify,valiCode){
		if(mobile.flag){
			mobile.flag=false;
			mobile.countDown(verifyCodeInputId,getCodeBtnId,voiceBtnId,type);
			var data=$.param({"username":phone});
			 $.ajax({
	               type: "POST",
	               url: url,
	               data: data,
	               success: function(data){
	            	   if(data.success){
		   					mobile.showVoice(type);	
		   				}else{
		   					$.message(data.msg);
		   					mobile.picVerifyCode("registerValiCode","imageStream1")
		   					mobile.flag=true;
		   					mobile.clearCountDown(verifyCodeInputId,getCodeBtnId,voiceBtnId,type);
		   					return false;
		   				}
	                  }
	            });
		}
	}
	
	mobile.verifiMobile=function(phoneInputId,type,verifyPhoneUrl,sendVerifyCodeUrl,verifyCodeInputId,getCodeBtnId,voiceBtnId,modify,valiCode,useType){//参数：手机输入框id,验证手机是否可用接口url,发送手机验证码接口url,严重码输入框id,获取短信验证码按钮id,获取语音验证码按钮id
		var phone=$("#"+phoneInputId).val();
		var pattern = /^1\d{10}$/;
		
		
		if(mobile.flag){
			if(!phone){
				mobile.showError(phoneInputId,"请输入手机号");
				return false;
			}else if (!pattern.test(phone)) {
				mobile.showError(phoneInputId,"请输入正确的手机号");
				return false;
			}else{
				if(verifyPhoneUrl){
					var data=$.param({"username":phone});
					$.ajax({
			             type: "POST",
			             url: verifyPhoneUrl,
			             data: data,
			             success: function(data){
			               //false:已注册；true:未注册	
			               if(useType=="findPassword"){
			            	   //找回密码
			            	   if(!data){
				          		   mobile.sendVerifyCode(sendVerifyCodeUrl,phone,type,verifyCodeInputId,getCodeBtnId,voiceBtnId,modify,valiCode);
				          	   }else{
				          		   var  message={"content":"用户不存在",type:"error"}
					   		       $.message(message);
				          	   }
			               }else if(useType=="register"){
			            	   //注册
			            	   if(data){
				          		   mobile.sendVerifyCode(sendVerifyCodeUrl,phone,type,verifyCodeInputId,getCodeBtnId,voiceBtnId,modify,valiCode);
				          	   }else{
				          		   var  message={"content":"手机号已被使用",type:"error"}
					   		       $.message(message);
				          	   }
			               }
			          	   
			             }
			        });
				}else{
					mobile.sendVerifyCode(sendVerifyCodeUrl,phone,type,verifyCodeInputId,getCodeBtnId,voiceBtnId,modify);
				}
				
			}
		}
	}
	//jquery扩展方法
	$.extend(mobile);
});
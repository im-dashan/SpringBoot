var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

$(function () {
// 短信验证码倒计时效果实现
	$("#messageCodeBtn").on("click", function () {

		var phone = $.trim($("#phone").val());
		if (phone == "") {
			alert("请输入手机号！")
			return;
		}
		//发送验证码
		$.ajax({
			url: "/p2p/loan/sendMessageCode",
			type: "get",
			data: {
				"phone": phone
			},
			success: function (data) {
				if (data.code == 1) {
					// 短信发送成功
					if (!$("#messageCodeBtn").hasClass("on")) {
						$.leftTime(60, function (d) {
							//d.status,值true||false,倒计时是否结束;
							//d.s,倒计时秒;
							if (d.status) {
								$("#messageCodeBtn").addClass("on")
								// 让00秒显示为60秒
								$("#messageCodeBtn").html(d.s == '00' ? '60s后获取' : d.s + 's后获取')
							} else {
								$("#messageCodeBtn").removeClass("on")
								$("#messageCodeBtn").html("获取验证码")
							}
						});
					}
				}
			}
		});
	});


	// 登录
	$("#loginBtn").on("click", function () {
		// 获取文本框
		var phone = $.trim($("#phone").val());
		var loginPassword = $.trim($("#loginPassword").val());
		var messageCode = $.trim($("#messageCode").val());

		if (phone == "") {
			alert("手机号不能为空！")
			return;
		}
		if (loginPassword == "") {
			alert("密码不能为空！")
			return;
		}
		if (messageCode == "") {
			alert("验证码不能为空！")
			return;
		}
		//发送登录数据
		$.ajax({
			url: "/p2p/loan/login",
			type: "get",
			data: {
				"phone": phone,
				"loginPassword": $.md5(loginPassword),
				"messageCode": messageCode
			},
			success: function (data) {
				if (data.code == 1) {
					// 登录成功,调转到首页
					window.location.href = "/p2p/index"
				} else {
					// 清空数据
					$("#phone").val("");
					$("#loginPassword").val("");
					$("#messageCode").val("");
					alert(data.message);
				}
			},
			error: function (data) {
				$("#phone").val("");
				$("#loginPassword").val("");
				$("#messageCode").val("");
				alert("登录失败！");
			}
		});
	});

});


//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	// 对手机号进行非空和格式验证
	$("#phone").on("blur", function (){
		var phone = $.trim($("#phone").val())
		if (phone == ""){
			showError("phone", "请输入手机号！")
		}else if (!/^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[35678]\d{2}|4(?:0\d|1[0-2]|9\d))|9[189]\d{2}|66\d{2})\d{6}$/.test(phone)) {
			showError("phone", "请输入正确的手机号")
		}else {
			showSuccess("phone")
		}
	});

	// 对姓名进行非空和格式验证
	$("#realName").on("blur", function (){
		var realName = $.trim($("#realName").val())
		if (realName == ""){
			showError("realName", "姓名不能为空！")
		}else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
			showError("realName", "请输入正确的姓名！")
		}else {
			showSuccess("realName")
		}
	});

	// 对身份证非空和格式验证
	$("#idCard").on("blur", function (){
		var idCard = $.trim($("#idCard").val())
		if (idCard == ""){
			showError("idCard", "身份证不能为空！")
		}else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
			showError("idCard", "请输入正确的身份证号！")
		}else {
			showSuccess("idCard")
		}
	});

	// 短信验证码倒计时效果实现
	$("#messageCodeBtn").on("click", function () {
		// 对手机号和姓名，身份证进行验证
		// 触发两个输入框的失去焦点事件
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();

		var phone = $.trim($("#phone").val());
		// 选择器
		var errorText = $("div[id$='Err']").text();
		if (errorText == "") {
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
									$("#messageCodeBtn").html(d.s == '00' ? '60s重新获取' : d.s + 's重新获取')
								} else {
									$("#messageCodeBtn").removeClass("on")
									$("#messageCodeBtn").html("获取验证码")
								}
							});
						}
					}
				}
			});
		}
	});

	// 注册按钮点击事件
	$("#btnRegist").on("click", function () {
		// 触发三个输入框的失去焦点事件
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();

		//获取值
		var phone = $.trim($("#phone").val());
		var realName = $.trim($("#realName").val());
		var idCard = $.trim($("#idCard").val());
		var messageCode = $.trim($("#messageCode").val());

		// 对验证码输入框进行非空验证
		if (messageCode == ""){
			showError("messageCode", "请输入验证码！")
		}else {
			showSuccess("messageCode")
		}

		// 选择器
		var errorText = $("div[id$='Err']").text();
		if (errorText == "") {
			// 注册
			$.ajax({
				url: "/p2p/loan/checkRealName",
				type: "post",
				data: {
					"phone": phone,
					"realName": realName,
					"idCard": idCard,
					"messageCode": messageCode
				},
				success: function (data) {
					if (data.code == 1) {
						// 实名认证成功
						window.location.href = "/p2p/index";
					} else {
						showError("messageCode", data.message)
					}
				},
				error: function () {
					showError("messageCode", "系统繁忙，认证失败！")
				}
			});
		}
	});

});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}
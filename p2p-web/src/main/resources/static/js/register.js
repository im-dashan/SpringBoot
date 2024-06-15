//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    // 统一注册样式
    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });

	// 对手机号文本框进行失去焦点验证
	$("#phone").on("blur", function () {
		var phone = $.trim($("#phone").val());
		// 非空验证
		if (phone == "") {
			showError("phone", "请输入手机号")
		}
		// 验证是否是中国大陆手机号
		else if (!/^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[35678]\d{2}|4(?:0\d|1[0-2]|9\d))|9[189]\d{2}|66\d{2})\d{6}$/.test(phone)) {
			showError("phone", "请输入正确的手机号")
		}
		//通过ajax请求后台，验证数据库是否有该手机号
		// 1.存在，提示错误 2.不存在，提示成功
		else {
			$.ajax({
                url:"/p2p/loan/checkPhone",
                type:"get",
                data:"phone=" + phone,
                success:function (data){
                    if (data.code == 1){
                        showSuccess("phone")
                    }else {
                        showError("phone", data.message);
                    }
                },
                error:function (){
                    showError("phone", "系统繁忙请稍后再试！")
                }
            });
		}
	});

    // 密码框失去焦点验证
    $("#loginPassword").on("blur", function (){
        var loginPassword = $.trim($("#loginPassword").val());
        if (loginPassword == ""){
            showError("loginPassword", "密码不能为空！")
        }else if (loginPassword.length < 6 || loginPassword.length > 20){
            showError("loginPassword", "密码的长度只能在6-20个字符！")
        }else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)){
            showError("loginPassword", "密码只能用数字和英文大小写！")
        }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
            showError("loginPassword", "密码应包含英文和数字！")
        }else {
            showSuccess("loginPassword")
        }
    });

    // 注册按钮点击事件
    $("#btnRegist").on("click", function () {
        // 触发 上面两个输入框的失去焦点时间
        $("#phone").blur();
        $("#loginPassword").blur();
        // 对验证码输入框进行非空验证
        var messageCode = $.trim($("#messageCode").val());
        if (messageCode == "") {
            showError("messageCode", "请输入验证码")
        } else {
            showSuccess("messageCode")
        }
        // $("input[name$='letter']")
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        $("#loginPassword").val($.md5(loginPassword));
        var errorText = $("div[id$='Err']").text();
        if (errorText == "") {
            // 注册
            $.ajax({
                url: "/p2p/loan/register",
                type: "post",
                data: {
                    "phone": phone,
                    "loginPassword": $.md5(loginPassword),
                    "messageCode": messageCode
                },
                success: function (data) {
                    if (data.code == 1) {
                        window.location.href = "/p2p/loan/realName";
                    } else {
                        showError("messageCode", data.message);
                    }
                },
                error: function () {
                    showError("messageCode", "系统繁忙，注册失败!")
                }
            })
        }

    });

    // 短信验证码倒计时效果实现
    $("#messageCodeBtn").on("click", function () {
        // 对手机号和密码框进行验证
        // 触发两个输入框的失去焦点事件
        $("#phone").blur();
        $("#loginPassword").blur();

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

});
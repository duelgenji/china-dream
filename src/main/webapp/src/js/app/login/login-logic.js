define("login-logic", ["jquery", "user-repos"], function(require, exports) {

	var
		$ = require("jquery"),

		userRepos = require("user-repos");

	function fn_initEvent() {
		$("#ul_loginType li").click(function() {
			$("#hd_type").val(
				$(this).removeClass("active").addClass("active")
				.siblings().removeClass('active')
				.end().data("type"));

			$("#input_user").val("");
			$("#input_password").val("");
			$("#hd_username").val("");
		});

		$("#input_password,#input_user").on("keydown",function(event) {
			if(event.keyCode==13){
				$("#btn_login").click();
			}
		});
		$("#btn_login").click(function() {
			var val = $("#input_user").val(),
				psw;

			if (!val) {
				$("#mainMask").css("display", "block");
				$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 160);
				$("#bubbleLayerWrap .error-tt p").text("请输入用户名");
				return;
			} else {
				$("#hd_username").val(val);
			}

			psw = $("#input_password").val();
			if (!psw) {
				$("#mainMask").css("display", "block");
				$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 250);
				$("#bubbleLayerWrap .error-tt p").text("请输入密码");
				return;
			}

			ajaxLogin(val,psw,function(data) {
				var account = {}
				account.email = val;
				account.password = psw;
				saveAccount(account);

				saveUserInfo(data);

				location.href = "index.html";
			}, function(result) {
				var message = result.message;
				if (message==null || message == "") {
					message = "对不起,服务端异常,您目前无法登录!";
				}

				$("#mainMask").css("display", "block");
				$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 160);
				$("#bubbleLayerWrap .error-tt p").text(message);
			},function(){
				$("#mainMask").css("display", "block");
				$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 180);
				$("#bubbleLayerWrap .error-tt p").text("对不起,服务端异常,您目前无法登录!");
			});
		});

		$("input.ui-logininput").on("focus", function() {
			$("#mainMask").css("display", "none");
			$("#bubbleLayer").removeClass("bubbleLayer-show");
		});

		$("#chk_cookie").click(function() {
			var val = 1 - parseInt($("#hd_savelogin").val());
			$("#hd_savelogin").val(val);
		});


		$("#btn_register").click(function(){
			self.location = "main.html#quickRegister"
		});
	}

	exports.load = function() {
		fn_initEvent();
	};
});
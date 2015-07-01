define("forgetPassword-logic", ["jquery", "user-repos"], function(require, exports) {

    var
        $ = require("jquery"),

        userRepos = require("user-repos"),

        validMod = require("pure-validator");

    function fn_initEvent() {

        $("#btn_login").click(function() {
            var val = $("#input_user").val()

            if (!val) {
                $("#mainMask").css("display", "block");
                $("#bubbleLayer").addClass("bubbleLayer-show").css("top", 150);
                $("#bubbleLayerWrap .error-tt p").text("请输入邮箱");
                return;
            } else if (!validMod.isEmail(val)) {
                $("#mainMask").css("display", "block");
                $("#bubbleLayer").addClass("bubbleLayer-show").css("top", 150);
                $("#bubbleLayerWrap .error-tt p").text("邮箱格式错误");
                return;
            } else {
                $("#hd_username").val(val);
            }

            ajaxForgetPassword(val,function(data) {
                location.href = "emailSent.html";
            }, function(result) {
                var message = result.message;
                if (validMod.isEmptyOrNull(message)) {
                    message = "对不起,服务端异常,您目前无法重置密码!";
                }

                $("#mainMask").css("display", "block");
                $("#bubbleLayer").addClass("bubbleLayer-show").css("top", 150);
                $("#bubbleLayerWrap .error-tt p").text(message);
            },function(){
                $("#mainMask").css("display", "block");
                $("#bubbleLayer").addClass("bubbleLayer-show").css("top", 150);
                $("#bubbleLayerWrap .error-tt p").text("对不起,服务端异常,您目前无法重置密码!");
            });
        });

        $("input.ui-logininput").on("focus", function() {
            $("#mainMask").css("display", "none");
            $("#bubbleLayer").removeClass("bubbleLayer-show");
        });
    }

    exports.load = function() {
        fn_initEvent();
    };
});
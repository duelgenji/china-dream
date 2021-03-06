/**
 * 注册页涉及到的js逻辑操作：
 *  不同注册用户的显示切换
 *  验证表单提交:更具不同用户的注册内容进行验证
 *  提交表单
 *    提交成功后的显示 弹出对话框-显示当前用户的流水号
 *    提交失败后的警告 弹出对话框-提示错误
 */

function pwdStrength(pwd) {
    O_color = "#eeeeee";
    L_color = "#FF0000";
    M_color = "#FF9900";
    H_color = "#33CC00";
    var level = 0, strength = "O";
    if (pwd == null || pwd == '') {
        strength = "O";
        Lcolor = Mcolor = Hcolor = O_color;
    }
    else {
        var mode = 0;
        if (pwd.length <= 0)
            mode = 0;
        else {
            for (i = 0; i < pwd.length; i++) {
                var charMode, charCode;
                charCode = pwd.charCodeAt(i);
                // 判断输入密码的类型
                if (charCode >= 48 && charCode <= 57) //数字
                    charMode = 1;
                else if (charCode >= 65 && charCode <= 90) //大写
                    charMode = 2;
                else if (charCode >= 97 && charCode <= 122) //小写
                    charMode = 4;
                else
                    charMode = 8;
                mode |= charMode;
            }
            // 计算密码模式
            level = 0;
            for (i = 0; i < 4; i++) {
                if (mode & 1)
                    level++;
                mode >>>= 1;
            }
        }
        switch (level) {
            case 0:
                strength = "O";
                Lcolor = Mcolor = Hcolor = O_color;
                break;
            case 1:
                strength = "L";
                Lcolor = L_color;
                Mcolor = Hcolor = O_color;
                break;
            case 2:
                strength = "M";
                Lcolor = Mcolor = M_color;
                Hcolor = O_color;
                break;
            default:
                strength = "H";
                Lcolor = Mcolor = Hcolor = H_color;
                break;
        }
    }
    document.getElementById("strength_L").style.background = Lcolor;
    document.getElementById("strength_M").style.background = Mcolor;
    document.getElementById("strength_H").style.background = Hcolor;
    return strength;
}

function pwdCheck () {
    if ($("#input_psw").val() == $("#input_psw2").val()) {
        $("#psw_dif").css("display", "none");
    } else {
        $("#psw_dif").css("display", "");
    }
}

define("register-logic", ["user-repos", "jquery", "pure-dialog", "pure-validator"], function (require, exports) {

    var
        $ = require("jquery"),

        userRepos = require("user-repos"),

        dialogMod = require("pure-dialog"),

        validMod = require("pure-validator"),

        regOkHtml = '<div class="ui-regTip"><span><img class="ui-tipOkIcon"/>恭喜您,注册成功!</span><p><a href="login.html">现在去登录</a><a href="index.html">返回平台首页</a></p></div>',

        regFailHtml = '<div class="ui-regTip"><span><img class="ui-tipFailIcon"/>对不起,注册失败!</span></div>',

        regErrorHtml = '<div class="ui-regTip"><span><img class="ui-tipErrorIcon"/>对不起,注册出现异常!</span></div>',

        serviceDg = {
            id: "serviceDg",
            width: 650,
            height: 600,
            title: "中梦国网服务协议",
            buttons: []
        },

        regCallDg = {
            id: "regCallDg",
            isHideTopClose: true,
            width: 400,
            height: 300,
            title: "注册提示",
            buttons: []
        };


    /**
     * 企业用户注册验证
     *   账户（必填）
     *   密码/确认密码（必填+相同)
     */
    function fn_companyCheck() {
        var
            isError = false,
            doms = $("#input_companyName,#industryCode,#provinceCode,#ownershipCode,#input_website");

        for (var i = 0, l = doms.length, dom; i < l; i++) {
            dom = $(doms[i]);

            if ((i == 0 || i == 4) && validMod.isEmptyOrNull(dom.val())) {
                showErrorTip(dom.data("pos"), dom.attr("placeholder"));
                isError = true;
                break;
            }

            if (dom.val() == "0") {
                showErrorTip(dom.data("pos"), dom.data("errmsg"));
                isError = true;
                break;
            }
        }

        return !isError;
    }

    /**
     * 群用户注册验证
     *   群大小(数字)
     *   群号
     *   群名称
     *   群简介
     */
    function fn_groupCheck() {
        return true;
    }

    function showErrorTip(errorPos, errorMsg) {
        window.scrollTo(0, 0);
        $("#mainMask").css("display", "block");
        $("#bubbleLayer").addClass("bubbleLayer-show").css("top", errorPos);
        $("#bubbleLayerWrap .error-tt p").text(errorMsg);
    }

    function registerParams() {
        var type = $("ul.ui-tab li.active").first().data("type");
        var typeCode = 1;
        switch (type) {
            case "p":
                typeCode = 1;
                break;
            case "c":
                typeCode = 2;
                break;
            case "g":
                typeCode = 3;
                break;
        }


        var params = {};
        params.type = typeCode;

        var li = $("#form").find("[data-type=" + type + "], [data-type='pcg']");
        var inputs = li.find("input, select, textarea");

        inputs.each(function () {
            var name;
            var value;
            if ((value = $(this).val()) != "" && (name = $(this).attr("name")) != null) {
                params[name] = value;
            }
        });

        return params;
    }

    function fn_checkSubmit() {

        var isError = false,

            type = $(".ui-tab li.active").data("type"),

            doms = $("#input_nickName,#input_email,#input_psw,#input_psw2");

        for (var i = 0, l = doms.length, dom, val, psw; i < l; i++) {
            val = (dom = $(doms[i])).val();
            if (i == 2) {
                psw = val;
            }

            if (validMod.isEmptyOrNull(val)) {
                showErrorTip(dom.data("pos"), dom.attr("placeholder"));
                isError = true;
                break;
            }

            if (i == 1 && !validMod.isEmail(val)) {
                showErrorTip(dom.data("pos"), dom.data("errmsg"));
                isError = true;
                break;
            }

            if (i == 3 && val !== psw) {
                showErrorTip(dom.data("pos"), dom.data("errmsg"));
                isError = true;
                break;
            }
        }

        val = (dom = $("#description")).val();
        if (val && val.length > 1000) {
            if(type=="g")
                showErrorTip(760, dom.data("errmsg"));
            else
                showErrorTip(1050, dom.data("errmsg"));
            isError = !!1;
        }


        if (isError)
            return false;


        switch (type) {
            case "c":
                isError = !fn_companyCheck();
                break;
            case "g":
                isError = !fn_groupCheck();
                break;
        }

        return !isError;
    }

    function showMessage (message) {
        $("#messageModalLabel").empty().append(message);
        $("#messageModal").modal('toggle');
    }

    function fn_submitForm() {

        var params = registerParams();
        //console.log(params);

        if (fn_checkSubmit()) {

            var dialog = dialogMod(regCallDg)
                .showModal()
                .content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');

            ajaxRegister(params, function () {
                dialog.content(regOkHtml).title("注册成功");
                dialog.close();

                location.href = "emailSent.html";
            }, function (result) {
                var message = result.message;
                if (validMod.isEmptyOrNull(message)) {
                    message = "对不起,服务端异常,您目前无法注册!";
                }
                showMessage(message);

                //$("#mainMask").css("display", "block");
                //$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 160);
                //$("#bubbleLayerWrap .error-tt p").text("对不起,服务端异常,您目前无法注册!");

                dialog.content(regFailHtml)
                    .title("注册失败!")
                    .buttons([{
                        cmd: "cancel",
                        text: "关闭"
                    }]);
                dialog.close();
            }, function () {
                showMessage("对不起,服务端异常,您目前无法注册!");

                //$("#mainMask").css("display", "block");
                //$("#bubbleLayer").addClass("bubbleLayer-show").css("top", 160);
                //$("#bubbleLayerWrap .error-tt p").text("对不起,服务端异常,您目前无法注册!");

                dialog.content(regErrorHtml)
                    .title("注册异常")
                    .buttons([{
                        cmd: "cancel",
                        text: "关闭"
                    }]);
                dialog.close();
            });
        }
    }

    /**
     * 初始化元素数据绑定
     * @return {[type]} [description]
     */
    function fn_initEvent() {
        $("ul.ui-tab li").click(function () {

            $("input[type=text],input[type=password]").val('');

            $("#mainMask").css("display", "none");
            $("#bubbleLayer").removeClass("bubbleLayer-show");
            $("#bubbleLayerWrap .error-tt p").text("");

            var type = $(this).removeClass("active").addClass('active').siblings().removeClass('active').end().data("type");

            var currentLi ;

            var hideLi;

            switch (type) {
                case "p":
                    hideLi = $("ul.ui-items li[data-type=c], ul.ui-items li[data-type=g]");
                    currentLi = $("ul.ui-items li[data-type=p],ul.ui-items li[data-type=pc]");
                    $("#input_type").val(1);
                    $(".c-tips").hide();
                    break;
                case "c":
                    hideLi = $("ul.ui-items li[data-type=p], ul.ui-items li[data-type=g]");
                    currentLi = $("ul.ui-items li[data-type=c],ul.ui-items li[data-type=pc]");
                    $(".c-tips").show();
                    $("#input_type").val(2);
                    break;
                case "g":
                    hideLi = $("ul.ui-items li[data-type=p], ul.ui-items li[data-type=c], ul.ui-items li[data-type=pc]");
                    currentLi = $("ul.ui-items li[data-type=g]");
                    $("#input_type").val(3);
                    $(".c-tips").hide();
                    break;
            }

            currentLi.siblings().css("display", "");
            hideLi.css("display", "none");

        });

        $("#chk_xieyi").click(function () {

            var status = parseInt($("#status").val());

            $("#btn_confirm")[status == 1 ? "attr" : "removeAttr"]("disabled", "disabled");

            $("#status").val(1 - status);
        })

        //$("#btn_confirm").click(fn_submitForm);

        $("#serviceAgreement").click(function () {
            //$("#myModal").modal('toggle');
            //window.open("serviceAgreement.html");
            //dialogMod(serviceDg).showModal().iframe("serviceAgreement.html");
        });

        $("input, select, textarea").on("focus", function() {
            $("#mainMask").css("display", "none");
            $("#bubbleLayer").removeClass("bubbleLayer-show");
        });

        //日期选择事件注册
        $("#calendar_ctrl").click(function (event) {
            event.stopPropagation();
            __showCalendar(event, "birthday");
        });

        //预览图片
        $("#logoImage").on("change",function(e){
            var file = e.target.files || e.dataTransfer.files;
            if(file && file[0]){
                var reader = new FileReader();
                reader.onload = function() {
                    $("#image").attr("src",this.result).show();
                }
                reader.readAsDataURL(file[0]);
            } else {
                $("#image").attr("src","").hide();
            }
        });

        $("#form").ajaxForm();
        $('#form').submit(function () {
            var dialog = dialogMod(regCallDg)
                .showModal()
                .content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');

            $("#btn_confirm").attr("disabled","");
            var options = {
                url: baseUrl + "/user/register",
                type: 'post',
                dataType: null,
                clearForm: false,
                beforeSubmit:function(data){
                   if(!fn_checkSubmit()){
                       $("#btn_confirm").removeAttr("disabled");
                       dialog.close();
                       return false;
                   }
                },
                success: function (data, textStatus, jqXHR) {
                    var result = data.success;
                    if (result == 1) {
                        location.href = "emailSent.html";
                    }else{
                        alert(data.message);
                    }
                },
                complete:function(){
                    $("#btn_confirm").removeAttr("disabled");
                    dialog.close();
                },
                fail:function(){
                    alert("注册异常!");

                }
            };
            $(this).ajaxSubmit(options);
            return false;
        });

    }

    exports.load = function () {
        fn_initEvent();
    };
});
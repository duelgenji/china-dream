/**
 *  修改信息页涉及到的js逻辑操作：
 *  验证表单提交:更具不同用户的注册内容进行验证
 *  提交表单
 *    提交成功后的显示 弹出对话框-显示当前用户的流水号
 *    提交失败后的警告 弹出对话框-提示错误
 */

define("modifyUserInfo-logic", ["user-repos", "jquery", "pure-dialog", "pure-validator"], function (require, exports) {

    var
        $ = require("jquery"),

        userRepos = require("user-repos"),

        dialogMod = require("pure-dialog"),

        validMod = require("pure-validator"),

        regOkHtml = '<div class="ui-regTip"><span><img class="ui-tipOkIcon"/>恭喜您,注册成功!</span><p><a href="login.html">现在去登录</a><a href="index.html">返回平台首页</a></p></div>',

        regFailHtml = '<div class="ui-regTip"><span><img class="ui-tipFailIcon"/>对不起,注册失败!</span></div>',

        regErrorHtml = '<div class="ui-regTip"><span><img class="ui-tipErrorIcon"/>对不起,注册出现异常!</span></div>',

        type = 'p',

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

    function modifyUserInfoParams() {
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
            var name = $(this).attr("name");
            var value = $(this).val();
            params[name] = value;
        });

        return params;
    }

    function fn_checkSubmit() {

        var isError = false,

            type = $(".ui-tab li.active").data("type"),

            //doms = $("#input_nickName,#input_email,#input_psw,#input_psw2");
            doms = $("#input_nickName");

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

        var params = modifyUserInfoParams();
        //console.log(params);

        if (fn_checkSubmit()) {

            var dialog = dialogMod(regCallDg)
                .showModal()
                .content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');

            ajaxregister(params, function () {
                dialog.content(regOkHtml).title("注册成功");
                dialog.close();

                location.href = "index.html";
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

    function showOriginInfo (type,params) {
        $("#form").css("display", "");
        var li = $("ul.ui-items li[data-type=" + type + "], ul.ui-items li[data-type='pcg']");

        var inputs = li.find("input, select, textarea");

        inputs.each(function() {
            var value = params[$(this).attr('name')];
            console.log(value);
            if (!validMod.isEmptyOrNull(value)) {
                console.log(value);
                $(this).val(value);
            }
        })

        li.css("display", "");
    }

    /**
     * 初始化元素数据绑定
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        showOriginInfo("g",{realNameOpen:"1",email:"123123@123.com"});

        $("#chk_xieyi").click(function () {

            var status = parseInt($("#status").val());

            $("#btn_confirm")[status == 1 ? "attr" : "removeAttr"]("disabled", "disabled");

            $("#status").val(1 - status);
        })

        $("#btn_confirm").click(fn_submitForm);

        $("#serviceAgreement").click(function () {
            //$("#myModal").modal('toggle');
            //window.open("serviceAgreement.html");
            //dialogMod(serviceDg).showModal().iframe("serviceAgreement.html");
        });

        //日期选择事件注册
        $("#calendar_ctrl").click(function (event) {
            event.stopPropagation();
            __showCalendar(event, "birthday");
        });
    }

    exports.load = function () {
        fn_initEvent();
    };
});
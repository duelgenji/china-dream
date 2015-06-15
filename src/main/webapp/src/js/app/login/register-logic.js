/**
 * 注册页涉及到的js逻辑操作：
 *  不同注册用户的显示切换
 *  验证表单提交:更具不同用户的注册内容进行验证
 *  提交表单
 *    提交成功后的显示 弹出对话框-显示当前用户的流水号
 *    提交失败后的警告 弹出对话框-提示错误
 */

define("register-logic", ["user-repos", "jquery", "pure-dialog", "pure-validator"], function(require, exports) {

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
      isError,
      doms = $("#input_companyName,#industryCode,#provinceCode,#ownershipCode");

    for (var i = 0, l = doms.length, dom; i < l; i++) {
      dom = $(doms[i]);

      if (i == 0 && validMod.isEmptyOrNull(dom.val())) {
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

  }

  function showErrorTip(errorPos, errorMsg) {
    window.scrollTo(0, 0);
    $("#mainMask").css("display", "block");
    $("#bubbleLayer").addClass("bubbleLayer-show").css("top", errorPos);
    $("#bubbleLayerWrap .error-tt p").text(errorMsg);
  }

  function fn_checkSubmit() {

    var isError, errorMsg, errorPosition,

      type = $(".ui-nav li.active").data("type"),

      doms = $("#input_nickName,#input_email,#input_psw,#input_psws");

    for (var i = 0, l = doms.length, dom, val; i < l; i++) {
      val = (dom = $(doms[i])).val();
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
      if (i == 3 && val !== dom.val()) {
        showErrorTip(dom.data("pos"), dom.data("errmsg"));
        isError = true;
        break;
      }
    }

    if (isError)
      return false;

    switch (type) {
      case "1":
        isError = fn_companyCheck();
        break;
      case "2":
        isError = fn_groupCheck();
        break;
    }

    return !isError;
  }

  function fn_submitForm() {

    if (fn_checkSubmit()) {

      var dialog = dialogMod(regCallDg)
        .showModal()
        .content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');

      userRepos.registerp($("#form").serialize(), function() {
        dialog.content(regOkHtml).title("注册成功");
      }, function() {
        dialog.content(regFailHtml)
          .title("注册失败!")
          .buttons([{
            cmd: "cancel",
            text: "关闭"
          }]);
      }, function() {
        dialog.content(regErrorHtml)
          .title("注册异常")
          .buttons([{
            cmd: "cancel",
            text: "关闭"
          }]);
      });
    }
  }

  /**
   * 初始化元素数据绑定
   * @return {[type]} [description]
   */
  function fn_initEvent() {
    $("ul.ui-tab li").click(function() {

      $("input[type=text],input[type=password]").val('');

      $("#mainMask").css("display", "none");
      $("#bubbleLayer").removeClass("bubbleLayer-show");
      $("#bubbleLayerWrap .error-tt p").text("");

      var type = $(this).removeClass("active").addClass('active').siblings().removeClass('active').end().data("type");

      var currentLi = $("ul.ui-items li[data-type=" + type + "]").css("display", "");

      switch (type) {
        case "p":
          currentLi.siblings("li[data-type=c]").css("display", "none")
            .end().siblings('li[data-type=g]').css("display", "none");
          break;
        case "c":
          currentLi.siblings("li[data-type=p]").css("display", "none")
            .end().siblings('li[data-type=g]').css("display", "none");
          break;
        case "g":
          currentLi.siblings("li[data-type=p]").css("display", "none")
            .end().siblings('li[data-type=c]').css("display", "none");
          break;
      }
    });

    $("#chk_xieyi").click(function() {

      var status = parseInt($("#status").val());

      $("#btn_confirm")[status == 1 ? "attr" : "removeAttr"]("disabled", "disabled");

      $("#status").val(1 - status);
    })

    $("#btn_confirm").click(fn_submitForm);

    $("#serviceAgreement").click(function() {
      dialogMod(serviceDg).showModal().iframe("serviceAgreement.html");
    });

    //日期选择事件注册
    $("#calendar_ctrl").click(function(event) {
      event.stopPropagation();
      __showCalendar(event, "birthday");
    });
  }

  exports.load = function() {
    fn_initEvent();
  };
});
/**
 * 询价发布的逻辑js
 * @param  {[type]} require        [description]
 * @param  {[type]} exports		   [description]
 * @return {[type]}                [description]
 */
define("create-logic", ["jquery", "main", "inquiry-repos", "pure-validator", "pure-dialog"], function(require, exports) {
	var $ = require("jquery"),

		mainMod = require("main"),

		validMod = require("pure-validator"),

		dialogMod = require("pure-dialog"),

		publishOkHtml = '<div class="ui-publishTip"><span><img class="ui-tipOkIcon"/>恭喜您,标号:<span id="span_biaohao"></span>的询标发布成功!</span><p><a href="index.html" target="_self">返回平台首页</a></p></div>',

		publishFailHtml = '<div class="ui-publishTip"><span><img class="ui-tipFailIcon"/>对不起,询价发布失败!</span></div>',

		publishErrorHtml = '<div class="ui-publishTip"><span><img class="ui-tipErrorIcon"/>对不起,询价发布出现异常!</span></div>',

		currentPosition = 1,

		publishCallDg = {
			id: "publishCallDg",
			isHideTopClose: true,
			width: 400,
			height: 300,
			title: "询价发布提示",
			buttons: []
		};

	inquiryRepos = require("inquiry-repos");

	function fn_submitForm() {

		var dialog = dialogMod(publishCallDg)
			.showModal()
			.content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');

		inquiryRepos.publish($("#inquiryForm").serialize(), function(data) {
			dialog.content(publishOkHtml).title("询价发布成功");
			$("#span_biaohao").text(data);
		}, function() {
			dialog.content(publishFailHtml)
				.title("询价发布失败!")
				.buttons([{
					cmd: "cancel",
					text: "关闭"
				}]);
		}, function() {
			dialog.content(publishErrorHtml)
				.title("询价发布异常")
				.buttons([{
					cmd: "cancel",
					text: "关闭"
				}]);
		});
	}

	function showErrorTip(errorPos, errorMsg) {
		window.scrollTo(0, 0);
		$("#mainMask").css("display", "block");
		$("#bubbleLayer").addClass("bubbleLayer-show").css("top", errorPos);
		$("#bubbleLayerWrap .error-tt p").text(errorMsg);
	}

	function valid_step1() {
		var isError, doms = $("#title,#industryCode,#provinceCode,#purchaseCloseDate,#totalPrice,#inquiryMode");

		for (var i = 0, l = doms.length, dom, val; i < l; i++) {
			val = (dom = $(doms[i])).val();
			if (validMod.isEmptyOrNull(val)) {
				showErrorTip(dom.data("pos"), dom.attr("placeholder"));
				isError = !!1;
				break;
			}
			if (i == 4 && !validMod.isPositive(val)) {
				showErrorTip(dom.data("pos"), dom.data("errmsg"));
				isError = !!1;
				break;
			}
		}

		val = (dom = $("#remarks")).val();
		if (val && val.length > 500) {
			showErrorTip(dom.data("pos"), dom.data("errmsg"));
			isError = !!1;
		}

		return !isError;
	}

	function valid_step2() {
		var isError, doms = $("#contactName,#contactMail,#contactTelephone");

		for (var i = 0, l = doms.length, dom, val; i < l; i++) {
			val = (dom = $(doms[i])).val();
			if (validMod.isEmptyOrNull(val)) {
				showErrorTip(dom.data("pos"), dom.attr("placeholder"));
				isError = true;
				break;
			}
			if ((i == 1 && !validMod.isEmail(val)) || (i == 2 && !validMod.isMobile(val))) {
				showErrorTip(dom.data("pos"), dom.data("errmsg"));
				isError = true;
				break;
			}
		}
		return !isError;
	}

	function evt_toPre(e) {
		var $this = $(this),
			$next = $("#btn_next"),
			preId = parseInt($this.data("pre"));

		currentPosition = preId;

		if (currentPosition == 1 || currentPosition == 4) {
			$this.css("display", "none");
		}
		if (currentPosition < 4) {
			$next.text("下一步");
		}

		$("#div_step" + (preId + 1)).css("display", "none");
		$("#div_step" + preId).css("display", "");
		$this.data("pre", preId - 1).data("current", preId);
		$next.data("next", preId + 1).data("current", preId);

		$("#div_steps li:eq(" + (currentPosition - 1) + ")").removeClass("active").addClass("active").siblings('li').removeClass("active");


		$("#bubbleLayer").removeClass("bubbleLayer-show");
	}

	function evt_toNext(e) {
		var $this = $(this),
			$pre = $("#btn_pre"),
			nextId = parseInt($this.data("next"));

		if (nextId == 2) {
			if (!valid_step1()) {
				return;
			}
			$("#hd_purchaseCloseDate").val($("#purchaseCloseDate").val() + " "+$("#purchaseCloseDateHour").val());
		}

		if (nextId == 3) {
			if (!valid_step2()) {
				return;
			}
		}

		currentPosition = nextId;

		if (currentPosition > 1) {
			$pre.css("display", "");
		}

		if (nextId == 5) {
			fn_submitForm();
			return;
		}
		if (nextId == 4) {
			$("#div_step4 ul span").each(function(i, span) {
				var target = $("#" + span.getAttribute("field")),
					from = span.getAttribute("data-from");

				if (i == 0) {
					span.innerText = $("input[name='inquiry.round'][type=radio]:checked").attr("title");
					return;
				}

				span.innerText = from == "selectedText" ? (from == "attrTitle" ? target.attr("title") : target.find("option:selected").text()) : target.val();
			});

			$this.text("确认提交");
		}
		if (nextId == 3) {}

		$("#div_step" + (nextId - 1)).css("display", "none");
		$("#div_step" + nextId).css("display", "");
		$this.data("next", nextId + 1).data("current", nextId);
		$pre.data("pre", nextId - 1).data("current", nextId);

		$("#div_steps li:eq(" + (currentPosition - 1) + ")").removeClass("active").addClass("active").siblings('li').removeClass("active");

		$("#bubbleLayer").removeClass("bubbleLayer-show");
	}

	function fn_init() {
		$("#chk_allowShare").click(function() {

			var status = parseInt($("#status").val());

			$("#btn_confirm")[status == 1 ? "attr" : "removeAttr"]("disabled", "disabled");

			$("#status").val(1 - status);
		});

		$("#userName").val(mainMod.loginInfo.nickname);
		$("#hd_userName").val(mainMod.loginInfo.name);
		$("#hd_inqueryUserName").val(mainMod.loginInfo.name);

		$("#btn_next").click(evt_toNext);
		$("#btn_pre").click(evt_toPre);

		//日期选择事件注册
		$("#calendar_ctrl").click(function(event) {
			event.stopPropagation();
			__showCalendar(event, "purchaseCloseDate");
		});
	}

	exports.load = function() {
		fn_init();
	};
});
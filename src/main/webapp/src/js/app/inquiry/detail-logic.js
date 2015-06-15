define("detail-logic", ["detail-config", "main", "inquiry-repos", "bid-repos", "collect-repos", "jquery", "pure-grid", "pure-dialog", "pure-url"], function(require, exports) {

	var
		$ = require("jquery"),

		configMod = require("detail-config"),

		gridMod = require("pure-grid"),

		dialogMod,

		urlMod = require("pure-url"),

		inquiryRepos = require("inquiry-repos"),

		bidRepos = require("bid-repos"),

		collectRepos = require("collect-repos"),

		mainMod = require("main"),

		currentGrid1,

		currentGrid2,

		inquiryID;

	/**
	 * 查看全部轮数窗口
	 * @return {[type]} [description]
	 */
	function showAllRoundDg() {

	}

	/**
	 * 弹出正式出价窗口
	 * @return {[type]} [description]
	 */
	function showBidDg() {

	}


	function renderRound(val, ri, objVal) {
		return (objVal.title = "第" + val + "轮");
	}

	function renderAttachments(val, ri, objVal) {
		var urls = (vals[1] || "").split(','),
			names = (vals[0] || "").split(',');
		var html = [];
		for (var i = 0, l = urls.length; i < l; i++) {
			html.push('<a href="' + urls[i] + '" target="_blank">' + names[i] + '</a>');
		}
		return html.join('');
	}

	function renderAttachments() {
		return '<a href="#">技术文件.doc</a>';
	}

	function renderOpt(val, ri, objVal) {
		return '<button data-cmd="seeAll" data-ri="' + ri + '">全部轮数</button>';
	}

	function fn_initGrid(config) {
		var grid = gridMod(config || configMod.gridConfig);
		grid.pubSub()
			.override("renderRound", renderRound)
			.override("renderAttachments", renderAttachments)
			.override("renderOpt", renderOpt)
			.override("grid.databound", function() {
				$("button[data-cmd=seeAll]").click(function() {
					var that = $(this),
						ri = that.data("ri");

				});
			});
		return grid;
	}

	/**
	 * 获取询价标详情
	 * @return {[type]} [description]
	 */
	function fn_getDetail() {
		inquiryRepos.getDetail(currentQueryObj.inquiryID, call_detailOk, call_detailFail, call_detailFail);
	}

	/**
	 * 辅助方法-映射询标类型
	 * @return {[type]} [description]
	 */
	function util_mapInquiryMode(val) {
		var reslt = "";
		switch (val) {
			case 1:
				reslt = "全明询价";
				break;
			case 2:
				reslt = "明询价";
				break;
			case 3:
				reslt = "半明询价";
				break;
			case 4:
				reslt = "半暗询价";
				break;
			case 5:
				reslt = "暗询价";
				break;
			case 6:
				reslt = "全暗询价";
				break;
		}
		return reslt;
	}

	function util_mapBidLimit(val) {
		var reslt = "";
		switch (val) {
			case 0:
				reslt = "不限";
				break;
			case 1:
				reslt = "仅接受个人出价";
				break;
			case 2:
				reslt = "仅接受企业出价";
				break;
		}
		return reslt;
	}

	function call_detailOk(data) {
		var span, inquiryMode,

			dataSource = data.datas,

			username = dataSource[data.index["userName"]];

		$("#detailbiaohao").text(dataSource[data.index["biaohao"]]);

		$("span.ui-value").each(function(i, span) {
			span = $(span);
			var col = span.data("col"),
				val = dataSource[data.index[col]],
				isOpen = dataSource[data.index[col + "Open"]];
			if (span.data("access") && isOpen == "0" && username != mainMod.loginInfo.name) {
				span.text("").addClass("ui-noOpen");
			} else {
				if (col == "inquiryMode") {
					val = util_mapInquiryMode(inquiryMode = val);
				}
				if (username == mainMod.loginInfo.name) {
					val = val + "(" + (isOpen == "1" ? "公开" : (isOpen == "2" ? "授权后公开" : "")) + ")";
				}
				span.text(val).attr("title", val);
			}
		});

		/**
		 * 如果当前询价是全明询价,那么才显示对手出价
		 * @return {[type]} [description]
		 */
		if (inquiryMode == 1) {
			fn_getOpponentBidList();
		} else {
			$("#data2").html('<h3>抱歉,此标非全明询价标,无法查看对手出价!</h3>');
		}

		/**
		 * 不是本人的询价 则需要显示我的出价
		 * 	反之则显示
		 * @param  {String} currentQueryObj.isSelf !             [description]
		 * @return {[type]}                        [description]
		 */

		if (!mainMod.loginInfo.name) {
			$("#btn_biddingApply").click(function() {
				alert("对不起,此部分功能需要登录后才能操作,请先登录!");
			});
			$("#btn_addCollect").click(function() {
				alert("对不起,此部分功能需要登录后才能操作,请先登录!");
			});
			return;
		}

		if (dataSource[data.index["userName"]] != mainMod.loginInfo.name) {
			fn_getMyBidList();
			$("#div_self").css("display", "none");
			$("#div_other").css("display", "");

			$("#btn_biddingApply").click(function() {
				var that = $(this);
				if (that.attr("status") == "working") {
					return;
				}
				var cmd = that.attr("status", "working").data("cmd");

				if (cmd == "apply") {
					/*
					判断当前询价标的出价限制的用户类型 是否与当前登录的用户类型一致
				 */
					var bidLimit = dataSource[data.index["biddingLimit"]];
					if (bidLimit != 0 && bidLimit != loginInfo.type) {
						alert("对不起,当前询标的出价限制为" + util_mapBidLimit(bidLimit) + "!");
						return;
					}
					bidRepos.sendBidRequest(mainMod.loginInfo.name, currentQueryObj.inquiryID, function() {
						alert("申请发送成功,请耐心等待对方的回复!");
						that.data("cmd", "addBid").attr("status", "done").text("正式出价");
					}, function() {
						alert("申请发送失败!");
					}, function() {
						alert("申请发送异常!");
					});
				} else if (cmd == "addBid") {

				}
			});

			$("#btn_addCollect").click(function() {
				var that = $(this);
				if (that.attr("status") == "working") {
					return;
				}
				var cmd = that.attr("status", "working").data("cmd");

				if (cmd == "add") {
					collectRepos.add(currentQueryObj.userName, 4, currentQueryObj.inquiryID, function(data) {
						that.data("cmd", "cancel").attr("status", "done").text("取消收藏");
					}, function() {
						alert("添加收藏失败!");
						that.removeAttr("status");
					}, function() {
						alert("添加收藏异常!");
						that.removeAttr("status");
					});
				} else {
					collectRepos.cancel(currentQueryObj.userName, currentQueryObj.inquiryID, function(data) {
						that.data("cmd", "add").attr("status", "done").text("添加收藏");
					}, function() {
						alert("取消收藏失败!");
						that.removeAttr("status");
					}, function() {
						alert("取消收藏异常!");
						that.removeAttr("status");
					})
				}
			});
		} else {
			$("#div_self").css("display", "");
			$("#div_other").css("display", "none");

			$("#btn_sucending").click(function() {

				!dialogMod && (dialogMod = require("pure-dialog"));

				dialogMod(configMod.dialogConfig)
					.showModal()
					.height(200)
					.content("<div>出价人员</div>")
					.onCmd("ok", function() {
						inquiryRepos.pass(mainMod.loginInfo.name, currentQueryObj.inquiryID, function() {

						}, function() {

						}, function() {

						});
					});
			});

			$("#btn_failending").click(function() {

				if (confirm("是否确认将此标执行流标操作?")) {

					inquiryRepos.flow(mainMod.loginInfo.name, currentQueryObj.inquiryID, function() {

					}, function() {

					}, function() {

					});
				}
			});
		}
	}

	function call_detailFail() {}

	/**
	 * 获取我的出价
	 * @return {[type]} [description]
	 */
	function fn_getMyBidList() {
		$("#myBid").css("display", "");

		currentGrid1 = fn_initGrid();

		bidRepos.getListOfMy(currentQueryObj.userName, currentQueryObj.inquiryID, currentQueryObj.pageno, currentQueryObj.pagesize, call_mybidok, call_mybidfail, call_mybidfail);
	}

	function call_mybidok(data) {
		currentGrid1.reBind(data);
	}

	function call_mybidfail() {

		currentGrid1.reBind(testData);
	}

	/**
	 * 获取对手出价
	 * @return {[type]} [description]
	 */
	function fn_getOpponentBidList() {

		currentGrid2 = fn_initGrid(configMod.gridConfig2);

		bidRepos.getListOfOther(currentQueryObj.userName, currentQueryObj.inquiryID, currentQueryObj.pageno, currentQueryObj.pagesize, call_opponentbid, call_opponentbidfail, call_opponentbidfail);
	}

	function call_opponentbid(data) {
		currentGrid2.reBind(testData);
	}

	function call_opponentbidfail() {

		currentGrid2.reBind(testData);
	}

	var testData = [{
		round: 1,
		userName: "上海公司",
		province: "上海",
		createTime: "2015/2/12 10:00",
		money: 2000,
		attachmentUrls: "www.baidu.com",
		id: 2
	}, {
		round: 1,
		userName: "上海公司",
		province: "上海",
		createTime: "2015/2/12 10:00",
		money: 2000,
		attachmentUrls: "www.baidu.com",
		id: 2
	}, {
		round: 1,
		userName: "上海公司",
		province: "上海",
		createTime: "2015/2/12 10:00",
		money: 2000,
		attachmentUrls: "www.baidu.com",
		id: 2
	}];

	var currentQueryObj = {
		pageno: 1,
		pagesize: 8,
	};

	exports.load = function() {
		var arr = urlMod.getSearch("key,isself");
		currentQueryObj.inquiryID = arr[0];
		currentQueryObj.isSelf = arr[1];
		fn_getDetail();
	};
});
define("userroom-logic", ["userroom-config", "jquery", "user-repos", "pure-grid", "pure-dialog", "pure-url"], function(require, exports) {

	var
		$ = require("jquery"),

		configMod = require("userroom-config"),

		gridMod,

		dialogMod = require("pure-dialog"),

		userRepos = require("user-repos"),

		currentGrid,

		/**
		 * 当前已选择的筛选项的键值对
		 * @type {Object}
		 */
		currentQuery = {
			pageno: 1,
			pagesize: 15
		};

	/**
	 * 用户logoUrl
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderLogo(vals, ri, objval) {
		objval.title = "";
		return '<a name="cmd-seeUserDetail" data-i="' + ri + '"><img class="ui-inner-logo" src="' + vals + '" alt="用户logo"/></a>';
	}

	/**
	 * 用户名称
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderUser(vals, ri, objval) {
		return '<a class="ui-name" name="cmd-seeUserDetail" data-i="' + ri + '">' + (objval.title = vals) + '</a>';
	}

	/**
	 * 出价成功率
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderBidSuccessRate(vals, ri, objval) {
		return !vals ? "--" : (vals + "%");
	}

	/**
	 * 绑定数据
	 * @return {[type]} [description]
	 */
	function fn_bind() {

		fn_initGridMod();

		dialogMod.mask.show();

		userRepos.getList(currentQuery, function(data) {
			currentGrid.bindData(data);
			setTimeout(dialogMod.mask.hide, 500);
		}, call_fail, call_fail);
	}

	function call_fail() {

		var testData = [{
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 5,
			countOfBid: 9
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 6,
			countOfBid: 10
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 7,
			countOfBid: 21
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 1,
			countOfBid: 20
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 2,
			countOfBid: 12
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 3,
			countOfBid: 13
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 4,
			countOfBid: 14
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 5,
			countOfBid: 15
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 8,
			countOfBid: 11
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 7,
			countOfBid: 13
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 2,
			countOfBid: 12
		}, {
			logoUrl: "../../../image/1.png",
			userName: "上海诣新信息技术股份有限公司",
			industry: "互联网+",
			province: "上海",
			percent: 3,
			countOfBid: 20
		}];

		currentGrid.bindData(testData);

		setTimeout(dialogMod.mask.hide, 500);
	}

	/**
	 * 判断是否当前已经登录用户
	 * @return {[type]} [description]
	 */
	function util_isLogin() {

	}

	/**
	 * 初始化数据grid组件
	 * @return {[type]} [description]
	 */
	function fn_initGridMod() {
		/**
		 * 初始化DataGrid，并订阅相关的消息与事件
		 */
		!gridMod
			&& (gridMod = require("pure-grid")) &&
			(
				(currentGrid = gridMod(configMod.gridConfig))
				.pubSub()
				.override("renderLogo", renderLogo)
				.override("renderUser", renderUser)
				.override("renderBidSuccessRate", renderBidSuccessRate)
				.override("grid.databound", function() {
					$("a[name=cmd-seeUserDetail]").click(function() {
						if (util_isLogin()) {
							location.href = "inquiry/detail.html?key=" + currentGrid.dataSource[$(this).data("i")]["id"];
						} else {
							alert("您好,如需查看此用户信息,请您先登录!");
						}
					});
				})
			);
	}

	/**
	 * 执行搜索
	 * @return {[type]} [description]
	 */
	function evt_doSearch() {
		if (currentQuery.keyword = $("#mq").val().trim()) {
			fn_bind();
		}
	}

	/**
	 * 初始化页面内的元素的事件操作
	 * @return {[type]} [description]
	 */
	function fn_initEvent() {

		$("#btn_search").click(evt_doSearch);

		/*
		更多
		 */
		$("a[name=a_More]").click(function() {
			var state = parseInt($(this).data("state"));
			$(this)
				.data("state", 1 - state)
				.text(state == 1 ? "更多" : "收起")
				.parent()
				.siblings('ul').attr("class", state == 1 ? "ui-normal" : "ui-expand");
		});
		/*
		筛选项
		 */
		$(".ui-attr li").click(function(e) {

			var that = $(this),
				state = that.data("state"),
				a = $(this).data("state", 1).children("a"),
				val = a.data("value");

			if (state != "1") {

				$("#link").append('<span class="link"><a data-value="' + val + '">' + a.text() + '</a><span data-index="' + that.attr("index") + '" data-target="' + $(this).parent().attr("id") + '" class="ui-close" title="去除当前筛选项">x</span>')

				var query = that.parent().data("query");

				currentQuery[query] = (currentQuery[query] || "") + val + ",";
			}

			setTimeout(fn_bind, 300);
		});
		/*
		删除筛选项
		 */
		$("#link").click(function(e) {
			var dom;
			if (e.target.className == "ui-close") {
				dom = $(e.target);
				var index = dom.data("index"),
					target = dom.data("target");
				var query = $("#" + target).children("li").eq(parseInt(index)).attr("state", 0).end().end().data("query");
				currentQuery[query] = currentQuery[query].replace(dom.siblings('a').data("value") + ",", "");

				dom.parent().remove();
			}
		});
	}

	/**
	 * 模块装载的调用
	 * @return {[type]} [description]
	 */
	exports.load = function() {

		fn_initEvent();

		fn_bind();
	};
})
define("list-logic", ["jquery", "inquiry-repos", "list-config", "pure-grid", "pure-dialog"], function(require, exports) {

	var
		$ = require("jquery"),

		ajaxMod = require("ajax"),

		configMod = require("list-config"),

		gridMod,

		dialogMod = require("pure-dialog"),

		urlMod = require("pure-url"),

		currentGrid,

		inquiryRepos = require("inquiry-repos"),
		/**
		 * 当前已选择的筛选项的键值对
		 * @type {Object}
		 */
		currentQuery = {
			pageno: 1,
			pagesize: 15
		};
	/**
	 * 询价logo的数据显示回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderInquiryLogo(vals, ri, objval) {
		objval.title = "询价标logo";
		return '<a href="inquiryDetail.html?key=' + vals[1] + '"><img class="ui-inner-logo" src="' + vals[0] + '" alt="询价标logo"/></a>';
	}

	/**
	 * 人气列数据显示的回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderPopularity(vals, ri, objval) {

		objval.title = vals[0] + "\n" + vals[1] + "\n" + (vals[2]||"");

		var html =
			'<p class="ui-inquiryTitle">' + '<a href="inquiryDetail.html?key=' + vals[3] + '" name="detail">' + vals[0] + '</a>' + '</p>' + '<p class="ui-biaohao">' + vals[1] + '</p>' + '<p class="ui-userName">' + vals[2] + '</p>';

		return html;
	}

	/**
	 * 标的列数据显示的回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderBiaoDi(vals, ri, objval) {
		objval.title = "标的(元):" + vals[0];
		return "￥" + vals[0] + '元<br /><span style="color:red;">赞:' + (vals[1]||0) + "</span>";
	}

	/**
	 * 询价成功率的数据显示回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderBidSuccessRate(vals, ri, objval) {
		objval.title = "成功率：" + vals[1];

		return vals[0] + "<br />询价成功率：" + (vals[1] ? (vals[1].indexOf("%") > -1 ? vals[1] : (vals[1] + "%")) : "0%");
	}

	/**
	 * 询价总数的数据显示回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderBidCount(vals, ri, objval) {
		objval.title = "总数：" + vals[1];
		return vals[0] + "<br />询价总数：" + (vals[1]||0);
	}

	/**
	 * 截止时间的数据显示回调
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderEndDate(vals, ri, objval) {

		if (vals[3] == "1") {
			return (objval.title = "中标方:" + (vals[5] == "0" ? vals[4] : "*******"));
		}

		objval.title = "第" + vals[1] + "轮";

		return vals[0] + "<br />第" + vals[1] + "轮<br />" + vals[2];
	}

	/**
	 * 绑定数据
	 * @return {[type]} [description]
	 */
	function fn_bind() {
		fn_initGrid();

		dialogMod.mask.show();

		inquiryRepos.getList(currentQuery, function(data) {
			currentGrid.reBind(data);
			setTimeout(function() {
				dialogMod.mask.hide();
			}, 800);
		}, call_fail, call_fail);
	}

	/**
	 * 数据绑定失败后的回调
	 * @return {[type]} [description]
	 */
	function call_fail() {

		currentGrid.reBind(null);

		setTimeout(function() {
			dialogMod.mask.hide();
		}, 300);
	}

	/**
	 * 初始化grid组件
	 * @return {[type]} [description]
	 */
	function fn_initGrid() {
		/**
		 * 初始化DataGrid，并订阅相关的消息与事件
		 */
		if (!gridMod) {
			gridMod = require("pure-grid");
			currentGrid = gridMod(configMod.gridConfig);

			currentGrid.pubSub()
				.override("renderInquiryLogo", renderInquiryLogo)
				.override("renderPopularity", renderPopularity)
				.override("renderBiaoDi", renderBiaoDi)
				.override("renderBidCount", renderBidCount)
				.override("renderBidSuccessRate", renderBidSuccessRate)
				.override("renderEndDate", renderEndDate);
			gridMod.basePubSub
				.override("grid.getDataLength", function(data) {
					return data && data.datas ? data.datas.length : 0;
				})
				.override("grid.getRowColsValues", function(data, ri, cols) {
					var colArr = cols.split(','),
						dataSource = data.datas;
					for (var i = colArr.length; i;) {
						colArr[(i -= 1)] = dataSource[ri][data.index[colArr[i]]];
					}
					return colArr.length > 0 ? colArr : colArr[0]
				});
		}
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
	 * 初始化元素事件绑定
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
		更多选项
		 */
		$("#a_extraMore").click(function() {
			var text = $(this).text();
			if (text == "精简选项") {
				$("div.ui-morePropAttr").css("display", "none");
				$(this).text("更多选项");
			} else {
				$("div.ui-morePropAttr").css("display", "");
				$(this).text("精简选项");
			}
		});
		/*
		筛选项
		 */
		$(".ui-attr li").click(function(e) {

			var that = $(this),
				state = that.data("state"),
				a = $(this).data("state", 1).children("a"),
				val = a.data("value");

			if (state != 1) {

				$("#link").append('<span class="link"><a data-value="' + val + '">' + a.text() + '</a><span data-index="' + that.attr("index") + '" data-target="' + $(this).parent().attr("id") + '" class="ui-close" title="去除当前筛选项">x</span>')

				var query = that.parent().data("query");

				currentQuery[query] = (currentQuery[query] || "") + val + ",";

				setTimeout(fn_bind, 200);
			}
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

				var query = $("#" + target).children("li").eq(parseInt(index)).data("state", 0).end().end().data("query");

				currentQuery[query] = currentQuery[query].replace(dom.siblings('a').data("value") + ",", "");

				dom.parent().remove();

				setTimeout(fn_bind, 200);
			}
		});
	}

	exports.load = function() {

		fn_initEvent();

		fn_bind();
	};
})
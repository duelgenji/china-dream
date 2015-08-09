define("userroom-logic", ["userroom-config", "jquery", "user-repos", "pure-grid", "pure-dialog", "pure-url"], function(require, exports) {

	var
		$ = require("jquery"),

		configMod = require("userroom-config"),

		gridMod,

		dialogMod = require("pure-dialog"),

		userRepos = require("user-repos"),

		currentGrid,

		mainMod = require("main"),

		/**
		 * 当前已选择的筛选项的键值对
		 * @type {Object}
		 */
		size = 20,

		currentQuery = {
			page: 0,
			size: size
		},
		currentPage = 0,
		maxPage = 0;

	function setPage (targetPage) {
		currentQuery.page = targetPage - 1;
	}

	function getCurrentPage () {
		return currentQuery.page + 1;
	}

	function setMaxPage (page) {
		maxPage = page;
		$(".maxPage").text(maxPage);
	}

	function getMaxPage () {
		return maxPage;
	}

	/**
	 * 用户logoUrl
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderLogo(vals, ri, objval) {
		objval.title = "";
		var url = vals[0];
		var type = vals[1];
		if(url==""){
			switch(type){
				case 1:
					url = "../image/pic/personalDefaultLogo.jpg";
					break;
				case 2:
					url = "../image/pic/companyDefaultLogo.jpg";
					break;
				case 3:
					url = "../image/pic/groupDefaultLogo.jpg";
					break;
				default :
					break;
			}
		}
		return '<a name="cmd-seeUserDetail" data-i="' + ri + '"><img class="ui-inner-logo" src="' + url + '" alt="用户logo"/></a>';
	}

	/**
	 * 用户名称
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {[type]}        [description]
	 */
	function renderUser(vals, ri, objval) {
		var vip="";
		if(vals[1]=="1"){
			vip += '<p style="font-weight: bolder;color: orange;"> 已认证</p>'
		}if(vals[2]=="1"){
			vip += '<p style="font-weight: bolder;color: orange;"> 测试用户</p>'
		}

		return '<a class="ui-name" name="cmd-seeUserDetail" data-i="' + ri + '">' + (objval.title = vals[0]) + vip + '</a>';
	}

	/**
	 * 出价成功率
	 * @param  {[type]} vals   [description]
	 * @param  {[type]} ri     [description]
	 * @param  {[type]} objval [description]
	 * @return {string}        [description]
	 */
	function renderBidSuccessRate(vals, ri, objval) {
		return vals;
	}

	/**
	 * 绑定数据
	 * @return {[type]} [description]
	 */
	function fn_bind() {

		fn_initGridMod();

		var params = {};
		params.type=0;

		for (var key in currentQuery) {
			var value = currentQuery[key],
				lenth = value.length;

			if (lenth > 0 && value.charAt(lenth - 1) == ',') {
				value = value.substr(0,lenth - 1);
			}

			params[key] = value;

		}

		if(currentQuery.keyword){
			params.key=currentQuery.keyword
		}

		console.log(JSON.stringify(params));

		dialogMod.mask.show();

		ajaxRetriveUserRoomList(params,function(data) {
			var testData = data.data,
				count = data.count;
			$(".changePage").val(getCurrentPage());
			if (count%size == 0) {
				setMaxPage(parseInt(data.count/size));
			} else {
				setMaxPage(parseInt(data.count/size) + 1);
			}

			currentPage = currentQuery.page;
			if (currentPage == 0) {
				$(".btn1").addClass("disable");
				$(".btn2").addClass("disable");
			} else {
				$(".btn1").removeClass("disable");
				$(".btn2").removeClass("disable");
			}

			if (currentPage == getMaxPage() - 1) {
				$(".btn3").addClass("disable");
				$(".btn4").addClass("disable");
			} else {
				$(".btn3").removeClass("disable");
				$(".btn4").removeClass("disable");
			}
			//console.log(data);

			currentGrid.reBind(data.data);
			setTimeout(dialogMod.mask.hide, 500);
		}, call_fail, call_fail);
	}

	function call_fail() {

		var testData = [];

		currentGrid.bindData(testData);

		setTimeout(dialogMod.mask.hide, 500);
	}

	/**
	 * 判断是否当前已经登录用户
	 * @return {[type]} [description]
	 */
	function util_isLogin() {
		return mainMod.isLogin();
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
							location.href = "userDetail.html?key=" + currentGrid.dataSource[$(this).data("i")]["userId"];
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
		currentQuery.keyword = $("#mq").val().trim();
		fn_bind();

	}

	/**
	 * 初始化页面内的元素的事件操作
	 * @return {[type]} [description]
	 */
	function fn_initEvent() {

		$(".pagination").append('<a href="javascript:void(0);" class="btn1">首页</a> <a href="javascript:void(0);" class="btn2">上一页</a> <a  href="javascript:void(0);" class="btn3">下一页</a> <a href="javascript:void(0);" class="btn4">尾页</a> 转到 <input class="changePage" type="text" size="1" maxlength="4"/>/<span class="maxPage"></span> 页 <a href="javascript:void(0);" class="btn5">GO</a>')

		$(".btn1").click(function firstPage(){    // 首页
			if ($(this).hasClass("disable")) return;
			setPage(1);
			fn_bind();
		});
		$(".btn2").click(function frontPage(){    // 上一页
			if ($(this).hasClass("disable")) return;
			setPage(getCurrentPage() - 1);
			fn_bind();
		});
		$(".btn3").click(function nextPage(){    // 下一页
			if ($(this).hasClass("disable")) return;
			setPage(getCurrentPage() + 1);
			fn_bind();
		});
		$(".btn4").click(function lastPage(){    // 尾页
			if ($(this).hasClass("disable")) return;
			setPage(getMaxPage());
			fn_bind();
		});
		$(".btn5").click(function changePage(){    // 转页
			curPage = $(this).siblings("input").val() * 1;
			if (!/^[1-9]\d*$/.test(curPage)) {
				alert("请输入正整数");
				return ;
			}
			if (curPage > getMaxPage()) {
				alert("超出数据页面");
				return ;
			}
			setPage(curPage);
			fn_bind();
		});
		$(".changePage").on("input", function(e) {
			if (isNaN(String.fromCharCode(e.keyCode))) {
				$(this).val($(this).val().replace(/\D/gi, ""));
			}
		});


		$("#btn_search").click(evt_doSearch);


		$('#mq').keydown(function(e){
			if(e.keyCode==13){
				evt_doSearch();
			}
		});
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
				var query = $("#" + target).children("li").eq(parseInt(index)).data("state", 0).end().end().data("query");
				currentQuery[query] = currentQuery[query].replace(dom.siblings('a').data("value") + ",", "");



				dom.parent().remove();

				setTimeout(fn_bind, 200);
			}
		});


		//排序项目
		$(document).on("click",".ui-grid-contentDiv-span",function(){
			if($(this).attr("active")=="1"){
				if($(this).find("span").html()=="↓"){
					$(this).find("span").html("↑");
					currentQuery.direction= 1;
				}else{
					$(this).find("span").html("↓");
					currentQuery.direction= 0;
				}
				fn_bind();
				return;
			}

			if($(this).closest("th").attr("ci")=="4"){
				currentQuery.type = 1;
			}else if($(this).closest("th").attr("ci")=="5"){
				currentQuery.type = 2;
			}else{
				return;
			}
			$(".ui-grid-contentDiv-span").removeAttr("active").removeClass("red").find("span").remove();
			$(this).attr("active","1").addClass("red").append("<span>↓</span>");
			fn_bind();
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
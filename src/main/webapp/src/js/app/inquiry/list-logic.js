define("list-logic", ["jquery", "inquiry-repos", "list-config", "pure-grid", "pure-dialog"], function (require, exports) {

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

        objval.title = vals[0] + "\n" + vals[1] + "\n" + (vals[2] || "");


        var css = "",vip="",status="";
        if(vals[4]=="1"){
            css = " orange ";
            status = "(测试标)";
        }else if(vals[5]=="2"){
            css = " blue ";
            status = "(流标)";
        }else if(vals[5]=="0"){
            css = " green ";
            status = "(进行中)";
        }else if(vals[5]=="1"){
            status = "(成功)";
        }

        if(vals[6]=="1"){
            vip = '<span style="font-weight: bolder" class="orange" > 已认证</span>'
        }

        var html =
            '<p class="ui-inquiryTitle">' + '<a href="inquiryDetail.html?key=' + vals[3] + '" name="detail">' + vals[0] + '</a>'
            + '</p>' + '<p class="ui-biaohao '+css+'">' + vals[1] + status + '</p>'
            + '<p class="ui-userName">' + vals[2] + vip + '</p>';

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
        var css = vals[2]?"red":"";
        var goodsTitle = vals[2]?"取消赞":"赞";
        objval.title = "标的(元):" + vals[0];
        return "￥" + vals[0] + '元<br /><span><span class="goods '+css+' glyphicon glyphicon-thumbs-up" title="'+goodsTitle+'" data-id="'+vals[3]+'"></span>:' + (vals[1] || 0) + "</span>";
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
        return vals[0] + "<br />询价总数：" + (vals[1] || 0);
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
            return (objval.title = "中标方:" + (vals[4] ? vals[4] : "*******")+"<p>中标价格:"+ vals[5]+"</p>");
        }else if (vals[3] == "2") {
            return "流标";
        }

        objval.title = "第" + vals[1] + "轮";

        var mode = vals[2];
        var modeDetail = "";
        switch (mode) {
            case "全明询价":
                modeDetail = "当询价方勾选全明询价选项后，即所有在本网站注册用户均可在该询价截止时间前看见所有出价方之方案，报价，清单及所有文件。";
                break;
            case "明询价":
                modeDetail = "当询价方勾选明询价选项后，即所有被授权出价方均可在该询价截止时间前看见对手之方案，报价，清单及所有文件。并可多次修改方案，出价。";
                break;
            case "半明询价":
                modeDetail = "当询价方勾选半明询价选项后，即所有出价方可在该询价截止时间前仅看见对手之报价。";
                break;
            case "半暗询价":
                modeDetail = "当询价方勾选暗询价选项后，仅询价方可看见出价方之方案，报价，清单及所有文件。";
                break;
            case "暗询价":
                modeDetail = "当询价方勾选半暗询价选项后，仅询价方可看见出价方之方案，及所有不涉及价格之文件，而清单及报价均储存于本网站服务器上，待截止时间后1小时—72小时(用户自行定义)之内发送至询价方。";
                break;
            case "全暗询价":
                modeDetail = "当询价方勾选全暗询价选项后，所有出价方可之方案，报价，清单及所有文件均储存于本网站服务器上，待截止时间后1小时—72小时(用户自行定义)之内询价方可以看到出价方的报价和文件。";
                break;
        }

        return vals[0] + "<br />第" + vals[1] + "轮<br /><span title=" + modeDetail + ">" + mode + "</span>";
    }

    /**
     * 绑定数据
     * @return {[type]} [description]
     */
    function fn_bind() {
        fn_initGrid();

        var params = {};

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

        ajaxRetriveIndexList(params, function (data) {
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



            currentGrid.reBind(testData);

            setTimeout(function () {
                dialogMod.mask.hide();
            }, 800);
        }, call_fail, call_fail);
    }

    /**
     * 数据绑定失败后的回调
     * @return {[type]} [description]
     */
    function call_fail() {
        currentQuery.page = currentPage;

        //currentGrid.reBind(null);
        alert(arguments[0].message);

        setTimeout(function () {
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
                .override("grid.getDataLength", function (data) {
                    return data ? data.length : 0;
                    //return data && data.datas ? data.datas.length : 0;
                })
                .override("grid.getRowColsValues", function (data, ri, cols) {
                    var colArr = cols.split(','),
                        dataSource = data;
                    for (var i = colArr.length; i;) {
                        colArr[(i -= 1)] = dataSource[ri][colArr[i]];

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
        setPage(1);
        currentQuery.keyword = $("#mq").val().trim();
        fn_bind();

    }

    /**
     * 初始化元素事件绑定
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        $("head").append("<style>a.disable {color: lightgray;cursor: default;} </style>");

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

        //点赞
        $(document).on("click",".goods",function(){
            var params = {};
            params.inquiryId = $(this).attr("data-id");
            var _this = $(this).parent();
            ajaxInquiryGoods(params, function (data) {
                var css = data.isGoods?"red":"";
                var goodsTitle = data.isGoods?"取消赞":"赞";
                _this.empty().append('<span class="goods '+css+' glyphicon glyphicon-thumbs-up" title="'+goodsTitle+'" data-id="'+params.inquiryId+'"></span>:' + (data.goods || 0));
            }, call_fail, call_fail);

        });

        /*
         更多
         */
        $("a[name=a_More]").click(function () {
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
        $("#a_extraMore").click(function () {
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
        $(".ui-attr li").click(function (e) {

            var that = $(this),
                state = that.data("state"),
                a = $(this).data("state", 1).children("a"),
                val = a.data("value");

            if (state != 1) {

                $("#link").append('<span class="link"><a data-value="' + val + '">' + a.text() + '</a><span data-index="' + that.attr("index") + '" data-target="' + $(this).parent().attr("id") + '" class="ui-close" title="去除当前筛选项">x</span>')

                //隐藏价格筛选
                if (that.parent().attr("data-query") == "bidprice") {
                    that.closest(".ui-targetAttr").hide();
                    var min = a.data("minPrice"),
                        max = a.data("maxPrice");
                    if (min != "") {
                        currentQuery.minPrice = min;
                    }
                    if (max != "") {
                        currentQuery.maxPrice = max;
                    }

                } else {
                    var query = that.parent().data("query");

                    currentQuery[query] = (currentQuery[query] || "") + val + ",";
                }

                setTimeout(fn_bind, 200);
            }
        });

        $("#customPrice").click(function() {
            var min = $("#minPrice").val(),
                max = $("#maxPrice").val(),
                text = "";

            if (min == "" && max == "") {
                return;
            }

            if (max == "") {
                text = min + "以上";
            } else if (min == "") {
                text = max + "以下";
            } else if (min == max) {
                text = min;
            } else {
                if (min > max) {
                    var temp = max;
                    max = min;
                    min = temp;
                    $("#minPrice").val(min);
                    $("#maxPrice").val(max);
                }
                text = min + "-" + max;
            }

            $("#link").append('<span class="link"><a>' + text + '</a><span data-index="' + $(this).attr("index") + '" data-target="' + $(this).parent().attr("id") + '" class="ui-close" title="去除当前筛选项">x</span>')

            //隐藏价格筛选
            $(this).closest(".ui-targetAttr").hide();

            if (min != "") {
                currentQuery.minPrice = min;
            }
            if (max != "") {
                currentQuery.maxPrice = max;
            }

            setTimeout(fn_bind, 200);

        });

        /*
         删除筛选项
         */
        $("#link").click(function (e) {
            var dom;
            if (e.target.className == "ui-close") {
                dom = $(e.target);
                var index = dom.data("index"),
                    target = dom.data("target");

                if (target == "ul_biaoDi") {
                    $("#ul_biaoDi").closest(".ui-targetAttr").show();
                    delete currentQuery.minPrice;
                    delete currentQuery.maxPrice;
                } else {
                    var query = $("#" + target).children("li").eq(parseInt(index)).data("state", 0).end().end().data("query");

                    currentQuery[query] = currentQuery[query].replace(dom.siblings('a').data("value") + ",", "");
                }

                dom.parent().remove();

                setTimeout(fn_bind, 200);
            }
        });
    }

    exports.load = function () {

        fn_initEvent();

        fn_bind();
    };
})
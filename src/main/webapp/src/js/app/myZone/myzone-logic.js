define("myzone-logic", ["main", "myzone-config", "jquery", "user-repos", "bid-repos", "inquiry-repos", "collect-repos", "lettermsg-repos", "pure-grid", "pure-dialog"], function (require, exports) {

    var
        $ = require("jquery"),

        configMod = require("myzone-config"),

        gridMod,

        dialogMod = require("pure-dialog"),

        userRepos = require("user-repos"),

        bidRepos = require("bid-repos"),

        inquiryRepos = require("inquiry-repos"),

        collectRepos = require("collect-repos"),

        letterMsgRepos = require("lettermsg-repos"),

        mainMod = require("main"),

        currentGrid,

        currentGrid2,

        currentQueryObj = {
            page: 0
        },

        dataSource;

    function renderInquiryNo(vals, ri, objval) {
        return '<a style="text-decoration: underline;" href="inquiryDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderInquiryTitle(vals, ri, objval) {
        return '<a style="text-decoration: underline;" href="inquiryDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderInquiryMode(vals, ri, objval) {
        return (objval.title = util_mapInquiryMode(vals));
    }

    function renderState(vals, ri, objval) {
        var result;
        switch (vals) {
            case 1:
                result = "成功";
                break;
            case 2:
                result = "流标";
                break;
            default:
                result = "进行中";
                break;
        }

        return (objval.title = result);
    }

    function renderOptOfBid(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><a href="inquiryDetail.html?id=' + vals + '&isself=1" title="查看">查看</a></div>';
    }

    function renderOptOfInquiry(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><a href="inquiryDetail.html?id=' + vals + '&isself=1" title="查看">查看</a></div>';
    }

    function renderOptOfCollect(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><button type="button" data-cmd="cancelCollect" data-ri="' + ri + '">取消收藏</button></div>';
    }

    function renderOptOfMessage(vals, ri, objval) {
        objval.title = "";
        if ($("#select_msg").val() == 1) {

            switch (dataSource[ri].messageStatus) {
                case 0:
                    return '<div class="ui-optDiv"><button type="button" data-cmd="pass" data-ri="' + ri + '">同意</button><button type="button" data-cmd="refuse" data-ri="' + ri + '">拒绝</button></div>';
                    break;
                case 1:
                    return "已授权";
                    break;
                case 2:
                    return "已拒绝";
                    break;
            }
        } else {
            switch (dataSource[ri].messageStatus) {
                case 0:
                    return "待授权";
                    break;
                case 1:
                    return "已授权";
                    break;
                case 2:
                    return "已拒绝";
                    break;
            }
            return "";
        }
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

    function fn_getMyInfo() {
        ajaxRetrieveUserDetail(loadUserInfo().id ,call_myInfoOk, call_myInfoFail, call_myInfoFail);
        //userRepos.getDetail(mainMod.loginInfo.name, call_myInfoOk, call_myInfoFail, call_myInfoFail);
    }

    function call_myInfoOk(data) {
        console.log(data);

        $("span.ui-val").each(function (i, span) {
            span = $(span);
            var col = span.data("col"),
                val = data[col];
            span.text(val).attr("title", val);
        });
    }

    function call_myInfoFail() {
        alert("请求失败");
    }

    /**
     * 绑定数据
     * @return {[type]} [description]
     */
    function fn_getMyBidList(gridCfg) {

        fn_initGrid(gridCfg);

        dialogMod.mask.show();

        bidRepos.getListOfMy(mainMod.loginInfo.name, currentQueryObj.inquiryID, currentQueryObj.pageno, currentQueryObj.pagesize, (function (g) {
            return function (data) {
                g.reBind(data);
            };
        }(currentGrid)), (function (g) {
            return function () {
                call_fail(g);
            }
        }(currentGrid)), (function (g) {
            return function () {
                call_fail(g);
            }
        }(currentGrid)));

        setTimeout(dialogMod.mask.hide, 500);
    }

    function fn_getMyInquiryList(gridCfg) {

        fn_initGrid(gridCfg);

        dialogMod.mask.show();

        inquiryRepos.getListOfMy(mainMod.loginInfo.name, $("#select_inquiry").val(), currentQueryObj.pageno, currentQueryObj.pagesize, (function (g) {
            return function (data) {
                g.reBind(data);
            };
        }(currentGrid)), (function (g) {
            return function () {
                call_fail(g);
            }
        }(currentGrid)), (function (g) {
            return function () {
                call_fail(g);
            }
        }(currentGrid)));

        setTimeout(dialogMod.mask.hide, 800);
    }

    function fn_getMyCollectList() {

        dialogMod.mask.show();
        var params = {};
        //params.page = 0;

        ajaxRetrieveCollectionList(params, function (data) {
            dataSource = data.data;
            currentGrid.reBind(dataSource);
            currentGrid2.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });

        setTimeout(dialogMod.mask.hide, 800);
    }

    function fn_getMyLettermsgList() {
        dialogMod.mask.show();

        var params = {};
        params.page = 0;
        params.type = $("#select_msg").val();

        ajaxRetrieveMessageList(params, function (data) {
            dataSource = data.data;
            console.log(dataSource);
            currentGrid.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });

        //letterMsgRepos.getListOfMy(mainMod.loginInfo.name, $("#select_msg").val(), currentQueryObj.pageno, currentQueryObj.pagesize, (function (g) {
        //    return function (data) {
        //        g.reBind(data);
        //    };
        //}(currentGrid)), (function (g) {
        //    return function () {
        //        call_fail(g);
        //    }
        //}(currentGrid)), (function (g) {
        //    return function () {
        //        call_fail(g);
        //    }
        //}(currentGrid)));

        setTimeout(dialogMod.mask.hide(), 800);
    }

    function call_fail(g) {
        var testData = [{
            biaohao: "沪A201502050001",
            state: 0,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 1,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 0,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 1,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 1,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 1,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 2,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 6,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 1,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 5,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 1,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 4,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 1,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 2,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }, {
            biaohao: "沪A201502050001",
            state: 2,
            title: "企业员工工作服采购",
            round: 1,
            inquiryMode: 3,
            province: "上海",
            userName: "上海无忧公司",
            industry: "服装",
            biaoDi: 5,
            purchaseCloseDate: "2015/3/31 10:00",
            money: 5000,
            id: 2
        }];
        g.reBind(testData);
    }

    function fn_initGrid(gridCfg) {
        /**
         * 初始化DataGrid，并订阅相关的消息与事件
         */
        !gridMod && (gridMod = require("pure-grid"));

        (currentGrid = gridMod(gridCfg || configMod.gridCfg1))
            .pubSub()
            .override("renderInquiryNo", renderInquiryNo)
            .override("renderInquiryTitle", renderInquiryTitle)
            .override("renderInquiryMode", renderInquiryMode)
            .override("renderState", renderState)
            .override("renderOptOfBid", renderOptOfBid)
            .override("renderOptOfInquiry", renderOptOfInquiry)
            .override("renderOptOfCollect", renderOptOfCollect)
            .override("renderOptOfPersonCollect", renderOptOfCollect)
            .override("renderOptOfMessage", renderOptOfMessage)
            .override("grid.databound", function () {
            });
    }

    function fn_initGrid2(gridCfg) {
        /**
         * 初始化DataGrid，并订阅相关的消息与事件
         */
        !gridMod && (gridMod = require("pure-grid"));

        (currentGrid2 = gridMod(gridCfg || configMod.gridCfg1))
            .pubSub()
            .override("renderInquiryNo", renderInquiryNo)
            .override("renderInquiryTitle", renderInquiryTitle)
            .override("renderInquiryMode", renderInquiryMode)
            .override("renderState", renderState)
            .override("renderOptOfBid", renderOptOfBid)
            .override("renderOptOfInquiry", renderOptOfInquiry)
            .override("renderOptOfCollect", renderOptOfCollect)
            .override("renderOptOfPersonCollect", renderOptOfCollect)
            .override("renderOptOfMessage", renderOptOfMessage)
            .override("grid.databound", function () {
            });
    }

    /**
     * 初始化相关HTMLElement的事件操作
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        fn_getMyInfo();

        $("#select_msg").on("change", function () {
            fn_initGrid(configMod["gridCfg4"]);
            fn_getMyLettermsgList();
        });

        $("#myTab li").click(function () {
            var gridNo = $(this).siblings().removeClass("active")
                .end().removeClass("active").addClass("active")
                .data("grid");

            $("#contentPanel").children().css("display", "none")
                .eq(parseInt(gridNo) - 1).css("display", "");

            var mod = $(this).data("mod");
            switch (mod) {
                case "bid":
                    fn_getMyBidList(configMod["gridCfg" + gridNo]);
                    break;
                case "inquiry":
                    fn_getMyInquiryList(configMod["gridCfg" + gridNo]);
                    break;
                case "collect":
                    fn_initGrid(configMod["gridCfg" + gridNo]);
                    fn_initGrid2(configMod["gridCfg" + 5]);
                    fn_getMyCollectList();
                    break;
                case "message":
                    fn_initGrid(configMod["gridCfg" + gridNo]);
                    fn_getMyLettermsgList();
                    break;
            }
        })[0].click();

        $(document).on("click", ".ui-optDiv button", function () {
            var that = $(this),
                cmd = that.data("cmd"),
                data = dataSource[that.data("ri")];

            if (cmd == "cancelCollect") {
                var params = {};
                params.collectionId = data.collectionId;
                ajaxCancelCollection(params, function () {
                    alert("取消收藏成功");
                    fn_initGrid(configMod["gridCfg3"]);
                    fn_getMyCollectList();
                }, function (result) {
                    alert(result.message);
                }, function () {
                    alert("请求失败");
                });

            } else if (cmd == "pass") {
                var params = {};
                params.status = 1;
                params.messageId = data.messageId;
                console.log(data.messageId);
                ajaxModifyMessageStatus(params, function () {
                    that.parent().empty();
                }, function (result) {
                    alert(result.message);
                }, function () {
                    alert("请求失败");
                })
            } else if (cmd == "refuse") {
                var params = {};
                params.status = 2;
                params.messageId = data.messageId

                ajaxModifyMessageStatus(params, function () {
                    that.parent().empty();
                }, function (result) {
                    alert(result.message);
                }, function () {
                    alert("请求失败");
                })
            }
        });
    };

    exports.load = function () {
        fn_initEvent();
    };
});
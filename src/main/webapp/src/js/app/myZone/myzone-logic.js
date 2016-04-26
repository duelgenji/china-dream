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
        objval.title= vals[0];
        return '<a title="" style="text-decoration: underline;" href="inquiryDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderUserId(vals, ri, objval) {
        objval.title= "";
        return '<a title="" style="text-decoration: underline;" href="userDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderNickname(vals, ri, objval) {
        objval.title= vals[0];
        return '<a title="" style="text-decoration: underline;" href="userDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderInquiryTitle(vals, ri, objval) {
        objval.title= vals[0];
        return '<a style="text-decoration: underline;" title="'+vals[0]+'" href="inquiryDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
    }

    function renderInquiryMode(vals, ri, objval) {
        return (objval.title = util_mapInquiryMode(vals[0]));
    }

    function renderProject(vals, ri, objval) {
        objval.title= vals[0];
        if(vals[1]){
            return '<a style="text-decoration: underline;" title="'+vals[0]+'" href="inquiryDetail.html?key=' + vals[1] + '">' + vals[0] + '</a>';
        }else{
            return vals[2]
        }
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
        return '<div class="ui-optDiv"><a href="inquiryDetail.html?key=' + vals + '" title="查看">查看</a></div>';
    }

    function renderOptOfInquiry(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><a href="inquiryDetail.html?key=' + vals + '" title="查看">查看</a></div>';
    }

    function renderOptOfCollect(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><button type="button" data-cmd="cancelCollect" data-ri="' + ri + '">取消收藏</button></div>';
    }

    function renderOptOfPersonCollect(vals, ri, objval) {
        objval.title = "";
        return '<div class="ui-optDiv"><button type="button" data-cmd="cancelCollectU" data-ri="' + ri + '">取消收藏</button></div>';
    }


    function renderAttachments(vals, ri, objVal) {
        //var urls = (vals[1] || "").split(','),
        //    names = (vals[0] || "").split(',');
        if(!vals){
            return "---";
        }
        var html = [];
        for (var i = 0, l = vals.length; i < l; i++) {
            var name = vals[i].remark == "" ? "附件" : vals[i].remark;
            html.push('<div style="float:left;overflow-x: hidden;max-width:'+ (120 / vals.length) +'px;"><a style="padding: 0 5px" href="' + vals[i].fileUrl + '" target="_blank">' + name + '</a></div>');
        }
        return html.join('');

    }

    function renderOptOfMessage(vals, ri, objval) {
        objval.title = "";
        if ($("#select_msg").val() == 0) {
            switch (dataSource[ri].messageStatus) {
                case 0:
                    return '<div class="ui-optDiv">待授权 <button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
                case 1:
                    return '<div class="ui-optDiv">已确认 <button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
                case 2:
                    return '<div class="ui-optDiv">已拒绝 <button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
            }
            return "";
        } else {
            switch (dataSource[ri].messageStatus) {
                case 0:
                    return '<div class="ui-optDiv"><button type="button" data-cmd="pass" data-ri="' + ri + '">同意</button><button type="button" data-cmd="refuse" data-ri="' + ri + '">拒绝</button><button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
                case 1:
                    return '<div class="ui-optDiv">已确认 <button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
                case 2:
                    return '<div class="ui-optDiv">已拒绝 <button type="button" data-cmd="check" data-ri="' + ri + '">查看</button></div>';
                    break;
            }

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

    function showCmdModal(title, body, submitFn) {
        $("#cmdModalTitle").text(title);
        $("#cmdModalBody").empty().append(body);
        $("#submitModal").unbind("click").bind("click", submitFn);
        $("#cmdModal").modal('show');
    }

    function dismissCmdModal() {
        $("#cmdModal").modal('hide');
    }

    function call_myInfoOk(data) {
        //console.log(data);
        showUserInfo(data);
        $("span.ui-val").each(function (i, span) {
            span = $(span);
            var col = span.data("col"),
                val = data[col];
            if(col=="companyWebsite"){
                var website=val;
                if(val && val.substring(0,4)!="http"){
                    website =  "http://"+val;
                }
                span.append('<a target="_blank" href="'+website+'">'+val+'</a>');
                return true;
            }
            span.text(val).attr("title", val);
        });
    }

    function showUserInfo(data){


        $("#ul_basic").hide();

        switch(data.userType){
            case 1:
                $("#ul_person").show();
                $("img#logo").attr("src","../image/pic/personalDefaultLogo.jpg");
                break;
            case 2:
                $("#ul_company").show();
                $("img#logo").attr("src","../image/pic/companyDefaultLogo.jpg");
                break;
            case 3:
                $("#ul_group").show();
                $("img#logo").attr("src","../image/pic/groupDefaultLogo.jpg");
                break;
            default :
                break;
        }

        if(data.logoUrl){
            $("img#logo").attr("src",data.logoUrl)
        }
        if(loadUserInfo().mailCount){
            $("li[data-mod='message'] a").html("站内信息"+loadUserInfo().mailCount);
        }


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
        //alert(document.body.scrollWidth);
        dialogMod.mask.show();

        var params = {};
        params.type= $("#select_bid").val();

        ajaxRetrieveMyQuotationList(params, function (data) {
            dataSource = data.data;
            currentGrid.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });


        setTimeout(dialogMod.mask.hide, 500);
    }

    function fn_getMyInquiryList() {

        dialogMod.mask.show();

        var params = {};
        if($("#select_inquiry").val()!=""){
            params.status = $("#select_inquiry").val();
        }
        //console.log(params);
        ajaxRetrieveMyInquiryList(params, function (data) {
            dataSource = data.data;
            currentGrid.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });


        setTimeout(dialogMod.mask.hide, 800);
    }

    function fn_getMyCollectList() {

        dialogMod.mask.show();
        var params = {};
        //params.page = 0;

        ajaxRetrieveCollectionList(params, function (data) {
            dataSource = data.data;
            currentGrid.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });

        ajaxRetrieveCollectionListU(params, function (data) {
            dataSource = data.data;
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
        params.size = 10000;
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

    function fn_getMyAccountList() {

        dialogMod.mask.show();

        var params = {};
        params.page = 0;
        params.size = 10000;
        ajaxRetrieveAccountLogList(params, function (data) {
            dataSource = data.data;
            console.log(dataSource);
            currentGrid.reBind(dataSource);
        }, function (result) {
            alert(result.message);
        }, function () {
            alert("请求失败");
        });

        dialogMod.mask.hide();
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
            .override("renderAttachments", renderAttachments)
            .override("renderNickname", renderNickname)
            .override("renderProject", renderProject)

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
            .override("renderUserId", renderUserId)
            .override("renderInquiryTitle", renderInquiryTitle)
            .override("renderInquiryMode", renderInquiryMode)
            .override("renderState", renderState)
            .override("renderOptOfBid", renderOptOfBid)
            .override("renderOptOfInquiry", renderOptOfInquiry)
            .override("renderOptOfCollect", renderOptOfCollect)
            .override("renderOptOfPersonCollect", renderOptOfPersonCollect)
            .override("renderOptOfMessage", renderOptOfMessage)
            .override("renderAttachments", renderAttachments)
            .override("grid.databound", function () {
            });
    }

    /**
     * 初始化相关HTMLElement的事件操作
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        fn_getMyInfo();


        $("#select_bid").on("change", function () {
            fn_initGrid(configMod["gridCfg1"]);
            fn_getMyBidList();
        });

        $("#select_inquiry").on("change", function () {
            fn_initGrid(configMod["gridCfg2"]);
            fn_getMyInquiryList();
        });

        $("#select_msg").on("change", function () {
            fn_initGrid(configMod["gridCfg4"]);
            fn_getMyLettermsgList();
        });



        $("#myTab li").click(function () {
            var gridNo = $(this).siblings().removeClass("active")
                .end().removeClass("active").addClass("active")
                .data("grid");

            if(gridNo == "6"){
                $("#contentPanel").children().css("display", "none").eq(4).css("display", "");
            }else{
                $("#contentPanel").children().css("display", "none")
                    .eq(parseInt(gridNo) - 1).css("display", "");
            }

            var mod = $(this).data("mod");
            switch (mod) {
                case "bid":
                    fn_getMyBidList(configMod["gridCfg" + gridNo]);
                    break;
                case "inquiry":
                    fn_initGrid(configMod["gridCfg" + gridNo]);
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
                case "account":
                    fn_initGrid(configMod["gridCfg" + gridNo]);
                    fn_getMyAccountList();
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
                    fn_initGrid(configMod["gridCfg" + 3]);
                    fn_initGrid2(configMod["gridCfg" + 5]);
                    fn_getMyCollectList();
                }, function (result) {
                    alert(result.message);
                }, function () {
                    alert("请求失败");
                });

            }else if (cmd == "cancelCollectU") {
                var params = {};
                params.collectionId = data.collectionId;
                ajaxCancelCollectionU(params, function () {
                    alert("取消收藏成功");
                    fn_initGrid(configMod["gridCfg" + 3]);
                    fn_initGrid2(configMod["gridCfg" + 5]);
                    fn_getMyCollectList();
                }, function (result) {
                    alert(result.message);
                }, function () {
                    alert("请求失败");
                });

            }else if (cmd == "pass") {
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
            }else if (cmd == "check") {

                var text = "申请留言";
                var content = data.content;
                if(data.type==1){
                    text = "合同金额"
                    content = data.content;
                }

                showCmdModal("查看详情", '<ul class="ui-items">' +
                '<li><label>申请日期:</label><div style="padding-left: 110px;">' +
                '<p class="modal-date">'+data.createTime+'</p></div>' +
                '<li><label>'+text+':</label><div style="padding-left: 110px;">' +
                '<p class="modal-date">'+content+'</p></div>' +
                '</ul>',null);
            }
        });
    };

    exports.load = function () {
        fn_initEvent();
    };
});
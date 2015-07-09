define("detail-logic", ["detail-config", "main", "inquiry-repos", "bid-repos", "collect-repos", "jquery", "pure-grid", "pure-dialog", "pure-url"], function (require, exports) {

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

    function renderAttachments(vals, ri, objVal) {
        //var urls = (vals[1] || "").split(','),
        //    names = (vals[0] || "").split(',');
        var html = [];
        for (var i = 0, l = vals.length; i < l; i++) {
            var name = vals[i].remark == "" ? "附件" : vals[i].remark;
            html.push('<div style="float:left;overflow-x: hidden;max-width:'+ (240 / vals.length) +'px;"><a style="padding: 0 5px" href="' + vals[i].fileUrl + '" target="_blank">' + name + '</a></div>');
        }
        return html.join('');

    }

    //function renderAttachments() {
    //    return '<a href="#">技术文件.doc</a>';
    //}

    function renderOpt(val, ri, objVal) {
        //return '<a type="button" data-cmd="seeAll" data-ri="' + ri + '">全部轮数</button>';
        return '<button type="button" data-cmd="seeAll" data-ri="' + ri + '">全部轮数</button>';
    }

    function fn_initGrid(config) {
        var grid = gridMod(config || configMod.gridConfig);

        grid.pubSub()
            .override("renderRound", renderRound)
            .override("renderAttachments", renderAttachments)
            .override("renderOpt", renderOpt)
            .override("grid.databound", function () {

                $("a[data-cmd=seeAll]").click(function () {
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
        console.log(JSON.stringify(currentQueryObj));
        ajaxRetriveDetail({inquiryId: currentQueryObj.inquiryId}, call_detailOk, call_detailFail, call_detailFail);
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

    var dataSource = {};

    function call_detailOk(data) {
        var span, inquiryMode;

        dataSource = data;

        $("#detailbiaohao").text(dataSource["inquiryNo"]);

        $("span.ui-value").each(function (i, span) {
            span = $(span);
            var col = span.data("col"),
                val = dataSource[col];
            if (col == "files") {
                var files = dataSource.fileList;
                for (var i = 0; i < files.length; i++) {
                    var name = files[i].remark == "" ? "附件" : files[i].remark;
                    span.append("<a href=" + files[i].fileUrl + ">" + name + "</a>")
                }
                span.append(val);
            } else {
                span.text(val).attr("title", val);
            }

        });

        console.log(dataSource);

        if (dataSource.applyStatus == 1) {
            $("#btn_biddingApply").attr("status", "working").text("已申请出价");
        } else if (dataSource.applyStatus == 2) {
            $("#btn_biddingApply").data("cmd", "addBid").text("正式出价");
        }

        if (dataSource.isCollection) {
            $("#btn_addCollect").data("cmd", "cancel").text("取消收藏");
            currentQueryObj.collectionId = dataSource.collectionId;
        }


        /**
         * 如果当前询价是全明询价,那么才显示对手出价
         * @return {[type]} [description]
         */
        fn_getOpponentBidList();

        /**
         * 不是本人的询价 则需要显示我的出价
         *    反之则显示
         * @param  {String} currentQueryObj.isSelf !             [description]
         * @return {[type]}                        [description]
         */

        var info = mainMod.loginInfo;

        if (!info) {
            $("#btn_biddingApply").click(function () {
                alert("对不起,此部分功能需要登录后才能操作,请先登录!");
            });
            $("#btn_addCollect").click(function () {
                alert("对不起,此部分功能需要登录后才能操作,请先登录!");
            });
            return;
        }

        if (!dataSource.isMe) {
            fn_getMyBidList();
            $("#div_self").css("display", "none");
            $("#div_other").css("display", "");

            $("#btn_biddingApply").click(function () {
                var canApply = true,
                    selfType = info.type,
                    userLimitType = dataSource.type,
                    limitReason = "";
                if (userLimitType == 1 && selfType == 2) {
                    canApply = false;
                    limitReason = '对不起,当前询标的出价限制为"仅接受个人/群出价"!';
                } else if (userLimitType == 2 && selfType != 2) {
                    canApply = false;
                    limitReason = '对不起,当前询标的出价限制为"仅接受企业出价"!';
                }

                if (!canApply) {
                    alert(limitReason);
                    return;
                }

                var that = $(this);
                if (that.attr("status") == "working") {
                    return;
                }

                var cmd = that.data("cmd");

                if (cmd == "apply") {

                    showCmdModal("申请出价", '<textarea id="modal-description" style="resize: none;width: 96%;height: 100px;font-size: 14px;padding: 2%;" placeholder="请输入内容，点击确定后将发送站内信给询价方，等待授权"></textarea>', function () {
                        var params = {};
                        params.inquiryId = currentQueryObj.inquiryId;
                        params.description = $("#modal-description").val();
                        console.log(JSON.stringify(params));

                        ajaxSendInquiryMessage(params, function () {
                            alert("申请发送成功,请耐心等待对方的回复!");

                            that.attr("status", "working").text("已申请出价");
                            //that.data("cmd", "addBid").attr("status", "done").text("正式出价");
                            dismissCmdModal();
                        }, function (result) {
                            alert(result.message);
                        }, function () {
                            alert("申请发送异常!");
                        })


                    });
                } else if (cmd == "addBid") {
                    showCmdModal("正式出价", '<form id="bidForm" method="post" enctype="multipart/form-data" accept-charset="utf-8">' +
                        '<input type="hidden" name="inquiryId" value="' + currentQueryObj.inquiryId + '"/>' +
                        '<ul class="ui-items"><li><label>出价金额:</label><input name="totalPrice" id="modal-money" type="text"/></li>' +
                        '<li><label>上传商务文件:</label><div style="padding-left: 110px;">' +
                        '<input class="modal-report" name="business1" type="file"/><span id="modal-delete-business1" class="delete-file">删除</span>' +
                        '<input class="modal-report" name="business2" type="file"/><span id="modal-delete-business2" class="delete-file">删除</span>' +
                        '<input class="modal-report" name="business3" type="file"/><span id="modal-delete-business3" class="delete-file">删除</span></div></li>' +
                        '<li><label>上传技术文件:</label><div style="padding-left: 110px;">' +
                        '<input class="modal-attachment" name="tech1" type="file"/><span id="modal-delete-tech1" class="delete-file">删除</span>' +
                        '<input class="modal-attachment" name="tech2" type="file"/><span id="modal-delete-tech2" class="delete-file">删除</span>' +
                        '<input class="modal-attachment" name="tech3" type="file"/><span id="modal-delete-tech3" class="delete-file">删除</span></div></li>' +
                        '<button id="modal-confirm" type="submit" style="display: none;">333</button></ul></form>', function () {
                        $("#modal-confirm").click();
                    });

                    $(".delete-file").hide().on("click", function () {
                        $(this).prev().val("");
                        $(this).hide();
                    });

                    var number = 0;

                    $("#bidForm").ajaxForm();
                    $("#bidForm").submit(function () {
                        var options = {
                            url: baseUrl + "/quotation/generateQuotation",
                            type: 'post',
                            beforeSubmit: function (data) {
                                console.log(data);
                            },
                            dataType: null,
                            clearForm: false,
                            success: function (data, textStatus, jqXHR) {
                                var result = data.success;
                                console.log(data);

                                dismissCmdModal();
                            },
                            fail: function(data){
                                console.log(data);
                            },
                            error:function(data) {
                                console.log(data);
                            }
                        };

                        $(this).ajaxSubmit(options);
                        return false;
                    }).on("change", ".modal-report", function (e) {
                        var file = e.target.files || e.dataTransfer.files;
                        if (file && file[0]) {
                            var doms = $(".modal-report"),
                                size = 0,
                                l = doms.length,
                                appendFile = true;

                            for (var i = 0; i < l; i++) {
                                var f = $(doms[i])[0].files[0];
                                if (f) {
                                    size += f.size;

                                    if (f.size > 5 * 1024 * 1024) {
                                        alert("上传商务文件大小请控制在5M以下");
                                        $(this).val("");
                                        return;
                                    }

                                    if (size > 30 * 1024 * 1024) {
                                        alert("上传商务文件总容量请控制在30M以下");
                                        $(this).val("");
                                        return;
                                    }
                                } else {
                                    appendFile = false;
                                }
                            }

                            $("#modal-delete-" + $(this).attr("name")).show();
                        } else {
                            $("#modal-delete-" + $(this).attr("name")).hide();
                        }
                    }).on("change", ".modal-attachment", function (e) {
                        var file = e.target.files || e.dataTransfer.files;
                        if (file && file[0]) {
                            var doms = $(".modal-attachment"),
                                size = 0,
                                l = doms.length,
                                appendFile = true;

                            for (var i = 0; i < l; i++) {
                                var f = $(doms[i])[0].files[0];
                                if (f) {
                                    size += f.size;

                                    if (f.size > 5 * 1024 * 1024) {
                                        alert("上传技术文件大小请控制在5M以下");
                                        $(this).val("");
                                        return;
                                    }

                                    if (size > 30 * 1024 * 1024) {
                                        alert("上传技术文件总容量请控制在30M以下");
                                        $(this).val("");
                                        return;
                                    }
                                } else {
                                    appendFile = false;
                                }
                            }

                            $("#modal-delete-" + $(this).attr("name")).show();

                        } else {
                            $("#modal-delete-" + $(this).attr("name")).hide();
                        }
                    });

                    $("#modal-money").on("change", function (e) {
                        if (isNaN(String.fromCharCode(e.keyCode))) {
                            $(this).val($(this).val().replace(/\D/gi, ""));
                        }
                    });
                }
            });

            $("#btn_addCollect").click(function () {
                var that = $(this);
                if (that.attr("status") == "working") {
                    return;
                }

                var cmd = that.attr("status", "working").data("cmd");

                if (cmd == "add") {
                    ajaxGenerateCollection({inquiryId: currentQueryObj.inquiryId}, function (data) {
                        alert("收藏成功");
                        currentQueryObj.collectionId = data.collectionId;
                        that.data("cmd", "cancel").attr("status", "done").text("取消收藏");
                    }, function () {
                        alert("添加收藏失败!");
                        that.removeAttr("status");
                    }, function () {
                        alert("添加收藏异常!");
                        that.removeAttr("status");
                    });
                } else {
                    console.log(currentQueryObj.collectionId);
                    ajaxCancelCollection({collectionId: currentQueryObj.collectionId}, function (data) {
                        alert("取消收藏成功");
                        that.data("cmd", "add").attr("status", "done").text("添加收藏");
                    }, function () {
                        alert("取消收藏失败!");
                        that.removeAttr("status");
                    }, function () {
                        alert("取消收藏异常!");
                        that.removeAttr("status");
                    });
                }
            });
        } else {
            $("#div_self").css("display", "");
            $("#div_other").css("display", "none");

            $("#btn_sucending").click(function () {

                showCmdModal("系统提示", '<span style="font-size: 14px;padding-bottom: 20px;">请选择合同公司：</span><div style="height: 10px;"></div><ul class="ui-items modal-user-list"></ul>', function () {
                    var params = {},
                        noCom = true;

                    $(".modal-successUser:checked").each(function () {
                        noCom = false;
                        if (params[$(this).attr("name")] == null) {
                            params[$(this).attr("name")] = $(this).val();
                        } else {
                            params[$(this).attr("name")] += "," + $(this).val();
                        }
                    });

                    if (noCom) {
                        alert("请选择合同公司");
                        return;
                    }

                    var radio = $(".modal-open:checked"),
                        remark = $(".modal-remark");
                    if (radio.length == 0) {
                        alert("请选择是否公开");
                        return;
                    }
                    params[radio.attr("name")] = radio.val();
                    params[remark.attr("name")] = remark.val();

                    if (confirm("是否确认将此标执行成功操作?")) {
                        console.log(JSON.stringify(params));
                    }

                });

                var testList = ["111", "222", "333", "444", "555"];
                console.log(JSON.stringify(testList));

                for (var i = 0; i < testList.length; i++) {
                    $(".modal-user-list").append('<li style="padding-bottom: 0;"><input class="modal-successUser" type="checkbox" name="successUser" id="' + i + '" value="' + i + '" /><label for="' + i + '" style="padding-left: 10px;float:none;font-weight:100;height">' + testList[i] + '</label></li>');
                }

                $(".modal-user-list").append('<li style="padding-bottom: 0;"><input class="modal-remark" type="text" name="remark" style="margin-left: 0;margin-right: 10px;"/><input class="modal-open" type="radio" name="open" id="open" value="0"/><label for="open" style="padding-left: 10px;float:none;font-weight:100;height">公开</label><input class="modal-open" type="radio" name="open" id="no-open" value="1"/><label for="no-open" style="padding-left: 10px;float:none;font-weight:100;height">不公开</label></li>')
            });

            $("#btn_failending").click(function () {


                showCmdModal("系统提示", '<span style="font-size: 14px;padding-bottom: 20px;">请选择流标原因：</span><div style="height: 10px;"></div><ul class="ui-items modal-reason-list"></ul>', function () {
                    var params = {},
                        reasonCount = 0;

                    $(".modal-fail-reason:checked").each(function () {
                        reasonCount++;
                        if (params[$(this).attr("name")] == null) {
                            params[$(this).attr("name")] = $(this).val();
                        } else {
                            params[$(this).attr("name")] += "," + $(this).val();
                        }
                    });

                    if (reasonCount < 4 || reasonCount > 6) {
                        alert("请选择4-6条流标原因");
                        return;
                    }

                    var otherReason = $(".modal-other-reason");
                    params[otherReason.attr("name")] = otherReason.val();

                    if (confirm("是否确认将此标执行流标操作?")) {
                        console.log(JSON.stringify(params));
                    }

                });

                var testList = ["询价方资质条件要求过高。", "项目的方案和型号，配置要求不合理。", "预算价格过低。", "付款方式过于苛刻。", "询价要求过高。","废标条款过多过滥。","梦想过于天马行空。","其他"];
                console.log(JSON.stringify(testList));

                for (var i = 0; i < testList.length; i++) {
                    $(".modal-reason-list").append('<li style="padding-bottom: 0;"><input class="modal-fail-reason" type="checkbox" name="failReason" id="' + i + '" value="' + i + '" /><label for="' + i + '" style="padding-left: 10px;float:none;font-weight:100;height">' + testList[i] + '</label></li>');
                }

                $(".modal-reason-list").append('<li style="padding-bottom: 0;"><textarea class="modal-other-reason" type="text" name="otherReason" style="margin-left: 0;resize: none;height:100px;width: 96%;padding: 2%;"/></li>')

                $(".modal-other-reason").on("change", function () {
                    if ($(this).val().length > 200) {
                        $(this).val($(this).val().substring(0, 200));
                    }
                });

            });

            $("#btn_next").click(function () {

            });
        }
    }

    function call_detailFail() {
        alert("数据加载失败");
    }

    /**
     * 获取我的出价
     * @return {[type]} [description]
     */
    function fn_getMyBidList() {
        $("#myBid").css("display", "");

        currentGrid1 = fn_initGrid();
        currentGrid1.reBind(dataSource.myList);
        $("#myBid .ui-grid-contentDiv").attr("title","");

        //bidRepos.getListOfMy(currentQueryObj.userName, currentQueryObj.inquiryID, currentQueryObj.pageno, currentQueryObj.pagesize, call_mybidok, call_mybidfail, call_mybidfail);
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

        currentGrid2.reBind(dataSource.hisList);
        $("#otherBid .ui-grid-contentDiv").attr("title","");
        //bidRepos.getListOfOther(currentQueryObj.userName, currentQueryObj.inquiryID, currentQueryObj.pageno, currentQueryObj.pagesize, call_opponentbid, call_opponentbidfail, call_opponentbidfail);
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

    var currentQueryObj = {};

    exports.load = function () {
        currentQueryObj.inquiryId = getParam("key");
        fn_getDetail();
    };
});
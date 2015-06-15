define("detail-config", [], function(require, exports) {

    var
        sortCss = {
            upImage: "../images/grid/icon-asc.png",
            downImage: "../images/grid/icon-desc.png",
            width: 15,
            height: 10
        },
        columns = [{
            index: 1,
            head: {
                width: 169,
                issort: false,
                text: "当前轮数"
            },
            body: {
                align: "center",
                fields: "round",
                renderFn: "renderRound"
            }
        }, {
            index: 2,
            head: {
                text: "投标方名称",
                width: 172,
                align: "center"
            },
            body: {
                align: "center",
                fields: "userName",
            }
        }, {
            index: 3,
            head: {
                text: "所在地",
                width: 169,
                align: "center"
            },
            body: {
                align: "center",
                fields: "province"
            }
        }, {
            index: 4,
            head: {
                text: "报价日期",
                width: 169,
                align: "center"
            },
            body: {
                align: "left",
                fields: "createTime"
            }
        }, {
            index: 5,
            head: {
                text: "报价金额(万)",
                width: 169,
                align: "center"
            },
            body: {
                align: "center",
                fields: "money"
            }
        }, {
            index: 6,
            head: {
                text: "附件信息",
                width: 180,
                align: "center"
            },
            body: {
                align: "left",
                fields: "attachmentUrls",
                renderFn: "renderAttachments"
            }
        }, {
            index: 7,
            head: {
                text: "操作",
                width: 160,
                align: "center"
            },
            body: {
                align: "center",
                // fields: "id",
                renderFn: "renderOpt"
            }
        }];

    exports.gridConfig = {
        key: "myBidGrid",
        renderTo: "data",
        columns: columns,
        sortCss: sortCss
    };

    exports.gridConfig2 = {
        key: "otherBidGrid",
        renderTo: "data2",
        columns: columns,
        sortCss: sortCss
    };

    exports.dialogConfig = {
        title: "出价方列表",
        isHideTopClose: true,
        width: 300,
        buttons: [{
            cmd: "ok",
            text: "确定"
        }, {
            cmd: "cancel",
            text: "取消"
        }]
    };
});
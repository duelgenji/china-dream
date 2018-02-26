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
                width: 99,
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
                width: 132,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "userNickName,userId",
                renderFn:"renderNickName"
            }
        }, {
            index: 3,
            head: {
                text: "所在地",
                width: 99,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "userProvince"
            }
        }, {
            index: 4,
            head: {
                text: "报价日期",
                width: 169,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "createTime"
            }
        }, {
            index: 5,
            head: {
                text: "报价金额",
                width: 169,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "totalPrice"
            }
        }, {
            index: 6,
            head: {
                text: "商务附件",
                width: 200,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "businessFileList",
                renderFn: "renderAttachments"
            }
        },  {
            index: 7,
            head: {
                text: "技术附件",
                width: 200,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                fields: "techFileList",
                renderFn: "renderAttachments"
            }
        }, {
            index: 8,
            head: {
                text: "操作",
                width: 120,
                issort: false,
                align: "center"
            },
            body: {
                align: "center",
                // fields: "id",
                issort: false,
                renderFn: "renderOpt"
            }
        }],
    columns_qm = [{
        index: 1,
        head: {
            width: 99,
            issort: false,
            text: "排名"
        },
        body: {
            align: "center",
            fields: "rank",
            renderFn: "renderRank"
        }
    }, {
        index: 2,
        head: {
            width: 99,
            issort: false,
            text: "当前轮数"
        },
        body: {
            align: "center",
            fields: "round",
            renderFn: "renderRound"
        }
    }, {
        index: 3,
        head: {
            text: "投标方名称",
            width: 132,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            fields: "userNickName,userId",
            renderFn:"renderNickName"
        }
    },  {
        index: 4,
        head: {
            text: "报价日期",
            width: 169,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            fields: "createTime"
        }
    }, {
        index: 5,
        head: {
            text: "报价金额",
            width: 169,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            fields: "totalPrice"
        }
    }, {
        index: 6,
        head: {
            text: "商务附件",
            width: 200,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            fields: "businessFileList",
            renderFn: "renderAttachments"
        }
    },  {
        index: 7,
        head: {
            text: "技术附件",
            width: 200,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            fields: "techFileList",
            renderFn: "renderAttachments"
        }
    }, {
        index: 8,
        head: {
            text: "操作",
            width: 120,
            issort: false,
            align: "center"
        },
        body: {
            align: "center",
            // fields: "id",
            issort: false,
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

    exports.gridConfig2qm = {
        key: "myBidGrid",
        renderTo: "data",
        columns: columns_qm,
        sortCss: sortCss
    };

    exports.gridConfig3 = {
        key: "otherBidGrid2",
        renderTo: "data3",
        columns: columns,
        sortCss: sortCss
    };
    exports.gridConfig4 = {
        key: "otherBidGrid3",
        renderTo: "data4",
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
define("userroom-config",[],function(require,exports){

    var 
        sortCss = {
            upImage: "../images/grid/icon-asc.png",
            downImage: "../images/grid/icon-desc.png",
            width: 15,
            height: 10
        };
    
    exports.gridConfig = {
        renderTo:"data",
        columns: [
            {
                    index: 1,
                    head: {
                        width: 160,
                        issort: false,
                        text:"综合排序"
                    },
                    body: {
                        align: "center",
                        fields:"logoUrl,userType",
                        renderFn: "renderLogo"
                    }
                }, {
                    index: 2,
                    head: {
                        text: "名称",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "nickname,VIP,test",
                        renderFn:"renderUser"
                    }
                }, {
                    index: 3,
                    head: {
                        text: "行业",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "industry"
                    }
                }, {
                    index: 4,
                    head: {
                        text: "地区",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "province"
                    }
                }, {
                    index: 5,
                    head: {
                        text: "出价成功率",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "quotationSuccessRate",
                        renderFn:"renderBidSuccessRate"
                    }
                }, {
                    index: 6,
                    head: {
                        text: "出价总数",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "quotationDoneTime"
                    }
                }, {
                    index: 7,
                    head: {
                        text: "评分",
                        width: 160,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        text:"--"
                    }
                }],
                sortCss: sortCss
    };
});
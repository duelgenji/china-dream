define("list-config",[],function(require,exports){

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
                        width: 170,
                        issort: false,
                        text:"综合排序"
                    },
                    body: {
                        align: "center",
                        fields:"logoUrl,id",
                        renderFn: "renderInquiryLogo"
                    }
                }, {
                    index: 2,
                    head: {
                        text: "人气",
                        width: 300,
                        issort: false,
                        align: "center"
                    },
                    body: {
                        align: "left",
                        fields: "title,inquiryNo,userName,id,test,status",
                        renderFn: "renderPopularity"
                    }
                }, {
                    index: 3,
                    head: {
                        text: "标的",
                        width: 150,
                        issort: false,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "totalPrice,good",
                        renderFn: "renderBiaoDi"
                    }
                }, {
                    index: 4,
                    head: {
                        text: "询价成功率",
                        width: 150,
                        issort: false,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "provinceCode,successRate",
                        renderFn: "renderBidSuccessRate"
                    }
                }, {
                    index: 5,
                    head: {
                        text: "询价总数",
                        width: 150,
                        issort: false,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "industryCode,inquiryTimes",
                        renderFn: "renderBidCount"
                    }
                }, {
                    index: 6,
                    head: {
                        text: "截止时间",
                        width: 170,
                        issort: false,
                        align: "center"
                    },
                    body: {
                        align: "center",
                        fields: "limitDate,round,inquiryMode,status,successName",
                        renderFn: "renderEndDate"
                    }
                }],
        sortCss: sortCss
	};
});
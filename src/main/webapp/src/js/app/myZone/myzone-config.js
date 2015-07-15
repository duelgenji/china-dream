define("myzone-config", [], function(require, exports) {
	var
		sortCss = {
			upImage: "../images/grid/icon-asc.png",
			downImage: "../images/grid/icon-desc.png",
			width: 15,
			height: 10
		};

	exports.gridCfg1 = {
		key:"bidGrid",
		renderTo: "data1",
		columns: [{
			index: 1,
			head: {
				width: 140,
				issort: false,
				text: "标号"
			},
			body: {
				align: "center",
				fields: "inquiryNo,inquiryId",
				renderFn: "renderInquiryNo"
			}
		}, {
			index: 2,
			head: {
				text: "标题",
				issort: false,
				width: 69,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryTitle,inquiryId",
				renderFn: "renderInquiryTitle"
			}
		}, {
			index: 3,
			head: {
				text: "轮次",
				issort: false,
				width: 40,
				align: "center"
			},
			body: {
				align: "center",
				fields: "round"
			}
		}, {
			index: 4,
			head: {
				text: "询价方式",
				issort: false,
				width: 70,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryMode"
				//renderFn: "renderInquiryMode"
			}
		}, {
			index: 5,
			head: {
				text: "发布方",
				issort: false,
				width: 59,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryNickname"
			}
		}, {
			index: 6,
			head: {
				text: "标的",
				issort: false,
				width: 50,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryPrice"
			}
		}, {
			index: 7,
			head: {
				text: "截止时间",
				issort: false,
				width: 119,
				align: "center"
			},
			body: {
				align: "center",
				fields: "limitDate"
			}
		}, {
			index: 8,
			head: {
				text: "商务附件",
				issort: false,
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "businessFileList",
				renderFn: "renderAttachments"
			}
		}, {
			index: 9,
			head: {
				text: "技术附件",
				issort: false,
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "techFileList",
				renderFn: "renderAttachments"
			}
		}, {
			index: 10,
			head: {
				text: "报价金额",
				issort: false,
				width: 89,
				align: "center"
			},
			body: {
				align: "center",
				fields: "quotationPrice"
			}
		}, {
			index: 11,
			head: {
				text: "操作",
				issort: false,
				width: 69,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryId",
				renderFn: "renderOptOfBid"
			}
		}]
	};

	exports.gridCfg2 = {
		key:"inquiryGrid",
		renderTo: "data2",
		columns:[{
			index: 1,
			head: {
				width: 150,
				issort: false,
				text: "标号"
			},
			body: {
				align: "center",
				fields: "inquiryNo,inquiryId",
				renderFn: "renderInquiryNo"
			}
		}, {
			index: 2,
			head: {
				text: "标题",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryTitle,inquiryId",
				renderFn: "renderInquiryTitle"
			}
		}, {
			index: 3,
			head: {
				text: "轮次",
				width: 53,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryRound"
			}
		}, {
			index: 4,
			head: {
				text: "询价方式",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryMode"
				//renderFn: "renderInquiryMode"
			}
		}, {
			index: 5,
			head: {
				text: "状态",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryStatus",
				renderFn: "renderState"
			}
		}, {
			index: 6,
			head: {
				text: "地区",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryProvince"
			}
		}, {
			index: 7,
			head: {
				text: "标的",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryPrice"
			}
		}, {
			index: 8,
			head: {
				text: "截止时间",
				width: 120,
				align: "center"
			},
			body: {
				align: "center",
				fields: "limitDate"
			}
		}, {
			index: 9,
			head: {
				text: "操作",
				width: 89,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryId",
				renderFn: "renderOptOfInquiry"
			}
		}]
	};

	exports.gridCfg3 = {
		key:"collectGrid",
		renderTo: "data3",
		columns:[{
			index: 1,
			head: {
				width: 150,
				issort: false,
				text: "标号"
			},
			body: {
				align: "center",
				fields: "inquiryNo,inquiryId",
				renderFn: "renderInquiryNo"
			}
		}, {
			index: 2,
			head: {
				text: "标题",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryTitle"
			}
		}, {
			index: 3,
			head: {
				text: "轮次",
				width: 83,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "round"
			}
		}, {
			index: 4,
			head: {
				text: "询价方式",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryMode"
			}
		}, {
			index: 5,
			head: {
				text: "状态",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "state",
				renderFn: "renderState"
			}
		}, {
			index: 6,
			head: {
				text: "地区",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "province"
			}
		}, {
			index: 7,
			head: {
				text: "标的",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "totalPrice"
			}
		}, {
			index: 8,
			head: {
				text: "截止时间",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "purchaseCloseDate"
			}
		}, {
			index: 9,
			head: {
				text: "操作",
				width: 89,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "collectionId",
				renderFn: "renderOptOfCollect"
			}
		}]
	};

	exports.gridCfg4 = {
		key:"messageGrid",
		renderTo: "data4",
		columns:[{
			index: 1,
			head: {
				width: 150,
				issort: false,
				text: "标号"
			},
			body: {
				align: "center",
				fields: "inquiryNo,inquiryId",
				renderFn: "renderInquiryNo"
			}
		}, {
			index: 2,
			head: {
				text: "标题",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryTitle,inquiryId",
				renderFn: "renderInquiryTitle"
			}
		}, {
			index: 3,
			head: {
				text: "轮次",
				width: 53,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "round"
			}
		}, {
			index: 4,
			head: {
				text: "出价/询价方",
				width: 103,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "userNickname,userId",
				renderFn: "renderNickname"
			}
		}, {
			index: 5,
			head: {
				text: "询价方式",
				width: 100,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryMode"
				//renderFn: "renderInquiryMode"
			}
		}, {
			index: 6,
			head: {
				text: "状态",
				width: 70,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "inquiryStatus",
				renderFn: "renderState"
			}
		}, {
			index: 7,
			head: {
				text: "地区",
				width: 70,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "province"
			}
		}, {
			index: 8,
			head: {
				text: "标的",
				width: 100,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "totalPrice"
			}
		}, {
			index: 9,
			head: {
				text: "截止时间",
				width: 120,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "limitDate"
			}
		}, {
			index: 10,
			head: {
				text: "操作",
				width: 149,
				issort: false,
				align: "center"
			},
			body: {
				align: "center",
				fields: "messageId",
				renderFn: "renderOptOfMessage"
			}
		}]
	};

	exports.gridCfg5={
		key:"collectGrid2",
		renderTo: "data5",
		columns:[
			{
				index:1,
				head:{
					issort:false,
					width:130,
					text:"名称"
				},
				body:{
					align:"center",
					fields:"userNickname,userId",
					renderFn:"renderUserId"
				}
			},
			{
				index:2,
				head:{
					issort:false,
					width:130,
					text:"行业"
				},
				body:{
					align:"center",
					fields:"industry"
				}
			},
			{
				index:3,
				head:{
					width:130,
					issort:false,
					text:"地区"
				},
				body:{
					align:"center",
					fields:"province"
				}
			},
			{
				index:4,
				head:{
					width:130,
					issort:false,
					text:"出价成功率"
				},
				body:{
					align:"center",
					fields:"quotationSuccessRate"
				}
			},
			{
				index:5,
				head:{
					width:130,
					issort:false,
					text:"出价总数"
				},
				body:{
					align:"center",
					fields:"quotationDoneTime"
				}
			},
			{
				index:6,
				head:{
					width:130,
					issort:false,
					text:"评分"
				},
				body:{
					align:"center",
					text:"--"
				}
			},
			{
				index:7,
				head:{
					width:130,
					issort:false,
					text:"操作"
				},
				body:{
					align:"center",
					fields:"userId",
					renderFn:"renderOptOfPersonCollect"
				}
			}
		]
	};
});
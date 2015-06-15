/**
 * 出价模块的相关数据操作
 * @param  {[type]} require  [description]
 * @param  {[type]} exports	 [description]
 * @param  {[type]} ok       [description]
 * @param  {[type]} fail     [description]
 * @param  {[type]} error    [description]
 * @return {[type]}          [description]
 */
define("bid-repos", ["base-repos"], function(require, exports) {

	var base = require("base-repos");

	/**
	 * 获取我的出价
	 * @param  {[type]} username   [description]
	 * @param  {[type]} inquirykey [description]
	 * @param  {[type]} pageno     [description]
	 * @param  {[type]} pagesize   [description]
	 * @param  {[type]} ok         [description]
	 * @param  {[type]} fail       [description]
	 * @param  {[type]} error      [description]
	 * @return {[type]}            [description]
	 */
	exports.getListOfMy = function(username, inquirykey, pageno, pagesize, ok, fail, error) {
		base.setAction("bid/getMine").get({
			"bid.userName": username,
			"bid.id": inquirykey,
			pageno: pageno,
			pagesize: pagesize
		}, ok, fail, error);

		return exports;
	};

	/**
	 * 获取他人出价
	 * @param  {[type]} username   [description]
	 * @param  {[type]} inquirykey [description]
	 * @param  {[type]} pageno     [description]
	 * @param  {[type]} pagesize   [description]
	 * @param  {[type]} ok         [description]
	 * @param  {[type]} fail       [description]
	 * @param  {[type]} error      [description]
	 * @return {[type]}            [description]
	 */
	exports.getListOfOther = function(username, inquirykey, pageno, pagesize, ok, fail, error) {
		base.setAction("bid/getOther").get({
			"bid.userName": username,
			"bid.id": inquirykey,
			pageno: pageno,
			pagesize: pagesize
		}, ok, fail, error);

		return exports;
	};

	/**
	 * 发送申请出价的站内信
	 * @param  {[type]} username   [description]
	 * @param  {[type]} inquirykey [description]
	 * @param  {[type]} ok         [description]
	 * @param  {[type]} fail       [description]
	 * @param  {[type]} error      [description]
	 * @return {[type]}            [description]
	 */
	exports.sendBidRequest = function(username, inquirykey, ok, fail, error) {
		base.setAction("bid/sendRequest").post({
			"bid.userName": username,
			"bid.id": inquirykey,
		}, ok, fail, error);

		return exports;
	};

	/**
	 * 提交表单
	 * @param {[type]} username  [description]
	 * @param {[type]} inquiryid [description]
	 * @param {[type]} money     [description]
	 * @param {[type]} formid    [description]
	 * @param {[type]} ok        [description]
	 * @param {[type]} fail      [description]
	 * @param {[type]} error     [description]
	 */
	exports.addBid = function(username, inquiryid, money, formid, ok, fail, error) {
		base.setAction("bid/add.action?userName=" + username + "&money=" + money, formid, ok, fail, error);
	};
});
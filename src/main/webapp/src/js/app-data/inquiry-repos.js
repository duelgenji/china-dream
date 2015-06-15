/**
 * 询价模块的相关数据操作
 * @param  {[type]} require  [description]
 * @param  {[type]} exports) {	var        base [description]
 * @return {[type]}          [description]
 */
define("inquiry-repos", ["base-repos"], function(require, exports) {

	var base = require("base-repos");

	/**
	 * 获取所有询价
	 * @param  {[type]} queryParams [description]
	 *     pageno
	 *     pagesize
	 *     keyword
	 *     province
	 *     industry
	 *
	 * @param  {[type]} ok          [description]
	 * @param  {[type]} fail        [description]
	 * @param  {[type]} error       [description]
	 * @return {[type]}             [description]
	 */
	exports.getList = function(queryParams, ok, fail, error) {
		base.setAction("inquiry/getInqueryList").post(queryParams, ok, fail, error);
	};

	/**
	 *
	 * @param  {[type]} queryParams [description]
	 * @param  {[type]} ok          [description]
	 * @param  {[type]} fail        [description]
	 * @param  {[type]} error       [description]
	 * @return {[type]}             [description]
	 */
	exports.getDetail = function(id, ok, fail, error) {
		base.setAction("inquiry/getAInquiry").get({
			"inquiry.id": id
		}, ok, fail, error);
	};

	/**
	 * 获取我的询价列表
	 * @param  {[type]} username [description]
	 * @param  {[type]} status   [description]
	 * @param  {[type]} ok       [description]
	 * @param  {[type]} fail     [description]
	 * @param  {[type]} error    [description]
	 * @return {[type]}          [description]
	 */
	exports.getListOfMy = function(username, status, pageno, pagesize,ok, fail, error) {
		base.setAction("inquiry/myInquiry").get({
			"inquiry.userName": username,
			"inquiry.status": status
		}, ok, fail, error);
	};

	/**
	 * 将询价标状态置为成功
	 * @param  {[type]} username  [description]
	 * @param  {[type]} inquiryID [description]
	 * @param  {[type]} ok        [description]
	 * @param  {[type]} fail      [description]
	 * @param  {[type]} error     [description]
	 * @return {[type]}           [description]
	 */
	exports.pass = function(username, inquiryID, ok, fail, error) {
		base.setAction("inquiry/pass").post({
			username: username,
			id: inquiryID
		}, ok, fail, error);
	};

	/**
	 * 将询价标置为流标状态
	 * @param  {[type]} username  [description]
	 * @param  {[type]} inquiryID [description]
	 * @param  {[type]} ok        [description]
	 * @param  {[type]} fail      [description]
	 * @param  {[type]} error     [description]
	 * @return {[type]}           [description]
	 */
	exports.flow = function(username, inquiryID, ok, fail, error) {
		base.setAction("inquiry/flow").post({
			username: username,
			id: inquiryID
		}, ok, fail, error);
	};

	/**
	 * 发布询价
	 * @param  {[type]} parameters [description]
	 * @param  {[type]} ok         [description]
	 * @param  {[type]} fail       [description]
	 * @param  {[type]} error      [description]
	 * @return {[type]}            [description]
	 */
	exports.publish = function(parameters,ok,fail,error){
		base.setAction("inquiry/createAInquiry").post(parameters,ok,fail,error);
	};
});
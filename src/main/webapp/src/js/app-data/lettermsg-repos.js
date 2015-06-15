/**
 * 站内信模块的相关数据操作
 * @param  {[type]} require  [description]
 * @param  {[type]} exports) {	var        base [description]
 * @param  {[type]} ok       [description]
 * @param  {[type]} fail     [description]
 * @param  {[type]} error    [description]
 * @return {[type]}          [description]
 */
define("lettermsg-repos", ["base-repos"], function(require, exports) {

	var base = require("base-repos");

	/**
	 * 获取我的站内信
	 * @param  {[type]} username [description]
	 * @param  {[type]} type   [description]
	 * @param  {[type]} pageno   [description]
	 * @param  {[type]} pagesize [description]
	 * @param  {[type]} ok       [description]
	 * @param  {[type]} fail     [description]
	 * @param  {[type]} error    [description]
	 * @return {[type]}          [description]
	 */
	exports.getListOfMy = function(username, type, pageno, pagesize, ok, fail, error) {
		base.setAction("lettermsg/getMine").get({
			username: username,
			type: type,
			pageno: pageno,
			pagesize: pagesize
		}, ok, fail, error);
	};

	/**
	 * 同意
	 * @param  {[type]} username [description]
	 * @param  {[type]} key   [description]
	 * @param  {[type]} ok    [description]
	 * @param  {[type]} fail  [description]
	 * @param  {[type]} error [description]
	 * @return {[type]}       [description]
	 */
	exports.pass = function(username, key, ok, fail, error) {
		base.setAction("lettermsg/pass").post({
			username: username,
			id: key
		}, ok, fail, error);
	};

	/**
	 * 拒绝
	 * @param  {[type]} key    [description]
	 * @param  {[type]} reason [description]
	 * @param  {[type]} ok     [description]
	 * @param  {[type]} fail   [description]
	 * @param  {[type]} error  [description]
	 * @return {[type]}        [description]
	 */
	exports.refuse = function(key, reason, ok, fail, error) {
		base.setAction("lettermsg/refuse").post({
			username: username,
			reason: escape(reason),
			id: key
		}, ok, fail, error);
	};
});
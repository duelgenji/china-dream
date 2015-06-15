define("collect-repos", ["base-repos"], function(require, exports) {
	var base = require("base-repos");

	/**
	 * 获取我的收藏
	 * @param  {[type]} username [当前用户]
	 * @param  {[type]} type     [收藏类型]
	 * @param  {[type]} ok       [description]
	 * @param  {[type]} fail     [description]
	 * @param  {[type]} error    [description]
	 * @return {[type]}          [description]
	 */
	exports.getListOfMy = function(username, type, pageno, pagSize, ok, fail, error) {
		base.setAction("collect/getMine").get({
			username: username,
			type: type,
			pageno: pageno,
			pagesize: pageSize
		}, ok, fail, error);
	};

	/**
	 * 添加收藏
	 * @param {[type]} username  [当前用户]
	 * @param {[type]} type      [收藏类型]
	 * @param {[type]} targetkey [收藏目标的标识]
	 * @param {[type]} ok        [description]
	 * @param {[type]} fail      [description]
	 * @param {[type]} error     [description]
	 */
	exports.add = function(username, type, targetkey, ok, fail, error) {
		base.setAction("collect/add").post({
			username: username,
			type: type,
			targetkey: targetkey
		}, ok, fail, error);
	};

	/**
	 * 取消收藏
	 * @param  {[type]} username [description]
	 * @param  {[type]} key      [description]
	 * @param  {[type]} ok       [description]
	 * @param  {[type]} fail     [description]
	 * @param  {[type]} error    [description]
	 * @return {[type]}          [description]
	 */
	exports.cancel = function(username, key, ok, fail, error) {
		base.setAction("collect/cancel").post({
			username: username,
			key: key
		}, ok, fail, error);
	};
});
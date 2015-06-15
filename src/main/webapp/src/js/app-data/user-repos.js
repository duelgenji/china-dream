define("user-repos", ["base-repos"], function(require, exports) {

	var base = require("base-repos");

	/**
	 * 获取用户大厅列表
	 * @param  {[type]} queryParams [description]
	 * @param  {[type]} ok          [description]
	 * @param  {[type]} fail        [description]
	 * @param  {[type]} error       [description]
	 * @return {[type]}             [description]
	 */
	exports.getList = function(queryParams, ok, fail, error) {
		base.setAction("user/getList").post(queryParams, ok, fail, error);
	};

	/**
	 * 获取用户详情
	 * @param  {[type]} username [description]
	 * @param  {[type]} ok       [description]
	 * @param  {[type]} fail     [description]
	 * @param  {[type]} error    [description]
	 * @return {[type]}          [description]
	 */
	exports.getDetail = function(username, ok, fail, error) {
		base.setAction("user/getDetail").post({
			username: username
		}, ok, fail, error);
	};

	/**
	 * 登录
	 * @param  {[type]} formid [description]
	 * @param  {[type]} ok     [description]
	 * @param  {[type]} fail   [description]
	 * @param  {[type]} error  [description]
	 * @return {[type]}        [description]
	 */
	exports.login = function(username, password, ok, fail, error) {
		base.setAction("login/login").post({
			"user.userName": username,
			"user.password": password
		}, ok, fail, error);
	};

	exports.registerp = function(params,ok,fail,error){
		base.setAction("reg/register_p").post(params,ok,fail,error);
	};

	exports.registerc = function(params,ok,fail,error){
		base.setAction("reg/register_c").post(params,ok,fail,error);
	};

	exports.registerg = function(params,ok,fail,error){
		base.setAction("reg/register_g").post(params,ok,fail,error);
	};
});
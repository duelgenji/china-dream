define("systemDefine-repos", ["base-repos"], function(require, exports) {

	var base = require("base-repos");

	exports.getAllIndustry = function(ok, fail, error) {

		base.setAction("systemDefine/getIndustry").get(null, ok, fail, error);

		return exports;
	};

	exports.getAllProvince = function(ok, fail, error) {
		base.setAction("systemDefine/getProvince").get(null, ok, fail, error);
	};

	exports.getAllDreamwords = function(ok, fail, error) {
		base.setAction("systemDefine/getDreamwords").get(null, ok, fail, error);
	};
});
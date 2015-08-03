define("systemDefine-repos", ["base-repos"], function (require, exports) {

    var base = require("base-repos");

    exports.getAllIndustry = function (ok, fail, error) {

        base.setAction("systemDefine/getIndustry").get(null, ok, fail, error);

        return exports;
    };

    exports.getAllProvince = function (ok, fail, error) {
        base.setAction("systemDefine/getProvince").get(null, ok, fail, error);
    };

    exports.getAllDreamword = function (ok, fail, error) {
        $.ajax({
            url: "../dreamWord/retrieveDreamWordList",
            type: "post",
            dataType: "json",
            success: function (result) {
                if (result.success == 1) {
                    ok(result.data);
                } else {
                    error();
                }
            },
            error: error()
            //complete
        })


    };
});
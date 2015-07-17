(function(global) {
    function factory() {
        var validator = {},
            customerValidator = {};
        /**
         * 是否为空
         * @param  {[String]}  val [待检测的值]
         * @return {Boolean}  true of false   [description]
         */
        validator.isEmptyOrNull = function(val) {
            return !val || val == "";
        };

        /**
         * 是否是对应精度的数字
         * @param  {[String|Number]}  d     [待检测的数字]
         * @param  {[Number]}  digits [精度数]
         * @return {Boolean}        [description]
         */
        validator.isNumber = function(d, digits) {
            if (isNaN(d)) return false;
            var i = d.toString().indexOf('.');

            if (i == -1) {
                return digits == 0;
            }
            var k = d.toString().substring(i + 1).length;
            return k !== digits;
        };

        validator.isFloat = function(d) {
            if (isNaN(d)) return false;
            var i = d.toString().indexOf('.');
            return i == -1;
        };

        validator.isRangeFloat = function(d, digit, start, end) {
            if (isNaN(d)) return false;

            var i = d.toString().indexOf('.');

            if (i == -1) {
                return digit >= 0 && (d > start && d < end);
            }

            var k = d.toString().substring(i + 1).length;

            return k <= digit && d > start && d < end;
        };

        /**
         * 是否是指定范围的数字
         * @param  {[String|Number]}  d     [description]
         * @param  {[type]}  start [description]
         * @param  {[type]}  end   [description]
         * @return {Boolean}       [description]
         */
        validator.isRangeNumber = function(d, start, end) {
            if (isNaN(d)) return false;
            d = parseInt(d, 10);
            return d >= start && d <= end;
        };

        /**
         * 是否是正数
         * @param  {[Number]}  d [待验证的值]
         * @return {Boolean}   [description]
         */
        validator.isPositive = function(d) {
            if (isNaN(d)) return false;
            return parseFloat(d) > 0;
        };

        /**
         * 是否为负数
         * @param  {[Number]}  d [待验证的值]
         * @return {Boolean}   [description]
         */
        validator.isNegative = function(d) {
            if (isNaN(d)) return false;
            return parseFloat(d) < 0;
        };

        /**
         * 是否是邮箱格式
         * @param  {[type]}  val [description]
         * @return {Boolean}     [description]
         */
        validator.isEmail = function(val) {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(val);
        };

        /**
         * 是否是手机号格式
         * @param  {[type]}  val [description]
         * @return {Boolean}     [description]
         */
        validator.isMobile = function(val) {
            var _d = /^1[3578][01379]\d{8}$/g;
            var _l = /^1[34578][01256]\d{8}$/g;
            var _y = /^(134[0-9]\d{7}|1[34578][0-35-9]\d{8})$/g;

            return _d.test(val) || _l.test(val) || _y.test(val);
        };

        /**
         * 是否是电话格式
         * @param  {[type]}  val [description]
         * @return {Boolean}     [description]
         */
        validator.isTel = function(val) {
            return /^((\+?86)|(\(\+86\)))?\d{3,4}-\d{7,8}(-\d{3,4})?$/.test(val);
        };

        /**
         * [比较两个数字的大小]
         * @param  {[Number]} d1 [数字1]
         * @param  {[Number]} d2 [数字2]
         * @return {[Number]}
         *         1: d1>d2
         *         0: d1=d2
         *        -1: d1<d2
         *          [description]
         */
        validator.compare = function(d1, d2) {
            if (isNaN(d1) || isNaN(d2)) {
                return false;
            }
            if (d1 === d2) return 0;
            return d1 > d2 ? 1 : -1;
        };

        /**
         * 正则验证
         * @param  {[type]} val    [description]
         * @param  {[type]} regStr [description]
         * @return {[type]}        [description]
         */
        validator.regTest = function(val, regStr) {
            if (validator.isEmptyOrNull(val) || validator.isEmptyOrNull(regStr)) return false;
            var reg = new RegExp(regStr);
            return reg.test(val);
        };

        var exts = validator.extends = {};
        /**
         * 装载其他验证处理
         * @param  {[String]} name [要装载的验证处理标识]
         * @param  {[Function]} call [验证处理]
         * @return {[type]}      [description]
         */
        exts.on = function(name, call) {
            if (!name) return this;
            if (!(name in customerValidator)) {
                customerValidator[name] = call;
            }
            return this;
        };

        /**
         * 卸载已经装载过的验证处理
         * @param  {[String]} name [要卸载的验证处理标识]
         * @return {[type]}      [description]
         */
        exts.off = function(name) {
            if (!name) return this;
            if (name in customerValidator) {
                delete customerValidator[name];
            }
            return this;
        };

        /**
         * 执行对应装载后的验证处理方法
         * @param  {[String]} name  [要装载的验证处理标识]
         * @param  {[Array]} param [待处理的参数]
         * @return {[Boolean]}       [验证结果]
         */
        exts.emit = function(name, param) {
            if (name in customerValidator) {
                return customerValidator[name].call(validator, param);
            }
            return false;
        };

        return validator;
    }

    !global.pureValidator && (global.pureValidator = factory())

    if (typeof define == "function") {
        if (define.amd || define.cmd) {
            define("pure-validator", [], function() {
                return pureValidator
            });
        }
    }

}(this));
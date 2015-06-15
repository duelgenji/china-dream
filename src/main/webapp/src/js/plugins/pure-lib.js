! function(global) {

    if (global.pure) {
        return
    }

    /**
     * 缓存数据
     * @type {Object}
     */
    var data = {}

    /**
     * 插件模块的缓存对象
     * @type {[Object]}
     */
    var cacheMods = data.modules = {}

    /**
     * 定义一个模块
     * @param  {[String]} id      [模块ID]
     * @param  {[Function]} factory [模块的工厂构造]
     * @return
     */
    function define2(id, factory) {
        cacheMods[id] = factory
    }

    /**
     * 加载需要的某个模块
     * @param  {[String]} id [待调用的模块ID]
     * @return {[Object]}    [模块的输出]
     */
    function require(id) {
        var mod = cacheMods[id];
        if (!mod || typeof mod === 'object') {
            return mod;
        }
        if (!mod.exports) {
            mod.exports = {};
            mod.exports = mod.call(mod.exports, require, mod.exports, mod) || mod.exports;
        }
        return mod.exports;
    }

    var pure = global.pure = {
        version: "0.0.1"
    };

    /**
     * 简单模块定义对象
     * @type {Object}
     */
    pure.define = define2;

    pure.require = require;

    if (typeof define == "function") {
        define("pure-lib", [], function() {
            return pure;
        });
    }
}(this);

pure.define("pure-core", function() {

    var core_hasOwn = {}.hasOwnProperty;

    var pattern;

    /**
     * 是否是指定类型的对象
     * @param  {[String]}  type [目标类型字符串]
     * @return {Boolean}      []
     */

    function isType(type) {
        return function(obj) {
            return Object.prototype.toString.call(obj) == "[object " + type + "]";
        };
    }

    this.isFunction = isType("Function");

    this.isArray = Array.isArray || isType("Array");

    this.isObject = isType("Object");

    this.isString = isType("String");

    /**
     * 是否是纯对象
     * @param  {[Object]}  obj [description]
     * @return {Boolean}     [description]
     */
    this.isPlainObject = function(obj) {
        if (!obj || this.isObject(obj)) {
            return false;
        }

        try {
            if (obj.constructor &&
                !core_hasOwn.call(obj, "constructor") &&
                !core_hasOwn.call(obj.constructor.prototype, "isPrototypeOf")) {
                return false;
            }
        } catch (e) {
            // IE8,9 Will throw exceptions on certain host objects #9897
            return false;
        }

        // Own properties are enumerated firstly, so to speed up,
        // if last one is own, then all properties are own.

        var key;
        for (key in obj) {}

        return key === undefined || core_hasOwn.call(obj, key);
    };

    /**
     * 去除左右两边的空格
     * @param  {[String]} str [待去除的字符串]
     * @return {[String]}     [去除后的字符串]
     */
    this.trim = function(str) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    };
    /**
     * 去除左边空格
     * @param  {[String]} str [待去除的字符串]
     * @return {[String]}     [去除后的字符串]
     */
    this.ltrim = function(str) {
        return str.replace(/(^\s*)/g, "");
    };

    /**
     * 去除右边空格
     * @param  {[String]} str [待去除的字符串]
     * @return {[String]}     [去除后的字符串]
     */
    this.rtrim = function(str) {
        return str.replace(/(\s*$)/g, "");
    };

    /**
     * 字符串格式化
     * @param  {[String]} str [带格式的字符串 例如: " i'm a %1 "]
     * @return {[String]}     [格式化完成的字符串]
     *  note: 占位符只支持到9个，如果需要更多的请再次调用此方法
     */
    this.format = function(str) {
        var args = arguments;
        if (!pattern) pattern = new RegExp("%([1-9])", "g");
        return String(str).replace(pattern, function(match, index) {
            return args[index];
        });
    };

    /**
     * 遍历数组，通过进行回调处理
     * @param  {[Array]}   arr [待遍历的数组]
     * @param  {Function} callback  [数组每个元素作为值的回调函数]
     * @return  no return
     */
    this.each = function(arr, callback) {
        for (var i = 0, a; a = arr[i];) {
            callback(a, i++);
        }
    };

    /**
     * 合并两个对象
     * @param  {[type]} old    [description]
     * @param  {[type]} target [description]
     * @return {[type]}        [description]
     */
    this.combineObject = function(old, target) {

        var prop, src, copy, copyIsArray;

        !target && (target = {});

        for (prop in old) {

            src = target[prop];

            copy = old[prop];

            if (src != undefined || old == src) {
                continue;
            }

            // Recurse if we're merging plain objects or arrays
            if (copy && (this.isPlainObject(copy) || (copyIsArray = this.isArray(copy)))) {
                if (copyIsArray) {
                    copyIsArray = false;
                    clone = src && this.isArray(src) ? src : [];
                } else {
                    clone = src && this.isPlainObject(src) ? src : {};
                }
                // Never move original objects, clone them
                target[prop] = this.combineObject(clone, copy);

                // Don't bring in undefined values
            } else if (copy !== undefined) {
                target[prop] = copy;
            }
        }
        return target;
    };

    var that = this;
    /**
     * 以下上将一些基本操作能够注入到js基本对象(String,Function,Array等)的prototype中
     * @return {[type]} [description]
     */
    !String.prototype.trim && (String.prototype.trim = function() {
        return that.trim(this);
    });

    !String.prototype.ltrim && (String.prototype.ltrim = function() {
        return that.ltrim(this);
    });

    !String.prototype.rtrim && (String.prototype.rtrim = function() {
        return that.rtrim(this);
    });

    !String.prototype.format && (String.prototype.format = function() {

        var argus = [this];

        for (var i = 0, l = arguments.length; i < l; i++) {
            argus.push(arguments[i]);
        }

        return that.format(argus);
    });

    !Array.prototype.each && (Array.prototype.each = function(callback) {
        return that.each(this, callback);
    });
});

/**
 * 内置的发布订阅对象
 * @type {Object}
 */
pure.define("pure-pubSub", function() {

    var pubSubList = {};

    /**
     * 主题所有板块的注册
     * @param  {[String]} sector [板块]
     * @return {[type]}        [description]
     */
    this.register = function(sector) {

        var pS = pubSubList[sector];

        return !pS ? (pubSubList[sector] = new pubSub()) : pS;
    };

    /**
     * 清空所有主题
     * @return {[type]} [description]
     */
    this.clear = function() {
        pubSubList = {};
        return this;
    };

    /**
     * 发布订阅构造函数
     * @return {[type]} [description]
     */
    function pubSub(key) {
        /**
         * 发布订阅的主题缓存
         * @type {[type]}
         */
        this.callbacks = {};
    }

    pubSub.prototype = {
        constructor: pubSub,

        /**
         * 添加要订阅的主题
         * @param  {[String]}   name     [主题名称]
         * @param  {Function} callback [主题对应的回调]
         */
        on: function(name, callback) {

            var callbacks = this.callbacks;

            var list = (callbacks[name] || (callbacks[name] = []));

            list.push(callback);

            return this;
        },
        /**
         * 取消要订阅的主题
         * @param  {[String]}   name     [主题名称]
         * @param  {Function} callback [主题对应的回调]
         */
        off: function(name, callback) {

            if (!(name || callback)) {
                this.callbacks = {};
                return this;
            }

            var list = this.callbacks[name];
            if (list) {
                if (callback) {
                    for (var i = list.length - 1; i >= 0; i--) {
                        (list[i] === callback) && list.splice(i, 1);
                    }
                } else {
                    delete this.callbacks[name];
                }
            }
            return this;
        },

        /**
         * 覆盖原订阅的主题
         * @param  {[String]}   name     [主题名称]
         * @param  {Function} callback [主题对应的回调]
         */
        override: function(name, callback) {

            var callbacks = this.callbacks;

            var list = callbacks[name];

            if (list && list.length == 1) {
                list[0] = callback;
            } else {
                list = callbacks[name] = [];
                list.push(callback);
            }

            return this;
        },
        /**
         * 清除该板块下的所有主题
         * @return {[type]} [description]
         */
        empty: function() {
            this.callbacks = {};

            return this;
        },
        /**
         * 发布主题
         * @param  {[type]}  name         [主题名称]
         * @param  {[type]}  argus        [主题参数]
         * @param  {Boolean} isUseApply   [是否使用apply来将主题参数的数组形式进行铺开]
         * @param  {Boolean} isNeedResult [是否需要返回结果]
         * @param  {[type]}  scope        [主题作用域]
         * @return {[type]}               [description]
         */
        emit: function(name, argus, isUseApply, isNeedResult, scope) {

            if (!name) {
                return null;
            }

            var list = this.callbacks[name];

            if (list) {

                list = list.slice();

                return isNeedResult ? emitResult(list, argus, isUseApply, scope) : emit(list, argus, isUseApply, scope);
            }
            return null;
        }
    };

    /**
     * 发布订阅的主题
     * @param  {[String]}   list     [主题回调列表]
     * @param  {[Object]} argus [发布主题时需要传入的附加内容]
     * @param  {Boolean} isNeedResult [是否需要返回结果]
     * @param  {Boolean} isUseApply   [是否需要通过apply方式将数组形式的argus铺开以便给方法进行传参]
     * @return {[type]}               [description]
     */

    function emit(list, argus, isUseApply, scope) {

        var i = 0,
            l = list.length;

        if (!isUseApply) {
            for (; i < l; i++) {
                list[i](argus);
            }
        } else {
            for (; i < l; i++) {
                list[i].apply(scope, argus);
            }
        }

        return emit;
    }

    /**
     * 发布订阅的主题(带结果)
     * @return {[type]}       [发布后的结果]
     */
    function emitResult(list, argus, isUseApply, scope) {

        var results = [];

        var i = 0,
            l = list.length;

        if (!isUseApply) {
            for (; i < l; i++) {
                results.push(list[i](argus));
            }
        } else {
            for (; i < l; i++) {
                results.push(list[i].apply(scope, argus));
            }
        }
        return results.length == 1 ? results[0] : results;
    }
});

/**
 * 简易版兼容ie低版本和现代浏览器w3c风格的事件绑定
 *     此处还有针对mouseover和mouseout的特殊性没有完成
 * @return {[type]}                [description]
 */
pure.define("pure-event", function(require) {
    var
        doc = window.document,

        _cache = {},

        _expando = "@pure-event_" + +new Date,

        _idSeed = 0,

        w3c = !!doc.addEventListener,

        addListener = w3c ?
        function(el, type, fn) {
            el.addEventListener(type, fn, false);
        } :
        function(el, type, fn) {
            el.attachEvent('on' + type, fn);
        },
        removeListener = w3c ?
        function(el, type, fn) {
            el.removeEventListener(type, fn, false);
        } :
        function(el, type, fn) {
            el.detachEvent('on' + type, fn);
        };

    var lib = require("pure-core");

    function guid(el) {
        return el[_expando] || (el[_expando] = ++_idSeed);
    }

    function cacheData(elem, isget) {
        var id = guid(elem);
        return isget ? _cache[id] : (_cache[id] = _cache[id] || {});
    }

    function removeData(elem) {
        var id = typeof elem == "object" ? guid(elem) : elem,
            cache = _cache[id];
        if (!cache) return false;
        delete _cache[id];
        return true;
    }

    /**
     * 利用柯力化的形式返回一个Function，此Function实际上是绑定到dom元素上的事件
     * @param  {[Object]} cache [description]
     * @return {[type]}       [description]
     */
    function handle(cache) {
        return function(event) {
            event = fix(event || window.event);
            for (var i = 0, listener = cache.listener, fn; fn = listener[i++];) {
                if (fn.call(cache.target, event) === false) {
                    event.preventDefault();
                    //                    event.stopPropagation();
                }
            }
        };
    }

    /**
     * 只要进行event对象兼容 w3c标准和非w3c标准
     * @param  {[type]} event [description]
     * @return {[type]}       [description]
     */
    function fix(event) {
        /**
         * 如果是w3c则直接返回
         */
        if (event.target) return event;

        var event2 = {
            target: event.srcElement || doc,
            stopPropagation: function() {
                this.cancelBubble = true;
            },
            preventDefault: function() {
                this.returnValue = false;
            }
        };

        //In IE6/7/8 添加属性到window.event对象上回造成内存无法回收,需要复制该event对象
        for (var i in event) {
            event2[i] = event[i];
        }
        return event2;
    }

    /**
     * 绑定事件
     * @param  {[Dom|Array Dom]}   elems    [待绑定的dom元素或元素数组]
     * @param  {[String]}   type     [绑定的类型]
     * @param  {Function} callback [绑定的方法]
     * @return {[type]}            [description]
     */
    this.on = function(elems, type, callback) {
        var cache, data, listener;

        !lib.isArray(elems) && (elems = [elems]);

        for (var i = 0, elem; elem = elems[i++];) {

            if (elem.nodeType == 3 || elem.nodeType == 8) {
                continue;
            }

            data = cacheData(elem);

            cache = data[type] = (data[type] || {});

            listener = cache.listener = cache.listener || [];

            listener.push(callback);

            if (!cache.handler) {
                cache.target = elem;

                cache.handler = handle(cache);

                addListener(elem, type, cache.handler);
            }
        }
        return this;
    };

    /**
     * 取消事件绑定
     * @param  {[Dom|Array Dom]}   elems    [待取消绑定的dom元素或元素数组]
     * @param  {[String]}   type     [绑定的类型]
     * @param  {Function} callback [绑定的方法]
     * @return {[type]}            [description]
     */
    this.off = function(elems, type, callback) {
        var cache, data, listener, empty = true;

        !lib.isArray(elems) && (elems = [elems]);

        for (var i = 0, elem; elem = elems[i++];) {
            if (elem.nodeType == 3 || elem.nodeType == 8) {
                continue;
            }

            data = cacheData(elem);

            cache = data[type];

            if (!cache) continue;

            listener = cache.listener;

            if (callback) {
                for (var j = listener.length - 1; j >= 0; j--) {
                    (listener[j] === callback) && (listener = cache.listener.splice(j, 1));
                }
            } else {
                cache.listener = [];
            }

            if (cache.listener.length == 0) {

                removeListener(elem, type, cache.handler);

                delete data[type];

                for (var k in data) {
                    empty = false;
                }

                empty && removeData(elem);
            }
        }
        return this;
    };
});

/**
 * 简易版本的javascript原生DOM操作，只实现了一些基本的样式兼容和浏览器版本判断，以及pure组件开发所需的兼容方法
 * @param  {[type]}          [description]
 * @return {[type]}          [description]
 */
pure.define("pure-jsDom", function(require) {

    var coreMod = require("pure-core");

    var doc = window.document;

    /**
     * IE5,6,7,8 能利用数组toString的bug进行判断
     * IE9以上不存在此bug
     *
     * refer : http://www.nowamagic.net/librarys/veda/detail/1406
     * @type {Boolean}
     */
    this.isIEBelow9 = !-[1, ];

    /**
     * 前部分可以判断出当前浏览器是否是IE5/6/7/8, 采用后面的判断主要是由于Opera可以伪装成为IE
     * @type {Boolean}
     */
    this.isIE = this.isIEBelow9 || !!(doc.all && navigator.userAgent.toLowerCase().indexOf('Opera') === -1);

    /**
     * 浏览器嗅探
     * @param  {Object} ) {                   var browser [description]
     * @return {[type]}   [description]
     */
    this.browser = (function() {
        var browser = {},
            userAgent = navigator.userAgent.toLowerCase();
        /**
         * ie版本
         * @type {[type]}
         */
        browser.ie = userAgent.match(/msie ([\d.]+)/);
        browser.ie = browser.ie ? browser.ie[1] : 0;

        /**
         * chrome版本
         * @type {[type]}
         */
        browser.chrome = userAgent.match(/chrome\/([\d.]+)/);
        browser.chrome = browser.chrome ? browser.chrome[1] : 0;

        /**
         * firefox版本
         * @type {[type]}
         */
        browser.ff = userAgent.match(/firefox\/([\d.]+)/);
        browser.ff = browser.ff ? browser.ff[1] : 0;

        /**
         * opera版本
         * @type {[type]}
         */
        browser.opera = userAgent.match(/opera.([\d.]+)/);
        browser.opera = browser.opera ? browser.opera[1] : 0;

        /**
         * safari版本
         * @type {[type]}
         */
        browser.safari = userAgent.match(/version\/([\d.]+).*safari/);
        browser.safari = browser.safari ? browser.chrome[1] : 0;

        /**
         * 是否为浏览器怪异模式(杂项模式)
         * @type {Boolean}
         */
        browser.isQuirksMode = !(doc.compatMode == "CSS1Compat");

        return browser;
    })();

    /**
     * 通过ID进行DOM元素的查找
     * @param  {[String]} id      [待查找DOM元素的ID字符串]
     * @param  {[Object]} context [查找DOM所在的上下文:默认是当前document]
     * @return {[DOM]}         [DOM元素]
     */
    this.byId = function(id, context) {
        !context && (context = doc);
        return context.getElementById(id);
    };

    /**
     * 样式名称的不重复交换
     *     当有此样式名称的时候则删除
     *     反之添加
     * @param  {[type]} obj       [description]
     * @param  {[type]} className [description]
     * @return {[type]}           [description]
     */
    this.toggleClass = function(obj, className) {
        var reg = new RegExp('(\\s|^)' + className + '(\\s|$)');
        obj.className && obj.className.match(reg) ? (obj.className = obj.className.replace(reg, " ")) : (obj.className += " " + className);

        return this;
    };

    /**
     * 获取和设置HTMLElement的样式值
     * @param  {[type]} elem  [description]
     * @param  {[type]} name  [description]
     * @param  {[type]} value [description]
     * @return {[type]}       [description]
     */
    var cssWHRegex = /width|height|left|top/i;

    /**
     * 设置和获取css样式
     *     当value==undefined的时候，则表示获取
     *     当执行设置的时候，name 有两种形式：String[驼峰形式的书写] or  Object[哈希键值对的形式]
     *     例：
     *         1.this.css(div,"width",44)
     *
     *         2.this.css(div, {
     *                width:         44,
     *                height:        44,
     *                top:           20
     *            })
     * @param  {[type]} elem  [description]
     * @param  {[type]} name  [description]
     * @param  {[type]} value [description]
     * @return {[type]}       [description]
     */
    this.css = function(elem, name, value) {
        // Don't set styles on text and comment nodes
        if (!elem || !name || elem.nodeType === 3 || elem.nodeType === 8 || !elem.style) {
            return this;
        }

        if (coreMod.isObject(name)) {
            for (var prop in name) {
                elem.style[prop] = cssWHRegex.test(prop) ? this.formatWH(name[prop]) : name[prop];
            }
            return this;
        }

        if (value === undefined) {
            return this.getStyle(elem, name);
        }

        elem.style[name] = cssWHRegex.test(name) ? this.formatWH(value) : value;

        return this;
    };

    /**
     * 获取HTMLElement的样式(兼容IE6以上和大部分现代浏览器)
     * @param  {[type]} elem [description]
     * @param  {[type]} name [description]
     * @return {[type]}      [description]
     */
    this.getStyle = function(elem, name) {
        var rPos = /^(left|right|top|bottom)$/,
            ecma = "getComputedStyle" in window,
            // 将中划线转换成驼峰式
            p = name.replace(/\-(\w)/g, function($, $1) {
                return $1.toUpperCase();
            });
        // 对float进行处理  
        p = p === "float" ? (ecma ? "cssFloat" : "styleFloat") : p;

        return !!elem.style[p] ? elem.style[p] :
            (ecma ?
                (function() {
                    var val = getComputedStyle(elem, null)[p];
                    // 处理top、right、bottom、left为auto的情况
                    if (rPos.test(p) && val === "auto") {
                        return "0px";
                    }
                    return val;
                }()) :
                (function() {
                    var val = elem.currentStyle[p];
                    // 获取元素在IE6/7/8中的宽度和高度
                    if ((p === "width" || p === "height") && val === "auto") {
                        var rect = elem.getBoundingClientRect();
                        return (p === "width" ? rect.right - rect.left : rect.bottom - rect.top) + "px";

                    }
                    // 获取元素在IE6/7/8中的透明度
                    if (p === "opacity") {
                        var filter = elem.currentStyle.filter;
                        if (/opacity/.test(filter)) {
                            val = filter.match(/\d /)[0] / 100;
                            return (val === 1 || val === 0) ? val.toFixed(0) : val.toFixed(1);
                        } else if (val === undefined) {
                            return "1";
                        }
                    }
                    // 处理top、right、bottom、left为auto的情况
                    if (rPos.test(p) && val === "auto") {
                        return "0px";
                    }
                    return val;
                })());
    };


    /**
     * 特殊的HTMLElement拼接映射
     */
    var specialHTMLElementMap;

    function strToHTMLElement(html) {

        if (!specialHTMLElementMap) {
            specialHTMLElementMap = {
                option: [1, '<select multiple="multiple">', '</select>'],
                legend: [1, '<fieldset>', '</fieldset>'],
                area: [1, '<map>', '</map>'],
                aram: [1, '<object>', '</object>'],
                thead: [1, '<table>', '</table>'],
                tr: [2, '<table><tbody>', '</tbody></table>'],
                col: [2, '<table><tbody></tbody><colgroup>', '</colgroup></table>'],
                td: [3, '<table><tbody><tr>', '</tr></tbody></table>'],
                body: [0, "", ""],
                _default: [1, '<div>', '</div>']
            }
            specialHTMLElementMap.optgroup = specialHTMLElementMap.option;
            specialHTMLElementMap.tbody = specialHTMLElementMap.tfoot = specialHTMLElementMap.colgroup = specialHTMLElementMap.caption = specialHTMLElementMap.thead;
            specialHTMLElementMap.th = specialHTMLElementMap.td;
        }

        var match = /<\s*\w.*?>/g.exec(html);
        var element = document.createElement('div');
        if (match != null) {
            var tag = match[0].replace(/</g, '').replace(/>/g, '');
            if (tag.toLowerCase() === 'body') {
                var dom = document.implementation.createDocument('http://www.w3.org/1999/xhtml', 'html', null);
                var body = document.createElement("body");
                // keeping the attributes
                element.innerHTML = html.replace(/<body>/g, '<div>').replace(/<\/body>/g, '</div>');
                var _attrs = element.firstChild.attributes;
                body.innerHTML = html;
                for (var i = 0; i < _attrs.length; i++) {
                    body.setAttribute(_attrs[i].name, _attrs[i].value);
                }
                return body;
            } else {
                var map = wrapMap[tag] || wrapMap._default,
                    element;
                html = map[1] + html + map[2];
                element.innerHTML = html;
                // Descend through wrappers to the right content
                var j = map[0] + 1;
                while (j--) {
                    element = element.lastChild;
                }
            }
        } else {
            element.innerHTML = html;
            element = element.lastChild;
        }
        return element;
    }

    /**
     * 获取和设置HTMLElement的内部HTML
     * @param  {[type]} elem  [description]
     * @param  {[type]} value [description]
     * @return {[type]}       [description]
     */
    this.html = function(elem, value) {
        // Don't set styles on text and comment nodes
        if (!elem || elem.nodeType === 3 || elem.nodeType === 8 || !("innerHTML" in elem)) {
            return this;
        }

        if (value !== undefined) {
            //防止出现内存溢出问题
            if (elem.childNodes.length) {
                var node = elem.firstChild;

                while (node) {
                    elem.removeChild(node);

                    node = elem.firstChild;
                }
            }

            elem.innerHTML = value;

            return this;
        }
        return elem.innerHTML;
    };

    this.append = function(container, html) {
        if (!html || !container || container.nodeType || container.nodeType == 3 || elem.nodeType === 8 || !("innerHTML" in elem))
            return this;

        if (coreMod.isString(html)) {
            html = strToHTMLElement(html);
        }
        container.appendChild(html);
    }

    var scrollCache;

    /**
     * 获取当前浏览器滚动条的宽度(分水平和垂直两部分)
     * @param  {[type]} context [description]
     * @return {[type]}         [description]
     */
    this.scrollBarWH = function(context) {

        !context && (context = doc);

        if (scrollCache) return scrollCache;

        var testDiv = context.createElement("div");

        testDiv.style.cssText = "overflow:scroll;width:100px;height:100px;";

        if (context.body) {
            context.body.appendChild(testDiv);
        }
        scrollCache = {
            horizontal: testDiv.offsetHeight - testDiv.clientHeight,
            vertical: testDiv.offsetWidth - testDiv.clientWidth
        };
        if (context.body) {
            context.body.removeChild(testDiv);
        }
        return scrollCache;
    };

    /**
     * 获取指定控件的border四个方位的宽度和长度
     * @param  {[type]} element [description]
     * @return {[type]}         [description]
     */
    this.borderMeta = function(element) {

        var bT = this.css(element, "borderTopWidth");
        var bB = this.css(element, "borderBottomWidth");
        var bL = this.css(element, "borderLeftWidth");
        var bR = this.css(element, "borderRightWidth");

        return {
            top: parseInt(bT.replace("px", "") || 0, 10),
            bottom: parseInt(bB.replace("px", "") || 0, 10),
            left: parseInt(bL.replace("px", "") || 0, 10),
            right: parseInt(bR.replace("px", "") || 0, 10)
        }
    }

    /**
     * 获取滚动条的left,top，width,height
     * @param  {[type]} doc [description]
     * @return {[type]}     [description]
     */
    this.scrollMeta = function(context) {
        var dom = context.documentElement && context.documentElement.scrollTop ? context.documentElement : context.body;

        return {
            left: dom.scrollLeft,
            top: dom.scrollTop,
            width: dom.scrollWidth,
            height: dom.scrollHeight
        };
    };

    /**
     * 获取当前面板的最大可是区域的长和宽
     * @param  {[type]}  context              [description]
     * @param  {Boolean} isWithOutScrollWidth [description]
     * @return {[type]}                       [description]
     */
    this.documentWH = function(context, isWithOutScrollWidth) {
        var scrollWH;

        !context && (context = doc);

        var target = context.documentElement || context.body;

        if (isWithOutScrollWidth) {
            return {
                width: target.clientWidth,
                height: target.clientHeight
            };
        }

        scrollWH = this.scrollMeta(doc);

        if (typeof window.innerWidth != "undefined") {

            return {
                width: window.innerWidth + scrollWH.width,
                height: window.innerHeight + scrollWH.height
            };
        }

        if (!target.clientWidth) {
            return {
                width: target.offsetWidth + scrollWH.width,
                height: target.offsetHeight + scrollWH.height
            };
        }
        return {
            width: target.clientWidth + scrollWH.width,
            height: target.offsetHeight + scrollWH.height
        };
    };

    /**
     * 格式化长度和高度的值
     * @param  {[type]} wh [description]
     * @return {[type]}    [description]
     */
    this.formatWH = function(wh) {
        if (!wh) return "auto";
        return !isNaN(wh) ? wh + "px" : wh;
    };
})
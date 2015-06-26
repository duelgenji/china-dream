! function(global) {

    /**
     * author: 		zpli
     * english: 	buns
     * 	 date: 		2014-12-31
     * version: 	0.0.1
     * description:
     * 	兼容性: ie6及其以上版本的IE,chrome,firefox,opera
     * 	特色：
     * 		1.内部除了基本的html模板之外不包含任何关于样式的设置，全部采用class的占位方式----尽量做到与样式的分离，做到UI级别的纯净
     * 		2.内容的所有事件调用全部采用订阅的方式进行,除了维持基本的字段类型Map和基本的基于普遍的DataTale类型的JSON数据的处理
     * 			2.1 开发人员可以通过订阅主题的方式，添加或者重写对应主题
     *        ----尽量做到与逻辑的分离做到逻辑处理级别的纯净
     *      3.支持AMD和配置路径映射的CMD风格的模块化
     *      4.支持一个页面多个grid显示，并能够提供开发者自定义key来进行快速的查找其他grid
     *      5.接口十分简单
     * 	note:
     * 	   a. 关于grid的数据处理 只提供四个最基本的 grid.getDataLength, grid.getRowColsValues,grid.addNew,grid.removeData的主题，
     * 		  如果开发人员提供的数据比较特殊，请确保调用pure.override重写该主题;
     * 	   b. 组件是基本pure-lib.js的基本库进行编写，如需要单独使用，请将两个js合并即可
     */
    function factory() {

        var doc = global.document;

        /**
         * grid的基本模板
         * @param  {[type]} require [description]
         * @return {[type]}          [description]
         */
        pure.define("pure-grid.template", function(require) {

            var core = require("pure-core");

            var _renderTo, _hasLock, _isShowHead, _emptyHtml;

            var mainTmpl = '<div style="position:relative;overflow:hidden;">%1 %2 %3</div>'

            var wrapperTmpl = '<div id="%1_%2_WrapperDiv" style="z-index:3;position:relative;float:left;overflow:hidden;"> %3 %4 </div>'

            var headTmpl = '<div id="%1_%2_HeadDiv" class="ui-grid-%2_headDiv" style="z-index:2;position:relative;"><table id="%1_%2_HeadTable" class="ui-grid-%2_headTable" style="position:relative;table-layout:fixed;" cellSpacing="0" cellPadding="0"><thead></thead></table></div>'

            var bodyTmpl = '<div id="%1_%2_BodyDiv" class="ui-grid-%2_bodyDiv" style="display:none;position:relative;%3"><table id="%1_%2_BodyTable" class="ui-grid-%2_bodyTable" style="position:relative;table-layout:fixed;" cellSpacing="0" cellPadding="0"><tbody></tbody></table></div>'

            var emptyTmpl = '<div id="%1_empty" class="ui-grid-empty" style="clear:both;display:none;">%2</div>'

            var output = {
                getTemplate: function() {
                    var flag, head, body, lockWrapper, unlockWrapper, overflow;

                    if (_hasLock) {

                        body = core.format(bodyTmpl, _renderTo, flag = "lock", "");

                        _isShowHead && (head = core.format(headTmpl, _renderTo, flag));

                        lockWrapper = core.format(wrapperTmpl, _renderTo, flag, head || '', body);

                        overflow = "overflow:auto;"
                    }

                    body = core.format(bodyTmpl, _renderTo, flag = "unlock", overflow || "overflow-x:hidden;overflow-y:auto;");

                    _isShowHead && (head = core.format(headTmpl, _renderTo, flag));

                    unlockWrapper = core.format(wrapperTmpl, _renderTo, flag, head || '', body);

                    return core.format(mainTmpl, lockWrapper || '', unlockWrapper || '', core.format(emptyTmpl, _renderTo, _emptyHtml)) //!isneedEmpty ? '' : 
                },
                getDomId: function(LR, position, type) {


                    return position == "empty" ? (_renderTo + "_" + position) : [_renderTo, "_", LR == "L" ? "lock" : "unlock", "_", position || "head", type || "Div"].join('')
                }
            }

            this.init = function(target, islock, isShowHead, emptyHtml) {

                _renderTo = target;

                _hasLock = islock;

                _isShowHead = isShowHead;

                _emptyHtml = emptyHtml;

                return output
            }
        });

        /**
         * grid的相关dom操作所需的辅助方式（不包含逻辑构建的）—
         * @param  {[type]} require [description]
         * @return {[type]}          [description]
         */
        pure.define("pure-grid.domUtils", function(require) {

            var jDom = require("pure-jsDom")

            /**
             * 判断当前浏览器是否是IE8以下的版本以及IE8(Quirks)，
             * 	只有这几个版本才完全支持Colgroup和col,避免给每个td设置宽度,提升部分效率;
             * @return {Boolean} [description]
             */
            this.isFullSupportCol = function() {
                if (jDom.isIEBelow9) {
                    var ieV = jDom.browser.ie;
                    // judge cc196988urrent internet version is IE5/6/7/8(Quirks mode)
                    // refer : http://msdn.microsoft.com/en-us/library/cc196988(VS.85).aspx
                    return (ieV < 8 || (ieV == 8 && jDom.browser.isQuirksMode))
                }
                return false
            }

            /**
             * 构建排序图像
             * @param  {[type]} sortcss [description]
             * @return {[type]}         [description]
             */
            this.buildSortImage = function(sortcss) {
                var img = doc.createElement("img")

                img.id = "img_sort";

                img.setAttribute("upsrc", sortcss.upImage);

                sortcss.upPosition && img.setAttribute("uppos", sortcss.upPosition);

                img.setAttribute("downsrc", sortcss.downImage);

                sortcss.downPosition && img.setAttribute("downpos", sortcss.downPosition);

                jDom.css(img, {
                    display: 'none',
                    width: sortcss.width || 15,
                    height: sortcss.height || 15
                });

                return img
            }

            /**
             * 构建Colgroup HTMLElement
             * @param  {[type]}   meta [长度和宽度的对象]
             * @return {[type]}        [description]
             */
            this.buildColGroup = function(meta) {
                var colG = doc.createElement("colgroup"),
                    col;
                for (var i = 0, m; m = meta[i++];) {
                    col = doc.createElement("col");
                    m.width && col.setAttribute("width", jDom.formatWH(m.width));
                    col.setAttribute("align", m.align || "center");
                    colG.appendChild(col);
                }
                return colG
            }

            /**
             * 向下查找首个指定tag类型的DOM元素
             * @param  {[type]} container [description]
             * @param  {[type]} tag       [description]
             * @return {[type]}           [description]
             */
            this.findDom = function(container, tag) {

                if (container.tagName === tag) return container;

                container = container.parentNode;

                while (container && container.tagName !== tag) {

                    container = container.parentNode
                }

                return container
            }

            this.formatWHToInt = function(wh, compareWH) {
                if (!wh) return compareWH;
                if (!isNaN(wh)) {
                    wh = wh.toString();
                    return wh.indexOf('%') > -1 ? (parseFloat(wh) / 100) * compareWH : parseInt(wh.replace("px", ""), 10)
                }
            }
        })

        /**
         * grid的默认对象 主题，事件等
         * @param  {[type]} require [description]
         * @return {[type]}          [description]
         */
        pure.define("pure-grid.default", function(require) {

            var jDom = require("pure-jsDom")

            var that = this;

            /**
             * 默认的主题样式名称
             * @type {Object}
             */
            this.theme = {
                th: "ui-grid-th",
                clickTr: "ui-grid-clickTr",
                dbclickTr: "ui-grid-dbclickTr",
                mouseoverTr: "ui-grid-mouseoverTr",
                oddTr: "ui-grid-oddTr",
                evenTr: "ui-grid-evenTr",
                oddTd: "ui-grid-oddTd",
                evenTd: "ui-grid-evenTd",
                contentDiv: "ui-grid-contentDiv"
            }

            /**
             * 默认事件处理
             * @type {Object}
             */
            this.dftEvts = {
                /**
                 * 排序时候的比较函数
                 * @param  {[type]}  cellIndex [description]
                 * @param  {[type]}  type      [description]
                 * @param  {Boolean} isAsc     [description]
                 * @param  {[type]}  LR        [description]
                 * @param  {[type]}  convertFn [description]
                 * @return {[type]}            [description]
                 */
                sort_compare: function(cellIndex, type, isAsc, LR, convertFn) {
                    return function(tr1, tr2) {
                        var td1 = tr1[LR].cells[cellIndex],
                            td2 = tr2[LR].cells[cellIndex];
                        var v1 = convertFn(type, td1.getAttribute("sortval") || td1.firstChild.firstChild.nodeValue),
                            v2 = convertFn(type, td2.getAttribute("sortval") || td2.firstChild.firstChild.nodeValue);
                        if (v1 === v2) return 0;
                        if (isAsc) return v1 > v2 ? 1 : -1;
                        return v1 > v2 ? -1 : 1
                    }
                }
            }

            /**
             * 默认配置项
             * @type {Object}
             */
            this.config = {
                /**
                 * 开发者自定义给当前grid的一个关键字，此作用是当页面存在多个grid的时候的快捷查找
                 * @type {String}
                 */
                key: 'grid_' + +new Date,

                /**
                 * 指定显示在哪个HTMLElement下
                 * @type {String}
                 */
                renderTo: '',

                /**
                 * 显示宽度
                 * @type {String}
                 */
                width: '100%',

                /**
                 * 数据为空的时候显示的内容
                 * @type {String}
                 */
                emptyText: '暂无相应数据',

                /**
                 * 是否显示表头
                 * @type {Boolean}
                 */
                isShowHead: true,

                /**
                 * 是否支持点击行变色
                 * @type {Boolean}
                 */
                isSupportClick: false,

                /**
                 * 是否支持鼠标行表色
                 * @type {Boolean}
                 */
                isSupportMouseSelect: false,

                /**
                 * 是否支持双击行
                 * @type {Boolean}
                 */
                isSupportDbClick: false,

                /**
                 * 锁定列的序号
                 *  此设置的数组形式：数组内的值为columns设置的最外层的index
                 *
                 *  例如： columns:[
                 *  		{
                 *  			index:0
                 *  		},
                 *  		{
                 *  			index:1
                 *  		}
                 *  	]
                 *
                 *   	lockColumnIndexs:[0] //此处0表示的是index=0的columns项
                 *
                 *  如果columns中元素项没有设置index属性,那么 则会默认按顺序设置index
                 *
                 * @type {[Array]}
                 */
                lockColumnIndexs: null,

                /**
                 * 数据列配置
                 * @type {[type]}
                 */
                columns: null,

                /**
                 * 锁定列的总宽度
                 * @type {Number}
                 */
                lockWidth: 0,

                /**
                 * 未锁定列的总宽度
                 * @type {Number}
                 */
                unlockWidth: 0,

                /**
                 * 事件绑定
                 *  默认： 都是进行样式的添加和删除
                 * @type {Object}
                 */
                events: {
                    /**
                     * 行点击事件
                     * @return {[type]} [description]
                     */
                    click: function() {
                        jDom.toggleClass(this, that.theme.clickTr)
                    },
                    /**
                     * 行双击事件
                     * @return {[type]} [description]
                     */
                    dbclick: function() {
                        jDom.toggleClass(this, that.theme.dbclickTr)
                    },
                    /**
                     * 鼠标滑过事件
                     * @return {[type]} [description]
                     */
                    mouseover: function() {
                        jDom.toggleClass(this, that.theme.mouseoverTr)
                    },
                    /**
                     * 鼠标划出事件
                     * @return {[type]} [description]
                     */
                    mouseout: function() {
                        jDom.toggleClass(this, that.theme.mouseoverTr)
                    }
                },
                /**
                 * 用于排序时候的图片样式设置
                 * @type {Object}
                 */
                sortCss: {
                    /**
                     * 升序排列的图片地址
                     * @type {String}
                     */
                    upImage: '',
                    /**
                     * 降序排列的图片地址
                     * @type {String}
                     */
                    downImage: '',
                    /**
                     * 升序排列图片位置 针对png图片可以进行position的偏移
                     * @type {String}
                     */
                    upPosition: '',
                    /**
                     * 降序排列图片位置 针对png图片可以进行position的偏移
                     * @type {String}
                     */
                    downPosition: '',
                    /**
                     * 图片宽度
                     * @type {Number}
                     */
                    width: 10,
                    /**
                     * 图片高度
                     * @type {Number}
                     */
                    height: 10
                },
                /**
                 * 分页项设置
                 * @type {Object}
                 */
                pages: {
                    renderCall: null
                }
            }
        })

        pure.define("pure-grid.pubSub", function(require) {

            var pubSubMod = require("pure-pubSub").register("pure-grid");

            function pubSub(key) {
                this.key = key;
            }
            pubSub.prototype = {
                constructor: pubSub,
                on: function(cmd, callback) {
                    pubSubMod.on(this.key + cmd, callback);
                    return this;
                },
                override: function(cmd, callback) {
                    pubSubMod.override(this.key + cmd, callback);
                    return this;
                },
                off: function(cmd, callback) {
                    pubSubMod.off(this.key + cmd, callback);
                    return this;
                },
                emit: function(name, argus, isUseApply, isNeedResult, scope) {
                    var a = pubSubMod.emit(name, argus, isUseApply, isNeedResult, scope);
                    return isNeedResult ? a : this;
                }
            }

            return pubSub;
        });

        /**
         * grid的构建内容
         *   主要生成对象
         * @param  {[type]} require [description]
         * @return {[type]}          [description]
         */
        pure.define("pure-grid.builder", function(require) {

            var
                _isSupportCol,

                gTmpl,

                evts,

                core = require("pure-core"),

                jDom = require("pure-jsDom"),

                pubSubMod = require("pure-pubSub").register("pure-grid"),

                gDft = require("pure-grid.default"),

                gDom = require("pure-grid.domUtils"),

                $$ = jDom.byId;


            function gBuilder(options) {

                this.cfgs = options;

                this._recombineCol(options.columns, options.lockColumnIndexs)
            }

            /**
             * 递归遍历外部提供的列配置，拆分得到4个关键配置
             * 	1. heads :表头配置信息，
             * 	2. body : 内容行配置信息，
             * 	3. headwhs :表头高度和宽度配置信息，
             * 	4. bodywh :数据行高度和宽度配置信息，
             * @param  {[Array]} heads   [表头列配置]
             * @param  {[Array]} body    [数据行配置]
             * @param  {[Array]} headwhs [表头各个单元格的高度和宽度配置]
             * @param  {[Array]} bodywh  [数据行各个单元格的高度和宽度配置]
             * @param  {[Array]} cols    [当前分析的列配置]
             * @param  {[Number]} depth   [递归深度]
             */
            gBuilder.convertCfg = function(heads, body, headwhs, bodywh, cols, depth) {
                var trEntry = heads[depth] || (heads[depth] = []);

                var preColIndex = 0;

                for (var i = 0, c; c = cols[i++];) {
                    trEntry.push(c.head);
                    !c.head.align && (c.head.align = "center");
                    if (c.body) {
                        _isSupportCol === false && !c.body.width && (c.body.width = c.head.width);

                        body.push(c.body);

                        bodywh.push({
                            align: c.body.align || "left",
                            width: c.body.width || c.head.width
                        })
                    }
                    c.head.colIndex = preColIndex;

                    if (c.cols && c.cols.length) {

                        preColIndex += c.cols.length;

                        gBuilder.convertCfg(heads, body, headwhs, bodywh, c.cols, depth + 1)
                    } else {

                        preColIndex += 1;

                        headwhs.push({
                            align: c.head.align || "center",
                            width: c.head.width
                        })
                    }
                }
            }

            /**
             * 插件的原型方法
             * @type {Object}
             */
            gBuilder.prototype = {
                constructor: gBuilder,
                /**
                 * 将开发人员提供的COlumns配置项进行重组
                 * @param  {[type]} cols       [列配置]
                 * @param  {[type]} lockIndexs [锁定列在列配置项的对应索引数组]
                 * @return {[type]}            [description]
                 */
                _recombineCol: function(cols, lockIndexs) {
                    var sortedCols = [];

                    for (var i = cols.length, c, j; c = cols[--i];) {
                        j = c.index ? parseInt(c.index, 10) : i;
                        j >= 0 ? (j -= 1) : (j = 0);
                        sortedCols[j] = c
                    }

                    if (_isSupportCol == undefined) _isSupportCol = gDom.isFullSupportCol();

                    if (lockIndexs && lockIndexs.length) {
                        var lockcols = [];

                        for (i = lockIndexs.length; i -= 1, i >= 0;) {
                            lockcols[i] = sortedCols.splice(lockIndexs[i], 1)[0]
                        }

                        gBuilder.convertCfg(this._LHead = [], this._LBody = [], this._LHeadWHS = [], this._LBodyWHS = [], lockcols, 0)
                    }

                    gBuilder.convertCfg(this._RHead = [], this._RBody = [], this._RHeadWHS = [], this._RBodyWHS = [], sortedCols, 0)
                },
                /**
                 * 将Td或者Th的配置内容进行转化
                 * 	主要获取td内将要显示的内容
                 * note:此处调用了已订阅主题的发布
                 * @param  {[type]} meta [description]
                 * @param  {[type]} ri   [description]
                 * @param  {[type]} ci   [description]
                 * @return {[type]}      [description]
                 */
                ___convertTd: function(meta, ri, ci) {
                    var g = this,
                        m = meta;

                    if (ri >= 0) {
                        meta.className = (ri % 2 == 0 ? gDft.theme.evenTd : gDft.theme.oddTd);

                        meta.domtype = "td"

                    } else {

                        meta.domtype = "th";

                        meta.className = gDft.theme.th;

                        meta.colIndex = m.colIndex || ci
                    }

                    //meta.type = m.type;

                    if (m.text) {

                        meta.title = m.text;

                        return meta
                    }

                    var valArr = null,
                        value, valobj = {};

                    m.fields && (valArr = pubSubMod.emit("grid.getRowColsValues", [g.data, ri, m.fields], true, true));

                    if (!g.isEmpty && m.issort != false) {
                        if (m.sortfield) {
                            meta.sortval = (valobj.sortval = pubSubMod.emit("grid.getRowColsValues", [g.data, ri, m.sortfield], true, true).join(''))
                        } else if (valArr) {
                            meta.sortval = core.isArray(valArr) ? valArr.join('') : valArr
                        }
                    }

                    if (m.renderFn) {

                        var fnValue = valArr && valArr.length == 1 ? valArr[0] : valArr;

                        value = core.isFunction(m.renderFn) ? m.renderFn(fnValue, ri, valobj) : pubSubMod.emit(g.cfgs.key + m.renderFn, [fnValue, ri, valobj], true, true);

                        meta.sortval = valobj.sortval || meta.sortval;

                        meta.title = (typeof valobj.title == "undefined") ? (m.fields ? value : "") : valobj.title;

                    } else {
                        meta.title = (value = pubSubMod.emit(m.type || "string", [valArr], true, true))
                    }

                    meta.value = value;

                    return meta
                },
                /**
                 * 构建td或者th
                 * @param  {[Object]} meta [单元格的配置项]
                 * @param  {[String]} LR   [是锁定列的还是非锁定列的]
                 * @return {[type]}      [description]
                 */
                ___buildTd: function(meta, LR) {
                    var m = meta,
                        td = doc.createElement(m.domtype || "td");

                    td.className = m.className;

                    td.setAttribute("align", m.align || "left");

                    m.rs && td.setAttribute("rowSpan", parseInt(m.rs, 10));

                    m.cs && td.setAttribute("colSpan", parseInt(m.cs, 10));

                    if (m.colIndex >= 0) {
                        td.setAttribute("ci", m.colIndex);

                        td.setAttribute("LR", LR || "R");

                        m.sortType && td.setAttribute("sType", m.sortType)
                    }

                    var div = doc.createElement("div");

                    div.className = gDft.theme.contentDiv;
                    //div.style.cssText = "text-overflow:ellipsis;overflow:hidden;white-space:nowrap;";
                    div.innerHTML = '<span class="ui-grid-contentDiv-span">' + (m.text || m.value) + '</span>';
                    div.setAttribute("title", m.title);

                    td.appendChild(div);

                    m.width && (div.style.width = td.style.width = jDom.formatWH(m.width));

                    if (!this.isEmpty && m.issort != false) {

                        m.sortval && td.setAttribute("sortval", m.sortval);

                        m.domtype == "th" && (td.style.cursor = "pointer")
                    } else {
                        td.setAttribute("issort", m.issort)
                    }
                    return td
                },
                /**
                 * 构建数据行或表头行
                 * @param  {[Object]} meta [行配置项]
                 * @param  {[Number]} ri   [行索引]
                 * @param  {[String]} LR   [是锁定列的还是非锁定列的]
                 * @return {[type]}      [description]
                 */
                ___buildTr: function(meta, ri, LR) {
                    var g = this,
                        m = meta,
                        tr = doc.createElement("tr");

                    if (ri >= 0) {

                        tr.className = ri % 2 == 0 ? gDft.theme.evenTr : gDft.theme.oddTr;

                        tr.setAttribute("index", ri)
                    }

                    for (var i = 0, len = m.length; i < len; i += 1) {
                        tr.appendChild(g.___buildTd(g.___convertTd(m[i], ri, i), LR))
                    }

                    return tr
                },
                /**
                 * 填充表头行
                 * @param  {[DOM]} trWrapper [表头行所在的DOM元素--默认都是THead]
                 * @param  {[Array]} trcfgs    [表头行配置项数组]
                 * @param  {[String]} LR        [是锁定列的还是非锁定列的]
                 * @return {[type]}           [description]
                 */
                __fillHeadTr: function(trWrapper, trcfgs, LR) {
                    for (var i = 0, trcfg; trcfg = trcfgs[i++];) {
                        trWrapper.appendChild(this.___buildTr(trcfg, undefined, LR))
                    }
                },
                /**
                 * 填充数据显示区域的数据行
                 * @param  {[DOM]} trWrapper [数据行所在的DOM元素--默认都是TBody]
                 * @param  {[Array]} trcfgs    [数据行配置项数组]
                 * @param  {[String]} LR        [是锁定列的还是非锁定列的]
                 * @param  {[String]} position  [数据时追加的以及追加的位置是top还是bottom]
                 * @return {[type]}           [description]
                 */
                __fillBodyTr: function(trWrapper, trcfgs, LR, position) {
                    var g = this,
                        objs,
                        i,
                        tr,
                        obj,
                        l = g.datalen;

                    !this._sortTrs && (this._sortTrs = {
                        trs: []
                    });

                    objs = g._sortTrs;

                    objs[LR + "TBody"] = trWrapper;

                    if (position == "top") {

                        l = g.datalen - (g.pre_datalen || 0);

                        var curFirstTr;

                        if (objs.trs.length < g.datalen) {

                            curFirstTr = objs.trs[0][LR];

                            g._sortTrs.trs = new Array(l).concat(objs.trs)

                        } else {
                            curFirstTr = objs.trs[l][LR]
                        }

                        for (i = 0; obj = objs.trs[i], i < l; i += 1) {

                            tr = g.___buildTr(trcfgs, i, LR);

                            trWrapper.insertBefore(tr, curFirstTr);

                            obj ? obj[LR] = tr : (objs.trs[i] = {}, objs.trs[i][LR] = tr)
                        }

                        for (i = l; i < g.datalen && (obj = objs.trs[i++]);) {
                            obj[LR].setAttribute("index", i)
                        }

                        return
                    }
                    for (i = g.pre_datalen || 0; obj = objs.trs[i], i < l; i += 1) {

                        tr = g.___buildTr(trcfgs, i, LR);

                        obj ? obj[LR] = tr : (objs.trs[i] = {}, objs.trs[i][LR] = tr);

                        trWrapper.appendChild(tr)
                    }
                },
                /**
                 * 给grid添加一些事件处理
                 * 	note:此处利用事件的冒泡处理来节省内存
                 * @return {[type]} [description]
                 */
                _attachEvent: function() {
                    var g = this,
                        cfgs = g.cfgs,
                        gEvtsCfg = g.cfgs.events;

                    !evts && (evts = require("pure-event"));

                    function getFullRowEventAttach(fn) {
                        var _g = this,
                            trs = _g._sortTrs.trs;

                        return function(e) {
                            var tr = gDom.findDom(e.target, "TR");
                            if (tr) {
                                var index = tr.getAttribute("index");

                                trs[index].L && fn.call(trs[index].L, e);

                                fn.call(trs[index].R, e);
                            }
                        };
                    }

                    var objs = [];

                    /**
                     * 是否支持单击事件
                     * @param  {[type]} cfgs.isSupportClick [description]
                     * @return {[type]}                     [description]
                     */
                    if (cfgs.isSupportClick) {

                        objs[0] = $$(g._RBodyTbId);

                        g._LBodyTbId && (objs[1] = $$(g._LBodyTbId));

                        evts.on(objs, "click", (function(_g) {
                            return getFullRowEventAttach.call(_g, gEvtsCfg.click);
                        }(g)));
                    }
                    /**
                     * 是否支持双击事件
                     */
                    if (cfgs.isSupportDbClick) {
                        if (objs.length == 0) {

                            objs[0] = $$(g._RBodyTbId);

                            g._LBodyTbId && (objs[1] = $$(g._LBodyTbId));
                        }

                        evts.on(objs, "dbclick", (function(_g) {
                            return getFullRowEventAttach.call(_g, gEvtsCfg.dbclick);
                        }(g)));
                    }

                    /**
                     * 是否支持鼠标划入划出
                     */
                    if (cfgs.isSupportMouseSelect) {

                        if (objs.length == 0) {

                            objs[0] = $$(g._RBodyTbId);

                            g._LBodyTbId && (objs[1] = $$(g._LBodyTbId));
                        }
                        /**
                         * ie下利用mouseenter 和mouseleave解决mouseover和mouseout的bug
                         */
                        evts.on(objs, jDom.isIE ? "mouseenter" : "mouseover", (function(_g) {
                                return getFullRowEventAttach.call(_g, gEvtsCfg.mouseover)
                            }(g)))
                            .on(objs, jDom.isIE ? "mouseleave" : "mouseout", (function(_g) {
                                return getFullRowEventAttach.call(_g, gEvtsCfg.mouseout)
                            }(g)));
                    }

                    /**
                     * 滚动条滚动事件
                     */
                    var obj = $$(g._RBodyDivId || (g._RBodyDivId = gTmpl.getDomId("R", "Body", "Div")))
                    evts.on(obj, "scroll", (function(_this, rhead, ltb) {
                        return function(e) {
                            ltb && (ltb.style.top = (-_this.scrollTop) + "px");

                            rhead && (rhead.style.left = (-_this.scrollLeft) + "px");

                            pubSubMod.emit(cfgs.key + "grid.scrolled", _this)
                        }
                    }(obj, $$(g._RHeadDivId || (g._RHeadDivId = gTmpl.getDomId("R", "Head", "Div"))), $$(g._LBodyTbId || (g._LBodyTbId = gTmpl.getDomId("L", "Body", "Table"))))))

                    /**
                     * 表头排序事件
                     */
                    if (cfgs.isShowHead) {

                        objs[0] = $$(g._RHeadTbId);

                        g._LHeadTbId && (objs[1] = $$(g._LHeadTbId));

                        evts.on(objs, "click", (function(_g) {
                            return function(e) {
                                var dom = gDom.findDom(e.target, "TH"),
                                    sortTrs = _g._sortTrs;

                                function valueConvertFn(type, value) {
                                    return pubSubMod.emit(type, [value], true, true)
                                }

                                function sort(th) {
                                    var LR = th.getAttribute("LR"),
                                        ci = th.getAttribute("ci"),
                                        orderby = th.getAttribute("order") || "asc",
                                        sorttype = th.getAttribute("sType") || "string",
                                        c = sortTrs.trs,
                                        innerDiv = th.childNodes[0];

                                    pubSubMod.emit(cfgs.key + "grid.beforeSort", th);

                                    var img = innerDiv.lastChild;

                                    (img.tagName != "IMG") && innerDiv.appendChild(img = _g.sortImageDom);

                                    img.src = orderby == "desc" ? img.getAttribute("upsrc") : img.getAttribute("downsrc");

                                    var backgroundPosition = orderby == "desc" ? img.getAttribute("uppos") : img.getAttribute("downpos");

                                    if (backgroundPosition) {
                                        img.style.backgroundPosition = backgroundPosition;
                                    }

                                    img.style.display = "";

                                    c.sort(gDft.dftEvts.sort_compare(ci, sorttype, orderby === "desc", LR, valueConvertFn));

                                    th.setAttribute("order", orderby === "desc" ? "asc" : "desc");

                                    var frag = doc.createDocumentFragment(),
                                        k, l = c.length,
                                        temp;

                                    if (LR === "L" || _g._LHeadTbId) {
                                        var frag2 = doc.createDocumentFragment();

                                        for (k = 0; k < l; k++) {

                                            (temp = c[k]).R.index = (temp.L.index = k);

                                            frag.appendChild(temp.R);

                                            frag2.appendChild(temp.L)
                                        }

                                        sortTrs.LTBody.appendChild(frag2);

                                        sortTrs.RTBody.appendChild(frag)

                                    } else {
                                        for (k = 0; k < l; k++) {

                                            (temp = c[k][LR]).index = k;

                                            frag.appendChild(temp)
                                        }

                                        sortTrs.RTBody.appendChild(frag)
                                    }

                                    pubSubMod.emit(cfgs.key + "grid.sorted", th)
                                }

                                if (dom) {
                                    var isSort = dom.getAttribute("issort");

                                    isSort != "false" && isSort != false && sort(dom);
                                }

                            }
                        }(g)))
                    }
                    return g;
                },
                /**
                 * 当有数据展示的时候如果根据当前配置项来控制组件展示的高度和欢度
                 * 	note: 要先了解下 pure-grid.template中的html模板
                 * 		其次在计算的时候主要是利用DOM元素的offsetHeight 和 offsetWidth来进行
                 * @param {[type]} mainDom [description]
                 */
                _setStyle: function(mainDom) {
                    var
                        g = this,

                        dom,

                        dom2,

                        width,

                        height,

                        tableHeight,

                        whFn = jDom.formatWH,

                        whFn2 = gDom.formatWHToInt,

                        headHeight = 0,

                        isShowHead = g.cfgs.isShowHead,

                        cfgHeight = g.cfgs.height;

                    if (isShowHead) {

                        dom = $$(g._RHeadTbId);

                        dom.style.width = whFn(dom.offsetWidth);

                        headHeight = dom.parentNode.offsetHeight
                    }

                    dom = $$(g._RBodyTbId);

                    dom.style.width = whFn(dom.offsetWidth);

                    tableHeight = dom.offsetHeight;

                    dom = dom.parentNode;

                    var borderWidthObj = jDom.borderMeta(dom);

                    if (g._LHead) {

                        dom2 = $$(g._LBodyTbId);

                        var width2 = whFn2(g.cfgs.lockWidth, dom2.offsetWidth);

                        dom2.style.width = (dom2.parentNode.style.width = whFn(width2));

                        if (isShowHead) {

                            dom2 = $$(g._LHeadTbId);

                            dom2.style.width = whFn(dom2.offsetWidth)
                        }

                        width = whFn2(g.cfgs.unlockWidth, dom.offsetWidth);

                        dom.style.width = (dom.parentNode.style.width = whFn(width));

                        width += width2 + (borderWidthObj.left || borderWidthObj.right) * 3;

                        mainDom.style.width = whFn(width);

                        height = whFn2(cfgHeight, mainDom.offsetHeight);

                        if (!cfgHeight && height > (tableHeight + headHeight)) {
                            height = tableHeight + headHeight
                        }

                        mainDom.style.height = height;

                        dom.style.height = whFn(height - headHeight);

                        return
                    }

                    height = whFn2(cfgHeight, mainDom.offsetHeight);

                    mainDom.style.height = whFn(height);

                    mainDom.style.width = g.cfgs.width;

                    dom.style.height = whFn(height - headHeight);

                    dom.style.width = (dom.parentNode.style.width = whFn(dom.childNodes[0].offsetWidth));

                    return g
                },
                /**
                 * 数据为空的时候进行插件的高度和宽度的控制
                 * @param {[type]} mainDom [description]
                 */
                _setStyle2: function(mainDom) {
                    var g = this,
                        fn = jDom.formatWH;

                    var dom = $$(g._emptyNodeId || (g._emptyNodeId = gTmpl.getDomId(null, "empty")));

                    if (g.cfgs.isShowHead) {
                        var width = 0,
                            dom2;
                        if (g._LHeadTbId) {

                            dom2 = $$(g._LHeadTbId);

                            width = dom2.parentNode.offsetWidth
                        }

                        dom2 = $$(g._RHeadTbId);

                        width += dom2.parentNode.offsetWidth;

                        jDom.css(dom, {
                            width: mainDom.style.width = fn(width),
                            display: ''
                        });

                        return
                    }

                    dom.style.width = (mainDom.style.width = fn(g.cfgs.width));

                    return g
                },
                /**
                 * 呈现分页控件的html处理
                 * 	此处有点需要分页控件提供一个输出对应html的方法，并将此方法挂接到插件的pages配置项中的rendercall上
                 * @param  {[type]} container [description]
                 * @return {[type]}           [description]
                 */
                _renderPage: function(container) {
                    var g = this,
                        pages = g.cfgs.pages;

                    if (pages && pages.renderCall) {

                        div = doc.createElement("div");

                        div.style.cssText = "clear:both;";

                        div.id = g.tmplRenderTo + "_page";

                        div.innerHTML = pages.renderCall(g.datalen);

                        container.appendChild(div)
                    }
                    return g
                },
                _innerRender: function(LR, part, position, isRetry) {
                    var g = this,
                        lr = "_" + LR + part,
                        trWrapper,
                        table = $$(this[lr + "TbId"] = gTmpl.getDomId(LR, part, "Table"));

                    part == "Head" ? (trWrapper = table.tHead) && g.__fillHeadTr(trWrapper, g[lr], LR) : (trWrapper = table.tBodies[0]) && g.__fillBodyTr(trWrapper, g[lr], LR, position);

                    !isRetry && table.insertBefore(gDom.buildColGroup(g[lr + "WHS"]), trWrapper);

                    return g
                },
                /**
                 * 重新绑定的时候移除原来的数据行
                 *  note:在移除的时候需要将内容维护的一个排序数据行的对象同时清空
                 *          同时此处做了w3c和非w3c在清除tbody内的tr元素时候的兼容处理
                 * @return {[type]} [description]
                 */
                _removeTrs: function() {
                    var g = this,
                        sortTrObjs = g._sortTrs;

                    if (!sortTrObjs) {
                        return;
                    }

                    if (!jDom.isIE) {
                        jDom.html(sortTrObjs.RTBody, "")
                            .html(sortTrObjs.LTBody, "");

                    } else {

                        var trs = sortTrObjs.trs;

                        for (var i = 0, tr; tr = trs[i++];) {
                            for (var j in tr) {
                                sortTrObjs[j + "TBody"].removeChild(tr[j])
                            }
                        }
                    }
                    this._sortTrs.trs = [];
                },
                /**
                 * 插件呈现至浏览器上的逻辑处理函数
                 * @return {[type]} [description]
                 */
                render: function() {
                    var g = this,
                        isShowHead = g.cfgs.isShowHead,
                        obj = $$(g.cfgs.renderTo);

                    if (!obj) return;

                    gTmpl = require("pure-grid.template").init(g.tmplRenderTo, !!g._LHead, isShowHead, g.cfgs.emptyText);

                    jDom.css(obj, "display", "")
                        .html(obj, '' + gTmpl.getTemplate());

                    if (isShowHead) {

                        g._LHead && g._innerRender("L", "Head");

                        g._innerRender("R", "Head");

                        !g.isEmpty && obj.appendChild(this.sortImageDom = gDom.buildSortImage(g.cfgs.sortCss))
                    }

                    if (!g.isEmpty) {

                        if (g._LBody) {

                            g._innerRender("L", "Body");

                            $$(g._LBodyDivId || (g._LBodyDivId = gTmpl.getDomId("L", "Body"))).style.display = ""
                        }

                        g._innerRender("R", "Body");

                        $$(g._RBodyDivId || (g._RBodyDivId = gTmpl.getDomId("R", "Body"))).style.display = "";

                        g._attachEvent()._renderPage(obj)
                    }

                    jDom.css(obj, 'display', '');

                    g.isEmpty ? g._setStyle2(obj.childNodes[0]) : g._setStyle(obj.childNodes[0]);

                    pubSubMod.emit(g.cfgs.key + "grid.databound", g.data)
                },
                /**
                 * 重新绑定时候的呈现方法
                 * @return {[type]} [description]
                 */
                reRender: function() {
                    var g = this,
                        obj = $$(g.cfgs.renderTo);

                    g._removeTrs();

                    var emptyDisplay = g.isEmpty ? '' : 'none',
                        dataDisplay = g.isEmpty ? 'none' : '';

                    jDom.css($$(g._emptyNodeId || (g._emptyNodeId = gTmpl.getDomId(null, "empty"))), 'display', emptyDisplay)
                        .css($$(g._LBodyDivId || (g._LBodyDivId = gTmpl.getDomId("L", "Body"))), 'display', dataDisplay)
                        .css($$(g._RBodyDivId || (g._RBodyDivId = gTmpl.getDomId("R", "Body"))), 'display', dataDisplay);

                    g._LBody && g._innerRender("L", "Body", "bottom", true);

                    g._innerRender("R", "Body", "bottom", true)
                        ._setStyle(obj.childNodes[0]);

                    pubSubMod.emit(g.cfgs.key + "grid.databound", g.data)
                },
                /**
                 * 追加数据时候的呈现方法
                 * @param {[type]} position [description]
                 */
                addNewRender: function(position) {
                    var
                        g = this,

                        obj;

                    g._LBody && g._innerRender("L", "Body", position, true);

                    g._innerRender("R", "Body", position, true);

                    if (g.isEmpty) {

                        obj = $$(g.cfgs.renderTo),
                            $$(g._emptyNodeId).style.display = "none";

                        g._LBodyDivId && ($$(g._LBodyDivId).style.display = "");

                        $$(g._RBodyDivId).style.display = "";

                        obj = $$(g.cfgs.renderTo);

                        g._setStyle(obj.childNodes[0]);
                    }
                    pubSubMod.emit(g.cfgs.key + "grid.databound", g.data);
                },
                /**
                 * 移除数据的内部同步方法
                 * @param  {[type]} rowIndex [description]
                 * @return {[type]}          [description]
                 */
                removeSync: function(rowIndex) {
                    var g = this,

                        tr,

                        obj,

                        sortTrObjs = g._sortTrs;

                    if (!sortTrObjs) {
                        return;
                    }

                    obj = $$(g.cfgs.renderTo);

                    tr = sortTrObjs.trs[rowIndex];

                    for (var j in tr) {
                        sortTrObjs[j + "TBody"].removeChild(tr[j])
                    }

                    g._setStyle(obj.childNodes[0]);
                }
            }

            return gBuilder;
        })

        /**
         * 提供给开发人员调用的外部接口, 保证是面向接口的
         *   主要生成对象
         * @param  {[type]} require [description]
         * @return {[type]}          [description]
         */
        pure.define("pure-grid.output", function(require) {
            var
            /**
             * grid的构建模块对象
             */
                gridBuilder,
                /**
                 * 多个grid的缓存列表
                 * @type {Object}
                 */
                gridList = {},

                /**
                 * 多个grid的发布订阅对象缓存列表
                 * @type {Object}
                 */
                gridPubSubList = {},

                /**
                 * 当前grid实例
                 */
                currentGrid,

                /*
                 * grid的默认模块
                 */
                gDftMod = require("pure-grid.default"),

                /**
                 * pure插件的代码模块
                 * @type {[type]}
                 */
                coreMod = require("pure-core"),

                /**
                 * pure插件的发布模块
                 * @type {[type]}
                 */
                pubSubMod = require("pure-pubSub").register("pure-grid"),

                /**
                 * pure-grid插件的发布模块
                 * @type {[type]}
                 */
                gPubSubMod = require("pure-grid.pubSub");

            pubSubMod
                .empty()
                .override("grid.getDataLength", function(data) {
                    return !data ? 0 : data.length
                })
                .override("grid.addNew", function(oldData, newData) {
                    return newData ? oldData.concat(newData) : oldData
                })
                .override("grid.getRowColsValues", function(data, ri, cols) {
                    var colArr = cols.split(',')
                    for (var i = colArr.length; i;) {
                        colArr[(i -= 1)] = data[ri][colArr[i]]
                    }
                    return colArr.length > 0 ? colArr : colArr[0]
                })
                .override("grid.removeData", function(data, ri) {
                    data[0].splice(data[1], 1);
                })
                .override("int", function(val) {
                    return parseInt(val, 10)
                })
                .override("float", function(val) {
                    return parseFloat(val)
                })
                .override("date", function(val) {
                    return new Date().parse(val)
                })
                .override("string", function(val) {
                    return "" + val
                });

            var cid = 0;

            /**
             * 对外输出接口的方法对象定义
             * @param  {[type]} grid [description]
             * @return {[type]}      [description]
             */
            function grid(gridBuilder, key) {

                this.gb = gridBuilder;

                this.key = key;


                /**
                 * 是否已经绑定
                 * @type {Boolean}
                 */
                this._isBinded = false,

                    /**
                     * 此属性是用来在构造模板的时候模板内的div的ID前缀.
                     * 	情况：因为一个页面会出现 多个grid会在不同情况下去填充某一个固定的renderTo,
                     * 	结果：由于renderTo相同导致不同的grid产生的模板ID相同，从而出现了异常;
                     * @type {[type]}
                     */
                    gridBuilder.tmplRenderTo = gridBuilder.cfgs.renderTo + cid;
            }

            grid.prototype = {
                constructor: grid,
                /**
                 * 数据绑定
                 * @param  {[type]} data [description]
                 * @return {[type]}      [description]
                 */
                bindData: function(data) {
                    var gb = this.gb;

                    gb.data = data;

                    gb.datalen = data == null ? 0 : pubSubMod.emit("grid.getDataLength", [data], true, true);

                    gb.isEmpty = !gb.datalen;

                    this.dataSource = gb.data;

                    gb.render();

                    this._isBinded = true;

                    return this
                },
                /**
                 * 重新绑定数据
                 * @param  {[type]} data [description]
                 * @return {[type]}      [description]
                 */
                reBind: function(data) {

                    var gb = this.gb;

                    var dataLen = data == null ? 0 : pubSubMod.emit("grid.getDataLength", [data], true, true);

                    if (dataLen <= 0 && gb.isEmpty) {
                        return this;
                    }

                    if (!this._isBinded) {

                        return this.bindData(data);
                    }

                    gb.data = data;

                    gb.datalen = dataLen;

                    gb.isEmpty = !gb.datalen;

                    this.dataSource = gb.data;

                    gb.reRender();

                    // gb.pre_datalen = dataLen;
                    
                    return this
                },
                /**
                 * 添加数据
                 * 	添加的位置 top , bottom
                 * @param {[type]} data     [description]
                 * @param {[String]} position [添加的位置: top, bottom]
                 */
                addData: function(data, position) {

                    var gb = this.gb;

                    if (!this._isBinded) {
                        return this.bindData(data)
                    }
                    if (gb.isEmpty) {
                        gb.pre_datalen = gb.datalen || 0;
                        return this.reBind(data);
                    }

                    var gb = this.gb;

                    gb.pre_datalen = gb.datalen;

                    gb.data = pubSubMod.emit("grid.addNew", position == "top" ? [data, gb.data] : [gb.data, data], true, true);

                    if (data == null) return undefined;

                    gb.datalen = pubSubMod.emit("grid.getDataLength", [gb.data], true, true);

                    if (!gb.data || gb.datalen <= 0) return undefined;

                    this.dataSource = gb.data;

                    gb.addNewRender(position);

                    return this
                },
                /**
                 * 移除数据的时候与grid内部的保持同步的操作
                 * @param  {[Number]} ri [数据行的索引]
                 * @return {[type]}    [description]
                 */
                removeData: function(ri) {
                    var gb = this.gb;

                    if (ri < 0) return this;

                    this.dataSource = gb.data;

                    gb.removeSync(ri);

                    pubSubMod.emit("grid.removeData", [gb.data, ri]);

                    return this;
                },
                /**
                 * 主题发布订阅对象
                 * @type {[type]}
                 */
                pubSub: function() {

                    var ps = gridPubSubList[this.key];

                    return !ps ? (gridPubSubList[this.key] = new gPubSubMod(this.key)) : ps;
                }
            }

            function api(options) {

                gridBuilder = require("pure-grid.builder");

                /**
                 * 将客户提供的配置项与默认配置项进行合并
                 *  -会将开发人员定义的配置项中的缺少部分补全
                 */
                coreMod.combineObject(gDftMod.config, options);


                return gridList[options.key] = currentGrid = new grid(new gridBuilder(options), options.key);
            }

            /**
             * 当页面存在多个grid的时候,那么
             * @param  {[type]} key [description]
             * @return {[type]}     [description]
             */
            api.getCurrent = function(key) {

                return !key ? currentGrid : gridList[key];
            }

            api.basePubSub = pubSubMod;

            return api;
        })

        return pure.require("pure-grid.output")
    }

    /**
     * 将插件注册到全局对象上(由于使用的环境是浏览器上，所以此处的global等于window对象)
     * @type {[type]}
     */
    !global.pureGrid && (global.pureGrid = factory())

    /**
     * 使组件支持AMD或配置好文件映射路径的CMD两种模块化
     * @param  {[type]} typeof define        [description]
     * @return {[type]}        [description]
     */
    if (typeof define == "function") {
        if (define.amd || define.cmd) {
            define("pure-grid", [], function() {
                return global.pureGrid
            })
        }
    }
}(this);
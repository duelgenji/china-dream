! function(global) {

    /**
     * author:      zpli
     * english:     buns
     *   date:      2014-01-15
     * version:     0.0.1
     * description:
     *  参考：，感谢 aritDialog的作者
     *  兼容性: ie6及其以上版本的IE,chrome,firefox,opera
     *  特色：
     *      1. 支持样式分离：样式与js实现分离,除了基本的dialog显示所需的基本布局其余全部采用className填充
     *      2. 采用开发人员提供新配置，并且组件内始终保持一份默认配置，保证在没有配置的前提下也能够进行展示;
     *      3. 对外接口均都采用链式调用的方式；
     *      4. 针对于弹出框底部按钮的对应事件绑定，利用冒泡原理：全部交由按钮的上层容器进行触发，
     *           这样实现按钮click事件的分离: 只需要在配置项中为你要添加的按钮设置一个cmd命令，
     *              然后通过组件Api中的onCmd，offCmd,cmdTrigger三个函数进行不同cmd命令按钮的click回调
     *          从而达到事件的分离，改善组件的灵活性
     *      5. 支持tooltip显示，12个方位的显示
     *      6. 支持ESC快捷关闭
     */
    function factory() {

        var doc = global.document;

        /**
         * dialog的默认配置
         */
        pure.define("pure-dialog.config", {

            /**
             * 开发人员定制的用于快速查找dialog对象的关键字
             * @type {String}
             */
            key: "dialog_" + +new Date,

            //弹出框的宽度
            width: 'auto',

            //弹出框的高度
            height: 'auto',

            //弹出框的标题文本
            title: '',

            //弹出框的内容文本
            content: '<span class="ui-pure-dialog-loading">Loading..</span>',

            /**
             * 是否隐藏顶部的关闭按钮
             * @type {Boolean}
             */
            isHideTopClose: false,

            /**
             * 是否允许拖拽(默认为true)
             * @type {Boolean}
             */
            isDrag: false,

            /**
             * 是否通过ESC键 进行关闭
             * @type {Boolean}
             */
            isCloseByESC: true,

            /**
             * 默认确定按钮的显示文本
             * @type {String}
             */
            okText: "OK",

            /**
             * 默认取消按钮的显示文本
             * @type {String}
             */
            cancelText: "Cancel",

            /**
             * 对话框的类型
             * 	参考popupType的枚举
             * @type {String}
             */
            type: "default",

            /**
             * 跟随其他dom元素显示的时候是显示在该元素的那个方位
             * 		 	top: 	上方
             * 	   left-top: 	左上方
             * 	   right-top: 	右上方
             *
             * 			left:  	左方
             *   	top-left:  	上左方
             *    bottom-left: 	下左方
             *
             * 		  right: 	右方
             *    top-right: 	上右方
             *    bottom-right: 下右方
             *
             *  	 bottom: 	下方
             *    left-bottom: 	左下方
             *    right-bottom: 右下方
             *
             * @type {String}
             */
            direction: "left-top",

            //弹出框的底部按钮 (默认添加 确认、取消 两个按钮)
            buttons: null
        })

        /**
         * dialog的遮罩层操作模块
         * 	主要进行提供的方法
         * 		lock: 	遮罩层锁定
         * 		unlock: 遮罩层解锁
         * 		remove: 遮罩层移除
         * @param  {[type]} 			   [description]
         * @return {[type]}                [description]
         */
        pure.define("pure-dialog.mask", function(require) {

            var
            /**
             * 基础dom操作库
             * @type {[type]}
             */
                jDom = require("pure-jsDom"),
                /**
                 * 基础代码操作库
                 * @type {[type]}
                 */
                coreMod = require("pure-core"),
                /**
                 * 基础event事件绑定操作库
                 * @type {[type]}
                 */
                eventMod = require("pure-event"),
                /**
                 * 当前遮罩层HTMLElement
                 */
                current,
                /**
                 * 当前遮罩层显示内容的HTMLElement
                 */
                currentContentDom,
                /**
                 * 默认配置
                 * @type {Object}
                 */
                dftCfg = {
                    className: 'ui-loadingMask',
                    content: '加载中...',
                    img: ''
                };

            /**
             * 构建遮罩层
             * @return {[type]} [description]
             */
            function build(options) {
                var
                    windowWH,

                    scrollTL,

                    dom = doc.createElement("div"),

                    zIndex;

                dom.className = "ui-pure-dialog-mask";

                dom.setAttribute("cmd", "close");

                windowWH = jDom.documentWH(doc);

                jDom.css(dom, {
                    width: windowWH.width,
                    height: windowWH.height,
                    display: "none"
                });

                doc.body && doc.body.appendChild(dom);

                if (options && coreMod.isObject(options)) {
                    coreMod.combineObject(dftCfg, options);

                    scrollTL = jDom.scrollMeta(doc);

                    currentContentDom = doc.createElement("div");

                    currentContentDom.className = options.className;

                    zIndex = jDom.css(dom, "zIndex");

                    jDom.css(currentContentDom, {
                        zIndex: (zIndex != "auto" ? (parseInt(zIndex, 10) + 1) : zIndex),
                        position: "absolute",
                        display: "none"
                    });

                    currentContentDom.setAttribute("title", options.content);

                    if (options.img) {
                        jDom.css(currentContentDom, "background", 'url(' + options.img + ') no-repeat 50% 50%');
                    }

                    var contentDiv = doc.createElement("div");

                    jDom.html(contentDiv, options.content);

                    currentContentDom.appendChild(contentDiv);

                    doc.body && doc.body.appendChild(currentContentDom);

                    jDom.css(currentContentDom, {
                        left: (windowWH.width - currentContentDom.offsetWidth) / 2 + scrollTL.left,
                        top: (windowWH.height - currentContentDom.offsetHeight) * 382 / 1000 + scrollTL.top // 黄金比例
                    });
                } else {
                    currentContentDom = null;
                }

                eventMod.on(window, 'resize', function() {
                    var wh = jDom.documentWH(doc);
                    jDom.css(current, {
                        width: wh.width,
                        height: wh.height
                    });
                });

                return (current = dom);
            }

            /**
             * 锁定
             * @return {[type]} [description]
             */
            this.show = function(options) {

                !current && build(options == false ? null : (options || {}));

                if (current) {
                    jDom.css(current, "display", "block");
                    if (options != false) {
                        jDom.css(currentContentDom, "display", "block");
                    }
                }

                jDom.css(jDom.browser.isQuirksMode ? doc.body : doc.documentElement, "overflow", "hidden");

                return this;
            };

            /**
             * 取消锁定
             * @return {[type]} [description]
             */
            this.hide = function() {

                !current && build();

                if (current) {
                    jDom.css(current, "display", "none")
                        .css(currentContentDom, "display", "none");
                }

                jDom.css(jDom.browser.isQuirksMode ? doc.body : doc.documentElement, "overflow", "auto");

                return this
            }

            /**
             * 移除
             * @return {[type]} [description]
             */
            this.remove = function() {
                if (doc.body && current) {
                    doc.body.removeChild(current);
                    if (currentContentDom) {
                        doc.body.removeChild(currentContentDom);
                    }
                    current = null
                }
                return this
            }
        })

        /**
         * dialog 弹出框逻辑模块
         * @param  {[type]} 		[description]
         * @return {[type]}          [description]
         */
        pure.define("pure-dialog.popup", function(require) {
            var
            /**
             * 基础dom操作库
             * @type {[type]}
             */
                jDom = require("pure-jsDom"),

                /**
                 * 基础event事件绑定操作库
                 * @type {[type]}
                 */
                eventMod = require("pure-event"),

                /**
                 * 发布订阅模块
                 * @type {[type]}
                 */
                pubSubMod = require("pure-pubSub").register("pure-dialog"),

                /**
                 * dialog的html基础构建模板
                 * @type {[type]}
                 */
                template =
                '<div class="ui-pure-dialog">' + '<div class="ui-pure-dialog-arrowA"></div>' + '<div class="ui-pure-dialog-arrowB"></div>' + '<div p="header" class="ui-pure-dialog-header">' + '<button p="topClose" cmd="close" class="ui-pure-dialog-topClose" title="close">&#215;</button>' + '<div p="topTitle" class="ui-pure-dialog-title"></div>' + '</div>' + '<div p="body" class="ui-pure-dialog-body">' + '<table style="border:none;margin:0;padding:0;width:100%;">' + '<tbody>' + '<tr>' + '<td  p="tipImage" class="ui-pure-dialog-leftImage" style="width:30%;">' + '</td>' + '<td  p="content"  class="ui-pure-dialog-content" style="width:70%;">' + '</td>' + '</tr>' + '</tbody>' + '</table>' + '</div>' + '<div p="footer" class="ui-pure-dialog-footer">' + '</div>' + '</div>',

                /*
                template =
                '<div class="ui-pure-dialog">'
                + 		'<div class="ui-pure-dialog-arrowA"></div>' 
                + 		'<div class="ui-pure-dialog-arrowB"></div>' 
                + 		'<div p="header" class="ui-pure-dialog-header">' 
                + 			'<button p="topClose" cmd="close" class="ui-pure-dialog-topClose" title="close">&#215;</button>'
                + 			'<div p="topTitle" class="ui-pure-dialog-title"></div>' 
                + 		'</div>'
                + 		'<div p="body" class="ui-pure-dialog-body">'
                +			'<table style="border:none;margin:0;padding:0;width:100%;">'
                +				'<tbody>'
                +					'<tr>'
                +						'<td style="width:30%;">'
                +							'<div p="tipImage" class="ui-pure-dialog-leftImage"></div>'
                +						'</td>'
                +						'<td  style="width:70%;">'
                + 							'<div p="content" class="ui-pure-dialog-content"></div>' 
                +						'</td>'
                +					'</tr>'
                +				'</tbody>'
                +			'</table>'
                + 		'</div>' 
                + 		'<div p="footer" class="ui-pure-dialog-footer">' 
                + 		'</div>' 
                + 	'</div>';
                */

                /**
                 * id序号标识
                 * @type {Number}
                 */
                cid = 0,

                /**
                 * id前缀标识
                 * @type {String}
                 */
                expando = "pure-dialog_",

                /**
                 * 当前dialog的状态
                 * @type {Object}
                 */
                Status = {
                    //默认初始化还未显示
                    none: 0,

                    //当前处于显示状态
                    show: 1,

                    //当前处于关闭状态
                    close: 2,

                    //当前dialog已经被销毁
                    destory: 3
                },

                /**
                 * 对话框类型
                 * 	tip:"提示信息"
                 * 	warn:"警告信息"
                 * 	success:"成功信息"
                 * 	error :"报错信息"
                 * @type {Object}
                 */
                popupType = {
                    dft: "default",
                    hint: "hint",
                    tip: "tip",
                    warn: "warn",
                    success: "success",
                    error: "error"
                },

                /**
                 * 临时的dialog容器HTMLElement
                 */
                tempDiv;

            function Popup() {

                this.gid = cid++;

                this.id = expando + this.gid;

                this.status = 0;
            }

            Popup.prototype = {
                /**
                 * 将HTMLElement拼接到浏览器上
                 * @return {[type]} [description]
                 */
                _show: function(type, anchor) {
                    var d = this,
                        options = d.options;

                    d.status = Status.show;

                    !tempDiv && (tempDiv = doc.createElement("div"));

                    jDom.html(tempDiv, template);

                    var mp = d.mainPart = tempDiv.firstChild;

                    doc.body && doc.body.appendChild(mp);

                    mp && mp.setAttribute("id", d.id);

                    jDom.css(mp, {
                        width: jDom.formatWH(options.width),
                        height: jDom.formatWH(options.height)
                    });

                    var header = Popup.findPart(mp, "header");

                    var titlePart = d.titlePart = Popup.findPart(header, "topTitle");

                    if (options.isDrag) {

                        titlePart.style.cursor = jDom.browser.ie && jDom.browser.ie <= 6 ? "hand" : "move";

                        eventMod.on(titlePart, "mousedown", function(e) {
                            Popup.drag(e, d.mainPart);
                        });

                        eventMod.on(global, "scroll", Popup.scrollToTop);
                    }

                    jDom.html(titlePart, options.title);

                    var body = d.body = Popup.findPart(mp, "body");

                    var contentPart = d.contentPart = Popup.findPart(body, "content");

                    jDom.html(contentPart, options.content);

                    var topClosePart = d.topClosePart = Popup.findPart(header, "topClose");

                    if (options.isHideTopClose) {

                        topClosePart.style.display = "none";
                    }

                    Popup.buildButton(d.footer = Popup.findPart(mp, "footer"), options.buttons, options.key);

                    jDom.css(mp, "display", "");

                    d._showTypeChanged(type || options.type)
                        ._adjustStyle()[anchor ? "_follow" : "_center"](anchor);
                },
                /**
                 * 当显示的Type发生变化的时候调整
                 * @param  {[type]} type [description]
                 * @return {[type]}      [description]
                 */
                _showTypeChanged: function(type) {
                    var d = this;
                    switch (type) {
                        case popupType.dft:
                            break;
                        case popupType.hint:
                            jDom.css(header, "display", "none")
                                .css(d.footer, "display", "none")
                                .css(d.arrowA = mp.firstChild, "display", "block")
                                .css(d.arrowB = mp.childNodes[1], "display", "block");
                            break;
                        default:
                            (d.imagePart || (d.imagePart = Popup.findPart(d.body, "tipImage"))).className = "ui-pure-dialog-" + type + "Img";
                            break;
                    }
                    return d;
                },
                /**
                 * 关闭对话框
                 * @return {[type]} [description]
                 */
                _close: function() {
                    var d = this;

                    d.status = Status.close;

                    jDom.css(d.mainPart, "display", "none");

                    eventMod.off(global, "scroll", Popup.scrollToTop);

                    return d;
                },
                /**
                 * 移除对话框
                 * @return {[type]} [description]
                 */
                _remove: function() {
                    var d = this;

                    d.status = Status.destory;

                    if (d.iframePart) {
                        d.iframePart.onload = d.iframePart.onreadystatechange = null;
                        d.iframePart.src = 'about:blank';
                        try {
                            var win = d.iframePart.contentWindow;

                            win.document.write('');

                            win.document.clear();

                        } catch (e) {};
                    }
                    delete d.iframePart;

                    d.mainPart.parentNode.removeChild(d.mainPart);


                    return d;
                },
                /**
                 * 对话框居中
                 * @return {[type]} [description]
                 */
                _center: function() {
                    var innerWH = jDom.documentWH(doc);

                    var scrollTL = jDom.scrollMeta(doc);

                    var mp = this.mainPart;

                    jDom.css(mp, {
                        left: (innerWH.width - mp.offsetWidth) / 2 + scrollTL.left,
                        top: (innerWH.height - mp.offsetHeight) * 382 / 1000 + scrollTL.top // 0.618黄金比例
                    });

                    return this;
                },
                /**
                 * 调整对话框的样式，保证内容能够自适应当前的高度和宽度
                 * @return {[type]} [description]
                 */
                _adjustStyle: function() {
                    var d = this;

                    var title = d.titlePart;

                    var footer = d.footer;

                    var main = d.mainPart;

                    var wh = {
                        width: main.offsetWidth - 1,
                        height: main.offsetHeight - title.offsetHeight - footer.offsetHeight - 1
                    };

                    var bodyBorderMeta = jDom.borderMeta(d.body);

                    wh.width -= bodyBorderMeta.left - bodyBorderMeta.right;
                    wh.height -= bodyBorderMeta.top - bodyBorderMeta.bottom;

                    var iW = 0;

                    var scrollBarWH = jDom.scrollBarWH(doc);

                    if (d.imagePart) {
                        iW = d.imagePart.clientWidth;
                    }

                    jDom.css(d.body, wh)
                        .css(d.body.firstChild, "height", wh.height);

                    wh.width = wh.width - iW - scrollBarWH.vertical;
                    wh.height = wh.height - scrollBarWH.horizontal;

                    jDom.css(d.iframePart, wh);
                    if (d.iframePart) {
                        d.iframePart.setAttribute("width", wh.width);
                        d.iframePart.setAttribute("height", wh.height);
                    }
                    return d;
                },
                _offset: function() {

                },
                _follow: function(anchor) {

                    var d = this;

                    var direction = d.direction;

                    jDom.toggleClass(d.arrowA, "ui-pure-dialog-arrowA-" + direction)
                        .toggleClass(d.arrowB, "ui-pure-dialog-arrowB-" + direction);

                    return d;
                }
            };

            /**
             * 当前对话框状态的枚举
             * @type {[type]}
             */
            Popup.Status = Status;

            /**
             * 当进行遮罩拖拽的时候防止浏览器滚动条向下滚动
             * @return {[type]} [description]
             */
            Popup.scrollToTop = function() {
                doc.documentElement.scrollTop = 0;
                doc.body.scrollTop = 0;
            }

            /**
             * 构建button
             * @param  {[HTMLElement]} container [description]
             * @param  {[Array]} metas     [description]
             * @return {[type]}           [description]
             */
            Popup.buildButton = function(container, metas, key) {

                var btnHtmls = '',
                    btn;

                for (var i = 0, l = metas.length; i < l; i++) {
                    btn = metas[i];
                    btnHtmls += '<button type="button"' + ' cmd="' + btn.cmd + '"' + (btn.isFocus ? ' autofocus class="ui-pure-dialog-buttons-focus"' : '') + '>' + btn.text +
                        '</button>';

                    pubSubMod.on(key + btn.cmd, btn.callback || Popup.returnFalse);
                }

                jDom.html(container, btnHtmls);
            };

            Popup.returnFalse = function() {
                return false;
            };

            /**
             * 查找指定属性p等于特定的partName的HTMLElement
             * @param  {[type]} container [description]
             * @param  {[type]} partName  [description]
             * @return {[type]}           [description]
             */
            Popup.findPart = function(container, partName) {

                var node, temp;

                var nodes = container.childNodes;

                for (var i = 0, l = nodes.length; i < l; i++) {
                    temp = nodes[i];
                    if (temp.nodeType == 3 || temp.nodeType == 8) continue;
                    if (temp.getAttribute("p") === partName) {
                        node = temp;
                        break;
                    }
                    node = Popup.findPart(temp, partName);
                    if (node) {
                        break;
                    }
                }
                return node;
            };

            /**
             * 拖拽事件
             * @param  {[type]} target [description]
             * @param  {[type]} popup  [description]
             * @return {[type]}        [description]
             */
            Popup.drag = function(e, popup) {
                var target = e.target;
                var context = (target.ownerDocument || target.document || target),
                    that = popup,
                    diffX = e.clientX - that.offsetLeft,
                    diffY = e.clientY - that.offsetTop;

                function move(evt) {

                    evt.preventDefault();

                    var innerObj = jDom.documentWH(context),
                        scrollObj = jDom.scrollMeta(context);

                    var left = evt.clientX - diffX,
                        top = evt.clientY - diffY;

                    if (left < 0) {
                        left = 0;
                    } else if (left > (innerObj.width - scrollObj.left - that.offsetWidth)) {
                        left = innerObj.width - scrollObj.left - that.offsetWidth;
                    }

                    if (top < 0) {
                        top = 0;
                    } else if (top > (innerObj.height - scrollObj.top - that.offsetHeight)) {
                        top = innerObj.height - scrollObj.top - that.offsetHeight;
                    }

                    var ml = parseInt(that.style.marginLeft || 0, 10),
                        mt = parseInt(that.style.marginTop || 0, 10);

                    that.style.left = left + (ml > 0 ? ml : -ml) + "px";
                    that.style.top = top + (mt > 0 ? mt : -mt) + "px";

                    that.setCapture && that.setCapture(); //ie 中
                }

                function up() {
                    eventMod.off(doc, "mousemove", move);
                    eventMod.off(doc, "mouseup", up);
                    that.releaseCapture && that.releaseCapture();
                }

                eventMod.on(doc, "mousemove", move);
                eventMod.on(doc, "mouseup", up);
            };

            /**
             * 创建弹出框
             * @param  {[type]} options [description]
             * @return {[type]}         [description]
             */
            Popup.create = function(options) {

                var popup = new Popup();

                popup.options = options;

                return popup;
            };

            return Popup;
        });

        /**
         * dialog对外输出的接口定义
         * @param  {[type]} require){			var coreMod       [description]
         * @return {[type]}                  [description]
         */
        pure.define("pure-dialog.output", function(require) {
            var
            /**
             * 是否显示遮罩
             * @type {Number}
             */
                isModal = 0,

                /**
                 * 当前所有生成的dialog实例列表 (dialog的id为key)
                 * @type {Object}
                 */
                dialogList = {},

                /**
                 * 基础dom操作库
                 * @type {[type]}
                 */
                jDom = require("pure-jsDom"),

                /**
                 * pure-插件的基础代码模块
                 * @type {[type]}
                 */
                coreMod = require("pure-core"),

                /**
                 * 发布订阅模块
                 * @type {[type]}
                 */
                pubSubMod = require("pure-pubSub").register("pure-dialog"),

                /**
                 * 基础event事件绑定操作库
                 * @type {[type]}
                 */
                eventMod = require("pure-event"),

                /**
                 * 获取dialog的默认配置对象
                 * @type {[Object]}
                 */
                defaultConfig = require("pure-dialog.config"),

                /**
                 * 弹框对象
                 * @type {[type]}
                 */
                popupMod = require("pure-dialog.popup"),

                /**
                 * 遮罩层模块
                 * @type {[type]}
                 */
                maskMod,

                /**
                 * 当前popup实例
                 */
                current,

                /**
                 * 当前dialog实例
                 */
                currentDialog;

            /**
             * cmd命令的button主题的回调
             * @param  {[type]} event [description]
             * @return {[type]}       [description]
             */
            function cmdHandle(event) {
                var btn = event.target;

                if (btn.tagName !== "BUTTON") {
                    return;
                }

                var cmd = btn.getAttribute("cmd");

                currentDialog.cmdTrigger(cmd, btn);
            };

            /**
             * 使用ESC关闭对话框
             * @param  {[type]} event [description]
             * @return {[type]}       [description]
             */
            function esc(event) {
                var target = event.target;
                var nodeName = target.nodeName;
                var rinput = /^input|textarea$/i;
                var keyCode = event.keyCode;

                // 避免输入状态中 ESC 误操作关闭
                if (rinput.test(nodeName) && target.type !== 'button') {
                    return;
                }
                if (keyCode === 27) {
                    currentDialog.cmdTrigger('remove');
                }
            };

            /**
             * Iframe 加载完毕后的回调处理
             * @return {[type]} [description]
             */
            function iframeOnload(callback) {

                if (this.readyState && this.readyState != 'complete') return;

                if (iframeOnload.isLoaded) {
                    return;
                }

                iframeOnload.isLoaded = true;

                callback && callback(this);
            };

            /**
             * 是否加载完毕,主要针对ie的bug-- ie上会存在多次调用iframe的onload或者onreadystatechange事件，导致重复执行多次
             * @type {Boolean}
             */
            iframeOnload.isLoaded = false;

            /**
             * 对外部提供的dialog操作对象
             * @param  {[type]} popup [description]
             * @param  {[type]} key   [description]
             * @return {[type]}       [description]
             */
            function dialog(popup, key) {

                this.popup = popup;

                this.key = key
            }

            dialog.prototype = {
                constructor: dialog,
                /**
                 * 显示对话框
                 * 	如果指定了anchor那么就是对应的tooltip
                 * @param  {[HTMLElement|String]} anchor [description]
                 * @return {[type]}        [description]
                 */
                show: function(type, anchor) {

                    var p = this.popup;

                    if (p.status == popupMod.Status.show) {
                        if (type && p.type !== type) {

                            p.type = type;

                            jDom.css(p.imagePart, "display", "inline-block")
                                .css(p.contentPart, "textAlign", "left");

                            p._showTypeChanged(type)._adjustStyle();

                        } else {
                            jDom.css(p.imagePart, "display", "none")
                                .css(p.contentPart, "textAlign", "center");
                            /**
                             * 没有将p.ImagePart的display设置成为none
                             * 		是因为IE8中的标准模式中，此HTMLElement.style.display=none之后的offsetWidth不是0
                             * @param  {[type]} p.imagePart [description]
                             * @return {[type]}                   [description]
                             */
                            if (p.imagePart) {
                                delete p.imagePart;
                            }
                        }
                        return this;
                    }

                    eventMod.on(window, "resize", function() {
                        p._center();
                    });

                    p.type = type;

                    p._show(type, anchor);

                    if (p.topClosePart) {
                        eventMod.on(p.topClosePart, "click", cmdHandle);
                    }

                    eventMod.on(p.footer, "click", cmdHandle);

                    var that = this;

                    if (p.options.isCloseByESC) {
                        eventMod.on(doc, "keydown", esc);

                        this.onCmd('remove', function() {

                            eventMod.off(doc, 'keydown', esc);

                            delete dialogList[p.id];

                            return false;
                        });
                    } else if (!p.options.isHideTopClose) {
                        this.onCmd('close', function() {
                            that.close().remove();
                        });
                    }
                    return this;
                },
                /**
                 * 显示带遮罩层的对话框
                 * @return {[type]} [description]
                 */
                showModal: function() {

                    if (isModal == 1 && this.popup.status == popupMod.Status.show) {
                        return this;
                    }

                    isModal = 1;

                    !maskMod && (maskMod = require("pure-dialog.mask"));

                    isModal && maskMod.show(false);

                    return this.show.apply(this, arguments);
                },
                hide: function() {
                    var p = this.popup;

                    if (p.status == popupMod.Status.close) {
                        return this;
                    }

                    return this.close().remove();
                },
                /**
                 * 关闭对话框，但是并不将其移除
                 * @return {[type]} [description]
                 */
                close: function() {

                    var p = this.popup;

                    if (p.status == popupMod.Status.close) {
                        return this;
                    }

                    p._close();

                    isModal && maskMod.hide();

                    return this;
                },
                /**
                 * 移除对话框
                 * @return {[type]} [description]
                 */
                remove: function() {

                    var p = this.popup;

                    if (p.status == popupMod.Status.destory) {
                        return this;
                    }

                    p._remove();

                    isModal && maskMod.remove();

                    pubSubMod.empty();

                    return this;
                },
                /**
                 * 设置title
                 * @param  {[type]} value [description]
                 * @return {[type]}       [description]
                 */
                title: function(value) {

                    jDom.html(this.popup.titlePart, value);

                    return this;
                },
                /**
                 * 设置文本内容
                 * @param  {[type]} value [description]
                 * @return {[type]}       [description]
                 */
                content: function(value) {

                    var p = this.popup;

                    jDom.html(p.contentPart, value);

                    p._adjustStyle()._center();

                    return this;
                },
                iframe: function(uri, loadCallback, isNeedScroll) {

                    if (!uri) return this;

                    var p = this.popup;

                    if (!p.iframePart) {
                        jDom.html(p.contentPart, '<iframe style="border:0" frameborder="0"></iframe>');
                        p.iframePart = p.contentPart.firstChild;
                    }

                    p.iframePart.src = uri;

                    p.iframePart.onload = p.iframePart.onreadystatechange = function() {
                        iframeOnload.call(this, loadCallback);
                    };

                    jDom.css(p.iframePart, "overflow", !isNeedScroll ? "hidden" : "auto");

                    p._adjustStyle()._center();

                    return this;
                },
                /**
                 * 设置宽度（设置的时候会重新保证对话框居中）
                 * @param  {[type]} value [description]
                 * @return {[type]}       [description]
                 */
                width: function(value) {

                    var p = this.popup;

                    jDom.css(p.mainPart, "width", value);

                    p._adjustStyle()._center();

                    return this;
                },
                /**
                 * 设置高度（设置的时候会重新保证对话框居中）
                 * @param  {[type]} value [description]
                 * @return {[type]}       [description]
                 */
                height: function(value) {

                    var p = this.popup;

                    jDom.css(p.mainPart, "height", value);

                    p._adjustStyle()._center();

                    return this;
                },
                /**
                 * 设置对应的位置 left,top
                 * @param  {[type]} left [description]
                 * @param  {[type]} top  [description]
                 * @return {[type]}      [description]
                 */
                position: function(left, top) {

                    var p = this.popup;

                    left && (p.mainPart.style.left = jDom.formatWH(left));

                    top && (p.mainPart.style.top = jDom.formatWH(top));

                    return this;
                },
                /**
                 * 替换或者添加buttons
                 * @param  {[type]}  opts     [description]
                 * @param  {Boolean} isAppend [description]
                 * @return {[type]}           [description]
                 */
                buttons: function(opts) {

                    var p = this.popup;

                    popupMod.buildButton(p.footer, opts, this.key);

                    return this;
                },
                /**
                 * 为button注册相应的命令回调，回调中的作用域会保证是当前点击的button
                 * @param  {[String]}   cmd      [button对应的命令]
                 * @param  {Function} callback 	 [回调]
                 * @return {[type]}            [description]
                 */
                onCmd: function(cmd, callback) {

                    pubSubMod.on(this.key + cmd, callback);

                    return this;
                },
                /**
                 * 删除对应的命令回调
                 * @param  {[type]} cmd [description]
                 * @return {[type]}     [description]
                 */
                offCmd: function(cmd) {

                    pubSubMod.off(this.key + cmd);

                    return this;
                },
                /**
                 * 命令触发
                 * @param  {[type]} cmd    [description]
                 * @param  {[type]} button [description]
                 * @return {[type]}        [description]
                 */
                cmdTrigger: function(cmd, button) {

                    var resultArr = pubSubMod.emit(this.key + cmd, [], true, true, button);

                    var resultValue = coreMod.isArray(resultArr) ? resultArr[resultArr.length - 1] : resultArr;

                    if (resultArr === undefined || resultArr === null || resultValue == false) {

                        this.close().remove();

                        isModal && maskMod.hide().remove();
                    }
                },
                /**
                 * 设置dialog头部关闭按钮的可见性
                 * @param  {[type]} visible    [description]
                 * @return {[type]}        [description]
                 */
                topClose: function(visible) {
                    jDom.css(this.popup.topClosePart, "display", visible == true ? "" : "none");
                    return this;
                }
            }

            /**
             * 对外接口-初始化组件
             * @param  {[Object]} options [组件的初始配置]
             * @return {[type]}         [description]
             */
            function api(options) {

                options.key = options.key || ("pure_dialog_" + +new Date);

                /**
                 * 将客户提供的配置项与默认配置项进行合并
                 *  -会将开发人员定义的配置项中的缺少部分补全
                 */
                coreMod.combineObject(defaultConfig, options);

                if (!options.buttons) {
                    (options.buttons = []).push({
                        cmd: "ok",
                        text: options.okText,
                        isFocus: true
                    }, {
                        cmd: "close",
                        text: options.cancelText,
                        isFocus: false
                    });
                }
                var popupInstance = popupMod.create(options);

                dialogList[options.key] = currentDialog = new dialog(popupInstance, options.key);

                return currentDialog;
            }

            /**
             * 获取当前dialog实例
             * @return {[type]} [description]
             */
            api.getCurrent = function(key) {
                return !key ? currentDialog : dialogList[key];
            };

            /**
             * 获取父窗体中的pureDialog对象
             * @param  {[type]}   [description]
             * @return {[type]}   [description]
             */
            api.parent = function(contextWindow, key) {

                var curWin = contextWindow || this.ownerWindow || this.ownerDocument || this;

                return curWin ? curWin.parent.pureDialog.getCurrent(key) : null;
            }

            /**
             * 提供遮罩层操作对象
             * @type {[type]}
             */
            api.mask = maskMod || (maskMod = require("pure-dialog.mask"));

            return api;
        });

        return pure.require("pure-dialog.output");
    }

    !global.pureDialog && (global.pureDialog = factory());

    if (typeof define == "function") {
        define("pure-dialog", [], function() {
            return global.pureDialog;
        })
    }

}(this);
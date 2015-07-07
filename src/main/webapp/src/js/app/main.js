baseUrl = ".."
//baseUrl = "http://10.0.0.98:8080"
//baseUrl = "http://121.40.143.120:8080/dream"

/**
 * 保存登陆信息
 */
function saveUserInfo(userInfo) {
    sessionStorage.chinaDream_userInfo = JSON.stringify(userInfo);
}

/**
 * 读取登陆信息
 */
function loadUserInfo() {
    return eval("(" + sessionStorage.chinaDream_userInfo + ")");
}

/**
 * 清除登陆信息
 */
function clearUserInfo() {
    sessionStorage.removeItem("chinaDream_userInfo");
}

/**
 * 保存账号密码
 */
function saveAccount(account) {
    localStorage.chinaDream_account = JSON.stringify(account);
}

/**
 * 读取账号密码
 */
function loadAccount() {
    return eval("(" + localStorage.chinaDream_account + ")");
}

/**
 * 清除账号密码
 */
function clearAccount() {
    localStorage.removeItem("chinaDream_account");
}

/**
 * 获取url
 */
function getParam(param){
    var SearchString = window.location.search.substring(1);
    var VariableArray = SearchString.split('&');
    for(var i = 0; i < VariableArray.length; i++){
        var KeyValuePair = VariableArray[i].split('=');
        if(KeyValuePair[0] == param){
            return KeyValuePair[1];
        }
    }
}


define("main", ["systemDefine-repos", "pure-dialog"], function (require, exports) {
    var
        //base = ((location.origin || (location.protocol + "//" + location.hostname + ":" + (location.port || "80"))) + "/ChinaDream"),
        base = ((location.origin || (location.protocol + "//" + location.hostname + ":" + (location.port || "80")))),
        headerHtml =
            '  <div id="site_nav" role="navigation">' + '    <div class="ui-sn-container">' + '      <p class="ui-welcome">' + '        <em>Hello Dreamer! 您好:<span id="loginTitle"></span></em>' + '        <div class="scroll_div">' + '          <ul class="scroll_ul" id="scrollarea">' + '          </ul>' + '         </div>' + '      </p>' + '      <ul class="ui-quick-menu" id="quick_menu" style="display:none;">' + '        <li class="ui-sn_login">' + '          <a target="_self" href="login.html" title="登录">登录</a>' + '        </li>' + '        <li class="ui-sn_reg">' + '          <a href="register.html" title="还等什么亲，赶快去注册吧！">注册</a>' + '        </li></ul>' + '      <ul class="ui-quick-menu" id="loginout_menu" style="display:none;">' + '        <li class="ui-sn_reg" >' + '          <a href="modifyUserInfo.html">设置</a>' + '        </li>' + '        <li class="ui-sn_reg" >' + '          <a id="a_loginOut">退出</a>' + '        </li>' + '      </ul>' + '    </div>' + '  </div>' + '  <div style="position: absolute;width: 100%;"></div>' + '  <div class="ui-header">' + '    <h1 id="logo">' + '      <span class="mlogo">' + '        <div id="J_FpLogo">' + '          <a class="" href="index.html" title="我的中国梦">' + '            <img src="%1/image/logo.png" height="124" width="290" alt="我的中国梦">' + '          </a>' + '        </div>' + '      </span>' + '    </h1>' + '    <div class="ui-query">' + '      <div id="search">' + '        <div class="ui-search-combox">' + '          <input id="mq" type="text" name="search" value="" placeholder="请输入关键字">' + '        </div>' + '        <button type="button" title="查找对应询价名称" id="btn_search">搜索</button>' + '      </div>' + '    </div>' + '    <div class="ui-nav clearfloat">' + '        <ul id="nav">' + '          <li data-mod="index" data-link="index.html">' + '            <a title="询价大厅">询价大厅</a>' + '          </li>' + '          <li data-mod="usercenter" data-link="userRoom.html">' + '            <a title="用户大厅" >用户大厅</a>' + '          </li>' + '          <li data-mod="selfcenter" data-link="myZone.html">' + '            <a title="我的主页">我的主页</a>' + '          </li>' + '          <li data-mod="createinquiry" data-link="inquiryNew.html">' + '            <a title="询价发布" >发布询价/梦想</a>' + '          </li>' + '          <li data-mod="help">' + '            <a title="新手上路">新手上路</a>' + '          </li>' + '        </ul>' + '      </div>' + '  </div>',

        footerHtml =
            ' <div class="ui-title">' + '    <span>中梦国网</span>' + '  </div>' + '  <div class="ui-zhinan">' + '    <dl>' + '      <dt></dt>' + '      <dd>' + '        <a href="#">写在开始</a>' + '        <a href="#">产生背景</a>' + '        <a href="#">案例分析</a>' + '      </dd>' + '    </dl>' + '    <dl>' + '      <dt></dt>' + '      <dd>' + '        <a href="#">提问回答(Q&A)</a>' + '        <a href="#">公司章程(节选)</a>' + '        <a href="#">采购规则(节选)</a>' + '      </dd>' + '    </dl>' + '    <dl>' + '      <dt></dt>' + '      <dd>' + '        <a href="#">接受捐赠</a>' + '        <a href="#">与我联系</a>' + '        <a href="#">文件下载</a>' + '      </dd>' + '    </dl>' + '    <dl>' + '      <dt></dt>' + '      <dd>' + '        <a href="#">周公吐哺</a>' + '        <a href="#">核心价值</a>' + '        <a href="#">投诉建议</a>' + '        <a href="#">问题举报</a>' + '      </dd>' + '    </dl>' + '  </div>' + '  <div class="ui-share">' + '    <a href="http://t.qq.com/mychinadreams" target="_blank">' + '      <img src="%1/image/weiboicon32.png" height="32" width="32" alt="腾讯微博" title="分享至腾讯微博">' + '    </a>' + '    <a href="http://www.weibo.com/5511866263/profile?topnav=1&wvr=6" target="_blank" title="分享至新浪微博">' + '      <img src="%1/image/weibo.png" height="32" width="36" alt="新浪微博">' + '    </a>' + '    <a id="weixin">' + '      <img src="%1/image/weixin.png" height="32" width="38" alt="微信" title="微信扫一扫">' + '      <div id="weixinLogo" style="display: none;">' + '        <div class="ui-weicode"></div>' + '        <h4>中国梦 等你来</h4>' + '        <b></b>' + '      </div>' + '    </a>' + '  </div>' + '  <div class="ui-beian clearfloat">' + '    <span>© COPYRIGHT 2015-2018 <a href="http://www.miitbeian.gov.cn/" target="_blank">沪ICP备15008817</a>  mychinadreams.com</span>' + '  </div>',

        systemDefineMod = require("systemDefine-repos"),

        pureDgMod = require("pure-dialog"),

        pattern,

        newLiHtml = '<li><a>%1</a></li>';

    /**
     * 字符串格式化
     * @param  {[String]} str [带格式的字符串 例如: " i'm a %1 "]
     * @return {[String]}     [格式化完成的字符串]
     *  note: 占位符只支持到9个，如果需要更多的请再次q 调用此方法
     */
    function format(str) {
        var args = arguments;
        if (!pattern) pattern = new RegExp("%([1-9])", "g");
        return String(str).replace(pattern, function (match, index) {
            return args[index];
        });
    }

    /**
     * 导航跳转
     * @param  {[type]} e [description]
     * @return {[type]}   [description]
     */
    function evt_nav(e) {
        var islogin,
            that = $(this),
            mod = $(this).data("mod"),
            url = "";

        switch (mod) {
            case "usercenter":
                url = that.data("link");
                break;
            case "selfcenter":
                if (!fn_isLogin()) {
                    alert("你还未登录!");
                    return;
                }
                url = that.data("link");
                break;
            case "createinquiry":
                url = that.data("link");
                break;
            case "help":
                alert("此功能尚在构建，请耐心等候!");
                return;
            default:
                //        url = base;
                url = that.data("link");
                break;
        }

        window.open(url, "_self");
        //window.open(url, that.data("target") || $("base").attr("target"));
    }

    /**
     * 初始化事件绑定
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        if (!fn_isLogin()) {
            $("#quick_menu").css("display", "");
            $("#loginout_menu").css("display", "none");

            // 未登陆不可查看页面跳转
            if (window.location.pathname == "") {
                location.href = "login.html";
            }
        } else {
            $("#quick_menu").css("display", "none");
            $("#loginout_menu").css("display", "");
            $("#loginTitle").text(loadUserInfo().nickName);
        }

        $("#a_loginOut").click(fn_loginOut);

        /**
         * 微信图片的鼠标划过事件绑定
         */
        $("#weixin").hover(function () {
            $("#weixinLogo").css("display", "block");
        }, function () {
            $("#weixinLogo").css("display", "none");
        });

        $("#nav li").click(evt_nav);

    }

    function call_newsOk(data) {
        var html = [];
        for (var i = 0, l = data.length; i < l; i++) {
            html.push(format(newLiHtml, data[i].content));
        }
        $("#scrollarea").html(html.join(''));
    }

    function call_newsFail() {
        var html = [
            '<li><a>梦想无论怎样模糊，总潜伏在我们心底，使我们的心境永远得不到宁静，直到这些梦想成为事实。</a></li>',
            '<li><a> 当生活给你十个理由哭泣时，你就拿出一百个梦想笑给它看！</a></li>',
            '<li><a>一个人至少拥有一个梦想，有一个理由去坚强。心若没有栖息的地方，到哪里都是在流浪 </a></li>',
            '<li><a>梦想无论怎样模糊，总潜伏在我们心底，使我们的心境永远得不到宁静，直到这些梦想成为事实才止；像种子在地下一样，一定要萌芽滋长，伸出地面来，寻找阳光。</a></li>',
            '<li><a>我们未来的每个人的生活就是因为你的梦想而被颠覆的。</a></li>',
            '<li><a>拥有梦想的人是值得尊敬的，也让人羡慕。当大多数人碌碌而为为现实奔忙的时候，坚持下去，不用害怕与众不同，你该有怎么样的人生，是该你亲自去撰写的。加油！让我们一起捍卫最初的梦想。</a></li>',
            '<li><a>你若不勇敢梦想，青春便老去。</a></li>',
            '<li><a>梦想不会逃跑，会逃跑的永远都是自己。</a></li>',
            '<li><a>在梦想/许愿时，必须要深信不疑，如果你不相信自己有能力让愿望成真，你的愿望就真的会飞走……如果你所希望的是有可能实现得了的，<br> 那么你将会不惜一切地去实现它！</a></li>',
            '<li><a>梦想的魔力不在于许愿，而在于去做！其实梦想无论多模糊，总潜伏在我心底，像地下种子一样，一定要萌芽滋长、冲出地面、寻找阳光</a></li>'
        ];

        $("#scrollarea").html(html.join(''));
    }

    /**
     * 新闻联播
     * @return {[type]} [description]
     */
    function fn_newsSlide() {
        var scroll_area = $("#scrollarea");
        var timespan = 5000;
        var timeID;
        scroll_area.hover(function () {
            clearInterval(timeID);
        }, function () {
            timeID = setInterval(function () {
                var moveline = scroll_area.find('li:first');
                var lineheight = moveline.height();
                moveline.animate({
                    marginTop: -lineheight + 'px'
                }, 3000, function () {
                    moveline.css('marginTop', 0).appendTo(scroll_area);
                });
            }, timespan);
        }).trigger('mouseleave');
    }

    /**
     * 初始化header和footer两个部分的页面显示的HTML
     * @return {[type]}              [description]
     */
    function fn_initHTMLTemplate(showPart) {

        if (showPart !== "footer") {
            var header = $("#header").html(format(headerHtml, base));

            if (header.length) {
                systemDefineMod.getAllDreamwords(call_newsOk, call_newsFail, call_newsFail);
            }
        }

        if (showPart !== "header") {
            $("#footer").html(format(footerHtml, base));
        }
    }


    /**
     * 登录信息
     * @type {[type]}
     */
    var loginInfo = exports.loginInfo = loadUserInfo();
    /**
     * 判断当前是否登录 登录用户的角色和身份
     * @return {[type]} [description]
     */
    var fn_isLogin = exports.isLogin = function () {
        //return (loginInfo.nickname = $.cookie("nickname")) && (loginInfo.name = $.cookie("username")) && (loginInfo.type = $.cookie("usertype"));
        //var isLogin = !!loadUserInfo();
        var isLogin = false;
        var status = 1;

        if (!isLogin) {
            // 获取登陆信息
            $.ajax({
                url: "../userInfo/retrieveInfo",
                type: "post",
                async: false,
                dataType: "json",
                success: function (result) {
                    if (result.success == 1) {
                        console.log(result);
                        isLogin = true;
                        saveUserInfo(result);

                        status = result.status;
                    }
                }
            })
        }

        if (!isLogin) {
            //  如本地有账号信息，尝试自动登陆
            var account = loadAccount();
            if (account) {
                $.ajax({
                    url: "../user/login",
                    type: "post",
                    async: false,
                    dataType: "json",
                    data: account,
                    success: function (result) {
                        if (result.success == 1) {
                            isLogin = true;
                            saveUserInfo(result);

                            status = result.status;
                        }
                    }
                })
            }
        }

        // 未邮箱认证
        if (status == 0) {
            $.ajax({
                url: "../user/logout",
                type: "post",
                dataType: "json",
                success: function (result) {
                    if (result.success == 1) {
                        clearUserInfo();
                        clearAccount();

                        location.href = "emailSent.html";
                    }
                }
            })
        }

        if (!isLogin) {
            clearUserInfo();
        }

        return isLogin;
    }

    /**
     * 执行登出操作
     * @type {[type]}
     */
    var fn_loginOut = exports.loginOut = function () {
        $.cookie('userkey', '', {
            path: "/",
            expires: -1
        }); // 删除 cookie
        $.cookie('username', '', {
            path: "/",
            expires: -1
        }); // 删除 cookie
        $.cookie('nickname', '', {
            path: "/",
            expires: -1
        }); // 删除 cookie

        $.ajax({
            url: "../user/logout",
            type: "post",
            dataType: "json",
            success: function (result) {
                if (result.success == 1) {
                    clearUserInfo();
                    clearAccount();

                    location.href = "index.html";
                }
            }
        })
    }


    var fn_openTopFixed = exports.openTopFixed = function () {
        /*
         头部的top固定
         */
        $("#header").fixtop({
            fixed: function (el) {
                el.addClass("ui-fixedHeader");
            },
            unfixed: function (el) {
                el.removeClass("ui-fixedHeader");
            }
        });

        return exports;
    }

    /**
     * 设置当前导航哪一个处于选中
     * @param {[type]} modName [模块名称]
     */
    exports.setActiveNav = function (modName) {

        $("#nav li[data-mod=" + modName + "]").data("target", "_self").children('a').addClass("active");

        return exports;
    };

    exports.closeTopFixed = function () {
        $(window).off("scroll");

        return exports;
    };

    /**
     * 模块装载初始化操作
     * @return {[type]} [description]
     */
    exports.load = function (showPart) {

        fn_initHTMLTemplate(showPart);

        fn_openTopFixed();

        fn_initEvent();

        fn_newsSlide();

        return exports;
    };
});
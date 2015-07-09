define("myzone-logic", ["main", "myzone-config", "jquery", "user-repos", "bid-repos", "inquiry-repos", "collect-repos", "lettermsg-repos", "pure-grid", "pure-dialog"], function (require, exports) {

    var
        $ = require("jquery"),

        configMod = require("myzone-config"),

        gridMod,

        dialogMod = require("pure-dialog"),

        userRepos = require("user-repos"),

        bidRepos = require("bid-repos"),

        inquiryRepos = require("inquiry-repos"),

        collectRepos = require("collect-repos"),

        letterMsgRepos = require("lettermsg-repos"),

        mainMod = require("main"),

        currentQueryObj = {
            page: 0
        },

        dataSource;



    function fn_getMyInfo() {
        ajaxRetrieveUserDetail(currentQueryObj.userId ,call_myInfoOk, call_myInfoFail, call_myInfoFail);
        //userRepos.getDetail(mainMod.loginInfo.name, call_myInfoOk, call_myInfoFail, call_myInfoFail);
    }


    function call_myInfoOk(data) {
        showUserInfo(data);

        $("span.ui-val").each(function (i, span) {
            span = $(span);
            var col = span.data("col"),
                val = data[col];
            span.text(val).attr("title", val);
        });

        $("#btn_addCollect").click(function () {
            var that = $(this);
            if (that.attr("status") == "working") {
                return;
            }

            var cmd = that.attr("status", "working").data("cmd");

            if (cmd == "add") {
                ajaxGenerateCollectionU({userId: currentQueryObj.userId}, function (data) {
                    alert("收藏成功");
                    currentQueryObj.collectionId = data.collectionId;
                    that.data("cmd", "cancel").attr("status", "done").text("取消收藏");
                }, function () {
                    alert("添加收藏失败!");
                    that.removeAttr("status");
                }, function () {
                    alert("添加收藏异常!");
                    that.removeAttr("status");
                });
            } else if(cmd == "cancel"){
                console.log(currentQueryObj.collectionId);
                ajaxCancelCollectionU({collectionId: currentQueryObj.collectionId}, function (data) {
                    alert("取消收藏成功");
                    that.data("cmd", "add").attr("status", "done").text("添加收藏");
                }, function () {
                    alert("取消收藏失败!");
                    that.removeAttr("status");
                }, function () {
                    alert("取消收藏异常!");
                    that.removeAttr("status");
                });
            }
        });
    }

    function call_myInfoFail() {
        alert("请求失败");
    }

    function showUserInfo(data){


        $("#ul_basic").hide();

        switch(data.userType){
            case 1:
                $("#ul_person").show();
                $("img#logo").attr("src","/image/pic/personalDefaultLogo.jpg");
                break;
            case 2:
                $("#ul_company").show();
                $("img#logo").attr("src","/image/pic/companyDefaultLogo.jpg");
                break;
            case 3:
                $("#ul_group").show();
                $("img#logo").attr("src","/image/pic/groupDefaultLogo.jpg");
                break;
            default :
                break;
        }

        if(data.logoUrl){
            $("img#logo").attr("src",data.logoUrl)
        }

        switch(data.isCollection){

            case -1:
                $("#btn_addCollect").hide().data("cmd", "");
                break;
            case 0:
                $("#btn_addCollect").text("加至收藏").data("cmd", "add");
                break;
            case 1:
                currentQueryObj.collectionId = data.collectionId;
                $("#btn_addCollect").text("取消收藏").data("cmd", "cancel");
                break;
            default :
                break;
        }
    }

    /**
     * 初始化相关HTMLElement的事件操作
     * @return {[type]} [description]
     */
    function fn_initEvent() {

        fn_getMyInfo();

    };
    var currentQueryObj = {};

    exports.load = function () {
        currentQueryObj.userId = getParam("key");
        fn_initEvent();
    };
});
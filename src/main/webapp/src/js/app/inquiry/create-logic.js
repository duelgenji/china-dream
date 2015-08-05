/**
 * 询价发布的逻辑js
 * @param  {[type]} require        [description]
 * @param  {[type]} exports           [description]
 * @return {[type]}                [description]
 */
define("create-logic", ["jquery", "main", "inquiry-repos", "pure-validator", "pure-dialog"], function (require, exports) {
    var $ = require("jquery"),

        mainMod = require("main"),

        validMod = require("pure-validator"),

        dialogMod = require("pure-dialog"),

        publishOkHtml = '<div class="ui-publishTip"><span><img class="ui-tipOkIcon"/>恭喜您,标号:<span id="span_biaohao"></span>的询标发布成功!</span><p><a href="index.html" target="_self">返回平台首页</a></p></div>',

        publishFailHtml = '<div class="ui-publishTip"><span><img class="ui-tipFailIcon"/>对不起,询价发布失败!</span></div>',

        publishErrorHtml = '<div class="ui-publishTip"><span><img class="ui-tipErrorIcon"/>对不起,询价发布出现异常!</span></div>',

        industries = ["梦幻梦想","奇思妙想","公益梦想","行业梦想","企业服务","股权融资","IT产业","房产租售","广告文案","工程建筑","文化艺术","服装纺织","专业服务","安全防护","环保绿化","旅游休闲","办公文教","电子电工","玩具礼品","风险投资","冶金矿产","石油化工","水利水电","交通运输","信息产业","机械机电","轻工食品","农林牧渔","家居用品","物流包装","体育用品","文教办公","软件游戏","强电弱电","债券清算","反向团购","其他"],

        industryList = $("#industry-list"),

        provinces = ["北京","天津","上海","重庆","河北","河南","云南","辽宁","黑龙江","湖南","安徽","山东","新疆","江苏","浙江","江西","湖北","广西","甘肃","山西","内蒙","陕西","吉林","福建","贵州","广东","青海","西藏","四川","宁夏","海南","台湾","香港","澳门","钓鱼岛","国外"],

        provinceList = $("#province-list"),

        currentPosition = 1,

        publishCallDg = {
            id: "publishCallDg",
            isHideTopClose: true,
            width: 400,
            height: 300,
            title: "询价发布提示",
            buttons: []
        };

    inquiryRepos = require("inquiry-repos");

    //function fn_submitForm() {
    //
    //    console.log($("#inquiryForm").serializeArray());
    //    console.log($("#fileForm").serializeArray());
    //
    //    //var dialog = dialogMod(publishCallDg)
    //    //	.showModal()
    //    //	.content('<div><img src="../image/loading.gif" alt="" />正在提交中,请耐心等候!</div>');
    //    //
    //    //inquiryRepos.publish($("#inquiryForm").serialize(), function(data) {
    //    //	dialog.content(publishOkHtml).title("询价发布成功");
    //    //	$("#span_biaohao").text(data);
    //    //}, function() {
    //    //	dialog.content(publishFailHtml)
    //    //		.title("询价发布失败!")
    //    //		.buttons([{
    //    //			cmd: "cancel",
    //    //			text: "关闭"
    //    //		}]);
    //    //}, function() {
    //    //	dialog.content(publishErrorHtml)
    //    //		.title("询价发布异常")
    //    //		.buttons([{
    //    //			cmd: "cancel",
    //    //			text: "关闭"
    //    //		}]);
    //    //});
    //}

    function showErrorTip(errorPos, errorMsg) {
        window.scrollTo(0, 0);
        $("#mainMask").css("display", "block");
        $("#bubbleLayer").addClass("bubbleLayer-show").css("top", errorPos);
        $("#bubbleLayerWrap .error-tt p").text(errorMsg);
    }

    function industrySearch(keyWord) {

        industryList.empty();

        for (index in industries) {
            var industry = industries[index];
            if (industry.indexOf(keyWord) != -1) {
                industryList.append('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0);" data-index=' + (index / 1 + 1) + '>'
                    + industry + '</a></li>');
            }
        }
    }

    function provinceSearch(keyWord) {

        provinceList.empty();

        for (index in provinces) {
            var province = provinces[index];
            if (province.indexOf(keyWord) != -1) {
                provinceList.append('<li role="presentation"><a role="menuitem" tabindex="-1" href="javascript:void(0);" data-index=' + (index / 1 + 1) + '>'
                    + province + '</a></li>');
            }
        }
    }

    function valid_step1() {
        var isError, doms = $("#title,#industryCode,#provinceCode,#purchaseCloseDate,#totalPrice,#inquiryMode");

        for (var i = 0, l = doms.length, dom, val; i < l; i++) {
            val = (dom = $(doms[i])).val();
            console.log(val);
            if (validMod.isEmptyOrNull(val)) {
                showErrorTip(dom.data("pos"), dom.attr("placeholder"));
                isError = !!1;
                break;
            }
            if (i == 4 && !validMod.isPositive(val)) {
                showErrorTip(dom.data("pos"), dom.data("errmsg"));
                isError = !!1;
                break;
            }
        }

        val = (dom = $("#remark")).val();
        if (val && val.length > 1000) {
            showErrorTip(dom.data("pos"), dom.data("errmsg"));
            isError = !!1;
        }

        val = (dom = $("#intervalHour")).val();
        if( $("#inquiryMode").val()==5 && (val<2 || val>240) ){
            showErrorTip(dom.data("pos"), dom.data("errmsg"));
            isError = !!1;
        }

        return !isError;
    }

    function valid_step2() {
        var isError, doms = $("#contactName,#contactMail,#contactTelephone");

        for (var i = 0, l = doms.length, dom, val; i < l; i++) {
            val = (dom = $(doms[i])).val();
            if (validMod.isEmptyOrNull(val)) {
                showErrorTip(dom.data("pos"), dom.attr("placeholder"));
                isError = true;
                break;
            }
            if ((i == 1 && !validMod.isEmail(val)) || (i == 2 && !validMod.isMobile(val))) {
                showErrorTip(dom.data("pos"), dom.data("errmsg"));
                isError = true;
                break;
            }
        }
        return !isError;
    }

    function valid_step3() {
        var isError = false,
            doms = $("#file1,#file2,#file3"),
            size = 0;

        for (var i = 0, l = doms.length, dom; i < l; i++) {
            var f = $(doms[i])[0].files[0];
            if (!validMod.isEmptyOrNull(f)) {
                size += f.size;

                if (f.size > 5 * 1024 * 1024) {
                    alert("上传文件大小请控制在5M以下");
                    return false;
                }

                if (size > 30 * 1024 * 1024) {
                    alert("上传文件总容量请控制在30M以下");
                    return false;
                }
            }
        }

        return !isError;
    }

    function evt_toPre(e) {
        var $this = $(this),
            $next = $("#btn_next"),
            preId = parseInt($this.data("pre"));

        currentPosition = preId;

        if (currentPosition == 1 || currentPosition == 4) {
            $this.css("display", "none");
        }
        if (currentPosition < 4) {
            $next.text("下一步");
            $next.css("display", "");
            $("#btn_confirm").css("display", "none");
        }

        $("#div_step" + (preId + 1)).css("display", "none");
        $("#div_step" + preId).css("display", "");
        $this.data("pre", preId - 1).data("current", preId);
        $next.data("next", preId + 1).data("current", preId);

        $("#div_steps li:eq(" + (currentPosition - 1) + ")").removeClass("active").addClass("active").siblings('li').removeClass("active");


        $("#bubbleLayer").removeClass("bubbleLayer-show");
    }

    function evt_toNext(e) {
        var $this = $(this),
            $pre = $("#btn_pre"),
            nextId = parseInt($this.data("next"));

        if (nextId == 2) {
            if (!valid_step1()) {
                return;
            }
            $("#hd_purchaseCloseDate").val($("#purchaseCloseDate").val() + " " + $("#purchaseCloseDateHour").val() + ":00");
        }

        if (nextId == 3) {
            if (!valid_step2()) {
                return;
            }
        }

        currentPosition = nextId;

        if (currentPosition > 1) {
            $pre.css("display", "");
        }

        if (nextId == 5) {
            fn_submitForm();
            return;
        }
        if (nextId == 4) {
            if (!valid_step3()) {
                return;
            }

            $("#div_step4 ul span").each(function (i, span) {
                var target = $("#" + span.getAttribute("field")),
                    from = span.getAttribute("data-from");

                if (i == 0) {
                    span.innerText = $("input[name='round'][type=radio]:checked").attr("title");
                    return;
                }

                span.innerText = from == "selectedText" ? (from == "attrTitle" ? target.attr("title") : target.find("option:selected").text()) : target.val();
            });

            $this.text("确认提交");
            $this.css("display", "none");
            $("#btn_confirm").css("display", "");
        }

        $("#div_step" + (nextId - 1)).css("display", "none");
        $("#div_step" + nextId).css("display", "");
        $this.data("next", nextId + 1).data("current", nextId);
        $pre.data("pre", nextId - 1).data("current", nextId);

        $("#div_steps li:eq(" + (currentPosition - 1) + ")").removeClass("active").addClass("active").siblings('li').removeClass("active");

        $("#bubbleLayer").removeClass("bubbleLayer-show");
    }

    function fn_init() {

        $.get(baseUrl + "/collection/retrieveCollectionListU", function (data) {
            data = data.data;
            for(var i=0;i<data.length;i++){
                //$("#userList").append('<option value="'+data[i].userId+'">'+data[i].userNickname+'</option>');
                $("#userList").append('<div class="userDiv"><input id="ucb'+i+'" type="checkbox" name="userList" value="'+data[i].userId+'"/><label for="ucb'+i+'">'+data[i].userNickname+'</label></div>');


            }
        });

        if(currentQueryObj.inquiryId){
            //第二第三轮隐藏 轮次选择
            $("input[name='round']").closest("li").hide();
            ajaxRetrieveInfo({inquiryId: currentQueryObj.inquiryId},
                function(data){

                    //轮次标题变更
                    if(data.round==1){
                        $("input[name='round'][type=radio]:checked").attr("title","第二轮(有的放矢)");
                    }else if(data.round==2){
                        $("input[name='round'][type=radio]:checked").attr("title","第三轮(精益求精)");
                    }

                    $("input,select,textarea").each(function (i, span) {
                        span = $(span);
                        var col = span.attr("name"),
                            val = data[col];
                        if(val){
                            span.val(val);
                            if(col=="industryCode"){
                                $("#industryCode").val(industries[val-1]);
                            }else if(col=="provinceCode"){
                                $("#provinceCode").val(provinces[val-1]);
                            }else if(col=="limitDate"){
                                $("#purchaseCloseDate").val(val.split(" ")[0]);
                                $("#purchaseCloseDateHour").val(parseInt(val.split(" ")[1].split(":")[0])+":00");

                            }
                        }
                    });
                }, function(data){
                    alert(data.message);
                    location.href=location.protocol+'//'+location.host+location.pathname;
                },  function(){});

        }

        $("#chk_allowShare").click(function () {

            var status = parseInt($("#status").val());

            $("#btn_confirm")[status == 1 ? "attr" : "removeAttr"]("disabled", "disabled");

            $("#status").val(1 - status);
        });

        $("#userName").val(mainMod.loginInfo.nickName);
        $("#hd_userName").val(mainMod.loginInfo.nickName);
        $("#hd_inqueryUserName").val(mainMod.loginInfo.nickName);

        $("#btn_next").click(evt_toNext);
        $("#btn_pre").click(evt_toPre);

        //日期选择事件注册
        $("#calendar_ctrl").click(function (event) {
            event.stopPropagation();
            __showCalendar(event, "purchaseCloseDate");
        });
    }

    $(".dropdown-input").on("focus", function () {

        if ($(this).attr("id") == "industryCode") {
            industrySearch($(this).val());
        } else if ($(this).attr("id") == "provinceCode") {
            provinceSearch($(this).val());
        }

    }).on("keyup", function (event) {

        if ($(this).attr("id") == "industryCode") {
            industrySearch($(this).val());
        } else if ($(this).attr("id") == "provinceCode") {
            provinceSearch($(this).val());
        }

    }).on("blur", function () {
        var value = $(this).val();
        if (value == "") {
            $(this).attr("data-lastContent", value);
        } else {
            $(this).val($(this).attr("data-lastContent"));
            $("#hd_" + $(this).attr("id")).val("0");
        }
    });

    $(".dropdown").on("click", ".dropdown-menu li a", function () {
        var input = $(this).closest(".dropdown").find(".dropdown-toggle");

        if (input.is("input")) {
            var value = $(this).html();
            input.val(value).attr("data-lastContent", value);

            $("#hd_" + input.attr("id")).val($(this).attr("data-index"));
        }
    });

    //预览图片
    $("#projectImage").on("change",function(e){
        var file = e.target.files || e.dataTransfer.files;
        if(file && file[0]){
            var reader = new FileReader();
            reader.onload = function() {
                $("#image").attr("src",this.result).show();
            }
            reader.readAsDataURL(file[0]);
        } else {
            $("#image").attr("src","").hide();
        }
    });
    //切换询价模式
    $("#inquiryMode").on("change",function(){
        if($(this).val()==5){
            $("#hourLi").show();
        }else{
            $("#hourLi").hide();
        }
    });

    $("#inquiryForm").ajaxForm();
    $('#inquiryForm').submit(function () {
        $("#btn_confirm").attr("disabled","");
        $("#loading").show();

        var url = baseUrl+"/inquiry/generateInquiry";
        if(currentQueryObj.inquiryId){
            url = baseUrl + "/inquiry/inquiryNextRound";
        }
        console.log(url);
        var options = {
            url: url,
            type: 'post',
            dataType: null,
            clearForm: false,
            success: function (data, textStatus, jqXHR) {
                var result = data.success;
                if (result == 1) {
                    $("#inquiryNo").text(data.inquiryNo);

                    $("#div_steps li:eq(4)").removeClass("active").addClass("active").siblings('li').removeClass("active");
                    $("#div_step4").css("display", "none");
                    $("#div_step5").css("display", "");

                    $("#btn_pre").css("display", "none");
                    $("#btn_next").css("display", "none");
                    $("#btn_confirm").css("display", "none");
                    $("#btn_back").css("display", "");

                }else{
                    alert(data.message)
                }
            },complete:function(){
                $("#btn_confirm").removeAttr("disabled");
                $("#loading").hide();
            }
        };

        $(this).ajaxSubmit(options);
        return false;
    });

    $(":file").on("change", function () {
        if (!valid_step3()) {
            $(this).val("");
        }
    })

    $("input, select, textarea").on("focus", function() {
        $("#mainMask").css("display", "none");
        $("#bubbleLayer").removeClass("bubbleLayer-show");
    });


    var currentQueryObj = {};

    exports.load = function () {
        currentQueryObj.inquiryId = getParam("key");
        fn_init();
    };
});
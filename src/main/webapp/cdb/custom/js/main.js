/**
 * Created by Knight on 2015/7/27.
 */
baseUrl = "..";

$(document).ready(function(){
    var href= window.location.href.split( "/" );
    href=href[href.length-1].split( "." )[0];
    console.log(href);
    initAll(href);
});

function initAll(param){

    initSileBar(param);
    initTable(param);


}

function initTable(param){

    var html="";

    switch (param){
        case "dream":
            html = '<th class="table-id">ID</th><th class="table-title">内容</th><th class="table-set">操作</th>' ;
            break;
        case "sensitive":
            html = '<th class="table-id">ID</th><th class="table-title">敏感词</th><th class="table-set">操作</th>';
            break;
        case "case":
            html = '<th class="table-id">ID</th><th class="table-title">标题</th><th class="table-title">链接</th><th class="table-set">操作</th>';
            break;
        case "file":
            html = '<th class="table-id">ID</th><th class="table-title">标题</th><th class="table-title">链接</th><th class="table-set">操作</th>';
            break;
        default :
            break;
    }



    if(html!=""){
        $(".table-main thead tr").empty().append(html);
    }


}

/**
 * 初始化 左边导航栏
 */
function initSileBar(param){


    var checkedHtml='<span class="am-icon-check am-fr am-margin-right admin-icon-red"></span>';

    var html =
        //'<li data-col="index"><a href="#"><span class="am-icon-home"></span> 首页</a></li>' +
        '<li data-col="user"><a href="user.html"><span class="am-icon-users"></span> 用户</a></li>' +
        '<li data-col="inquiry"><a href="inquiry.html"><span class="am-icon-pencil-square-o"></span> 梦想/询价</a></li>' +
        '<li data-col="file"><a href="file.html"><span class="am-icon-download"></span> 文件下载</a></li>' +
        '<li data-col="case"><a href="case.html"><span class="am-icon-archive"></span> 案例分析</a></li>' +
        '<li data-col="sensitive"><a href="sensitive.html"><span class="am-icon-warning"></span> 敏感词</a></li>' +
        '<li data-col="dream"><a href="dream.html"><span class="am-icon-birthday-cake"></span> 梦想语录</a></li>';

    $(".admin-sidebar-list").empty().append(html);

    $(".admin-sidebar-list li[data-col='"+param+"'] a").append(checkedHtml);


}

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
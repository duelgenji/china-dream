/**
 * Created by Knight on 2015/7/27.
 */


var page=getParam("index")?getParam("index"):0;
var keyword=getParam("key")?getParam("key"):"";

function ajaxRetrieveUserList() {

    var params={};
    params.size=20;
    params.type=0;
    params.removed=true;
    params.page=page;
    params.key=decodeURI(keyword);


    var url="/userInfo/retrieveUserList";
    if(params.key){
        url="/userInfo/searchUserList"
    }

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data : params,
        success: function (data) {
            if (data.success == 1) {
                console.log(data)
                updateTable(data.data);
            } else {
                alert(data.message);
            }
        },
        error: function(){
            alert("请求错误");
        }
        //complete
    })
}

function ajaxRemove(params) {
    var url="/backend/removeUser";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data:params,
        success: function (data) {
            if (data.success == 1) {
                location.reload();
            } else {
                alert(data.message);
            }
        },
        error: function(){
            alert("请求错误,请尝试点击右上角退出重新登录!");
        }
        //complete
    })
}


function ajaxUpgrade(params) {
    var url="/backend/upgradeUser";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data:params,
        success: function (data) {
            if (data.success == 1) {
                location.reload();
            } else {
                alert(data.message);
            }
        },
        error: function(){
            alert("请求错误,请尝试点击右上角退出重新登录!");
        }
        //complete
    })
}


function ajaxTest(params) {
    var url="/backend/testUser";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data:params,
        success: function (data) {
            if (data.success == 1) {
                location.reload();
            } else {
                alert(data.message);
            }
        },
        error: function(){
            alert("请求错误,请尝试点击右上角退出重新登录!");
        }
        //complete
    })
}

var userType=["","个人","公司","群"];
var html="";
var vip="";
var obj;
var logoUrl="";
function updateTable(data){
    html="";
    for(var i=0;i<data.length;i++){
        obj=data[i];
        logoUrl="";
        vip='';
        if(obj.logoUrl!=""){
            logoUrl=obj.logoUrl+"?imageView2/2/w/40&name=dl.jpg";
        }
        if(obj.VIP==1){
            vip +=' <span class="am-icon-credit-card admin-icon-red" title="认证"></span>';
        }
        if(obj.removed){
            vip +=' <span class="am-icon-ban admin-icon-red" title="封停"></span>';
        }
        if(obj.test){
            vip +=' <span class="am-icon-user-secret admin-icon-orange" title="测试"></span>';
        }
        html+=
            '<tr>' +
            '<td>'+obj.userId+'</td>' +
            '<td><a target="_blank" href="/html/userDetail.html?key='+obj.userId+'">'+ obj.nickname+vip+'</a></td>' +
            '<td>'+userType[obj.userType]+'</td>' +
            '<td class="am-hide-sm-only">'+obj.industry+'</td>' +
            '<td class="am-hide-sm-only">'+obj.createDate+'</td>' +
            '<td class="am-hide-sm-only"><img class="small-logo" src="'+logoUrl+'"/></td>' +
            '<td data-id="'+obj.userId+'">' +
            '<div class="am-btn-toolbar">' +
            '<div class="am-btn-group am-btn-group-xs">' +
            '<button type="button" class="am-btn am-btn-default am-btn-xs am-text-warning am-hide-sm-only btn-upgrade"><span class="am-icon-trash-o"></span> 认证</button>' +
            '<button type="button" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only btn-remove"><span class="am-icon-trash-o"></span> 封停</button>' +
            '<button type="button" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only btn-test"><span class="am-icon-trash-o"></span> 测试</button>' +
            '</div>' +
            '</div>' +
            '</td>' +
            '</tr>';
    }

    $("#main-table").empty().append(html);

}


$(document).ready(function(){
    ajaxRetrieveUserList();
    //点击搜索 调用请求数据
    $(".am-btn-default").on("click",function(){
        self.location="user.html?index=0&key="+$(".am-form-field").val();

    });

    /* 删除 click */
    $(document).on("click",".btn-remove", function() {
        $('#my-confirm').modal({
            relatedTarget: this,
            onConfirm: function() {
                var $link = $(this.relatedTarget).closest('td');
                var id = $link.data('id');
                var params={};
                params.id=id;
                ajaxRemove(params);
            }
        });
    });

    /* 认证 click */
    $(document).on("click",".btn-upgrade", function() {
        $('#my-confirm-upgrade').modal({
            relatedTarget: this,
            onConfirm: function() {
                var $link = $(this.relatedTarget).closest('td');
                var id = $link.data('id');
                var params={};
                params.id=id;
                ajaxUpgrade(params);
            }
        });
    });

    /* 认证 click */
    $(document).on("click",".btn-test", function() {
        $('#my-confirm-test').modal({
            relatedTarget: this,
            onConfirm: function() {
                var $link = $(this.relatedTarget).closest('td');
                var id = $link.data('id');
                var params={};
                params.id=id;
                ajaxTest(params);
            }
        });
    });

    $("#page-prev").on("click",function(){
        if(page>=1){
            self.location="user.html?index="+(page-1)+"&key="+keyword;
        }
    });

    $("#page-next").on("click",function(){
        self.location="user.html?index="+(parseInt(page)+1)+"&key="+keyword;
    });
});
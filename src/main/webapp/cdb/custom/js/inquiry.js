/**
 * Created by Knight on 2015/7/27.
 */

var page=getParam("index")?getParam("index"):0;
var keyword=getParam("key")?getParam("key"):"";

function ajaxRetrieveInquiryList() {


    var params={};
    params.size=20;
    params.page=page;
    params.key=decodeURI(keyword);

    var url="/inquiry/retrieveInquiryList";
    if(params.key){
        url="/inquiry/searchInquiryList"
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

    var url="/backend/removeInquiry";

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

var html="";
var obj;
var logoUrl="";
function updateTable(data){
    html="";
    for(var i=0;i<data.length;i++){
        obj=data[i];
        logoUrl="";
        if(obj.logoUrl!=""){
            logoUrl=obj.logoUrl+"?imageView2/2/w/40&name=dl.jpg";
        }
        html+=
            '<tr>' +
            '<td>'+obj.id+'</td>' +
            '<td><a target="_blank" href="/html/inquiryDetail.html?key='+obj.id+'">'+ obj.title+'</a></td>' +
            '<td>'+obj.userName+'</td>' +
            '<td class="am-hide-sm-only">'+obj.inquiryNo+'</td>' +
            '<td class="am-hide-sm-only">'+obj.limitDate+'</td>' +
            '<td class="am-hide-sm-only"><img class="small-logo" src="'+logoUrl+'"/></td>' +
            '<td data-id="'+obj.id+'">' +
            '<div class="am-btn-toolbar">' +
            '<div class="am-btn-group am-btn-group-xs">' +
            '<button type="button" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only btn-remove"><span class="am-icon-trash-o"></span> 删除</button>' +
            '</div>' +
            '</div>' +
            '</td>' +
            '</tr>';
    }

    $("#main-table").empty().append(html);

}


$(document).ready(function(){


    ajaxRetrieveInquiryList();

    //点击搜索 调用请求数据
    $(".am-btn-default").on("click",function(){
        self.location="inquiry.html?index=0&key="+$(".am-form-field").val();

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

    $("#page-prev").on("click",function(){
        if(page>=1){
            self.location="inquiry.html?index="+(page-1)+"&key="+keyword;
        }
    });

    $("#page-next").on("click",function(){
        self.location="inquiry.html?index="+(parseInt(page)+1)+"&key="+keyword;
    });

});
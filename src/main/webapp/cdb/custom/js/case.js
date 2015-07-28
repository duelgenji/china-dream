/**
 * Created by Knight on 2015/7/28.
 */

var page=getParam("index")?getParam("index"):0;

function ajaxRetrieveList() {

    var url="/dreamCase/retrieveDreamCaseList";

    var params={};
    params.size=15;
    params.page=page;

    console.log(params);
    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data:params,
        success: function (data) {
            if (data.success == 1) {
                updateTable(data.data.content);
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

function ajaxGenerate(params) {

    var url="/dreamCase/generateDreamCase";

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

function ajaxRemove(params) {

    var url="/dreamCase/removeDreamCase";

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
function updateTable(data){
    html="";
    for(var i=0;i<data.length;i++){
        obj=data[i];
        html+=
            '<tr>' +
            '<td>'+obj.id+'</td>' +
            '<td class="am-hide-sm-only">'+obj.title+'</td>' +
            '<td class="am-hide-sm-only">'+obj.url+'</td>' +
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

    ajaxRetrieveList();

    /* 新增 click */
    $('#doc-prompt-toggle').on('click', function() {
        var title=$("#title").val();
        var url=$("#url").val();

        if(title=="" || url==""){
            alert("输入不能为空");
            return;
        }

        var params={};
        params.title=title;
        params.url=url;
        ajaxGenerate(params);
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
            self.location="case.html?index="+(page-1);
        }
    });

    $("#page-next").on("click",function(){
        self.location="case.html?index="+(parseInt(page)+1);
    });
});
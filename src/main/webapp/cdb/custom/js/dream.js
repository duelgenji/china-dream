/**
 * Created by Knight on 2015/7/28.
 */

/**
 * Created by Knight on 2015/7/27.
 */

function ajaxRetrieveList() {

    var url="/dreamWord/retrieveDreamWordList";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        success: function (data) {
            if (data.success == 1) {
                console.log(data);
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


function ajaxGenerate(params) {

    var url="/dreamWord/generateDreamWord";

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

function ajaxGenerateList(params) {

    var url="/dreamWord/generateDreamWords";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data: params,
        success: function (data) {
            if (data.success == 1) {
                alert("成功增加"+data.total+"条(重复数据:"+data.same+")");
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

    var url="/dreamWord/removeDreamWord";

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
        html+=
            '<tr>' +
            '<td>'+obj.id+'</td>' +
            '<td class="am-hide-sm-only">'+obj.content+'</td>' +
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


    $("#doc-modal-toggle").on('click', function(e) {
        $('#my-modal').modal('toggle');
    });

    $("#modal-submit").on("click",function(){

        var words = $("#words").val();

        if(!words){
            alert("请输入内容");
            return;
        }

        words = words.split("\n");
        var arr = [];

        for(var i = 0;i<words.length;i++){
            if(words[i])
                arr.push(words[i])
        }
        var params = {};
        params.words = arr.toString();

        console.log(params);
        ajaxGenerateList(params);

    });


    ajaxRetrieveList();

    /* 新增 click */
    $('#doc-prompt-toggle').on('click', function() {
        $('#my-prompt').modal({
            relatedTarget: this,
            onConfirm: function(e) {
                var content= e.data || '';
                if(content){
                    var params={};
                    params.word=content;
                    ajaxGenerate(params);

                }
            }
        });
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

});
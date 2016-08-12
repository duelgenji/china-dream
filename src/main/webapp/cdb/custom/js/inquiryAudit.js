/**
 * Created by Knight on 2015/7/27.
 */

var page=getParam("index")?getParam("index"):0;

function ajaxRetrieveInquiryList() {


    var params={};
    params.size=10;
    params.auditStatus=0;

    var url="/backend/auditInquiryList";


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

function ajaxAuditInquiry(params) {
    var url="/backend/auditInquiry";

    $.ajax({
        url: baseUrl + url,
        type : "post",
        async:false,
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
            '<tr data-id="'+obj.id+'" data-json='+encodeURI(JSON.stringify(obj))+'>' +
            '<td>'+obj.id+'</td>' +
            '<td>'+obj.title+'</td>' +
            '<td>'+obj.userName+'</td>' +
            '<td class="am-hide-sm-only">'+obj.inquiryNo+'</td>' +
            '<td class="am-hide-sm-only">'+obj.limitDate+'</td>' +
            '<td class="am-hide-sm-only"><img class="small-logo" src="'+logoUrl+'"/></td>' +
            '<td data-id="'+obj.id+'">' +
            '<div class="am-btn-toolbar">' +
            '<div class="am-btn-group am-btn-group-xs">' +
            '<button type="button" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only btn-check"> 查看</button>' +
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
    $("#btn-search").on("click",function(){
        self.location="inquiryAudit.html?index=0&key="+$(".am-form-field").val();
    });


    /* 查看 */
    $(document).on("click",".btn-check", function() {
        var id = $(this).closest('tr').data('id');
        $('#modal-amount').modal("open");
        var json = $(this).closest('tr').data('json');
        json = eval("("+decodeURI(json)+")");
        $("#inquiryId").val(id);
        for(var i in json){

            if(i=="fileList"){
                var html = "";
                for(var j = 0 ;j<json[i].length;j++){
                    html += '<a href="'+json[i][j].fileUrl+'">'+json[i][j].remark+'</a><br/>';
                }
                $("div.am-u-sm-9[data-key="+i+"]").html(html);

            }else if(i=="logoUrl"){
                var html = '<img src="'+json[i]+'" />';
                $("div.am-u-sm-9[data-key="+i+"]").html(html);
            }
            else{
                $("div.am-u-sm-9[data-key="+i+"]").html(json[i]);
            }

        }
    });

    /* 通过 */
    $(document).on("click","#btn_accept", function() {
        var params={};
        params.inquiryId=$("#inquiryId").val();
        params.auditStatus=2;
        console.log(params);
        ajaxAuditInquiry(params);
    });
    /* 拒绝 */
    $(document).on("click","#btn_refuse", function() {
        var params={};
        params.inquiryId=$("#inquiryId").val();
        params.auditStatus=1;
        console.log(params);
        ajaxAuditInquiry(params);
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
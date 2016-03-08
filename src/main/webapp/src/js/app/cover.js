/**
 * Created by knight on 16/2/26.
 */
$(document).ready(function(){
    $("#go_personal").on("click",function(){
        $("#tab1").removeClass("am-active");
        $("#tab2").addClass("am-active");
    });
    $("#go_company").on("click",function(){
        $("#tab2").removeClass("am-active");
        $("#tab1").addClass("am-active");
    });

    $(".detail-pre").on("click",function(){})


    $("#register").on("click",function(){

        if($("#tab1").hasClass("am-active")){
            c_reg();
        }else{
            p_reg();
        }
    });

    $("input").on("change",function(){
        $(this).removeClass("am-field-error");
    });

    function c_reg(){
        var json = {};

        var $doms = $("#tab1").find("input,select");
        for(var i = 0 ;i<$doms.length;i++){
            var $dom = $($doms[i]);
            if($dom.attr("required") && !$dom.val()){
                $dom.addClass("am-field-error");
                return;
            }
            if($dom[0].tagName == "SELECT" && $dom.val()=="0") {
                continue;
            }
            json[$dom.attr("name")] =$dom.val();

        }
        json.type=2;
        register(json);


    }

    function p_reg(){
        var json = {};

        var $doms = $("#tab2").find("input,select");
        for(var i = 0 ;i<$doms.length;i++){
            var $dom = $($doms[i]);
            if($dom.attr("required") && !$dom.val()){
                $dom.addClass("am-field-error");
                return;
            }
            if($dom[0].tagName == "SELECT" && $dom.val()=="0") {
                continue;
            }
            json[$dom.attr("name")] = $dom.val();

        }
        json.type=1;
        register(json);
    }

    function register(json){

        //console.log(json);
        $.ajax({
            url : "../user/quickRegister",
            type :"post",
            data : json,
            success:function(result){
                if(result.success==1){
                    $("#my-alert").modal("open");
                }else{
                    alert(result.message);
                }
            }
        });
    }
});

/**
 * Created by knight on 16/2/26.
 */
$(document).ready(function(){
    $("#go_personal").on("click",function(){
        aaa();
        $("#tab1").removeClass("am-active");
        $("#tab2").addClass("am-active");
    });
    $("#go_company").on("click",function(){
        $("#tab2").removeClass("am-active");
        $("#tab1").addClass("am-active");
    });
});

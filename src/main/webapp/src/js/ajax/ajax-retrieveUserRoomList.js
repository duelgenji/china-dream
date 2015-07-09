function ajaxRetriveUserRoomList(params, ok, fail, error) {

    var url="/userInfo/retrieveUserList";
    if(params.key){
        url="/userInfo/searchUserList"
    }

    $.ajax({
        url: baseUrl + url,
        type : "post",
        dataType: "json",
        data : params,
        success: function (result) {
            if (result.success == 1) {
                ok(result);
            } else {
                fail(result);
            }
        },
        error: error
        //complete
    })
}
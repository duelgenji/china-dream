function ajaxRetriveUserRoomList(params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/userInfo/retrieveUserList",
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
function ajaxRetrieveUserDetail(userId, ok, fail, error) {

    var data = {}
    data.userId = userId;

    $.ajax({
        url: baseUrl + "/quotation/retrieveUserDetail",
        type : "post",
        dataType: "json",
        data : data,
        success: function (result) {
            if (result.success == 1) {
                ok(result);
            } else {
                if (fail) {
                    fail(result);
                } else {
                    alert(result.message);
                }
            }
        },
        error: error
        //complete
    })
}
function ajaxmodify(params, ok, fail, error) {
    $.ajax({
        url: "../user/modify",
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
function ajaxregister(params, ok, fail, error) {
    console.log(12222132);
    $.ajax({
        url: "../user/register",
        type : "post",
        dataType: "json",
        data : params,
        success: function (result) {
            console.log(result);
            if (result.success == 1) {
                ok(result);
            } else {
                fail();
            }
        },
        error: error
        //complete
    })
}
function ajaxforgetpassword(email, ok, fail, error) {

    var data = {}
    data.email = email;

    $.ajax({
        url: "../user/forgetPassword",
        type : "post",
        dataType: "json",
        data : data,
        success: function (result) {
            console.log(result);
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
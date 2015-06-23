function ajaxlogin(email, password, ok, fail, error) {

    var data = {}
    data.email = email;
    data.password = password;

    $.ajax({
        url: "../user/login",
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
function ajaxLogin(email, password, ok, fail, error) {

    var data = {}
    data.email = email;
    data.password = password;

    $.ajax({
        url: baseUrl + "/user/login",
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
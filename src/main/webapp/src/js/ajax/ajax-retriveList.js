function ajaxretriveList(params, ok, fail, error) {
    $.ajax({
        url: "http://10.0.0.98:8080/inquiry/retrieveInquiryList",
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
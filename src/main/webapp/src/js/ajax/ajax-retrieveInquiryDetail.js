function ajaxretriveDetail(params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/inquiry/retrieveInquiryDetail",
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
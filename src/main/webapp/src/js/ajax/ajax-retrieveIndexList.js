function ajaxRetriveIndexList(params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/inquiry/retrieveInquiryList",
        type : "post",
        dataType: "json",
        data : params,
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
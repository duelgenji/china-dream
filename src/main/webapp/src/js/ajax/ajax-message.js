function ajaxSendInquiryMessage (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/inquiry/sendInquiryMessage",
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

function ajaxRetrieveMessageList (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/message/retrieveMessageList",
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
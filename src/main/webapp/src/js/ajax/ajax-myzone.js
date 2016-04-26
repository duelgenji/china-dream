/**
 * Created by Knight on 2015/7/8.
 */
function ajaxRetrieveMyQuotationList (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/quotation/retrieveMyQuotationList",
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

function ajaxRetrieveMyInquiryList (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/inquiry/retrieveMyInquiryList",
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

function ajaxRetrieveAccountLogList (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/userInfo/retrieveAccountLogList",
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
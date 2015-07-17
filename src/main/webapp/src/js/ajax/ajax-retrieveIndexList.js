function ajaxRetriveIndexList(params, ok, fail, error) {


    var url="/inquiry/retrieveInquiryList";
    if(params.key){
        url="/inquiry/searchInquiryList"
    }

    $.ajax({
        url: baseUrl + url,
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


function ajaxInquiryGoods(params, ok, fail, error) {


    var url="/inquiry/inquiryGood";

    $.ajax({
        url: baseUrl + url,
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


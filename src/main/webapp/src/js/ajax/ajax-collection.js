function ajaxGenerateCollection (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/generateCollection",
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

function ajaxCancelCollection (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/cancelCollection",
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

function ajaxRetrieveCollectionList (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/retrieveCollectionList",
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


function ajaxGenerateCollectionU (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/generateCollectionU",
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

function ajaxCancelCollectionU (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/cancelCollectionU",
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

function ajaxRetrieveCollectionListU (params, ok, fail, error) {
    $.ajax({
        url: baseUrl + "/collection/retrieveCollectionListU",
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
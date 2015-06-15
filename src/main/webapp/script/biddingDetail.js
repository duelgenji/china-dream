$(function () {
    $("#sendApplicationModal").modal({
        show: false
    });

    $("#btnSendApplication").click(function () {
        $("#sendApplicationModal").modal("show");
    });
});
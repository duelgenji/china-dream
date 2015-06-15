$(function () {
    $(".modal").modal({ show: false });

    $(".btnSuccess").click(function () {
        $("#successModal").modal("show");
    });

    $(".btnFail").click(function () {
        $("#failModal").modal("show");
    });
});
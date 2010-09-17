$(function() {
    $("#reportsTabs").tabs({selected: 0});
});

$("#getReportButton").livequery(function() {
    $(this).click(function() {
        window.location = $(this).siblings("a").attr("href");
    })
});
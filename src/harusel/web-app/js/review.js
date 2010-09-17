$(function() {
    $("#performanceReviewTabs").tabs({
        spinner: false,
        selected: 0,
        select: function(event, ui) {
            if ($(ui.tab).hasClass("separatedWindow")) {
                var url = $.data(ui.tab, 'load.tabs');
                if (url) {
                    window.open(url, "_blank");
                }
                return false;
            }
            return true;
        }
    });
});

$(".timelinesList").livequery(function() {
    $(".mark").each(function() {
        var content = $(this).attr("title");
        $(this).bt(content, {
            trigger: 'hover',
            positions: ['bottom', 'top', 'right', 'left'],
            shrinkToFit: true,
            fill: '#feffc5',
            strokeStyle: "#aeafa5",
            strokeWidth: 1,
            spikeLength: 10,
            spikeGirth: 10,
            padding: 15,
            cornerRadius: 10
        });
    })
});

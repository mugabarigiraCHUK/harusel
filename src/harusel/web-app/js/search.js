$(function() {
    var popup = $("#btnAddNew").bt($('#addPersonPopup').remove().html(), {
        trigger: 'click',
        positions: ['bottom'],
        width: 250,
        fill: '#bfddf3',
        strokeWidth: 0, /*no stroke*/
        spikeLength: 40,
        spikeGirth: 10,
        padding: 20,
        cornerRadius: 15,
        cssStyles: {
            fontFamily: '"lucida grande",tahoma,verdana,arial,sans-serif',
            fontSize: '13px'
        },
        cssClass: "popupForm"
    });

    $(".searchForm").livequery(function() {
        $("input[name='query']", this).focus();
    });

    $("#searchButton").click(function() {
        $(this).siblings("input[name='query']").focus();
    });

    $(".searchForm").live('submit', function() {
        var query = $(this).children("input[name='query']").val();
        $.post(this.action, $(this).serialize(), function(data) {
            $("#mainPane").html(data);
            $(".personListFullName>a").each(function() {
                var originalFullName = $(this).html();
                $(this).html(highlightSearchTermsInText(originalFullName, query));
            });
            // to fix bug with calling btOff without calling of btOn
            popup.btOn();
            popup.btOff();
            FilterManager.resetSelection();
            SelectionManager.init();
        });
        return false;
    });
});

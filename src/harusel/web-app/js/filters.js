$(function() {
    FilterManager.init();

    // Display first filter opened.
    $("#filterPane .parentFilter .viewSubFiltresIcon:first").click();
});

var FilterManager = {


    init : function() {

        this.hideSubfilters();

        $("#filterPane .filter .viewSubFiltresIcon").click(function() {
            $(this).parent().siblings(".subfilters").toggle("fast");
            $(this).toggleClass("opened").toggleClass("closed");
        });

        $("#filterPane a").click(function() {
            $("#filterPane a").each(function() {
                $(this).removeClass("selected");
            });
            $(this).addClass("selected");

            $("#filterPane .parentFilter").removeClass("style_openedPrnt");
            $(this).parents(".filter").children(".parentFilter").addClass("style_openedPrnt");
        });

        $("#filterPane a").next(".viewSubFiltresIcon")
                .removeClass("closed")
                .addClass("opened");
    },

    resetSelection: function() {
        this.setSelectedItem();
    },

    setSelectedItem: function(selectedItemId, parentItemId) {
        FilterManager.hideSubfilters();
        $("#filterPane .selected").removeClass("selected");
        $("#filterPane .opened").removeClass("opened").addClass("closed");
        $("[itemId='" + selectedItemId + "']")
                .addClass("selected")
                .parents(".subfilters")
                .show("fast");

        if (selectedItemId != parentItemId) {
            $("[itemId='" + parentItemId + "']")
                    .parent()
                    .next(".viewSubFiltresIcon")
                    .removeClass("closed")
                    .addClass("opened");
        }

        $("#filterPane .parentFilter").removeClass("style_openedPrnt");
        $("[itemId='" + parentItemId + "']").parents(".filter").children(".parentFilter").addClass("style_openedPrnt");
    },

    hideSubfilters: function() {
        $("#filterPane .subfilters").hide();
        $(".viewSubFiltresIcon").removeClass("opened").addClass("closed");
    },

    getSelectedItem: function() {
        var selectedFilter = $("#filterPane a.selected");
        var parentFilter = selectedFilter.parents(".filter");
        parentFilter = $("a.header", parentFilter);

        if (selectedFilter == parentFilter) {
            parentFilter = undefined;
        }
        return {
            selected : selectedFilter.attr('itemId'),
            parent: parentFilter.attr('itemId')
        };
    },

    reload: function() {
        var currentState = this.getSelectedItem();
        $("#filterPane").load(getPathTo("home/getFilters"), function() {
            FilterManager.init();
            FilterManager.setSelectedItem(currentState.selected, currentState.parent);
        });
    }
};

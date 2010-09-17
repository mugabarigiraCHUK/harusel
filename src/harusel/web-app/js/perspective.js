$(function() {
    var perspectiveMarkerElement = $(".perspectiveMarker");
    if (perspectiveMarkerElement.size() > 0) {
        $("#perspectiveSelector")[0].selectedIndex = perspectiveMarkerElement[0].id.split("_")[1];
        $("#perspectiveSelector").bind("change", onChangePerspective);
    }
});

function onChangePerspective() {
    var selectedPerspective = $(this).val();
    window.location = $("#baseUrl").text() + "/" + selectedPerspective;
}


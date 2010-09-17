$(function() {
    $(".transformationText").bind("click", makeEditor);
});

$(".messageBox").livequery(function() {
    var messageBox = $(this);
    setTimeout(function() {
        messageBox.hide("slow");
    }, 3 * 1000);
});

function makeEditor() {
    var cell = $(this);
    cell.unbind("click");
    cell.children("div").remove();
    var inputElement = cell.children("input");
    var textArea = $("<textarea>" + inputElement.val() + "</textarea>").addClass("area").appendTo(cell).focus();
    textArea.css("width", "100%");
    textArea.bind("blur", function() {
        var value = $(this).val();
        $(this).remove();
        $("<div><pre>" + value + "</pre></div>").appendTo(cell);

        inputElement.val(value);
        setTimeout(function() {
            cell.bind("click", makeEditor);
        }, 1000);
    })
}

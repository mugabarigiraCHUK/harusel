var saveRequestCounter = 0;

$(function() {
    $(".slider").each(function() {
        var originalValueHolder = $(this).next(":hidden");
        this.originalValueHolder = originalValueHolder[0];
        var value = originalValueHolder.val();
        $(this).slider({
            min:0,
            max:2,
            animate: true,
            value: value,
            change: onChangeSlider
        });
    });
    $(".transformationText").bind("click", showEditor);
    $(".controlButton").click(function() {
        if (saveRequestCounter > 0) {
            $.blockUI.defaults.message = "<pre style='margin:3em 3em;'>" +
                    getMessageProperty("performanceReview-label-waitRespond") +
                    "</pre>";
            $.blockUI();
            waitForRequestFinishing(this);
            return false;
        }
    })
});

function waitForRequestFinishing(anchor) {
    setTimeout(function() {
        if (saveRequestCounter > 0) {
            waitForRequestFinishing(anchor);
        } else {
            window.location = $(anchor).attr("href");
        }
    }, 100);
}

function showEditor() {
    var cell = $(this);
    cell.unbind("click");
    var valueHolder = cell.children("div");
    valueHolder.hide();
    if (!valueHolder[0].textArea) {
        valueHolder[0].textArea = $("<textarea/>").appendTo(cell).focus();
        valueHolder[0].textArea.css("width", "100%").css("hight", "30");
        valueHolder[0].textArea.bind("blur", onBlurEditor);
    }
    var originalValue = valueHolder.text();
    valueHolder[0].textArea.val(originalValue);
    valueHolder[0].textArea.show().focus();
    valueHolder[0].textArea[0].originalValue = originalValue;
}

function onBlurEditor() {
    var cell = $(this).parents("td");
    var value = $(this).val();
    $(this).hide();
    var contentHolder = cell.children("div");
    contentHolder.empty();
    $("<pre>").text(value).appendTo(contentHolder);
    contentHolder.show();
    if (value != this.originalValue) {
        var valueDescription = cell.attr("id").split("_");
        var valueName = valueDescription[0];
        var questionId = valueDescription[1];
        saveValue(questionId, valueName, value);
    }
    setTimeout(function() {
        cell.bind("click", showEditor);
    }, 1000);
}

function saveValue(questionId, valueName, value) {
    saveRequestCounter++;
    $.post(getPathTo("employee/saveAnswer"), {
        questionId: questionId,
        valueName : valueName,
        value: value
    }, function(data) {
        if (data === "ok") {
            saveRequestCounter--;
        } else {
            var result = JSON.parse(data);
            if (result.status === "failed") {
                addMessage(result.message, 'error-notify-message', 1);
            }
        }
    });
}

function onChangeSlider(event, ui) {
    var valueDescription = $(ui.handle).parents("div").attr("id").split("_");
    var valueName = valueDescription[0];
    var questionId = valueDescription[1];
    if ($(this.originalValueHolder).val() != ui.value) {
        saveValue(questionId, valueName, ui.value);
        $(this.originalValueHolder).val(ui.value);
    }
}

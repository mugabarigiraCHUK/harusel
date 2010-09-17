$("#saveAssigns").livequery(function() {
    $(this).click(function() {
        saveAssignedTemplates();
    })
});
$("#cancelAssigns").livequery(function() {
    $(this).click(function() {
        var selected = $("#performanceReviewTabs").tabs('option', 'selected');
        $("#performanceReviewTabs").tabs("load", selected);
        addMessage(getMessageProperty("common-label-canceled"));
    })
});

function saveAssignedTemplates() {
    var errorCount = 0;
    var assignments = [];
    $(".assigningTemplateTable select").removeClass("error");
    $(".assigningTemplateTable tbody tr").each(function() {
        var managerSelector = $("[name=assignedManager]", this);
        var templateSelector = $("[name=assignedTemplate]", this);
        var manager = managerSelector.val().split(" ");
        var template = templateSelector.val().split(" ");
        if (manager.length != template.length) {
            var selector;
            if (manager.length == 2) {
                managerSelector.addClass("error");
            } else {
                templateSelector.addClass("error");
            }
            errorCount++;
            return
        }
        var entry = {
            userId : manager[1],
            managerId : manager[2] ? manager[2] : "",
            templateId : template[2] ? template[2] : ""
        };
        assignments.push(entry);
    });
    if (errorCount > 0) {
        return;
    }

    var data = JSON.stringify(assignments);
    $.post(getPathTo("review/assign"), {assignments: data}, function(data) {
        addMessage(data);
    });
}
/**
 * Closes dialog which is given by 'this' object
 */
function closeButtonHandler() {
    $(this).dialog("destroy").remove();
}

function FieldValidator(field, rules) {
    this.field = field;
    this.rules = rules;
    if (!rules) {
        this.rules = [];
    }
    this.field.validator = this;

    var validateHandler = function(event) {
        this.validator.validate(event);
    };

    $(this.field).bind("keyup", validateHandler);
    $(this.field).bind("blur", validateHandler);
}

FieldValidator.prototype.email = function() {
    var emailRegexp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    var emails = $(this.field).val().split(";");
    var invalidMails = $.grep(emails, function(email) {
        return !emailRegexp.test($.trim(email))
    });
    return invalidMails.length == 0;
};

FieldValidator.prototype.required = function() {
    return $.trim($(this.field).val()) !== "";
};

FieldValidator.prototype.updateValid = function(isValid) {
    if (isValid) {
        $(this.field).removeClass("errorFormField");
    } else {
        $(this.field).addClass("errorFormField");
    }
    this.field.isValid = isValid;
};

FieldValidator.prototype.validate = function(event) {
    var isValid = true;
    for (var i in this.rules) {
        isValid = isValid && this[this.rules[i]].call(this);
    }
    this.updateValid(isValid);
};

function memberSize(obj) {
    var count = 0;
    for (var a in obj) {
        count++;
    }
    return count;
}

function sendMail(event) {
    var cancelButtonPane = new Object();
    cancelButtonPane[getMessageProperty("common-button-cancel")] = closeButtonHandler;

    $.get(getPathTo("mailTextTemplate/selectTemplateDialog"), null, function(data) {
        if (memberSize(data) != 0) {
            var templateChooserDom = createTemplateDialog(data);
            $(templateChooserDom).dialog({
                title: getMessageProperty("dialog-title-selectMailTemplate"),
                bgiframe: true,
                modal: true,
                close: closeButtonHandler,
                buttons: cancelButtonPane
            }).dialog("option", "width", "420");
        } else {
            // there are no templates yet
            composeMail(SelectionManager.selectedPersonList[0].id);
        }
    }, "json");
}

function createTemplateDialog(data) {
    var dialog = $("<div>");
    for (var group in data) {
        var groupSet = $("<div class='template'>").appendTo(dialog);
        var groupTitle = $("<div class='templateGroupTitle'>").appendTo(groupSet);
        groupTitle.text(group);
        var templateList = $("<ul class='templateList'>").appendTo(groupSet);
        var templates = data[group];
        for (var i in templates) {
            var template = templates[i];
            var templateItem = $("<li class='templateName'>").appendTo(templateList);
            var link = $("<a href='#' class='templateList'>").appendTo(templateItem);
            link.text(template.name);
            $(link).bind("click", {dialog: dialog, templateId : template.id}, function(event) {
                $(event.data.dialog).dialog("close");
                composeMail(SelectionManager.selectedPersonList[0].id, event.data.templateId);
                event.stopPropagation();
                return false;
            });
        }
    }
    return dialog;
}

var editor;

function initFields() {
    $(this).find(".field").each(function(i, element) {
        var classNames = element.className.split(" ");
        classNames = $.grep(classNames, function(className) {
            return $.trim(className) !== "field";
        });
        new FieldValidator(element, classNames);
    });

    createCKEditor("#mailText");
}

function createCKEditor(selector) {
    var textArea = $(selector);
    editor = CKEDITOR.appendTo('mailTextEditor');
    editor.setData(textArea.val());
    textArea.ckeditor = editor;
}

function disposeCKEditor() {
    editor.destroy();
}

function retrieveCKEditorContent() {
    $("#mailText").val(editor.getData());
}

function composeMail(personId, templateId) {
    var buttons = new Object();
    buttons[getMessageProperty("common-button-cancel")] = closeButtonHandler;
    buttons[getMessageProperty("button-sendEmail")] = postMail;

    var data = {personId: personId};
    if (templateId) {
        data.id = templateId;
    }
    $("<div>").load(getPathTo("mailTextTemplate/mailComposer"), data, initFields).dialog(
    {
        title: getMessageProperty("dialog-title-composeEmail"),
        bgiframe: true,
        modal: true,
        resizable: false,
        close: function() {
            disposeCKEditor.call(this);
            closeButtonHandler.call(this)
        },
        buttons: buttons,
        width: 855
    });
}

function fireUpdate() {
    $("#toolbar").triggerHandler("sendMail");
}

function postMail() {
    retrieveCKEditorContent();
    var formFields = $(this).find(".field");
    var isValid = $.grep(formFields, function(element) {
        element.validator.validate();
        return !element.isValid
    }).length === 0;
    if (isValid) {
        var formData = $(this).find("form").serialize();
        var dialog = this;
        $.post(getPathTo("mailTextTemplate/sendMail"), formData, function(response) {
            if (response.error) {
                showErrors(response.error);
            } else {
                closeButtonHandler.call(dialog);
                if (response.message) {
                    addMessage(response.message);
                }
                fireUpdate();
            }
        }, "json");
    }
}

function showErrors(errors) {
    var buttons = new Object();
    buttons[getMessageProperty("common-button-ok")] = function() {
        $(this).dialog("close");
    };

    var ul = $("<ul>").addClass("errors");
    $.each(errors, function(i, error) {
        $("<li>" + error + "</li>").addClass("error").appendTo(ul);
    });
    $("<div>").append(ul).dialog({
        title: getMessageProperty("dialog-title-errorMessage"),
        modal: true,
        resizeble: false,
        buttons: buttons
    })
}
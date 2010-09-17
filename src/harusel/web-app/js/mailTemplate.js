$(function() {
    createCKEditor("mailText");
    $(".retrieveCKEditorBeforeSubmit").bind("submit", function() {
        retrieveCKEditorContent("mailText");
    });
});

function createCKEditor(selector) {
    var textArea = $("#" + selector)[0];
    if (textArea) {
        textArea.ckeditor = CKEDITOR.appendTo(selector + 'Editor');
        textArea.ckeditor.setData($(textArea).val());
    }
}

function disposeCKEditor(selector) {
    retrieveCKEditorContent(selector);
    $("#" + selector)[0].ckeditor.destroy();
}

function retrieveCKEditorContent(selector) {
    var textArea = $("#" + selector);
    if (textArea) {
        textArea.val(textArea[0].ckeditor.getData());
    }
}


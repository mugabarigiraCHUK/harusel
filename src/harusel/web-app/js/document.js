$('.ajaxForm').livequery(function() {
    $(this).ajaxForm({ target: this.parentNode })
})

function openDocumentUploadDialog() {

    var panel, formUrl = getPathTo('document/uploadForm/' + getSelectedUserIdList()[0])

    function arrangeDialog() {
        $('.otherDocTypeOption').each(function() {
            var option = $(this)

            var prefix = option.attr('name').match(/(.*])/)[1]

            var textField = $('[name=' + prefix + '.typeName]', panel).click(function() {
                option.attr('checked', true)
            })

            $('[name=' + prefix + '.selectButton]', panel).click(function() {
                var dialog = $('<div>').dialog({
                    title: 'Select the type',
                    bgiframe: true,
                    modal: true,
                    resizable: false,
                    width: 230
                })
                var select = $('select', panel).clone().change(function() {
                    textField.attr('value', select.attr('value'))
                    option.attr('checked', true)
                    dialog.dialog('close')
                }).appendTo(dialog)
            })
        })
    }

    function closeDialog() {
        panel.dialog('destroy').remove()
    }

    var panelButtons = new Object();
    panelButtons[getMessageProperty('common-button-cancel')] = closeDialog;
    panelButtons[getMessageProperty('common-button-upload')] = function() {
        $('form', panel).ajaxSubmit({
            iframe: true,
            success: function(response) {
                if (!/error/.test(response)) {
                    closeDialog()
                    $("#toolbar").triggerHandler("addDoc");
                    $('#notificationHolder').html(response);
                } else {
                    $('.errors', panel).css('display', '').html(response)
                }
            }
        })
    };

    panel = $('<div>').load(formUrl, arrangeDialog).dialog({
        title: getMessageProperty('toolbar-addDocument-title'),
        bgiframe: true,
        modal: true,
        width: 500,
        height: 400,
        close: closeDialog,
        buttons: panelButtons
    })
}
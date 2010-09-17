$(function() {
    $(".ui-state-default").livequery(function() {
        $(this).hover(function() {
            $(this).addClass("ui-state-hover")
        }, function() {
            $(this).removeClass("ui-state-hover")
        });
    })
});


$(".htmlTextHolder").livequery(function() {
    var htmlText = $(this).val();
    $(this).next("span").html(htmlText);
});


function getMessageProperty(key) {
    var valueHolder = $('.message-property.' + key);
    if (valueHolder.size() <= 0) {
        return key;
    } else {
        return valueHolder.text();
    }
}

function loginForm() {
    window.location = getPathTo("login/auth");
}

$(function() {
    if ($("#noAjaxBlockMarker").size() == 0) {
        $.blockUI.defaults.message = "<div class='spinner'></div>";
        $.blockUI.defaults.overlayCSS = {cursor: 'wait', backgroundColor: 'transparent'};
        $.blockUI.defaults.fadeIn = 0;
        $.blockUI.defaults.fadeOut = 0;
        $.blockUI.defaults.baseZ = 2000;

        $().ajaxStart($.blockUI).ajaxStop($.unblockUI)
    }
    $().ajaxStart(function() {
        $("#notificationHolder").children().each(function() {
            var element = $(this)[0];
            var count = element.requestNumberToIgnoreBeforeClean ? element.requestNumberToIgnoreBeforeClean : 0;
            if (count <= 0) {
                $(this).remove();
            }
            element.requestNumberToIgnoreBeforeClean = count - 1;
        })
    });
    $(".notify,.error-notify").livequery(function() {
        var object = $(this);
        addMessage(object.text(), object.attr("class") + "-message", 0)
    });
});

function addMessage(text, cssClass, requestNumberToIgnoreBeforeClean) {
    if (!requestNumberToIgnoreBeforeClean) {
        requestNumberToIgnoreBeforeClean = 0
    }
    if (!cssClass) {
        cssClass = 'notify-message'
    }
    var messageElement = $("<div class='" + cssClass + "'>").text(text).appendTo($("#notificationHolder"));
    messageElement[0].requestNumberToIgnoreBeforeClean = requestNumberToIgnoreBeforeClean;
}

/**
 * Returns absolute path to application resourses
 * @param path relative path
 */
function getPathTo(path) {
    var baseUrl = $("#baseUrl");
    return baseUrl.text() + "/" + path;
}


/**
 * Make string of joined via semicolon mails clickable
 * @param mails string of joined via semicolon mails
 */
function wrapWithMailTo(mails) {
    var list = [];
    mails.split(";").forEach(function(element) {
        list.push(element.link("mailto:" + element));
    });
    return list.join("; ");
}

/**
 * Use for make decisions Yes, Cancel.
 * @param title
 * @param message
 * @param callbackFn
 * @param callbackThis
 */
function OptionalPane(title, message, callbackFn, callbackThis) {
    this.callbackFn = callbackFn;
    this.callbackThis = callbackThis;

    var dialogContainer = $("#helperElement");
    var html = "<div id='dialog' title='" + title + "'><span>" + message + "</span></div>";
    dialogContainer.html(html);

    var self = this;
    var buttons = new Object();
    buttons[getMessageProperty('toolbar-optionpane-no')] = function() {
        self.dlg.dialog('close');
    };
    buttons[getMessageProperty('toolbar-optionpane-yes')] = function() {
        self.callbackFn.call(callbackThis, self);
        self.dlg.dialog('close');
    };
    this.dlg = $("#dialog").dialog({
        bgiframe: true,
        autoOpen: true,
        modal: true,
        width: 'auto',
        closeOnEscape: false,
        close: function() {
            self.dispose();
        },
        buttons: buttons
    });

    this.close = function() {
        this.dlg.dialog('close');
    },

        this.dispose = function() {
            this.dlg.empty();
        };

}

/**
 * Display message on the standalone dialog
 * @param title title of the dialog
 * @param message message to display
 */
function MessageBox(title, message, isModal) {
    var dialogContainer = $("<div id='dialog' title='" + title + "'><span>" + message + "</span></div>");
    var buttons = new Object();
    buttons[getMessageProperty("common-button-ok")] = function() {
        $(this).dialog("close");
    }

    $(dialogContainer).dialog({
        bgiframe: true,
        autoOpen: true,
        modal: isModal,
        maxWidth: 600,
        maxHiegnt: 600,
        closeOnEscape: true,
        buttons: buttons
    });
}

/**
 * Special dialog to make decisions
 * @param dialogUrl link to load content of dialog
 * @param currentSelected list of selected items (for radio and check -boxes)
 * @param callbackThis object reference for callback on ok action
 * @param callback callback function for callback on ok action
 */
function ChooserModalDialog(dialogUrl, currentSelected, callbackThis, callback) {
    this.url = getPathTo(dialogUrl);
    this.callback = callback;
    this.callbackThis = callbackThis;
    this.current = currentSelected;
    this.selected = [];

    this.confirmDialog = function(dlg) {
        var self = this;
        self.selected = [];
        $(":checkbox,:radio", dlg).each(function() {
            if (this.checked) {
                self.selected.push({id:this.value, name: $(this).next(":label").text()});
            }
        });
        this.closeDialog(dlg);
        this.callback.call(this.callbackThis, this);
    };

    this.closeDialog = function(dlg) {
        if (!dlg) {
            dlg = this.dlg;
        }
        dlg.dialog('close');
        dlg.remove();
    };

    this.showDialog = function(template) {
        var self = this;

        var buttons = new Object();
        buttons[getMessageProperty("common-button-cancel")] = function() {
            self.closeDialog(dlg);
        };
        buttons[getMessageProperty("common-button-ok")] = function() {
            self.confirmDialog(dlg);
        };
        var dlg = $(template).dialog({
            bgiframe: true,
            autoOpen: false,
            modal: true,
            maxWidth: 600,
            maxHeight: 800,
            buttons: buttons
        });

        $(":checkbox,:radio", dlg).each(function() {
            this.checked = (self.current.indexOf(this.value) > -1);
        });

        this.dlg = dlg;
        dlg.dialog('open');
    };

    var self = this;
    $.ajax({
        url: self.url,
        dataType: 'html',
        success: function(msg) {
            self.showDialog(msg);
        }
    });
}

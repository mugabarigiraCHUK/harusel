$(function() {
    Toolbar.init();
});


var Toolbar = {
    buttons: [],
    setStageButton: null,
    candidateOperations: null,
    addDoc: null,
    addScore: null,
    addComment: null,

    init: function() {
        var self = this;
        this.buttons = [];
        $("#toolbar > .toolbarButton").each(function(index, element) {
            var button = new ToolbarStageButton(element);
            self.buttons.push(button);
            button.setDisabled(true);
        });

        self.actionButtons = [];
        var actionButtonsDescription = [
            // array of [selector, callback function, minSelectionToEnable, maxSelectionToEnable, eventType]
            {
                selector:"#setStageButton",
                callback: "buttonSetStageButtonClicked",
                minSelection: 1,
                maxSelection: Number.MAX_VALUE
            },
            {
                selector:"#addDoc",
                callback: "buttonAddDocClicked",
                minSelection: 1,
                maxSelection: 1
            },
            {
                selector:"#addScore",
                callback:"buttonAddScoreClicked",
                minSelection:1,
                maxSelection:1
            },
            {
                selector:"#addComment",
                callback:"buttonAddCommentClicked",
                minSelection:1,
                maxSelection:Number.MAX_VALUE
            },
            {
                selector:"#sendEmail",
                callback:"buttonSendEmailClicked",
                minSelection:1,
                maxSelection:1
            },
            {
                selector:"#candidateOperations",
                callback:"buttonCandidateOperationsChanged",
                minSelection:1,
                maxSelection:Number.MAX_VALUE,
                eventType: "change"
            }
        ];

        for (var i = 0; i < actionButtonsDescription.length; i++) {
            var actionButton = new Button(actionButtonsDescription[i].selector,
                Toolbar[actionButtonsDescription[i].callback],
                actionButtonsDescription[i].minSelection, actionButtonsDescription[i].maxSelection,
                actionButtonsDescription[i].eventType);
            self.actionButtons.push(actionButton);
        }

        SelectionManager.updateListeners[this] = this.updateState;

        this.updateState();
    },

    buttonSendEmailClicked : function(event) {
        sendMail(event);
    },

    buttonAddCommentClicked: function(/*event*/) {
        new AddNoteDialog(this, this.addNote);
    },

    buttonAddScoreClicked: function(/*event*/) {
        ScoresManager.addScore(getSelectedUserIdList()[0]);
    },

    buttonAddDocClicked: function(/*event*/) {
        openDocumentUploadDialog();
    },

    buttonSetStageButtonClicked: function() {
        new QuickSetStageDialog(getPathTo("person/directChangeStageDialog"));
    },

    buttonCandidateOperationsChanged: function(event) {
        if (event.target.value == "") {
            return;
        }
        this[event.target.value].call(this);
        event.target.value = "";
    }
    ,

    subscribe: function(dlg) {
        var personIdList = getSelectedUserIdList();
        if (!dlg) {
            new OptionalPane(getMessageProperty('toolbar-subscribe-title'), getMessageProperty('toolbar-subscribe-content'), this.subscribe, this);
            return;
        }
        $.ajax({
            url: getPathTo("home/subscribe"),
            data: {
                id: personIdList
            },
            success: function(/*data*/) {
                dlg.dispose();
            }
        });
    }
    ,

    remove: function(dlg) {
        var removedPersonIdList = getSelectedUserIdList();
        if (!dlg) {
            new OptionalPane(getMessageProperty('toolbar-remove-title'), getMessageProperty('toolbar-remove-content'), this.remove, this);
            return;
        }
        $.ajax({
            url: getPathTo("home/remove"),
            data: {
                id: removedPersonIdList
            },
            success: function(/*data*/) {
                dlg.close();
                FilterManager.reload();
                SelectionManager.backToList();
            }
        });
    }
    ,

    addNote: function(note) {
        if (!note) {
            return;
        }
        var dataToSend = {
            note: note,
            personIdList: getSelectedUserIdList()
        };
        $.ajax({
            url: getPathTo("home/addComment"),
            data: dataToSend,
            type: 'post'
            ,
            success: function(data) {
                $("#toolbar").triggerHandler("addNote");
                $('#notificationHolder').html(data);
            }
        });
    }
    ,

    updateState: function() {
        var personList = SelectionManager.selectedPersonList;
        if (personList.length == 1 && !personList[0].id) {
            // new Person!
            $.each(Toolbar.buttons, function(i, button) {
                button.setDisabled(true);
            });
        } else {
            var stages = [];
            $.each(personList, function(i, person) {
                if (jQuery.inArray(person.stage, stages) < 0) {
                    stages.push(person.stage);
                }
            });
            $.each(Toolbar.buttons, function(i, button) {
                button.updateForPersonStages(stages);
            });
        }

        Toolbar._updateActionButtons(personList);
    },

    _updateActionButtons: function(personList) {
        var selectionCount = personList.length;
        for (var i = 0; i < Toolbar.actionButtons.length; i++) {
            if (selectionCount >= Toolbar.actionButtons[i].minSelected &&
                selectionCount <= Toolbar.actionButtons[i].maxSelected) {
                Toolbar.actionButtons[i].setEnabled(true);
            } else {
                Toolbar.actionButtons[i].setEnabled(false);
            }
        }
    }

};
/**
 * Wrapper for button to make possibility
 * @param selector element selector for button
 * @param callback function to call when button is pressed
 *
 */
function Button(selector, callback, minSelectedPersonToEnabled, maxSelectedPersonToEnabled, eventType) {
    this.minSelected = minSelectedPersonToEnabled;
    this.maxSelected = maxSelectedPersonToEnabled;
    var enabled = false;

    var element = $(selector);

    if (!element) {
        return
    }
    if (!eventType) {
        eventType = "click";
    }

    var notAllowed = element.hasClass("notAllowed");

    element.bind(eventType, function(event) {
        if (enabled && !notAllowed) {
            callback.call(Toolbar, event);
        }
    });

    function arrange() {
        if (!element) {
            return
        }
        var isInputElement = element.filter(":input").size() > 0;
        if (enabled && !notAllowed) {
            element.css('opacity', '1').css('cursor', 'pointer');
            if (isInputElement) {
                element.removeAttr("disabled");
            }
        } else {
            element.css('opacity', '0.3').css('cursor', 'default');
            if (isInputElement) {
                element.attr("disabled", "true");
            }
        }
    }

    this.setEnabled = function(enable) {
        if (!element) {
            return
        }
        if (enabled != enable) {
            enabled = enable;
            arrange();
        }
    };

    arrange();
}

function ToolbarStageButton(elementNode) {

    this.node = elementNode;
    this.actionName = $(elementNode).attr("id").split("_")[1];
    this.isDisabled = true;
    this.disableOn = $(elementNode).children("[name=disableOn]").val().split(",");

    this.setDisabled = function(isDisabled) {
        if (this.isDisabled == isDisabled) {
            return;
        }
        this.isDisabled = isDisabled;
        if (isDisabled) {
            $(this.node).unbind('click', this.onClick);
            $(this.node).removeClass("activeToolbarButton");
            $(this.node).addClass("disabledToolbarButton");
        } else {
            $(this.node).bind('click', {self:this}, this.onClick);
            $(this.node).addClass("activeToolbarButton");
            $(this.node).removeClass("disabledToolbarButton");
        }
    };

    this.onClick = function(event) {
        var self = event.data.self;
        new QuickSetStageDialog(getPathTo("person/changeStateNoteDialog"), "actionName=" + self.actionName);
    };

    this.updateForPersonStages = function(stages) {
        var self = this;
        var disabled = true;
        if (stages.length > 0) {
            disabled = false;
            $.each(stages, function(i, stageId) {
                $.each(self.disableOn, function(i, disabledStageId) {
                    if (disabledStageId == stageId) {
                        disabled = true;
                    }
                });
            });
        }
        this.setDisabled(disabled);
    };
}

function QuickSetStageDialog(contentUrl, params) {
    this.url = contentUrl;
    this.dlg = null;

    if (!params) {
        params = "";
    }
    this.params = params;

    this.confirmDialog = function() {
        var dataToSend = $("form", this.dlg).serialize();
        dataToSend = dataToSend + "&" + serializeSelectedUsersId();
        dataToSend = dataToSend + "&" + this.params;
        var self = this;
        $.post(getPathTo("person/setStage"), dataToSend, function(response) {
            if (response.errors) {
                self.setErrors(response.errors);
            } else {
                var event = jQuery.Event("changeStage");
                event.stageId = response.id;
                event.stageName = response.name;
                $("#toolbar").trigger(event);
                self.closeDialog();
            }
        }, "json");
    };

    this.setErrors = function(errorList) {
        if (errorList.length > 0) {
            var errorsHolder = $("#errorsHolder", this.dlg);
            $(".errors", errorsHolder).remove();
            var errors = $("<div class='errors'>");
            $.each(errorList, function() {
                errors.append("<p>" + this + "</p>");
            });
            errorsHolder.append(errors);
        }
    }

    this.closeDialog = function() {
        this.dlg.dialog('destroy').remove();
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
            closeOnEscape: false,
            buttons: buttons
        });
        this.dlg = dlg;
        dlg.dialog('open');
    };

    var self = this;
    var dataAsUrl = serializeSelectedUsersId();

    $.post(self.url, dataAsUrl, function(template) {
        self.showDialog(template);
    });
}

function AddNoteDialog(callbackThis, callback) {
    this.callback = callback;
    this.callbackThis = callbackThis;
    this.dlg = null;
    this.note = null;

    var addNoteDialogTitle = getMessageProperty("toolbar-addNote-title");
    $("#helperElement").html("<div id='dialog' title='" + addNoteDialogTitle +
                             "'><textarea id='noteText' style='width:100%; height:100px;'></textarea></div>");
    var self = this;

    var buttons = new Object();
    buttons[getMessageProperty("common-button-cancel")] = function() {
        self.dispose();
    };
    buttons[getMessageProperty("common-button-ok")] = function() {
        self.note = $("#noteText", self.dlg).attr('value');
        self.dispose();
        self.callback.call(self.callbackThis, self.note);
    };

    this.dlg = $("#dialog").dialog({
        bgiframe: true,
        autoOpen: true,
        modal: true,
        closeOnEscape: false,
        buttons: buttons
    });

    this.dispose = function() {
        $(this.dlg).dialog('destroy');
        $("#dialog").remove();
    };

    this.getNote = function() {
        return this.note;
    };

}

function serializeSelectedUsersId() {
    var selectedUsers = getSelectedUserIdList();
    var dataAsUrl = "";
    $.each(selectedUsers, function(index, personId) {
        dataAsUrl += "personIdList=" + personId;
        if (index < selectedUsers.length) {
            dataAsUrl += "&";
        }
    });
    return dataAsUrl;
}
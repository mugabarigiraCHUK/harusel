// edit sheet link
$(".editScoreSheet").live("click", function(event) {
    ScoresManager.editScore($(this).attr("sheetId"), getSelectedUserIdList()[0]);
    event.preventDefault();
    return false;
});

$(".imageOnTab").livequery(function() {
    $(this).click(function(event) {
        var personId = getSelectedUserId();
        if (personId) {
            var href = $(this).attr('href');
            if (!href.match(/.*\/\d+$/)) {
                href += "/" + personId;
                $(this).attr('href', href);
            }
        } else {
            event.preventDefault(true);
            return false;
        }
    });
}, false);

$("#addScores").livequery(function() {
    $('.resizeable').autoResize({
        extraSpace : 10
    });
    $("#date").datepicker();

    $("#getSheetNames").click(ScoresManager.selectSheetName);

    ScoresManager.personId = $("#personScoreSheetId").attr('value');
});

$(function() {
    ScoresManager.init();
});

var ScoresManager = {

    init: function() {
        ScoresManager.dlg = undefined;
    },

    openDialog: function(html) {
        var container = $("#helperElement");
        container.html(html);

        var buttons = new Object();
        buttons[getMessageProperty("common-button-cancel")] = function() {
            ScoresManager.close();
        };
        buttons[getMessageProperty("common-button-save")] = function() {
            var query = ScoresManager.collectData();
            $.post(getPathTo("score/save"), query, ScoresManager.checkSaveResponse);
        };
        ScoresManager.dlg = $("#dialog", container).dialog({
            height: 500,
            width: 800,
            modal: true,
            autoOpen: true,
            close: function() {
                ScoresManager.close();
            },
            buttons: buttons
        });
    },


    collectData: function() {
        return $("#dialog :input").serialize();
    },

    checkSaveResponse: function(response) {
        ScoresManager.close();
        if ($("#dialog", response).size() > 0) {
            ScoresManager.openDialog(response);
            return;
        }
        $("#toolbar").triggerHandler("addScore");
        $("#notificationHolder").html(response);
    },

    addScore: function(personId) {
        $.ajax({
            url: getPathTo("score/create/" + personId),
            success: ScoresManager.openDialog
        });
    },

    editScore: function(id, personId) {
        $.ajax({
            url: getPathTo("score/edit/" + id),
            data: { personId: personId },
            success: ScoresManager.openDialog
        });
    },

    close:function() {
        if (ScoresManager.dlg == undefined) {
            return;
        }
        ScoresManager.dlg.dialog('destroy');
        ScoresManager.dlg.remove();
        ScoresManager.dlg = undefined;
    },


    selectSheetName: function() {
        new ChooserModalDialog("score/getSheetNames/" +
                               ScoresManager.personId, [], ScoresManager, ScoresManager.setSheetName);
    },

    setSheetName: function(dlg) {
        var data = dlg.selected;
        if (data.length == 0) {
            return;
        }
        var value = data[0].id;
        $("[name='name']").attr('value', value);
    }

};

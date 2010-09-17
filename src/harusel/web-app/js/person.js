$(function() {
    PersonInfo.init();

});

$('#genericInfo').livequery(function() {
    updateTabs();
});

var PersonInfo = {
    eventListenerTabs : {
        'changeStage' : [0],
        'addNote' : [0,4],
        'addDoc' : [0,2],
        'addScore' : [0, 3],
        'sendMail' : [0]
    },

    init: function() {
        $("#toolbar").bind("changeStage", function(event) {
            PersonInfo._changeStage(event.stageId, event.stageName);
        });
        $.each(PersonInfo.eventListenerTabs, function(eventName, tabList) {
            $("#toolbar").bind(eventName, function() {
                var tabPane = $("#tabPane");
                if (tabPane.size() == 0) {
                    return;
                }
                var selectedTab = tabPane.tabs('option', 'selected');
                if (tabList.indexOf(selectedTab) >= 0) {
                    $("#tabPane").tabs('load', selectedTab);
                }
                ;
            });
        });
    },

    cancelUpdates: function(personId) {
        $("#genericInfo").load(getPathTo("person/cancel/" + personId));
    },

    savePerson: function(postUrl) {
        var formData = $("#mainPane :input").serialize();
        var self = this;
        $.post(postUrl, formData, function(response) {
            $("#genericInfo").parent().html(response);
            if ($("div.errors").size() == 0) {
                self.updateHeader();
                FilterManager.reload();
            }
        });
        updateTabs();
    },

    addPerson: function() {
        PersonInfo.savePerson(getPathTo("person/save"));
    },

    updatePerson: function() {
        PersonInfo.savePerson(getPathTo("person/update"));
    },

    cancelAddition: function() {
        SelectionManager.backToList();
    },

    editVacancies: function() {
        new ChooserModalDialog("vacancy/select", this._getCurrentVacancies(), this, this._onChangeVacancies);
    },

    editSource: function() {
        new ChooserModalDialog("source/select", [this._getCurrentSource()], this, this._onChangeSources);
    },

    updateHeader: function() {
        var value = $("#fullName").attr("value");
        $("#headerFullName").text(value);

        value = $("#phones").attr("value");
        $("#headerPhones").text(value);

        value = $("#emails").attr("value");
        $("#headerEmails").html(wrapWithMailTo(value));

        var personId = $("#personId").attr("value");
        var stageId = $("[name='stage.id']").attr("value");
        SelectionManager.setSelectedPerson(personId, stageId);
    },

    ///////////////////////////////////////////////////////////////////// Helpers
    _changeStage: function(stageId, stageName) {
        $("[name='stage.id']").val(stageId);
        $("#headerStage").text(stageName);
        SelectionManager.setSelectedPerson($("#personId").attr("value"), stageId);
    },

    //// Vacancy management
    _onChangeVacancies: function(dlg) {
        var self = this;
        self._updateCurrentVacancy(dlg.selected);
        dlg.closeDialog();
    },

    _updateCurrentVacancy: function(vacancies) {
        // delete old vacancies
        var vacanciesUl = $("ul#vacancy");
        vacanciesUl.empty();
        var index = 0;
        $(vacancies).each(function() {
            //            vacanciesUl.append("<li>" + this.name + "</li><input type='hidden' name='vacancies[" + index +
            //                               "].id' value='" + this.id + "'/>");
            vacanciesUl.append("<li>" + this.name + "</li><input type='hidden' name='vacancies" +
                               ".id' value='" + this.id + "'/>");
            index++;
        });

    },

    _getCurrentVacancies: function() {
        var vacancyInputs = [];
        $("#vacancy :hidden").each(function() {
            vacancyInputs.push(this.value);
        });
        return vacancyInputs;
    },

    //// Source management
    _onChangeSources: function(dlg) {
        var self = this;
        self._updateCurrentSource(dlg.selected);
        dlg.closeDialog();
    },

    _updateCurrentSource: function(sources) {
        var sourceInput = $(":input#sourceName");
        $(sourceInput).attr('value', sources[0].name);
        $(sourceInput).next(':hidden').attr('value', sources[0].id);
    },

    _getCurrentSource: function() {
        return $(":input#sourceName").next(":hidden").attr('value');
    }
};


/**
 * pagination for timeline tab
 * @param size new item size
 * @param personId id visibyl person
 */
function timelinePaginate(size, personId) {
    $('#tabPane').tabs('url', 0, getPathTo('person/timeline') + "/" + personId + "?max=" + size);
    $('#tabPane').tabs('load', 0);
}

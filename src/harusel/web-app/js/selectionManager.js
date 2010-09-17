// This instance is the model of page.
var SelectionManager = {
    selectedPersonList : [],
    updateListeners: {},
    // current opened list
    filterId:0, vacancyId:0, max:10, offset:0,
    filterQuery:null,

    backToList: function() {
        var self;
        self = SelectionManager;
        if (SelectionManager.filterQuery) {
            $("#mainPane").load(getPathTo("home/search"), {query:SelectionManager.filterQuery});
            FilterManager.reload(); // because 'unread' counters might be changed
        } else if (SelectionManager.filterId) {
            $("#mainPane").load(getPathTo("person/filter/" + self.filterId + "?vacancyId=" + self.vacancyId + "&max=" +
                                          self.max + "&offset=" + self.offset));
            FilterManager.reload(); // because 'unread' counters may change
        } else {
            location.reload();
        }
    },

    setSelectedPerson: function(selectedPersonId, selectedPersonStageId) {
        this.selectedPersonList = [
            {
                id: selectedPersonId,
                stage: selectedPersonStageId
            }
        ],
            this.fireUpdate();
    },

    init: function(filterId, vacancyId, max, offset, filterQuery) {
        this.filterId = filterId != undefined ? filterId : SelectionManager.filterId;
        this.vacancyId = vacancyId != undefined ? vacancyId : SelectionManager.vacancyId;
        this.max = max != undefined ? max : SelectionManager.max;
        this.offset = offset != undefined ? offset : SelectionManager.offset;
        this.filterQuery = filterQuery != undefined ? filterQuery : SelectionManager.filterQuery;

        this.selectedPersonList = [];
        var self = this;
        $("#personList").find(":checkbox").each(function(index, element) {
            if ($(element).attr("checked")) {
                self._selectPerson(element);
            }
            $(element).change(function() {
                if ($(element).attr("checked")) {
                    self._selectPerson(element);
                } else {
                    self._deselectPerson(element);
                }
                self.fireUpdate();
            });
        });

        $("#toolbar").bind("changeStage", SelectionManager._updatePersonList);
        $("#toolbar").bind("addNote", SelectionManager._updatePersonList);
        $("#toolbar").bind("addDoc", SelectionManager._updatePersonList);
        $("#toolbar").bind("addScore", SelectionManager._updatePersonList);

        self.fireUpdate();
        this.ajaxPaggingLinks($("#pagging"));
    },

    _updatePersonList: function() {
        if ($("#personList").size() > 0) {
            SelectionManager.backToList();
        }
    },

    _selectPerson: function(element) {
        var personInfo = { id: $(element).attr("id"), stage: $(element).next().val() };
        this.selectedPersonList.push(personInfo);
    },

    _deselectPerson: function(element) {
        var id = $(element).attr("id");
        this.selectedPersonList = $.grep(this.selectedPersonList, function(person) {
            return person.id != id;
        });
    },

    fireUpdate: function() {
        var self = this;
        $.each(this.updateListeners, function(obj, callback) {
            callback.call(obj, self.selectedPersonList);
        });
        Toolbar.updateState();
    },

    ajaxPaggingLinks: function(container) {
        $(container).children("a[@id]").each(function(index, element) {
            $(element).click(function(event) {
                var href = $(event.target).attr("href");
                jQuery.ajax({type:'POST', url:href,
                    success:function(data) {
                        jQuery('#mainPane').html(data);
                    }});
                event.preventDefault();
            });
        });
    }
};

/**
 * Tab initialization function. The main goal is update tab's link for just added person
 */
function initTabs() {
    $("#tabPane").tabs({
        spinner: false,
        selected: 1,
        // when person card is opened to create new user, there is no tabs with right personId in link
        // add person id on select action
        select: function (event, ui) {
            var id = getSelectedUserIdList()[0];
            if (!id) {
                return false;
            }
            var url = $.data(ui.tab, 'load.tabs');
            if (url.search(/[0-9]+$/) == -1) {
                $.data(ui.tab, 'load.tabs', url + "/" + id);
            }
            return true;
        }
    });
}

/**
 * It disables all tabs but generic for new person
 */
function updateTabs() {
    var tabPane = $("#tabPane");
    var id = $("#personId").attr("value");
    if (!id || id == '') {
        tabPane.tabs('option', 'disabled', [0, 2, 3, 4]);
    } else {
        tabPane.tabs('option', 'disabled', false);
    }
}

/**
 * returns list selected persons id
 */
function getSelectedUserIdList() {
    var idList = [];
    $.each(SelectionManager.selectedPersonList, function(i, person) {
        idList.push(person.id);
    });
    return idList;
}

/**
 * returns first selected persons id
 */
function getSelectedUserId() {
    if (SelectionManager.selectedPersonList && SelectionManager.selectedPersonList.length > 0) {
        return SelectionManager.selectedPersonList[0].id;
    }
    return null;
}

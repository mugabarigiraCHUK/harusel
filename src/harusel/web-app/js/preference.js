$(function() {
    $('#criterionPane').livequery(function() {
        PreferenceManager.initCriterion();
    });
    $('#vacancyCriterionPane').livequery(function() {
        PreferenceManager.initVacancyEdit();
    });
    $('#vacancyPane').livequery(function() {
        PreferenceManager.initVacancy();
    });
    $('#sourcePane').livequery(function() {
        PreferenceManager.initSource();
    });
    $('#preferenceTabPane').livequery(function() {
        $(this).tabs({
            spinner: false,
            selected: $("#selectedPreferanceTab").val(),
            select: function(event, ui) {
                if ($(ui.tab).hasClass("separatedWindow")) {
                    var url = $.data(ui.tab, 'load.tabs');
                    if (url) {
                        window.open(url, "_blank");
                    }
                    return false;
                }
                return true;
            }
        });
    })
});

var PreferenceManager = {
    initDeactivateButton : function(domainName) {
        $("#" + domainName + "Deactivate").click(function() {
            var idList = getCheckedElementsFromTable($("#" + domainName + "ListTable"));
            if (idList.length == 0) {
                turnState();
                return true;
            }
            $("#" + domainName + "DomainDetails").load(getPathTo(domainName + "/deactivate"), {id: idList});
        });
    },

    saveData: function(domainName, close) {
        var data = $("#" + domainName + "View :input").serialize();
        data += "&close=" + close;
        var url = getPathTo(domainName + "/save");
        if (this.getId(domainName) != "") {
            url = getPathTo(domainName + "/update");
        }
        $.post(url, data, function(html) {
            $("#" + domainName + "DomainDetails").html(html);
        });
    },

    backToList: function(domainName) {
        $("#" + domainName + "DomainDetails").load(getPathTo(domainName + "/list"));
    },

    initSaveButtons : function(domainName) {
        $("#" + domainName + "EditCancel").click(function() {
            PreferenceManager.cancel(domainName);
        });
        $("#" + domainName + "EditSave").click(function() {
            PreferenceManager.saveData(domainName, false);
        });
        $("#" + domainName + "EditSaveAndClose").click(function() {
            PreferenceManager.saveData(domainName, true);
        });

    },

    initVacancy : function() {
        PreferenceManager.initDeactivateButton("vacancy");
    },

    initSource: function() {
        PreferenceManager.initDeactivateButton("source");
    },

    initCriterion: function() {
        if (this.tree != undefined) {
            this.tree.destroy();
            this.tree = undefined;
        }
        this.tree = $.tree_create();
        // to remove checkboxes
        $("#criterionPane a").css('background-image', 'none').css('padding-left', '0px');
        this.tree.init($("#criterionPane"), {
            ui : {
                theme_name : "checkbox",
                animation : 250
            },
            rules: {
                draggable : "all",
                dragrules : ["item before item", "item after item","root before root", "root after root"],
                renameable : ["root", "item"],
                deletable: ["root", "item"],
                creatable : [ "root", "superRoot" ]
            },
            lang: {
                new_node: "New criterion"
            },
            callback : {
                oncreate : function(NODE, REF_NODE, TYPE, TREE_OBJ) {
                    // to remove checkboxes
                    $("#criterionPane a").css('background-image', 'none').css('padding-left', '0px');

                    var ref_type = TREE_OBJ.get_type(REF_NODE);
                    if ((TYPE == 'after' || TYPE == 'before') && ref_type == 'root' ||
                        TYPE == 'inside' && ref_type == 'superRoot') {
                        $(NODE).attr('rel', 'root').attr('id', 'new');
                    } else {
                        $(NODE).attr('rel', 'item').attr('id', 'new');
                    }
                }
            }
        });
        $("#criterionPane").css("display", "block");
        $("#criteriaEditSave").click(function() {
            PreferenceManager.saveCriteria();
        });
        $("#criteriaEditCancel").click(function() {
            PreferenceManager.cancelCriteria();
        });
    },

    cancelCriteria: function() {
        $.ajax({
            url: getPathTo("criterion/cancel"),
            type: 'post',
            success: function(data) {
                $("#notificationHolder").html(data);
                var selectedTab = $('#preferenceTabPane').tabs('option', 'selected');
                $('#preferenceTabPane').tabs('load', selectedTab);
            }
        });
    },

    saveCriteria: function() {
        var treeObject = [];
        $("#criteriasTree").children("ul").children("li").each(function(i, element) {
            var item = PreferenceManager._getCriteriaItem(element);
            treeObject.push(item);
        });

        var query = $.toJSON(treeObject);
        $.ajax({
            url: getPathTo("criterion/save"),
            type: 'post',
            data: {criterias: query},
            success: function(data) {
                $("#notificationHolder").html(data);
            }
        });
    },

    _getCriteriaItem: function(node) {
        var item = { id: "", name: "", children:[]};
        item.id = $(node).attr('id');
        item.name = $(node).children("a").text();
        if ($(node).children("ul") != undefined) {
            $("li", node).each(function(i, child) {
                item.children.push(PreferenceManager._getCriteriaItem(child));
            });
        }
        return item;
    },

    initVacancyEdit: function() {
        if (this.treeOnVacancy != undefined) {
            this.treeOnVacancy.destroy();
            this.treeOnVacancy = undefined;
        }
        this.treeOnVacancy = $.tree_create();
        this.treeOnVacancy.init($("#vacancyCriterionPane"), {
            ui : {
                theme_name : "checkbox",
                animation : 250,
                context: {}

            },
            callback : {
                onchange : treeCheckboxes
            },
            rules: {
                creatable: "none",
                renameable : "none",
                deletable: "none"
            }
        });

        $("#editReviewers").click(function() {
            new ChooserModalDialog("preferences/reviewersList", PreferenceManager.getCurrentVacancyReviewers(),
                PreferenceManager, PreferenceManager.editReviewers);
        });
        $("#vacancyEditSave").click(function() {
            PreferenceManager.saveVacancy(false);
        });
        $("#vacancyEditCancel").click(function() {
            PreferenceManager.cancel("vacancy");
        });
        $("#vacancyEditSaveAndClose").click(function() {
            PreferenceManager.saveVacancy(true);
        });
    },

    cancel: function (domainName) {
        var id = PreferenceManager.getId(domainName);
        if (!id || id == '') {
            PreferenceManager.backToList(domainName);
        } else {
            $("#" + domainName + "DomainDetails").load(getPathTo(domainName + "/cancel/" + id));
        }
    },

    editReviewers: function(dlg) {
        this.setCurrentVacancyReviewers(dlg.selected);
        dlg.closeDialog();
    },

    getCurrentVacancyReviewers: function() {
        var list = [];
        $(":input[name='usersIdList']").each(function() {
            list.push($(this).val());
        });
        return list;
    },

    setCurrentVacancyReviewers: function(users) {
        var listElement = $("#reviewersList");
        listElement.empty();
        var listHtml = "<ul class='reviewers'>";
        $(users).each(function() {
            listHtml += "<li><input type='hidden' name='usersIdList' value='" + this.id + "'/>" + this.name + "</li>";
        });
        listHtml += "</ul>";
        listElement.html(listHtml);
    },

    getId : function(domain) {
        var idElem = $("#" + domain + "Id");
        return idElem.attr("value");
    },

    saveVacancy: function(backToListAfterSave) {
        var data = this.getherVacancyFormData();
        var id = PreferenceManager.getId('vacancy');
        if (id != '') {
            action = 'update';
        } else {
            action = 'save';
        }
        data += "&backToList=" + backToListAfterSave;
        $.post(getPathTo("vacancy/" + action), data, function(response) {
            $("#vacancyDomainDetails").html(response);
        });
    },

    getherVacancyFormData: function() {
        var data = $("#vacancyView :input").serialize();
        var list = PreferenceManager.getCheckedVacancyList();
        $.each(list, function(i, id) {
            data += "&criteriasIdList=" + id;
        });
        return data;
    },

    getCheckedVacancyList: function() {
        var list = [];
        $("#vacancyCriterionPane a.checked").filter(function() {
            return $(this).parent("li").children("ul").size() == 0;
        }).each(function(i, elem) {
            list.push($(elem).parent("li").attr('id'));
        });
        return list;
    }
};

function turnState() {
    var a = $(":radio[name='active']");
    a.each(function(i, e) {
        if ($(e).attr('value') == 'false') {
            $(e).click();
        }
    });
}

function getCheckedElementsFromTable(tableElement) {
    var idList = [];
    $(":checkbox[checked]", tableElement).each(function(i, elem) {
        idList.push($(elem).attr('id'));
    });
    return idList;
}
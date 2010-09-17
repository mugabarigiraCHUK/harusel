$("#templateList ul li a").livequery(function() {
    $(this).click(clickTemplateTab);
});

$("#saveTemplate").livequery(function() {
    $(this).click(saveTemplate);
});

$("#cancelTemplate").livequery(function() {
    $(this).click(cancelTemplate);
});

$("#addTemplateNameField").livequery(function() {
    $(this).focus();
});

$(".addTemplateForm").live('submit', function(event) {
    var templateNameField = $("#addTemplateNameField");
    var templateName = $.trim(templateNameField.val());
    if (templateName == "") {
        templateNameField.val(templateName);
        templateNameField.addClass("error").focus();
        event.preventDefault();
        return false;
    }
    $("#createTemplateButton")[0].popup.btOff();
    $.post($(this)[0].action, {name: templateName}, function(message) {
        $("#performanceReviewTabs").tabs("load", 1);
        addMessage(message, 'notify-message', 1)
    });
    event.preventDefault();
    return false;
});

$("#createTemplateButton").livequery(function() {
    var button = $("#createTemplateButton");
    button[0].popup = button.bt($('#addTemplatePopup').html(), {
        trigger: 'click',
        positions: ['right','bottom'],
        width: 250,
        fill: '#326aae',
        strokeWidth: 0, /*no stroke*/
        spikeLength: 10,
        spikeGirth: 10,
        padding: 20,
        cornerRadius: 15,
        cssStyles: {
            fontFamily: '"lucida grande",tahoma,verdana,arial,sans-serif',
            fontSize: '13px'
        },
        cssClass: "addTemplatePopup"
    });
});

/**
 * Handler on change PR template
 */
function clickTemplateTab(event) {
    selectTemplateTab(this);
    loadCurrentTemplate();
    event.stopPropagation();
    return false;
}

/**
 * Loads questions for selected template and create tree
 */
function loadCurrentTemplate(params) {
    var selectedTemplate = getSelectedTemplate();
    if (selectedTemplate == null) {
        return;
    }
    var address = $(selectedTemplate).children("a").attr("href");
    var editor = $("#templateEditor")[0];
    //    if (editor.tree) {
    //        editor.tree.destroy();
    //        editor.tree = false;
    //    }
    $("#templateEditor").load(address, params, function() {
        var editor = $("#templateEditor")[0];
        editor.tree = $.tree_create();
        editor.tree.init($(editor), {
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
                new_node: getMessageProperty("performanceReview-label-newQuestion")
            },
            callback : {
                onchange : treeCheckboxes,
                oncreate : function(NODE, REF_NODE, TYPE, TREE_OBJ) {
                    var ref_type = TREE_OBJ.get_type(REF_NODE);
                    if ((TYPE == 'after' || TYPE == 'before') && ref_type == 'root' ||
                        TYPE == 'inside' && ref_type == 'superRoot') {
                        $(NODE).attr('rel', 'root').attr('id', 'new');
                        $(NODE).children("a").text(getMessageProperty("performanceReview-label-newSection"));
                    } else {
                        $(NODE).attr('rel', 'item').attr('id', 'new');
                    }
                },
                beforedelete: function(NODE, TREE_OBJ) {
                    $(NODE).children("ul").children("li").each(function() {
                        $(this).hide();
                    });
                    $(NODE).hide();
                    return false
                }
            }
        });
    });
}

/**
 * Parse id in DOM element attribute for pattern 'someUnicPrefix'_'ID'
 * @param element
 */
function parseId(element) {
    var idAttr = $(element).attr("id");
    return idAttr.substr(idAttr.lastIndexOf("_") + 1);
}

/**
 * Returns DOM element of selected template menu
 */
function getSelectedTemplate() {
    var selectedTab = $("#templateList ul li.selectedTab");
    if (selectedTab.size() != 1) {
        return null;
    }
    return selectedTab[0];
}

/**
 * Selects template menu given by anchor element within it
 */
function selectTemplateTab(anchorElement) {
    $("#templateList ul li").each(function(index, elem) {
        $(this).removeClass("selectedTab");
    });
    $(anchorElement).parents("li").addClass("selectedTab");
}

/**
 * Scan questions tree and returns JSON object of current tree state
 */
function getTemplateModel(root) {
    var model = [];
    $(root).children("li").each(function() {
        var item = new Object();
        item.id = parseId(this);
        item.text = $(this).children("a").text();
        item.selected = $(this).children("a").hasClass("checked");
        item.isDeleted = $(this).css("display") == "none";
        var childrenElements = $(this).children("ul");
        if (childrenElements.size() > 0) {
            item.children = getTemplateModel(childrenElements[0]);
        }
        model.push(item);
    });
    return model;
}

/**
 * returns current template Id
 */
function getTemplateId() {
    return parseId(getSelectedTemplate());
}

/**
 * Makes post request to server with current template state to save it
 */
function saveTemplate() {
    var templateId = getTemplateId();
    var questionsList = $("#templateEditor ul li ul:first");
    var model = getTemplateModel(questionsList);
    var jsonData = JSON.stringify(model);
    // TODO: Handle failed requests.
    $.post("review/saveTemplate/" + templateId, {groups: jsonData}, function() {
        loadCurrentTemplate();
    });
}

$("#editTemplateTab").livequery(function() {
    $("#templateList ul li a:first").click();
});

/**
 * Reloads current template tree
 */
function cancelTemplate() {
    loadCurrentTemplate("req=cancel");
}

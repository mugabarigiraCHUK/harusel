$(function() {
    // init selection changing
    $('#goto-home').click(function() {
        $('#goto-home').addClass('selected')
        $('#goto-preference').removeClass('selected')
    });
    $('#goto-preference').click(function() {
        $('#goto-home').removeClass('selected')
        $('#goto-preference').addClass('selected')
    });
    $('#goto-home').addClass('selected');

    // init preference loading
    $('#goto-preference').click(function() {
        // update menu
        updatePreferenceMenu();
        // initialize PreferenceManager
        PreferenceManager.init();
        // prevent default behaviour
        return false;
    });
});

function updatePreferenceMenu() {
    $.ajax({
        type: 'POST',
        url: getPathTo('preference/menu'),
        success: function(data, textStatus) {
            $('#page-menu').html(data);

            $('.menu-item a').click(function() {
                // deselect all menu items
                $('.menu-item a').removeClass('selected');
                $('.menu-item').removeClass("style_openedPrnt");
                // select this menu item
                $(this).addClass('selected');
                $(this).parents('.menu-item').addClass("style_openedPrnt");
            });

            // click on the first menu item
            $('.menu-item:first a').click();
        },
        error:function(XMLHttpRequest, textStatus, errorThrown) {
        }
    });
}

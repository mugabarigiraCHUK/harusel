/**
 * Appends tooltip dialog with specified content to the target element.
 *
 * @param targetElement  html-element to append tooltip to.
 * @param htmlContent    html-content of the tooltip dialog.
 */
$(function() {
    initTextOverflow();
});

function appendToolTip(targetElement, htmlContent) {
    var processed = targetElement.attr('processed');
    if (!processed) {
        targetElement.bt(htmlContent, {
            strokeStyle: "#aeafa5",
            strokeWidth: 1,
            spikeLength: 10,
            trigger: 'hover',
            positions: ['bottom', 'top', 'right', 'left'],
            //            width: 250,
            fill: '#e3e3e3',
            spikeGirth: 10,
            padding: 10,
            cornerRadius: 10,
            cssClass: 'popupForm'
        });
        targetElement.attr('processed', true);
    }
}

/**
 * Truncates too long string and creates tooltip controller
 * To make this work, the following html structure should be made:
 *  <div class='.textOverflowContainer'>
 *      <div>...</div>   // if you need some elements like icons on the line
 *      <div>   // incapsulate text in div
 *          <span class="text-overflow"> long text </span>
 *      </div>
 *  </div>
 *
 * That's all!
 *
 */
function initTextOverflow() {
    $('.text-overflow').livequery(function() {
        var container = $(this).parents(".textOverflowContainer");
        var lineWidth = container.width();
        var otherBlocksWidth = 0;
        $.each(container.children("div"), function() {
            if (!$(this).hasClass("clear") && $(this).children(".text-overflow").size() == 0) {
                otherBlocksWidth += $(this).width();
            }
        });
        $(this).parent().width(lineWidth - otherBlocksWidth);
        this.originalContent = $(this).html();
        $(this).textOverflow('<div class="text-overflow-more-image"></div>');
        var moreIcon = $(this).children('.text-overflow-more-image');
        if (moreIcon.size() > 0) {
            appendToolTip($(moreIcon), this.originalContent);
        }
    });
}

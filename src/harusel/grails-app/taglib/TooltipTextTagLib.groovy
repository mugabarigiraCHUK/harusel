// CR: major dkranchev 02-Mar-2010 Missed javadoc.  
class TooltipTextTagLib {

    static final int SHY_DISTANCE = 20;

    // CR: major dkranchev 02-Mar-2010 Rename to makeTooltip?
    def tooltipText = {attrs, body ->
        String text = body()
        out << (0..text.size() / SHY_DISTANCE).collect() {
            int beginIndex = it * SHY_DISTANCE
            int endIndex = (it + 1) * SHY_DISTANCE
            (endIndex < text.size()) ? text.substring(beginIndex, endIndex) : text.substring(beginIndex)
        }.join('&shy;').replace('\n', '<br />')
    }

    // CR: major dkranchev 02-Mar-2010 Unclear name. Rename.
    def shyEveryLetter = {attrs, body ->
        String text = body();
        text = text.collect { it }.join("&shy;");
        out << text.replace("\n", "<br/>");
    }
}

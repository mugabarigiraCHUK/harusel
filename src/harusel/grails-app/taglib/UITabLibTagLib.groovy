import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.mapping.UrlCreator
import org.springframework.web.servlet.support.RequestContextUtils

// CR: major dkranchev 02-Mar-2010 Unclear taglib name.
class UITabLibTagLib {
    def grailsUrlMappingsHolder
    def perspectiveService
    def messageSource

    /**
     * Wrap content inside html tag
     */
    def wrap(String elementName, String cssClass, String content, String attrs = "") {
        return "<$elementName class='$cssClass' $attrs>$content</$elementName>"
    }

    // CR: major dkranchev 02-Mar-2010 rename to renderPerspectives.
    def renderPerspectives = {attrs, body ->
        def list = perspectiveService.perspectives
        def current = attrs.remove('current')
        Locale locale = RequestContextUtils.getLocale(request);
        out << '<div class="perspectiveLinks">';
        out << list.collect {perspective ->
            def perspectiveText = messageSource.getMessage(perspective.code, null, locale)
            if (StringUtils.equals(perspective.controllerName, current)) {
                return wrap("span", "currentPerspective", perspectiveText)
            } else {
                UrlCreator mapping = grailsUrlMappingsHolder.getReverseMapping(perspective.controllerName, null, null)
                def url = mapping.createURL(perspective.controllerName, null, null, request.characterEncoding, null)
                return wrap("a", "currentPerspective", perspectiveText, "href='${url.encodeAsHTML()}'")
            }
        }.join('&nbsp;&middot;&nbsp;')
        out << "</div>"
    }
}

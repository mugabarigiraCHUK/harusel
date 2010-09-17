package common

import org.apache.log4j.Logger

class BeanNestedPropertiesForStrSubstitutor {
    public static final Logger log = Logger.getLogger(BeanNestedPropertiesForStrSubstitutor.class)

    private String prefix = "\${";
    private String suffix = "}";
    private String PROPERTY_SEPARATOR = ".";

    private def bean


    def BeanNestedPropertiesForStrSubstitutor(def bean) {
        this.bean = bean;
    }

    def substitute(String template) {
        def res = ""
        def startIndex = 0
        def placeholderStart = template.indexOf(prefix)
        def templateLength = template.length()
        while (placeholderStart >= 0 && placeholderStart < templateLength) {
            def placeholderEnd = template.indexOf(suffix, startIndex)
            if (placeholderEnd < 0) {
                res += template.substring(startIndex)
                break
            }
            res += template.substring(startIndex, placeholderStart)
            String property = template.substring(placeholderStart + prefix.length(), placeholderEnd)
            res += String.valueOf(getBeanProperty(property))
            startIndex = placeholderEnd + suffix.length()
            placeholderStart = template.indexOf(prefix, startIndex)
        }
        return res += template.substring(startIndex)
    }

    def getBeanProperty(String propertyPath) {
        def propertiesChain = propertyPath.split("[${PROPERTY_SEPARATOR}]")
        def value = bean;
        try {
            for (String property: propertiesChain) {
                if (value) {
                    value = value[property]
                } else
                    break;
            }
        } catch (Exception e) {
            log.error("Can't find property [$propertyPath] in class ${bean.class.name}", e)
            return ''
        }
        return value;
    }
}

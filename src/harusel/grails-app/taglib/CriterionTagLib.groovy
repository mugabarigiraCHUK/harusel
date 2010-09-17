class CriterionTagLib {
    // CR: major dkranchev 02-Mar-2010 Javadoc missed. 
    def checker = {attrs, body ->
        def selectedList = attrs.remove('selected')
        def criteria = attrs.remove('criteria')
        def varCssClass = attrs.remove('var')

        def cssClass = ''
        if (selectedList.find {it == criteria.id}) {
            cssClass = 'checked'
        } else if (criteria.children?.find { selectedList.contains(it.id)}) {
            cssClass = criteria.children?.find { !selectedList.contains(it.id)} ? 'undetermined' : 'checked'
        }
        pageScope."${varCssClass}" = cssClass
    }
}

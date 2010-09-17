package domain.person

import domain.person.DocumentUpload
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.ListUtils

/**
 * Document upload command
 */
class DocumentUploadCommand {
    List documents = ListUtils.lazyList([], FactoryUtils.instantiateFactory(DocumentUpload))
}
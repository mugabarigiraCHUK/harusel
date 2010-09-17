package domain.person

import domain.person.DocumentType
import org.springframework.web.multipart.MultipartFile

/**
 * Uploaded document
 */
class DocumentUpload {
    MultipartFile file
    DocumentType type
    // CR: normal dkranchev 02-Mar-2010 Unclear name. Javadoc missed. 
    String typeName
}
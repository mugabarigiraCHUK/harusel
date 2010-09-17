package domain.event

import domain.person.Document

/**
 * Signals that document has been added
 */
class DocumentAddedEvent extends PersonEvent {

    static belongsTo = [document: Document] // Target document

}

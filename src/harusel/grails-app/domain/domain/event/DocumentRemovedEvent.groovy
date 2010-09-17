package domain.event

import domain.person.Document

/**
 * Signals that document has been removed.
 */
class DocumentRemovedEvent extends PersonEvent {

    static belongsTo = [document: Document] // Target document

}

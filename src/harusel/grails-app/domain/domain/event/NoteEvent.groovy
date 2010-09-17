package domain.event

import domain.person.Note

/**
 * Signals that new note has been added on person.
 */
class NoteEvent extends PersonEvent {

    static belongsTo = [note: Note] // Added note.


    def NoteEvent() {
    }
}

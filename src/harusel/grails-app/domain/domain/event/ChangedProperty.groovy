package domain.event

// CR: minor akabanov 02-Mar-2010 unused import.
import domain.event.GenericDataEvent

// CR: minor akabanov 02-Mar-2010 Missed javadoc. 
class ChangedProperty implements Serializable {

    static belongsTo = [event: GenericDataEvent]

    /**
     * Changed property name
     */
    String propertyName

    /**
     * New property value
     */
    String newValue

    String toString() {
        "$propertyName: $newValue"
    }
}

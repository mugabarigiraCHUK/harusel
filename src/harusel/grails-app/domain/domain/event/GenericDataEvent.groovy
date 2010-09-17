package domain.event

/**
 * Signals that person's generic data has been changed.
 */
class GenericDataEvent extends PersonEvent {
    static transients = ['changedPropertiesAsString']

    static hasMany = [changedProperties: ChangedProperty]

    static mapping = {
        changedProperties cascade: "delete"
    }

    def getChangedPropertiesAsString() {
        return changedProperties.collect {property ->
            "${property.propertyName}=${property.newValue}"
        }.join(', ')
    }
}

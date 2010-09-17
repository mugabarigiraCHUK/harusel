package common

/**
 * Command object to wrap source report parameters
 */
class DatePeriodCommand {
    Date dateFrom
    Date dateTo

    void setDateFrom(Date date) {
        date.setHours 0
        date.setMinutes 0
        date.setSeconds 0
        dateFrom = date
    }

    void setDateTo(Date date) {
        date.setHours 23
        date.setMinutes 59
        date.setSeconds 59
        dateTo = date
    }
}

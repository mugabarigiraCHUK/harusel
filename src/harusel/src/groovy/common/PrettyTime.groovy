package common

class PrettyTime {

    private Date today
    private Date yesterday
    private Date currentWeek
    private Date oneWeekAgo
    private Date twoWeeksAgo
    private Date threeWeeksAgo
    private Date lastMonth

    PrettyTime() {
        // initialize calendar
        Calendar calendar = new GregorianCalendar()

        // get today date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        today = calendar.getTime()

        // set yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        yesterday = calendar.getTime()

        // set begin date of weeks
        calendar.setTime(today)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        currentWeek = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        oneWeekAgo = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        twoWeeksAgo = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        threeWeeksAgo = calendar.getTime()

        // set begin of last month
        calendar.setTime(today)
        calendar.add(Calendar.MONTH, -1)
        lastMonth = calendar.getTime()
    }

    /**
     * Returns the code of human-readable explanation of specified date.
     * @param date the date to format.
     * @return the code of human-readable explanation.
     */
    def getCode(Date date) {
        if (today.before(date)) {
            return "timeline.period.today"
        } else if (yesterday.before(date)) {
            return "timeline.period.yesterday"
        } else if (currentWeek.before(date)) {
            Integer dayNumber = new GregorianCalendar(time: date).get(Calendar.DAY_OF_WEEK)
            return "timeline.period.currentWeek." + dayNumber
        } else if (oneWeekAgo.before(date)) {
            return "timeline.period.oneWeekAgo"
        } else if (twoWeeksAgo.before(date)) {
            return "timeline.period.twoWeeksAgo"
        } else if (threeWeeksAgo.before(date)) {
            return "timeline.period.threeWeeksAgo"
        } else if (lastMonth.before(date)) {
            return "timeline.period.lastMonth"
        } else {
            return "timeline.period.older"
        }
    }

}

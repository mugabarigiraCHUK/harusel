package common

import common.Period

/**
 * TODO: add header
 */

public class TimelinePeriod {

    Date dateLast;
    Period lastPeriod;

    def TimelinePeriod() {
        dateLast = null;
        lastPeriod = null;
    }

    Boolean isNextPeriod(Date date) {
        if (dateLast == null) {
            dateLast = date;
            lastPeriod = Period.getPeriodByDay(date);
            return true;
        }
        boolean res = !lastPeriod.equals(Period.getPeriodByDay(date));
        if (res) {
            lastPeriod = Period.getPeriodByDay(date);
        }
        return res;
    }

    Period getCurPeriodName() {
        return lastPeriod;
    }
}
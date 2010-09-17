package common;


import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * TODO: add header
 */
public enum Period {
    CYR_DAY,
    PREW_DAY,
    CYR_WEAK,
    PREW_WEAK,
    CYR_MONTH,
    PREW_MONTH,
    OLDER;

    Date curDate;

    Period() {
        curDate = new Date();
    }

    static Period getPeriodByDay(Date date) {
        try {
            Date curDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
            curDate = dateFormat.parse(dateFormat.format(curDate));
            if (date.getTime() > curDate.getTime()){
                return CYR_DAY;
            }
            if (date.getTime() > DateUtils.addDays(curDate,-1).getTime()){
                return PREW_DAY;
            }
            dateFormat = new SimpleDateFormat("www:yyyy");
            curDate = dateFormat.parse(dateFormat.format(new Date()));
            if (date.getTime() > curDate.getTime()){
                return CYR_WEAK;
            }
            if (date.getTime() > DateUtils.addWeeks(curDate,-1).getTime()){
                return PREW_WEAK;
            }
            dateFormat = new SimpleDateFormat("mmm:yyyy");
            curDate = dateFormat.parse(dateFormat.format(new Date()));
            if (date.getTime() > curDate.getTime()){
                return CYR_MONTH;
            }
            if (date.getTime() >  DateUtils.addMonths(curDate,-1).getTime()){
                return PREW_MONTH;
            }

        } catch (ParseException e) {

            e.printStackTrace();  //TODO:
        }
        throw new IllegalArgumentException();
    }
}

package com.ratik.todone.util;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ratik on 22/12/16.
 */

public class TimeHelper {
    public static String getHumanReadableTimeDifference(Calendar c) {
        Calendar present = Calendar.getInstance();
        long diff = c.getTimeInMillis() - present.getTimeInMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long minutes =  TimeUnit.MILLISECONDS.toMinutes(diff);
        if (hours >= 1) {
            if (hours == 1) {
                return (int) hours + " HR";
            } else {
                return (int) hours + " HRS";
            }

        } else {
            return (5 * (Math.round(minutes/5)) + 5) + " MIN";
        }
    }
}

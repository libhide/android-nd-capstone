package com.ratik.todone.util;

/**
 * Created by Ratik on 22/12/16.
 */

public class TimeHelper {
    public static String convertTo12Hour(int hour) {
        if (hour == 0) {
            return Integer.toString(12) + " AM";
        } else if (hour > 0 && hour < 12) {
            return Integer.toString(hour) + " AM";
        } else {
            return Integer.toString(hour) + " PM";
        }
    }
}

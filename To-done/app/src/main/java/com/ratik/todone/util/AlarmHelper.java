package com.ratik.todone.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.receiver.TimeOverReceiver;
import com.ratik.todone.ui.InputActivity;

import java.util.Calendar;

/**
 * Created by Ratik on 20/12/16.
 */

public class AlarmHelper {

    private static final int TIME_OVER_REQUEST = 0;

    public static void setTimeOverAlarm(Context context, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeOverReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, TIME_OVER_REQUEST, intent, 0);

        // set the alarm
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        // save hour and minute for later use
        Prefs.putInt(InputActivity.DATE, calendar.get(Calendar.DATE));
        Prefs.putInt(InputActivity.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        Prefs.putInt(InputActivity.MINUTE, calendar.get(Calendar.MINUTE));
    }

    public static void removeAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeOverReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, TIME_OVER_REQUEST, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}

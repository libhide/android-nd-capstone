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

    public static void setTimeOverAlarm(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeOverReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, TIME_OVER_REQUEST, intent, 0);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);

        // set the alarm
        alarmManager.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);

        // save hour and minute
        // for later use
        Prefs.putInt(InputActivity.HOUR_OF_DAY, hour);
        Prefs.putInt(InputActivity.MINUTE, minute);
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

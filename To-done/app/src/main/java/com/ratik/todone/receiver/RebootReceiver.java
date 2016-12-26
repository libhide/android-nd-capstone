package com.ratik.todone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.ui.InputActivity;
import com.ratik.todone.util.AlarmHelper;

import java.util.Calendar;

/**
 * Created by Ratik on 20/12/16.
 */

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            int hourOfDay = Prefs.getInt(InputActivity.HOUR_OF_DAY, 0);
            int minute = Prefs.getInt(InputActivity.MINUTE, 0);
            // Set the alarm
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            AlarmHelper.setTimeOverAlarm(context, calendar);
        }
    }
}

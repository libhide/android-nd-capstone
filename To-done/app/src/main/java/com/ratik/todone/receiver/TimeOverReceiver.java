package com.ratik.todone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.provider.TodoDbHelper;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.ui.InitActivity;
import com.ratik.todone.ui.ListInputActivity;
import com.ratik.todone.util.Constants;
import com.ratik.todone.util.NotificationHelper;

/**
 * Created by Ratik on 20/12/16.
 */

public class TimeOverReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // remove notification
        NotificationHelper.removeNotification(context);

        // push success / fail notif
        int numberOfLeftoverTasks = TodoProvider.getNumberOfUncheckedTasks(context);
        if (numberOfLeftoverTasks == 0) {
            NotificationHelper.pushSuccessNotification(context);
        } else {
            NotificationHelper.pushUnsuccessfulNotification(context);
        }

        // update preference to help toggle the app's view
        Prefs.putBoolean(Constants.LIST_EXISTS, false);

        /* CLEAR OUT STUFF */

        // delete db
        TodoDbHelper helper = new TodoDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.deleteDb(db);

        // remove shared preferences
        Prefs.remove(InitActivity.HOUR_OF_DAY);
        Prefs.remove(InitActivity.MINUTE);
        Prefs.remove(ListInputActivity.TOTAL_TODOS);
    }
}

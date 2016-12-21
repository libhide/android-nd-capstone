package com.ratik.todone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.provider.TodoDbHelper;
import com.ratik.todone.ui.InitActivity;
import com.ratik.todone.util.Constants;

/**
 * Created by Ratik on 20/12/16.
 */

public class TimeOverReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // update preference
        Prefs.putBoolean(Constants.LIST_EXISTS, false);
        TodoDbHelper helper = new TodoDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        // delete everything
        helper.deleteDb(db);
        Prefs.remove(InitActivity.HOUR_OF_DAY);
        Prefs.remove(InitActivity.MINUTE);
    }
}

package com.ratik.todone.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.NotificationHelper;

import java.util.List;

/**
 * Created by Ratik on 29/12/16.
 */

class InsertTask extends AsyncTask<List<String>, Void, Void> {
    private Context context;

    InsertTask(Context context) {
        this.context = context;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<String>... todoLists) {
        List<String> todos = todoLists[0];
        ContentValues values = new ContentValues();
        for (int i = 0; i < todos.size(); i++) {
            values.put(TodoContract.TodoEntry.COLUMN_ID, i);
            values.put(TodoContract.TodoEntry.COLUMN_TASK, todos.get(i));
            values.put(TodoContract.TodoEntry.COLUMN_CHECKED, 0);
            // save
            context.getContentResolver()
                    .insert(TodoProvider.CONTENT_URI, values);
        }

        // push notification
        NotificationHelper.pushNotification(context, todos.size());

        return null;
    }
}

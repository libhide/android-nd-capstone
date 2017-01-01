package com.ratik.todone.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.NotificationHelper;

import java.util.List;

import static com.ratik.todone.ui.InputActivity.TOTAL_TODOS;

/**
 * Created by Ratik on 29/12/16.
 */

class InsertTask extends AsyncTask<List<String>, Void, Void> {
    private Context context;
    private int numberOfTasks;

    InsertTask(Context context) {
        this.context = context;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<String>... todoLists) {
        List<String> todos = todoLists[0];

        int totalTasks = Prefs.getInt(TOTAL_TODOS, 0);

        ContentValues values = new ContentValues();
        for (int i = 0; i < todos.size(); i++) {
            if (totalTasks == 0) {
                values.put(TodoContract.TodoEntry.COLUMN_ID, i);
            } else {
                values.put(TodoContract.TodoEntry.COLUMN_ID, totalTasks++);
            }
            values.put(TodoContract.TodoEntry.COLUMN_TASK, todos.get(i));
            values.put(TodoContract.TodoEntry.COLUMN_CHECKED, 0);
            // save
            context.getContentResolver()
                    .insert(TodoProvider.CONTENT_URI, values);
        }

        // save total number of todos
        if (totalTasks == 0) {
            Prefs.putInt(TOTAL_TODOS, todos.size());
        } else {
            Prefs.putInt(TOTAL_TODOS, totalTasks);
        }


        // push notification
        numberOfTasks = TodoProvider.getNumberOfUncheckedTasks(context);
        NotificationHelper.pushNotification(context, numberOfTasks);

        return null;
    }
}

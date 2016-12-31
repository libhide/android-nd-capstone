package com.ratik.todone.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.ratik.todone.R;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ratik on 20/12/16.
 */

public class NotificationHelper {
    private static final int TODONE_NOTIF = 7;
    private static final int TODONE_SUCCESS = 1;
    private static final int TODONE_FAILURE = 0;

    public static void pushNotification(Context context, int numTasks) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(true)
                .setContentText(context.getString(R.string.main_notif_content_text))
                .setSmallIcon(R.drawable.ic_stat_todone)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        List<String> todos = getTodos(context);
        if (todos.size() > 0) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(context.getString(R.string.main_notif_inbox_style_lead));
            for (int i = 0; i < todos.size(); i++) {
                inboxStyle.addLine(i + 1 + ". " + todos.get(i));
            }
            builder.setStyle(inboxStyle);
        }

        if (numTasks == 1) {
            builder.setContentTitle(context.getString(R.string.one_task_text));
        } else {
            builder.setContentTitle(String.format(context.getString(R.string.remaining_tasks_text), numTasks));
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_NOTIF, builder.build());
    }

    public static void removeNotification(Context context) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(TODONE_NOTIF);
    }

    public static void pushSuccessNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.all_done_text))
                .setContentText(context.getString(R.string.all_done_praise_text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.colorSuccess))
                .setLights(Color.GREEN, 500, 2000)
                .setSmallIcon(R.drawable.ic_stat_success);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_SUCCESS, builder.build());
    }

    public static void pushUnsuccessfulNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.failed_text))
                .setContentText(context.getString(R.string.failed_praise_text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.colorFailure))
                .setLights(Color.RED, 500, 2000)
                .setSmallIcon(R.drawable.ic_stat_fail);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_FAILURE, builder.build());
    }

    // Helpers
    private static List<String> getTodos(Context context) {
        List<String> todos = new ArrayList<>();

        // Query
        String[] projection = {
                TodoContract.TodoEntry.COLUMN_TASK
        };

        Cursor cursor = context.getContentResolver().query(
                TodoProvider.CONTENT_URI,
                projection,
                "checked=0",
                null,
                null
        );

        assert cursor != null;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String todo = cursor.getString(cursor.getColumnIndex(
                        TodoContract.TodoEntry.COLUMN_TASK));
                todos.add(todo);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return todos;
    }
}

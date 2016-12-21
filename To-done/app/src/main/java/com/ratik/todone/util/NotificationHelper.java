package com.ratik.todone.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ratik.todone.R;
import com.ratik.todone.ui.MainActivity;

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
                .setContentText("Finish these by 3 PM!")
                .setSmallIcon(R.drawable.ic_stat_todone)
                .setContentIntent(pendingIntent);

        if (numTasks == 0) {
            builder.setContentTitle("All tasks completed! Good job!");
            builder.setContentText("You should be proud of yourself, well done!");
        } else if (numTasks == 1) {
            builder.setContentTitle(numTasks + " task to go!");
        } else {
            builder.setContentTitle(numTasks + " tasks to go!");
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
        builder.setContentTitle("All tasks completed! Good job!")
                .setContentText("You should be proud of yourself, well done!")
                .setSmallIcon(R.drawable.ic_stat_todone);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_SUCCESS, builder.build());
    }

    public static void pushUnsuccessfulNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("You failed :(")
                .setContentText("Don't worry, try harder next time!")
                .setSmallIcon(R.drawable.ic_stat_todone);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_FAILURE, builder.build());
    }
}

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
    private static final int TODONE_NOTIF = 007;

    public static void pushNotification(Context context, int numTasks) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if (numTasks > 1) {
            builder.setContentTitle(numTasks + " tasks to go!");
        } else {
            builder.setContentTitle(numTasks + " task to go!");
        }
        builder.setOngoing(true)
                .setContentText("Finish these by 3 PM!")
                .setSmallIcon(R.drawable.ic_notification_placeholder)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(TODONE_NOTIF, builder.build());
    }
}

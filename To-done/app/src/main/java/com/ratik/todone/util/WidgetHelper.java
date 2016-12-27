package com.ratik.todone.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import com.ratik.todone.R;
import com.ratik.todone.widget.WidgetProvider;

/**
 * Created by Ratik on 27/12/16.
 */

public class WidgetHelper {
    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.todoListView);
    }
}

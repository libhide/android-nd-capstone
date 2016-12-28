package com.ratik.todone.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.R;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.ui.InputActivity;
import com.ratik.todone.ui.MainActivity;
import com.ratik.todone.util.NotificationHelper;
import com.ratik.todone.util.WidgetHelper;

import static com.ratik.todone.provider.TodoContract.TodoEntry.COLUMN_CHECKED;


public class WidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CHECK = "ACTION_CHECK";
    public static final String EXTRA_TODO_TEXT = "EXTRA_TODO_TEXT";

    public static final String START_FROM_WIDGET = "fromWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            Intent i = new Intent(context, WidgetService.class);
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            i.setData(Uri.parse(i.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            rv.setRemoteAdapter(appWidgetId, R.id.todoListView, i);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.todoListView, R.id.empty_view);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent checkIntent = new Intent(context, WidgetProvider.class);
            // Set the action for the intents.
            checkIntent.setAction(WidgetProvider.ACTION_CHECK);
            checkIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            checkIntent.setData(Uri.parse(checkIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent checkPendingIntent = PendingIntent.getBroadcast(context, 0, checkIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.todoListView, checkPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Check the intent's action and carry out the op
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CHECK)) {
            final String task = intent.getStringExtra(EXTRA_TODO_TEXT);
            int index = TodoProvider.getColumnId(context, task);
            // Toast.makeText(context, task + " @ " + viewIndex, Toast.LENGTH_SHORT).show();

            // mark item as done
            // db stuff
            ContentValues values = new ContentValues();
            values.put(COLUMN_CHECKED, 1);
            context.getContentResolver().update(
                    TodoProvider.CONTENT_URI,
                    values,
                    TodoContract.TodoEntry.COLUMN_ID + "=?",
                    new String[]{String.valueOf(index)}
            );

            // check if all tasks are done
            if (TodoProvider.getNumberOfCheckedTasks(context)
                    == Prefs.getInt(InputActivity.TOTAL_TODOS, 0)) {
                // all tasks done
                // alert dialog
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.putExtra(START_FROM_WIDGET, true);
                context.startActivity(mainIntent);
            } else {
                // notification stuff
                NotificationHelper.pushNotification(context,
                        TodoProvider.getNumberOfUncheckedTasks(context));
            }
        }
        WidgetHelper.updateWidget(context);
        super.onReceive(context, intent);
    }
}
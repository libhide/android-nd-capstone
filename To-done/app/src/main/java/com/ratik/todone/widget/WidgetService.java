package com.ratik.todone.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ratik.todone.R;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;

import static com.ratik.todone.provider.TodoContract.TodoEntry.COLUMN_CHECKED;

/**
 * Created by Ratik on 27/12/16.
 */

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}

class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private int appWidgetId;
    private String[] projection = {
            TodoContract.TodoEntry._ID,
            TodoContract.TodoEntry.COLUMN_ID,
            TodoContract.TodoEntry.COLUMN_TASK,
            TodoContract.TodoEntry.COLUMN_CHECKED
    };
    private Cursor cursor;

    WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void initCursor() {
        if (cursor != null) {
            cursor.close();
        }

        String selection = "checked=0";

        final long identityToken = Binder.clearCallingIdentity();
        // This is done because the widget runs as a separate thread
        // when compared to the current app and hence the app's data
        // won't be accessible to it because I'm using a content provider
        cursor = context.getContentResolver().query(
                TodoProvider.CONTENT_URI,
                projection,
                selection,
                null,
                null
        );
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        initCursor();
        if (cursor != null) {
            cursor.moveToFirst();
        }
    }

    @Override
    public void onDataSetChanged() {
        // Listen for data changes and initialize the cursor again
        initCursor();
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);

        String task = cursor.getString(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_TASK));

        int taskIsDone = cursor.getInt(cursor.getColumnIndex(
                COLUMN_CHECKED));

        // Construct a remote views item based on the app widget item XML file,
        // and set the text based on the position.
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todo_item);

        // <s> stuff
        if (taskIsDone == 1) {
            rv.setTextViewText(R.id.todoTextView,
                    Html.fromHtml("<s>" + task + "</s>"));
            rv.setInt(R.id.todoTextView, "setTextColor",
                    Color.argb(155, 255, 255, 255));
            rv.setInt(R.id.doneButton, "setVisibility", View.INVISIBLE);
        } else {
            rv.setTextViewText(R.id.todoTextView,
                    Html.fromHtml(task));
            rv.setInt(R.id.todoTextView, "setTextColor",
                    Color.argb(255, 255, 255, 255));
            rv.setInt(R.id.doneButton, "setVisibility", View.VISIBLE);
        }

        // OnTouch Stuff
        Bundle extras = new Bundle();
        extras.putString(WidgetProvider.EXTRA_TODO_TEXT, task);
        Intent checkIntent = new Intent();
        checkIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.doneButton, checkIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
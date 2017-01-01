package com.ratik.todone.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.R;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoDbHelper;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.ui.InputActivity;
import com.ratik.todone.util.AlarmHelper;
import com.ratik.todone.util.Constants;
import com.ratik.todone.util.NotificationHelper;
import com.ratik.todone.widget.WidgetProvider;

import static com.ratik.todone.provider.TodoContract.TodoEntry.COLUMN_CHECKED;

/**
 * Created by Ratik on 19/12/16.
 */

public class TodoAdapter extends CursorAdapter {

    private static final String TAG = TodoAdapter.class.getSimpleName();
    private LayoutInflater inflater;

    public TodoAdapter(Context context, Cursor c) {
        super(context, c);
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView todoTextView = (TextView) view.findViewById(R.id.todoTextView);
        final ToggleButton doneButton = (ToggleButton) view.findViewById(R.id.doneButton);

        // get data
        int index = cursor.getInt(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_ID));

        String task = cursor.getString(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_TASK));

        int taskIsDone = cursor.getInt(cursor.getColumnIndex(
                COLUMN_CHECKED));

        // update view
        todoTextView.setText(task);
        todoTextView.setTag(index);

        if (taskIsDone == 1) {
            todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            todoTextView.setTextColor(Color.argb(100, 255, 255, 255));
            doneButton.setChecked(true);
        } else {
            todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                    & (~Paint.STRIKE_THRU_TEXT_FLAG));
            todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
            doneButton.setChecked(false);
        }
        doneButton.setTag(cursor.getPosition());

        // mark item as done
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                boolean checked = ((ToggleButton) view).isChecked();

                if (checked) {
                    // check item
                    // view stuff
                    todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    todoTextView.setTextColor(Color.argb(100, 255, 255, 255));
                    doneButton.setChecked(false);

                    // db stuff
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_CHECKED, 1);
                    context.getContentResolver().update(
                            TodoProvider.CONTENT_URI,
                            values,
                            TodoContract.TodoEntry.COLUMN_ID + "=?",
                            new String[]{String.valueOf(view.getTag())}
                    );

                    // check if all tasks are done
                    if (TodoProvider.getNumberOfCheckedTasks(context)
                            == Prefs.getInt(InputActivity.TOTAL_TODOS, 0)) {
                        // all tasks done
                        // alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.done_verification_dialog_title)
                                .setMessage(R.string.done_verification_dialog_message)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // remove notification
                                        NotificationHelper.removeNotification(context);

                                        NotificationHelper.pushSuccessNotification(context);

                                        // update preference to help toggle the app's view
                                        Prefs.putBoolean(Constants.LIST_EXISTS, false);

                                        // delete db
                                        TodoDbHelper helper = new TodoDbHelper(context);
                                        SQLiteDatabase db = helper.getWritableDatabase();
                                        helper.deleteDb(db);

                                        // remove shared preferences
                                        Prefs.remove(InputActivity.HOUR_OF_DAY);
                                        Prefs.remove(InputActivity.MINUTE);
                                        Prefs.remove(InputActivity.TOTAL_TODOS);

                                        // remove alarm for future alarm
                                        AlarmHelper.removeAlarm(context);

                                        ((Activity) context).finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // db stuff
                                        ContentValues values = new ContentValues();
                                        values.put(COLUMN_CHECKED, 0);
                                        context.getContentResolver().update(
                                                TodoProvider.CONTENT_URI,
                                                values,
                                                TodoContract.TodoEntry.COLUMN_ID + "=?",
                                                new String[]{String.valueOf(view.getTag())}
                                        );

                                        // view stuff
                                        todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                                                & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                        todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
                                        doneButton.setVisibility(View.VISIBLE);
                                    }
                                });

                        builder.create().show();
                    } else {
                        // notification stuff
                        NotificationHelper.pushNotification(context,
                                TodoProvider.getNumberOfUncheckedTasks(context));
                    }
                } else {
                    // uncheck item

                    // view stuff
                    todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
                    doneButton.setChecked(true);

                    // db stuff
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_CHECKED, 0);
                    context.getContentResolver().update(
                            TodoProvider.CONTENT_URI,
                            values,
                            TodoContract.TodoEntry.COLUMN_ID + "=?",
                            new String[]{String.valueOf(view.getTag())}
                    );

                    // notification stuff
                    NotificationHelper.pushNotification(context,
                            TodoProvider.getNumberOfUncheckedTasks(context));
                }

                // widget stuff
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, WidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.todoListView);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup view) {
        return inflater.inflate(R.layout.todo_item, view, false);
    }
}

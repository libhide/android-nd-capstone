package com.ratik.todone.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.R;
import com.ratik.todone.adapter.TodoAdapter;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoDbHelper;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.AlarmHelper;
import com.ratik.todone.util.Constants;
import com.ratik.todone.util.NotificationHelper;
import com.ratik.todone.util.TimeHelper;
import com.ratik.todone.util.WidgetHelper;
import com.ratik.todone.widget.WidgetProvider;

import java.util.Calendar;

import static com.ratik.todone.provider.TodoContract.TodoEntry.COLUMN_CHECKED;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TodoAdapter adapter;
    private ListView todoListView;

    private CoordinatorLayout mainLayout;
    private TextView timeDifferenceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeDifferenceTextView = (TextView) findViewById(R.id.timeDiffTextView);
        mainLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);

        todoListView = (ListView) findViewById(R.id.todoListView);
        adapter = new TodoAdapter(this, null);
        todoListView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        if (!Prefs.getBoolean(Constants.LIST_EXISTS, false)) {
            Snackbar.make(mainLayout, "List created. Now, get to work!",
                    Snackbar.LENGTH_SHORT).show();
            // Update preference
            Prefs.putBoolean(Constants.LIST_EXISTS, true);
        }

        // ui things
        int hours = Prefs.getInt(InputActivity.HOUR_OF_DAY, 0);
        int minutes = Prefs.getInt(InputActivity.MINUTE, 0);
        Calendar notifTime = Calendar.getInstance();
        notifTime.set(Calendar.HOUR_OF_DAY, hours);
        notifTime.set(Calendar.MINUTE, minutes);
        String time = String.format(getString(R.string.time_difference_text),
                TimeHelper.getHumanReadableTimeDifference(notifTime));
        timeDifferenceTextView.setText(time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update widget
        WidgetHelper.updateWidget(this);

        // we are coming from the widget
        // user just finished the last task (?)
        Intent intent = getIntent();
        if (intent.hasExtra(WidgetProvider.START_FROM_WIDGET)) {
            // we are coming from the widget
            // user just finished the last task (?)

            final int lastIndex = intent.getIntExtra(WidgetProvider.EXTRA_LAST_POS, 0);

            // alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Everything Done!")
                    .setMessage("Are you sure you're through with all the tasks?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // remove notification
                            NotificationHelper.removeNotification(MainActivity.this);

                            NotificationHelper.pushSuccessNotification(MainActivity.this);

                            // update preference to help toggle the app's view
                            Prefs.putBoolean(Constants.LIST_EXISTS, false);

                            // delete db
                            TodoDbHelper helper = new TodoDbHelper(MainActivity.this);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            helper.deleteDb(db);

                            // remove shared preferences
                            Prefs.remove(InputActivity.HOUR_OF_DAY);
                            Prefs.remove(InputActivity.MINUTE);
                            Prefs.remove(InputActivity.TOTAL_TODOS);

                            // remove alarm for future alarm
                            AlarmHelper.removeAlarm(MainActivity.this);

                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // db stuff
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_CHECKED, 0);
                            MainActivity.this.getContentResolver().update(
                                    TodoProvider.CONTENT_URI,
                                    values,
                                    TodoContract.TodoEntry.COLUMN_ID + "=?",
                                    new String[]{String.valueOf(lastIndex)}
                            );

                            // view stuff
                            adapter.notifyDataSetChanged();

                            // Update widget
                            WidgetHelper.updateWidget(MainActivity.this);

                            finish();
                        }
                    });
            builder.create().show();
        }
    }

    // Creates a new loader after the initLoader() call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_ID,
                TodoContract.TodoEntry.COLUMN_TASK,
                TodoContract.TodoEntry.COLUMN_CHECKED
        };
        return new CursorLoader(
                this,
                TodoProvider.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

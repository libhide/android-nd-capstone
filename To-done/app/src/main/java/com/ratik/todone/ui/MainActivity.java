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
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
    public static final String FROM_MAIN = "fromMain";

    private TodoAdapter adapter;
    private ListView todoListView;

    private CoordinatorLayout mainLayout;
    private TextView timeDifferenceTextView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AdMob
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F10B72A932B17CB36CBBE69C25167324")
                .build();
        adView.loadAd(adRequest);

        timeDifferenceTextView = (TextView) findViewById(R.id.timeDiffTextView);
        mainLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);

        todoListView = (ListView) findViewById(R.id.todoListView);
        adapter = new TodoAdapter(this, null);
        todoListView.setAdapter(adapter);

        if (!Prefs.getBoolean(Constants.LIST_EXISTS, false)) {
            Snackbar.make(mainLayout, R.string.get_to_work_text,
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

        Button addMoreButton = (Button) findViewById(R.id.addMoreButton);
        addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(FROM_MAIN, true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get data
        getLoaderManager().initLoader(0, null, this);

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
            builder.setTitle(getString(R.string.all_done_text))
                    .setMessage(R.string.all_done_sure_text)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
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
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

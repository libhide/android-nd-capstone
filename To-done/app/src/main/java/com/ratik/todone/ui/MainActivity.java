package com.ratik.todone.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.Constants;
import com.ratik.todone.util.TimeHelper;
import com.ratik.todone.util.WidgetHelper;

import java.util.Calendar;

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

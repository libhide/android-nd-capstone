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

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.R;
import com.ratik.todone.adapter.TodoAdapter;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.Constants;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private TodoAdapter adapter;
    private ListView todoListView;

    private CoordinatorLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

package com.ratik.todone.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.TodoProvider;
import com.ratik.todone.util.Constants;
import com.ratik.todone.adapter.ItemsToBeAddedAdapter;
import com.ratik.todone.R;
import com.ratik.todone.db.TodoContract.TodoEntry;
import com.ratik.todone.db.TodoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class ListInputActivity extends AppCompatActivity {

    private List<String> todos;
    private ItemsToBeAddedAdapter adapter;

    private FloatingActionButton fab;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_input);

        dbHelper = new TodoDbHelper(this);
        db = dbHelper.getWritableDatabase();

        final EditText itemInputEditText = (EditText) findViewById(R.id.itemInputEditText);
        itemInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem(v.getText().toString());
                    itemInputEditText.setText("");
                    handled = true;
                }
                return handled;
            }
        });

        todos = new ArrayList<>();
        ListView itemsToAddList = (ListView) findViewById(R.id.itemsToAddList);
        adapter = new ItemsToBeAddedAdapter(this, todos);
        itemsToAddList.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTodos();
            }
        });
    }

    private void saveTodos() {
        ContentValues values = new ContentValues();
        for(String todo : todos) {
            values.put(TodoEntry.COLUMN_TASK, todo);
            values.put(TodoEntry.COLUMN_CHECKED, 0);
            // save
            getContentResolver().insert(TodoProvider.CONTENT_URI, values);
        }

        // Update preference
        Prefs.putBoolean(Constants.LIST_EXISTS, true);

        // Start MainActivity
        Intent intent = new Intent(ListInputActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void addItem(String s) {
        todos.add(s);
        adapter.notifyDataSetChanged();

        // start showing fab when
        // 3 or more items have been added
        if (todos.size() >= 3) {
            fab.setVisibility(View.VISIBLE);
        }
    }
}

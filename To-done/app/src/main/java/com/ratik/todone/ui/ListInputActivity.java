package com.ratik.todone.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.R;
import com.ratik.todone.adapter.ItemsToBeAddedAdapter;
import com.ratik.todone.provider.TodoContract.TodoEntry;
import com.ratik.todone.provider.TodoProvider;
import com.ratik.todone.util.AlarmHelper;
import com.ratik.todone.util.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class ListInputActivity extends AppCompatActivity {

    public static final String TAG = ListInputActivity.class.getSimpleName();
    public static final String TOTAL_TODOS = "total_todos";

    private List<String> todos;
    private ItemsToBeAddedAdapter adapter;

    private FloatingActionButton fab;

    private int hourOfDay;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_input);

        Intent intent = getIntent();
        hourOfDay = intent.getIntExtra(InitActivity.HOUR_OF_DAY, 0);
        minute = intent.getIntExtra(InitActivity.MINUTE, 0);

        final EditText itemInputEditText = (EditText) findViewById(R.id.itemInputEditText);
        itemInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String item = v.getText().toString();
                    if (!item.isEmpty()) {
                        addItem(item);
                        itemInputEditText.setText("");
                        handled = true;
                    } else {
                        Toast.makeText(ListInputActivity.this,
                                "No blanks allowed ;)", Toast.LENGTH_SHORT).show();
                    }
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
        // save total number of todos
        Prefs.putInt(TOTAL_TODOS, todos.size());

        // db stuff
        ContentValues values = new ContentValues();
        for(int i = 0; i < todos.size(); i++) {
            values.put(TodoEntry.COLUMN_ID, i);
            values.put(TodoEntry.COLUMN_TASK, todos.get(i));
            values.put(TodoEntry.COLUMN_CHECKED, 0);
            // save
            getContentResolver().insert(TodoProvider.CONTENT_URI, values);
        }

        // alarm stuff
        AlarmHelper.setTimeOverAlarm(this, hourOfDay, minute);

        // notification stuff
        NotificationHelper.pushNotification(this, todos.size());

        // Start MainActivity
        Intent intent = new Intent(ListInputActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

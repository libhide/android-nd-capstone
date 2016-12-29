package com.ratik.todone.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.ratik.todone.util.Constants;
import com.ratik.todone.util.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class InputActivity extends AppCompatActivity implements
        OnTimeSetListener, OnTimeSetCancel {

    public static final String TAG = InputActivity.class.getSimpleName();

    public static final String HOUR_OF_DAY = "hour";
    public static final String MINUTE = "minute";
    public static final String TOTAL_TODOS = "total_todos";

    private List<String> todos;
    private ItemsToBeAddedAdapter adapter;

    private FloatingActionButton fab;
    private CoordinatorLayout inputLayout;

    private Snackbar helperSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If a list already exists
        // Transfer UI to MainActivity
        if (Prefs.getBoolean(Constants.LIST_EXISTS, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        setContentView(R.layout.activity_list_input);

        inputLayout = (CoordinatorLayout) findViewById(R.id.inputLayout);

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
                        Toast.makeText(InputActivity.this,
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
                hideKeyboard();
                helperSnack = Snackbar.make(inputLayout,
                        "Set the time duration for the todos you entered",
                        Snackbar.LENGTH_INDEFINITE);
                helperSnack.show();
                new FormDialog().show(getSupportFragmentManager(), "FormDialog");
            }
        });

        // input help
        if (Prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(itemInputEditText)
                    .setPrimaryText("Enter your first task for the todo list!")
                    .setSecondaryText("Try adding at least three items!")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    }).show();
        }
    }

    private void saveTodos(Calendar calendar) {
        // save total number of todos
        Prefs.putInt(TOTAL_TODOS, todos.size());

        // db stuff
        ContentValues values = new ContentValues();
        for (int i = 0; i < todos.size(); i++) {
            values.put(TodoEntry.COLUMN_ID, i);
            values.put(TodoEntry.COLUMN_TASK, todos.get(i));
            values.put(TodoEntry.COLUMN_CHECKED, 0);
            // save
            getContentResolver().insert(TodoProvider.CONTENT_URI, values);
        }

        // alarm stuff
        AlarmHelper.setTimeOverAlarm(this, calendar);

        // notification stuff
        NotificationHelper.pushNotification(this, todos.size());

        // Start MainActivity
        Intent intent = new Intent(InputActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void addItem(String s) {
        todos.add(s);
        adapter.notifyDataSetChanged();

        // start showing fab when
        // 3 or more items have been added
        if (todos.size() >= 3) {

            if (Prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {
                hideKeyboard();
                fab.setVisibility(View.VISIBLE);
                new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(fab)
                        .setPrimaryText("Your first todo list!")
                        .setSecondaryText("Tap the check mark to set a timer for the todo list or add a few more items if you want to.")
                        .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                            @Override
                            public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            }

                            @Override
                            public void onHidePromptComplete() {
                            }
                        }).show();
            } else {
                fab.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTimeSet(Calendar c) {
        // dismiss helper
        helperSnack.dismiss();
        // save
        saveTodos(c);
    }

    @Override
    public void onTimeSetCancel() {
        helperSnack.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void hideKeyboard() {
        // Check if no view has focus
        View v = InputActivity.this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}

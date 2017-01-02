package com.ratik.todone.ui;

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
import com.ratik.todone.util.AlarmHelper;
import com.ratik.todone.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class InputActivity extends AppCompatActivity implements
        OnTimeSetListener, OnTimeSetCancel {

    public static final String TAG = InputActivity.class.getSimpleName();

    public static final String DATE = "date";
    public static final String HOUR_OF_DAY = "hour";
    public static final String MINUTE = "minute";
    public static final String TOTAL_TODOS = "total_todos";

    private List<String> todos;
    private ItemsToBeAddedAdapter adapter;

    private FloatingActionButton fab;
    private CoordinatorLayout inputLayout;

    private Snackbar helperSnack;

    private boolean fromMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if returning from MainActivity
        fromMain = getIntent().getBooleanExtra(MainActivity.FROM_MAIN, false);

        // If a list already exists
        // Transfer UI to MainActivity
        if (Prefs.getBoolean(Constants.LIST_EXISTS, false) && !fromMain) {
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
                                R.string.no_blanks_allowed_text, Toast.LENGTH_SHORT).show();
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
                        R.string.set_duration_prompt,
                        Snackbar.LENGTH_INDEFINITE);
                helperSnack.show();
                new FormDialog().show(getSupportFragmentManager(), "FormDialog");
            }
        });

        // input help
        if (Prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {
            new MaterialTapTargetPrompt.Builder(this)
                    .setTarget(itemInputEditText)
                    .setPrimaryText(String.format(getString(R.string.welcome_text),
                            getString(R.string.app_name)))
                    .setSecondaryText(R.string.welcome_secondary_text)
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    }).show();
        } else {
            itemInputEditText.requestFocus();
            showKeyboard();
        }
    }

    private void saveTodos(Calendar calendar) {
        // db stuff
        new InsertTask(this).execute(todos);

        // alarm stuff
        AlarmHelper.setTimeOverAlarm(this, calendar);

        // Start MainActivity
        Intent intent = new Intent(InputActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void addItem(String s) {
        todos.add(s);
        adapter.notifyDataSetChanged();

        if (fromMain) {
            fab.setVisibility(View.VISIBLE);
        } else {
            // start showing fab when
            // 3 or more items have been added
            if (todos.size() >= 3) {

                if (Prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {
                    hideKeyboard();
                    fab.setVisibility(View.VISIBLE);
                    new MaterialTapTargetPrompt.Builder(this)
                            .setTarget(fab)
                            .setPrimaryText(R.string.list_input_help_text)
                            .setSecondaryText(R.string.list_input_secondary_text)
                            .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                @Override
                                public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                    Prefs.putBoolean(Constants.IS_FIRST_RUN, false);
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

    // Helpers
    private void hideKeyboard() {
        // Check if no view has focus
        View v = InputActivity.this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        // Get view with focus
        View v = InputActivity.this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
}

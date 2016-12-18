package com.ratik.todone;

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

import java.util.ArrayList;
import java.util.List;

public class ListInputActivity extends AppCompatActivity {

    private List<String> tasks;
    private ItemsToBeAddedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_input);

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

        tasks = new ArrayList<>();
        ListView itemsToAddList = (ListView) findViewById(R.id.itemsToAddList);
        adapter = new ItemsToBeAddedAdapter(this, tasks);
        itemsToAddList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListInputActivity.this, tasks.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addItem(String s) {
        tasks.add(s);
        adapter.notifyDataSetChanged();
    }
}

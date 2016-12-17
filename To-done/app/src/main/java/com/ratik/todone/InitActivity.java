package com.ratik.todone;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ratik.todone.db.TodoContract;
import com.ratik.todone.db.TodoDbHelper;

public class InitActivity extends AppCompatActivity {

    private static final String TAG = InitActivity.class.getSimpleName();

    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        dbHelper = new TodoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_TASK, "Eat an Apple");
        values.put(TodoContract.TodoEntry.COLUMN_CHECKED, 0);

        db.insert(TodoContract.TodoEntry.TABLE_NAME, null, values);
    }
}

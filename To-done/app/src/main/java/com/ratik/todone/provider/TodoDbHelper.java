package com.ratik.todone.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ratik.todone.provider.TodoContract.TodoEntry.TABLE_NAME;

/**
 * Created by Ratik on 17/12/16.
 */

public class TodoDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "todone.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
            Required query -
            CREATE TABLE <name> (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                task TEXT NOT NULL,
                checked INTEGER DEFAULT 0
            );
         */
        String createQuery = "CREATE TABLE " + TABLE_NAME + " ( " +
                TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoContract.TodoEntry.COLUMN_TASK + " TEXT NOT NULL, " +
                TodoContract.TodoEntry.COLUMN_CHECKED + " INTEGER DEFAULT 0);";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deleteDb(SQLiteDatabase db) {
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }
}

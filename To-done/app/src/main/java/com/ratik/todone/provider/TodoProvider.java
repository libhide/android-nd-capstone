package com.ratik.todone.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pixplicity.easyprefs.library.Prefs;
import com.ratik.todone.ui.ListInputActivity;

import java.util.Arrays;
import java.util.HashSet;

import static com.ratik.todone.provider.TodoContract.TodoEntry.TABLE_NAME;

/**
 * Created by Ratik on 18/12/16.
 */

@SuppressWarnings("ConstantConditions")
public class TodoProvider extends ContentProvider {

    private TodoDbHelper dbHelper;

    // used for the UriMatcher
    private static final int TODOS = 10;
    private static final int TODO_ID = 20;

    public static final String AUTHORITY = "com.ratik.todone.provider";

    private static final String BASE_PATH = "todos";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/todos";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/todo";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TodoDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TODOS:
                break;
            case TODO_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(TodoContract.TodoEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case TODOS:
                id = db.insert(TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case TODOS:
                rowsDeleted = db.delete(TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(
                            TABLE_NAME,
                            TodoContract.TodoEntry._ID + "=" + id,
                            null
                    );
                } else {
                    rowsDeleted = db.delete(
                            TABLE_NAME,
                            TodoContract.TodoEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case TODOS:
                rowsUpdated = db.update(
                        TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                            TABLE_NAME,
                            values,
                            TodoContract.TodoEntry._ID + "=" + id,
                            null
                    );
                } else {
                    rowsUpdated = db.update(
                            TABLE_NAME,
                            values,
                            TodoContract.TodoEntry._ID + "=" + id + " and " + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    // Helpers
    private void checkColumns(String[] projection) {
        String[] available = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_ID,
                TodoContract.TodoEntry.COLUMN_TASK,
                TodoContract.TodoEntry.COLUMN_CHECKED
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    public static int getNumberOfCheckedTasks(Context context) {
        String[] projection = {
                TodoContract.TodoEntry.COLUMN_CHECKED
        };

        String selection = "checked=1";

        Cursor cursor = context.getContentResolver().query(
                TodoProvider.CONTENT_URI,
                projection,
                selection,
                null,
                null
        );
        if (cursor != null) {
            return cursor.getCount();
        }
        cursor.close();
        return 0;
    }

    public static int getNumberOfUncheckedTasks(Context context) {
        int totalTasks = Prefs.getInt(ListInputActivity.TOTAL_TODOS, 0);
        int checked = getNumberOfCheckedTasks(context);
        return totalTasks - checked;
    }
}

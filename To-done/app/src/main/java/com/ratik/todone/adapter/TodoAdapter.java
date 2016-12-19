package com.ratik.todone.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ratik.todone.R;
import com.ratik.todone.provider.TodoContract;
import com.ratik.todone.provider.TodoProvider;

import static com.ratik.todone.provider.TodoContract.TodoEntry.COLUMN_CHECKED;

/**
 * Created by Ratik on 19/12/16.
 */

public class TodoAdapter extends CursorAdapter {

    private static final String TAG = TodoAdapter.class.getSimpleName();
    private LayoutInflater inflater;

    public TodoAdapter(Context context, Cursor c) {
        super(context, c);
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView todoTextView = (TextView) view.findViewById(R.id.todoTextView);
        final ImageButton doneButton = (ImageButton) view.findViewById(R.id.doneButton);

        // get data
        int index = cursor.getInt(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_ID));

        String task = cursor.getString(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_TASK));

        int taskIsDone = cursor.getInt(cursor.getColumnIndex(
                COLUMN_CHECKED));

        // update view
        todoTextView.setText(task);
        todoTextView.setTag(index);

        if (taskIsDone == 1) {
            todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            todoTextView.setTextColor(Color.argb(150, 255, 255, 255));
            doneButton.setVisibility(View.INVISIBLE);
        } else {
            todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                    & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
            doneButton.setVisibility(View.VISIBLE);
        }
        doneButton.setTag(cursor.getPosition());

        // mark item as done
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // view stuff
                todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                todoTextView.setTextColor(Color.argb(150, 255, 255, 255));
                doneButton.setVisibility(View.INVISIBLE);

                // db stuff
                ContentValues values = new ContentValues();
                values.put(COLUMN_CHECKED, true);
                context.getContentResolver().update(
                        TodoProvider.CONTENT_URI,
                        values,
                        TodoContract.TodoEntry.COLUMN_ID + "=?",
                        new String[] {String.valueOf(view.getTag())}
                );
            }
        });

        // un-check item
        todoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // view stuff
                todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                        & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
                doneButton.setVisibility(View.VISIBLE);

                // db stuff
                ContentValues values = new ContentValues();
                values.put(COLUMN_CHECKED, false);
                context.getContentResolver().update(
                        TodoProvider.CONTENT_URI,
                        values,
                        TodoContract.TodoEntry.COLUMN_ID + "=?",
                        new String[] {String.valueOf(view.getTag())}
                );
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup view) {
        return inflater.inflate(R.layout.item_todo, view, false);
    }
}

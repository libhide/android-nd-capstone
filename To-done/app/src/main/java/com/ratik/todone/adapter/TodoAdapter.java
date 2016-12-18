package com.ratik.todone.adapter;

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

/**
 * Created by Ratik on 19/12/16.
 */

public class TodoAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public TodoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView todoTextView = (TextView) view.findViewById(R.id.todoTextView);
        final ImageButton doneButton = (ImageButton) view.findViewById(R.id.doneButton);

        String task = cursor.getString(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_TASK));
        todoTextView.setText(task);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                todoTextView.setTextColor(Color.argb(150, 255, 255, 255));
                doneButton.setVisibility(View.INVISIBLE);
            }
        });

        todoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoTextView.setPaintFlags(todoTextView.getPaintFlags()
                        & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                todoTextView.setTextColor(Color.argb(255, 255, 255, 255));
                doneButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup view) {
        return inflater.inflate(R.layout.item_todo, view, false);
    }
}

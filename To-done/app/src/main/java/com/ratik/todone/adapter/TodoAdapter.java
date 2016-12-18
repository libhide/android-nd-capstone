package com.ratik.todone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ratik.todone.R;
import com.ratik.todone.db.TodoContract;

/**
 * Created by Ratik on 19/12/16.
 */

public class TodoAdapter extends CursorAdapter {

    private Context context;
    private LayoutInflater inflater;

    public TodoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView todoTextView = (TextView) view.findViewById(R.id.todoTextView);
        ImageButton doneButton = (ImageButton) view.findViewById(R.id.doneButton);

        String task = cursor.getString(cursor.getColumnIndex(
                TodoContract.TodoEntry.COLUMN_TASK));
        todoTextView.setText(task);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
                // ..
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup view) {
        return inflater.inflate(R.layout.item_todo, view, false);
    }
}

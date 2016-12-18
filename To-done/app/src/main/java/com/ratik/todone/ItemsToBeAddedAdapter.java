package com.ratik.todone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ratik on 18/12/16.
 */

public class ItemsToBeAddedAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private List<String> todos;

    public ItemsToBeAddedAdapter(Context context, List<String> todos) {
        this.context = context;
        this.todos = new ArrayList<>();
        this.todos = todos;
    }

    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Object getItem(int i) {
        return todos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.to_be_added_item, parent, false);

            holder = new ViewHolder();

            holder.todoTextView = (TextView) convertView.findViewById(R.id.todoTextView);
            holder.cancelButton = (ImageButton) convertView.findViewById(R.id.cancelButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String todo = todos.get(position);

        // Set values for views
        holder.todoTextView.setText(todo);
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todos.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView todoTextView;
        ImageButton cancelButton;
    }
}
